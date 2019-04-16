var contextPath = location.protocol + '//' + location.host;
var selectedDate = moment(new Date()).format('YYYY-MM-DD'); // default to
															// current date
$(document).ready(
		function() {
			$("#planDatePicker").datepicker("setDate", new Date());
			$('#planDatePicker').on('changeDate', function(event) {
				$('#sbUsers').val("-1");
				$('#tblTabUserTasks').html('');
				selectedDate = moment(event.date).format('YYYY-MM-DD');
				$(this).datepicker('hide');
			});
			$('#sbUsers').change(function() {
				loadUserTasks($(this).val());
				loadDownloadedTask($(this).val())
			});
			$('#cbAllTasks').change(
					function() {
						$('#tblTabTasks tr td input[type="checkbox"]').prop(
								'checked', $(this).prop('checked'));
					});
			$('#btnAddTabTasks').click(function() {
				addTaskToUserTasks();
			});
			$('#btnAddTabTaskList').click(function() {
				addTaskListToUserTasks();
			});
			$('#btnSaveUserTasks').click(function() {
				if ($('#sbUsers').val() == "-1") {
					alert("Please select a user.");
					return;
				}
				saveUserTasks();
			});
			$('#newTaskForm').submit(function(e) {
				// prevent Default functionality
				e.preventDefault();
				saveNewTask($(this));
			});
			$('#btnSaveAssignedTasks').click(function(e) {
				saveAssignedTasks();
			});
			// select all checkbox in tasklist tab
			$('#checkTreeTabTaskList').find('.allcheckbox').click(
					function() {
						$(this).closest('table').find(
								'tbody tr td input[type="checkbox"]').prop(
								'checked', $(this).prop('checked'));
					});
			// filter task list pop up search text box
			$('#txtTaskListPopUpSearch').keyup(function() {
				Orderfleet.searchTable($(this).val(), $('#tblAssignTasks'));
			});
		});

function saveAssignedTasks() {
	var selectedTasks = "";
	var taskListPid = $('#tblAssignTasks').data('taskListPid');
	$.each($("#tblAssignTasks input[name='task']:checked"), function() {
		selectedTasks += $(this).val() + ",";
	});
	if (selectedTasks == "") {
		alert("Please select Tasks");
		return;
	}
	$.ajax({
		url : contextPath + '/web/task-lists/assignTasks',
		type : "POST",
		data : {
			pid : $('#tblAssignTasks').data('taskListPid'),
			assignedTasks : selectedTasks,
		},
		success : function(response) {
			// Add to table
			var html = "";
			$.each(response.tasks, function(index, task) {
				html += '<tr><td id="' + task.pid
						+ '"><input type="checkbox" /></td>' + '<td id="'
						+ task.activityPid + '">' + task.activityName + '</td>'
						+ '<td id="' + task.accountProfilePid + '">'
						+ task.accountProfileName + '</td>' + '<td>'
						+ task.remarks + '</td>' + '</tr>';
			});

			$('#' + response.pid).next('tr').find('tbody').html(html);
			$('#assignTasksModal').modal('hide');
		},
		error : function(xhr, error) {
			console.log("error");
		},
	});
}

function saveNewTask(el) {
	if ($('#field_activity').val() == -1) {
		alert("Please select activity");
		return;
	}
	if ($('#field_accountType').val() == -1) {
		alert("Please select account type");
		return;
	}
	if ($('#field_account').val() == -1) {
		alert("Please select account");
		return;
	}
	var taskModel = {};
	taskModel.name = $('#field_name').val();
	taskModel.alias = $('#field_alias').val();
	taskModel.activityPid = $('#field_activity').val();
	taskModel.accountTypePid = $('#field_accountType').val();
	taskModel.accountProfilePid = $('#field_account').val();
	taskModel.remarks = $('#field_remarks').val();
	taskModel.accountPids = $('#field_accounts').val();

	$.ajax({
		method : $(el).attr('method'),
		url : $(el).attr('action'),
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(taskModel),
		success : function(response) {
			$(el).trigger('reset');
			// Add to table
			var html = "";
			$.each(response, function(index, task) {
				html += '<tr><td><input id="' + task.pid
						+ '" type="checkbox" /></td>' + '<td id="'
						+ task.activityPid + '">' + task.activityName + '</td>'
						+ '<td id="' + task.accountProfilePid + '">'
						+ task.accountProfileName + '</td>' + '<td>'
						+ task.remarks + '</td>' + '</tr>';
			});
			$('#tblTabTasks').append(html);
			$('#newTaskModal').modal('hide');
		},
		error : function(xhr, error) {
			onError(xhr, error);
		}
	});
}

