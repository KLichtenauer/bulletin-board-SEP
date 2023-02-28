#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

echo "This file should not be executed."
exit

docker build -t rederror/sep2022:def-1.0 cicd/docker/default
docker push rederror/sep2022:def-1.0

docker build -t rederror/sep2022:st-1.0.2 cicd/docker/system-test
docker push rederror/sep2022:st-1.0.2
