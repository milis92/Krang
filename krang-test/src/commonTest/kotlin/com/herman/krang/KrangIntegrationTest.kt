package com.herman.krang

import com.herman.krang.runtime.Krang
import kotlin.test.*

class KrangIntegrationTest {

    @Test
    fun krangInstrumentsAFunction() = KrangTestArguments().map { testArgument ->
        // Given
        val listener = KrangTestListener()
        Krang.addListener(listener)

        // When
        testArgument.function.invoke()

        // Then
        // Catch the assertion errors so we can execute all the cases
        val testResult = runCatching {
            testArgument.expectedFunctionCalls.assertFunctionCalled(
                actualCalls = listener.capturedFunctionCalls
            ) { expectedFunctionCall, actualFunctionCall ->
                println()
                println("Expected: $expectedFunctionCall")
                println("Actual:   $actualFunctionCall")
            }
        }.onSuccess {
            println("✅ Success")
            println("-----------")
        }.onFailure {
            println("❌ Failure")
            println("-----------")
        }.exceptionOrNull()

        Krang.removeListener(listener)
        testResult
    }.filterIsInstance<Throwable>().toList().failWithMultipleErrors()

    private fun List<KrangFunctionCall>.assertFunctionCalled(
        actualCalls: List<KrangFunctionCall>,
        onEach: (KrangFunctionCall, KrangFunctionCall?) -> Unit = { _, _ -> }
    ) {
        forEachIndexed { index, expectedCall ->
            val actualCall = actualCalls.getOrNull(index)
            onEach(expectedCall, actualCall)

            if (actualCall == null) {
                fail("Expected function call: $expectedCall, but no function call was captured")
            }

            expectedCall.assertFunctionCalled(actualCall)
        }
    }
}

fun List<Throwable>.failWithMultipleErrors() {
    when {
        isEmpty() -> return
        size == 1 -> fail(first().message)
        else -> {
            throw fold(AssertionError("Multiple assertion errors occurred:")) { acc, assertionError ->
                acc.addSuppressed(assertionError)
                acc
            }
        }
    }
}
