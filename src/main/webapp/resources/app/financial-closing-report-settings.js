if (!this.SalesReportSettings) {
	this.SalesReportSettings = {};
}

(function() {
	'use strict';

	var salesReportSettingsContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#salesReportSettingsForm");
	var deleteForm = $("#deleteForm");
	var salesReportSettingsModel = {
		id : null,
		sortOrder : null,
		debitCredit : null,
		paymentMode : null,
		documentPid : null,

	};

	// Specify the validation rules
	var validationRules = {
		
			debitCredit : {
			valueNotEquals : "-1"
		},
		paymentMode : {
			valueNotEquals : "-1"
		},
		documentPid : {
			valueNotEquals : "-1"
		},
	};

	// Specify the validation error messages
	var validationMessages = {
		debitCredit : {
			valueNotEquals : "This field is required."
		},
		paymentMode : {
			valueNotEquals : "This field is required."
		},
		documentPid : {
			valueNotEquals : "This field is required."
		}
	};

	$(document).ready(
			function() {

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateSalesReportSettings(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteSalesReportSettings(e.currentTarget.action);
				});

			});


	function createUpdateSalesReportSettings(el) {
		console.log($('#field_document').val());
		salesReportSettingsModel.documentPid = $('#field_document').val();
		salesReportSettingsModel.debitCredit = $('#field_debitCredit').val();
		salesReportSettingsModel.paymentMode = $('#field_paymentMode').val();
		salesReportSettingsModel.sortOrder = $('#field_sortorder').val();
		
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(salesReportSettingsModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showSalesReportSettings(id) {
		$.ajax({
			url : salesReportSettingsContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_document').text(data.documentName);
				$('#lbl_paymentMode').text(data.paymentMode);
				$('#lbl_debitCredit').text(data.debitCredit);
				$('#lbl_sortorder').text(data.sortOrder);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editSalesReportSettings(id) {
		$.ajax({
			url : salesReportSettingsContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data);
				$('#field_sortorder').val(data.sortOrder);
				$('#field_debitCredit').val(data.debitCredit);
				$('#field_paymentMode').val(data.paymentMode);
				$('#field_document').val(data.documentPid);
				salesReportSettingsModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteSalesReportSettings(actionurl, id) {
		$.ajax({
			url : actionurl,
			method : 'DELETE',
			success : function(data) {
				onDeleteSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}



	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = salesReportSettingsContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = salesReportSettingsContextPath;
	}

	SalesReportSettings.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showSalesReportSettings(id);
				break;
			case 1:
				editSalesReportSettings(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', salesReportSettingsContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	SalesReportSettings.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		salesReportSettingsModel.id = null; // reset salesReportSettings model;
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