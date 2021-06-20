#!/bin/sh

java \
  -Xmx${JAVA_HEAP_XMX} \
  -Xms${JAVA_HEAP_XMS} \
  -jar ${1}
