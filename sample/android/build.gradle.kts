@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.github.milis92.krang")
}

android {
    defaultConfig {
        applicationId = "com.herman.android"

        minSdk = 15
        targetSdk = 29
        compileSdk = 31

        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

krang {
    enabled.set(true)
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
}