// task list assign task pop up search
function filterTasksInAssignTaskPopup(el) {
	var filterByActivityPids = $("#sbActivities option:selected").map(
			function() {
				if ($(this).val() != "-1") {
					return $(this).val();
				}
			}).get();
	var filterByLocationPids = $("#sbLocations option:selected").map(
			function() {
				if ($(this).val() != "-1") {
					return $(this).val();
				}
			}).get();

	$.ajax({
		url : contextPath + '/web/tasks/filterByActivityLocaion',
		method : 'GET',
		data : {
			activityPids : filterByActivityPids.join(", "),
			locationPids : filterByLocationPids.join(", ")
		},
		success : function(response) {
			$('#tblAssignTasks tbody tr').hide();
			// Add to table
			var html = "";
			$.each(response,
					function(index, task) {
						$('#tblAssignTasks tbody').find(
								"input[value='" + task.pid + "']")
								.closest('tr').show();
					});
			// $('#tblAssignTasks').html(html);
		},
		error : function(xhr, error) {
			console.log("task filtering error : " + error);
		}
	});

}

function filterTaskByActivityLocations() {
	var activityPids = [];
	var locationPids = [];
	$("#tActivity").find('input[type="checkbox"]:checked').each(function() {
		activityPids.push($(this).val());
	});
	$("#tLocation").find('input[type="checkbox"]:checked').each(function() {
		locationPids.push($(this).val());
	});
	$('#tblTabTasks').html(
			"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
	$.ajax({
		url : contextPath + '/web/tasks/filterByActivityLocaion',
		method : 'GET',
		data : {
			activityPids : activityPids.join(", "),
			locationPids : locationPids.join(", ")
		},
		success : function(tasks) {
			$('#tblTabTasks').html('');
			$.each(tasks, function(index, task) {
				$('#tblTabTasks').append(
						'<tr>' + '<td><input id="' + task.pid
								+ '" type="checkbox" /></td>' + '<td id="'
								+ task.activityPid + '">' + task.activityName
								+ '</td>' + '<td id="' + task.accountProfilePid
								+ '">' + task.accountProfileName + '</td>'
								+ '<td>' + task.remarks + '</td>' + '</tr>');
			});
		},
		error : function(xhr, error) {
			console.log("task filtering error : " + error);
		}
	});
}

function saveUserTasks() {
	var userTaskList = [];
	$('#tblTabUserTasks').find('tr')
			.each(
					function() {
						var executiveTaskPlanDTO = {
							pid : null,
							createdDate : null,
							createdBy : null,
							taskPlanStatus : "PENDING",
							remarks : null,
							plannedDate : null,
							activityPid : null,
							activityName : null,
							accountTypePid : null,
							accountTypeName : null,
							accountProfilePid : null,
							accountProfileName : null,
							taskCreatedType : "TASK_SERVER",
							taskPid : null,
							taskGroupPid : null,
							taskListPid : null
						};
						var row = $(this);
						var pid = row.data('pid') == "" ? null : row
								.data('pid');
						var taskPid = row.data('taskPid') == "" ? null : row
								.data('taskPid');
						var taskListPid = row.data('taskListPid') == "" ? null
								: row.data('taskListPid');
						executiveTaskPlanDTO.pid = pid;
						executiveTaskPlanDTO.remarks = row.find('td:eq(2)')
								.find('textarea').html();
						executiveTaskPlanDTO.plannedDate = selectedDate
								+ "T01:01:01.001"
						executiveTaskPlanDTO.activityPid = row.find('td:eq(0)')
								.attr('id');
						executiveTaskPlanDTO.activityName = row
								.find('td:eq(0)').html();
						executiveTaskPlanDTO.accountProfilePid = row.find(
								'td:eq(1)').attr('id');
						executiveTaskPlanDTO.accountProfileName = row.find(
								'td:eq(1)').html();
						executiveTaskPlanDTO.taskPid = taskPid;
						executiveTaskPlanDTO.taskListPid = taskListPid;
						userTaskList.push(executiveTaskPlanDTO);
					});
	// save user tasks
	$.ajax({
		url : contextPath + '/web/user-task-plan/' + $('#sbUsers').val(),
		type : "POST",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(userTaskList),
		success : function(response) {
			console.log("SUCCESS: ", response);
			if (response.length > 0) {
				var name="";
				$.each(response,function(index, account) {
					name+=account+" | ";
				});
				$('#errmsg').html(
						"This user have no access to these Account Profiles/Activities :   "
								+ name);
			} else {
				// reloading page
				window.location = contextPath + '/web/user-task-plan';
			}

		},
		error : function(xhr, error) {
			console.log("ERROR: ");
		},
	});
}

function addTaskListToUserTasks() {
	var html = '';
	var taskListPid = "";
	var taskListName = "";
	$('#checkTreeTabTaskList table tbody tr').each(function() {
		var tbllist = $(this);
		if(tbllist.attr('id')) {
			taskListPid = tbllist.attr('id');
		}
		if(tbllist.attr('title')) {
			taskListName = tbllist.attr('title');
		}
		tbllist.find('table tbody tr').each(function() {
			var row = $(this);
			row.find('td input[type="checkbox"]:checked').each(function() {
				var taskPid = row.find('td:eq(0)').attr('id');
				var activityName = row.find('td:eq(1)').html();
				var activityPid = row.find('td:eq(1)').attr('id');
				var accountProfileName = row.find('td:eq(2)').html();
				var accountProfilePid = row.find('td:eq(2)').attr('id');
				var remarks = row.find('td:eq(3)').html();
				html += '<tr data-pid="" data-task-pid="'+ taskPid + '" data-task-list-pid="'+ taskListPid + '">'
						+ '<td id="'+ activityPid + '">'+ activityName + " - " + taskListName + '</td>'
						+ '<td id="'+ accountProfilePid + '">' + accountProfileName + '</td>'
						+ '<td ondblclick="enableRemarkColumn(this)"><textarea onblur="disableRemarkColumn(this);" class="form-control" disabled="disabled" rows="3">'
						+ remarks
						+ '</textarea>'
						+ '</td>'
						+ '<td><button type="button" class="btn btn-default" onclick="deleteRow(this);">Delete</button></td>' 
						+ '</tr>';
			});
		});
	});
	// clear all checkbox
	$('#checkTreeTabTaskList').find('input[type=checkbox]:checked').removeAttr(
			'checked');
	$('#tblTabUserTasks').append(html);
	alert("Added succesfully");
	// show user task tab
	$('.nav-tabs a[href="#tabUserTasks"]').tab('show');
}

function addTaskToUserTasks() {
	var html = '';
	$('#tblTabTasks')
			.find('tr')
			.each(
					function() {
						var row = $(this);
						var checkBox = row.find('input[type="checkbox"]');
						if (checkBox.is(':checked')) {
							var taskPid = checkBox.attr('id');
							var activityName = row.find('td:eq(1)').html();
							var activityPid = row.find('td:eq(1)').attr('id');
							var accountProfileName = row.find('td:eq(2)')
									.html();
							var accountProfilePid = row.find('td:eq(2)').attr(
									'id');
							var remarks = row.find('td:eq(3)').html();
							html += '<tr data-pid="" data-task-pid="'
									+ taskPid
									+ '" data-task-list-pid="">'
									+ '<td id="'
									+ activityPid
									+ '">'
									+ activityName
									+ '</td>'
									+ '<td id="'
									+ accountProfilePid
									+ '">'
									+ accountProfileName
									+ '</td>'
									+ '<td ondblclick="enableRemarkColumn(this)"><textarea onblur="disableRemarkColumn(this);" class="form-control" disabled="disabled" rows="3">'
									+ remarks
									+ '</textarea>'
									+ '</td>'
									+ '<td><button type="button" class="btn btn-default" onclick="deleteRow(this);">Delete</button></td>'
									+ '</tr>';
						}
					});
	$('#tblTabTasks tr td input[type="checkbox"]').prop('checked', false);
	$('#tblTabUserTasks').append(html);
	alert("Added succesfully");
	// show user task tab
	$('.nav-tabs a[href="#tabUserTasks"]').tab('show');
}

function deleteRow(el) {
	if (confirm("Are you sure!")) {
		$(el).closest('tr').remove();
	}
}

function loadUserTasks(userPid) {
	$('#tblTabUserTasks').html('');
	$('#loadingUserTasks').html(
			'<img src="/resources/assets/images/content-ajax-loader.gif">');
	$
			.ajax({
				url : contextPath + '/web/user-tasks?userPid=' + userPid
						+ '&plannedDate=' + selectedDate,
				method : 'GET',
				ContentType : 'json',
				success : function(response) {
					var html = '';
					$
							.each(
									response,
									function(index, userTask) {
										var remarks = "";
										var taskPid = "";
										var taskListPid = "";
										if (userTask.remarks) {
											remarks = userTask.remarks;
										}
										if (userTask.taskPid) {
											taskPid = userTask.taskPid;
										}
										if (userTask.taskListPid) {
											taskListPid = userTask.taskListPid;
										}
										html += '<tr data-pid="'
												+ userTask.pid
												+ '" data-task-pid="'
												+ taskPid
												+ '" data-task-list-pid="'
												+ taskListPid
												+ '">'
												+ '<td id="'
												+ userTask.activityPid
												+ '">'
												+ userTask.activityName
												+ '</td>'
												+ '<td id="'
												+ userTask.accountProfilePid
												+ '">'
												+ userTask.accountProfileName
												+ '</td>'
												+ '<td ondblclick="enableRemarkColumn(this)"><textarea onblur="disableRemarkColumn(this);" class="form-control" disabled="disabled" rows="3">'
												+ remarks
												+ '</textarea>'
												+ '</td>'
												+ '<td><button type="button" class="btn btn-default" onclick="deleteRow(this);">Delete</button></td>'
												+ '</tr>';
									});
					$('#tblTabUserTasks').html(html);
					$('#tblTabUserTasks').sortable();
					$('#loadingUserTasks').html('');
				},
				error : function(e) {
					$('#loadingUserTasks').html('');
				}
			});
}

function loadAllTasks(el) {
	$('#tblAssignTasks').data('taskListPid', $(el).closest('tr').attr('id'));
	var map = {};
	$(el).closest('tr').next('tr').find('table td:first-child').each(
			function() {
				map[$(this).attr('id')] = $(this).attr('id');
			});
	$('#assignTasksModal').modal('show');
	$.ajax({
		method : 'GET',
		url : contextPath + '/web/tasks.json',
		contentType : "application/json; charset=utf-8",
		success : function(response) {
			// Add to table
			var html = "";
			$.each(response, function(index, task) {
				html += '<tr>';
				if (map[task.pid]) {
					html += '<td><input name="task" value="' + task.pid
							+ '" type="checkbox" checked="checked" /></td>'
				} else {
					html += '<td><input name="task" value="' + task.pid
							+ '" type="checkbox" /></td>'
				}
				html += '<td id="' + task.activityPid + '">'
						+ task.activityName + '</td>' + '<td id="'
						+ task.accountProfilePid + '">'
						+ task.accountProfileName + '</td>' + '<td>'
						+ task.remarks + '</td>' + '</tr>';
			});
			$('#tblAssignTasks').html(html);
		},
		error : function(xhr, error) {
			onError(xhr, error);
		}
	});
}

function loadDownloadedTask(userPid) {
	$('#tblTabUserTaskDownloaded').html('');
	$('#loadingUserTaskDownloaded').html(
			'<img src="/resources/assets/images/content-ajax-loader.gif">');
	$.ajax({
		url : contextPath + '/web/downloaded-user-tasks?userPid=' + userPid
				+ '&plannedDate=' + selectedDate,
		method : 'GET',
		ContentType : 'json',
		success : function(response) {
			var html = '';
			$.each(response, function(index, userTask) {
				var remarks = "";
				var userRemarks = "";
				var taskPid = "";
				var taskListPid = "";
				if (userTask.remarks) {
					remarks = userTask.remarks;
				}
				if (userTask.userRemarks) {
					userRemarks = userTask.userRemarks;
				}
				if (userTask.taskPid) {
					taskPid = userTask.taskPid;
				}
				if (userTask.taskListPid) {
					taskListPid = userTask.taskListPid;
				}
				html += '<tr data-pid="' + userTask.pid + '" data-task-pid="'
						+ taskPid + '" data-task-list-pid="' + taskListPid
						+ '">' + '<td id="' + userTask.activityPid + '">'
						+ userTask.activityName + '</td>' + '<td id="'
						+ userTask.accountProfilePid + '">'
						+ userTask.accountProfileName + '</td>' + '<td>'
						+ remarks + '</td>' + '<td>' + userRemarks + '</td>'
						+ '<td>' + userTask.taskPlanStatus + '</td>' + '</tr>';
			});
			$('#tblTabUserTaskDownloaded').html(html);
			$('#loadingUserTaskDownloaded').html('');
		},
		error : function(e) {
			$('#loadingUserTaskDownloaded').html('');
		}
	});
}

function enableRemarkColumn(el) {
	$(el).find('textarea').prop('disabled', false);
}

function disableRemarkColumn(el) {
	$(el).html($(el).val());
	$(el).prop('disabled', true);
}
