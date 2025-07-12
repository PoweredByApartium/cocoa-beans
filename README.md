![main](https://github.com/poweredbyapartium/cocoa-beans/actions/workflows/main.yml/badge.svg)
![release](https://github.com/poweredbyapartium/cocoa-beans/actions/workflows/release.yml/badge.svg)
![GitHub](https://img.shields.io/github/license/PoweredByApartium/cocoa-beans)

## Welcome to Cocoa beans!
This library aims to be a very well-made and thought-out toolkit for developers to save the hassle
of copying common code between projects. Cocoa beans is modular, it contains a spigot module 
and a common java module and commands java module and commands spigot module inside of it, with more modules planned for the future.\
**Contributions are welcome!**

### Relevant Links
* [🚰 Spigot javadocs](https://cocoa-beans.apartium.net/%version%/spigot/)
* [📄 Common javadocs](https://cocoa-beans.apartium.net/%version%/common/)
* [🖥️ Command javadocs](https://cocoa-beans.apartium.net/%version%/commands/)
* [🤖 Command Spigot javadocs](https://cocoa-beans.apartium.net/%version%/commands-spigot/)
* [📜 Our wiki](https://cocoa-beans.apartium.net/)
<br/>

>[!IMPORTANT]
> ### Requirements
> * Java 17
> * Minecraft 1.8 and above (For the spigot modules)

### Installation
>[!NOTE]
> Cocoabean could be included in your jar file, but if you are making a Spigot plugin you should consider downloading the library as a plugin from Hangar.

![Latest release](https://github.com/PoweredByApartium/cocoa-beans/releases/latest)

**Maven:**
```xml
<dependencies>
    <dependency>
        <groupId>dev.apartium.cocoa-beans</groupId>
        <!-- You can also use 'spigot' instead to get the spigot utilities as well -->
        <artifactId>common</artifactId>
        <version>[VERSION]</version>
    </dependency>

    <!-- For the command system itself -->
    <dependency>
        <groupId>dev.apartium.cocoa-beans</groupId>
        <!-- You can also use 'commands-spigot' instead to get the spigot utilities as well -->
        <artifactId>commands</artifactId>
        <version>[VERSION]</version>
    </dependency>
</dependencies>
```

**Gradle:**
```groovy
dependencies {
    // You can also use 'spigot' instead to get the spigot utilities as well
    implementation('dev.apartium.cocoa-beans:common:[VERSION]')

    // For the command system (You also could use 'commands-spigot' instead to get the spigot command utilities as well)
    implementation('dev.apartium.cocoa-beans:commands:[VERSION]')
}
```

## Contributing
This project is at a very early stage right now, with a very minimal set of features included.
Any contribution of high quality code is welcome. 

[Read more](https://github.com/PoweredByApartium/cocoa-beans/blob/main/CONTRIBUTING.md)

## Star History

<a href="https://star-history.com/#PoweredByApartium/cocoa-beans&Date">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=PoweredByApartium/cocoa-beans&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=PoweredByApartium/cocoa-beans&type=Date" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=PoweredByApartium/cocoa-beans&type=Date" />
 </picture>
</a>
