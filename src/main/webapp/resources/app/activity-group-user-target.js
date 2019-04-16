// Create a ActivityGroupUserTarget object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ActivityGroupUserTarget) {
	this.ActivityGroupUserTarget = {};
}

(function() {
	'use strict';

	var activityGroupUserTargetContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#activityGroupUserTargetForm");
	var deleteForm = $("#deleteForm");
	var activityGroupUserTargetModel = {
		pid : null,
		startDate : null,
		endDate : null,
		activityGroupPid : null,
		userPid : null,
		targetNumber : null
	};

	// Specify the validation rules
	var validationRules = {
		startDate : {
			required : true,
		},
		endDate : {
			required : true,
		},
		activityGroupPid : {
			valueNotEquals : "-1"
		},
		userPid : {
			valueNotEquals : "-1"
		},
		targetNumber : {
			required : true,
		}

	};

	// Specify the validation error messages
	var validationMessages = {
		startDate : {
			required : "This field is required.",
		},
		endDate : {
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
				createUpdateActivityGroupUserTarget(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteActivityGroupUserTarget(e.currentTarget.action);
		});
	});

	function createUpdateActivityGroupUserTarget(el) {
		activityGroupUserTargetModel.startDate = $('#field_startDate').val();
		activityGroupUserTargetModel.endDate = $('#field_endDate').val();
		activityGroupUserTargetModel.activityGroupPid = $(
				'#field_activityGroup').val();
		activityGroupUserTargetModel.userPid = $('#field_user').val();
		activityGroupUserTargetModel.targetNumber = $('#field_targetNumber').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(activityGroupUserTargetModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showActivityGroupUserTarget(id) {
		$.ajax({
			url : activityGroupUserTargetContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_startDate').text(data.startDate);
				$('#lbl_endDate').text(data.endDate);
				$('#lbl_activityGroup').text(data.activityGroupName);
				$('#lbl_user').text(data.userName);
				$('#lbl_targetNumber').text(data.targetNumber);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editActivityGroupUserTarget(id) {
		$.ajax({
			url : activityGroupUserTargetContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_startDate').val(data.startDate);
				$('#field_endDate').val(data.endDate);
				$('#field_activityGroup').val(data.activityGroupPid);
				$('#field_user').val(data.userPid);
				$('#field_targetNumber').val(data.targetNumber);
				// set pid
				activityGroupUserTargetModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteActivityGroupUserTarget(actionurl, id) {
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
		window.location = activityGroupUserTargetContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = activityGroupUserTargetContextPath;
	}

	ActivityGroupUserTarget.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showActivityGroupUserTarget(id);
				break;
			case 1:
				editActivityGroupUserTarget(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', activityGroupUserTargetContextPath
						+ "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	ActivityGroupUserTarget.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		activityGroupUserTargetModel.pid = null; // reset
		// activityGroupUserTarget
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