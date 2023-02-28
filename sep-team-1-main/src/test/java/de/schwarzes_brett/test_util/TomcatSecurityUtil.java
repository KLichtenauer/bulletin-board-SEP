package de.schwarzes_brett.test_util;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for configuring and generating certificates.
 *
 * @author Tim-Florian Feulner
 */
final class TomcatSecurityUtil {

    private static final Logger LOGGER = Logger.getLogger(TomcatSecurityUtil.class.getName());

    private static final int KEY_SIZE = 4096;
    private static final String CERTIFICATE_ALIAS = "tomcat";
    private static final String HASH_ALGORITHM = "SHA256withRSA";
    private static final String COMMON_NAME = "localhost";
    private static final int CERTIFICATE_VALID_DAY_COUNT = 1000;

    private static final String KEY_STORE_FILE_PATH = "conf/certificate.crt";
    private static final String KEY_STORE_PASSWORD = "isdhfisdhfisisdhbfiobsdfdfi";

    private TomcatSecurityUtil() {}

    static String generateSSLHostConfigEntry() {
        return "<SSLHostConfig> <Certificate certificateKeystoreFile=\"" + KEY_STORE_FILE_PATH + "\" certificateKeystorePassword=\""
               + KEY_STORE_PASSWORD + "\" type=\"RSA\"/> </SSLHostConfig>";
    }

    /**
     * Generates a self-signed certificate and writes it to file.
     *
     * @param tomcatHome The path to the tomcat home directory.
     */
    static void createCertificate(String tomcatHome) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE);
            KeyPair keyPair = generator.generateKeyPair();

            Certificate certificate = generateCertificate(keyPair);

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null, null);
            keystore.setKeyEntry(CERTIFICATE_ALIAS, keyPair.getPrivate(), KEY_STORE_PASSWORD.toCharArray(), new Certificate[]{certificate});
            try (FileOutputStream outputStream = new FileOutputStream(tomcatHome + "/" + KEY_STORE_FILE_PATH)) {
                keystore.store(outputStream, KEY_STORE_PASSWORD.toCharArray());
            }
        } catch (CertificateException | NoSuchAlgorithmException | OperatorCreationException | IOException | KeyStoreException e) {
            String message = "Error generating tomcat certificate.";
            LOGGER.severe(message);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(message, e);
        }
    }

    private static X509Certificate generateCertificate(KeyPair keyPair)
            throws OperatorCreationException, CertificateException, CertIOException, NoSuchAlgorithmException {
        Instant now = Instant.now();
        Date notBefore = Date.from(now);
        Date notAfter = Date.from(now.plus(Duration.ofDays(CERTIFICATE_VALID_DAY_COUNT)));

        ContentSigner signer = new JcaContentSignerBuilder(HASH_ALGORITHM).build(keyPair.getPrivate());
        X500Name name = new X500Name("CN=" + COMMON_NAME);
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(name, BigInteger.valueOf(now.toEpochMilli()), notBefore,
                                                                                      notAfter, name, keyPair.getPublic());
        certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, generateSubject(keyPair.getPublic()))
                          .addExtension(Extension.authorityKeyIdentifier, false, generateAuthority(keyPair.getPublic()))
                          .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(signer));
    }

    private static SubjectKeyIdentifier generateSubject(PublicKey publicKey) throws OperatorCreationException {
        return new X509ExtensionUtils(getDigestCalculator()).createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    private static AuthorityKeyIdentifier generateAuthority(PublicKey publicKey) throws OperatorCreationException {
        return new X509ExtensionUtils(getDigestCalculator()).createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    private static DigestCalculator getDigestCalculator() throws OperatorCreationException {
        return new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
    }

}
