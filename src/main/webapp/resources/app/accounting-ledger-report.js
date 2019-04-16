// Create a AccountingLedgerReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountingLedgerReport) {
	this.AccountingLedgerReport = {};
}

(function() {
	'use strict';

	var accountingLedgerReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// load today data
		AccountingLedgerReport.filter();
	});

	AccountingLedgerReport.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyAccountingLedgerReport').html(
				"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		$('#lblTotalByAmount').text(0.00);
		$('#lblTotalToAmount').text(0.00);
		$
				.ajax({
					url : accountingLedgerReportContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						accountPid : $("#dbAccount").val(),
						documentPid : $("#dbDocument").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(accountingLedgerReports) {
						$('#tBodyAccountingLedgerReport').html("");
						if (accountingLedgerReports.length == 0) {
							$('#tBodyAccountingLedgerReport')
									.html(
											"<tr><td colspan='8' align='center'>No data available</td></tr>");
							return;
						}
						var byTotal = 0.00;
						var toTotal = 0.00;
						$
								.each(
										accountingLedgerReports,
										function(index, accountingLedgerReport) {
											byTotal += accountingLedgerReport.byAmount;
											toTotal += accountingLedgerReport.toAmount;
											$('#tBodyAccountingLedgerReport')
													.append(
															"<tr><td>"
																	+ accountingLedgerReport.employeeName
																	+ "</td><td>"
																	+ accountingLedgerReport.documentName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(accountingLedgerReport.createdDate)
																	+ "</td><td>"
																	+ accountingLedgerReport.byAccountName
																	+ "</td><td>"
																	+ accountingLedgerReport.toAccountName
																	+ "</td><td>"
																	+ (accountingLedgerReport.incomeExpenseHead == null ? "" : accountingLedgerReport.incomeExpenseHead)
																	+ "</td><<td>"
																	+ accountingLedgerReport.byAmount
																	+ "</td><td>"
																	+ accountingLedgerReport.toAmount
																	+ "</td></tr>");
										});
						$('#lblTotalByAmount').text(byTotal);
						$('#lblTotalToAmount').text(toTotal);
					}
				});
	}

	AccountingLedgerReport.showDatePicker = function() {
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

	AccountingLedgerReport.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showAccountingLedgerReport(pid);
				break;
			}
		}
		el.modal('show');
	}

	AccountingLedgerReport.closeModalPopup = function(el) {
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