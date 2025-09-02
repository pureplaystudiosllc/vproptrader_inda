package com.futureharvest.vproptrader;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;

public class TradingAccountPlugin extends CordovaPlugin {
    private static final String TAG = "TradingAccountPlugin";
    
    private static final int REQUEST_CODE_PURCHASE = 1001;
    private CallbackContext purchaseCallback;
    
    private AppEventsLogger facebookLogger;
    private FirebaseAnalytics firebaseAnalytics;
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        initializeAnalytics();
        
        Log.d(TAG, "TradingAccountPlugin initialized");
    }

    private void initializeAnalytics() {
        try {
            // Initialize Facebook logger
            facebookLogger = AppEventsLogger.newLogger(cordova.getActivity());
            
            // Initialize Firebase Analytics
            firebaseAnalytics = FirebaseAnalytics.getInstance(cordova.getActivity());
            
            // AppsFlyer is automatically initialized in Application class
            Log.d(TAG, "Analytics services initialized in plugin");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize analytics services", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            return switch (action) {
                case "showTradingAccounts" -> {
                    showTradingAccounts(callbackContext);
                    yield true;
                }
                case "purchaseTradingAccount" -> {
                    purchaseTradingAccount(args, callbackContext);
                    yield true;
                }
                case "getAvailableProducts" -> {
                    getAvailableProducts(callbackContext);
                    yield true;
                }
                case "checkPurchaseStatus" -> {
                    checkPurchaseStatus(args, callbackContext);
                    yield true;
                }
                case "logEvent" -> {
                    logEvent(args, callbackContext);
                    yield true;
                }
                default -> {
                    callbackContext.error("Unknown action: " + action);
                    yield false;
                }
            };
        } catch (Exception e) {
            Log.e(TAG, "Error executing action: " + action, e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Plugin error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Show trading accounts purchase interface
     */
    private void showTradingAccounts(CallbackContext callbackContext) {
        try {

            purchaseCallback = callbackContext;

            Intent intent = new Intent(cordova.getActivity(), TradingAccountPurchaseActivity.class);
            cordova.startActivityForResult(this, intent, REQUEST_CODE_PURCHASE);

            callbackContext.success("Purchase interface launched");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to show trading accounts", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to show trading accounts: " + e.getMessage());
        }
    }
    
    /**
     * Purchase a specific trading account
     */
    private void purchaseTradingAccount(JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            JSONObject params = args.getJSONObject(0);
            String productId = params.getString("productId");
            
            // Store callback for later use
            purchaseCallback = callbackContext;
            
            // Launch trading account purchase activity with specific product
            Intent intent = new Intent(cordova.getActivity(), TradingAccountPurchaseActivity.class);
            intent.putExtra("product_id", productId);
            cordova.startActivityForResult(this, intent, REQUEST_CODE_PURCHASE);
            
            // Send success response immediately
            callbackContext.success("Purchase initiated for product: " + productId);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to purchase trading account", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to purchase trading account: " + e.getMessage());
        }
    }
    
    /**
     * Get available trading account products
     */
    private void getAvailableProducts(CallbackContext callbackContext) {
        try {
            JSONArray products = new JSONArray();
            JSONObject starter = new JSONObject();
            starter.put("id", "trading_account_1000");
            starter.put("name", "Starter Trading Account");
            starter.put("description", "Start your trading journey with $1,000 account balance");
            starter.put("accountBalance", 1000.0);
            starter.put("price", 9.99);
            starter.put("currency", "USD");
            products.put(starter);
            
            JSONObject standard = new JSONObject();
            standard.put("id", "trading_account_5000");
            standard.put("name", "Standard Trading Account");
            standard.put("description", "Professional trading with $5,000 account balance");
            standard.put("accountBalance", 5000.0);
            standard.put("price", 39.99);
            standard.put("currency", "USD");
            products.put(standard);
            
            JSONObject premium = new JSONObject();
            premium.put("id", "trading_account_10000");
            premium.put("name", "Premium Trading Account");
            premium.put("description", "Advanced trading with $10,000 account balance");
            premium.put("accountBalance", 10000.0);
            premium.put("price", 69.99);
            premium.put("currency", "USD");
            products.put(premium);
            
            callbackContext.success(products);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to get available products", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to get available products: " + e.getMessage());
        }
    }
    

    private void checkPurchaseStatus(JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            JSONObject params = args.getJSONObject(0);
            String productId = params.getString("productId");
            
            // In a real implementation, this would check against the billing system
            // For now, return a mock response
            JSONObject result = new JSONObject();
            result.put("productId", productId);
            result.put("isPurchased", false);
            result.put("purchaseDate", null);
            
            callbackContext.success(result);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to check purchase status", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to check purchase status: " + e.getMessage());
        }
    }
    
    /**
     * Log custom events to all analytics services
     */
    private void logEvent(JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            JSONObject params = args.getJSONObject(0);
            String eventName = params.getString("eventName");
            JSONObject eventParams = params.optJSONObject("parameters");
            
            // Report event to all analytics services
            reportEventToAllServices(eventName, eventParams);
            
            callbackContext.success("Event logged successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to log event", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to log event: " + e.getMessage());
        }
    }
    
    /**
     * Report event to all analytics services
     */
    private void reportEventToAllServices(String eventName, JSONObject eventParams) {
        try {
            // Facebook Analytics
            if (facebookLogger != null && eventParams != null) {
                Bundle facebookParams = new Bundle();
//                for (String key : eventParams.keySet()) {
//                    Object value = eventParams.get(key);
//                    switch (value) {
//                        case String s -> facebookParams.putString(key, s);
//                        case Double v -> facebookParams.putDouble(key, v);
//                        case Integer i -> facebookParams.putInt(key, i);
//                        default -> {
//                        }
//                    }
//                }
                facebookLogger.logEvent(eventName, facebookParams);
            }
            
            // Firebase Analytics
            if (firebaseAnalytics != null && eventParams != null) {
                Bundle firebaseParams = new Bundle();
//                for (String key : eventParams.keySet()) {
//                    Object value = eventParams.get(key);
//                    switch (value) {
//                        case String s -> firebaseParams.putString(key, s);
//                        case Double v -> firebaseParams.putDouble(key, v);
//                        case Integer i -> firebaseParams.putInt(key, i);
//                        default -> {
//                        }
//                    }
//                }
                firebaseAnalytics.logEvent(eventName, firebaseParams);
            }
            
            // AppsFlyer
            if (eventParams != null) {
                Map<String, Object> appsFlyerParams = new HashMap<>();
//                for (String key : eventParams.keySet()) {
//                    appsFlyerParams.put(key, eventParams.get(key));
//                }
                AppsFlyerLib.getInstance().logEvent(cordova.getActivity(), eventName, appsFlyerParams);
            }
            
            Log.d(TAG, "Event '" + eventName + "' reported to all analytics services");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to report event to analytics services", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (requestCode == REQUEST_CODE_PURCHASE && purchaseCallback != null) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    // Purchase successful
                    String productId = intent.getStringExtra("purchased_product_id");
                    String productName = intent.getStringExtra("purchased_product_name");
                    double accountBalance = intent.getDoubleExtra("account_balance", 0);
                    
                    JSONObject result = new JSONObject();
                    result.put("success", true);
                    result.put("productId", productId);
                    result.put("productName", productName);
                    result.put("accountBalance", accountBalance);
                    result.put("message", "Purchase completed successfully");
                    
                    purchaseCallback.success(result);
                    
                    // Log purchase success event
                    JSONObject eventParams = new JSONObject();
                    eventParams.put("product_id", productId);
                    eventParams.put("product_name", productName);
                    eventParams.put("account_balance", accountBalance);
                    reportEventToAllServices("cordova_purchase_completed", eventParams);
                    
                } else {
                    // Purchase cancelled or failed
                    JSONObject result = new JSONObject();
                    result.put("success", false);
                    result.put("message", "Purchase was cancelled or failed");
                    
                    purchaseCallback.error(result);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error handling purchase result", e);
                FirebaseCrashlytics.getInstance().recordException(e);
                purchaseCallback.error("Error handling purchase result: " + e.getMessage());
            }
            
            // Clear callback
            purchaseCallback = null;
        }
    }
}
