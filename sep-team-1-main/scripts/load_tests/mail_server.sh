#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"

cd "${HOME}/Dev/Git/sep-team-1"

java -Dgreenmail.smtp.hostname=0.0.0.0 -Dgreenmail.smtp.port=3027 \
    -Dgreenmail.imap.hostname=0.0.0.0 -Dgreenmail.imap.port=3028 \
    -Dgreenmail.verbose -jar lib/greenmail-standalone-2.0.0-alpha-3.jar
