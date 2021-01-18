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

import org.gradle.api.plugins.JavaBasePlugin.DOCUMENTATION_GROUP

plugins {
    id("org.jetbrains.dokka")
    id("signing")
    id("maven-publish")
}

val github = "https://github.com/milis92/Krang"
val description = ""
val projectName = "DebugLog"
val projectDescription = "DebugLog"

val dokkaJar by tasks.creating(Jar::class) {
    group = DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks["dokkaHtml"])
}

signing {
    setRequired(provider { gradle.taskGraph.hasTask("publish") })
    sign(publishing.publications)
}

publishing {
    publications.configureEach {
        if (this !is MavenPublication) return@configureEach

        artifact(dokkaJar)

        pom {
            name.set(project.name)
            description.set(description)
            url.set(github)

            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("$github/blob/main/LICENSE.txt")
                }
            }
            scm {
                url.set(github)
                connection.set("$github.git")
            }
            developers {
                developer {
                    name.set("Ivan Milisavljevic")
                    url.set("https://github.com/milis92")
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {

                val url = when {
                    "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                    else -> property("sonatypeReleaseUrl")
                } as String

                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
    }
}