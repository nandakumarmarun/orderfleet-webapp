// Create a PurchaseHistoryConfig object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PurchaseHistoryConfig) {
	this.PurchaseHistoryConfig = {};
}

(function() {
	'use strict';

	var purchaseHistoryConfigContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#purchaseHistoryConfigForm");
	var deleteForm = $("#deleteForm");
	var purchaseHistoryConfigModel = {
		pid : null,
		name : null,
		startMonth : 0,
		startMonthName : null,
		startMonthMinus : 0,
		startMonthYearMinus : null,
		endMonth : 0,
		endMonthName : null,
		endMonthMinus : 0,
		endMonthYearMinus : null,
		createDynamicLabel : null,
		description : null,
		sortOrder : 0
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		description : {
			maxlength : "This field cannot be longer than 250 characters."
		}
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdatePurchaseHistoryConfig(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deletePurchaseHistoryConfig(e.currentTarget.action);
		});

	});

	function createUpdatePurchaseHistoryConfig(el) {
		purchaseHistoryConfigModel.name = $('#field_name').val();

		purchaseHistoryConfigModel.startMonth = $('#field_startMonth').val();
		purchaseHistoryConfigModel.startMonthName = $(
				'#field_startMonth option:selected').text();
		purchaseHistoryConfigModel.startMonthMinus = $('#field_startMonthMinus')
				.val();
		purchaseHistoryConfigModel.startMonthYearMinus = $(
				'#field_startMonthYearMinus').val();

		purchaseHistoryConfigModel.endMonth = $('#field_endMonth').val();
		purchaseHistoryConfigModel.endMonthName = $(
				'#field_endMonth option:selected').text();
		purchaseHistoryConfigModel.endMonthMinus = $('#field_endMonthMinus')
				.val();
		purchaseHistoryConfigModel.endMonthYearMinus = $(
				'#field_endMonthYearMinus').val();

		purchaseHistoryConfigModel.createDynamicLabel = $(
				'#field_createDynamicLabel').prop("checked");
		purchaseHistoryConfigModel.description = $('#field_description').val();
		
		purchaseHistoryConfigModel.sortOrder=$('#field_sortOrder').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(purchaseHistoryConfigModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editPurchaseHistoryConfig(id) {
		$.ajax({
			url : purchaseHistoryConfigContextPath + "/" + id,
			method : 'GET',
			success : function(data) {

				$('#field_name').val(data.name);

				$('#field_startMonth').val(data.startMonth);
				$('#field_startMonthMinus').val(data.startMonthMinus);
				$('#field_startMonthYearMinus').val(data.startMonthYearMinus);

				$('#field_endMonth').val(data.endMonth);
				$('#field_endMonthMinus').val(data.endMonthMinus);
				$('#field_endMonthYearMinus').val(data.endMonthYearMinus);

				$('#field_createDynamicLabel').prop("checked",
						data.createDynamicLabel);
				$('#field_description').val(data.description);
				$('#field_sortOrder').val(data.sortOrder);

				// set pid
				purchaseHistoryConfigModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deletePurchaseHistoryConfig(actionurl, id) {
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
		window.location = purchaseHistoryConfigContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = purchaseHistoryConfigContextPath;
	}

	PurchaseHistoryConfig.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showPurchaseHistoryConfig(id);
				break;
			case 1:
				editPurchaseHistoryConfig(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', purchaseHistoryConfigContextPath
						+ "/" + id);
				break;
			case 3:
				loadAccounts(id);
				break;
			}
		}
		el.modal('show');
	}

	PurchaseHistoryConfig.onchangeStartMonth = function() {
		var value = $('#field_startMonth').val();
		if(value == 0){
			$("#field_startMonthMinus").attr("readonly", false); 
		}else{
			$("#field_startMonthMinus").val(0); 
			$("#field_startMonthMinus").attr("readonly", true); 
		}
	}

	PurchaseHistoryConfig.onchangeEndMonth = function() {
		var value = $('#field_endMonth').val();
		if(value == 0){
			$("#field_endMonthMinus").attr("readonly", false); 
		}else{
			$("#field_endMonthMinus").val(0); 
			$("#field_endMonthMinus").attr("readonly", true); 
		}
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		purchaseHistoryConfigModel.pid = null; // reset purchaseHistoryConfig
		// model;
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