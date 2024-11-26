#!/bin/bash

cd /tmp
exec java ${JAVA_OPTIONS} ${SPRING_PROFILES} -jar ${APP_JAR}