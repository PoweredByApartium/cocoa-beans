plugins {
    id("java-test-fixtures")
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.project.version

dependencies {
    api(project(":common"))
    testImplementation(platform("org.junit:junit-bom:${project.findProperty("versions.junit.bom")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")
    testFixturesCompileOnly("org.jetbrains:annotations:${findProperty("versions.jetbrains.annotations")}")

}
