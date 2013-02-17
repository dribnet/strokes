/**
 * Provides isArray.js support for for libraries that verify an object
 * is an array via either Object.prototype.toString or Array.isArray.
 *
 * For more information: https://github.com/dribnet/isArray.js
 */

// this method patches (or implements) Array.isArray
// and stores the original function so it can be restored
(function(ar) {
  var isArray = ar.isArray;
  // toString is only used if Array.isArray does not exist
  var toString = Object.prototype.toString;
  ar["$originalToString"] = isArray;
  ar.isArray = function(obj) {
    var that = this;
    if(obj && obj.isArray === '[object Array]') {
      return true;
    }
    else if (isArray) {
      return isArray.call(that, obj);
    }
    else {
      // fallback - polyfill the Array.isArray method
      return toString.call(obj) === "[object Array]";
    }
  }
})(Array);

// this method patches Object.prototype.toString
// and stores the original function so it can be restored
(function(op) {
  var toString = op.toString;
  op["$originalToString"] = toString;
  op.toString = function(args) {
    var that = this;
    if(that && that.isArray === '[object Array]') {
      return '[object Array]';
    }
    else {
      return toString.call(that);
    }
  }
})(Object.prototype);

