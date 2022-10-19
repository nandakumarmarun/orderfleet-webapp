if (!this.ReleaseInfo) {
	this.ReleaseInfo = {};
}

(function() {
	'use strict';

	var releaseInfoPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		console.log("entered*************" + releaseInfoPath)
		ReleaseInfo.getData();

	});

	ReleaseInfo.getData = function() {

		console.log("entering here ")

		$.ajax({
			url : releaseInfoPath + "/Release-data",
			type : 'GET',

			success : function(data) {

				$('#bodyInfo').append(
						"<tr><td><h3><label style='font-style:bold'>" +"Release Number :" + "</label></h3></td>" + "<td>"+"<h3>"
								+ data.releaseNumber + "</h3></td></tr><tr><td><h3><label style='font-style:bold'>"
								+ "Release Date :" + "</label></h3></td>" + "<td>"+"<h3>"
								+ data.releaseDate+"</h3></td></tr>" + "<tr><td><h3><label style='font-style:bold'>"
								+ "Features:" + "</label></h3></td></tr>");

				$.each(data.features, function(index, details) {
					$('#bodyInfo').append("<hr><tr><td><h4>" + details +"</h4></td></tr>");
				});
				
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = releaseInfoPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = releaseInfoPath;
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function onError(httpResponse, exception) {
		var i;
		switch (httpResponse.status) {
		// connection refused, server not reachable
		case 0:
			addErrorAlert('Server not reachable', 'error.server.not.reachable');
			break;
		case 400:
			var errorHeader = httpResponse
					.getResponseHeader('X-orderfleetwebApp-error');
			var entityKey = httpResponse
					.getResponseHeader('X-orderfleetwebApp-params');
			if (errorHeader) {
				var entityName = entityKey;
				addErrorAlert(errorHeader, errorHeader, {
					entityName : entityName
				});
			} else if (httpResponse.responseText) {
				var data = JSON.parse(httpResponse.responseText);
				if (data && data.fieldErrors) {
					for (i = 0; i < data.fieldErrors.length; i++) {
						var fieldError = data.fieldErrors[i];
						var convertedField = fieldError.field.replace(
								/\[\d*\]/g, '[]');
						var fieldName = convertedField.charAt(0).toUpperCase()
								+ convertedField.slice(1);
						addErrorAlert(
								'Field ' + fieldName + ' cannot be empty',
								'error.' + fieldError.message, {
									fieldName : fieldName
								});
					}
				} else if (data && data.message) {
					addErrorAlert(data.message, data.message, data);
				} else {
					addErrorAlert(data);
				}
			} else {
				addErrorAlert(exception);
			}
			break;
		default:
			if (httpResponse.responseText) {
				var data = JSON.parse(httpResponse.responseText);
				if (data && data.description) {
					addErrorAlert(data.description);
				} else if (data && data.message) {
					addErrorAlert(data.message);
				} else {
					addErrorAlert(data);
				}
			} else {
				addErrorAlert(exception);
			}
		}
	}
})();