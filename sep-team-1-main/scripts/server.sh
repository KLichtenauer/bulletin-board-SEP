#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

source "$(dirname "$0")/setup_tomcat.sh"

tomcatDir="tomcat/apache-tomcat"

source "$(dirname "$0")/build.sh"
source "$(dirname "$0")/package.sh"
cp target/*.war "${tomcatDir}/webapps/schwarzes_brett.war"

sh -c "sleep 2 && xdg-open http://localhost:8001/schwarzes_brett/" &

"${tomcatDir}/bin/catalina.sh" run
