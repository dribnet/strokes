// patch selection.data to allow optional filter function on 1st param
(function () {
    var orig_data = d3.selection.prototype.data;
    d3.selection.prototype.data = function () {
        // if a filter has been installed and it should be called...
        if(d3.selection.prototype.hasOwnProperty("dataToArray") &&
                arguments.length > 0) {
            //console.log("Fixing stray : " + typeof arguments[0]);
            //console.log(typeof d3.selection.prototype.dataToArray);
            arguments[0] = d3.selection.prototype.dataToArray(arguments[0]);
        }
        // console.log("Filter installed");
        // console.log(arguments);
        return orig_data.apply(this, Array.prototype.slice.call(arguments));
    };
}());

                // typeof arguments[0] !== "function" &&
                // Object.prototype.toString.call( someVar ) !== '[object Array]') {
