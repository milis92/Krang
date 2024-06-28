import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("io.gitlab.arturbosch.detekt")
}

tasks.withType<Detekt> {
    parallel = true
    // Define the detekt configuration you want to use.
    config.setFrom(files(rootDir.resolve("config/detekt/detekt.yml")))
    // Applies the config files on top of detekt's default config file.
    buildUponDefaultConfig = false
    // Turns on all the rules.
    allRules = false
    // Enable autocorrection for the rules that support it
    autoCorrect = true
    // Configure base path so detekt can properly format reports
    basePath = rootDir.absolutePath
    // Outputs dir
    reportsDir.set(file(rootDir.resolve("build/reports/detekt/${project.name}")))
    reports {
        sarif.required = true
        xml.required = false
        html.required = false
        md.required = false
        txt.required = false
    }
}

val libs = the<LibrariesForLibs>()
dependencies {
    detektPlugins(libs.detekt.formatting)
}
