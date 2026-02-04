#!/bin/sh

# Detect the operating system
unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)
        if [ -n "$ANDROID_DATA" ]; then
            os=Termux
        else
            os=Linux
        fi
        ;;
    Darwin*)
        os=Mac
        ;;
    CYGWIN*|MINGW*|MSYS*)
        os=Windows
        ;;
    *)
        os=Linux
        ;;
esac

# Locate the project root directory
if [ -n "${BASH_SOURCE-}" ]; then
    GRADLE_PROPS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
elif [ -n "${ZSH_VERSION-}" ]; then
    GRADLE_PROPS_DIR="$(cd "$(dirname "$0")" && pwd)"
else
    # Use POSIX-compliant way to get script directory
    SCRIPT_DIR=$(dirname "$0")
    GRADLE_PROPS_DIR=$(cd "$SCRIPT_DIR" && pwd)
fi

# Set the main classpath
CLASSPATH="${GRADLE_PROPS_DIR}/gradle/wrapper/gradle-wrapper.jar"

# Set the Java command
if [ -n "${JAVA_HOME-}" ]; then
    if [ "$os" = "Windows" ]; then
        JAVA_CMD="${JAVA_HOME}/bin/java.exe"
    else
        JAVA_CMD="${JAVA_HOME}/bin/java"
    fi
else
    JAVA_CMD="java"
fi

# Verify that Java is available
if ! command -v "$JAVA_CMD" >/dev/null 2>&1; then
    echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." >&2
    exit 1
fi

# Verify that the wrapper JAR exists
if [ ! -f "$CLASSPATH" ]; then
    echo "ERROR: Could not find gradle-wrapper.jar at $CLASSPATH" >&2
    exit 1
fi

# Execute the Gradle wrapper
exec "$JAVA_CMD" \
    -Dorg.gradle.appname=gradlew \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"