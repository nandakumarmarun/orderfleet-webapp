// Create a UserTaskList object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserTaskList) {
	this.UserTaskList = {};
}

(function() {
	'use strict';

	var userTaskListContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var userTaskListModel = {
		userPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveTaskList').on('click', function() {
			saveAssignedTaskLists();
		});
	});

	function loadUserTaskList(userPid) {
		console.log(userPid);
		$("#taskListsCheckboxes input:checkbox").attr('checked', false);
		userTaskListModel.userPid = userPid;
		// clear all check box
		$.ajax({
			url : userTaskListContextPath + "/" + userPid,
			type : "GET",
			success : function(taskLists) {
				if (taskLists) {
					$.each(taskLists, function(index, taskList) {
						$(
								"#taskListsCheckboxes input:checkbox[value="
										+ taskList.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedTaskLists() {
		$(".error-msg").html("");
		var selectedTaskLists = "";

		$.each($("input[name='taskList']:checked"), function() {
			selectedTaskLists += $(this).val() + ",";
		});

		if (selectedTaskLists == "") {
			$(".error-msg").html("Please select TaskLists");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : userTaskListContextPath + "/save",
			type : "POST",
			data : {
				userPid : userTaskListModel.userPid,
				assignedTaskLists : selectedTaskLists,
			},
			success : function(status) {
				$("#taskListsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userTaskListContextPath;
	}

	UserTaskList.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserTaskList(id);
				break;
			}
		}
		el.modal('show');
	}

	UserTaskList.closeModalPopup = function(el) {
		el.modal('hide');
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