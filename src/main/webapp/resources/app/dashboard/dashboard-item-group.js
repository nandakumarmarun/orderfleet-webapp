// methods in a closure to avoid creating global variables.

if (!this.DbItemGroup) {
	this.DbItemGroup = {};
}

(function() {
	'use strict';

	let dbItemGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	let createEditForm = $("#dbItemGroupForm");
	let deleteForm = $("#deleteForm");

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

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateDashboardItemGroup(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteDashboardItemGroup(e.currentTarget.action);
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});

				$('#btnSaveDbItems').click(function() {
					saveAssignedDashboardItems();
				});
				
				$('#btnSaveUsers').click(function() {
					saveAssignedUsers();
				});
			});

	function createUpdateDashboardItemGroup(el) {
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			data : {
				id : $('#hdnDbItemGroupId').val(),
				name : $('#field_name').val(),
				sortOrder : $('#field_sortOrder').val(),
				appearInDbSummary : $('#chkboxDbSummary').prop('checked')
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showDashboardItemGroup(id) {
		$.ajax({
			url : dbItemGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_sortOrder').text(data.sortOrder);
				$('#lbl_dbSummary').text(data.appearInDashboardSummary);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editDashboardItemGroup(id) {
		$.ajax({
			url : dbItemGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_sortOrder').val((data.sortOrder));
				$('#chkboxDbSummary').prop('checked', data.appearInDashboardSummary); 
				$('#hdnDbItemGroupId').val(data.id);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteDashboardItemGroup(actionurl, id) {
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

	DbItemGroup.loadDashboardItems = function(id, obj) {
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#dbItemCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : dbItemGroupContextPath + "/findDashboardItems/" + id,
			type : "GET",
			success : function(data) {
				$('#hdnDbItemGroupId').val(id)
				if (data) {
					$.each(data, function(index, stage) {
						$(
								"#dbItemCheckboxes input:checkbox[value="
										+ stage.pid + "]")
								.prop("checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		$("#dbItemModal").modal("show");
	}

	function saveAssignedDashboardItems() {
		$(".error-msg").html("");
		var selectedDbItems = "";

		$.each($("input[name='dashboardItem']:checked"), function() {
			selectedDbItems += $(this).val() + ",";
		});
		if (selectedDbItems == "") {
			if(!confirm("Are you sure!")){
				return
			}
		}
		$.ajax({
			url : dbItemGroupContextPath + "/assign-dashboard-items",
			type : "POST",
			data : {
				id : $('#hdnDbItemGroupId').val(),
				assignedDashboardItemPids : selectedDbItems,
			},
			success : function(status) {
				$("#dbItemModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	DbItemGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDashboardItemGroup(id);
				break;
			case 1:
				editDashboardItemGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dbItemGroupContextPath + "/" + id);
				break;
			case 3:
				$('#hdnDbItemGroupId').val(id);
				loadUsers(id);
				break;
			}
		}
		el.modal('show');
	}
	
	function loadUsers(id) {
		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		$.ajax({
			url : dbItemGroupContextPath + "/users/" + id,
			type : "GET",
			success : function(userPids) {
					$.each(userPids, function(index, userPid) {
						$(
								"#divUsers input:checkbox[value="
										+ userPid + "]").prop("checked",
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
		let selectedUsers = "";

		$.each($("input[name='user']:checked"), function() {
			selectedUsers += $(this).val() + ",";
		});
		if (selectedUsers == "") {
			if(!confirm("Are you sure!")){
				return
			}
		}
		$.ajax({
			url : dbItemGroupContextPath + "/assign-users",
			type : "POST",
			data : {
				id : $('#hdnDbItemGroupId').val(),
				assignedUserPids : selectedUsers,
			},
			success : function(status) {
				$("#assignUsersModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = dbItemGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = dbItemGroupContextPath;
	}
	
	DbItemGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		// funnelModel.pid = null; // reset Stage model;
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

	function searchTable(inputVal) {
		var table = $('#tblDbItems');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(
				function(index, row) {
					var allCells = $(row).find('td');
					if (allCells.length > 0) {
						var found = false;
						allCells.each(function(index, td) {
							if (index == 0) {
								if (filterBy != "all") {
									var val = $(td).find('input').prop(
											'checked');
									var deselected = $(td).closest('tr').find(
											'td:eq(2)').text();
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

})();