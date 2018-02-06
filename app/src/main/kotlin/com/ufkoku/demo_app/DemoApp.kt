package com.ufkoku.demo_app

import android.app.Application
import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary

class DemoApp : Application() {

    override fun onCreate() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        super.onCreate()

        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build())

        StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build())

        LeakCanary.install(this)
    }

}
