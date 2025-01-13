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

import com.herman.krang.runtime.FunctionCallListener
import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.TracingContext
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.descriptors.runtime.components.tryLoadClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.test.fail

@OptIn(ExperimentalCompilerApi::class)
class KrangCompilerTestExtension(
    private val cleanUp: Boolean = true
) : BeforeAllCallback, AfterAllCallback {

    private val compilerOutputDir = Path.of("build")
    private lateinit var compilerWorkDirectory: Path

    private lateinit var compiler: KotlinCompilation

    override fun beforeAll(context: ExtensionContext?) {
        compilerWorkDirectory = Files.createTempDirectory(
            compilerOutputDir,
            context?.displayName ?: "Kotlin-Compilation"
        )
        compiler = KotlinCompilation().apply {
            messageOutputStream = System.out
            compilerPluginRegistrars = listOf(KrangCompilerPluginRegistrar())
            commandLineProcessors = listOf(KrangCommandLineProcessor())
            inheritClassPath = true
            verbose = false
            workingDir = compilerWorkDirectory.toFile()
        }
    }

    @OptIn(ExperimentalPathApi::class)
    override fun afterAll(context: ExtensionContext?) {
        if (cleanUp) {
            compilerWorkDirectory.deleteRecursively()
        }
    }

    fun compileAndInvoke(
        sources: List<SourceFile>,
        invoke: (JvmCompilationResult) -> Unit = { compilationResult ->
            compilationResult.invokeMainFunction("MainKt")
        }
    ): MutableList<KrangFunctionCall> {
        compiler.sources = sources

        val compilationResult = compiler.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)

        val krangTestListener = KrangTestListener()
        Krang.addListener(krangTestListener)

        invoke(compilationResult)

        return krangTestListener.capturedFunctionCalls
    }

    private fun JvmCompilationResult.invokeMainFunction(classId: String) {
        val mainMethod = classLoader.tryLoadClass(classId)?.methods?.firstOrNull {
            it.name == "main" && it.parameterCount == 0
        } ?: fail("function main() not found")

        mainMethod.invoke(null)
    }
}

data class KrangFunctionCall(
    val functionName: String,
    val functionParameters: List<Any?>,
    val tracingContext: TracingContext
)

private class KrangTestListener : FunctionCallListener {
    val capturedFunctionCalls = mutableListOf<KrangFunctionCall>()
    override fun onFunctionCalled(name: String , parameters: Array<out Any?>, tracingContext: TracingContext) {
        capturedFunctionCalls.add(KrangFunctionCall(name, parameters.toList(), tracingContext))
    }
}