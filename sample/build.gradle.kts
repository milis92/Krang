plugins {
    kotlin("multiplatform") version "1.4.20"
    id("com.github.milis92.krang") version "1.0.3-SNAPSHOT"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
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
