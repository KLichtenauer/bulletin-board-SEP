# Installation instructions (Tim-Florian Feulner)


## 1 Installation


### 1.1 SMTP server (mail server)
The application requires access to an SMTP mail server in order to send mails.
It must be accessible from the application server via the respective port.
The details for the connection to this mail server can be configured, see section 2.


### 1.2 PostgreSQL database (database server)
The application requires access to an PostgreSQL server for persistent data storage.
PostgreSQL version 14.5 is supported.
Installation instructions and further documentation can be found at
https://www.postgresql.org/docs/14/index.html.

The database server must have a user and database configured to be used by the application.
That user must have full privileges to that database.
Additionally, the database server must be accessible from the application server via the respective port.
To achieve the best performance, the connection speed between the database server
and the application server should be at least 1 Gbit/s.
The details for the connection to the database server can be configured, see section 2.


### 1.3 Java 18 (application server)
Download and install a Java Runtime Environment or Java Development Kit for Java 18,
for example OpenJDK 18 (https://jdk.java.net/18/).
After that, make sure that the correct version of Java is used, e.g. by running `java -version`.


### 1.4 Apache Tomcat (application server)
The application supports Apache Tomcat version 10.0.27.
Download and install/extract Tomcat 10.0.27 from
https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.27/bin/.
For more download instructions, look at the bottom of that page.

The application requires the configuration of SSL/TLS, because it is only accessible through HTTPS.
In order to set up SSL/TLS with Tomcat, follow the instructions under
https://tomcat.apache.org/tomcat-10.0-doc/ssl-howto.html.

Further documentation:
- Setup: https://tomcat.apache.org/tomcat-10.0-doc/setup.html
- General: https://tomcat.apache.org/tomcat-10.0-doc/index.html


### 1.5 Application deployment (application server)
From now on, `$CATALINA_HOME` refers to the directory into which Tomcat was installed.

Extract the contents of the shipped `.war` file into the directory `$CATALINA_HOME/webapps/schwarzes_brett`.
This directory may have to be created.




## 2 Configuration

The application can be configured by editing the files `config.properties` and `logging.properties`
that can be found in the directory `$CATALINA_HOME/webapps/schwarzes_brett/WEB-INF/config`.
The file `config.properties` is used to configure the database connection settings
and the SMTP server connection settings.
The file `logging.properties` is used to configure the logging of the application.
A description of the respective configuration options is included in the files.

In order to check for errors with the configuration, check the log of the Tomcat server.
The default location of the log files is `$CATALINA_HOME/logs`.




## 3 Startup

When all necessary configurations have been made, Tomcat can be started, e.g. by running `$CATALINA_HOME/bin/startup.sh`.
The startup time is dependent on the configuration, but generally it should be less than 10 seconds.




## 4 Web interface

When Tomcat is running and the startup of the application did not lead to errors due to invalid configuration,
the web interface of the application can be accessed from `https://<hostname>:<port>/schwarzes_brett`,
for example `https://localhost:8443/schwarzes_brett`.

The login credentials for the default admin are:
- Username: admin
- Password: start123

It is strongly advised to change this password immediately after the first login.

Additionally, a context-sensitive help text is accessible via the question mark in the header
of every page.




## 5 Shutdown

The server can be stopped by running `$CATALINA_HOME/bin/shutdown.sh` if it was started as described in section 3.
The shutdown time is dependent on the configuration, but for "normal" configurations, the shutdown should
happen in under 90 seconds, generally it should complete in under 10 seconds.
