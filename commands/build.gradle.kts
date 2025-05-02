plugins {
    id("java-test-fixtures")
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.project.version

dependencies {
    api(project(":common"))

    testImplementation("org.junit.jupiter:junit-jupiter")

    testFixturesCompileOnly(platform(libs.junit.bom))
    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter-api")
    testFixturesCompileOnly(libs.jetbrains.annotations)

}
