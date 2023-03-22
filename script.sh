#!/bin/bash

set -euo pipefail

FAT_JAR="./build/libs/tkom-1.0-SNAPSHOT-all.jar"
FILE="./src/main/resources/query.txt"

help () {
    cat << EOF
Usage: script.sh [OPTION] [FILE]
    -h --help        Display this message
    -c --clean       Re-build project from scratch before running application
EOF
exit 0;
}

while [[ $# -gt 0 ]]; do
  case $1 in
    -h|--help)
        help
        ;;
    -c|--clean)
        ./gradlew clean
        shift
        ;;
    -*)
        echo "Unknown option $1" 1>&2
        help
        exit 1
        ;;
    *)
        FILE="$1"
        shift
        ;;
  esac
done



if [ ! -f "$FAT_JAR" ]; then
  ./gradlew shadowJar
fi

java -jar "$FAT_JAR" "$FILE"
