#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

http_port=8001
https_port=8445
server_xml="conf/server.xml"
keystore_file="conf/certificate.keystore"
keystore_pw="dsofh34ze0pdfvxjpoq349sjxdvi"

if [[ ! -d tomcat ]]; then
    echo "Setting up tomcat"
    mkdir tomcat
    cd tomcat

    wget "https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.27/bin/apache-tomcat-10.0.27.tar.gz" -q

    tar xzf apache-tomcat-*.tar.gz
    rm apache-tomcat-*.tar.gz
    mv apache-tomcat-* apache-tomcat
    cd apache-tomcat

    keytool -genkey -alias tomcat -keyalg RSA -storepass "$keystore_pw" -keystore "$keystore_file" \
        -dname "CN=localhost, OU=, O=, L=, S=, C=" -noprompt

    sed -i -e "s/8080/${http_port}/g" "$server_xml"
    sed -i -e "s/8443/${https_port}/g" "$server_xml"
    sed -i -e "84a\
        <Connector port=\"${https_port}\" protocol=\"org.apache.coyote.http11.Http11NioProtocol\" \
                maxThreads=\"1000\" SSLEnabled=\"true\" socketBuffer=\"256000\" acceptCount=\"1000\"> \
            <SSLHostConfig> \
                <Certificate certificateKeystoreFile=\"${keystore_file}\" \
                    certificateKeystorePassword=\"${keystore_pw}\" \
                    type=\"RSA\"/> \
            </SSLHostConfig> \
        </Connector>" "$server_xml"

    cd ../..
else
    echo "Tomcat already set up"
fi
