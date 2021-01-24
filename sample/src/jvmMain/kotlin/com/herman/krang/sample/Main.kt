package com.herman.krang.sample

import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.annotations.Intercept
import com.herman.krang.runtime.annotations.Redact

fun main() {
    Krang.addInterceptor { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} called")
    }
    testFunction("test", 1, true)
}

@Intercept
fun testFunction(@Redact a: String, @Redact b: Int, c: Boolean) {
}