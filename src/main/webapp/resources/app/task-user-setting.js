// Create a TaskUserSetting object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.TaskUserSetting) {
	this.TaskUserSetting = {};
}

(function() {
	'use strict';

	var taskUserSettingContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#taskUserSettingForm");
	var deleteForm = $("#deleteForm");
	var taskUserSettingModel = {
		pid : null,
		executorPid : null,
		taskSettingPid : null,
		approvers : []
	};

	// Specify the validation rules
	var validationRules = {
		executorPid : {
			valueNotEquals : "-1"
		},
		taskSettingPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		executorPid : {
			valueNotEquals : "This field is required.",
		},
		taskSettingPid : {
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
				createUpdateTaskUserSetting(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteTaskUserSetting(e.currentTarget.action);
		});

		$('#btnAddApprover').on('click', function() {
			addApprover();
		});
	});

	function createUpdateTaskUserSetting(el) {

		taskUserSettingModel.executorPid = $('#field_executor').val();
		taskUserSettingModel.taskSettingPid = $('#field_taskSetting').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(taskUserSettingModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showTaskUserSetting(id) {
		$.ajax({
			url : taskUserSettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_executor').text(data.executorName);
				$('#lbl_activityEvent').text(data.taskSettingEvent);

				$('#tbl_Approvers').html("");
				$.each(data.approvers, function(index, approver) {
					$('#tbl_Approvers').append(
							"<tr><td >" + approver.firstName + "</td></tr>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editTaskUserSetting(id) {
		$.ajax({
			url : taskUserSettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_executor').val(data.executorPid);
				$('#field_taskSetting').val(data.taskSettingPid);

				createApproverTableView(data.approvers);
				taskUserSettingModel.approvers = data.approvers;

				// set pid
				taskUserSettingModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteTaskUserSetting(actionurl, id) {
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

	function addApprover() {
		$.each($("input[name='approver']:checked"), function() {
			var pid = $(this).val();
			var approver = {};
			approver["pid"] = pid;
			approver["firstName"] = $(this).closest('tr').find('td').eq(1)
					.text();

			remove(taskUserSettingModel.approvers, pid);
			taskUserSettingModel.approvers.push(approver);
			createApproverTableView(taskUserSettingModel.approvers);
		});
		$('#approversModal').modal('hide');
	}

	function createApproverTableView(approvers) {
		$('#tblApprovers').html("");
		$
				.each(
						approvers,
						function(index, approver) {
							$('#tblApprovers')
									.append(
											"<tr><td >"
													+ approver.firstName
													+ "</td><td><button type='button' onclick='TaskUserSetting.removeOption(this,\""
													+ approver.pid
													+ "\")'>&times;</button></td></tr>");
						});
	}

	TaskUserSetting.removeOption = function(obj, pid) {
		remove(taskUserSettingModel.approvers, pid);
		$(obj).closest('tr').remove();
	}

	function remove(array, pid) {
		for (var i = 0; i < array.length; i++) {
			var value = array[i];
			if (value.pid == pid) {
				array.splice(i, 1);
				break;
			}
		}
	}

	TaskUserSetting.loadDocuments = function() {
		$('#field_document')
				.html("<option value='-1'>Select Document</option>");
		var pid = $('#field_activity').val();
		if (pid == '-1') {
			return;
		}
		$('#field_document').html(
				"<option value='no'>Document loading...</option>");
		$.ajax({
			url : taskUserSettingContextPath + "/load-documents",
			method : 'GET',
			data : {
				pid : pid
			},
			success : function(documents) {
				$('#field_document').html(
						"<option value='-1'>Select Document</option>");
				$.each(documents, function(index, document) {
					$('#field_document').append(
							"<option value='" + document.pid + "'>"
									+ document.name + "</option>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	TaskUserSetting.loadTaskSettings = function() {
		$('#field_taskSetting').html(
				"<option value='no'>Task Settings loading...</option>");
		$.ajax({
			url : taskUserSettingContextPath + "/load-task-settings",
			method : 'GET',
			data : {
				activityPid : $('#field_activity').val(),
				documentPid : $('#field_document').val()
			},
			success : function(taskSettings) {
				$('#field_taskSetting').html(
						"<option value='-1'>Select Task Setting</option>");
				$.each(taskSettings, function(index, taskSetting) {
					$('#field_taskSetting').append(
							"<option value='" + taskSetting.pid + "'>"
									+ taskSetting.activityEvent + "-"
									+ taskSetting.activityName + "</option>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = taskUserSettingContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = taskUserSettingContextPath;
	}

	TaskUserSetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showTaskUserSetting(id);
				break;
			case 1:
				editTaskUserSetting(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm
						.attr('action', taskUserSettingContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	TaskUserSetting.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		taskUserSettingModel.pid = null; // reset taskUserSetting model;

		taskUserSettingModel.approvers = [];
		$('#tblApprovers').html("");
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