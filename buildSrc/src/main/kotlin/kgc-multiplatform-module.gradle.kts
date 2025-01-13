import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("kgc-kotlin-multiplatform")
    id("kgc-kotlin")
    id("kgc-buildconfig")
    id("kgc-detekt")
    id("kgc-dokka")
    id("kgc-testing")
    id("kgc-publishing")
}

tasks.withType<Detekt> {
    tasks.getByName("check").dependsOn(this)
}