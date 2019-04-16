if (!this.RootPlanSubgroupApprove) {
	this.RootPlanSubgroupApprove = {};
}

(function() {
	'use strict';

	var rootPlanSubgroupApproveContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#rootPlanSubgroupApproveForm");
	var updateForm = $("#rootPlanSubgroupApproveEditForm");
	var deleteForm = $("#deleteForm");
	var rootPlanSubgroupApproveModel = {
		id : null,
		attendanceStatusSubgroupId : null,
		userPid : null,
		approveRequired : true,
		rootPlanBased : true,

	};

	// Specify the validation rules
	var validationRules = {
	
			attendanceStatusSubgroupId : {
			valueNotEquals : "-1"
		},
		userPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		
			attendanceStatusSubgroupId : {
			valueNotEquals : "This field is required."
		},
		userPid : {
			valueNotEquals : "This field is required."
		}
	};

	$(document).ready(
			function() {

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "Please Select");

				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateRootPlanSubgroupApprove(form);
					}
				});
				updateForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						updateRootPlanSubgroupApprove(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteRootPlanSubgroupApprove(e.currentTarget.action);
				});

			
				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				// select all checkbox in table tblPriceLevels
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function searchTable(inputVal) {
		var table = $('#tblPriceLevels');
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

	function createUpdateRootPlanSubgroupApprove(el) {
		rootPlanSubgroupApproveModel.userPid = $('#field_user').val();
		rootPlanSubgroupApproveModel.attendanceStatusSubgroupId = $('#field_attendanceStatusSubgroup').val();
		rootPlanSubgroupApproveModel.approvalRequired = $('#field_approvalRequired').prop('checked');
		rootPlanSubgroupApproveModel.rootPlanBased = $('#field_rootPlanBased').prop('checked');
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(rootPlanSubgroupApproveModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	function updateRootPlanSubgroupApprove(el) {
		rootPlanSubgroupApproveModel.userPid = $('#field_userEdit').val();
		rootPlanSubgroupApproveModel.attendanceStatusSubgroupId = $('#field_attendanceStatusSubgroupEdit').val();
		rootPlanSubgroupApproveModel.approvalRequired = $('#field_approvalRequiredEdit').prop('checked');
		rootPlanSubgroupApproveModel.rootPlanBased = $('#field_rootPlanBasedEdit').prop('checked');
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(rootPlanSubgroupApproveModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showRootPlanSubgroupApprove(id) {
		$.ajax({
			url : rootPlanSubgroupApproveContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_user').text(data.userName);
				$('#lbl_attendanceSubgroup').text(data.attendanceStatusSubgroupName);
				$('#lbl_approvalRequired').text(data.approvalRequired);
				$('#lbl_rootPlanBased').text(data.rootPlanBased);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editRootPlanSubgroupApprove(id) {
		$.ajax({
			url : rootPlanSubgroupApproveContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data);
				$('#field_userEdit').val(data.userPid);
				$('#field_attendanceStatusSubgroupEdit').val(data.attendanceStatusSubgroupId);
				$('#field_approvalRequiredEdit').prop("checked", data.approvalRequired);
				$('#field_rootPlanBasedEdit').prop("checked", data.rootPlanBased);
				
				rootPlanSubgroupApproveModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteRootPlanSubgroupApprove(actionurl, id) {
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
		window.location = rootPlanSubgroupApproveContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = rootPlanSubgroupApproveContextPath;
	}

	RootPlanSubgroupApprove.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showRootPlanSubgroupApprove(id);
				break;
			case 1:
				updateResetForm();
				editRootPlanSubgroupApprove(id);
				updateForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', rootPlanSubgroupApproveContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	RootPlanSubgroupApprove.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		rootPlanSubgroupApproveModel.id = null; // reset rootPlanSubgroupApprove model;
	}
	function updateResetForm() {
		$('.alert').hide();
		updateForm.trigger("reset"); // clear form fields
		updateForm.validate().resetForm(); // clear validation messages
		updateForm.attr('method', 'POST'); // set default method
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