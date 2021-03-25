plugins {
    id("com.android.library")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(15)
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
}