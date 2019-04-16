// Create a EmployeeUser object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.EmployeeUser) {
	this.EmployeeUser = {};
}
var employeePid = "";

(function() {
	'use strict';

	var employeeUserContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var employeeUserModel = {
		userPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		$('#btnSaveUser').on('click', function() {
			saveAssignedUser();
		});
	});

	function searchTable(inputVal) {
		var table = $('#tbodyemployeeUsers');
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
	
	function loadEmployeeUser(employeeName) {
		
		$("#EmployeeName").html(employeeName);
		
		$.ajax({
			url : employeeUserContextPath + "/" + employeePid,
			type : "GET",
			success : function(employeeUser) {
				console.log(employeeUser.userPid);
				if (employeeUser.userPid != null) {
					$('#userId').val(employeeUser.userPid);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedUser() {

		$(".error-msg").html("");

		var userPid = $("#userId").val();
		if (userPid == "-1" || $('#userId :selected').text() == '') {
			$(".error-msg").html("Please select user");
			return;
		}

		$.ajax({
			url : employeeUserContextPath + "/check",
			type : "POST",
			data : {
				userPid : userPid,
				employeePid : employeePid,
			},
			success : function(data) {
				console.log("---");
				console.log(data);
				console.log("---");
				var message ="Are you sure you want to assing this user";
				if(data != null && data != "" ){
					message =  ' User : "'+data.userFirstName+'" is already assigned with \n Employee : "'+data.name+'"'
								+' \n\n Would you like to release User : "'+data.userFirstName+'"' 
								+' from Employee "'+data.name+'"';
				}
					
					if(confirm(message)) {
						$.ajax({
							url : employeeUserContextPath + "/save",
							type : "POST",
							data : {
								userPid : userPid,
								employeePid : employeePid,
							},
							success : function(status) {
								$("#usersModal").modal("hide");
								onSaveSuccess(status);
							},
							error : function(xhr, error) {
								console.log(".............................");
								onError(xhr, error);
							},
						});
					}
				
				
			},
			error : function(xhr, error) {
				console.log("............*............");
				onError(xhr, error);
			},
		});

	}

	

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = employeeUserContextPath;
	}

	EmployeeUser.showModalPopup = function(el, pid, action, employeeName) {
		resetForm();
		if (pid) {
			switch (action) {
			case 0:
				employeePid = pid;
				loadEmployeeUser(employeeName);
				break;
			}
		}
		el.modal('show');
	}

	
	function resetForm() {
		$('.alert').hide();
		$(".error-msg").html("");
		$('#userId').val("-1");
		employeePid = null; // reset employeePid;
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