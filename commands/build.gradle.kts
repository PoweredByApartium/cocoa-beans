group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project.project(":common"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
