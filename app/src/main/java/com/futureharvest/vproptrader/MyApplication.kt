package com.futureharvest.vproptrader

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log

class MyApplication : Application() {

    private val TAG = "MyApplication"

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        try {
            SdkInit.initIfNeeded(this)
        } catch (e: Exception) {
            Log.e(TAG, "Deferred SDK init error", e)
        }
    }
}