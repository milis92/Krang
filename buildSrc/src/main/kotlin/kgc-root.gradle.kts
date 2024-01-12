import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("kgc-detekt")
}

// Registers detekt all task that runs the detekt on the entire codebase
tasks.register("detektAll", Detekt::class) {
    setSource(rootDir)

    include("**/*.kt")
    include("**/*.kts")

    exclude("**/.idea/**")
    exclude("**/build/**")
}
