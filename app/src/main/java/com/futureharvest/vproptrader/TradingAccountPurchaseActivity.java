package com.futureharvest.vproptrader;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.facebook.appevents.AppEventsLogger;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TradingAccountPurchaseActivity extends ComponentActivity implements BillingManager.BillingManagerListener {
    private static final String TAG = "TradingAccountPurchase";
    
    private BillingManager billingManager;
    private AppEventsLogger facebookLogger;
    private FirebaseAnalytics firebaseAnalytics;
    private TradingAccountViewModel viewModel;
    private String selectedProductId;
    private final ExecutorService verifyExecutor = Executors.newSingleThreadExecutor();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initializeAnalytics();
        billingManager = new BillingManager(this, this);
        viewModel = new ViewModelProvider(this).get(TradingAccountViewModel.class);
        
        selectedProductId = getIntent().getStringExtra("product_id");
        
        loadTradingAccountProducts();
        
        if (selectedProductId != null) {
            onProductSelected(billingManager.getTradingAccountProduct(selectedProductId));
        }
    }
    
    private void initializeAnalytics() {
        try {
            facebookLogger = AppEventsLogger.newLogger(this);
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
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
            if (product == null) {
                Log.e(TAG, "Product not found");
                return;
            }
            Log.d(TAG, "Selected product: " + product.getName());
            
            reportProductViewedEvent(product);
            billingManager.queryProductDetails(product.getProductId());
        } catch (Exception e) {
            Log.e(TAG, "Error handling product selection", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(this, "Error selecting product", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void reportProductViewedEvent(BillingManager.TradingAccountProduct product) {
        try {
            if (facebookLogger != null) {
                Bundle facebookParams = new Bundle();
                facebookParams.putString("product_id", product.getProductId());
                facebookParams.putString("product_name", product.getName());
                facebookParams.putDouble("price", product.getPrice());
                facebookParams.putString("currency", product.getPriceCurrency());
                facebookParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                facebookLogger.logEvent("product_viewed", facebookParams);
            }
            
            if (firebaseAnalytics != null) {
                Bundle firebaseParams = new Bundle();
                firebaseParams.putString("product_id", product.getProductId());
                firebaseParams.putString("product_name", product.getName());
                firebaseParams.putDouble("price", product.getPrice());
                firebaseParams.putString("currency", product.getPriceCurrency());
                firebaseParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                firebaseAnalytics.logEvent("product_viewed", firebaseParams);
            }
            
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
                ProductDetails productDetails = productDetailsList.get(0);
                Log.d(TAG, "Product details loaded: " + productDetails.getProductId());
                
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
            
            reportPurchaseSuccessEvent(purchase, product);
            
            // Server-side verification hook (non-blocking with timeout)
            Future<Boolean> verifyFuture = verifyExecutor.submit(() -> verifyPurchaseOnServer(
                getPackageName(),
                product.getProductId(),
                purchase.getPurchaseToken()
            ));
            try {
                Boolean verified = verifyFuture.get(3, TimeUnit.SECONDS);
                Log.d(TAG, "Server verification result: " + verified);
            } catch (Exception e) {
                Log.w(TAG, "Server verification timeout or error", e);
            }
            
            String message = String.format("Congratulations! You have successfully purchased %s", product.getName());
            runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
            
            Intent resultIntent = new Intent();
            resultIntent.putExtra("purchased_product_id", product.getProductId());
            resultIntent.putExtra("purchased_product_name", product.getName());
            resultIntent.putExtra("account_balance", product.getAccountBalance());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error handling purchase success", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    private boolean verifyPurchaseOnServer(String packageName, String productId, String purchaseToken) {
        try {
            // TODO: Replace with real HTTP call to your backend API.
            // The backend should validate the purchase via Google Play Developer API.
            // Keep this stub simple and safe.
            Log.d(TAG, "Verifying purchase on server: " + productId);
            // Simulate success
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Server verification failed", e);
            return false;
        }
    }
    
    private void reportPurchaseSuccessEvent(Purchase purchase, BillingManager.TradingAccountProduct product) {
        try {
            if (facebookLogger != null) {
                Bundle facebookParams = new Bundle();
                facebookParams.putString("product_id", product.getProductId());
                facebookParams.putString("product_name", product.getName());
                facebookParams.putDouble("price", product.getPrice());
                facebookParams.putString("currency", product.getPriceCurrency());
                facebookParams.putString("purchase_token", purchase.getPurchaseToken());
                facebookParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                facebookParams.putString("purchase_time", String.valueOf(purchase.getPurchaseTime()));
                facebookLogger.logEvent("purchase_completed", facebookParams);
            }
            
            if (firebaseAnalytics != null) {
                Bundle firebaseParams = new Bundle();
                firebaseParams.putString("product_id", product.getProductId());
                firebaseParams.putString("product_name", product.getName());
                firebaseParams.putDouble("price", product.getPrice());
                firebaseParams.putString("currency", product.getPriceCurrency());
                firebaseParams.putString("purchase_token", purchase.getPurchaseToken());
                firebaseParams.putString("account_balance", String.valueOf(product.getAccountBalance()));
                firebaseParams.putLong("purchase_time", purchase.getPurchaseTime());
                firebaseAnalytics.logEvent("purchase_completed", firebaseParams);
            }
            
            Map<String, Object> appsFlyerParams = new HashMap<>();
            appsFlyerParams.put("product_id", product.getProductId());
            appsFlyerParams.put("product_name", product.getName());
            appsFlyerParams.put("price", product.getPrice());
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
    
    public void onPurchaseError(BillingResult billingResult) {
        try {
            Log.e(TAG, "Purchase failed: " + billingResult.getDebugMessage());
            
            reportPurchaseFailureEvent(billingResult);
            
            String errorMessage = "Purchase failed: " + billingResult.getDebugMessage();
            runOnUiThread(() -> Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            Log.e(TAG, "Error handling purchase error", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    
    private void reportPurchaseFailureEvent(BillingResult billingResult) {
        try {
            if (facebookLogger != null) {
                Bundle facebookParams = new Bundle();
                facebookParams.putString("error_code", String.valueOf(billingResult.getResponseCode()));
                facebookParams.putString("error_message", billingResult.getDebugMessage());
                facebookParams.putString("error_domain", "billing");
                facebookLogger.logEvent("purchase_failed", facebookParams);
            }
            
            if (firebaseAnalytics != null) {
                Bundle firebaseParams = new Bundle();
                firebaseParams.putString("error_code", String.valueOf(billingResult.getResponseCode()));
                firebaseParams.putString("error_message", billingResult.getDebugMessage());
                firebaseParams.putString("error_domain", "billing");
                firebaseAnalytics.logEvent("purchase_failed", firebaseParams);
            }
            
            Map<String, Object> appsFlyerParams = new HashMap<>();
            appsFlyerParams.put("error_code", billingResult.getResponseCode());
            appsFlyerParams.put("error_message", billingResult.getDebugMessage());
            appsFlyerParams.put("error_domain", "billing");
            AppsFlyerLib.getInstance().logEvent(this, "purchase_failed", appsFlyerParams);
            
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
    
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        try {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully");
                
                if (firebaseAnalytics != null) {
                    Bundle params = new Bundle();
                    params.putString("status", "success");
                    firebaseAnalytics.logEvent("purchase_acknowledged", params);
                }
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: " + billingResult.getDebugMessage());
                
                if (firebaseAnalytics != null) {
                    Bundle params = new Bundle();
                    params.putString("status", "failed");
                    params.putString("error_code", String.valueOf(billingResult.getResponseCode()));
                    params.putString("error_message", billingResult.getDebugMessage());
                    firebaseAnalytics.logEvent("purchase_acknowledged", params);
                }
                
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
            
            if (billingManager != null) {
                billingManager.destroy();
            }
            verifyExecutor.shutdown();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
