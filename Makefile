.PHONY: clean all build

.DEFAULT_GOAL := all

TARGETS=build

CUR := $(shell pwd)
OS := $(shell uname)
VERSION := $(shell cat ${CUR}/VERSION)
IMAGE_REPO := amazon-kinesis-consumer
IMAGE_TAG := ${VERSION}

all: $(TARGETS)

build:
	mvn package

docker-build:
	set -x
	export DOCKER_BUILDKIT=1
	docker build -t ${IMAGE_REPO}:${IMAGE_TAG} . --target executor

clean:
	mvn clean
