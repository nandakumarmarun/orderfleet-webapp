// Create a DayPlan object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DayPlan) {
	this.DayPlan = {};
}

(function() {
	'use strict';

	var dayPlanContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtDate").datepicker({ dateFormat: "dd-mm-yy" });
	});

	DayPlan.filter = function() {
		if ($("#txtDate").val() == "" || $("#dbEmployee").val() == "no") {
			$('#tBodyDayPlans').html("<tr><td colspan='3' align='center'>Please select user and date</td></tr>");
			return;
		}
		$('#tBodyDayPlans').html("<tr><td colspan='3' align='center'>Please wait...</td></tr>");
		
		$.ajax({
					url : dayPlanContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						date : $("#txtDate").val()
					},
					success : function(dayPlans) {
						$('#tBodyDayPlans').html("");
						if (dayPlans.length == 0) {
							$('#tBodyDayPlans')
									.html("<tr><td colspan='3' align='center'>No data available</td></tr>");
							return;
						}
						$.each(dayPlans, function(index, dayPlan) {

							$('#tBodyDayPlans').append(
									"<tr><td>" + dayPlan.name + "</td><td>" + dayPlan.taskType + "</td><td>"
											+ dayPlan.status + "</td></tr>");
						});
					}
				});
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
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