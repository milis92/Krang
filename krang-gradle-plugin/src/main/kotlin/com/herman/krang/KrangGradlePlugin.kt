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

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused")
class KrangGradlePlugin : KotlinCompilerPluginSupportPlugin {

    private val runtimeDependency = "${BuildConfig.PLUGIN_GROUP_ID}:krang-runtime:${BuildConfig.PLUGIN_VERSION}"

    override fun apply(target: Project): Unit = with(target) {
        extensions.create(KrangGradleExtension.extensionName, KrangGradleExtension::class.java)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getCompilerPluginId(): String = "${BuildConfig.PLUGIN_GROUP_ID}.${BuildConfig.PLUGIN_ARTIFACT_ID}"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = BuildConfig.PLUGIN_GROUP_ID,
        artifactId = BuildConfig.PLUGIN_ARTIFACT_ID,
        version = BuildConfig.PLUGIN_VERSION
    )

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        //Add runtime dependency to kotlin source
        kotlinCompilation.kotlinSourceSets.forEach {
            it.dependencies {
                implementation(runtimeDependency)
            }
        }
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(KrangGradleExtension::class.java)
        return project.provider {
            listOf(
                SubpluginOption(key = "enabled", value = extension.enabled.toString()),
                SubpluginOption(key = "godMode", value = extension.godMode.toString()),
            )
        }
    }
}
