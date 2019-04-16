// Create a PriceTrendConfiguration object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PriceTrendConfiguration) {
	this.PriceTrendConfiguration = {};
}

(function() {
	'use strict';

	var priceTrendConfigurationContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {

		$('#btnSaveConfig').on('click', function() {
			createUpdatePriceTrendConfiguration();
		});
		loadPriceTrendConfiguration();
	});

	function createUpdatePriceTrendConfiguration() {

		var priceTrendConfigurations = [];
		$.each($("input[name='price']:checked"), function() {
			var name = $(this).val();
			var value = $("#txt" + name).val();
			if (value == "") {
				value = name;
			}
			priceTrendConfigurations.push({
				name : name,
				value : value
			})
		});

		if ($('#remarks').prop('checked')) {
			priceTrendConfigurations.push({
				name : 'remarks',
				value : 'remarks'
			})
		}
		if (priceTrendConfigurations.length == 0) {
			$("#msg").text("Please select price");
			return;
		}
		console.log(priceTrendConfigurations);
		$.ajax({
			method : "POST",
			url : priceTrendConfigurationContextPath,
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(priceTrendConfigurations),
			success : function(data) {
				console.log(data);
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function loadPriceTrendConfiguration() {
		$.ajax({
			url : priceTrendConfigurationContextPath + "/edit",
			type : "GET",
			success : function(priceTrendConfigurations) {
				if (priceTrendConfigurations) {
					$.each(priceTrendConfigurations, function(index,
							priceTrendConfiguration) {
						$("#tblConfig input:checkbox[value="
										+ priceTrendConfiguration.name + "]")
								.prop("checked", true);
						$("#txt" + priceTrendConfiguration.name).val(
								priceTrendConfiguration.value);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = priceTrendConfigurationContextPath;
	}

	PriceTrendConfiguration.closeModalPopup = function(el) {
		el.modal('hide');
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