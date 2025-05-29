plugins {
    id("java-test-fixtures")
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.project.version

dependencies {
    api(project.project(":common"))
    testImplementation(platform("org.junit:junit-bom:${project.findProperty("versions.junit.bom")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:${project.findProperty("versions.jackson.annotations")}")

    testFixturesCompileOnly(platform(libs.junit.bom))
    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter-api")
    testFixturesCompileOnly(libs.jetbrains.annotations)
}
