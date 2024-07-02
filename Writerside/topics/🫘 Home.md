# 🫘 Cocoa Beans

Welcome to the cocoa-beans wiki!
Cocoa beans is a toolkit for you to take with you to every new project. Instead of copying and pasting utilities code between projects, merge them into cocoa beans and improve other developer's utilities, so you can save time and complexity managing code across multiple projects.

## Relevant Links
* [Spigot javadocs](https://cocoa-beans.apartium.net/v170.0.1.test/spigot/)
* [Common javadocs](https://cocoa-beans.apartium.net/v170.0.1.test/common/)
* [Commands javadocs](https://cocoa-beans.apartium.net/v170.0.1.test/commands/)

## Including in your project

We support Gradle and Maven build scripts.
The recommended approach to using Cocoa Beans is to include the library in your project's classpath, and install it as a separate plugin. 
Cocoa Beans is available as a plugin from Hangar. 

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
        <!-- You can also use 'spigot' instead to get the spigot utilities as well -->
        <artifactId>common</artifactId>
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
    // You can also use 'spigot' instead to get the spigot utilities as well
    compileOnly 'net.apartium.cocoa-beans:common:[VERSION]'
}
```

</tab>

</tabs>
