package com.herman.krang.sample.jvm

import com.herman.krang.runtime.Krang
import com.herman.krang.sample.common.Foo

fun main() {
    Krang.addListener { name, arguments ->
        println("Function with $name and ${arguments.joinToString()} called")
    }
    Foo().bar("bazz")
}
