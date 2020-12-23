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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(CompilerTestFactory::class)
class CompilerTest(private val compiler: KotlinCompilation) {

    @DisplayName("No arguments functions")
    @Nested
    inner class NoArgsFunctions {

        @Test
        fun `main function calls tracer`() {

            @Language("kotlin") val source =
                """
                import com.herman.krang.runtime.annotations.Trace

                @Trace
                fun main(){}
                """

            val clazz = source.compile("Main.kt")
                .classLoader.loadClass("MainKt")
            val func = clazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }

            assertTracerAfterInvoke("main", null) {
                func.invoke(null)
            }
        }

        @Test
        fun `Class function calls tracer`() {

            @Language("kotlin") val source =
                """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                
                @Trace
                fun main(){
                
                }
                
                }
                """

            val clazz = source.compile("Main.kt")
                .classLoader.loadClass("Main")
            val func = clazz.declaredMethods.single { it.name == "main" }

            assertTracerAfterInvoke("main", null) {
                func.invoke(clazz.newInstance())
            }
        }

        @Test
        fun `No body class function calls tracer`() {

            @Language("kotlin") val source =
                """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                
                @Trace
                fun main() = true
                
                }
                """

            val clazz = source.compile("Main.kt")
                .classLoader.loadClass("Main")
            val func = clazz.declaredMethods.single { it.name == "main" }

            assertTracerAfterInvoke("main", null) {
                func.invoke(clazz.newInstance())
            }
        }

        @Test
        fun `Scope body function calls tracer`() {

            @Language("kotlin") val source =
                """
                import com.herman.krang.runtime.annotations.Trace

                class Main {
                
                @Trace
                fun main() = run { 
                    
                 }
                }
                """

            val clazz = source.compile("Main.kt")
                .classLoader.loadClass("Main")
            val func = clazz.declaredMethods.single { it.name == "main" }

            assertTracerAfterInvoke("main", null) {
                func.invoke(clazz.newInstance())
            }
        }

        @Test
        fun `Inner function calls tracer`() {

            @Language("kotlin") val source =
                """
                import com.herman.krang.runtime.annotations.Trace

                class Main {

                fun main() { 
                    @Trace 
                    fun innerFunction(){
                    
                    }
                    innerFunction()
                 }
                }
                """

            val clazz = source.compile("Main.kt")
                .classLoader.loadClass("Main")
            val func = clazz.declaredMethods.single { it.name == "main" }

            assertTracerAfterInvoke("innerFunction", null) {
                func.invoke(clazz.newInstance())
            }
        }

        @Test
        fun `Extension function calls tracer`() {

            @Language("kotlin") val source =
                """
                import com.herman.krang.runtime.annotations.Trace

                @Trace
                fun String.main() {
                }
                """

            val clazz = source.compile("Main.kt")
                .classLoader.loadClass("MainKt")
            val func = clazz.declaredMethods.single { it.name == "main" }

            assertTracerAfterInvoke("main", null) {
                func.invoke(null, "Test")
            }
        }
    }

    private fun String.compile(fileName: String): KotlinCompilation.Result {
        compiler.sources = listOf(SourceFile.kotlin(fileName, this, true))
        val result = compiler.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        return result
    }

    private fun assertTracerAfterInvoke(functionName: String, vararg: Any?, invoke: () -> Unit) {
        var invoked = false
        val tracer = Tracer { function, params ->
            invoked = true
            assertEquals(functionName, function)
        }
        Krang.addTracer(tracer)
        invoke()
        assertTrue(invoked)
        Krang.removeTracer(tracer)
    }
}