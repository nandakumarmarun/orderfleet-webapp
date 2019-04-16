// Create a UnableFindLocations object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UnableFindLocations) {
	this.UnableFindLocations = {};
}

(function() {
	'use strict';

	var unableFindLocationContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

		$("#btnLoadLocation").click(function() {
			updateLocation();

		});
	});

	function updateLocation() {

		$('#tb_unableLoc').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");

		$.ajax({
			url : unableFindLocationContextPath + "/updateLocation",
			method : 'GET',
			data : {
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val()
			},
			success : function(data) {
				updateTable(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	UnableFindLocations.filter = function() {

		$('#tb_unableLoc').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : unableFindLocationContextPath + "/filter",
			method : 'GET',
			data : {
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val()
			},
			success : function(data) {
				updateTable(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function updateTable(data) {

		$('#lblTotalCount').html(data.length);

		$('#tb_unableLoc').html("");
		if (data.length == 0) {
			$('#tb_unableLoc')
					.html(
							"<tr><td colspan='4' align='center'>No data available</td></tr>");
			return;
		}
		$.each(data, function(index, execution) {
			$('#tb_unableLoc').append(
					"<tr><td>" + execution.locationType + "</td><td>"
							+ execution.latitude + "</td><td>"
							+ execution.longitude + "</td><td>"
							+ execution.location + "</td></tr>");
		});
	}

	UnableFindLocations.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

	UnableFindLocations.closeModalPopup = function(el) {
		el.modal('hide');
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

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
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