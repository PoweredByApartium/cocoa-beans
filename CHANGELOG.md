# CHANGELOG
## 0.0.71-t
- test

## 0.0.41
- [scoreboard] Add numeric & team display
- [scoreboard] Add ViewerGroup
- [state] Add LazyWatcher
- [scoreboard] Add TabList api

## 0.0.40
- [spigot] Add NMSUtils
- [common] Add Ensures#isFalse
- [repo] Add platform maven artifact
- [commands] Make PluginParser case insensitive

## 0.0.39
- [commands-spigot] Add `PluginParser`
- [commands-spigot] Add `WorldParser` to SpigotParsers
- [commands-spigot] Fix `WorldParser` to support on legacy versions
- [common] Bump Minecraft Version 1.21.2 - 1.21.5
- [commands] Add Contextual Report for "Not Found" Results in `MapBasedParser` as `NoSuchElementInMapResponse`
- [common] 📺 **Observable** api
- [commands] Add ContextualMapBasedParser
- [commands] Add `VirtualCommand`
- [minecraft] Add minecraft module
- [scoreboard] Add Scoreboard api for Spigot & Minestom
- [animation] Add Animation state

## 0.0.38
- [commands] Add `WrappedArgumentParser` for better handling of argument parsers
- [commands-spigot] Improve `LocationParser` that will use `CompoundParser`
- [commands-spigot] Add `WorldParser`
- [commands] Fixes CompoundParser method access modifiers
- [commands] Fixes MapBasedParser tabCompletion index
- [commands] Added error logging for cases where subcommand methods are non-public or when variables are misused.
- [commands] Added `MethodUtils#getAllMethods(Class)` for getting all methods of a class including inherited & public & non-public
- [commands] Fixes bug with `ExecptionHandle` ignoring value
- [commands] Removed `ExecptionArgumentMapper` in favor of `ArgumentMapper`
- [commands] Added additional types to `ArgumentMapper#mapIndices`
- [commands] Added External Requirement Factory: Allows custom factories to override annotation behavior, enabling requirements from external sources. `CommandManager#registerRequirementFactory`
- [commands] Added `EvaluationContext`
  
## 0.0.37
- [commands] Enhanced command handling with improved serialization of subcommands
- [commands] Introduced a new CommandLexer interface for better command tokenization
- [commands] Added infrastructure for parameter names in argument parsers, for future improving clarity
- [commands] Updated exception handling in command tests for more precise error reporting
- [spigot] Change EnchantGlow's key and name so it wouldn't collide with other plugins
- [commands] Add support for named command arguments
- [common] Add `CollectionHelpers#mergeInto(Map<K, V>, Map<K, V>)` to add all elements from one map to another if key isn't present
- [commands] Improve code structure for registering a command
- [commands] Add Compound parser
- [commands] Add `ParserAssertions#assertParserTabCompletion` for better unit testing

## 0.0.36
- [common] Improve MinecraftVersion added known version (1.8 to 1.21.1) with protocol number and some helper methods
- [commands] Add UUIDParser
- [commands] Add GameMode parser with lazy mapping
- [spigot] Fix getting protocol version before checking if server version is known
- [spigot] Add simple dependency injection capabilities to ListenerAutoRegistration
- [commands] Add ParserAssertions class for parser testing
- [commands-spigot] Add GameModeParser to `SpigotCommandManager.SPIGOT_PARSERS`
- [commands] WithParser support keyword change
- [commands] Add new constructor to every `ArgumentParser` with those two arguments `int, String`
- [commands] Add QuotedStringParser & ParagraphParser
- [commands] Improve ObjectMapper performance by pre-calculating parsers during initialization instead of at runtime

## 0.0.35
- [spigot] **hotfix** Wrong package for `PlayerVisibilityController_1_8_R1`
- [spigot] **hotfix** Hidden groups don't always update (Added test to make sure it doesn't happen again)
- [spigot] **hotfix** Fixes reload when leave all groups
- [spigot-commands] Fixes the bug of sender type invert didn't work at new version

## 0.0.32
- [commands] Add lazy map for map based parser
- [commands] Add `CommandProcessingContext#report`
- [commands] Add additional CommandInfo as such `Description` & `Usage` & `LongDescription`
- [commands] Fixes duplication of tab completion options
- [commands] Add ignore case for map based parser
- [commands] Add dynamic parser for method/class
- [common] Add Rotation and Transform classes
- [docs] Add docs talking about region box, transform, region and rotation.
- [spigot-commands] `MaterialParser` using namespaced key for tab competition
- [spigot] Fix visibility API to work on 1.8
- [commands] Add `ValueRequirement`
- [docs] Add docs about region box, transform, region and rotation.

## v0.0.29
- [commands] Improve location parser
- [common]  Add DummyCollection & DummyList
- [commands] optional or invalid arguments
- [spigot] visibility manager api
- [commands] Polymorphic command declaration
- Introduce new docs based on Writerside
