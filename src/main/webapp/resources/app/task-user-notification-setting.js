if (!this.TaskUserNotificationSetting) {
	this.TaskUserNotificationSetting = {};
}

(function() {
	'use strict';

	var taskUserNotificationSettingContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#taskUserNotificationSettingForm");
	var deleteForm = $("#deleteForm");
	var taskUserNotificationSettingModel = {
		pid : null,
		executorPid : null,
		taskNotificationSettingPid : null,
		approvers : [],
		enableTerritory:null
	};

	// Specify the validation rules
	var validationRules = {
		executorPid : {
			valueNotEquals : "-1"
		},
		taskNotificationSettingPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		executorPid : {
			valueNotEquals : "This field is required.",
		},
		taskNotificationSettingPid : {
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
				createUpdateTaskUserNotificationSetting(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteTaskUserNotificationSetting(e.currentTarget.action);
		});

		$('#btnAddApprover').on('click', function() {
			addApprover();
		});
	});

	function createUpdateTaskUserNotificationSetting(el) {
		
		taskUserNotificationSettingModel.executorPid = $('#field_executor').val();
		taskUserNotificationSettingModel.taskNotificationSettingPid = $('#field_taskNotificationSetting').val();
		taskUserNotificationSettingModel.enableTerritory = $("#field_enableTerritory").is(":checked");
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(taskUserNotificationSettingModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showTaskUserNotificationSetting(id) {
		$.ajax({
			url : taskUserNotificationSettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_executor').text(data.executorName);
				$('#lbl_activityEvent').text(data.taskNotificationSettingEvent);

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

	function editTaskUserNotificationSetting(id) {
		$.ajax({
			url : taskUserNotificationSettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_executor').val(data.executorPid);
				$('#field_taskNotificationSetting').val(data.taskNotificationSettingPid);
				$('#field_enableTerritory').prop("checked",data.enableTerritory);
				createApproverTableView(data.approvers);
				taskUserNotificationSettingModel.approvers = data.approvers;

				// set pid
				taskUserNotificationSettingModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteTaskUserNotificationSetting(actionurl, id) {
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

			remove(taskUserNotificationSettingModel.approvers, pid);
			taskUserNotificationSettingModel.approvers.push(approver);
			createApproverTableView(taskUserNotificationSettingModel.approvers);
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
													+ "</td><td><button type='button' onclick='TaskUserNotificationSetting.removeOption(this,\""
													+ approver.pid
													+ "\")'>&times;</button></td></tr>");
						});
	}

	TaskUserNotificationSetting.removeOption = function(obj, pid) {
		remove(taskUserNotificationSettingModel.approvers, pid);
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

	TaskUserNotificationSetting.loadDocuments = function() {
		$('#field_document')
				.html("<option value='-1'>Select Document</option>");
		var pid = $('#field_activity').val();
		if (pid == '-1') {
			return;
		}
		$('#field_document').html(
				"<option value='no'>Document loading...</option>");
		$.ajax({
			url : taskUserNotificationSettingContextPath + "/load-documents",
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

	TaskUserNotificationSetting.loadTaskNotificationSettings = function() {
		$('#field_taskSetting').html(
				"<option value='no'>Task Notification Settings loading...</option>");
		$.ajax({
			url : taskUserNotificationSettingContextPath + "/load-task-notification-settings",
			method : 'GET',
			data : {
				activityPid : $('#field_activity').val(),
				documentPid : $('#field_document').val()
			},
			success : function(taskNotificationSettings) {
				$('#field_taskNotificationSetting').html(
						"<option value='-1'>Select Task Notification Setting</option>");
				$.each(taskNotificationSettings, function(index, taskNotificationSetting) {
					$('#field_taskNotificationSetting').append(
							"<option value='" + taskNotificationSetting.pid + "'>"
									+ taskNotificationSetting.activityEvent + "-"
									+ taskNotificationSetting.activityName + "</option>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = taskUserNotificationSettingContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = taskUserNotificationSettingContextPath;
	}

	TaskUserNotificationSetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showTaskUserNotificationSetting(id);
				break;
			case 1:
				editTaskUserNotificationSetting(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm
						.attr('action', taskUserNotificationSettingContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	TaskUserNotificationSetting.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		taskUserNotificationSettingModel.pid = null; // reset taskUserSetting model;

		taskUserNotificationSettingModel.approvers = [];
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