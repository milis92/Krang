package com.herman.sample.android

import android.app.Activity
import android.os.Bundle
import com.herman.krang.runtime.annotations.Intercept

class MainActivity : Activity() {

    @Intercept
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}