@file:Suppress("all")

package com.herman.krang.fixtures

import com.herman.krang.KrangFunctionCall
import com.herman.krang.KrangTestArgument
import com.herman.krang.runtime.annotations.Intercept


@Intercept
fun mainFunctionOnly() {
    fun localFunction() {}
    localFunction()
}

fun localFunctionOnly() {
    @Intercept
    fun localFunction() {
    }
    localFunction()
}

fun localFunctionWithArguments() {
    @Intercept
    fun localFunctionWithArguments(a: Int, b: String) {
    }
    localFunctionWithArguments(1, "test")
}

@Intercept
fun localFunction() {
    @Intercept
    fun localFunction() {
    }
    localFunction()
}

data class MemberFunctionArgument(val a: Int, val b: String)

@Intercept
fun localFunctionWithArguments(a: Int, b: String, c: MemberFunctionArgument, d: () -> Unit) {
    @Intercept
    fun localFunctionWithArguments(a: Int, b: String, c: MemberFunctionArgument, d: () -> Unit){
    }
    localFunctionWithArguments(a, b, c, d)
}


@Intercept
fun localFunctionWithSomeArguments(a: Int, b: String, c: MemberFunctionArgument, d: () -> Unit){
    @Intercept
    fun localFunctionWithSomeArguments(a: MemberFunctionArgument) {
    }
    localFunctionWithSomeArguments(MemberFunctionArgument(1, "test"))
}

val localFunctionsTestArgument = sequenceOf(
    KrangTestArgument(
        function = { mainFunctionOnly() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.mainFunctionOnly",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { localFunctionOnly() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.localFunctionOnly.localFunction",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { localFunctionWithArguments() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.localFunctionWithArguments.localFunctionWithArguments",
            functionArguments = listOf(1, "test")
        )
    ),
    KrangTestArgument(
        function = { localFunction() },
        expectedFunctionCalls = listOf(
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.localFunction",
                functionArguments = emptyList()
            ),
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.localFunction.localFunction",
                functionArguments = emptyList()
            )
        )
    ),
    KrangTestArgument(
        function = { localFunctionWithArguments(1, "test", MemberFunctionArgument(1, "test"), {}) },
        expectedFunctionCalls = listOf(
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.localFunctionWithArguments",
                functionArguments = listOf(1, "test", MemberFunctionArgument(1, "test"), {})
            ),
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.localFunctionWithArguments.localFunctionWithArguments",
                functionArguments = listOf(1, "test", MemberFunctionArgument(1, "test"), {})
            )
        )
    ),
    KrangTestArgument(
        function = { localFunctionWithSomeArguments(1, "test", MemberFunctionArgument(1, "test"), {}) },
        expectedFunctionCalls = listOf(
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.localFunctionWithSomeArguments",
                functionArguments = listOf(1, "test", MemberFunctionArgument(1, "test"), {})
            ),
            KrangFunctionCall(
                functionName =
                "com.herman.krang.fixtures.localFunctionWithSomeArguments.localFunctionWithSomeArguments",
                functionArguments = listOf(MemberFunctionArgument(1, "test"))
            )
        )
    ),
)