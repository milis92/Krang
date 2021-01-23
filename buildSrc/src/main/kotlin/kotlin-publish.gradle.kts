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
import org.gradle.api.publish.maven.MavenPom

plugins {
    id("org.jetbrains.dokka")
    id("signing")
    id("maven-publish")
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

//Credentials
val repositoryUsername: String = getProperty("sonatypeUsername", "")
val repositoryPassword: String = getProperty("sonatypePassword", "")

//POM
val projectName = getProperty("NAME", project.name)
val projectDescription = getProperty("DESCRIPTION", "")
//SCM
val projectScm = getProperty("SCM", "")
val projectScmConnection = getProperty("SCM_CONNECTION", "")
//Licence
val projectLicenceName: String = getProperty("LICENCE_NAME", "")
val projectLicenceUrl: String = getProperty("LICENCE_URL", "")
//Developer
val projectDeveloperName: String = getProperty("DEVELOPER", "")
val projectDeveloperUrl: String = getProperty("DEVELOPER_URL", "")

fun configure(pom: MavenPom) = with(pom) {

    name.set(projectName)
    description.set(projectDescription)
    url.set(projectScm)

    scm {
        url.set(projectScm)
        connection.set(projectScmConnection)
        developerConnection.set(projectDeveloperUrl)
    }

    licenses {
        license {
            name.set(projectLicenceName)
            url.set(projectLicenceUrl)
        }
    }

    developers {
        developer {
            name.set(projectDeveloperName)
            url.set(projectDeveloperUrl)
        }
    }
}

afterEvaluate {

    val dokkaJar by tasks.creating(Jar::class) {
        group = DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        from(tasks["dokkaHtml"])
    }

    publishing {
        publications.withType(MavenPublication::class.java).configureEach {

            artifact(dokkaJar)

            configure(this.pom)
        }
    }

    signing {
        setRequired(provider { gradle.taskGraph.hasTask("publish") })
        sign(publishing.publications)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> getProperty(key: String, defaultValue: T): T =
    if (hasProperty(key)) property(key) as T
    else defaultValue