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

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import javax.inject.Inject

abstract class KrangGradleExtension @Inject constructor(
    objects: ObjectFactory
) : Named {

    companion object {
        const val extensionName = "krang"
    }

    @Internal
    override fun getName(): String = extensionName

    // Enable or disable krang at Compile time - true by default
    val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    // Enable krang for entire codebase - false by default
    val godMode: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    // Allow krang to support different configurations based on the KotlinCompilation
    internal var variantFilter: Action<KotlinCompilation<*>>? = null

    @Suppress("unused")
    fun variantFilter(action: Action<KotlinCompilation<*>>) {
        variantFilter = action
    }
}