package com.herman.krang

import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCompilerApi::class)
class KrangTest {
    companion object {
        @JvmField
        @RegisterExtension
        var compilerExtension = KrangCompilerTestExtension(cleanUp = false)
    }

    @Test
    fun functionCallNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            fun main() {
                testFunction()
            }

            @Trace
            fun testFunction() {
                // Do nothing
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("testFunction", capturedFunctionCalls[0].functionName)
    }

    @Test
    fun redactedParameterNotPassedToKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace
            import com.herman.krang.runtime.annotations.Redact

            fun main() {
                testFunction("Hello")
            }

            @Trace
            fun testFunction(@Redact string: String) {
                // Do nothing
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("testFunction", capturedFunctionCalls[0].functionName)
        assertEquals(0, capturedFunctionCalls[0].functionParameters.size)
    }

    @Test
    fun namespacedFunctionNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            fun main() {
                com.herman.namespaced.testFunction()
            }
            """
        )

        val nameSpaced = SourceFile.kotlin(
            name = "Namespaced.kt",
            contents = """
            package com.herman.namespaced

            import com.herman.krang.runtime.annotations.Trace

            @Trace
            fun testFunction() {
                // Do nothing
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile, nameSpaced))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("com.herman.namespaced.testFunction", capturedFunctionCalls[0].functionName)
    }

    @Test
    fun functionWithParametersPassesArgumentsToKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            fun main() {
                testFunction("Hello", 1)
            }

            @Trace
            fun testFunction(message: String, number: Int) {
                // Do nothing
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("testFunction", capturedFunctionCalls[0].functionName)
        assertContentEquals(listOf("Hello", 1), capturedFunctionCalls[0].functionParameters)
    }

    @Test
    fun krangDoesNotTraceKrangFunctionCallListener() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace
            import com.herman.krang.runtime.FunctionCallListener
            import com.herman.krang.runtime.TracingContext

            fun main() {
                KrangListener.onFunctionCalled("testFunction", arrayOf("Hello", "1"), TracingContext(null))
            }

            object KrangListener : FunctionCallListener {
                @Trace
                override fun onFunctionCalled(name: String, parameters: Array<out Any?>, tracingContext: TracingContext) {
                    // Do nothing
                }
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertTrue(capturedFunctionCalls.isEmpty())
    }

    @Test
    fun functionWithReceiverPassesRecieverToKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            fun main() {
                "Hello".testFunction()
            }

            @Trace
            fun String.testFunction(){
                // Do nothing
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("testFunction", capturedFunctionCalls[0].functionName)
        assertContentEquals(listOf("Hello"), capturedFunctionCalls[0].functionParameters)
    }

    @Test
    fun propertyGetterAndSettersCallsNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            fun main() {
                variableWithGetterAndSetter = 2
                variableWithGetterAndSetter
            }

            var variableWithGetterAndSetter = 1
                @Trace
                get() = field
            
                @Trace
                set(value) {
                    field = value
                }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(2, capturedFunctionCalls.size)
        assertEquals("<set-variableWithGetterAndSetter>", capturedFunctionCalls[0].functionName)
        assertEquals("<get-variableWithGetterAndSetter>", capturedFunctionCalls[1].functionName)
    }

    @Test
    fun memberFunctionCallNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            fun main() {
                val test = Test()
                test.testFunction()
            }

            class Test {
                @Trace
                fun testFunction() {
                    // Do nothing
                }
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("Test.testFunction", capturedFunctionCalls[0].functionName)
    }

    @Test
    fun memberFunctionWithAnnotatedParentCallNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            fun main() {
                val test = Test()
                test.testFunction()
            }

            @Trace
            class Test { 
                fun testFunction() {
                    // Do nothing
                }
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("Test.testFunction", capturedFunctionCalls[0].functionName)
    }

    @Test
    fun memberFunctionWithAnnotatedFileCallNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            @file:Trace

            import com.herman.krang.runtime.annotations.Trace

            fun main() {
                val test = Test()
                test.testFunction()
            }

            class Test { 
                fun testFunction() {
                    // Do nothing
                }
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(2, capturedFunctionCalls.size)
        assertEquals("Test.testFunction", capturedFunctionCalls[1].functionName)
    }

    @Test
    fun memberFunctionWithCustomTraceAnnotationNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            @Trace
            annotation class CustomTrace(
              val message: String = "Custom Trace"
            )

            fun main() {
                val test = Test()
                test.testFunction()
            }

            class Test  {
                @CustomTrace
                fun testFunction() {
                    // Do nothing
                }
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("Test.testFunction", capturedFunctionCalls[0].functionName)
        assertEquals(
            "@CustomTrace(message=Custom Trace)",
            capturedFunctionCalls[0].tracingContext?.annotation.toString()
        )
    }

    @Test
    fun memberFunctionWithCustomTraceAnnotationParentNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace

            @Trace
            annotation class CustomTrace(
              val message: String = "Custom Trace"
            )

            fun main() {
                val test = Test()
                test.testFunction()
            }

            @CustomTrace
            class Test  {
                fun testFunction() {
                    // Do nothing
                }
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("Test.testFunction", capturedFunctionCalls[0].functionName)
        assertEquals(
            "@CustomTrace(message=Custom Trace)",
            capturedFunctionCalls[0].tracingContext?.annotation.toString()
        )
    }

    @Test
    fun customRedactedParameterNotPassedToKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace
            import com.herman.krang.runtime.annotations.Redact

            @Redact
            annotation class CustomRedact

            fun main() {
                testFunction("Hello")
            }

            @Trace
            fun testFunction(@CustomRedact string: String) {
                // Do nothing
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("testFunction", capturedFunctionCalls[0].functionName)
        assertEquals(0, capturedFunctionCalls[0].functionParameters.size)
    }

    @Test
    fun redactedTypeNotPassedToKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Trace
            import com.herman.krang.runtime.annotations.Redact

            fun main() {
                testFunction(Redacted("Hello"))
            }

            @Redact
            data class Redacted(val value: String)

            @Trace
            fun testFunction(redacted: Redacted) {
                // Do nothing
            }
            """
        )

        // When
        val capturedFunctionCalls = compilerExtension.compileAndInvoke(listOf(sourceFile))

        // Then
        assertEquals(1, capturedFunctionCalls.size)
        assertEquals("testFunction", capturedFunctionCalls[0].functionName)
        assertEquals(0, capturedFunctionCalls[0].functionParameters.size)
    }
}
