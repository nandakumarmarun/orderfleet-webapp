// Create a UserTaskListAssignment object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserTaskListAssociation) {
	this.UserTaskListAssociation = {};
}

(function() {
	'use strict';

	var userTaskListAssociationContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#userTaskListAssignmentForm");
	var deleteForm = $("#deleteForm");
	var userTaskListAssignmentModel = {
		pid : null,
		userPid : null,
		taskListPid : null,

	};

	// Specify the validation rules
	var validationRules = {
		userPid : {
			valueNotEquals : "-1"
		},
		taskListPid : {
			valueNotEquals : "-1"
		}

	};

	// Specify the validation error messages

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
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
		$('#btnDownload').on('click', function() {
			var tblCompany = $("#tbltasklistuser tbody");
			if (tblCompany.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblCompany[0].textContent == "No data available") {
				alert("no values available");
				return;
			}

			downloadXls();
		});
	});

	
	function downloadXls() {
		console.log("Ready to download")
	
		var clonedTable = $("#tbodytasklistuser").clone();
		
		clonedTable.find('[style*="display: none"]').remove();
		clonedTable.find('.noExl').remove();
		clonedTable.find('.noActive').remove();

		var excelName = "EmployeeWiseList";

		clonedTable.table2excel({
			

		
			filename : excelName, // do not include extension
		
		});
	}
	function createUpdateUserTaskListAssignment(el) {

		userTaskListAssignmentModel.userPid = $('#field_executiveUser').val();
		userTaskListAssignmentModel.userName = $('#field_executiveUser').find(
				'option:selected').text();
		userTaskListAssignmentModel.taskListPid = $('#field_taskList').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userTaskListAssignmentModel),
			success : function(userTasklist) {
			
				alert(userTaskListAssignmentModel.userName +"  has assigned  "+ $('#field_taskList').find('option:selected').text())
				onSaveSuccess(userTasklist);
			

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	
	UserTaskListAssociation.loadDataTable = function()
	{
		var user = $('#dbEmployee').val();
		console.log(user);
		
		$.ajax({
			method : 'GET',
			url : userTaskListAssociationContextPath +"/getList",
			data :{
				userPid :user
			},
		success : function(userTaskList) {
			loadDataTables(userTaskList);
		},
		error : function(xhr, error) {
			onError(xhr, error);
		}
		
		});
		
		
	}
	function loadDataTables(userTaskList) {

		$('#tbodytasklistuser').html("");

		$
				.each(
						userTaskList,
						function(index, userTasklists) {
							
             console.log(index)
							$('#tbodytasklistuser')
									.append(
											"<tr class ='noExl' style='"
													+ userTasklists.pid
													+ "' data-parent=\"\"><td class='tableexport-string target' style='font-weight :bold'>"
													+ userTasklists.userName
													+ "</td><td style='font-weight :bold'>"
													+ userTasklists.taskList.name
													+ "</td>" 
												    +"<td><button type='button' class='btn btn-info' onclick='UserTaskListAssociation.showModalPopup($(\"#deleteModal\"),\""
													+ userTasklists.pid
													+ "\",0);'>Delete</button>"
													+ "</td></tr>");

             if(index==0){
            	 $('#tbodytasklistuser')
					.append(
						"<tr class ='tractive' data-parent='"
						+ userTasklists.pid
						+ "'><th>"
						+"EmployeeName"+"</th><th>"
						+ "ActivityName"
						+ "</th><th>"
						+ "AccountProfileName"
						+ "</th><th>"
						+ "TaskListName"
						+ "</th></tr>");
             }
             else{
            	 $('#tbodytasklistuser')
					.append(
						"<tr class ='noActive' data-parent='"
						+ userTasklists.pid
						+ "'><th>"
						+"EmployeeName"+"</th><th>"
						+ "ActivityName"
						+ "</th><th>"
						+ "AccountProfileName"
						+ "</th><th>"
						+ "TaskListName"
						+ "</th></tr>");
             }
             
							
							
							$
							.each(
									userTasklists.taskList.tasks,
								
									function(index,task) {
										$(
										'#tbodytasklistuser')
										.append(
												"<tr class='tableexport-ignore'"
												
												+ "' data-id=\"\""
												+ " data-parent='"
												+ userTasklists.pid
												+"'>" 
												+"<td>"+userTasklists.userName+"</td>"
												+"<td>"+task.activity.name +"</td> "
												+"<td>"+task.accountProfile.name
												+"</td><td>"+userTasklists.taskList.name
												+ "</td></tr>");
									});
						});
		
	}

	
	function deleteUserTaskListAssignment(actionurl, id) {
		console.log("Pid:"+id)
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userTaskListAssociationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userTaskListAssociationContextPath;
	}

	UserTaskListAssociation.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			
			
			case 0:
				deleteForm.attr('action', userTaskListAssociationContextPath
						+ "/" + id);
				break;
			
			}
		}
		el.modal('show');
	}

	UserTaskListAssociation.closeModalPopup = function(el) {
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