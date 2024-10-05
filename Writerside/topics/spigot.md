# 🚰 Spigot

**Cocoabeans** has many utility for spigot that will help you to make your plugin faster and easier

## Relevant Links
* [Spigot javadocs](https://cocoa-beans.apartium.net/%version%/spigot/)

## Include in your project

<tabs>
<tab title="Maven">

```xml
<repositories>
    <repository>
        <id>apartium-releases</id>
        <url>https://mvn.apartiumservices.com/repository/apartium-releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.apartium.cocoa-beans</groupId>
        <artifactId>spigot</artifactId>
        <version>[VERSION]</version>
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
    compileOnly 'net.apartium.cocoa-beans:spigot:[VERSION]'
}
```

</tab>

<tab title="Gradle - Kotlin">

```kotlin
repositories {
    maven {
        name = "ApartiumNexus"
        url = uri("https://nexus.voigon.dev/repository/apartium")
    }
}

dependencies {
    compileOnly("net.apartium.cocoa-beans:spigot:[VERSION]")
}
```

</tab>



</tabs>

## Read more
* [📜 Minecraft Version](minecraft-version.md)
* [👁️ Visibility Api](Visibility-Api.md)