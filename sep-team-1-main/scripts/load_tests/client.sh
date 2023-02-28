#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

# shellcheck disable=SC2154
process_id="$client_id"

if [[ -z "$process_id" ]]; then
    echo "No process id given."
    exit 1
fi

echo "Process with id $process_id"
echo "Hostname: $(hostname)"

server_url="https://tonno.cip.fim.uni-passau.de:8445/schwarzes_brett"
build_directory="target-lt-${process_id}"

export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"

cd "${HOME}/Dev/Git/sep-team-1"

set +e
mvn --no-transfer-progress -DSYSTEM_TEST_BROWSER=chrome -DPROCESS_ID="$process_id" -DSERVER_URL="$server_url" -DBUILD_DIRECTORY="$build_directory" \
    -DskipUnitTests -DskipIntegerationTests -Pload-tests clean compile verify
test_exit_value=$?
set -e

if [[ $test_exit_value -ne 0 ]]; then
    sleep 1d
fi
