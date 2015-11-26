#!/bin/sh

JAR=demo-1.0-SNAPSHOT.jar
if [ -f "$JAR" ]; then

  ENVIRONMENT="DEMO"
  [ -n "$1" ] && ENVIRONMENT="$1"

  PROPERTY_FILE=""
  [ -n "$2" ] && PROPERTY_FILE="$2"

  JAVA_OPTS="-Dapp.environment=$ENVIRONMENT"
  [ -n "$PROPERTY_FILE" ] && JAVA_OPTS="$JAVA_OPTS -Dapp.properties=$PROPERTY_FILE"

  echo "Starting $JAR with options $JAVA_OPTS"
  java ${JAVA_OPTS} -jar ${JAR}
else
  echo "jar file $JAR not found!"
fi
