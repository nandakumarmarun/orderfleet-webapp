if (!this.ActivityType) {
	this.ActivityType = {};
}

(function() {
	'use strict';

	var activityTypeContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#activityTypeForm");
	var deleteForm = $("#deleteForm");
	var activityTypeModel = {
		pid : null,
		name : null,
		alias : null,
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
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateActivityType(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteActivityType(e.currentTarget.action);
		});
		
		$('#btnActivateActivityType').on('click', function() {
			activateAssignedActivityType();
		});
		
		$('#btnSaveActivities').click(function() {
			saveAssignedActivities();
		});
		
		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});
	
	function saveAssignedActivities() {

		$(".error-msg").html("");
		var selectedActivities = "";

		$.each($("input[name='activity']:checked"), function() {
			selectedActivities += $(this).val() + ",";
		});

		if (selectedActivities == "") {
			$(".error-msg").html("Please select Activity");
			return;
		}
		$.ajax({
			url : activityTypeContextPath + "/assignActivities",
			type : "POST",
			data : {
				pid : activityTypeModel.pid,
				assignedActivities : selectedActivities,
			},
			success : function(status) {
				$("#activitiesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function activateAssignedActivityType() {
		$(".error-msg").html("");
		var selectedActivityType = "";

		$.each($("input[name='activityType']:checked"), function() {
			selectedActivityType += $(this).val() + ",";
		});

		if (selectedActivityType == "") {
			$(".error-msg").html("Please select ActivityType");
			return;
		}
		$.ajax({
			url : activityTypeContextPath + "/activateActivityType",
			type : "POST",
			data : {
				activityTypes : selectedActivityType,
			},
			success : function(status) {
				$("#enableActivityTypeModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function searchTable(inputVal) {
		var table = $('#allActivities');
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
									} else if (filterBy == "deactivated") {
										if (deselected == "true") {
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
	
	ActivityType.loadActivities = function(pid, obj) {
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#activitiesCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : activityTypeContextPath + "/findActivities/" + pid,
			type : "GET",
			success : function(data) {
				activityTypeModel.pid = pid;
				if (data) {
					$.each(data, function(index, activity) {
						$(
								"#activitiesCheckboxes input:checkbox[value="
										+ activity.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		$("#activitiesModal").modal("show");
	}
	
	function createUpdateActivityType(el) {
		activityTypeModel.name = $('#field_name').val();
		activityTypeModel.alias = $('#field_alias').val();
		activityTypeModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(activityTypeModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showActivityType(id) {
		$.ajax({
			url : activityTypeContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_description').text((data.description == null ? "" : data.description));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editActivityType(id) {
		$.ajax({
			url : activityTypeContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_description').val((data.description == null ? "" : data.description));
				// set pid
				activityTypeModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteActivityType(actionurl, id) {
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

	ActivityType.setActive=function(name,pid,active){
		activityTypeModel.pid=pid;
		activityTypeModel.activated=active;
		activityTypeModel.name=name;
		if(confirm("Are you confirm?")){
			$.ajax({
				url:activityTypeContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(activityTypeModel),
				success:function(data){
					onSaveSuccess(data);
				},
				error:function(xhr,error){
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = activityTypeContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = activityTypeContextPath;
	}

	ActivityType.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showActivityType(id);
				break;
			case 1:
				editActivityType(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', activityTypeContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	ActivityType.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		activityTypeModel.pid = null; // reset activityType model;
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