plugins {
    id("java-test-fixtures")
    id("apartium-maven-publish")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project.project(":common"))
    testImplementation(platform("org.junit:junit-bom:${project.findProperty("versions.junit.bom")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")
    testFixturesCompileOnly("org.jetbrains:annotations:${findProperty("versions.jetbrains.annotations")}")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:${project.findProperty("versions.jackson.annotations")}")

}
