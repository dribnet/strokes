/**
 * Provides ArrayLike support for for libraries that verify an object
 * is an Array via either Object.prototype.toString or Array.isArray.
 *
 * For more information: https://github.com/dribnet/ArrayLike.js
 */

// this method patches (or implements) Array.isArray
// and stores the original function so it can be restored
(function(ar) {
  var isArray = ar.isArray;
  // toString is only used if Array.isArray does not exist
  var toString = Object.prototype.toString;
  // ar["$originalIsArray"] = isArray;
  ar.isArray = function(obj) {
    var that = this;
    if(obj && obj.__ArrayLike) {
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
  // op["$originalToString"] = toString;
  op.toString = function(args) {
    var that = this;
    if(that && that.__ArrayLike) {
      return '[object Array]';
    }
    else {
      return toString.call(that);
    }
  }
})(Object.prototype);

