plugins {
    `kgc-jvm-module`
    `kgc-testing`
}

dependencies {
    compileOnly(libs.kotlin.compiler.embedable)

    ksp(libs.google.autoService.core)
    implementation(libs.google.autoService.annotations)

    testImplementation(projects.krangRuntime)
    testImplementation(libs.compileTesting)
}

buildConfig {
    packageName("com.herman.krang")
}
