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

import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.Tracer
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import kotlin.test.assertTrue

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
    fun `compilation preserves original body`(arguments: List<Any>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                    @Trace
                    fun foo(){
                        
                    }
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertTracerAfterInvoke("foo") {
            func.invoke(clazz.newInstance())
        }
    }

    @Test
    fun `when main is called then tracer is called`() {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                @Trace
                fun main(){}
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("MainKt")
        val func = clazz.methods.single { it.name == "main" && it.parameterCount == 0 }

        assertTracerAfterInvoke("main") {
            func.invoke(null)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when class function is called then tracer is called`(arguments: List<Any>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                    @Trace
                    fun foo(${arguments.toFunctionArguments()}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")

        val func = clazz.methods.single { it.name == "foo" }

        assertTracerAfterInvoke("foo") {
            func.invoke(clazz.newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function without a body is called then tracer is called`(arguments: List<Any>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                    @Trace
                    fun foo(${arguments.toFunctionArguments()}) = true
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertTracerAfterInvoke("foo") {
            func.invoke(clazz.newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when scope function is called then tracer is called`(arguments: List<Any>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                    @Trace
                    fun foo(${arguments.toFunctionArguments()}) = run {}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertTracerAfterInvoke("foo") {
            func.invoke(clazz.newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when inner function is called then tracer is called`(arguments: List<Any>) {

        //TODO Add support for passing arguments to inner function
        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                    fun foo() { 
                       @Trace 
                       fun innerFunction(){}
                       innerFunction() 
                    }
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertTracerAfterInvoke("innerFunction") {
            func.invoke(clazz.newInstance())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when extension function is called then tracer is called`(arguments: List<Any>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                @Trace
                fun String.foo(${arguments.toFunctionArguments()}) {}
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("MainKt")
        val func = clazz.methods.single { it.name == "foo" }

        assertTracerAfterInvoke("foo") {
            val injectReceiverArg = arguments.toMutableList().apply {
                add(0, "Test")
            }
            func.invoke(null, *injectReceiverArg.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when overridden function is called than tracer is called`(arguments: List<Any>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                interface Testable {
                    fun test(${arguments.toFunctionArguments()})
                }

                class Main : Testable {
                    @Trace
                    override fun test(${arguments.toFunctionArguments()}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "test" }

        assertTracerAfterInvoke("test") {
            func.invoke(clazz.newInstance(), *arguments.toTypedArray())
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function with annotated parent is called than tracer is called`(arguments: List<Any>) {

        @Language("kotlin") val source =
            """
                import com.herman.krang.runtime.annotations.Trace

                interface Testable {
                    @Trace
                    fun test(${arguments.toFunctionArguments()})
                }

                class Main : Testable {
                    override fun test(${arguments.toFunctionArguments()}){}
                }
                """

        val clazz = source.compile("Main.kt")
            .classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "test" }

        assertTracerAfterInvoke("test") {
            func.invoke(clazz.newInstance(), *arguments.toTypedArray())
        }
    }

    private fun List<Any>.toFunctionArguments(): String {
        return if (isNullOrEmpty()) {
            ""
        } else {
            joinToString {
                "arg${indexOf(it)} : ${it::class.qualifiedName ?: Any::class::qualifiedName}"
            }
        }
    }

    private fun String.compile(fileName: String): KotlinCompilation.Result {
        compiler.sources = listOf(SourceFile.kotlin(fileName, this, true))
        val result = compiler.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        return result
    }

    private fun assertTracerAfterInvoke(expectedLogStatement: String, invoke: () -> Unit) {
        val tracer = AssertedTracer(expectedLogStatement)
        Krang.addTracer(tracer)
        invoke()
        Krang.removeTracer(tracer)
        assertAll(
            Executable {
                assertTrue(tracer.functionEnterCalled, "Function enter not called")
                assertTrue(tracer.functionExitCalled, "Function exit not called")
            }
        )
    }

    private class AssertedTracer(
        private val expectedLogStatement: String
    ) : Tracer {

        var functionEnterCalled = false
        var functionExitCalled = false

        override fun onFunctionEnter(functionSignature: String) {
            functionEnterCalled = true
        }

        override fun onFunctionExit(functionSignature: String) {
            functionExitCalled = true
        }
    }
}