/**
 * Trading Account Plugin for Cordova
 * Provides JavaScript interface for trading account purchase functionality
 */

var exec = require('cordova/exec');

/**
 * Trading Account Plugin
 */
var TradingAccount = {
    
    /**
     * Show trading accounts purchase interface
     * @param {Function} successCallback - Success callback function
     * @param {Function} errorCallback - Error callback function
     */
    showTradingAccounts: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'TradingAccount', 'showTradingAccounts', []);
    },
    
    /**
     * Purchase a specific trading account
     * @param {Object} productInfo - Product information
     * @param {string} productInfo.productId - Product ID to purchase
     * @param {Function} successCallback - Success callback function
     * @param {Function} errorCallback - Error callback function
     */
    purchaseTradingAccount: function(productInfo, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'TradingAccount', 'purchaseTradingAccount', [productInfo]);
    },
    
    /**
     * Get available trading account products
     * @param {Function} successCallback - Success callback function
     * @param {Function} errorCallback - Error callback function
     */
    getAvailableProducts: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'TradingAccount', 'getAvailableProducts', []);
    },
    
    /**
     * Check purchase status for a specific product
     * @param {Object} productInfo - Product information
     * @param {string} productInfo.productId - Product ID to check
     * @param {Function} successCallback - Success callback function
     * @param {Function} errorCallback - Error callback function
     */
    checkPurchaseStatus: function(productInfo, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'TradingAccount', 'checkPurchaseStatus', [productInfo]);
    },
    
    /**
     * Log custom events to all analytics services
     * @param {Object} eventInfo - Event information
     * @param {string} eventInfo.eventName - Event name
     * @param {Object} eventInfo.parameters - Event parameters (optional)
     * @param {Function} successCallback - Success callback function
     * @param {Function} errorCallback - Error callback function
     */
    logEvent: function(eventInfo, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'TradingAccount', 'logEvent', [eventInfo]);
    }
};

module.exports = TradingAccount;
