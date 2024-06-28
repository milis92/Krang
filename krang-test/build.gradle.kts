plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    id("com.github.milis92.krang")
}

group = "com.herman.krang.test"
version = "1.0"

krang {
    enabled.set(true)
    godMode.set(false)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm()

    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }
        compilations.matching { it.name == "main" || it.name == "test" }.configureEach {
            kotlinOptions {
                sourceMap = true
                moduleKind = "umd"
            }
        }
    }

//    wasmJs {
//        nodejs()
//    }
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // Tier 2
    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // Tier 3
    mingwX64()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    watchosDeviceArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    defaultConfig {
        namespace = "com.herman.krang.test.android"

        minSdk = 15
        compileSdk = 31
    }
    buildTypes {
        debug {
            isDefault = true
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
        }
    }
}
