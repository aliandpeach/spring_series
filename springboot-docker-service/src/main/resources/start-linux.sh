#!/bin/sh

BLOCK_CHAIN_CURRENT_PATH=`pwd`
BLOCK_CHAIN_JAR_PATH="${BLOCK_CHAIN_CURRENT_PATH}"/springboot-docker-service-1.0-SNAPSHOT.jar
BLOCK_CHAIN_YML_PATH="${BLOCK_CHAIN_CURRENT_PATH}"/*.yml
nohup java -Xmx1024m -Xms64m -XX:ThreadStackSize=512 -jar "${BLOCK_CHAIN_JAR_PATH}" --spring.config.location="${BLOCK_CHAIN_YML_PATH}" &