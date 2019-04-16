// Create a UserDistance object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserDistance) {
	this.UserDistance = {};
}

(function() {
	'use strict';

	var userDistanceContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		UserDistance.filter();
	});

	UserDistance.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
	}
	UserDistance.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyUserDocument').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : userDistanceContextPath + "/filter",
			type : 'GET',
			data : {
				userPid : $("#dbUser").val(),
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val()
			},
			success : function(userDistances) {
				$('#tBodyUserDocument').html("");
				$.each(userDistances, function(index, userDistance) {
					$('#tBodyUserDocument').append(
							"<tr><td>" + userDistance.userName + "</td><td>"
									+ userDistance.date + "</td><td>"
									+ userDistance.kilometre + "</td><td>"
									+ userDistance.startLocation + "</td><td>"
									+ userDistance.endLocation + "</td></tr>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userDistanceContextPath;
	}

	UserDistance.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserDistance(id);
				break;
			}
		}
		el.modal('show');
	}

	UserDistance.closeModalPopup = function(el) {
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