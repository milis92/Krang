
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
    kotlin("multiplatform")
    `kotlin-publish`
}

kotlin {

    jvm {
        compilations.all {
            kotlinOptions {
                kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    linuxX64()
    mingwX64()
    iosArm32()
    iosArm64()
    iosX64()
    macosX64()
    tvosArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()
}