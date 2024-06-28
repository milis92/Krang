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
import com.herman.krang.KrangCommandLineProcessor.Companion.ARG_ENABLED
import com.herman.krang.KrangCommandLineProcessor.Companion.ARG_GOD_MODE
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class KrangCompilerPluginRegistrar constructor(
    private val enabledByDefault: Boolean = true,
    private val godModeByDefault: Boolean = false,
    override val supportsK2: Boolean = true
) : CompilerPluginRegistrar() {

    override fun ExtensionStorage.registerExtensions(
        configuration: CompilerConfiguration
    ) {
        val enabled = configuration.get(ARG_ENABLED, enabledByDefault)
        val godMode = configuration.get(ARG_GOD_MODE, godModeByDefault)
        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        if (enabled) {
            IrGenerationExtension.registerExtension(
                KrangIrGenerationExtension(
                    godMode = godMode,
                    logger = logger
                )
            )
        }
    }
}
