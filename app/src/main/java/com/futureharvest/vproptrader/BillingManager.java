package com.futureharvest.vproptrader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BillingManager {
    private static final String TAG = "BillingManager";
    
    public static final String IN_APP_PURCHASE = "inapp";
    private static final String PREFS_NAME = "BillingPrefs";
    private static final String KEY_PURCHASED_PRODUCTS = "purchased_products";
    
    private final BillingClient billingClient;
    private final ExecutorService executorService;
    private final BillingManagerListener listener;
    private final SharedPreferences sharedPreferences;
    
    private boolean isServiceConnected = false;
    
    private final Map<String, TradingAccountProduct> tradingAccountProducts;
    private final List<Purchase> userPurchases;
    
    public interface BillingManagerListener {
        void onBillingClientReady();
        void onProductDetailsLoaded(List<ProductDetails> productDetailsList);
        void onPurchaseSuccess(Purchase purchase, TradingAccountProduct product);
        void onPurchaseError(BillingResult billingResult);
        void onPurchasesUpdated(List<Purchase> purchases);
        void onAcknowledgePurchaseResponse(BillingResult billingResult);
    }
    
    public static class TradingAccountProduct {
        private final String productId;
        private final String name;
        private final String description;
        private final double accountBalance;
        private final String currency;
        private final double price;
        private final String priceCurrency;
        
        public TradingAccountProduct(String productId, String name, String description, 
                                   double accountBalance, String currency, double price, String priceCurrency) {
            this.productId = productId;
            this.name = name;
            this.description = description;
            this.accountBalance = accountBalance;
            this.currency = currency;
            this.price = price;
            this.priceCurrency = priceCurrency;
        }
        
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getAccountBalance() { return accountBalance; }
        public String getCurrency() { return currency; }
        public double getPrice() { return price; }
        public String getPriceCurrency() { return priceCurrency; }
    }
    
    public BillingManager(Context context, BillingManagerListener listener) {
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.userPurchases = new ArrayList<>();
        
        this.tradingAccountProducts = initializeTradingAccountProducts();
        
        billingClient = BillingClient.newBuilder(context)
                .setListener(this::onPurchasesUpdated)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .build();
        
        startServiceConnection();
    }
    
    private Map<String, TradingAccountProduct> initializeTradingAccountProducts() {
        Map<String, TradingAccountProduct> products = new HashMap<>();
        
        products.put("trading_account_1000", new TradingAccountProduct(
            "trading_account_1000",
            "Starter Trading Account",
            "Start your trading journey with $1,000 account balance",
            1000.0, "USD", 9.99, "USD"
        ));
        
        products.put("trading_account_5000", new TradingAccountProduct(
            "trading_account_5000",
            "Standard Trading Account",
            "Professional trading with $5,000 account balance",
            5000.0, "USD", 39.99, "USD"
        ));
        
        products.put("trading_account_10000", new TradingAccountProduct(
            "trading_account_10000",
            "Premium Trading Account",
            "Advanced trading with $10,000 account balance",
            10000.0, "USD", 69.99, "USD"
        ));
        
        products.put("trading_account_25000", new TradingAccountProduct(
            "trading_account_25000",
            "Professional Trading Account",
            "Professional trading with $25,000 account balance",
            25000.0, "USD", 149.99, "USD"
        ));
        
        products.put("trading_account_50000", new TradingAccountProduct(
            "trading_account_50000",
            "Enterprise Trading Account",
            "Enterprise-level trading with $50,000 account balance",
            50000.0, "USD", 249.99, "USD"
        ));
        
        return products;
    }
    
    public List<TradingAccountProduct> getAvailableTradingAccountProducts() {
        return new ArrayList<>(tradingAccountProducts.values());
    }
    
    public TradingAccountProduct getTradingAccountProduct(String productId) {
        return tradingAccountProducts.get(productId);
    }
    
    private void startServiceConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    isServiceConnected = true;
                    Log.d(TAG, "Billing client connected successfully");
                    listener.onBillingClientReady();
                    queryExistingPurchases();
                } else {
                    Log.e(TAG, "Billing client connection failed: " + billingResult.getDebugMessage());
                }
            }
            
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onBillingServiceDisconnected() {
                isServiceConnected = false;
                Log.d(TAG, "Billing client disconnected");
                startServiceConnection();
            }
        });
    }
    
    public void queryProductDetails(String productId) {
        if (!isServiceConnected) {
            Log.w(TAG, "Billing client not connected");
            return;
        }
        
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(IN_APP_PURCHASE)
                .build());
        
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();
        
        executorService.execute(() -> {
            billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Product details loaded successfully for: " + productId);
                    listener.onProductDetailsLoaded(productDetailsList);
                } else {
                    Log.e(TAG, "Failed to load product details: " + billingResult.getDebugMessage());
                }
            });
        });
    }
    
    public void launchBillingFlow(Activity activity, ProductDetails productDetails) {
        if (!isServiceConnected) {
            Log.w(TAG, "Billing client not connected");
            return;
        }
        
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(List.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                ))
                .build();
        
        billingClient.launchBillingFlow(activity, billingFlowParams);
    }
    
    private void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
            listener.onPurchasesUpdated(purchases);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Purchase cancelled by user");
        } else {
            Log.e(TAG, "Purchase error: " + billingResult.getDebugMessage());
            listener.onPurchaseError(billingResult);
        }
    }
    
    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                acknowledgePurchase(purchase.getPurchaseToken());
            }
            
            TradingAccountProduct product = getTradingAccountProduct(purchase.getProducts().get(0));
            if (product != null) {
                savePurchase(purchase);
                listener.onPurchaseSuccess(purchase, product);
            } else {
                Log.e(TAG, "Unknown product ID: " + purchase.getProducts());
            }
        }
    }
    
    private void acknowledgePurchase(String purchaseToken) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build();
        
        billingClient.acknowledgePurchase(params, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Purchase acknowledged successfully");
                } else {
                    Log.e(TAG, "Failed to acknowledge purchase: " + billingResult.getDebugMessage());
                }
                listener.onAcknowledgePurchaseResponse(billingResult);
            }
        });
    }
    
    private void queryExistingPurchases() {
        if (!isServiceConnected) {
            return;
        }
        
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                .setProductType(IN_APP_PURCHASE)
                .build(), (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                userPurchases.clear();
                for (Purchase purchase : purchases) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        userPurchases.add(purchase);
                        savePurchase(purchase);
                    }
                }
            }
        });
    }
    
    private void savePurchase(Purchase purchase) {
        String productId = purchase.getProducts().get(0);
        String purchaseData = purchase.getPurchaseToken() + "|" + purchase.getPurchaseTime();
        
        String existingPurchases = sharedPreferences.getString(KEY_PURCHASED_PRODUCTS, "");
        if (!existingPurchases.contains(productId)) {
            String newPurchases = existingPurchases.isEmpty() ? productId : existingPurchases + "," + productId;
            sharedPreferences.edit().putString(KEY_PURCHASED_PRODUCTS, newPurchases).apply();
        }
        
        sharedPreferences.edit().putString("purchase_" + productId, purchaseData).apply();
    }
    
    public boolean isProductPurchased(String productId) {
        String purchasedProducts = sharedPreferences.getString(KEY_PURCHASED_PRODUCTS, "");
        return purchasedProducts.contains(productId);
    }
    
    public List<TradingAccountProduct> getUserPurchasedAccounts() {
        List<TradingAccountProduct> purchasedProducts = new ArrayList<>();
        String purchasedProductIds = sharedPreferences.getString(KEY_PURCHASED_PRODUCTS, "");
        
        if (!purchasedProductIds.isEmpty()) {
            String[] productIds = purchasedProductIds.split(",");
            for (String productId : productIds) {
                TradingAccountProduct product = tradingAccountProducts.get(productId.trim());
                if (product != null) {
                    purchasedProducts.add(product);
                }
            }
        }
        
        return purchasedProducts;
    }
    
    public void destroy() {
        if (billingClient != null && isServiceConnected) {
            billingClient.endConnection();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
