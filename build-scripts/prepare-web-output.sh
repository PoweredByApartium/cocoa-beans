#!/bin/bash

if [ ! -d "docs" ]; then
  mkdir $docs
  unzip artifacts/webHelpCB2-all.zip -d $docs
  mkdir -p $docs/common
  mkdir -p $docs/state
  mkdir -p $docs/spigot
  mkdir -p $docs/minestom
  mkdir -p $docs/state-spigot
  mkdir -p $docs/commands-spigot
  mkdir -p $docs/commands
  mkdir -p $docs/scoreboard
  mkdir -p $docs/scoreboard-spigot
  mkdir -p $docs/scoreboard-minestom

  ls $docs/
  cp -a common/build/docs/javadoc/* $docs/common
  cp -a state/build/docs/javadoc/* $docs/state
  cp -a minestom/build/docs/javadoc/* $docs/minestom
  cp -a state/spigot/build/docs/javadoc/* $docs/state-spigot
  cp -a spigot/build/docs/javadoc/* $docs/spigot
  cp -a commands/build/docs/javadoc/* $docs/commands
  cp -a commands/spigot/build/docs/javadoc/* $docs/commands-spigot
  cp -a scoreboard/build/docs/javadoc/* $docs/scoreboard
  cp -a scoreboard/spigot/build/docs/javadoc/* $docs/scoreboard-spigot
  cp -a scoreboard/minestom/build/docs/javadoc/* $docs/scoreboard-minestom
fi