#!/bin/bash
# Script to run the Kotlin JVM networking test

echo "Starting PeerSignal Kotlin JVM Test..."
echo "Checking for modern Gradle (8.7)..."
if [ ! -d "gradle-8.7" ]; then
    echo "Downloading Gradle 8.7..."
    curl -L -O https://services.gradle.org/distributions/gradle-8.7-bin.zip
    echo "Unzipping Gradle 8.7..."
    unzip -q gradle-8.7-bin.zip
    rm gradle-8.7-bin.zip
fi

echo "Installing Java 17 (Gradle 8.7 does not support Java 25)..."
apt-get update -qq && apt-get install -y openjdk-17-jdk-headless -qq
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-$(dpkg --print-architecture)

echo "Running gradle test task (using modern gradle-8.7 and Java 17)..."
./gradle-8.7/bin/gradle app:testDebugUnitTest --tests "com.peersignal.app.ParserApiClientTest"

if [ $? -eq 0 ]; then
    echo "======================================"
    echo "✅ TEST PASSED: Kotlin successfully parsed the Python proxy response!"
    echo "======================================"
else
    echo "======================================"
    echo "❌ TEST FAILED: Check if proxy is running and gradle is configured."
    echo "======================================"
fi
