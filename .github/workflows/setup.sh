#!/bin/bash

java -version # Debug only

run: ./gradlew javadoc

mkdir docs
mkdir docs/common
mkdir docs/spigot
mkdir docs/commands
mkdir docs/commands-spigot
unzip artifacts/webHelpCB2-all.zip -d docs
ls docs/
cp -a common/build/docs/javadoc/* docs/common
cp -a spigot/build/docs/javadoc/* docs/spigot
cp -a commands/build/docs/javadoc/* docs/commands
cp -a commands/spigot-platform/build/docs/javadoc/* docs/commands-spigot