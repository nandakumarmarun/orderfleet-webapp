// Create a DashboardActivity object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DashboardActivity) {
	this.DashboardActivity = {};
}

(function() {
	'use strict';

	var dashboardActivityContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#btnSaveActivities").click(function() {
			saveAssignedActivities();
		});
	});

	DashboardActivity.assignActivities = function() {
		// clear all check box
		$("#divActivities input:checkbox").attr('checked', false);
		$.ajax({
			url : dashboardActivityContextPath + "/edit",
			type : "GET",
			success : function(activities) {
				$.each(activities,
						function(index, activity) {
							$(
									"#divActivities input:checkbox[value="
											+ activity.pid + "]").prop(
									"checked", true);
						});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		$("#assignActivitiesModal").modal("show");
	}

	function saveAssignedActivities() {

		$(".error-msg").html("");
		var selectedActivities = [];

		$.each($("input[name='activity']:checked"), function() {
			selectedActivities.push($(this).val());
		});
		if (selectedActivities.length == 0) {
			$(".error-msg").html("Please select activities");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : dashboardActivityContextPath,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedActivities),
			success : function(status) {
				$("#assignActivitiesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = dashboardActivityContextPath;
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