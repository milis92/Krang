package com.herman.krang.sample

import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.annotations.Intercept

fun main() {
    Krang.addInterceptor { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} called")
    }
    testfunction("test", "df", "D")
}

@Intercept
fun testfunction(name: String, vararg test: Any?) {
}