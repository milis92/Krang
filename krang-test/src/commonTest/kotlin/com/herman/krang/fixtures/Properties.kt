package com.herman.krang.fixtures

import com.herman.krang.KrangFunctionCall
import com.herman.krang.KrangTestArgument
import com.herman.krang.runtime.annotations.Intercept

val propertiesTestArguments = sequenceOf(
    KrangTestArgument(
        function = { propertyWithGetter },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.<get-propertyWithGetter>",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = { variableWithSetter = 2 },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.<set-variableWithSetter>",
            functionArguments = listOf(2)
        )
    ),
    KrangTestArgument(
        function = { variableWithGetter },
        expectedFunctionCall = KrangFunctionCall(
            functionName = "com.herman.krang.fixtures.<get-variableWithGetter>",
            functionArguments = emptyList()
        )
    ),
    KrangTestArgument(
        function = {
            variableWithGetterAndSetter += 1
        },
        expectedFunctionCalls = listOf(
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.<get-variableWithGetterAndSetter>",
                functionArguments = emptyList()
            ),
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.<set-variableWithGetterAndSetter>",
                functionArguments = listOf(2)
            )
        )
    ),
    KrangTestArgument(
        function = {
            val holder = PropertyHolder()
            holder.property
            holder.variable
        },
        expectedFunctionCalls = listOf(
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.PropertyHolder.<get-property>",
                functionArguments = emptyList()
            ),
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.PropertyHolder.<get-variable>",
                functionArguments = emptyList()
            )
        )
    ),
    KrangTestArgument(
        function = {
            val holder = PropertyHolder()
            holder.variable = 2
        },
        expectedFunctionCalls = listOf(
            KrangFunctionCall(
                functionName = "com.herman.krang.fixtures.PropertyHolder.<set-variable>",
                functionArguments = listOf(2)
            )
        )
    ),
)

val propertyWithGetter = 1
    @Intercept
    get() = field

var variableWithSetter = 1
    @Intercept
    set(value) {
        field = value
    }

var variableWithGetter = 1
    @Intercept
    get() = field

var variableWithGetterAndSetter = 1
    @Intercept
    get() = field

    @Intercept
    set(value) {
        field = value
    }

class PropertyHolder {
    val property = 1
        @Intercept
        get() = field

    var variable = 1
        @Intercept
        get() = field

        @Intercept
        set(value) {
            field = value
        }
}
