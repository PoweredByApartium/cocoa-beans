# 📜 Minecraft Version

There are some cases when we want to know the version of the server because we make our plugin for multiple versions
Cocoabeans provide us an easier way to do it.

We could just call `ServerUtils#getVersion`
```java
MinecraftVersion myServerVersion = ServerUtils.getVersion();
```

Then we can check if is higher than 1.12 for example
```java
MinecraftVersion myServerVersion = ServerUtils.getVersion();

if (myServerVersion.isHigherThanOrEqual(MinecraftVersion.V1_12_2)) {
    // Do something
}
```

We could also get the protocol version from `MinecraftVersion`
```java
int protocolVersion = ServerUtils.getVersion().protocol();
```

## Relevant Links
* [ServerUtils.java](https://cocoa-beans.apartium.net/%version%/spigot/net/apartium/cocoabeans/spigot/ServerUtils.html)
* [MinecraftVersion.java](https://cocoa-beans.apartium.net/%version%/common/net/apartium/cocoabeans/structs/MinecraftVersion.html)