package com.futureharvest.vproptrader;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.facebook.appevents.AppEventsLogger;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;

public class CommonActionsPlugin extends CordovaPlugin {
    private AppEventsLogger facebookLogger;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        facebookLogger = AppEventsLogger.newLogger(cordova.getActivity());
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "shareApp":
                openCommonActionsActivity("shareApp");
                callbackContext.success();
                return true;
            case "openMore":
                openCommonActionsActivity("openMore");
                callbackContext.success();
                return true;
            case "openPrivacy":
                openCommonActionsActivity("openPrivacy");
                callbackContext.success();
                return true;
            case "openWebsiteWithToken":
                String tokenWebsite = args.getString(0);
                openCommonActionsActivity("openWebsiteWithToken", tokenWebsite);
                callbackContext.success();
                return true;
            case "openWithdrawWithToken":
                String tokenWithdraw = args.getString(0);
                openCommonActionsActivity("openWithdrawWithToken", tokenWithdraw);
                callbackContext.success();
                return true;
            case "openDepositWithToken":
                String tokenDeposit = args.getString(0);
                openCommonActionsActivity("openDepositWithToken", tokenDeposit);
                callbackContext.success();
                return true;
            case "logAppsFlyerEvent":
                try {
                    JSONObject eventData = args.getJSONObject(0);
                    String eventName = eventData.getString("eventName");
                    JSONObject eventValues = eventData.getJSONObject("eventValues");

                    HashMap<String, Object> appsFlyerParams = new HashMap<>();
                    Iterator<String> keys = eventValues.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        appsFlyerParams.put(key, eventValues.get(key));
                    }
                    AppsFlyerLib.getInstance().logEvent(
                            cordova.getActivity().getApplicationContext(),
                            eventName,
                            appsFlyerParams,
                            new AppsFlyerRequestListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d("AF-FB", "AppsFlyer event logged successfully: " + eventName);
                                    callbackContext.success("AppsFlyer event logged: " + eventName);
                                }
                                @Override
                                public void onError(int errorCode,  String errorDescription) {
                                    Log.e("AF-FB", "AppsFlyer event error: " + errorDescription + " (code: " + errorCode + ")");
                                    callbackContext.error("AppsFlyer event error: " + errorDescription);
                                }
                            }
                    );

                    String facebookEventName;
                    switch (eventName) {
                        case "af_complete_registration":
                            facebookEventName = "fb_mobile_complete_registration";
                            break;
                        case "af_initiated_checkout":
                            facebookEventName = "fb_mobile_initiated_checkout";
                            break;
                        case "af_purchase":
                            facebookEventName = "fb_mobile_purchase";
                            break;
                        default:
                            facebookEventName = eventName;
                            break;
                    }

                    Bundle facebookParams = new Bundle();
                    keys = eventValues.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        Object value = eventValues.get(key);
                        if (value instanceof String) {
                            facebookParams.putString(key, (String) value);
                        } else if (value instanceof Number) {
                            facebookParams.putDouble(key, ((Number) value).doubleValue());
                        }
                    }

                    Log.d("AF-FB", "Logging Facebook event: " + facebookEventName + ", params: " + facebookParams);


                    if ("af_purchase".equals(eventName)) {
                        double value = eventValues.optDouble("af_revenue", 0.0);
                        String currency = eventValues.optString("af_currency", "USD");
                        facebookLogger.logPurchase(BigDecimal.valueOf(value), Currency.getInstance(currency), facebookParams);
                    } else {
                        facebookLogger.logEvent(facebookEventName, facebookParams);
                    }

                    return true;
                } catch (Exception e) {
                    Log.e("AF-FB", "Error processing event: " + e.getMessage(), e);
                    callbackContext.error("Error processing event: " + e.getMessage());
                    return false;
                }
            default:
                return false;
        }
    }

    private void openCommonActionsActivity(String actionType) {
        openCommonActionsActivity(actionType, null);
    }

    private void openCommonActionsActivity(String actionType, String token) {
        Intent intent = new Intent(cordova.getActivity(), CommonActionsActivity.class);
        intent.putExtra("action_type", actionType);
        if (token != null) {
            intent.putExtra("token", token);
        }
        cordova.getActivity().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        cordova.getActivity().runOnUiThread(() -> {
            if (this.webView != null) {
                this.webView.getView().invalidate();
            }
        });
    }
}