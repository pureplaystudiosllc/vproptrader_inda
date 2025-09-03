package com.futureharvest.vproptrader;

import android.os.Bundle;
import android.util.Log;
import org.apache.cordova.*;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.onesignal.OneSignal;

public class MainActivity extends CordovaActivity {
    private static final String TAG = "MainActivity";
    private AppEventsLogger logger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        logger = AppEventsLogger.newLogger(this);
        facebookEventLogging();

        checkAndRequestNotificationPermission();

        loadUrl(launchUrl);
    }

    private void checkAndRequestNotificationPermission() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
                    Log.d(TAG, "Requesting notification permission");
                } else {
                    Log.d(TAG, "Notification permission already granted");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking notification permission: " + e.getMessage(), e);
        }
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
