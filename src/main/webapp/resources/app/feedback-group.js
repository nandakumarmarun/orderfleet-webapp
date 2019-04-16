// Create a FeedbackGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.FeedbackGroup) {
	this.FeedbackGroup = {};
}

(function() {
	'use strict';

	var feedbackGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#feedbackGroupForm");
	var deleteForm = $("#deleteForm");
	var feedbackGroupModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		activated : true
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
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
				createUpdateFeedbackGroup(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteFeedbackGroup(e.currentTarget.action);
		});
		$('#btnSaveFormElements').on('click', function() {
			saveAssignedFormElements();
		});

		$('#btnSaveUsers').on('click', function() {
			saveAssignedUsers();
		});

		$('#btnSaveStatusField').on('click', function() {
			saveStatusField();
		});
	});

	FeedbackGroup.setStatusField = function(pid, statusFieldPid) {
		$(".error-msg").html("");
		$
				.ajax({
					url : feedbackGroupContextPath + "/findElements/" + pid
							+ "/ANSWER",
					type : "GET",
					success : function(data) {
						feedbackGroupModel.pid = pid;
						$("#dbStatusField")
								.html(
										'<option value="-1">Select Status Field</option>');
						if (data) {
							$
									.each(
											data,
											function(index, formElement) {
												if (formElement.formElementTypeName == "dropdown") {
													$("#dbStatusField")
															.append(
																	'<option value="'
																			+ formElement.pid
																			+ '">'
																			+ formElement.name
																			+ '</option>');
												}
											});
							if (statusFieldPid != "") {
								$("#dbStatusField").val(statusFieldPid);
							}
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
		$("#setStatusFieldModal").modal("show");
	}

	function saveStatusField() {
		if ($("#dbStatusField").val() == "-1") {
			$(".error-msg").html("Please select field");
			return;
		}
		$.ajax({
			url : feedbackGroupContextPath + "/setStatusField",
			type : "POST",
			data : {
				feedbackGroupPid : feedbackGroupModel.pid,
				statusField : $("#dbStatusField").val()
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

	FeedbackGroup.loadUsers = function(pid) {
		$(".error-msg").html("");
		$("#divUsers input:checkbox").attr('checked', false);
		feedbackGroupModel.pid = pid;
		// clear all check box
		$.ajax({
			url : feedbackGroupContextPath + "/findUsers/" + pid,
			type : "GET",
			success : function(users) {
				console.log(users);
				if (users) {
					$.each(users, function(index, user) {
						$("#divUsers input:checkbox[value=" + user.pid + "]")
								.prop("checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		$("#assignUsersModal").modal("show");
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
			url : feedbackGroupContextPath + "/assignUsers",
			type : "POST",
			data : {
				feedbackGroupPid : feedbackGroupModel.pid,
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

	var feedbackElementType = "";
	FeedbackGroup.loadFormElements = function(el, pid, type) {
		$(".error-msg").html("");
		$('.alert').hide();
		feedbackElementType = type;
		// clear all check box
		$("#formElementsCheckboxes input:checkbox").attr('checked', false);
		if (type == 'QUESTION') {
			$('#lblType').text("Questions");
		} else {
			$('#lblType').text("Answers");
		}
		$.ajax({
			url : feedbackGroupContextPath + "/findElements/" + pid + "/"
					+ type,
			type : "GET",
			success : function(data) {
				feedbackGroupModel.pid = pid;
				if (data) {
					$.each(data, function(index, formElement) {
						$(
								"#formElementsCheckboxes input:checkbox[value="
										+ formElement.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		el.modal('show');
	}

	function saveAssignedFormElements() {

		$(".error-msg").html("");
		var selectedFormElements = "";

		$.each($("input[name='formElement']:checked"), function() {
			selectedFormElements += $(this).val() + ",";
		});

		if (selectedFormElements == "") {
			$(".error-msg").html("Please select Form Elements");
			return;
		}
		$.ajax({
			url : feedbackGroupContextPath + "/assignElements",
			type : "POST",
			data : {
				feedbackGroupPid : feedbackGroupModel.pid,
				assignedElements : selectedFormElements,
				type : feedbackElementType
			},
			success : function(status) {
				$("#formElementsAnswerModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function createUpdateFeedbackGroup(el) {
		feedbackGroupModel.name = $('#field_name').val();
		feedbackGroupModel.alias = $('#field_alias').val();
		feedbackGroupModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(feedbackGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showFeedbackGroup(id) {
		$.ajax({
			url : feedbackGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_description').text(data.description);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editFeedbackGroup(id) {
		$.ajax({
			url : feedbackGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				// set pid
				feedbackGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteFeedbackGroup(actionurl, id) {
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

	FeedbackGroup.setActive = function(name, pid, active) {
		feedbackGroupModel.pid = pid;
		feedbackGroupModel.activated = active;
		feedbackGroupModel.name = name;
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : feedbackGroupContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(feedbackGroupModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = feedbackGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = feedbackGroupContextPath;
	}

	FeedbackGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showFeedbackGroup(id);
				break;
			case 1:
				editFeedbackGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', feedbackGroupContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	FeedbackGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		feedbackGroupModel.pid = null; // reset feedbackGroup model;
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
			var entityMessage = httpResponse
					.getResponseHeader('X-orderfleetwebApp-message');
			if (errorHeader) {
				var entityName = entityKey;
				addErrorAlert(entityMessage, errorHeader, {
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