import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.konan.properties.hasProperty
import org.jetbrains.kotlin.konan.properties.loadProperties

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
    id("com.github.gmazzo.buildconfig")
}

val artifactConfig: java.util.Properties by lazy {
    loadProperties(rootDir.resolve("artifact.properties").path)
}

buildConfig {
    packageName("com.herman.krang")
    useKotlinOutput()

    artifactConfig
        .stringPropertyNames()
        .stream()
        .filter {
            artifactConfig.hasProperty(it)
        }
        .forEach {
            buildConfigField("String", it, "\"${artifactConfig.getProperty(it)}\"")
        }
}