@file:Suppress("UnstableApiUsage")

/*
* Copyright (C) 2020 Ivan Milisavljevic
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

plugins {
    kotlin("jvm")
    `kotlin-kapt`
    `kotlin-publish`
}

dependencies {
    implementation(projects.krangRuntime)
    compileOnly(prodLibs.kotlin.compilerEmbedable)
    kapt(prodLibs.google.autoService.core)
    compileOnly(prodLibs.google.autoService.annotations)

    testImplementation(platform(testLibs.junit.bom))
    testImplementation(testLibs.junit.jupiter)
    testImplementation(testLibs.kotlin.junit)
    testImplementation(testLibs.kotlin.compilerEmbedable)
    testImplementation(testLibs.compileTesting)
    testImplementation(projects.krangRuntime)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}
