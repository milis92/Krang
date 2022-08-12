plugins {
    kotlin("jvm")
    id("com.github.milis92.krang")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

krang {
    enabled.set(true)
}