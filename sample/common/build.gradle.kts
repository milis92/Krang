plugins {
    kotlin("multiplatform")
    id("com.github.milis92.krang") version "2.0.4-SNAPSHOT"
}

krang {
    enabled = true
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting

        val jvmMain by getting {
            dependencies {
                dependsOn(commonMain)
            }
        }
    }
}