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

package com.herman.krang

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class KrangGradleExtension @Inject constructor(project: Project) : Named {

    companion object {
        const val extensionName = "krang"
    }

    override fun getName(): String = extensionName

    private val objects = project.objects

    // Enable or disable krang at Compile time - true by default
    val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    // Enable krang for entier codebase - false by default
    val godMode: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
}
