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
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    jcenter()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
    api("org.jetbrains.dokka:dokka-gradle-plugin:1.4.10.2")

    api("com.gradle.publish:plugin-publish-plugin:0.12.0")
    api("com.github.gmazzo:gradle-buildconfig-plugin:2.0.2")
}