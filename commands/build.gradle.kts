group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project.project(":common"))
    testImplementation(platform("org.junit:junit-bom:${project.findProperty("junit.bom.version")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}