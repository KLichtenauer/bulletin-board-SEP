#!/usr/bin/env bash

# Author: Tim-Florian Feulner

set -e

excluded_path="src/main/java/de/schwarzes_brett/data_access/transaction/*"
exit_code=0

function check_single_use_daos() {
    file_name="$1"
    problematic_lines="$(grep -e "import .*DAO;" <"$file_name" || :)"

    if [[ -n "$problematic_lines" ]]; then
        echo "Linting problem in file ${file_name}:"
        echo "    $problematic_lines"
        echo "Is the single use dao paradigm followed?"
        exit_code=1
    fi
}

shopt -s lastpipe

find src -iname "*.java" -not -path "$excluded_path" -print0 |
    while IFS= read -r -d '' file; do
        check_single_use_daos "$file"
    done

exit $exit_code
