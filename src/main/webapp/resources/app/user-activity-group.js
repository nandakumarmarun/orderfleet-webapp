// Create a UserActivityGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserActivityGroup) {
	this.UserActivityGroup = {};
}

(function() {
	'use strict';

	var userActivityGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var userActivityGroupModel = {
		userPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveActivityGroup').on('click', function() {
			saveAssignedActivityGroups();
		});
	});

	function loadUserActivityGroup(userPid) {
		console.log(userPid);
		$("#activityGroupsCheckboxes input:checkbox").attr('checked', false);
		userActivityGroupModel.userPid = userPid;
		// clear all check box
		$.ajax({
			url : userActivityGroupContextPath + "/" + userPid,
			type : "GET",
			success : function(activityGroups) {
				if (activityGroups) {
					$.each(activityGroups, function(index, activity) {
						$(
								"#activityGroupsCheckboxes input:checkbox[value="
										+ activity.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedActivityGroups() {
		$(".error-msg").html("");
		var selectedActivityGroups = "";

		$.each($("input[name='activityGroup']:checked"), function() {
			selectedActivityGroups += $(this).val() + ",";
		});

		if (selectedActivityGroups == "") {
			$(".error-msg").html("Please select Activity Groups");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : userActivityGroupContextPath + "/save",
			type : "POST",
			data : {
				userPid : userActivityGroupModel.userPid,
				assignedActivityGroups : selectedActivityGroups,
			},
			success : function(status) {
				$("#activityGroupsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userActivityGroupContextPath;
	}

	UserActivityGroup.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserActivityGroup(id);
				break;
			}
		}
		el.modal('show');
	}

	UserActivityGroup.closeModalPopup = function(el) {
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