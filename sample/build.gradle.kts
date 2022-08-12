plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("com.github.milis92.krang") version "2.5.0-SNAPSHOT"
}

group = "com.herman.sample"
version = "1.0"

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}