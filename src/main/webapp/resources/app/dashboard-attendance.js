if (!this.DashboardAttendance) {
	this.DashboardAttendance = {};
}

(function() {
	'use strict';

	var dashboardAttendanceContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#dashboardAttendanceForm");
	var deleteForm = $("#deleteForm");
	var dashboardAttendanceModel = {
		id : null,
		name : null,
		attendanceStatus : null,
		attSSubgroupId : null,
		attSSubgroupName : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};

	$(document).ready(function() {

		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		$('#btnSaveUsers').click(function() {
			saveAssignedUsers();
		});
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateDashboardAttendance(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDashboardAttendance(e.currentTarget.action);
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});

	});
	
	function loadUsers(id) {
		dashboardAttendanceModel.id = id;
		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		$.ajax({
			url : dashboardAttendanceContextPath + "/users/" + id,
			type : "GET",
			success : function(users) {
					$.each(users, function(index, user) {
						
						$(
								"#divUsers input:checkbox[value="
										+ user.pid + "]").prop("checked",
								true);
					});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function saveAssignedUsers() {

		$(".error-msg").html("");
		var selectedUsers = "";

		$.each($("input[name='user']:checked"), function() {
			selectedUsers += $(this).val() + ",";
		});

		if (selectedUsers == "") {
			$(".error-msg").html("Please select Users");
			return;
		}
		$.ajax({
			url : dashboardAttendanceContextPath + "/assign-users",
			type : "POST",
			data : {
				id : dashboardAttendanceModel.id,
				assignedUsers : selectedUsers,
			},
			success : function(status) {
				$("#assignUsersModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	DashboardAttendance.onChangeAttendance = function() {
		var groupOrSubgroup = $('#field_groupOrSubgroup').val();
		if (groupOrSubgroup == 'group') {
			$('#divAttendanceStatusSubgroup').hide();
			$('#divAttendanceStatus').show();
		} else {
			$('#divAttendanceStatus').hide();
			$('#divAttendanceStatusSubgroup').show();
			
		}
	}


	function createUpdateDashboardAttendance(el) {
		dashboardAttendanceModel.name = $('#field_name').val();
		if($('#field_groupOrSubgroup').val()==-1){
			addErrorAlert("Please Select Group or SubGroup");
			return;
		}
		if ($('#field_groupOrSubgroup').val() == 'group') {
			dashboardAttendanceModel.attendanceStatus = $('#field_attendanceStatus').val();
		}else{
			dashboardAttendanceModel.attSSubgroupId = $('#field_attendanceStatusSubgroup').val();
		}
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(dashboardAttendanceModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editDashboardAttendance(id) {
		$.ajax({
			url : dashboardAttendanceContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);

				if (data.attendanceStatus =="LEAVE" || data.attendanceStatus =="PRESENT") {
					$("#field_groupOrSubgroup").val("group");
					$("#field_attendanceStatus").val(data.attendanceStatus);
					$('#divAttendanceStatusSubgroup').hide();
					$('#divAttendanceStatus').show();
					
				} else {
					$("#field_groupOrSubgroup").val("subgroup");
					$('#field_attendanceStatusSubgroup').val(data.attSSubgroupId);
					$('#divAttendanceStatus').hide();
					$('#divAttendanceStatusSubgroup').show();

				}
				// set id
				dashboardAttendanceModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	
	function showDashboardAttendance(id) {
		$.ajax({
			url : dashboardAttendanceContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_attendanceStatus').text((data.attendanceStatus == null ? "" : data.attendanceStatus));
				$('#lbl_attendanceStatusSubgroup').text((data.attSSubgroupName == null ? "" : data.attSSubgroupName));
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteDashboardAttendance(actionurl, id) {
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
		window.location = dashboardAttendanceContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = dashboardAttendanceContextPath;
	}

	DashboardAttendance.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDashboardAttendance(id);
				break;
			case 1:
				editDashboardAttendance(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dashboardAttendanceContextPath + "/" + id);
				break;
			case 3:
				loadUsers(id);
				break;
			}
		}
		el.modal('show');
	}

	function searchTable(inputVal) {
		var table = $('#tBodyDashboardAttendance');
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

	function resetForm() {
		$('#divAttendanceStatus').hide();
		$('#divAttendanceStatusSubgroup').hide();

		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		dashboardAttendanceModel.id = null; // reset dashboardAttendance model;
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