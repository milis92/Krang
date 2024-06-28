package com.herman.krang

import com.herman.krang.runtime.FunctionCallListener
import kotlin.test.assertEquals
import kotlin.test.assertIs

data class KrangFunctionCall(
    val functionName: String,
    val functionArguments: List<Any?>
) {
    private val printableName: String = "Name: $functionName"
    private val printableArguments: String = buildString {
        val whiteSpace = "  "
        if (functionArguments.isNotEmpty()) {
            appendLine()
            appendLine("${whiteSpace}Arguments:")
        }
        functionArguments.forEachIndexed { index, argument ->
            appendLine("${whiteSpace}${whiteSpace}$index. Value: $argument")
        }
    }

    fun assertFunctionCalled(actualCall: KrangFunctionCall) {
        // Check if the expected function name is the same as the actual function name
        assertEquals(
            expected = functionName,
            actual = actualCall.functionName,
            message = "Expected a $printableName But was:: ${actualCall.printableName}"
        )

        // Check if the expected function parameters have the same position and value as the actual function parameters
        functionArguments.forEachIndexed { parameterIndex, argument ->
            val actualArgument = actualCall.functionArguments.getOrNull(parameterIndex)
            if (argument is Function<*>) {
                // Equality check cant be applied to the {} type
                assertIs<Function<*>>(
                    value = actualArgument,
                    message = buildString {
                        appendLine()
                        append("Expected: ${this@KrangFunctionCall}")
                        append("But was: $actualCall")
                    }
                )
            } else {
                assertEquals(
                    expected = argument,
                    actual = actualArgument,
                    message = buildString {
                        appendLine()
                        append("Expected: ${this@KrangFunctionCall}")
                        append("But was: $actualCall")
                    }
                )
            }
        }
    }

    override fun toString(): String = buildString {
        append(printableName)
        append(printableArguments)
    }
}

class KrangTestListener : FunctionCallListener {
    private val _capturedFunctionCalls = mutableListOf<KrangFunctionCall>()
    val capturedFunctionCalls
        get() = _capturedFunctionCalls.toList()

    override fun onFunctionCalled(
        functionName: String,
        vararg parameters: Any?
    ) {
        _capturedFunctionCalls += KrangFunctionCall(
            functionName = functionName,
            functionArguments = parameters.toList()
        )
    }
}
