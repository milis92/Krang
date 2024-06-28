plugins {
    `java-gradle-plugin`
    `kgc-jvm-module`
}

gradlePlugin {
    plugins {
        create("krang") {
            id = "com.github.milis92.krang"
            displayName = "Kotlin function interceptor"
            description = "Kotlin Compiler Plugin which adds logging interceptors to the functions"
            implementationClass = "com.herman.krang.KrangGradlePlugin"
        }
    }
}

buildConfig {
    packageName("com.herman.krang")
}

dependencies {
    implementation(libs.kotlin.gradle.api)
}
