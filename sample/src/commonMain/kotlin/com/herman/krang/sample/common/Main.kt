package com.herman.krang.sample.common

import com.herman.krang.runtime.annotations.Intercept

class Foo {
    @Intercept
    fun bar(@Suppress("UNUSED_PARAMETER") param1: String) {
        /* no-op */
    }
}
