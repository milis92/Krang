@file:Suppress("all")

package com.herman.krang.fixtures

import com.herman.krang.KrangFunctionCall
import com.herman.krang.KrangTestArgument
import com.herman.krang.runtime.annotations.Intercept

class MemberFunctionHolder {
    @Intercept
    fun memberFunction() {
    }

    @Intercept
    fun memberFunctionWithArguments(a: Int, b: String, c: () -> Unit) {
    }
}

class MembersInsideCompanionObject {
    companion object {
        @Intercept
        fun memberFunction() {
        }

        @Intercept
        fun memberFunctionWithArguments(a: Int, b: String, c: () -> Unit) {
        }
    }
}

@Intercept
class MembersWithClassAnnotation(){
    fun memberFunction() {
    }

    fun memberFunctionWithArguments(a: Int, b: String, c: () -> Unit) {
    }
}

val memberFunctionsTestArguments = sequenceOf(
    KrangTestArgument(
        function = { MemberFunctionHolder().memberFunction() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.MemberFunctionHolder.memberFunction",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { MemberFunctionHolder().memberFunctionWithArguments(1, "test", {}) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.MemberFunctionHolder.memberFunctionWithArguments",
            functionArguments = listOf(1, "test", {})
        )
    ),
    KrangTestArgument(
        function = { MembersInsideCompanionObject.memberFunction() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.MembersInsideCompanionObject.Companion.memberFunction",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { MembersInsideCompanionObject.memberFunctionWithArguments(1, "test", {}) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.MembersInsideCompanionObject.Companion.memberFunctionWithArguments",
            functionArguments = listOf(1, "test", {})
        )
    ),
    KrangTestArgument(
        function = { MembersWithClassAnnotation().memberFunction() },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.MembersWithClassAnnotation.memberFunction",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { MembersWithClassAnnotation().memberFunctionWithArguments(1, "test", {}) },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.MembersWithClassAnnotation.memberFunctionWithArguments",
            functionArguments = listOf(1, "test", {})
        )
    )
)



