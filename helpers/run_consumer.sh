#!/usr/bin/env bash
[[ -n $DEBUG ]] && set -x -e

SCRIPTS_DIR=$(cd $(dirname $0); pwd)
PROJECT_DIR=${SCRIPTS_DIR}/..

if [ $# -ne 1 ]
then
  echo "$0 <env file>"
  echo "ex."
  echo "$0 myconfig.env"
  exit 1
fi

JAR=$(find ${PROJECT_DIR}/target -name "*.jar")
source "${1}"
${PROJECT_DIR}/entrypoint.sh ${JAR}
