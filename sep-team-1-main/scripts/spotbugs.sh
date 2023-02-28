#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

mvn --no-transfer-progress -DskipUnitTests -DskipIntegrationTests clean compile test-compile spotbugs:check
