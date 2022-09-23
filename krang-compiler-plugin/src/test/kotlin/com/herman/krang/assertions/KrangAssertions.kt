package com.herman.krang.assertions

import com.herman.krang.runtime.Krang
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.function.Executable

fun String.compile(
    compiler: KotlinCompilation,
    fileName: String = "Main.kt",
    compilationResult: KotlinCompilation.Result.() -> Unit
) {
    compiler.sources = listOf(SourceFile.kotlin(fileName, this, true))
    val result = compiler.compile()
    compilationResult(result)
}

fun KotlinCompilation.Result.assertCompilation() {
    Assertions.assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
}

fun assertInvoke(
    expectedFunctionName: String,
    expectedParameters: List<Any?>,
    expectedNumberOfInvocations: Int = 1,
    assert: () -> Unit
) {
    assertInvoke(listOf(expectedFunctionName), expectedParameters, expectedNumberOfInvocations, assert)
}

fun assertInvoke(
    expectedFunctions: List<String?>,
    expectedParameters: List<Any?>,
    expectedNumberOfInvocations: Int = expectedFunctions.size,
    assert: () -> Unit
) {
    val listener = KrangCaptureListener()
    Krang.addListener(listener)
    assert()
    Krang.removeListener(listener)

    Assertions.assertAll(
        Executable {
            Assertions.assertLinesMatch(expectedFunctions, listener.capturedFunctionName) {
                "Function name doest not match - expected: $expectedFunctions received " +
                    "${listener.capturedFunctionName}"
            }
            Assertions.assertEquals(expectedNumberOfInvocations, listener.capturedNumberOfInvocations) {
                "Function invoked more than expected: $expectedNumberOfInvocations received " +
                    "${listener.capturedNumberOfInvocations}"
            }
            if (expectedParameters.isNotEmpty()) {
                Assertions.assertTrue(expectedParameters.containsAll(listener.capturedParameters)) {
                    "Function arguments not matching - expected: ${expectedParameters.joinToString()} " +
                        "received: ${listener.capturedParameters.joinToString()}"
                }
            }
        }
    )
}
