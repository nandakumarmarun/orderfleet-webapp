// Create a ProductNameText object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

(function() {
	'use strict';

	var productNameTextSettingsContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$('#btnSave').on('click', function() {
			saveProductNameText();
		});

		$('#btnDefValues').on('click', function() {
			saveDefaultValues();
		});

		var rowCount = $('#tbl_ProdText tr').length;
		if (rowCount == 4) {
			$('#btnDefValues').hide();
		}

	});

	function saveDefaultValues() {
		$.ajax({
			url : productNameTextSettingsContextPath + "/defValues",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			success : function(status) {
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveProductNameText() {

		var productNameTextSettings = [];

		$.each($("input[name='productNameText']"), function() {
			var pid = $(this).val();
			var enabled = $(this).prop('checked');
			productNameTextSettings.push({
				pid : pid,
				enabled : enabled
			})
		});
		console.log(productNameTextSettings);
		if (productNameTextSettings.length == 0) {
			return;
		}
		$.ajax({
			url : productNameTextSettingsContextPath,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(productNameTextSettings),
			success : function(status) {
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = productNameTextSettingsContextPath;
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