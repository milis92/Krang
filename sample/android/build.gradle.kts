plugins {
    id("com.android.application")
    kotlin("android")
    id("com.github.milis92.krang") version "2.0.3-SNAPSHOT"
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.herman.android"
        minSdkVersion(15)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    lintOptions {
        isQuiet = true
        isAbortOnError = false
        isIgnoreWarnings = true
        isCheckReleaseBuilds = false
    }
}

krang {
    enabled = true
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
    }
}