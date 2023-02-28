#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

mvn --no-transfer-progress -Pintegration-tests -DskipUnitTests verify
grep -o '<tfoot>.*</tfoot>' <target/site/jacoco-it/index.html
