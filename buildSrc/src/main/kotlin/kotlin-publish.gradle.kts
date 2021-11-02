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

val dokkaJar = tasks.create<Jar>("dokkaJar") {
    group = DOCUMENTATION_GROUP
    description = "Assembles Javadoc jar from Dokka API docs"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
    dokkaSourceSets {
        configureEach {
            sourceRoot(file("src"))
        }
    }
}

fun configure(pom: MavenPom) = with(pom) {

    name.set("NAME".byProperty)
    description.set("DESCRIPTION".byProperty)
    url.set("SCM".byProperty)

    scm {
        url.set("SCM".byProperty)
        connection.set("SCM_CONNECTION".byProperty)
        developerConnection.set("DEVELOPER_URL".byProperty)
    }

    issueManagement {
        system.set("ISSUE_MANAGEMENT_SYSTEM".byProperty)
        url.set("ISSUE_MANAGEMENT_URL".byProperty)
    }

    licenses {
        license {
            name.set("LICENCE_NAME".byProperty)
            url.set("LICENCE_URL".byProperty)
        }
    }

    developers {
        developer {
            name.set("DEVELOPER".byProperty)
            url.set("DEVELOPER_URL".byProperty)
        }
    }
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication> {
            artifact(dokkaJar)
            configure(this.pom)
        }

        repositories {
            maven {
                setUrl(
                    when {
                        isReleaseBuild -> releaseRepositoryUrl
                        else -> snapshotRepositoryUrl
                    }
                )
                credentials {
                    username = "REPOSITORY_USERNAME".byProperty
                    password = "REPOSITORY_PASSWORD".byProperty
                }
            }
        }

        signing {
            val signingKey = "SIGNING_KEY".byProperty
            val signingPwd = "SIGNING_PASSWORD".byProperty

            setRequired(provider { gradle.taskGraph.hasTask("publish") })
            useInMemoryPgpKeys(signingKey, signingPwd)
            sign(publishing.publications)
        }
    }
}

val isReleaseBuild: Boolean = "SNAPSHOT" !in version.toString()

//Repositories
val releaseRepositoryUrl: String = getProperty(
    "sonatypeReleaseUrl",
    "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
)
val snapshotRepositoryUrl: String = getProperty(
    "sonatypeSnapshotUrl",
    "https://oss.sonatype.org/content/repositories/snapshots/"
)

val String.byProperty: String get() = getProperty(this, "")

@Suppress("UNCHECKED_CAST")
fun getProperty(key: String, defaultValue: String): String {
    val property = providers.gradleProperty(key).forUseAtConfigurationTime()
    return property.getOrElse(defaultValue)
}