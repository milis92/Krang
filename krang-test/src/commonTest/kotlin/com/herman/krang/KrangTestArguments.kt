package com.herman.krang

import com.herman.krang.fixtures.*

data class KrangTestArgument(
    val function: () -> Unit,
    val expectedFunctionCalls: List<KrangFunctionCall>,
) {
    constructor(
        function: () -> Unit,
        expectedFunctionCall: KrangFunctionCall
    ) : this(function, listOf(expectedFunctionCall))
}

object KrangTestArguments {
    operator fun invoke(): Sequence<KrangTestArgument> = sequence {
        yieldAll(simpleFunctionsTestArguments)
        yieldAll(infixFunctionsTestArguments)
        yieldAll(localFunctionsTestArgument)
        yieldAll(memberFunctionsTestArguments)
        yieldAll(extensionFunctionsTestArguments)
        yieldAll(inlineFunctionsTestArguments)
        yieldAll(propertiesTestArguments)
    }
}
