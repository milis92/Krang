package com.herman.krang.sample.jvm

import com.herman.krang.runtime.Krang

fun main() {
    Krang.addListener { name, arguments ->
        println("Function with $name and ${arguments.joinToString()} called")
    }
    Foo().bar("bazz")
}

class Foo {

    fun bar(@Suppress("UNUSED_PARAMETER") param1: String) {
        /* no-op */
    }
}