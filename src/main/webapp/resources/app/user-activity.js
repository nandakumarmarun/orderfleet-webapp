// Create a UserActivity object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserActivity) {
	this.UserActivity = {};
}

(function() {
	'use strict';

	var userActivityContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var userActivityModel = {
		userPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveActivity').on('click', function() {
			saveAssignedActivities();
		});
	});

	function loadUserActivity(userPid) {
		console.log(userPid);
		$("#activitiesCheckboxes input:checkbox").attr('checked', false);
		userActivityModel.userPid = userPid;
		// clear all check box
		$.ajax({
			url : userActivityContextPath + "/" + userPid,
			type : "GET",
			success : function(activities) {
				if (activities) {
					$.each(activities, function(index, activity) {
						$("#activitiesCheckboxes input:checkbox[value="+ activity.pid + "]").prop("checked",true);
						$("#planThrouchOnly" + activity.pid).prop("checked",activity.planThrouchOnly);
						$("#excludeAccountsInPlan" + activity.pid).prop("checked",activity.excludeAccountsInPlan);
						$("#saveActivityDuration" + activity.pid).prop("checked",activity.saveActivityDuration);
						$("#interimSave" + activity.pid).prop("checked",activity.interimSave);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedActivities() {
		$(".error-msg").html("");
		var selectedActivities = [];
		$.each($("input[name='activity']:checked"), function() {
			var pid = $(this).val();
			var planThrouchOnly = $("#planThrouchOnly" + pid).prop("checked");
			var excludeAccountsInPlan = $("#excludeAccountsInPlan" + pid).prop("checked");
			var saveActivityDuration = $("#saveActivityDuration" + pid).prop("checked");
			var interimSave = $("#interimSave" + pid).prop("checked");
			selectedActivities.push({
				pid : pid,
				planThrouchOnly : planThrouchOnly,
				excludeAccountsInPlan : excludeAccountsInPlan,
				saveActivityDuration:saveActivityDuration,
				interimSave:interimSave
			});
		});
		if (selectedActivities.length == 0) {
			$(".error-msg").html("Please select Activities");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : userActivityContextPath + "/save/"+userActivityModel.userPid,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedActivities),
			success : function(status) {
				$("#activitiesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userActivityContextPath;
	}

	UserActivity.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserActivity(id);
				break;
			}
		}
		el.modal('show');
	}

	UserActivity.closeModalPopup = function(el) {
		el.modal('hide');
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