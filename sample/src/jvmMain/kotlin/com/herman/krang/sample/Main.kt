package com.herman.krang.sample

import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.Tracer
import com.herman.krang.runtime.annotations.Trace

fun main() {
    Krang.addTracer(object : Tracer {
        override fun onFunctionEnter(functionSignature: String) {
            println(functionSignature)
        }

        override fun onFunctionExit(functionSignature: String) {
            println(functionSignature)
        }
    })
    sampleTest("test")
}

@Trace
fun sampleTest(param: String) {
}