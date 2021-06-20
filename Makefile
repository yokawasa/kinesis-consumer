.PHONY: clean all build

.DEFAULT_GOAL := all

TARGETS=build

CUR := $(shell pwd)
OS := $(shell uname)
VERSION := $(shell cat ${CUR}/VERSION)
IMAGE_REPO := amazon-kinesis-consumer
IMAGE_TAG := ${VERSION}
REGISTORY := docker.io
DOCKER_ACCOUNT := yoichikawasaki

all: $(TARGETS)

build:
	mvn package

docker-build:
	set -x
	export DOCKER_BUILDKIT=1
	docker build -t ${IMAGE_REPO}:${IMAGE_TAG} . --target executor

docker-push:
	docker login --username ${DOCKER_ACCOUNT}
	docker tag ${IMAGE_REPO}:${IMAGE_TAG} ${REGISTORY}/${DOCKER_ACCOUNT}/${IMAGE_REPO}:${IMAGE_TAG}
	docker push ${REGISTORY}/${DOCKER_ACCOUNT}/${IMAGE_REPO}:${IMAGE_TAG}

clean:
	mvn clean
