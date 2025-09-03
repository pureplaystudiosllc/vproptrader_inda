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
    private final MutableLiveData<BillingManager.TradingAccountProduct> selectedProduct = new MutableLiveData<>();
    
    public LiveData<List<BillingManager.TradingAccountProduct>> getProducts() {
        return products;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<BillingManager.TradingAccountProduct> getSelectedProduct() {
        return selectedProduct;
    }
    
    public void updateProducts(List<BillingManager.TradingAccountProduct> newProducts) {
        if (newProducts != null) {
            products.setValue(newProducts);
            errorMessage.setValue(null);
        }
    }
    
    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
    
    public void setError(String error) {
        errorMessage.setValue(error);
    }
    
    public void clearError() {
        errorMessage.setValue(null);
    }
    
    public void selectProduct(BillingManager.TradingAccountProduct product) {
        selectedProduct.setValue(product);
    }
    
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
    
    public boolean hasProduct(String productId) {
        return getProductById(productId) != null;
    }
    
    public int getProductsCount() {
        List<BillingManager.TradingAccountProduct> currentProducts = products.getValue();
        return currentProducts != null ? currentProducts.size() : 0;
    }
    
    public List<BillingManager.TradingAccountProduct> getProductsByPriceRange(double minPrice, double maxPrice) {
        List<BillingManager.TradingAccountProduct> currentProducts = products.getValue();
        List<BillingManager.TradingAccountProduct> filteredProducts = new ArrayList<>();
        
        if (currentProducts != null) {
            for (BillingManager.TradingAccountProduct product : currentProducts) {
                if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                    filteredProducts.add(product);
                }
            }
        }
        
        return filteredProducts;
    }
    
    public List<BillingManager.TradingAccountProduct> getProductsByAccountBalance(double minBalance, double maxBalance) {
        List<BillingManager.TradingAccountProduct> currentProducts = products.getValue();
        List<BillingManager.TradingAccountProduct> filteredProducts = new ArrayList<>();
        
        if (currentProducts != null) {
            for (BillingManager.TradingAccountProduct product : currentProducts) {
                if (product.getAccountBalance() >= minBalance && product.getAccountBalance() <= maxBalance) {
                    filteredProducts.add(product);
                }
            }
        }
        
        return filteredProducts;
    }
    
    public void sortProductsByPrice(boolean ascending) {
        List<BillingManager.TradingAccountProduct> currentProducts = products.getValue();
        if (currentProducts != null) {
            List<BillingManager.TradingAccountProduct> sortedProducts = new ArrayList<>(currentProducts);
            if (ascending) {
                sortedProducts.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
            } else {
                sortedProducts.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
            }
            products.setValue(sortedProducts);
        }
    }
    
    public void sortProductsByAccountBalance(boolean ascending) {
        List<BillingManager.TradingAccountProduct> currentProducts = products.getValue();
        if (currentProducts != null) {
            List<BillingManager.TradingAccountProduct> sortedProducts = new ArrayList<>(currentProducts);
            if (ascending) {
                sortedProducts.sort((p1, p2) -> Double.compare(p1.getAccountBalance(), p2.getAccountBalance()));
            } else {
                sortedProducts.sort((p1, p2) -> Double.compare(p2.getAccountBalance(), p1.getAccountBalance()));
            }
            products.setValue(sortedProducts);
        }
    }
}
