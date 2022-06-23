plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    `kotlin-common-conventions`
    com.gradle.`plugin-publish`
    `maven-publish`
    `krang-build-config`
}

dependencies {
    implementation(prodLibs.kotlin.gradle.api)
}

pluginBundle {
    website = "https://github.com/milis92/Krang"
    vcsUrl = "https://github.com/milis92/Krang.git"
    tags = listOf("kotlin", "krang", "function-interceptor", "debugLog")
}

gradlePlugin {
    plugins {
        create("krang") {
            id = "com.github.milis92.krang"
            displayName = "Kotlin function logging interceptor"
            description = "Kotlin Compiler Plugin which adds logging interceptors to the functions"
            implementationClass = "com.herman.krang.KrangGradlePlugin"
        }
    }
}

tasks.named("publish") {
    dependsOn("publishPlugins")
}