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
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class CompilerTest {

    private val krangComponentRegistrars = listOf(
        KrangComponentRegistrar()
    )

    private val compiler = KotlinCompilation().apply {
        useIR = true
        messageOutputStream = System.out
        compilerPlugins = krangComponentRegistrars
        inheritClassPath = true
        verbose = false
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `compilation preserves original body`(arguments: List<Any?>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                class Main {
                    @Intercept
                    fun foo(){
                        
                    }
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("foo", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when class is annotated then call to any function notifies the listener`(arguments: List<Any?>) {
        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept
                
                @Intercept
                class Main {
                    fun foo(${arguments.toFunctionArguments()}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("foo", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    @Test
    fun `when main is called then listener is notified`() {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                @Intercept
                fun main(){}
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("MainKt")
        val func = clazz.methods.single { it.name == "main" && it.parameterCount == 0 }

        assertAfterInvoke("main", emptyList()) {
            func.invoke(null)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when class function is called then listener is notified`(arguments: List<Any?>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                class Main {
                    @Intercept
                    fun foo(${arguments.toFunctionArguments()}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("foo", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function without a body is called then listener is notified`(arguments: List<Any?>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                class Main {
                    @Intercept
                    fun foo(${arguments.toFunctionArguments()}) = true
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("foo", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when scope function is called then listener is notified`(arguments: List<Any?>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                class Main {
                    @Intercept
                    fun foo(${arguments.toFunctionArguments()}) = run {}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("foo", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when inner function is called then listener is notified`(arguments: List<Any?>) {

        //TODO Add support for passing arguments to inner function
        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                class Main {
                    fun foo() { 
                       @Intercept 
                       fun innerFunction(){}
                       innerFunction() 
                    }
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("innerFunction", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when extension function is called then listener is notified`(arguments: List<Any?>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                @Intercept
                fun String.foo(${arguments.toFunctionArguments()}) {}
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("MainKt")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("foo", arguments) {
            val injectReceiverArg = arguments.toMutableList().apply {
                add(0, "Test")
            }
            func.invoke(null, *injectReceiverArg.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when overridden function is called than listener is notified`(arguments: List<Any?>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                interface Testable {
                    fun test(${arguments.toFunctionArguments()})
                }

                class Main : Testable {
                    @Intercept
                    override fun test(${arguments.toFunctionArguments()}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "test" }

        assertAfterInvoke("test", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function with annotated parent is called than listener is notified`(arguments: List<Any?>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept

                interface Testable {
                    @Intercept
                    fun test(${arguments.toFunctionArguments()})
                }

                class Main : Testable {
                    override fun test(${arguments.toFunctionArguments()}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "test" }

        assertAfterInvoke("test", arguments) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function with redacted parameter is called listener is notified without redacted parameters`(
        arguments: List<Any?>
    ) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Intercept
                import com.herman.krang.runtime.annotations.Redact

                class Main {
                    @Intercept
                    fun foo(${arguments.toFunctionArguments(0)}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertAfterInvoke("foo", arguments.drop(1)) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    private fun List<Any?>.toFunctionArguments(redactAtIndex: Int? = null): String {
        return if (isNullOrEmpty()) {
            ""
        } else {
            joinToString {
                """${if (redactAtIndex == indexOf(it)) "@Redact" else ""} 
                    |arg${indexOf(it)} : ${it!!::class.qualifiedName ?: Any::class::qualifiedName}
                """.trimMargin()
            }
        }
    }

    private fun String.compile(fileName: String): KotlinCompilation.Result {
        compiler.sources = listOf(SourceFile.kotlin(fileName, this, true))
        val result = compiler.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        return result
    }

    private fun assertAfterInvoke(
        expectedFunctionName: String,
        expectedParameters: List<Any?>,
        invoke: () -> Unit
    ) {
        val listener = AssertedListener()
        Krang.addListener(listener)
        invoke()
        Krang.removeListener(listener)

        assertAll(
            Executable {
                assertEquals(
                    expectedFunctionName,
                    listener.capturedFunctionName,
                    "Function name doest not match - expected: $expectedFunctionName received " +
                        "${listener.capturedFunctionName}"
                )
                if (expectedParameters.isNotEmpty()) {
                    assertTrue(
                        expectedParameters.containsAll(listener.capturedParameters),
                        "Function arguments not matching - expected: ${expectedParameters.joinToString()} " +
                            "received: ${listener.capturedParameters.joinToString()}"
                    )
                }
            }
        )
    }

    private class AssertedListener : FunctionCallListener {

        var capturedFunctionName: String? = null
        var capturedParameters: MutableList<Any?> = mutableListOf()

        override fun onFunctionCalled(functionName: String, vararg parameters: Any?) {
            capturedFunctionName = functionName
            capturedParameters.addAll(parameters)
        }
    }
}