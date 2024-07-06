#!/bin/bash

if [ ! -d "docs" ]; then
  mkdir $docs
  unzip artifacts/webHelpCB2-all.zip -d $docs
  mkdir -p $docs/common
  mkdir -p $docs/spigot
  mkdir -p $docs/commands-spigot
  mkdir -p $docs/commands

  ls $docs/
  cp -a common/build/docs/javadoc/* $docs/common
  cp -a spigot/build/docs/javadoc/* $docs/spigot
  cp -a commands/build/docs/javadoc/* $docs/commands
  cp -a commands/spigot-platform/build/docs/javadoc/* $docs/commands-spigot
fi