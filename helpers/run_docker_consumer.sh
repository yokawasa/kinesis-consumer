#!/usr/bin/env bash
[[ -n $DEBUG ]] && set -x -e

if [ $# -ne 1 ]
then
  echo "$0 <env file>"
  echo "ex."
  echo "$0 myconfig.env"
  exit 1
fi

source "${1}"

SCRIPTS_DIR=$(cd $(dirname $0); pwd)
PROJECT_DIR=${SCRIPTS_DIR}/..
VERSION=$(cat ${PROJECT_DIR}/VERSION)
IMAGE_REPO=amazon-kinesis-consumer
IMAGE_TAG=${VERSION}
AWS_PROFILE_NAME=myenv

docker run --rm -it \
		-v ${HOME}/.aws:/root/.aws \
    -e AWS_PROFILE=${AWS_PROFILE_NAME} \
    -e KINESIS_APPLICATION_NAME=${KINESIS_APPLICATION_NAME} \
    -e KINESIS_STREAM_NAME=${KINESIS_STREAM_NAME} \
    -e KINESIS_REGION=${KINESIS_REGION} \
    -e JAVA_HEAP_XMX=512M \
    -e JAVA_HEAP_XMS=512M \
		${IMAGE_REPO}:${IMAGE_TAG}
