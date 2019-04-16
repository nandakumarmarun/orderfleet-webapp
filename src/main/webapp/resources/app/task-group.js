// Create a TaskGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.TaskGroup) {
	this.TaskGroup = {};
}

(function() {
	'use strict';

	var taskGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#taskGroupForm");
	var deleteForm = $("#deleteForm");
	var taskGroupModel = {
		pid : null,
		name : null,
		alias : null,
		activityGroupPid : null,
		description : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		},
		activityGroupPid : {
			valueNotEquals : "-1"
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
				createUpdateTaskGroup(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteTaskGroup(e.currentTarget.action);
		});

		$('#btnSaveTasks').on('click', function() {
			saveAssignedTasks();
		});
	});

	function createUpdateTaskGroup(el) {
		taskGroupModel.name = $('#field_name').val();
		taskGroupModel.alias = $('#field_alias').val();
		taskGroupModel.activityGroupPid = $('#field_activityGroup').val();
		taskGroupModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(taskGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showTaskGroup(id) {
		$.ajax({
			url : taskGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_activityGroup').text(data.activityGroupName);
				$('#lbl_description').text(data.description);

				$('#tblTasks').html("");
				var tasks = "";
				$.each(data.tasks, function(index, task) {
					tasks += ("<tr><td>" + task.activityName + "</td></tr>");
				});
				if (tasks != "") {
					var table = "<thead><tr><th>Tasks</th></tr></thead><tbody>"
							+ tasks + "</tbody>";
					$('#tblTasks').append(table);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editTaskGroup(id) {
		$.ajax({
			url : taskGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_activityGroup').val(data.activityGroupPid);
				$('#field_description').val(data.description);
				// set pid
				taskGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteTaskGroup(actionurl, id) {
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

	function loadTasks(pid) {
		// clear all check box
		$("#tasksCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : taskGroupContextPath + "/" + pid,
			type : "GET",
			success : function(data) {
				taskGroupModel.pid = data.pid;
				if (data.tasks) {
					$.each(data.tasks,
							function(index, task) {
								$(
										"#tasksCheckboxes input:checkbox[value="
												+ task.pid + "]").prop(
										"checked", true);
							});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedTasks() {

		$(".error-msg").html("");
		var selectedTasks = "";

		$.each($("input[name='task']:checked"), function() {
			selectedTasks += $(this).val() + ",";
		});

		if (selectedTasks == "") {
			$(".error-msg").html("Please select Tasks");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : taskGroupContextPath + "/assignTasks",
			type : "POST",
			data : {
				pid : taskGroupModel.pid,
				assignedTasks : selectedTasks,
			},
			success : function(status) {
				$("#tasksModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = taskGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = taskGroupContextPath;
	}

	TaskGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showTaskGroup(id);
				break;
			case 1:
				editTaskGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', taskGroupContextPath + "/" + id);
				break;
			case 3:
				loadTasks(id);
				break;
			}
		}
		el.modal('show');
	}

	TaskGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		taskGroupModel.pid = null; // reset taskGroup model;
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