plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

dependencies {
    api(project(":cocoa-beans-common"))
    api(project(":cocoa-beans-state"))
    api(project(":cocoa-beans-state-animation"))



    compileOnly(libs.adventure)
    testImplementation(libs.adventure)

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
