package com.herman.krang.sample.common

import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.annotations.Intercept

fun main() {
    Krang.addListener { name, arguments ->
        println("Function with $name and ${arguments.joinToString()} called")
    }
    Foo().bar("bazz")
}

class Foo {

    @Intercept
    fun bar(@Suppress("UNUSED_PARAMETER") param1: String) {
        /* no-op */
    }
}
