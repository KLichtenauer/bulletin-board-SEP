package de.schwarzes_brett.test_util;

import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.tomcat.Tomcat10xInstalledLocalContainer;
import org.codehaus.cargo.container.tomcat.Tomcat10xStandaloneLocalConfiguration;
import org.codehaus.cargo.util.log.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Manages the Tomcat lifecycle.
 *
 * @author Tim-Florian Feulner
 */
public final class TomcatServerLifecycle {

    /**
     * The port of the Tomcat server.
     */
    public static final int HTTPS_PORT = 8445;

    private static final int DEFAULT_HTTPS_PORT = 8443;
    private static final int HTTP_PORT = 8081;
    private static final int DEFAULT_HTTP_PORT = 8080;
    private static final String TOMCAT_CONFIG_DIR = "target/tomcat-config";
    private static final String TOMCAT_EXTRACTION_TARGET = "target/tomcat-st";
    private static final String TOMCAT_HOME_DIR = TOMCAT_EXTRACTION_TARGET + "/apache-tomcat-10.0.27/apache-tomcat-10.0.27";
    private static final String LOG_FILE = "target/tomcat.log";
    private static final String DEPLOYABLE_PATH = "target/schwarzes_brett-1.0-system_test.war";
    private static final String SERVER_CONFIG_FILE = "/conf/server.xml";

    private final Logger logger = Logger.getLogger(TomcatServerLifecycle.class.getName());
    private Tomcat10xInstalledLocalContainer tomcatContainer;

    /**
     * Configures and starts the Tomcat server.
     *
     * @param testName The name of the current test. Must be distinct.
     * @throws IOException If the Jacoco agent jar could not be found.
     */
    public void init(String testName) throws IOException {
        Tomcat10xStandaloneLocalConfiguration config = new Tomcat10xStandaloneLocalConfiguration(TOMCAT_CONFIG_DIR);

        WAR deployable = new WAR(DEPLOYABLE_PATH);
        deployable.setContext("/schwarzes_brett/");
        config.addDeployable(deployable);

        config.setLogger(new SimpleLogger());

        if (System.getenv("MAVEN_REPO_LOCAL") == null) {
            logger.warning("Environment variables for system test coverage are not set, skipping coverage measuring.");
        } else {
            // Enable Jacoco agent inside Tomcat JVM.
            testName = testName.replace(" ", "_").replace(".", "_");
            config.setProperty(GeneralPropertySet.JVMARGS,
                               "-javaagent:" + getJacocoAgentPath() + "=destfile=../../target/coverage-reports/jacoco-st-" + testName
                               + ".exec,append=false");
        }

        tomcatContainer = new Tomcat10xInstalledLocalContainer(config);

        downloadTomcat();

        tomcatContainer.setHome(TOMCAT_HOME_DIR);
        tomcatContainer.setOutput(LOG_FILE);

        tomcatContainer.start();

        logger.info("Tomcat started.");
    }

    private void downloadTomcat() throws IOException {
        File tomcatDir;
        URL downloadURL;
        tomcatDir = new File(TOMCAT_EXTRACTION_TARGET);
        downloadURL = new URL("https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.27/bin/apache-tomcat-10.0.27.tar.gz");
        ZipURLInstaller installer = new ZipURLInstaller(downloadURL, tomcatDir.getParent(), tomcatDir.toString());

        if (!installer.isAlreadyDownloaded()) {
            logger.info("Setting up tomcat.");
            installer.download();
            installer.install();
            configureTomcat();
            logger.info("Finished tomcat setup.");
        } else {
            logger.info("Tomcat is already set up.");
        }
    }

    private void configureTomcat() throws IOException {
        logger.finest("Started tomcat configuration.");

        Path serverConfigFile = Paths.get(TOMCAT_HOME_DIR + SERVER_CONFIG_FILE);

        List<String> lines = Files.readAllLines(serverConfigFile);
        List<String> transformedLines = lines.stream().map(line -> {
            if (line.contains("A \"Connector\" using the shared thread pool")) {
                return "<Connector port=\"" + HTTPS_PORT
                       + "\" protocol=\"org.apache.coyote.http11.Http11NioProtocol\" maxThreads=\"150\" SSLEnabled=\"true\" "
                       + "socketBuffer=\"64000\"> " + TomcatSecurityUtil.generateSSLHostConfigEntry() + " </Connector>";
            } else {
                return line.replace(Integer.toString(DEFAULT_HTTPS_PORT), Integer.toString(HTTPS_PORT))
                           .replace(Integer.toString(DEFAULT_HTTP_PORT), Integer.toString(HTTP_PORT));
            }
        }).toList();

        Files.writeString(serverConfigFile, String.join("\n", transformedLines), StandardOpenOption.WRITE);

        logger.finest("Create tomcat certificate.");
        TomcatSecurityUtil.createCertificate(TOMCAT_HOME_DIR);
        logger.finest("Finished tomcat configuration.");
    }

    private String getJacocoAgentPath() throws IOException {
        String localMavenRepo = System.getenv("MAVEN_REPO_LOCAL");
        String jacocoVersion = System.getenv("JACOCO_VERSION");
        Optional<Path> jacocoAgentJarPath;

        try (Stream<Path> paths = Files.find(Paths.get(localMavenRepo), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())) {
            jacocoAgentJarPath = paths.filter(p -> p.toString().endsWith(".jar")).filter(p -> p.toString().contains("jacoco"))
                                      .filter(p -> p.toString().contains("agent")).filter(p -> p.toString().contains(jacocoVersion)).findFirst();
        }

        if (jacocoAgentJarPath.isPresent()) {
            return jacocoAgentJarPath.get().toAbsolutePath().toString();
        } else {
            throw new IOException(
                    "Jacoco agent jar for version " + jacocoVersion + " could not be found in local maven repository " + localMavenRepo + ".");
        }
    }

    /**
     * Shuts down the Tomcat server.
     */
    public void destroy() {
        tomcatContainer.stop();

        logger.info("Tomcat stopped.");
    }

}
