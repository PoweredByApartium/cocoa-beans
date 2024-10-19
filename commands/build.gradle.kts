plugins {
    id("apartium-maven-publish")
    id("java-test-fixtures")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project.project(":common"))
    testImplementation(platform("org.junit:junit-bom:${project.findProperty("versions.junit.bom")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")
    testFixturesCompileOnly("org.jetbrains:annotations:${findProperty("versions.jetbrains.annotations")}")

}
