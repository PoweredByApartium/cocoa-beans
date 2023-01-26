![GitHub](https://img.shields.io/github/license/PoweredByApartium/cocoa-beans)
[![GitHub branch checks state](https://github.com/PoweredByApartium/cocoa-beans/actions/workflows/gh-publish.yml/badge.svg)](https://github.com/PoweredByApartium/cocoa-beans/actions/workflows/gh-publish.yml)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/PoweredByApartium/cocoa-beans?style=plastic)

## Welcome to Cocoa beans!
This library aims to be a very well-made and thought-out toolkit for developers to save the hassle
of copying common code between projects. Cocoa beans is modular, it contains a spigot module 
and a common java module, with more modules planned for the future.\
**Contributions are welcome!**

### Relevant Links
* [Spigot javadocs](https://cocoa-beans.apartium.net/spigot/)
* [Common javadocs](https://cocoa-beans.apartium.net/common/)
* [Our wiki](https://github.com/PoweredByApartium/cocoa-beans/wiki)

### Requirements
* Java 17
* Minecraft 1.19 (For the spigot module)

### Installation
**Maven:**
```xml
<repositories>
    <repository>
        <id>apartium-releases</id>
        <url>https://mvn.apartiumservices.com/repository/apartium-releases</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
        <groupId>net.apartium.cocoa-beans</groupId>
        <!-- You can also use 'spigot' instead to get the spigot utilities as well -->
        <artifactId>common</artifactId>
        <version>[VERSION]</version>
    </dependency>
</dependencies>
```

**Gradle:**
```groovy
repositories {
    maven {
        name 'apartium-releases'
        url 'https://mvn.apartiumservices.com/repository/apartium-releases'
    }
}

dependencies {
    // You can also use 'spigot' instead to get the spigot utilities as well
    implementation 'net.apartium.cocoa-beans:common:[VERSION]'
}
```

## Contributors
Shout out to our contributors:
* [ikfir](https://github.com/ikfir/)
* [OfirTIM](https://github.com/ofirtim/)
* [Voigon](https://github.com/liorsl/)

## Contributing
This project is at a very early stage right now, with a very minimal set of features included.
Any contribution of high quality code is welcome. 