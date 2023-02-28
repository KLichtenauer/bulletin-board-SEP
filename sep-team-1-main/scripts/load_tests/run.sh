#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

user="feulner"
server_host="tonno.cip.fim.uni-passau.de"
server_ssh_target="${user}@${server_host}"
smtp_server_host="goldfish.cip.fim.uni-passau.de"
smtp_server_ssh_target="${user}@${smtp_server_host}"
website_url="http://${server_host}:8001/schwarzes_brett"
client_hosts=(
    "lasagne.cip.fim.uni-passau.de"
    "regina.cip.fim.uni-passau.de"
    "ravioli.cip.fim.uni-passau.de"
    "romana.cip.fim.uni-passau.de"
    "discovery.cip.fim.uni-passau.de"
    "swan.cip.fim.uni-passau.de"
    "ds9.cip.fim.uni-passau.de"
    "lobster.cip.fim.uni-passau.de"
    "romana.cip.fim.uni-passau.de"
    "shark.cip.fim.uni-passau.de"
    "ravioli.cip.fim.uni-passau.de"
    "waterlily.cip.fim.uni-passau.de"
    "rigatoni.cip.fim.uni-passau.de"
    "auster.cip.fim.uni-passau.de"
    "prosecco.cip.fim.uni-passau.de"
    "kelp.cip.fim.uni-passau.de"
    "scampi.cip.fim.uni-passau.de"
    "moya.cip.fim.uni-passau.de"
    "crab.cip.fim.uni-passau.de"
    "penguin.cip.fim.uni-passau.de"
)

# Reset application database
echo "Resetting application database"
sql_command="scripts/load_tests/sqlrunner.sh jdbc:postgresql://bueno.fim.uni-passau.de/sep22g01t sep22g01 eek7no7yooSh"
$sql_command "DROP SCHEMA IF EXISTS schwarzes_brett CASCADE"

# Init test record database
echo "Init test record database"
execution_time_schema_script=$(cat "src/test/resources/load_tests/executionTimeSchema.sql")
$sql_command "$execution_time_schema_script"

# Setup server configuration
# shellcheck disable=SC2029
ssh "$server_ssh_target" -t "$(cat "scripts/load_tests/server_init.sh")"

# Start mail server
# shellcheck disable=SC2029
gnome-terminal --wait -- ssh "$smtp_server_ssh_target" -t "$(cat "scripts/load_tests/mail_server.sh")" &
mail_server_pid=$!

# Start application server
echo "Starting server on $server_ssh_target "
# shellcheck disable=SC2029
gnome-terminal --wait -- ssh "$server_ssh_target" -t "$(cat "scripts/load_tests/server_start.sh")" &
# shellcheck disable=SC2091
until $(curl --output /dev/null --silent --head --insecure --fail ${website_url}); do
    printf '.'
    sleep 2
done
echo "Server started"

# Inject testing data
max_index=${#client_hosts[@]}
((max_index--)) || :
testing_data_script=""
for id in $(seq 0 "$max_index"); do
    echo "Injecting testing data for client $id"
    testing_data_script="${testing_data_script} $(cat "src/test/resources/de/schwarzes_brett/testing_data/testingDataST.sql")"
    testing_data_script="${testing_data_script//\{ID\}/$id}"
done
$sql_command "$testing_data_script"

# Run clients
sleep 5
client_id=0
for client_host in "${client_hosts[@]}"; do
    client_ssh_target="${user}@${client_host}"
    gnome-terminal --wait -- ssh "$client_ssh_target" -t "client_id=$client_id;$(cat "scripts/load_tests/client.sh")" &
    pids[${client_id}]=$!
    ((client_id++)) || :
    sleep 2
done
for pid in "${pids[@]}"; do
    wait $pid
done

# Stop application server
echo "Stopping server"
# shellcheck disable=SC2029
ssh "$server_ssh_target" -t "$(cat "scripts/load_tests/server_stop.sh")"

# Stop mail server
echo "Stopping mail server"
kill $mail_server_pid
