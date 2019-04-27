// Create a ProductNameText object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

(function() {
	'use strict';

	var accountNameTextSettingsContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$('#btnSave').on('click', function() {
			saveAccountNameText();
		});

		$('#btnDefValues').on('click', function() {
			saveDefaultValues();
		});

		var rowCount = $('#tbl_AccountText tr').length;
		if (rowCount == 1) {
			$('#btnDefValues').hide();
		}

	});

	function saveDefaultValues() {
		console.log("context :"+accountNameTextSettingsContextPath);
		$.ajax({
			url : accountNameTextSettingsContextPath + "/defValues",
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

	function saveAccountNameText() {

		var accountNameTextSettings = [];

		$.each($("input[name='accountNameText']"), function() {
			var pid = $(this).val();
			var enabled = $(this).prop('checked');
			accountNameTextSettings.push({
				pid : pid,
				enabled : enabled
			})
		});
		console.log(accountNameTextSettings);
		if (accountNameTextSettings.length == 0) {
			return;
		}
		$.ajax({
			url : accountNameTextSettingsContextPath,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountNameTextSettings),
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
		window.location = accountNameTextSettingsContextPath;
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