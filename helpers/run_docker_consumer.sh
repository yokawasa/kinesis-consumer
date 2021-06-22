#!/usr/bin/env bash
set -e -x
[[ -n $DEBUG ]] && set -x -e

if [ $# -ne 2 ]
then
  echo "$0 <env file> <aws-profile-name>"
  echo "ex."
  echo "$0 myconfig.env my-aws-profile"
  exit 1
fi

source "${1}"

AWS_PROFILE="${2}"

SCRIPTS_DIR=$(cd $(dirname $0); pwd)
PROJECT_DIR=${SCRIPTS_DIR}/..
VERSION=$(cat ${PROJECT_DIR}/VERSION)
IMAGE_REPO=yoichikawasaki/amazon-kinesis-consumer
IMAGE_TAG=${VERSION}

docker run --rm -it \
    -v ${HOME}/.aws:/root/.aws \
    -e AWS_PROFILE=${AWS_PROFILE} \
    -e KINESIS_APPLICATION_NAME=${KINESIS_APPLICATION_NAME} \
    -e KINESIS_STREAM_NAME=${KINESIS_STREAM_NAME} \
    -e KINESIS_REGION=${KINESIS_REGION} \
    -e JAVA_HEAP_XMX=${JAVA_HEAP_XMX} \
    -e JAVA_HEAP_XMS=${JAVA_HEAP_XMS} \
    ${IMAGE_REPO}:${IMAGE_TAG}
