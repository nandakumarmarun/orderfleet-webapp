// Create a dayPlanExecutionConfig object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DayPlanExecutionConfig) {
	this.DayPlanExecutionConfig = {};
}

(function() {
	'use strict';

	var dayPlanExecutionConfigContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$('#btnSave').on('click', function() {
			saveDayPlanExecutionConfig();
		});

		$('#btnSaveDuration').on('click', function() {
			saveDuration();
		});
		
		$('#btnSaveUsers').on('click', function() {
			saveAssignedUsers();
		});
		
		getDuration();
	});

	function getDuration() {
		$.ajax({
			url : dayPlanExecutionConfigContextPath
					+ "/account-purchase-history-duration",
			type : "GET",
			success : function(month) {
				console.log(month);
				$("#dbDuration").val(month);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveDuration() {
		$.ajax({
			url : dayPlanExecutionConfigContextPath
					+ "/account-purchase-history-duration",
			type : "POST",
			data : {
				month : $("#dbDuration").val()
			},
			success : function(status) {
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveDayPlanExecutionConfig() {

		var order = [];
		var dayPlanExecutionConfigs = [];
		var status = true;
		$.each($("input[name='dayPlanExecutionConfig']"), function() {
			var name = $(this).val();
			var enabled = $(this).prop('checked');
			var sortOrder = $("#txtOrder" + name).val();
			if (enabled && sortOrder < 1) {
				alert("Enter valid order");
				status = false;
				return;
			}
			if (sortOrder > 0) {
				order.push(sortOrder);
				var duplicate = hasDuplicates(order);
				if (duplicate) {
					alert("Duplicates in order");
					status = false;
					return;
				}
			}

			dayPlanExecutionConfigs.push({
				name : name,
				sortOrder : sortOrder,
				enabled : enabled
			})
		});
		console.log(dayPlanExecutionConfigs);
		if (!status) {
			return;
		}
		if (dayPlanExecutionConfigs.length == 0) {
			return;
		}
		$.ajax({
			url : dayPlanExecutionConfigContextPath,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(dayPlanExecutionConfigs),
			success : function(status) {
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	var dayPlanExecutionConfigId = 
	DayPlanExecutionConfig.showModalPopup = function(id) {
		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		dayPlanExecutionConfigId = id;
		$.ajax({
			url : dayPlanExecutionConfigContextPath + "/" + id,
			type : "GET",
			success : function(users) {
				console.log(users);
				if (users) {
					$.each(users, function(index, user) {
						$("#divUsers input:checkbox[value="+ user.pid + "]").prop("checked",true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		$('#assignUsersModal').modal('show');
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
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : dayPlanExecutionConfigContextPath + "/assignUsers",
			type : "POST",
			data : {
				id : dayPlanExecutionConfigId,
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

	function hasDuplicates(array) {
		var valuesSoFar = Object.create(null);
		for (var i = 0; i < array.length; ++i) {
			var value = array[i];
			if (value in valuesSoFar) {
				return true;
			}
			valuesSoFar[value] = true;
		}
		return false;
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = dayPlanExecutionConfigContextPath;
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