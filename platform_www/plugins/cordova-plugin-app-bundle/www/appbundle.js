cordova.define("cordova-plugin-app-bundle.appbundle", function(require, exports, module) {
var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'AppBundle', 'coolMethod', [arg0]);
};

});
