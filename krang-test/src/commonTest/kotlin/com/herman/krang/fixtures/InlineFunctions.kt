@file:Suppress("all", "NOTHING_TO_INLINE")

package com.herman.krang.fixtures

import com.herman.krang.KrangFunctionCall
import com.herman.krang.KrangTestArgument
import com.herman.krang.runtime.annotations.Trace

// Define the inline functions
@Trace
inline fun inlineFunction() {
}

@Trace
inline fun inlineFunctionWithArguments(a: Int, b: String) {
}

@Trace
inline fun <T> inlineFunctionWithGenericArguments(a: T) {
}

@Trace
inline fun inlineFunctionWithNullableArgument(a: Int?, b: String?) {
}

@Trace
inline fun inlineFunctionWithDefaultArgument(a: Int, b: String = "default") {
}

@Trace
inline fun inlineFunctionWithLambda(
    a: Int,
    b: String,
    crossinline c: () -> Unit
) {
}

val inlineFunctionsTestArguments = sequenceOf(
    KrangTestArgument(
        function = { inlineFunction() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.inlineFunction",
            functionArguments = listOf()
        )
    ),
    KrangTestArgument(
        function = { inlineFunctionWithArguments(1, "test") },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.inlineFunctionWithArguments",
            functionArguments = listOf(1, "test")
        )
    ),
    KrangTestArgument(
        function = { inlineFunctionWithGenericArguments("test") },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.inlineFunctionWithGenericArguments",
            functionArguments = listOf("test")
        )
    ),
    KrangTestArgument(
        function = { inlineFunctionWithNullableArgument(null, null) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.inlineFunctionWithNullableArgument",
            functionArguments = listOf(null, null)
        )
    ),
    KrangTestArgument(
        function = { inlineFunctionWithDefaultArgument(1) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.inlineFunctionWithDefaultArgument",
            functionArguments = listOf(1, "default")
        )
    ),
    KrangTestArgument(
        function = { inlineFunctionWithLambda(1, "test") {  } },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.inlineFunctionWithLambda",
            functionArguments = listOf(1, "test")
        )
    ),
)