plugins {
    kotlin("jvm")
    `kgc-module`
}

dependencies {
    implementation(projects.krangRuntime)
    compileOnly(libs.kotlin.compiler.embedable)

    ksp(libs.google.autoService.core)
    implementation(libs.google.autoService.annotations)
}

buildConfig {
    packageName("com.herman.krang")
}
