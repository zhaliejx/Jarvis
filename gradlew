#!/bin/sh
# Gradle wrapper script for Termux
# This is a simplified version for demonstration purposes

# Set GRADLE_OPTS to increase heap size
export GRADLE_OPTS="-Xmx1024m"

# Find the project root directory
PRJDIR="$(cd "$(dirname "$0")" && pwd)"

# Set the Gradle home directory
GRADLE_USER_HOME="${PRJDIR}/gradle"

# Create the Gradle cache directory if it doesn't exist
mkdir -p "${GRADLE_USER_HOME}/caches"

# Execute Gradle with the specified arguments
exec java \
  -XX:MaxMetaspaceSize=512m \
  -Dorg.gradle.appname="$(basename "$0")" \
  -classpath "${PRJDIR}/gradle/wrapper/gradle-wrapper.jar" \
  org.gradle.wrapper.GradleWrapperMain \
  "-g" "${GRADLE_USER_HOME}" \
  "$@"