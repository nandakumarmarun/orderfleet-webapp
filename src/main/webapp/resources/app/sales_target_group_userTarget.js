// Create a SalesTargetGroupUserTarget object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesTargetGroupUserTarget) {
	this.SalesTargetGroupUserTarget = {};
}

(function() {
	'use strict';

	var salesTargetGroupUserTargetContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#salesTargetGroupUserTargetForm");
	var deleteForm = $("#deleteForm");
	var salesTargetGroupUserTargetModel = {
		pid : null,
		fromDate : null,
		toDate : null,
		salesTargetGroupPid : null,
		userPid : null,
		amount : null,
		volume : null
	};

	// Specify the validation rules
	var validationRules = {
		fromDate : {
			required : true,
		},
		toDate : {
			required : true,
		},
		salesTargetGroupPid : {
			valueNotEquals : "-1"
		},
		userPid : {
			valueNotEquals : "-1"
		},
		amount : {
			required : function() {
				return !$('#field_volume').val();
			},
		},
		volume : {
			required : function() {
				return !$('#field_amount').val();
			},
		}

	};

	// Specify the validation error messages
	var validationMessages = {
		fromDate : {
			required : "This field is required.",
		},
		toDate : {
			required : "This field is required.",
		}
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateSalesTargetGroupUserTarget(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteSalesTargetGroupUserTarget(e.currentTarget.action);
		});

	});

	function createUpdateSalesTargetGroupUserTarget(el) {
		salesTargetGroupUserTargetModel.fromDate = $('#field_fromDate').val();
		salesTargetGroupUserTargetModel.toDate = $('#field_toDate').val();
		salesTargetGroupUserTargetModel.salesTargetGroupPid = $(
				'#field_salesTargetGroup').val();
		salesTargetGroupUserTargetModel.userPid = $('#field_user').val();
		salesTargetGroupUserTargetModel.volume = $('#field_volume').val();
		salesTargetGroupUserTargetModel.amount = $('#field_amount').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(salesTargetGroupUserTargetModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showSalesTargetGroupUserTarget(id) {
		$.ajax({
			url : salesTargetGroupUserTargetContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_fromDate').text(data.fromDate);
				$('#lbl_toDate').text(data.toDate);
				$('#lbl_salesTargetGroup').text(data.salesTargetGroupName);
				$('#lbl_user').text(data.userName);
				$('#lbl_amount').text(data.amount);
				$('#lbl_volume').text(data.volume);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editSalesTargetGroupUserTarget(id) {
		$.ajax({
			url : salesTargetGroupUserTargetContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_fromDate').val(data.fromDate);
				$('#field_toDate').val(data.toDate);
				$('#field_salesTargetGroup').val(data.salesTargetGroupPid);
				$('#field_user').val(data.userPid);
				$('#field_volume').val(data.volume);
				$('#field_amount').val(data.amount);
				// set pid
				salesTargetGroupUserTargetModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteSalesTargetGroupUserTarget(actionurl, id) {
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
		window.location = salesTargetGroupUserTargetContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = salesTargetGroupUserTargetContextPath;
	}

	SalesTargetGroupUserTarget.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showSalesTargetGroupUserTarget(id);
				break;
			case 1:
				editSalesTargetGroupUserTarget(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', salesTargetGroupUserTargetContextPath
						+ "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		salesTargetGroupUserTargetModel.pid = null; // reset
		// salesTargetGroupUserTarget
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