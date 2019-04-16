var appContextPath = location.protocol + '//' + location.host;
$.getMultiScripts = function(arr, path) {
    var _arr = $.map(arr, function(scr) {
        return $.getScript( (path||"") + scr );
    });
    _arr.push($.Deferred(function( deferred ){
        $( deferred.resolve );
    }));
    return $.when.apply($, _arr);
}

function formatDate(date, pattern) {
	if (date) {
		return moment(date).format(pattern);
	} else {
		return "";
	}
}

function getParameterByName(name, url) {
	if (!url)
		url = window.location.href;
	name = name.replace(/[\[\]]/g, "\\$&");
	var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
			.exec(url);
	if (!results)
		return null;
	if (!results[2])
		return '';
	return decodeURIComponent(results[2].replace(/\+/g, " "));
}
