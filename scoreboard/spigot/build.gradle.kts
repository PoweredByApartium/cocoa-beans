plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

dependencies {
    api(project(":scoreboard"))
    api(project(":common"))
    api(project(":spigot"))

    compileOnly(libs.paper)

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation(libs.mock.bukkit)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")}
