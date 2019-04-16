// Create a UserReceiptTarget object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserReceiptTarget) {
	this.UserReceiptTarget = {};
}

(function() {
	'use strict';

	var userReceiptTargetContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#userReceiptTargetForm");
	var deleteForm = $("#deleteForm");
	var userReceiptTargetModel = {
		pid : null,
		startDate : null,
		endDate : null,
		userPid : null,
		targetAmount : null,
		targetPercentage : null
	};

	// Specify the validation rules
	var validationRules = {
		startDate : {
			required : true,
		},
		endDate : {
			required : true,
		},
		userPid : {
			valueNotEquals : "-1"
		},
		targetAmount : {
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
				createUpdateUserReceiptTarget(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteUserReceiptTarget(e.currentTarget.action);
		});

		$('#btnSaveDocument').on('click', function() {
			saveAssignedDocuments();
		});

		$("input:radio").click(function() {
			var value = $(this).val();
			if(value=="percentage"){
				$('#divPercentage').show();
				$('#divAmount').hide();
				$('#field_targetAmount').val(0);
			}else{
				$('#divAmount').show();
				$('#divPercentage').hide();
				$('#field_targetPercentage').val(0);
			}
		});

	});

	function createUpdateUserReceiptTarget(el) {
		userReceiptTargetModel.startDate = $('#field_startDate').val();
		userReceiptTargetModel.endDate = $('#field_endDate').val();
		userReceiptTargetModel.userPid = $('#field_user').val();
		userReceiptTargetModel.targetAmount = $('#field_targetAmount').val();
		userReceiptTargetModel.targetPercentage = $('#field_targetPercentage')
				.val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userReceiptTargetModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showUserReceiptTarget(id) {
		$.ajax({
			url : userReceiptTargetContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_startDate').text(data.startDate);
				$('#lbl_endDate').text(data.endDate);
				$('#lbl_user').text(data.userName);
				$('#lbl_targetAmount').text(data.targetAmount);
				$('#lbl_targetPercentage').text(data.targetPercentage);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editUserReceiptTarget(id) {
		$.ajax({
			url : userReceiptTargetContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_startDate').val(data.startDate);
				$('#field_endDate').val(data.endDate);
				$('#field_user').val(data.userPid);
				$('#field_targetAmount').val(data.targetAmount);
				$('#field_targetPercentage').val(data.targetPercentage);
				
				if(data.targetAmount == 0){
					$('#divPercentage').show();
					$('#divAmount').hide();
				}else{
					$('#divAmount').show();
					$('#divPercentage').hide();
				}
				
				// set pid
				userReceiptTargetModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteUserReceiptTarget(actionurl, id) {
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

	function loadUserDocument(userReceiptTargetPid) {
		console.log(userReceiptTargetPid);
		$("#documentsCheckboxes input:checkbox").attr('checked', false);
		userReceiptTargetModel.pid = userReceiptTargetPid;
		// clear all check box
		$.ajax({
			url : userReceiptTargetContextPath + "/getDocuments/"
					+ userReceiptTargetPid,
			type : "GET",
			success : function(documents) {
				console.log(documents);
				if (documents) {
					$.each(documents, function(index, document) {
						$(
								"#documentsCheckboxes input:checkbox[value="
										+ document.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedDocuments() {

		$(".error-msg").html("");
		var selectedDocuments = "";

		$.each($("input[name='document']:checked"), function() {
			selectedDocuments += $(this).val() + ",";
		});

		if (selectedDocuments == "") {
			$(".error-msg").html("Please select Documents");
			return;
		}
		$.ajax({
			url : userReceiptTargetContextPath + "/saveDocuments",
			type : "POST",
			data : {
				userReceiptTargetId : userReceiptTargetModel.pid,
				assignedDocuments : selectedDocuments,
			},
			success : function(status) {
				$("#documentsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userReceiptTargetContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userReceiptTargetContextPath;
	}

	UserReceiptTarget.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showUserReceiptTarget(id);
				break;
			case 1:
				editUserReceiptTarget(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', userReceiptTargetContextPath + "/"
						+ id);
				break;
			case 3:
				loadUserDocument(id);
				break;
			}
		}
		el.modal('show');
	}

	UserReceiptTarget.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		userReceiptTargetModel.pid = null; // reset
		// userReceiptTarget
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