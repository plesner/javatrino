#!/bin/sh

# Bail on error.
set -e

JAR=bin/neutrino.jar

if [ ! -f ${JAR} ]; then
  echo Can\'t find ${JAR}. Making it.
  make ${JAR}
fi

echo java -jar ${JAR} $*
