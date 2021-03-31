plugins {
    kotlin("multiplatform")
    id("com.github.milis92.krang") version "2.0.3-SNAPSHOT"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
}

krang {
    enabled = true
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
                kotlinOptions.useIR = true
            }
        }
    }
}