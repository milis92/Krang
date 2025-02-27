@file:Suppress("all")

package com.herman.krang.fixtures

import com.herman.krang.KrangFunctionCall
import com.herman.krang.KrangTestArgument
import com.herman.krang.runtime.annotations.Trace

data class SimpleFunctionArgument(val a: Int, val b: String)

@Trace
fun simpleFunction() {}

@Trace
fun simpleFunctionWithBlockBody() = run {}

@Trace
fun simpleFunctionWithArguments(
    a: Int,
    b: String,
    d: SimpleFunctionArgument,
    c: () -> Unit
) {}

@Trace
fun simpleFunctionWithLambda(
    c: (Int,  String) -> String
){}

@Trace
fun simpleFunctionWithDefaultArguments(
    a: Int = 1,
    b: String = "test",
    d: SimpleFunctionArgument = SimpleFunctionArgument(1, "test"),
    c: (String) -> Unit = { }
) {}

@Trace
fun simpleFunctionWithSomeDefaultArguments(
    a: Int = 1,
    b: String = "test",
    d: SimpleFunctionArgument,
    c: (String) -> Unit
) {}

@Trace
fun simpleFunctionWithNullableArguments(
    a: Int?,
    b: String?,
    d: SimpleFunctionArgument?,
    c: () -> Unit
) {}

@Trace
fun simpleExpressionFunction() = 1 + 1

@Trace
fun simpleExpressionFunctionWithArguments(a: Int, b: String) = a + b.length

val simpleFunctionsTestArguments = sequenceOf(
    KrangTestArgument(
        function = { simpleFunction() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunction",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { simpleFunctionWithBlockBody() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunctionWithBlockBody",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { simpleFunctionWithArguments(1, "test", SimpleFunctionArgument(1, "test"), {}) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunctionWithArguments",
            functionArguments = listOf(1, "test", SimpleFunctionArgument(1, "test"), {})
        )
    ),
    KrangTestArgument(
        function = { simpleFunctionWithLambda { _, _ -> "test" } },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunctionWithLambda",
            functionArguments = listOf({})
        )
    ),
    KrangTestArgument(
        function = { simpleFunctionWithDefaultArguments() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunctionWithDefaultArguments",
            functionArguments = listOf(1, "test", SimpleFunctionArgument(1, "test"), {})
        )
    ),
    KrangTestArgument(
        function = {
            simpleFunctionWithSomeDefaultArguments(1, "test", SimpleFunctionArgument(1, "test"), {})
        },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunctionWithSomeDefaultArguments",
            functionArguments = listOf(1, "test", SimpleFunctionArgument(1, "test"), {})
        )
    ),
    KrangTestArgument(
        function = { simpleFunctionWithNullableArguments(null, null, null) {} },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunctionWithNullableArguments",
            functionArguments = listOf(null, null, null, {})
        )
    ),
    KrangTestArgument(
        function = { simpleFunctionWithNullableArguments(1, null, SimpleFunctionArgument(1, "test")) {} },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleFunctionWithNullableArguments",
            functionArguments = listOf(1, null, SimpleFunctionArgument(1, "test"), {})
        )
    ),
    KrangTestArgument(
        function = { simpleExpressionFunction() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleExpressionFunction",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { simpleExpressionFunctionWithArguments(1, "test") },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.simpleExpressionFunctionWithArguments",
            functionArguments = listOf(1, "test")
        )
    ),
)