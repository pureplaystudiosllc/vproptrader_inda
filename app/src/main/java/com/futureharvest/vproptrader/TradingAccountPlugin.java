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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.Bundle;

public class TradingAccountPlugin extends CordovaPlugin {
    private static final String TAG = "TradingAccountPlugin";
    
    private static final int REQUEST_CODE_PURCHASE = 1001;
    private static final int REQUEST_CODE_COMPOSE = 1002;
    private CallbackContext purchaseCallback;
    
    private AppEventsLogger facebookLogger;
    private FirebaseAnalytics firebaseAnalytics;
    private BillingManager billingManager;
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        initializeAnalytics();
        initializeBillingManager();
        
        Log.d(TAG, "TradingAccountPlugin initialized");
    }

    private void initializeAnalytics() {
        try {
            facebookLogger = AppEventsLogger.newLogger(cordova.getActivity());
            firebaseAnalytics = FirebaseAnalytics.getInstance(cordova.getActivity());
            Log.d(TAG, "Analytics services initialized in plugin");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize analytics services", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    private void initializeBillingManager() {
        try {
            billingManager = new BillingManager(cordova.getActivity(), new BillingManager.BillingManagerListener() {
                @Override
                public void onBillingClientReady() {
                    Log.d(TAG, "Billing client ready in plugin");
                }

                @Override
                public void onProductDetailsLoaded(List<com.android.billingclient.api.ProductDetails> productDetailsList) {
                    Log.d(TAG, "Product details loaded in plugin");
                }

                @Override
                public void onPurchaseSuccess(com.android.billingclient.api.Purchase purchase, BillingManager.TradingAccountProduct product) {
                    Log.d(TAG, "Purchase success in plugin: " + product.getName());
                }

                @Override
                public void onPurchaseError(com.android.billingclient.api.BillingResult billingResult) {
                    Log.e(TAG, "Purchase error in plugin: " + billingResult.getDebugMessage());
                }

                @Override
                public void onPurchasesUpdated(List<com.android.billingclient.api.Purchase> purchases) {
                    Log.d(TAG, "Purchases updated in plugin");
                }

                @Override
                public void onAcknowledgePurchaseResponse(com.android.billingclient.api.BillingResult billingResult) {
                    Log.d(TAG, "Purchase acknowledged in plugin");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize billing manager", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            return switch (action) {
                case "showTradingAccounts" -> { showTradingAccounts(callbackContext); yield true; }
                case "purchaseTradingAccount" -> { purchaseTradingAccount(args, callbackContext); yield true; }
                case "getAvailableProducts" -> { getAvailableProducts(callbackContext); yield true; }
                case "checkPurchaseStatus" -> { checkPurchaseStatus(args, callbackContext); yield true; }
                case "logEvent" -> { logEvent(args, callbackContext); yield true; }
                case "getPurchasedAccounts" -> { getPurchasedAccounts(callbackContext); yield true; }
                case "showTradingAccountsCompose" -> { showTradingAccountsCompose(callbackContext); yield true; }
                default -> { callbackContext.error("Unknown action: " + action); yield false; }
            };
        } catch (Exception e) {
            Log.e(TAG, "Error executing action: " + action, e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Plugin error: " + e.getMessage());
            return false;
        }
    }
    
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

    private void showTradingAccountsCompose(CallbackContext callbackContext) {
        try {
            purchaseCallback = callbackContext;
            Intent intent = new Intent(cordova.getActivity(), TradingAccountComposeActivity.class);
            cordova.startActivityForResult(this, intent, REQUEST_CODE_COMPOSE);
            callbackContext.success("Compose purchase interface launched");
        } catch (Exception e) {
            Log.e(TAG, "Failed to show trading accounts (compose)", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to show trading accounts (compose): " + e.getMessage());
        }
    }
    
    private void purchaseTradingAccount(JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            JSONObject params = args.getJSONObject(0);
            String productId = params.getString("productId");
            
            purchaseCallback = callbackContext;
            
            Intent intent = new Intent(cordova.getActivity(), TradingAccountPurchaseActivity.class);
            intent.putExtra("product_id", productId);
            cordova.startActivityForResult(this, intent, REQUEST_CODE_PURCHASE);
            
            callbackContext.success("Purchase initiated for product: " + productId);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to purchase trading account", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to purchase trading account: " + e.getMessage());
        }
    }
    
    private void getAvailableProducts(CallbackContext callbackContext) {
        try {
            if (billingManager != null) {
                List<BillingManager.TradingAccountProduct> products = billingManager.getAvailableTradingAccountProducts();
                JSONArray productsArray = new JSONArray();
                
                for (BillingManager.TradingAccountProduct product : products) {
                    JSONObject productObj = new JSONObject();
                    productObj.put("id", product.getProductId());
                    productObj.put("name", product.getName());
                    productObj.put("description", product.getDescription());
                    productObj.put("accountBalance", product.getAccountBalance());
                    productObj.put("price", product.getPrice());
                    productObj.put("currency", product.getPriceCurrency());
                    productsArray.put(productObj);
                }
                
                callbackContext.success(productsArray);
            } else {
                callbackContext.error("Billing manager not initialized");
            }
            
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
            
            if (billingManager != null) {
                boolean isPurchased = billingManager.isProductPurchased(productId);
                
                JSONObject result = new JSONObject();
                result.put("productId", productId);
                result.put("isPurchased", isPurchased);
                result.put("purchaseDate", null);
                
                callbackContext.success(result);
            } else {
                callbackContext.error("Billing manager not initialized");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to check purchase status", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to check purchase status: " + e.getMessage());
        }
    }
    
    private void getPurchasedAccounts(CallbackContext callbackContext) {
        try {
            if (billingManager != null) {
                List<BillingManager.TradingAccountProduct> purchasedProducts = billingManager.getUserPurchasedAccounts();
                JSONArray productsArray = new JSONArray();
                
                for (BillingManager.TradingAccountProduct product : purchasedProducts) {
                    JSONObject productObj = new JSONObject();
                    productObj.put("id", product.getProductId());
                    productObj.put("name", product.getName());
                    productObj.put("description", product.getDescription());
                    productObj.put("accountBalance", product.getAccountBalance());
                    productObj.put("price", product.getPrice());
                    productObj.put("currency", product.getPriceCurrency());
                    productsArray.put(productObj);
                }
                
                callbackContext.success(productsArray);
            } else {
                callbackContext.error("Billing manager not initialized");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to get purchased accounts", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to get purchased accounts: " + e.getMessage());
        }
    }
    
    private void logEvent(JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            JSONObject params = args.getJSONObject(0);
            String eventName = params.getString("eventName");
            JSONObject eventParams = params.optJSONObject("parameters");
            
            reportEventToAllServices(eventName, eventParams);
            
            callbackContext.success("Event logged successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to log event", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            callbackContext.error("Failed to log event: " + e.getMessage());
        }
    }
    
    private void reportEventToAllServices(String eventName, JSONObject eventParams) {
        try {
            if (facebookLogger != null && eventParams != null) {
                Bundle facebookParams = new Bundle();
                Iterator<String> keys = eventParams.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = eventParams.get(key);
                    switch (value) {
                        case String s -> facebookParams.putString(key, s);
                        case Double v -> facebookParams.putDouble(key, v);
                        case Integer i -> facebookParams.putInt(key, i);
                        default -> {
                        }
                    }
                }
                facebookLogger.logEvent(eventName, facebookParams);
            }
            
            if (firebaseAnalytics != null && eventParams != null) {
                Bundle firebaseParams = new Bundle();
                Iterator<String> keys = eventParams.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = eventParams.get(key);
                    switch (value) {
                        case String s -> firebaseParams.putString(key, s);
                        case Double v -> firebaseParams.putDouble(key, v);
                        case Integer i -> firebaseParams.putInt(key, i);
                        default -> {
                        }
                    }
                }
                firebaseAnalytics.logEvent(eventName, firebaseParams);
            }
            
            if (eventParams != null) {
                Map<String, Object> appsFlyerParams = new HashMap<>();
                Iterator<String> keys = eventParams.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    appsFlyerParams.put(key, eventParams.get(key));
                }
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
                    
                    JSONObject eventParams = new JSONObject();
                    eventParams.put("product_id", productId);
                    eventParams.put("product_name", productName);
                    eventParams.put("account_balance", accountBalance);
                    reportEventToAllServices("cordova_purchase_completed", eventParams);
                    
                } else {
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
            
            purchaseCallback = null;
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (billingManager != null) {
            billingManager.destroy();
        }
    }
}
