plugins {
    `kgc-multiplatform-module`
    id("com.android.library")
}

kotlin {
    kotlin {
        androidTarget {
            publishLibraryVariants("release")
        }
    }
}

android {
    namespace = "com.github.milis92.krang.runtime"
    compileSdk = 34
}
