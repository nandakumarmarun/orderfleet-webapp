// Create a Task object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Task) {
	this.Task = {};
}

(function() {
	'use strict';

	var taskContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#taskForm");
	var deleteForm = $("#deleteForm");
	var taskModel = {
		pid : null,
		activityPid : null,
		accountTypePid : null,
		accountProfilePid : null,
		accountPids : null,
		remarks : null
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
		activityPid : {
			valueNotEquals : "-1"
		},
		accountTypePid : {
			valueNotEquals : "-1"
		},
		accountProfilePid : {
			valueNotEquals : "-1"
		},
		accountPids : {
			valueNotEquals : "null"
		},
		remarks : {
			maxlength : 250
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
		remarks : {
			maxlength : "This field cannot be longer than 250 characters."
		},
		accountPids : {
			valueNotEquals : "This field is required."
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
				createUpdateTask(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteTask(e.currentTarget.action);
		});

		loadAllTasks();
		
		$('#btnSearch').click(
				function() {
					searchTable($("#value").val(),
							$('#tBodyTask'));
				});
		
		$('#field_activity').on('change', function() {
			getAccountTypesByActivity();
		});

		$('#field_accountType').on('change', function() {
			getAccountsByType();
		});
		$('#btnActivateTask').on('click', function() {
			activateAssignedTask();
		});
		
		$('input:checkbox.allcheckbox')
		.click(
				function() {
					$(this)
							.closest('table')
							.find(
									'tbody tr td input[type="checkbox"]')
							.prop(
									'checked',
									$(this).prop(
											'checked'));
				});
	});

	Task.filterByActivityAndAccountType = function() {
		var activityPids = [];
		var accountTypePids = [];
		$("#activity").find('input[type="checkbox"]:checked').each(function() {
			activityPids.push($(this).val());
		});
		$("#accountType").find('input[type="checkbox"]:checked').each(function() {
			accountTypePids.push($(this).val());
		});
		$('#tBodyTask').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : taskContextPath + "/filterByActivityAndAccountType",
					method : 'GET',
					data : {
						activityPids : activityPids.join(","),
						accountTypePids : accountTypePids.join(","),
						
					},

					success : function(taskDTOs) {

						$('#tBodyTask').html("");
						// select box for set size
						$('#field_products').html("");
						if (taskDTOs.length == 0) {
							$('#tBodyTask')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$
						.each(
								taskDTOs,
								function(index, task) {
									$('#tBodyTask')
											.append(
													"<tr><td>"
															+ task.activityName
															+ "</td><td>"
															+ task.accountTypeName
															+ "</td><td>"
															+ task.accountProfileName
															+ "</td><td>"
															+ task.remarks
															+ "</td><td>"
															+ spanActivated(
																	task.activated,
																	task.pid)
															+ "</td><td><button type='button' class='btn btn-blue' onclick='Task.showModalPopup($(\"#viewModal\"),\""
															+ task.pid
															+ "\",0);'>View</button>&nbsp;<button type='button' class='btn btn-blue' onclick='Task.showModalPopup($(\"#myModal\"),\""
															+ task.pid
															+ "\",1);'>Edit</button>&nbsp;<button type='button' class='btn btn-danger' onclick='Task.showModalPopup($(\"#deleteModal\"),\""
															+ task.pid
															+ "\",2);'>Delete</button></td></tr>");

								});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	Task.productNameSearch = function(e) {
		var inputVal = e.value;
		$('#tBodyTask').find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 1) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}
	
	function searchTable(inputVal, table) {
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}
	
	function loadAllTasks() {
		$('#tBodyTask').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : taskContextPath + "/filterByActivityAndAccountType",
					method : 'GET',
					data : {
						activityPids : "",
						accountTypePids : ""
					},
					success : function(taskDTOs) {
						$('#tBodyTask').html("");
						// select box for set size
						$('#field_products').html("");
						if (taskDTOs.length == 0) {
							$('#tBodyTask')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										taskDTOs,
										function(index, task) {
											$('#tBodyTask')
													.append(
															"<tr><td>"
																	+ task.activityName
																	+ "</td><td>"
																	+ task.accountTypeName
																	+ "</td><td>"
																	+ task.accountProfileName
																	+ "</td><td>"
																	+ task.remarks
																	+ "</td><td>"
																	+ spanActivated(
																			task.activated,
																			task.pid)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='Task.showModalPopup($(\"#viewModal\"),\""
																	+ task.pid
																	+ "\",0);'>View</button>&nbsp;<button type='button' class='btn btn-blue' onclick='Task.showModalPopup($(\"#myModal\"),\""
																	+ task.pid
																	+ "\",1);'>Edit</button>&nbsp;<button type='button' class='btn btn-danger' onclick='Task.showModalPopup($(\"#deleteModal\"),\""
																	+ task.pid
																	+ "\",2);'>Delete</button></td></tr>");

										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}
	
	function spanActivated(activated, taskPid) {
		var spanActivated = "";
		var task = "'" + taskPid + "'";
		if (activated) {
			spanActivated = '<span class="label label-success" onclick="Task.setActive('
					+ task
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Activated</span>';
		} else {
			spanActivated = '<span class="label label-danger" onclick="Task.setActive('
					+ task
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Deactivated</span>';
		}
		return spanActivated;
	}
	
	function activateAssignedTask() {
		$(".error-msg").html("");
		var selectedTask = "";

		$.each($("input[name='task']:checked"), function() {
			selectedTask += $(this).val() + ",";
		});

		if (selectedTask == "") {
			$(".error-msg").html("Please select Task");
			return;
		}
		$.ajax({
			url : taskContextPath + "/activateTask",
			type : "POST",
			data : {
				tasks : selectedTask,
			},
			success : function(status) {
				$("#enableTaskModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateTask(el) {
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
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showTask(id) {
		$.ajax({
			url : taskContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_activity').text(data.activityName);
				$('#lbl_accountType').text(data.accountTypeName);
				$('#lbl_account').text(data.accountProfileName);
				$('#lbl_remarks').text(data.remarks);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editTask(id) {
		$('#field_account').show();
		$('#field_accounts').hide();
		$.ajax({
			url : taskContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_activity').val(data.activityPid);
				getAccountTypesByActivity()
				$('#field_accountType').val(data.accountTypePid);
				getAccountsByType()
				$('#field_account').val(data.accountProfilePid);
				$('#field_remarks').val(data.remarks);
				// set pid
				taskModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteTask(actionurl, id) {
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

	function getAccountTypesByActivity() {

		// clear account select boox
		$('#field_accountType').html(
				'<option value="-1">Select Account Type</option>');

		if ($('#field_activity').val() == -1) {
			return;
		}
		$.ajax({
			async : false,
			url : location.protocol + '//' + location.host + "/web/tasks/accountTypes/"
					+ $('#field_activity').val(),
			method : 'GET',
			success : function(data) {
				// add accounts to select boox
				$.each(data, function(key, account) {
					$('#field_accountType').append(
							'<option value=' + account.pid + '>' + account.name
									+ '</option>');
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	Task.setActive=function(pid,activated){
		taskModel.pid=pid;
		taskModel.activated=activated;
		
		if(confirm("Are you confirm?")){
		$.ajax({
			url:taskContextPath+"/changeStatus",
			method:'POST',
			contentType : "application/json; charset=utf-8",
				data:JSON.stringify(taskModel),
				success:function(data){
					onSaveSuccess(data)
				},
		
				error:function(xhr,error){
					onError(xhr, error)
				}
		});
	}}
	
	function getAccountsByType() {

		// clear account select boox
		$('#field_account').html('');
		$('#field_accounts').html('');
		if ($('#field_accountType').val() == -1) {
			return;
		}
		$.ajax({
			async : false,
			url : location.protocol + '//' + location.host + "/web/tasks/accounts/" ,
			method : 'GET',
			data : {
				activityPid : $('#field_activity').val(),
				accountTypePid:$('#field_accountType').val(),
			},
			success : function(status) {
				// add accounts to select boox
				console.log(status);
				$.each(status, function(key, account) {
					$('#field_account').append(
							'<option value=' + account.pid + '>' + account.name
									+ '</option>');
					$('#field_accounts').append(
							'<option value=' + account.pid + '>' + account.name
									+ '</option>');
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = taskContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = taskContextPath;
	}

	Task.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showTask(id);
				break;
			case 1:
				editTask(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', taskContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	Task.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('#field_accounts').show();
		$('#field_account').hide();
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		taskModel.pid = null; // reset task model;
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