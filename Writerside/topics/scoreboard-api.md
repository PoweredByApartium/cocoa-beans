# 📊 Scoreboard Api

<sup>
Available Since 0.0.39
</sup>

**Table of content:**
- [Introduction](#introduction)
- [Showcase](#showcase)
- [Usage](#usage)
- [Advantages](#advantages)
- [](#include-in-your-project)
- [What next](#what-next)

## Introduction
The Scoreboard Api aka `CocoaBoard` is a simple way to work with scoreboard in minecraft,
The Scoreboard Api work with [Observable Api](observable.md) as it accept observable and update the scoreboard by itself

(as for now we only support sidebar scoreboard but there are plan to make it work with list and below-name also with teams)

## Showcase

<video src="scoreboard-showcase.mp4" preview-src="scoreboard-showcase.png" />

<video src="scoreboard-helloworld.mp4" preview-src="scoreboard-hellowolrd.png" />

<video src="scoreboard-system-info.mp4" preview-src="scoreboard-system-info.png" />

<img src="scoreboard-animation.gif"  alt="Scoreboard-animation.gif"/>

## Usage
First we will want to create BoardManager
<tabs>
<tab title="BoardManager.java">

```java
public class BoardManager {
    private final Map<UUID, CocoaBoard> boards = new HashMap<>();
    private final MutableObservable<Long> heartbeatTime;
    private BukkitTask cprTask;

    public BoardManager() {
        this.heartbeatTime = Observable.mutable(0L);
    }

    public void initialize(JavaPlugin plugin) {
        if (cprTask != null)
            return;

        cprTask = new BukkitRunnable() {
            @Override
            public void run() {
                heartbeat();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void heartbeat() {
        long startTime = System.currentTimeMillis();

        for (CocoaBoard board : boards.values())
            board.heartbeat();

        long endTime = System.currentTimeMillis();
        
        // can be used to get duration of heartbeat
        heartbeatTime.set(endTime - startTime); 
    }

    public CocoaBoard getBoard(Player player) {
        return boards.computeIfAbsent(
                player.getUniqueId(), 
                uuid -> SpigotCocoaBoard.create(
                        player, // Player to create scoreboard for
                        "example", // Objective id
                        Component.text("Test") // name could be observable
                )
        );
    }

    public void unregisterBoard(UUID targetUUID) {
        CocoaBoard board = boards.remove(targetUUID);
        if (board != null)
            board.delete();
    }
    
    public void disable() {
        if (cprTask == null)
            return;

        cprTask.cancel();
        cprTask = null;

        for (CocoaBoard board : boards.values())
            board.delete();


        boards.clear();
    }

    // Getters
    public Observable<Long> getHeartbeatTime() {
        return heartbeatTime;
    }
}
```

</tab>
</tabs>

## Advantages

## Include in your project
<tabs>
<tab title="Standalone">
<tabs>
<tab title="Maven">

```xml
<repositories>
    <repository>
        <id>apartium-releases</id>
        <url>https://nexus.voigon.dev/repository/apartium-releases</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>net.apartium.cocoa-beans</groupId>
    <artifactId>scoreboard</artifactId>
    <version>%version%</version>
</dependency>
</dependencies>
```

</tab>

<tab title="Gradle">

```groovy
repositories {
    maven {
        name 'apartium-releases'
        url 'https://nexus.voigon.dev/repository/apartium-releases'
    }
}

dependencies {
    compileOnly("net.apartium.cocoa-beans:scoreboard:%version%")
}
```

</tab>

<tab title="Gradle - Kotlin">

```kotlin
repositories {
    maven {
        name = "apartium-releases"
        url = uri("https://nexus.voigon.dev/repository/apartium-releases")
    }
}

dependencies {
    compileOnly("net.apartium.cocoa-beans:scoreboard:%version%")
}
```

</tab>
</tabs>
</tab>
<tab title="Spigot">
<tabs>
<tab title="Maven">

```xml
<repositories>
    <repository>
        <id>apartium-releases</id>
        <url>https://nexus.voigon.dev/repository/apartium-releases</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>net.apartium.cocoa-beans</groupId>
    <artifactId>scoreboard-spigot</artifactId>
    <version>%version%</version>
</dependency>
</dependencies>
```

</tab>

<tab title="Gradle">

```groovy
repositories {
    maven {
        name 'apartium-releases'
        url 'https://nexus.voigon.dev/repository/apartium-releases'
    }
}

dependencies {
    compileOnly("net.apartium.cocoa-beans:scoreboard-spigot:%version%")
}
```

</tab>

<tab title="Gradle - Kotlin">

```kotlin
repositories {
    maven {
        name = "apartium-releases"
        url = uri("https://nexus.voigon.dev/repository/apartium-releases")
    }
}

dependencies {
    compileOnly("net.apartium.cocoa-beans:scoreboard-spigot:%version%")
}
```

</tab>
</tabs>
</tab>
<tab title="Minestom">
<tabs>
<tab title="Maven">

```xml
<repositories>
    <repository>
        <id>apartium-releases</id>
        <url>https://nexus.voigon.dev/repository/apartium-releases</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>net.apartium.cocoa-beans</groupId>
    <artifactId>scoreboard-minestom</artifactId>
    <version>%version%</version>
</dependency>
</dependencies>
```

</tab>

<tab title="Gradle">

```groovy
repositories {
    maven {
        name 'apartium-releases'
        url 'https://nexus.voigon.dev/repository/apartium-releases'
    }
}

dependencies {
    compileOnly("net.apartium.cocoa-beans:scoreboard-minestom:%version%")
}
```

</tab>

<tab title="Gradle - Kotlin">

```kotlin
repositories {
    maven {
        name = "apartium-releases"
        url = uri("https://nexus.voigon.dev/repository/apartium-releases")
    }
}

dependencies {
    compileOnly("net.apartium.cocoa-beans:scoreboard-minestom:%version%")
}
```

</tab>
</tabs>
</tab>
</tabs>

## What next
* [](animation.md)
* [Javadocs](https://cocoa-beans.apartium.net/%version%/scoreboard/net/apartium/cocoabeans/scoreboard/package-summary.html)