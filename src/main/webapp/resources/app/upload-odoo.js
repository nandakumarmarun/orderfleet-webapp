// Create a UploadOdoo object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.uploadOdoo) {
	this.uploadOdoo = {};
}

(function() {
	'use strict';

	var uploadOdooContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {


		$('#uploadAccountProfiles').on('click', function() {
			uploadAccountProfiles();
		});
		$('#uploadProductProfileProfiles').on('click', function() {
			uploadProductProfiles();
		});
	
	});

	function uploadAccountProfiles() {

		$(".error-msg").html("Uploading Account Profiles....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadAccountProfiles",
					method : 'GET',
					success : function(data) {
						alert("Upload Account Profile Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					success : function(data) {
						alert("Upload Account Profile Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading account profiles .................");
						$(".error-msg").html(
								"Error uploading account profiles .................");
					}
				});

	}

	function uploadProductProfiles() {

		$(".error-msg").html("Uploading Product Profiles....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadProductProfiles",
					method : 'GET',
					success : function(data) {
						alert("Upload Product Profile Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading product profiles .................");
						$(".error-msg").html(
								"Error uploading product profiles .................");
					}
				});

	}

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = uploadOdooContextPath;
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