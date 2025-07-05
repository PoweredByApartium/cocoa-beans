plugins {
    id("java-test-fixtures")
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.project.version

dependencies {
    api(project.project(":common"))

    testImplementation(platform("org.junit:junit-bom:${libs.junit.bom.get().version}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.jackson.databind)


    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter-api:${libs.junit.bom.get().version}")
    testFixturesCompileOnly(libs.jetbrains.annotations)
}
