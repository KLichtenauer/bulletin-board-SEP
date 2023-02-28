#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

cd "${HOME}/Dev/Git/sep-team-1"

tomcatDir="tomcat/apache-tomcat"
"${tomcatDir}/bin/shutdown.sh"

git checkout -
