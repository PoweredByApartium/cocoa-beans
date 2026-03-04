plugins {
    id("apartium-maven-publish")
    id("com.gradleup.shadow")
    id("java-test-fixtures")

}

group = parent!!.group
version = parent!!.version


dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.authlib)
    compileOnly(project(":cocoa-beans-commands"))
    api(project(":cocoa-beans-common"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation(libs.mock.bukkit)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testFixturesCompileOnly(platform(rootProject.libs.junit.bom))
    testFixturesCompileOnly(libs.mock.bukkit)
    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter-api")
}
