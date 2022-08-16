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

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@Suppress("unused")
@AutoService(CommandLineProcessor::class)
class KrangCommandLineProcessor : CommandLineProcessor {

    companion object {
        private const val OPTION_ENABLED = "enabled"

        private const val OPTION_GOD_MODE = "godMode"

        val ARG_ENABLED = CompilerConfigurationKey<Boolean>(OPTION_ENABLED)
        val ARG_GOD_MODE = CompilerConfigurationKey<Boolean>(OPTION_GOD_MODE)
    }

    override val pluginId: String = BuildConfig.PLUGIN_ARTIFACT_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = OPTION_ENABLED,
            valueDescription = "bool <true | false>",
            description = "If the Krang should be applied",
            required = false,
        ),
        CliOption(
            optionName = OPTION_GOD_MODE,
            valueDescription = "bool <true | false>",
            description = "If the Krang should be applied to entire codebase",
            required = false,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        return when (option.optionName) {
            OPTION_ENABLED -> configuration.put(ARG_ENABLED, value.toBoolean())
            OPTION_GOD_MODE -> configuration.put(ARG_GOD_MODE, value.toBoolean())
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}
