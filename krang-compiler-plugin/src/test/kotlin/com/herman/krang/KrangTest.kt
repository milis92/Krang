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
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                testFunction()
            }

            @Intercept
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

            import com.herman.krang.runtime.annotations.Intercept

            @Intercept
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
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                testFunction("Hello", 1)
            }

            @Intercept
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
            import com.herman.krang.runtime.annotations.Intercept
            import com.herman.krang.runtime.FunctionCallListener

            fun main() {
                KrangListener.onFunctionCalled("testFunction", arrayOf("Hello", "1"))
            }

            object KrangListener : FunctionCallListener {
                @Intercept
                override fun onFunctionCalled(functionName: String, parameters: Array<out Any?>) {
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
    fun memberFunctionCallNotifiesKrang() {
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                val test = Test()
                test.testFunction()
            }

            class Test {
                @Intercept
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
    fun memberFunctionWhoseParentIsAnnotatedNotifiesKrang(){
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                val test = Test()
                test.testFunction()
            }

            @Intercept
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
    fun memberFunctionThatOveridesAnnotatedParentFunctionNotifiesKrang(){
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                val test = Test()
                test.testFunction()
            }

            interface Interceptable {
                @Intercept
                fun testFunction()
            }

            open class Parent : Interceptable {
                override fun testFunction() {
                    // Do nothing
                }
            }

            class Test : Parent() {
                override fun testFunction() {
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
    fun memberFunctionThatOveridesAnnotatedParentNotifiesKrang(){
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                val test = Test()
                test.testFunction()
            }

            @Intercept
            interface Interceptable {
                fun testFunction()
            }

            open class Parent : Interceptable {
                override fun testFunction() {
                    // Do nothing
                }
            }

            class Test : Parent() {
                override fun testFunction() {
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
    fun functionWithReceiverPassesRecieverToKrang(){
        // Given
        val sourceFile = SourceFile.kotlin(
            name = "Main.kt",
            contents = """
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                "Hello".testFunction()
            }

            @Intercept
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
            import com.herman.krang.runtime.annotations.Intercept

            fun main() {
                variableWithGetterAndSetter = 2
                variableWithGetterAndSetter
            }

            var variableWithGetterAndSetter = 1
                @Intercept
                get() = field
            
                @Intercept
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
}

