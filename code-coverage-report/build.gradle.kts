plugins {
    id("jacoco-report-aggregation")
}

dependencies {
    project.rootProject.subprojects.forEach { project ->
        if (project.name != "code-coverage-report") {
            add("jacocoAggregation", project)
        }
    }
}

reporting {
    reports {
        val unifiedCoverageReport by creating(JacocoCoverageReport::class) {
            testSuiteName = "unit-test"

        }
    }
}

tasks.check {
    dependsOn(tasks.named<JacocoReport>("unifiedCoverageReport"))
}