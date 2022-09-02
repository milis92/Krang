#!/bin/bash

set -o pipefail -e

readonly usage="

$(basename "$0") [-h] [ -f=\"path/to/file\" -p=\"key\"]

where:
    -h --help            show this help text
    -f --file            properties file
    -k --key             specific property key to look for
"

# Input file
file=

# Property to look for
key=

# Parse arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
  --help | -h)
    echo "$usage" >&2
    exit 0
    ;;
  --file=* | -f=*)
    file="${1#*=}"
    ;;
  --key=* | -k=*)
    key="${1#*=}"
    ;;
  *)
    printf "Error: Invalid argument: %s" "$1"
    echo "$usage" >&2
    exit 1
    ;;
  esac
  shift
done

## Verify we have all the arguments
if [[ -z "$file" ]]; then
  printf "Error: Missing argument: file"
  echo "$usage" >&2
  exit 1
elif [[ -z "$key" ]]; then
  printf "Error: Missing argument: key"
  echo "$usage" >&2
  exit 1
fi

# Extract prop from file
grep "${key}" "${file}" | cut -d'=' -f2
