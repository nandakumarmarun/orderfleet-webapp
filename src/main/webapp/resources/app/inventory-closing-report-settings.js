if (!this.InventoryClosingReportSettings) {
	this.InventoryClosingReportSettings = {};
}

(function() {
	'use strict';

	var inventoryClosingReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#inventoryClosingReportForm");
	var deleteForm = $("#deleteForm");
	var inventoryClosingReportModel = {
		id : null,
		documentPid : null,
		inventoryClosingReportSettingGroupPid:null
	};

	// Specify the validation rules
	var validationRules = {
		documentPid : {
			valueNotEquals : "-1"
		},
		inventoryClosingReportSettingGroupPid : {
			valueNotEquals : "-1"
		},
	};

	// Specify the validation error messages
	var validationMessages = {
		documentPid : {
			valueNotEquals : "This field is required."
		},
		inventoryClosingReportSettingGroupPid : {
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
						createUpdateInventoryClosingReportSettings(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteInventoryClosingReportSettings(e.currentTarget.action);
				});

			});


	function createUpdateInventoryClosingReportSettings(el) {
		inventoryClosingReportModel.documentPid = $('#field_document').val();
		
		inventoryClosingReportModel.inventoryClosingReportSettingGroupPid=$('#field_inClgRptStgGrp').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(inventoryClosingReportModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showInventoryClosingReportSettings(id) {
		$.ajax({
			url : inventoryClosingReportContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_document').text(data.documentName);
				
				$('#lbl_settingGroup').text(data.inventoryClosingReportSettingGroupName);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editInventoryClosingReportSettings(id) {
		$.ajax({
			url : inventoryClosingReportContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				
				$('#field_inClgRptStgGrp').val(data.inventoryClosingReportSettingGroupPid);
				$('#field_document').val(data.documentPid);
				inventoryClosingReportModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteInventoryClosingReportSettings(actionurl, id) {
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
		window.location = inventoryClosingReportContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = inventoryClosingReportContextPath;
	}

	InventoryClosingReportSettings.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showInventoryClosingReportSettings(id);
				break;
			case 1:
				editInventoryClosingReportSettings(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', inventoryClosingReportContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	InventoryClosingReportSettings.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		inventoryClosingReportModel.id = null; // reset inventoryClosingReport model;
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