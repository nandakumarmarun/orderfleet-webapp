// Create a StockTrackingReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.StockTrackingReport) {
	this.StockTrackingReport = {};
}
(function() {
	'use strict';

	var stockTrackingReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// load today data
		StockTrackingReport.filter();

		$('#btnDownload').on('click', function() {
			var tblPunchOutReport = $("#tblStockTrackingReport tbody");
			if (tblPunchOutReport.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblPunchOutReport[0].textContent == "No data available") {
				alert("no values available");
				return;
			}

			downloadXls();
		});

	});

	function downloadXls() {
		$("#tblStockTrackingReport th:nth-last-child(2), #tblStockTrackingReport td:nth-last-child(2)").hide();
		var excelName = "StockTrackingReport";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblStockTrackingReport'),excelName);
		 $("#tblStockTrackingReport th:nth-last-child(2), #tblStockTrackingReport td:nth-last-child(2)").show();
	}

	StockTrackingReport.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyStockTrackingReport').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");

		$
				.ajax({
					url : stockTrackingReportContextPath + "/filter",
					type : 'GET',
					data : {
						userPid : $("#dbEmployee").val(),
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(stockTrackingList) {

						$('#tBodyStockTrackingReport').html("");
						if (stockTrackingList.length == 0) {
							$('#tBodyStockTrackingReport')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										stockTrackingList,
										function(index, stockTracking) {
											if (stockTracking.stockTrackingSubGroupName == null) {
												$('#tBodyStockTrackingReport')
														.append(
																"<tr><td>"
																		+ stockTracking.employeeName
																		+ "</td><td>"
																		+ stockTracking.accountName
																		+ "</td><td>"
																		+ stockTracking.productName
																		+ "</td><td>"
																		+ convertDateTimeFromServer(stockTracking.createdDate)
																		+ "</td><td>"
																		+ stockTracking.sellingRate
																		+ "</td><td>"
																		+ stockTracking.quantity
																		+ "</td><td>"
																		+ stockTracking.rowTotal
																		+ "</td></tr>");
											}
										});
					}
				});
	}

	StockTrackingReport.showDatePicker = function() {
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