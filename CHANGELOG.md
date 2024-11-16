# CHANGELOG
## 0.0.37
- [commands] Enhanced command handling with improved serialization of subcommands
- [commands] Introduced a new CommandLexer interface for better command tokenization
- [commands] Added infrastructure for parameter names in argument parsers, for future improving clarity
- [commands] Updated exception handling in command tests for more precise error reporting
- [spigot] Change EnchantGlow's key and name so it wouldn't collide with other plugins
- [commands] Add support for named command arguments

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

## v0.0.29
- [commands] Improve location parser
- [common]  Add DummyCollection & DummyList
- [commands] optional or invalid arguments
- [spigot] visibility manager api
- [commands] Polymorphic command declaration
- Introduce new docs based on Writerside
