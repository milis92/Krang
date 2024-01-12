@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-multiplatform`
    `kgc-module`
}

kotlin {
    jvm()
    js(IR) {
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap = true
                metaInfo = true
            }
        }
        nodejs()
        browser()
    }
}
