/**
 * Created by olatunji on 8/2/17.
 */

$.isBlank = function(obj) {
    return (!obj || $.trim(obj) === "");
};

//Check if an element exists
$.exists = function(element) {
    return typeof element !== typeof undefined && !$.isBlank(element);
};


