@file:Suppress("all")

package com.herman.krang.fixtures

import com.herman.krang.KrangFunctionCall
import com.herman.krang.KrangTestArgument
import com.herman.krang.runtime.annotations.Intercept

@Intercept
infix fun Int.simpleInfixFunction(b: Int) {
}

@Intercept
infix fun Int.simpleInfixFunctionWithBlockBody(b: Int) = {}

@Intercept
infix fun Int.simpleInfixFunctionWithArguments(b: Int) {
}

@Intercept
infix fun Int.simpleInfixFunctionWithArguments(a: () -> Unit) {
}

@Intercept
infix fun Int.simpleInfixFunctionWithNullableArguments(b: Int?) {
}

val infixFunctionsTestArguments = sequenceOf(
    KrangTestArgument(
        function = { 1.simpleInfixFunction(2) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleInfixFunction",
            functionArguments = listOf(1, 2)
        )
    ),
    KrangTestArgument(
        function = { 1.simpleInfixFunctionWithBlockBody(2) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleInfixFunctionWithBlockBody",
            functionArguments = listOf(1, 2)
        )
    ),
    KrangTestArgument(
        function = { 1.simpleInfixFunctionWithArguments(2) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleInfixFunctionWithArguments",
            functionArguments = listOf(1, 2)
        )
    ),
    KrangTestArgument(
        function = { 1.simpleInfixFunctionWithArguments { } },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleInfixFunctionWithArguments",
            functionArguments = listOf(1, {})
        )
    ),
    KrangTestArgument(
        function = { 1.simpleInfixFunctionWithNullableArguments(null) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleInfixFunctionWithNullableArguments",
            functionArguments = listOf(1, null)
        )
    ),
    KrangTestArgument(
        function = { 1.simpleInfixFunctionWithNullableArguments(2) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleInfixFunctionWithNullableArguments",
            functionArguments = listOf(1, 2)
        )
    )
)
