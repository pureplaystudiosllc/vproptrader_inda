package com.futureharvest.vproptrader;

import android.os.Bundle;
import android.util.Log;
import org.apache.cordova.*;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends CordovaActivity
{
    private static final String TAG = "MainActivity";
    private AppEventsLogger logger;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }
        logger = AppEventsLogger.newLogger(this);
        facebookEventLogging();

        loadUrl(launchUrl);
    }


    private void facebookEventLogging() {
        try {
            if (logger != null) {
                logger.logEvent("app_launched");
                Log.d(TAG, "Facebook test event 'app_launched' logged successfully");
            } else {
                Log.e(TAG, "Facebook logger is null in test method");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error testing Facebook event logging: " + e.getMessage(), e);
        }
    }
}
