@file:Suppress("all")

package com.herman.krang.fixtures

import com.herman.krang.KrangFunctionCall
import com.herman.krang.KrangTestArgument
import com.herman.krang.runtime.annotations.Intercept

// Define the extension functions
@Intercept
fun String.extensionFunction() {
}

@Intercept
fun String.extensionFunctionWithArguments(a: Int, b: String, c: () -> Unit) {
}

val extensionFunctionsTestArguments = sequenceOf(
    KrangTestArgument(
        function = { "test".extensionFunction() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.extensionFunction",
            functionArguments = listOf("test")
        )
    ),
    KrangTestArgument(
        function = { "test".extensionFunctionWithArguments(1, "test", {}) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.extensionFunctionWithArguments",
            functionArguments = listOf("test", 1, "test", {})
        )
    ),
)