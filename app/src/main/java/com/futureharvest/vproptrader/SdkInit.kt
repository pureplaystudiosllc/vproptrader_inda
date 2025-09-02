package com.futureharvest.vproptrader

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.Choreographer
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk.addLoggingBehavior
import com.facebook.FacebookSdk.isInitialized
import com.facebook.FacebookSdk.setAutoLogAppEventsEnabled
import com.facebook.FacebookSdk.setIsDebugEnabled
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel


object SdkInit {

    private const val TAG = "SdkInit"
//    private const val PREFS_NAME = "app_prefs"
//    private const val KEY_AGREED = "privacy_agreed"

    @Volatile
    private var initialized: Boolean = false

    @JvmStatic
    fun initIfNeeded(context: Context) {
        if (initialized) return

//        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        if (!prefs.getBoolean(KEY_AGREED, false)) {
//            Log.i(TAG, "Consent not granted. SDK init deferred.")
//            return
//        }

        // Defer heavy inits to first frame to avoid blocking cold start
        Choreographer.getInstance().postFrameCallback {
            if (initialized) return@postFrameCallback
            try {
                initSdks(context)
                initialized = true
            } catch (t: Throwable) {
                Log.e(TAG, "SDK init error", t)
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun initSdks(context: Context) {
        // AppsFlyer
        try {
            val appsFlyer = AppsFlyerLib.getInstance()
            if (BuildConfig.DEBUG) {
                appsFlyer.setDebugLog(true)
                appsFlyer.init("hLUQN3SC4fsHctnhC7VpRE", null, context)
                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                appsFlyer.setCustomerUserId("$androidId:debug")
                appsFlyer.start(context)
            } else {
                appsFlyer.setDebugLog(false)
                appsFlyer.init("hLUQN3SC4fsHctnhC7VpRE", null, context)
                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                appsFlyer.setCustomerUserId(androidId)
                appsFlyer.start(context)
            }
        } catch (t: Throwable) {
            Log.e(TAG, "AppsFlyer init skipped", t)
        }

        try {
            OneSignal.Debug.logLevel = if (BuildConfig.DEBUG) LogLevel.VERBOSE else LogLevel.NONE
            OneSignal.initWithContext(context, "d835b645-d58e-4ed0-a1c5-fe4e9add590b")
        } catch (e: Exception) {
            Log.e(TAG, "OneSignal init skipped", e)
        }

        try {
            if (isInitialized()) {
                setAutoLogAppEventsEnabled(true)
                if (BuildConfig.DEBUG) {
                    setIsDebugEnabled(true)
                    addLoggingBehavior(LoggingBehavior.APP_EVENTS)
                } else {
                    setIsDebugEnabled(false)
                }
                val app = context.applicationContext as Application
                AppEventsLogger.activateApp(app)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Facebook init skipped", e)
        }
    }
}


