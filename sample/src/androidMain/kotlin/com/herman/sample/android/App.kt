package com.herman.sample.android

import android.app.Application
import com.herman.krang.runtime.Krang
import com.herman.krang.sample.common.Foo

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Krang.addListener {name, params ->

        }
        Foo().bar("bazz")
    }
}
