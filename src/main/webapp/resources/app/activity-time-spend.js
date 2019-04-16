if (!this.ActivityTimeSpend) {
	this.ActivityTimeSpend = {};
}

(function() {
	'use strict';

	var activityTimeSpendContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// load today data
		ActivityTimeSpend.filter();

		$('#btnDownload').on('click', function() {
			var tblActivityTimeSpend = $("#tblActivityTimeSpend tbody");
			if (tblActivityTimeSpend.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblActivityTimeSpend[0].textContent == "No data available") {
				alert("no values available");
				return;
			}

			downloadXls();
		});
	});

	function downloadXls() {
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblActivityTimeSpend").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "activitytimespend";

		clonedTable.table2excel({
			// exclude CSS class
			// exclude : ".odd .even",
			// name : "Dynamic Document Form",
			filename : excelName, // do not include extension
		// fileext : ".xls",
		// exclude_img : true,
		// exclude_links : true,
		// exclude_inputs : true
		});
	}

	ActivityTimeSpend.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyActivityTimeSpend').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : activityTimeSpendContextPath
							+ "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						activityPid : $("#dbActivity").val(),
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(executiveTaskExecutions) {
						$('#tBodyActivityTimeSpend').html("");
						if (executiveTaskExecutions.length == 0) {
							$('#tBodyActivityTimeSpend')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										executiveTaskExecutions,
										function(index, executiveTaskExecution) {
											console.log(executiveTaskExecution);
											if (executiveTaskExecution.startTime != null
													&& executiveTaskExecution.endTime != null) {
												$('#tBodyActivityTimeSpend')
														.append(
																"<tr><td>"
																		+ executiveTaskExecution.employeeName
																		+ "</td><td>"
																		+ executiveTaskExecution.accountProfileName
																		+ "</td><td>"
																		+ executiveTaskExecution.activityName
																		+ "</td><td>"
																		+ convertDateTimeFromServer(executiveTaskExecution.startTime)
																		+ "--<span style='background-color: antiquewhite;'>"
																		+ executiveTaskExecution.startLocation
																		+ "</span ></td><td>"
																		+ convertDateTimeFromServer(executiveTaskExecution.endTime)
																		+ "--<span style='background-color: antiquewhite;'>"
																		+ executiveTaskExecution.location
																		+ "</span></td><td style='width: 10%;'>"
																		+ executiveTaskExecution.timeSpend
																		+ "</td></tr>");
											}
										});
					}
				});
	}

	ActivityTimeSpend.showDatePicker = function() {
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

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
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

	ActivityTimeSpend.closeModalPopup = function(el) {
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