package com.futureharvest.vproptrader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DeviceIdPlugin extends CordovaPlugin {
    private static final String TAG = "DeviceIdPlugin";
    private static final String PREFS_NAME = "device_id_prefs";
    private static final String KEY_DEVICE_ID = "generated_device_id";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getDeviceId")) {
            this.getDeviceId(callbackContext);
            return true;
        }
        return false;
    }

    private void getDeviceId(CallbackContext callbackContext) {
        String cachedId = getCachedDeviceId();
        if (cachedId != null && !cachedId.isEmpty()) {
            callbackContext.success(cachedId);
            return;
        }

        AsyncTask.execute(() -> {
            try {
                Context context = cordova.getActivity().getApplicationContext();

                String deviceId = getAdvertisingId(context);

                if (deviceId == null || deviceId.isEmpty()) {
                    deviceId = getFallbackDeviceId(context);
                }

                if (deviceId != null && !deviceId.isEmpty()) {
                    cacheDeviceId(deviceId);
                    callbackContext.success(deviceId);
                } else {
                    String fallbackId = generateDeviceBasedId(context);
                    cacheDeviceId(fallbackId);
                    callbackContext.success(fallbackId);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error getting device ID", e);
                try {
                    Context context = cordova.getActivity().getApplicationContext();
                    String fallbackId = generateDeviceBasedId(context);
                    cacheDeviceId(fallbackId);
                    callbackContext.success(fallbackId);
                } catch (Exception ex) {
                    String uuid = UUID.randomUUID().toString().replace("-", "");
                    cacheDeviceId(uuid);
                    callbackContext.success(uuid);
                }
            }
        });
    }

    private String getCachedDeviceId() {
        try {
            SharedPreferences prefs = cordova.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return prefs.getString(KEY_DEVICE_ID, null);
        } catch (Exception e) {
            Log.w(TAG, "Error getting cached device ID", e);
            return null;
        }
    }

    private void cacheDeviceId(String deviceId) {
        try {
            SharedPreferences prefs = cordova.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply();
        } catch (Exception e) {
            Log.w(TAG, "Error caching device ID", e);
        }
    }

    private String getAdvertisingId(Context context) {
        try {
            Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                return adInfo.getId();
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get Advertising ID: " + e.getMessage());
        }
        return null;
    }

    private String getFallbackDeviceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String id = prefs.getString(KEY_DEVICE_ID, null);

        if (id == null) {
            id = generateDeviceBasedId(context);
            prefs.edit().putString(KEY_DEVICE_ID, id).apply();
        }

        return id;
    }

    private String generateDeviceBasedId(Context context) {
        try {
            @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceModel = android.os.Build.MODEL;
            String manufacturer = android.os.Build.MANUFACTURER;

            String combined = (androidId != null ? androidId : "") +
                    (deviceModel != null ? deviceModel : "") +
                    (manufacturer != null ? manufacturer : "");

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString().substring(0, 32);

        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "SHA-256 not available, using UUID fallback");
            return UUID.randomUUID().toString();
        }
    }
}