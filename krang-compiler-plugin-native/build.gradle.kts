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
    compileOnly(projects.krangRuntime)
    compileOnly(prodLibs.kotlin.compiler)
    kapt(prodLibs.google.autoService.core)
    compileOnly(prodLibs.google.autoService.annotations)
}

tasks.named("compileKotlin") { dependsOn("syncSource") }

//Copies entire source from krang-compiler-plugin to native
tasks.register<Sync>("syncSource") {
    from(project(":krang-compiler-plugin").sourceSets.main.get().allSource)
    into("src/main/kotlin")
    filter {
        when (it) {
            "import org.jetbrains.kotlin.com.intellij.mock.MockProject" -> "import com.intellij.mock.MockProject"
            else -> it
        }
    }
    exclude { it.file.name == "BuildConfig.kt" }
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}
