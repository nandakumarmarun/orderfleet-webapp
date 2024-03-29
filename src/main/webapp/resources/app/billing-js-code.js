// Create a StaticJsCode object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.StaticJsCode) {
	this.StaticJsCode = {};
}

(function() {
	'use strict';

	var staticJsCodeContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		
		$('.selectpicker').selectpicker();

		$('#myFormSubmit').on('click', function() {
			createUpdateStaticJsCode();
		});
	});

	function createUpdateStaticJsCode() {
		var companyPid = $('#dbCompany').val();
		if (companyPid == "no") {
			addErrorAlert("Please select company and document");
			return;
		}
		var jsCode = $('#txtJsCode').val();
		if (jsCode == "") {
			addErrorAlert("Please enter JS Code");
			return;
		}
		$.ajax({
			method : 'POST',
			url : staticJsCodeContextPath + "/save",
			data : {
				companyPid : companyPid,
				jsCode : jsCode
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	StaticJsCode.onChangeDocument = function() {
		var companyPid = $('#dbCompany').val();
		$('#txtJsCode').val('');
		if (companyPid == "no") {
			return;
		}
		$('#txtJsCode').val('JS Code loading...');
		$.ajax({
			url : staticJsCodeContextPath + "/change-document/" + companyPid,
			method : 'GET',
			success : function(jsCode) {
				console.log("jsCode");
				console.log(jsCode);
				$('#txtJsCode').val(jsCode);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = staticJsCodeContextPath;
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