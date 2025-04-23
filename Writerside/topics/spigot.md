# 🚰 Spigot

**Cocoabeans** has many utilities for Spigot that will help you make your plugin development faster and easier

## Relevant Links
* [Spigot javadocs](https://cocoa-beans.apartium.net/%version%/spigot/)

## Include in your project

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
        <artifactId>spigot</artifactId>
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
    compileOnly 'net.apartium.cocoa-beans:spigot:%version%'
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
    compileOnly("net.apartium.cocoa-beans:spigot:%version%")
}
```

</tab>



</tabs>

## Read more
* [](minecraft-version.md)
* [](visibility-api.md)