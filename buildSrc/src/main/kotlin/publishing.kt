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

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create

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
const val github = "https://github.com/milis92/Krang"

fun PublishingExtension.withDefaults(project: Project, description: String) {
    publications {
        create<MavenPublication>("default") {

            from(project.components.getByName("java"))
            artifact(project.tasks.getByName("sourcesJar"))
            artifact(project.tasks.getByName("dokkaJar"))

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
                    connection.set("$$github.git")
                }
                developers {
                    developer {
                        name.set("Ivan Milisavljevic")
                        url.set("https://github.com/milis92")
                    }
                }
            }
        }
    }

    repositories {
        if (
            project.hasProperty("sonatypeUsername") &&
            project.hasProperty("sonatypePassword") &&
            project.hasProperty("sonatypeSnapshotUrl") &&
            project.hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url = when {
                    "SNAPSHOT" in project.version.toString() -> project.property("sonatypeSnapshotUrl")
                    else -> project.property("sonatypeReleaseUrl")
                } as String
                setUrl(url)
                credentials {
                    username = project.property("sonatypeUsername") as String
                    password = project.property("sonatypePassword") as String
                }
            }
        }
        maven {
            name = "test"
            setUrl("file://${project.rootProject.buildDir}/localMaven")
        }
    }
}
