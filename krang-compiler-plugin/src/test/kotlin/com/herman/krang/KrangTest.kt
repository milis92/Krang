package com.herman.krang

import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.test.assertContentEquals

class KrangTest {
    companion object {
        @JvmField
        @RegisterExtension
        var compilerExtension: KrangCompilerTestExtension = KrangCompilerTestExtension()

        @JvmStatic
        fun provideTestArguments(): Stream<Arguments> {
            return Stream.of(
                KrangTestArgument(
                    "SimpleFunction.kt",
                    listOf("simpleFunction"),
                    emptyList()
                ),
                KrangTestArgument(
                    "SimpleFunctionWithArguments.kt",
                    listOf("simpleFunctionWithArguments"),
                    listOf(1, "test")
                ),
            )
        }

        data class KrangTestArgument(
            val fileName: String,
            val expectedFunctionNames: List<String>,
            val expectedParameters: List<Any>
        ) : Arguments {
            override fun get(): Array<Any> {
                return arrayOf(fileName, expectedFunctionNames, expectedParameters)
            }
        }
    }

    @ParameterizedTest(name = "request: {0}")
    @MethodSource("provideTestArguments")
    fun functionCallNotifiesKrang(
        fileName: String,
        expectedFunctionNames: List<String>,
        expectedParameters: List<Any>
    ) {
        compilerExtension.compileAndAssert(
            resourceClassPath("/$fileName") ?: return
        ) {
            assertContentEquals(expectedFunctionNames, it.capturedFunctionName)
            assertContentEquals(expectedParameters, it.capturedParameters)
        }
    }
}

fun Any.resourceClassPath(resource: String): Path? {
    val clazz = javaClass.getResource(resource)?.toURI() ?: return null
    return Path.of(clazz)
}
