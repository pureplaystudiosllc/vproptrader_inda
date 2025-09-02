package com.futureharvest.vproptrader;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TradingAccountViewModel extends ViewModel {
    private final MutableLiveData<List<BillingManager.TradingAccountProduct>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    /**
     * Get observable products list
     */
    public LiveData<List<BillingManager.TradingAccountProduct>> getProducts() {
        return products;
    }
    
    /**
     * Get loading state
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * Get error message
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Update products list
     */
    public void updateProducts(List<BillingManager.TradingAccountProduct> newProducts) {
        if (newProducts != null) {
            products.setValue(newProducts);
            errorMessage.setValue(null);
        }
    }
    
    /**
     * Set loading state
     */
    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
    
    /**
     * Set error message
     */
    public void setError(String error) {
        errorMessage.setValue(error);
    }
    
    /**
     * Clear error message
     */
    public void clearError() {
        errorMessage.setValue(null);
    }
    
    /**
     * Get product by ID
     */
    public BillingManager.TradingAccountProduct getProductById(String productId) {
        List<BillingManager.TradingAccountProduct> currentProducts = products.getValue();
        if (currentProducts != null) {
            for (BillingManager.TradingAccountProduct product : currentProducts) {
                if (product.getProductId().equals(productId)) {
                    return product;
                }
            }
        }
        return null;
    }
    
    /**
     * Check if product exists
     */
    public boolean hasProduct(String productId) {
        return getProductById(productId) != null;
    }
    
    /**
     * Get products count
     */
    public int getProductsCount() {
        List<BillingManager.TradingAccountProduct> currentProducts = products.getValue();
        return currentProducts != null ? currentProducts.size() : 0;
    }
}
