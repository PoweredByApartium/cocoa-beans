## 👥 Contributing to Apartium Cocoa Beans
To keep it simple, there are just a few simple 📜 rules for contributing:
1. 👨‍💻 Each class, method and package must have a clear and useful documentation
2. 🧪 Modules not dependent on Spigot most have as close to a 100% unit test coverage
3. 🔬 Each unrelated change most have its own PR

## 🔒 Dependency verification

`gradle/verification-metadata.xml` holds a SHA-256 checksum for every dependency and Gradle
plugin artifact the build resolves. Gradle checks each download against it, so a tampered or
swapped artifact fails the build instead of silently ending up on the classpath.

If you add, remove or bump a dependency, the build will fail with
`Dependency verification failed` until the file is regenerated:

```shell
./gradlew --write-verification-metadata sha256 --refresh-dependencies build shadowJar javadoc
```

Commit the resulting diff together with your dependency change, and sanity-check it: the only
new checksums should belong to the artifacts you actually intended to pull in.

`-SNAPSHOT` dependencies are absent from the file by design: Gradle does not verify changing
modules.
