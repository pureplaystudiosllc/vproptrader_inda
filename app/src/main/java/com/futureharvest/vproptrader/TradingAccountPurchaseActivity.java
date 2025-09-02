package com.futureharvest.vproptrader;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.facebook.appevents.AppEventsLogger;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trading Account Purchase Activity
 * Handles the purchase of different trading account tiers
 * Implements Google Play Billing Library 6.1.0 standards
 * Reports events to AppsFlyer, Facebook, and Firebase
 */
public class TradingAccountPurchaseActivity extends ComponentActivity implements BillingManager.BillingManagerListener {
    private static final String TAG = "TradingAccountPurchase";
    
    private BillingManager billingManager;
    private AppEventsLogger facebookLogger;
    private FirebaseAnalytics firebaseAnalytics;
    private TradingAccountViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize analytics services
        initializeAnalytics();
        
        // Initialize billing manager
        billingManager = new BillingManager(this, this);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TradingAccountViewModel.class);
        
        // Set Compose content - this will call the Kotlin Compose function
//        setContent(() -> TradingAccountPurchaseScreen(
//            viewModel,
//            this::onProductSelected,
//            this::finish
//        ));
        
        // Load available trading account products
        loadTradingAccountProducts();
    }
    
    /**
     * Initialize all analytics services
     */
    private void initializeAnalytics() {
        try {
            // Initialize Facebook logger
            facebookLogger = AppEventsLogger.newLogger(this);
            
            // Initialize Firebase Analytics
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            
            // AppsFlyer is automatically initialized in Application class
            Log.d(TAG, "Analytics services initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize analytics services", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    private void loadTradingAccountProducts() {
        try {
            List<BillingManager.TradingAccountProduct> products = billingManager.getAvailableTradingAccountProducts();
            viewModel.updateProducts(products);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load trading account products", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    private void onProductSelected(BillingManager.TradingAccountProduct product) {
        try {
            Log.d(TAG, "Selected product: " + product.getName());
            
            // Report product view event to all analytics services
            reportProductViewedEvent(product);
            
            // Query product details and launch billing flow
            billingManager.queryProductDetails(product.getProductId());
        } catch (Exception e) {
            Log.e(TAG, "Error handling product selection", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(this, "Error selecting product", Toast.LENGTH_SHORT).show();
        }
    }
    

    private void reportProductViewedEvent(BillingManager.TradingAccountProduct product) {
        try {
            // Facebook Analytics
            if (facebookLogger != null) {
                Bundle facebookParams = new Bundle();
                facebookParams.putString("product_id", product.getProductId());
                facebookParams.putString("product_name", product.getName());
                facebookParams.putDouble("price", product.getPrice());
                facebookParams.putString("currency", product.getPriceCurrency());
                facebookParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                facebookLogger.logEvent("product_viewed", facebookParams);
            }
            
            // Firebase Analytics
            if (firebaseAnalytics != null) {
                Bundle firebaseParams = new Bundle();
                firebaseParams.putString("product_id", product.getProductId());
                firebaseParams.putString("product_name", product.getName());
                firebaseParams.putDouble("price", product.getPrice());
                firebaseParams.putString("currency", product.getPriceCurrency());
                firebaseParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                firebaseAnalytics.logEvent("product_viewed", firebaseParams);
            }
            
            // AppsFlyer
            Map<String, Object> appsFlyerParams = new HashMap<>();
            appsFlyerParams.put("product_id", product.getProductId());
            appsFlyerParams.put("product_name", product.getName());
            appsFlyerParams.put("price", product.getPrice());
            appsFlyerParams.put("currency", product.getPriceCurrency());
            appsFlyerParams.put("account_balance", product.getAccountBalance());
            AppsFlyerLib.getInstance().logEvent(this, "product_viewed", appsFlyerParams);
            
            Log.d(TAG, "Product viewed event reported to all analytics services");
        } catch (Exception e) {
            Log.e(TAG, "Failed to report product viewed event", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    

    
    @Override
    public void onBillingClientReady() {
        Log.d(TAG, "Billing client is ready");
        runOnUiThread(() -> Toast.makeText(this, "Billing service ready", Toast.LENGTH_SHORT).show());
    }
    
    @Override
    public void onProductDetailsLoaded(List<ProductDetails> productDetailsList) {
        try {
            if (productDetailsList != null && !productDetailsList.isEmpty()) {
                ProductDetails productDetails = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    productDetails = productDetailsList.getFirst();
                }
                assert productDetails != null;
                Log.d(TAG, "Product details loaded: " + productDetails.getProductId());
                
                // Launch billing flow
                billingManager.launchBillingFlow(this, productDetails);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling product details loaded", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    public void onPurchaseSuccess(Purchase purchase, BillingManager.TradingAccountProduct product) {
        try {
            Log.d(TAG, "Purchase successful: " + product.getName());
            
            // Report purchase success event to all analytics services
            reportPurchaseSuccessEvent(purchase, product);
            
            // Show success message
            String message = String.format("Congratulations! You have successfully purchased %s", product.getName());
            runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
            
            // Handle post-purchase logic here
            // For example: unlock trading account, update user status, etc.
            
            // Return result
            Intent resultIntent = new Intent();
//            resultIntent.putExtra("purchased_product_id", purchase.getProductId());
            resultIntent.putExtra("purchased_product_name", product.getName());
            resultIntent.putExtra("account_balance", product.getAccountBalance());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error handling purchase success", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    /**
     * Report purchase success event to all analytics services
     */
    private void reportPurchaseSuccessEvent(Purchase purchase, BillingManager.TradingAccountProduct product) {
        try {
            // Facebook Analytics
            if (facebookLogger != null) {
                Bundle facebookParams = new Bundle();
//                facebookParams.putString("product_id", purchase.getProductId());
                facebookParams.putString("product_name", product.getName());
                facebookParams.putDouble("price", product.getPrice());
                facebookParams.putString("currency", product.getPriceCurrency());
                facebookParams.putString("purchase_token", purchase.getPurchaseToken());
                facebookParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                facebookParams.putString("purchase_time", String.valueOf(purchase.getPurchaseTime()));
                facebookLogger.logEvent("purchase_completed", facebookParams);
            }
            
            // Firebase Analytics
            if (firebaseAnalytics != null) {
                Bundle firebaseParams = new Bundle();
//                firebaseParams.putString("product_id", purchase.getProductId());
                firebaseParams.putString("product_name", product.getName());
                firebaseParams.putDouble("price", product.getPrice());
                firebaseParams.putString("currency", product.getPriceCurrency());
                firebaseParams.putString("purchase_token", purchase.getPurchaseToken());
                firebaseParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                firebaseParams.putLong("purchase_time", purchase.getPurchaseTime());
                firebaseAnalytics.logEvent("purchase_completed", firebaseParams);
            }
            
            // AppsFlyer
            Map<String, Object> appsFlyerParams = new HashMap<>();
//            appsFlyerParams.put("product_id", purchase.getProductId());
            appsFlyerParams.put("product_name", product.getName());
//            appsFlyerParams.put("price", purchase.getPrice());
            appsFlyerParams.put("currency", product.getPriceCurrency());
            appsFlyerParams.put("purchase_token", purchase.getPurchaseToken());
            appsFlyerParams.put("account_balance", product.getAccountBalance());
            appsFlyerParams.put("purchase_time", purchase.getPurchaseTime());
            AppsFlyerLib.getInstance().logEvent(this, "purchase_completed", appsFlyerParams);
            
            Log.d(TAG, "Purchase success event reported to all analytics services");
        } catch (Exception e) {
            Log.e(TAG, "Failed to report purchase success event", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    public void onPurchaseError(com.android.billingclient.api.BillingResult billingResult) {
        try {
            Log.e(TAG, "Purchase failed: " + billingResult.getDebugMessage());
            
            // Report purchase failure event to all analytics services
            reportPurchaseFailureEvent(billingResult);
            
            // Show error message
            String errorMessage = "Purchase failed: " + billingResult.getDebugMessage();
            runOnUiThread(() -> Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            Log.e(TAG, "Error handling purchase error", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    /**
     * Report purchase failure event to all analytics services
     */
    private void reportPurchaseFailureEvent(com.android.billingclient.api.BillingResult billingResult) {
        try {
            // Facebook Analytics
            if (facebookLogger != null) {
                Bundle facebookParams = new Bundle();
                facebookParams.putString("error_code", String.valueOf(billingResult.getResponseCode()));
                facebookParams.putString("error_message", billingResult.getDebugMessage());
                facebookParams.putString("error_domain", "billing");
                facebookLogger.logEvent("purchase_failed", facebookParams);
            }
            
            // Firebase Analytics
            if (firebaseAnalytics != null) {
                Bundle firebaseParams = new Bundle();
                firebaseParams.putString("error_code", String.valueOf(billingResult.getResponseCode()));
                firebaseParams.putString("error_message", billingResult.getDebugMessage());
                firebaseParams.putString("error_domain", "billing");
                firebaseAnalytics.logEvent("purchase_failed", firebaseParams);
            }
            
            // AppsFlyer
            Map<String, Object> appsFlyerParams = new HashMap<>();
            appsFlyerParams.put("error_code", billingResult.getResponseCode());
            appsFlyerParams.put("error_message", billingResult.getDebugMessage());
            appsFlyerParams.put("error_domain", "billing");
            AppsFlyerLib.getInstance().logEvent(this, "purchase_failed", appsFlyerParams);
            
            // Log to Firebase Crashlytics for debugging
            FirebaseCrashlytics.getInstance().log("Purchase failed: " + billingResult.getDebugMessage());
            
            Log.d(TAG, "Purchase failure event reported to all analytics services");
        } catch (Exception e) {
            Log.e(TAG, "Failed to report purchase failure event", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        try {
            Log.d(TAG, "Purchases updated: " + purchases.size() + " purchases");
            
            // Report purchases updated event
            if (firebaseAnalytics != null) {
                Bundle params = new Bundle();
                params.putInt("purchase_count", purchases.size());
                firebaseAnalytics.logEvent("purchases_updated", params);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling purchases updated", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    public void onAcknowledgePurchaseResponse(com.android.billingclient.api.BillingResult billingResult) {
        try {
            if (billingResult.getResponseCode() == com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully");
                
                // Report acknowledgment success
                if (firebaseAnalytics != null) {
                    Bundle params = new Bundle();
                    params.putString("status", "success");
                    firebaseAnalytics.logEvent("purchase_acknowledged", params);
                }
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: " + billingResult.getDebugMessage());
                
                // Report acknowledgment failure
                if (firebaseAnalytics != null) {
                    Bundle params = new Bundle();
                    params.putString("status", "failed");
                    params.putString("error_code", String.valueOf(billingResult.getResponseCode()));
                    params.putString("error_message", billingResult.getDebugMessage());
                    firebaseAnalytics.logEvent("purchase_acknowledged", params);
                }
                
                // Log to Crashlytics
                FirebaseCrashlytics.getInstance().log("Purchase acknowledgment failed: " + billingResult.getDebugMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling acknowledge purchase response", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            
            // Release billing manager resources
            if (billingManager != null) {
                billingManager.destroy();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
