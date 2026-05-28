plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

dependencies {

    testImplementation(libs.jackson.databind)
    testImplementation(libs.commons.codec)
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    // ClassLoaderUtils reflects into URLClassLoader#addURL; the static initializer fails
    // without this open on Java 9+ (documented on the class itself).
    jvmArgs("--add-opens", "java.base/java.net=ALL-UNNAMED")
}
