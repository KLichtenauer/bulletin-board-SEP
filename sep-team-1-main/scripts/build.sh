#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

mvn --no-transfer-progress clean compile test-compile
