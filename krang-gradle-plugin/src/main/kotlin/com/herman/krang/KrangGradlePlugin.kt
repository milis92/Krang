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

    companion object {
        // Runtime artefact dependency
        private const val RUNTIME_ARTIFACT = "$PROJECT_GROUP:krang-runtime:$PROJECT_VERSION"

        private const val COMPILER_PLUGIN_ARTIFACT_ID = "krang-compiler-plugin"

        // Compiler plugin artefact
        private val compilerPluginArtifact = SubpluginArtifact(
            groupId = PROJECT_GROUP,
            artifactId = COMPILER_PLUGIN_ARTIFACT_ID,
            version = PROJECT_VERSION
        )
    }

    override fun apply(
        target: Project
    ): Unit = with(target) {
        extensions.create(KrangGradleExtension.extensionName, KrangGradleExtension::class.java)
    }

    // Is applicable has to return true because we always have to attach the runtime dependency.
    override fun isApplicable(
        kotlinCompilation: KotlinCompilation<*>
    ): Boolean = true

    override fun getCompilerPluginId(): String = compilerPluginArtifact.artifactId

    override fun getPluginArtifact(): SubpluginArtifact = compilerPluginArtifact

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(KrangGradleExtension::class.java)

        // Add runtime dependency to kotlin source
        kotlinCompilation.defaultSourceSet.dependencies {
            implementation(RUNTIME_ARTIFACT)
        }

        // Run the optional variant filter to potentially change the config based on the compilation
        extension.variantFilter?.execute(kotlinCompilation)

        return project.provider {
            listOf(
                SubpluginOption(key = "enabled", value = extension.enabled.get().toString()),
                SubpluginOption(key = "godMode", value = extension.godMode.get().toString()),
            )
        }
    }
}
