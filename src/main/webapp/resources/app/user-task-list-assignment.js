// Create a UserTaskListAssignment object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserTaskListAssignment) {
	this.UserTaskListAssignment = {};
}

(function() {
	'use strict';

	var userTaskListAssignmentContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#userTaskListAssignmentForm");
	var deleteForm = $("#deleteForm");
	var userTaskListAssignmentModel = {
		pid : null,
		executiveUserPid : null,
		taskListPid : null,
		priorityStatus : null,
		startDate : null,
		remarks : null
	};

	// Specify the validation rules
	var validationRules = {
		executiveUserPid : {
			valueNotEquals : "-1"
		},
		taskListPid : {
			valueNotEquals : "-1"
		},
		startDate : {
			required : true,
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		startDate : {
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
				createUpdateUserTaskListAssignment(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteUserTaskListAssignment(e.currentTarget.action);
		});

		/*
		 * $('#btnSaveTasks').on('click', function() { saveAssignedTasks(); });
		 */
	});

	function createUpdateUserTaskListAssignment(el) {

		userTaskListAssignmentModel.executiveUserPid = $('#field_executiveUser')
				.val();
		userTaskListAssignmentModel.executiveUserName = $(
				'#field_executiveUser').find('option:selected').text();
		userTaskListAssignmentModel.taskListPid = $('#field_taskList').val();
		userTaskListAssignmentModel.priorityStatus = $('#field_priority').val();
		userTaskListAssignmentModel.startDate = $('#field_startDate').val();
		userTaskListAssignmentModel.remarks = $('#field_remarks').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userTaskListAssignmentModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showUserTaskListAssignment(id) {
		$.ajax({
			url : userTaskListAssignmentContextPath + "/" + id,
			method : 'GET',
			success : function(data) {

				$('#lbl_executiveUser').text(data.executiveUserName);
				$('#lbl_taskList').text(data.taskListName);
				$('#lbl_priority').text(data.priorityStatus);
				$('#lbl_startDate').text(data.startDate);
				$('#lbl_remarks').text(data.remarks);

				/*
				 * $('#tblTasks').html(""); $.each(data.tasks, function(index,
				 * task) { $('#tblTasks') .append("<tr><td>" + task.name + "</td></tr>");
				 * });
				 */
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editUserTaskListAssignment(id) {
		$.ajax({
			url : userTaskListAssignmentContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_executiveUser').val(data.executiveUserPid);
				$('#field_taskList').val(data.taskListPid);
				$('#field_priority').val(data.priorityStatus);
				$('#field_startDate').val(data.startDate);
				$('#field_remarks').val(data.remarks);
				// set pid
				userTaskListAssignmentModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteUserTaskListAssignment(actionurl, id) {
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

	/*
	 * function loadTasks(pid) { // clear all check box $("#tasksCheckboxes
	 * input:checkbox").attr('checked', false); $.ajax({ url :
	 * userTaskListAssignmentContextPath + "/" + pid, type : "GET", success :
	 * function(data) { userTaskListAssignmentModel.pid = data.pid; if
	 * (data.tasks) { $.each(data.tasks, function(index, task) { $(
	 * "#tasksCheckboxes input:checkbox[value=" + task.pid + "]").prop(
	 * "checked", true); }); } }, error : function(xhr, error) { onError(xhr,
	 * error); }, }); }
	 * 
	 * function saveAssignedTasks() {
	 * 
	 * $(".error-msg").html(""); var selectedTasks = "";
	 * 
	 * $.each($("input[name='task']:checked"), function() { selectedTasks +=
	 * $(this).val() + ","; });
	 * 
	 * if (selectedTasks == "") { $(".error-msg").html("Please select Tasks");
	 * return; } // $(".error-msg").html("Please wait....."); $.ajax({ url :
	 * userTaskListAssignmentContextPath + "/assignTasks", type : "POST", data : {
	 * pid : userTaskListAssignmentModel.pid, assignedTasks : selectedTasks, },
	 * success : function(status) { $("#tasksModal").modal("hide");
	 * onSaveSuccess(status); }, error : function(xhr, error) { onError(xhr,
	 * error); }, }); }
	 */

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userTaskListAssignmentContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userTaskListAssignmentContextPath;
	}

	UserTaskListAssignment.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showUserTaskListAssignment(id);
				break;
			case 1:
				editUserTaskListAssignment(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', userTaskListAssignmentContextPath
						+ "/" + id);
				break;
			/*
			 * case 3: loadTasks(id); break;
			 */
			}
		}
		el.modal('show');
	}

	UserTaskListAssignment.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		userTaskListAssignmentModel.pid = null; // reset
		// userTaskListAssignment
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