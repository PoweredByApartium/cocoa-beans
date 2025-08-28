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
  cp -a cocoa-beans-common/build/docs/javadoc/* $docs/common
  cp -a cocoa-beans-state/build/docs/javadoc/* $docs/state
  cp -a cocoa-beans-minestom/build/docs/javadoc/* $docs/minestom
  cp -a cocoa-beans-state-spigot/build/docs/javadoc/* $docs/state-spigot
  cp -a cocoa-beans-spigot/build/docs/javadoc/* $docs/spigot
  cp -a cocoa-beans-commands/build/docs/javadoc/* $docs/commands
  cp -a cocoa-beans-commands-spigot/build/docs/javadoc/* $docs/commands-spigot
  cp -a cocoa-beans-scoreboard/build/docs/javadoc/* $docs/scoreboard
  cp -a cocoa-beans-scoreboard-spigot/build/docs/javadoc/* $docs/scoreboard-spigot
  cp -a cocoa-beans-scoreboard-minestom/build/docs/javadoc/* $docs/scoreboard-minestom
fi