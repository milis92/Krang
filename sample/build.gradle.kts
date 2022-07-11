buildscript {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        classpath("com.github.milis92.krang:com.github.milis92.krang-gradle-plugin:2.4.0-SNAPSHOT")
    }
}

plugins {
    id("com.android.application") version "7.0.3" apply false
    id("com.android.library") version "7.0.3" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
}

group = "com.herman.sample"
version = "1.0"

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}