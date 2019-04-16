// Create a ScheduleTask object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ScheduleTask) {
	this.ScheduleTask = {};
}

(function() {
	'use strict';

	var scheduleTaskContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var selectedUserPid = null;

	$(document).ready(function() {

		$('#btnSaveTask').on('click', function() {
			saveAssignedTasks();
		});

		$('#btnSaveTaskGroup').on('click', function() {
			saveAssignedTaskGroup();
		});

		$('#btnSaveTaskList').on('click', function() {
			saveAssignedTaskLists();
		});
		
		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

	});
	
	function searchTable(inputVal) {
		var table = $('#tbodyTask');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
					var regExp = new RegExp(inputVal, 'i');
					if (regExp.test($(td).text())) {
						found = true;
						return false;
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	/**
	 * task assign
	 * 
	 * save assigned task
	 */
	function saveAssignedTasks() {

		var userTaskList = [];
		$.each($("input[name='task']:checked:not(:disabled)"), function() {
			var taskPid = $(this).val();
			var priorityStatus = $("#priority" + taskPid).val();
			var startDate = $("#startDate" + taskPid).val();
			if(startDate == ""){
				$(".error-msg").html("Please select date");
				return false;
			}
			var remarks = $("#remarks" + taskPid).val();
			userTaskList.push({
				pid : null,
				executiveUserPid : selectedUserPid,
				taskPid : taskPid,
				priorityStatus : priorityStatus,
				startDate : startDate,
				remarks : remarks
			});
		});
		if (userTaskList.length == 0) {
			$(".error-msg").html("Please select Tasks");
			return;
		}
		$(".error-msg").html("");
		$.ajax({
			method : 'POST',
			url : scheduleTaskContextPath + "/user-tasks",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userTaskList),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	/**
	 * task assign
	 * 
	 * set assigned task
	 */
	function setAssignedTasks(userPid) {
		
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");
		
		$("#divTaskAssignment input:checkbox[name='task']").attr('checked',
				false);
		$("#divTaskAssignment input:checkbox[name='task']").attr('disabled',
				false);
		$.ajax({
			url : scheduleTaskContextPath + "/user-tasks",
			method : 'GET',
			data : {
				userPid : userPid
			},
			success : function(userTasks) {
				$.each(userTasks, function(index, userTask) {
					$(
							"#divTaskAssignment input:checkbox[value="
									+ userTask.taskPid + "]").prop("checked",
							true);
					$(
							"#divTaskAssignment input:checkbox[value="
									+ userTask.taskPid + "]").prop("disabled",
							true);

					$("#priority" + userTask.taskPid).val(
							userTask.priorityStatus);
					$("#priority" + userTask.taskPid).prop("disabled", true);

					$("#startDate" + userTask.taskPid).val(userTask.startDate);
					$("#startDate" + userTask.taskPid).prop("disabled", true);

					$("#remarks" + userTask.taskPid).val(userTask.remarks);
					$("#remarks" + userTask.taskPid).prop("disabled", true);
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	/**
	 * task group assign
	 * 
	 * save assigned task groups
	 */
	function saveAssignedTaskGroup() {

		var userTaskGroupList = [];
		$.each($("input[name='taskGroup']:checked:not(:disabled)"), function() {
			var taskGroupPid = $(this).val();
			var priorityStatus = $("#priority" + taskGroupPid).val();
			var startDate = $("#startDate" + taskGroupPid).val();
			if(startDate == ""){
				$(".error-msg").html("Please select date");
				return false;
			}
			var remarks = $("#remarks" + taskGroupPid).val();
			userTaskGroupList.push({
				pid : null,
				executiveUserPid : selectedUserPid,
				taskGroupPid : taskGroupPid,
				priorityStatus : priorityStatus,
				startDate : startDate,
				remarks : remarks
			});
		});

		if (userTaskGroupList.length == 0) {
			$(".error-msg").html("Please select Task Groups");
			return;
		}
		$(".error-msg").html("");
		$.ajax({
			method : 'POST',
			url : scheduleTaskContextPath + "/user-task-groups",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userTaskGroupList),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	/**
	 * task group assign
	 * 
	 * set assigned task groups
	 */
	function setAssignedTaskGroups(userPid) {
		$("#divTaskGroupAssignment input:checkbox[name='taskGroup']").attr('checked',false);
		$("#divTaskGroupAssignment input:checkbox[name='taskGroup']").attr('disabled', false);
		$.ajax({
			url : scheduleTaskContextPath + "/user-task-groups",
			method : 'GET',
			data : {
				userPid : userPid
			},
			success : function(userTaskGroups) {
				$.each(userTaskGroups, function(index, userTaskGroup) {
					$("#divTaskGroupAssignment input:checkbox[value="+ userTaskGroup.taskGroupPid + "]").prop("checked",true);
					$("#divTaskGroupAssignment input:checkbox[value="+ userTaskGroup.taskGroupPid + "]").prop("disabled",true);

					$("#priority" + userTaskGroup.taskGroupPid).val(userTaskGroup.priorityStatus);
					$("#priority" + userTaskGroup.taskGroupPid).prop("disabled", true);

					$("#startDate" + userTaskGroup.taskGroupPid).val(userTaskGroup.startDate);
					$("#startDate" + userTaskGroup.taskGroupPid).prop("disabled", true);

					$("#remarks" + userTaskGroup.taskGroupPid).val(userTaskGroup.remarks);
					$("#remarks" + userTaskGroup.taskGroupPid).prop("disabled", true);
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	/**
	 * task list assign
	 * 
	 * save assigned task list
	 */
	function saveAssignedTaskLists() {

		var userTaskLists = [];
		$.each($("input[name='taskList']:checked:not(:disabled)"), function() {
			var taskListPid = $(this).val();
			var priorityStatus = $("#priority" + taskListPid).val();
			var startDate = $("#startDate" + taskListPid).val();
			if(startDate == ""){
				$(".error-msg").html("Please select date");
				return false;
			}
			var remarks = $("#remarks" + taskListPid).val();
			userTaskLists.push({
				pid : null,
				executiveUserPid : selectedUserPid,
				taskListPid : taskListPid,
				priorityStatus : priorityStatus,
				startDate : startDate,
				remarks : remarks
			});
		});
		if (userTaskLists.length == 0) {
			$(".error-msg").html("Please select Task List");
			return;
		}
		$(".error-msg").html("");
		$.ajax({
			method : 'POST',
			url : scheduleTaskContextPath + "/user-task-list",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userTaskLists),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	/**
	 * task list assign
	 * 
	 * set assigned task list
	 */
	function setAssignedTaskList(userPid) {
		$("#divTaskListAssignment input:checkbox[name='taskList']").attr('checked',false);
		$("#divTaskListAssignment input:checkbox[name='taskList']").attr('disabled',false);
		$.ajax({
			url : scheduleTaskContextPath + "/user-task-list",
			method : 'GET',
			data : {
				userPid : userPid
			},
			success : function(userTaskLists) {
				$.each(userTaskLists, function(index, userTaskList) {
					$("#divTaskListAssignment input:checkbox[value="+ userTaskList.taskListPid + "]").prop("checked",true);
					$("#divTaskListAssignment input:checkbox[value="+ userTaskList.taskListPid + "]").prop("disabled",true);

					$("#priority" + userTaskList.taskListPid).val(userTaskList.priorityStatus);
					$("#priority" + userTaskList.taskListPid).prop("disabled", true);

					$("#startDate" + userTaskList.taskListPid).val(userTaskList.startDate);
					$("#startDate" + userTaskList.taskListPid).prop("disabled", true);

					$("#remarks" + userTaskList.taskListPid).val(userTaskList.remarks);
					$("#remarks" + userTaskList.taskListPid).prop("disabled", true);
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = scheduleTaskContextPath;
	}

	ScheduleTask.showModalPopup = function(el, userPid, action) {
		// set user pid
		selectedUserPid = userPid;
		resetForm();
		if (userPid) {
			switch (action) {
			case "Task":
				setAssignedTasks(userPid);
				break;
			case "TaskGroup":
				setAssignedTaskGroups(userPid);
				break;
			case "TaskList":
				setAssignedTaskList(userPid);
				break;
			}
		}
		el.modal('show');
	}

	function resetForm() {
		$(".startDate").val("");
		$(".startDate").prop("disabled", false);
		$(".remarks").val("");
		$(".remarks").prop("disabled", false);
		$(".priority").val("NORMAL");
		$(".priority").prop("disabled", false);
		
		$(".error-msg").html("");
		
		// set today date
		$(".startDate").datepicker().datepicker("setDate", new Date());
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