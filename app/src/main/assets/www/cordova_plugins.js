cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "cordova-plugin-device.device",
      "file": "plugins/cordova-plugin-device/www/device.js",
      "pluginId": "cordova-plugin-device",
      "clobbers": [
        "device"
      ]
    },
    {
      "id": "cordova-plugin-trading-account.TradingAccount",
      "file": "plugins/cordova-plugin-trading-account/www/trading-account.js",
      "pluginId": "cordova-plugin-trading-account",
      "clobbers": [
        "TradingAccount"
      ]
    }
  ];
  module.exports.metadata = {
    "cordova-plugin-device": "3.0.0",
    "cordova-plugin-trading-account": "1.0.0"
  };
});