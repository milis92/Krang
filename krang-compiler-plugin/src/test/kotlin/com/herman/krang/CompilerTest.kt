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

import com.herman.krang.assertions.assertCompilation
import com.herman.krang.assertions.assertInvoke
import com.herman.krang.assertions.compile
import com.herman.krang.providers.FunctionArgumentsProvider
import com.tschuchort.compiletesting.KotlinCompilation
import fixtures.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import java.nio.file.Files
import java.nio.file.Path

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
        workingDir = Files.createTempDirectory(Path.of("build"), "Kotlin-Compilation").toFile()
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `compilation preserves original body`(arguments: List<Any?>) =
        simpleClassFunction(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke("Main.foo", arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @Test
    fun `when topLevel function is called then listener is notified`() =
        topLevelFunction.compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("MainKt")
            val func = clazz.methods.single { it.name == "main" && it.parameterCount == 0 }

            assertInvoke("main", emptyList()) {
                func.invoke(null)
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when class function is called then listener is notified`(arguments: List<Any?>) =
        simpleClassFunction(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke("Main.foo", arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function without a body is called then listener is notified`(arguments: List<Any?>) =
        functionWithoutBody(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke("Main.foo", arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when scope function is called then listener is notified`(arguments: List<Any?>) =
        scopeFunction(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke("Main.foo", arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when inner function is called then listener is notified`(arguments: List<Any?>) =
        innerFunction(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke("Main.foo.innerFunction", arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when extension function is called then listener is notified`(arguments: List<Any?>) =
        extensionFunction(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("MainKt")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke("foo", arguments) {
                val injectReceiverArg = arguments.toMutableList().apply {
                    add(0, "Test")
                }
                func.invoke(null, *injectReceiverArg.toTypedArray())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when overridden function is called than listener is notified`(arguments: List<Any?>) =
        overriddenFunction(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "test" }

            assertInvoke("Main.test", arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @Test
    fun `when enum function is called then listener is notified`() =
        enumFunctionWithAnnotatedParrent().compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" && it.parameterCount == 0 }

            assertInvoke("Testable.TEST1.provider", emptyList()) {
                func.invoke(clazz.getDeclaredConstructor().newInstance())
            }
        }

    @Test
    fun `when enum function with annotated parent is called then listener is notified`() =
        enumFunctionWithAnnotatedParrent().compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" && it.parameterCount == 0 }

            assertInvoke("Testable.TEST1.provider", emptyList()) {
                func.invoke(clazz.getDeclaredConstructor().newInstance())
            }
        }

    @Test
    fun `when enum without a function is called then listener is notified`() =
        enumWithoutFunction().compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" && it.parameterCount == 0 }

            assertInvoke(listOf(), emptyList()) {
                func.invoke(clazz.getDeclaredConstructor().newInstance())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function with annotated parent is called than listener is notified`(arguments: List<Any?>) =
        functionWithAnnotatedParent(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "test" }

            assertInvoke("Main.test", arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when function with redacted parameter is called listener is notified without redacted parameters`(
        arguments: List<Any?>
    ) = functionWithRedactedParameter(arguments).compile(compiler) {
        assertCompilation()

        val clazz = classLoader.loadClass("Main")
        val func = clazz.methods.single { it.name == "foo" }

        assertInvoke("Main.foo", arguments.drop(1)) {
            func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
        }
    }

    @Test
    fun `when function with redacted parent parameter is called listener is notified without redacted parameters`() =
        functionWithRedactedParentParameter().compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            val parameter = classLoader.loadClass("Test")

            assertInvoke("Main.foo", emptyList()) {
                func.invoke(
                    clazz.getDeclaredConstructor().newInstance(),
                    parameter.getDeclaredConstructor().newInstance()
                )
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when class is annotated all functions called notify a listener`(arguments: List<Any?>) =
        simpleClass(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke(listOf("Main.foo"), arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when class is annotated and if function is annotated listener is notified only once`(arguments: List<Any?>) =
        annotatedClassWithAnnotatedFunction(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke(listOf("Main.foo"), arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `when class is with annotated parent all functions called notify a listener`(arguments: List<Any?>) =
        simpleClassWithAnnotatedParent(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke(listOf("Main.foo"), arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }
}
