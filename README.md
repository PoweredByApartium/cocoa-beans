![GitHub](https://img.shields.io/github/license/PoweredByApartium/cocoa-beans)

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
Shout out to our contributors:<br>
<a href="https://github.com/PoweredByApartium/cocoa-beans/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=PoweredByApartium/cocoa-beans" />
</a>

| contributor | Profile     | 
| :-------- | :------- |
| `Liorsl` | https://github.com/liorsl |
| `Ikfir` | https://github.com/ikfir |
| `OfirTim` | https://github.com/ofirtim|

## Contributing
This project is at a very early stage right now, with a very minimal set of features included.
Any contribution of high quality code is welcome. 
