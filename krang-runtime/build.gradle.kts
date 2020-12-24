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
    id("org.jetbrains.dokka")
    id("signing")
    id("maven-publish")
}

tasks.register("sourcesJar", Jar::class) {
    group = "build"
    description = "Assembles Kotlin sources"

    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    dependsOn(tasks.classes)
}

tasks.register("dokkaJar", Jar::class) {
    group = "documentation"

    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
    dependsOn(tasks.dokkaJavadoc)
}

signing {
    setRequired(provider { gradle.taskGraph.hasTask("publish") })
    sign(publishing.publications)
}

publishing {
    withDefaults(project, "Krang runtime")
}