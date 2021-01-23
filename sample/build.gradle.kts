plugins {
    kotlin("multiplatform") version "1.4.20"
    id("com.github.milis92.krang") version "2.0.0"
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
