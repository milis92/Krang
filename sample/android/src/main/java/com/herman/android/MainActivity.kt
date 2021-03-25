package com.herman.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herman.krang.runtime.annotations.Intercept

class MainActivity : AppCompatActivity() {

    @Intercept
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}