#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

# Package war file
mvn --no-transfer-progress -DskipUnitTests -DskipIntegrationTests -DskipSystemTests clean compile package

# Package maven project into zip file
zip_target="target/schwarzes_brett-1.0-maven-project.zip"

cd src/main
main_package_infos=$(find . -name "package-info.java" | sort)
cd ../test
test_package_infos=$(find . -name "package-info.java" | sort)
cd ../..
excluded_package_infos=$(comm -12 <(echo "$test_package_infos") <(echo "$main_package_infos") | sed -e 's/.\//-x src\/test\//')

rm -f "$zip_target"
# shellcheck disable=SC2086
zip -r "$zip_target" cicd scripts src README.md java-pmd-ruleset.xml .editorconfig \
    pom.xml .gitlab-ci.yml .mega-linter.yml .sqlfluff sqlrunner lib/postgresql-42.5.0.jar $excluded_package_infos >/dev/null
