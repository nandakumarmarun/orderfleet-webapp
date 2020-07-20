// Create a Notification object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Notification) {
	this.Notification = {};
}

(function() {
	'use strict';

	var notificationContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		// load today data
		Notification.filter();

		// table search
		$('#search').keyup(function() {
			searchTable($(this).val());
		});
	});

	Notification.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}

		let empPids = $('#dbEmployee').val();
		if ("no" == empPids) {
			empPids = $('#dbEmployee option').map(function() {
				if ($(this).val() != 'no') {
					return $(this).val();
				}
			}).get().join(',');
		}
		$('#tBodyNotification').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : notificationContextPath + "/filter",
					type : 'GET',
					data : {
						employeePids : empPids,
						status : $("#dbStatus").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(notifications) {
						$('#tBodyNotification').html("");
						if (notifications.length == 0) {
							$('#tBodyNotification')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}
						console.log(notifications);

						$
								.each(
										notifications,
										function(index, notification) {
											let importent = "";
											var resendTime = "-";
											if (notification.isImportant) {
												importent = "important";
												resendTime = convertDateTimeFromServer(notification.lastModifiedDate);

											} else {
												importent = "not important";
											}
											let status;
											if (notification.msgStatus == "NONE") {
												status = "<span class='label label-default'>"
														+ notification.msgStatus
														+ "</span>";

											} else if (notification.msgStatus == "FAILED") {
												status = "<span class='label label-danger'>"
														+ notification.msgStatus
														+ "</span>";
												status += "<br />"
														+ notification.failedReason;
											} else {
												status = "<span class='label label-success'>"
														+ notification.msgStatus
														+ "</span>";
											}
											$('#tBodyNotification')
													.append(
															"<tr><td>"
																	+ notification.executiveName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(notification.createdDate)
																	+ "</td><td>"
																	+ importent
																	+ "</td><td>"
																	+ resendTime
																	+ "</td><td>"
																	+ notification.title
																	+ "</td><td>"
																	+ notification.message
																	+ "</td><td>"
																	+ status
																	+ "</td><td>"
																	+ convertDateTimeFromServer(notification.lastModifiedDateDetail)
																	+ "</td></tr>");
										});

					}
				});
	}
	function searchTable(inputVal) {
		console.log(inputVal);
		var table = $('#dtNotification');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
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

	Notification.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
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