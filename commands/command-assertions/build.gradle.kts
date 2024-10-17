plugins {
    id("apartium-maven-publish")
}


group = parent!!.project.group
version = parent!!.version

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")

    implementation(project.project(":commands"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.findProperty("versions.junit")}")
}

tasks.test {
    useJUnitPlatform()
}