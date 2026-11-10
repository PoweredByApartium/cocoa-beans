<div align="center">

# 🫘 Cocoa Beans

**A modular Java toolkit for Minecraft developers — stop copy-pasting the same code between projects.**

![main](https://github.com/poweredbyapartium/cocoa-beans/actions/workflows/main.yml/badge.svg)
![License](https://img.shields.io/github/license/PoweredByApartium/cocoa-beans)
![Java](https://img.shields.io/badge/Java-17+-orange)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=PoweredByApartium_cocoa-beans&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=PoweredByApartium_cocoa-beans)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=PoweredByApartium_cocoa-beans&metric=coverage)](https://sonarcloud.io/summary/new_code?id=PoweredByApartium_cocoa-beans)
[![Latest release](https://img.shields.io/github/v/release/PoweredByApartium/cocoa-beans)](https://github.com/PoweredByApartium/cocoa-beans/releases/latest)

[📜 Docs & Wiki](https://cocoa-beans.apartium.net/) · [📦 Releases](https://github.com/PoweredByApartium/cocoa-beans/releases) · [🤝 Contributing](CONTRIBUTING.md)

</div>

## ✨ What's inside

| Module | What it gives you |
|---|---|
| 🧰 **common** | Core java utilities |
| 🖥️ **commands** / **commands-spigot** | Annotation-based command system with parsers, tab completion and more |
| 🔄 **state** / **state-spigot** / **state-animation** | Reactive state and observables, plus text animations |
| 📊 **scoreboard** (+ spigot, minestom) | Minecraft scoreboards without the boilerplate |
| 🗺️ **schematic** (+ spigot, minestom) | Schematic library with custom binary format |
| 🚰 **spigot** | Spigot utilities |
| ⛏️ **minestom** | Minestom utilities |

📚 **Javadocs:**
[common](https://cocoa-beans.apartium.net/snapshot/common/) ·
[commands](https://cocoa-beans.apartium.net/snapshot/commands/) ·
[commands-spigot](https://cocoa-beans.apartium.net/snapshot/commands-spigot/) ·
[state](https://cocoa-beans.apartium.net/snapshot/state/) ·
[state-spigot](https://cocoa-beans.apartium.net/snapshot/state-spigot/) ·
[scoreboard](https://cocoa-beans.apartium.net/snapshot/scoreboard/) ·
[scoreboard-spigot](https://cocoa-beans.apartium.net/snapshot/scoreboard-spigot/) ·
[scoreboard-minestom](https://cocoa-beans.apartium.net/snapshot/scoreboard-minestom/) ·
[schematic](https://cocoa-beans.apartium.net/snapshot/schematic/) ·
[schematic-spigot](https://cocoa-beans.apartium.net/snapshot/schematic-spigot/) ·
[spigot](https://cocoa-beans.apartium.net/snapshot/spigot/)

> [!IMPORTANT]
> **Requirements:** Java 17+ · Minecraft 1.8 and above (for the Spigot modules)

## 📦 Installation

> [!NOTE]
> Cocoa Beans can be shaded into your jar, but if you're making a Spigot plugin, consider using the library as a plugin from Hangar instead.

Replace `[VERSION]` with the latest release.

**Gradle (Kotlin DSL):**
```kotlin
dependencies {
    implementation("dev.apartium.cocoa-beans:cocoa-beans-common:[VERSION]")

    // Command system (use commands-spigot for the Spigot utilities)
    implementation("dev.apartium.cocoa-beans:cocoa-beans-commands:[VERSION]")
}
```

**Maven:**
```xml
<dependencies>
    <dependency>
        <groupId>dev.apartium.cocoa-beans</groupId>
        <artifactId>cocoa-beans-common</artifactId>
        <version>[VERSION]</version>
    </dependency>

    <!-- Command system (use commands-spigot for the Spigot utilities) -->
    <dependency>
        <groupId>dev.apartium.cocoa-beans</groupId>
        <artifactId>cocoa-beans-commands</artifactId>
        <version>[VERSION]</version>
    </dependency>
</dependencies>
```

Swap `common` for `spigot` (or any other module above) to pull in its artifact: `cocoa-beans-<module>`.

## 🤝 Contributing

Contributions are very welcome! Check out [CONTRIBUTING.md](CONTRIBUTING.md) to get started, and see the [CHANGELOG](CHANGELOG.md) for what's new.

## 📄 License

See [LICENSE.md](LICENSE.md).
