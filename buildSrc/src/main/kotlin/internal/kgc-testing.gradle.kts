import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-test-fixtures`
}

tasks.withType<Test>().configureEach {
    val halfOfAvailableProcessors = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1

    maxParallelForks = halfOfAvailableProcessors
    failFast = false

    jvmArgs(
        listOf(
            "-XX:+UseParallelGC",
            "-XX:+UseGCOverheadLimit",
            "-XX:GCTimeLimit=10",
        )
    )
    useJUnitPlatform()
    testLogging {
        showCauses = true
        showStandardStreams = true

        events(
            "passed",
            "skipped",
            "failed"
        )
        exceptionFormat = TestExceptionFormat.FULL
    }
}

val libs = the<LibrariesForLibs>()
val testImplementation: Configuration? = configurations.findByName("testImplementation")
dependencies {
    testImplementation?.let { it(platform(libs.junit)) }
    testImplementation?.let { it(libs.junit.jupiter) }

    testImplementation?.let { it(kotlin("test-junit5")) }
}
