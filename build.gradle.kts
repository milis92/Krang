plugins {
    `kgc-root`
}

allprojects {
    group = "com.github.milis92.krang"
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

tasks.register("versionToFile") {
    doLast {
        file("version.txt").writeText("v$version")
    }
}
