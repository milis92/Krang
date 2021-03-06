package com.herman.android

import android.app.Application
import android.util.Log
import com.herman.krang.runtime.Krang

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Krang.addInterceptor { functionName, parameters ->
            Log.d("App", "Function with name:$functionName and ${parameters.joinToString()} called")
        }
    }
}