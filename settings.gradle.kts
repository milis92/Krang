rootProject.name = "krang"

plugins {
    id("com.gradle.enterprise") version "3.5"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

include(":krang-gradle-plugin")
include(":krang-compiler-plugin")
include(":krang-compiler-plugin-native")
include(":krang-runtime")
