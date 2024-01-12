@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("multiplatform") version "1.9.21"
    id("com.android.application") version "8.1.3"
    id("com.github.milis92.krang")
}

group = "com.herman.sample"
version = "1.0"

krang {
    enabled.set(true)
    godMode.set(true)
}

android {
    defaultConfig {
        applicationId = "com.herman.sample.android"
        namespace = "com.herman.sample.android"

        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

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

kotlin {
    androidTarget {
    }
    jvm {
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
