// Create a DocumentApprovalLevel object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DocumentApprovalLevel) {
	this.DocumentApprovalLevel = {};
}

(function() {
	'use strict';

	var documentApprovalLevelContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#documentApprovalLevelForm");
	var deleteForm = $("#deleteForm");
	var documentApprovalLevelModel = {
		pid : null,
		name : null,
		documentPid : null,
		approverCount : null,
		script : null,
		required : false
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		approverCount : {
			required : true
		},
		documentPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		approverCount : {
			required : "This field is required.",
		},
		documentPid : {
			valueNotEquals : "This field is required.",
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
				createUpdateDocumentApprovalLevel(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDocumentApprovalLevel(e.currentTarget.action);
		});

		$('#btnSaveUsers').on('click', function() {
			saveAssignedUsers();
		});
	});

	function createUpdateDocumentApprovalLevel(el) {
		documentApprovalLevelModel.name = $('#field_name').val();
		documentApprovalLevelModel.documentPid = $('#field_document').val();
		documentApprovalLevelModel.script = $('#field_script').val();
		documentApprovalLevelModel.approverCount = $('#field_approverCount').val();
		documentApprovalLevelModel.required = $('#field_required').prop('checked');

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(documentApprovalLevelModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showDocumentApprovalLevel(id) {
		$.ajax({
			url : documentApprovalLevelContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_document').text(data.documentName);
				$('#lbl_approverCount').text(data.approverCount);
				$('#lbl_approvalOrder').text(data.approvalOrder);
				$('#lbl_required').text(data.required);
				$('#lbl_script').text((data.script == null ? "" : data.script));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editDocumentApprovalLevel(id) {
		$.ajax({
			url : documentApprovalLevelContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_document').val(data.documentPid);
				$('#field_approverCount').val(data.approverCount);
				$('#field_script').val((data.script == null ? "" : data.script));
				$('#field_required').prop('checked', data.required)
				// set pid
				documentApprovalLevelModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteDocumentApprovalLevel(actionurl, id) {
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

	function loadUsers(pid) {
		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		$.ajax({
			url : documentApprovalLevelContextPath + "/" + pid,
			type : "GET",
			success : function(data) {
				documentApprovalLevelModel.pid = data.pid;
				if (data.users) {
					$.each(data.users, function(index, user) {
						$("#divUsers input:checkbox[value=" + user.pid + "]")
								.prop("checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function saveAssignedUsers() {

		$(".error-msg").html("");
		var selectedUsers = "";

		$.each($("input[name='user']:checked"), function() {
			selectedUsers += $(this).val() + ",";
		});

		if (selectedUsers == "") {
			$(".error-msg").html("Please select Users");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : documentApprovalLevelContextPath + "/assignUsers",
			type : "POST",
			data : {
				pid : documentApprovalLevelModel.pid,
				assignedUsers : selectedUsers,
			},
			success : function(status) {
				$("#assignUsersModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentApprovalLevelContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = documentApprovalLevelContextPath;
	}

	DocumentApprovalLevel.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDocumentApprovalLevel(id);
				break;
			case 1:
				editDocumentApprovalLevel(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', documentApprovalLevelContextPath
						+ "/" + id);
				break;
			case 3:
				loadUsers(id);
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
		documentApprovalLevelModel.pid = null; // reset documentApprovalLevel
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