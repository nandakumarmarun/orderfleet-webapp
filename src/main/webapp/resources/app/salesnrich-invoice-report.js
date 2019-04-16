// Create a SalesnrichInvoiceReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesnrichInvoiceReport) {
	this.SalesnrichInvoiceReport = {};
}

(function() {
	'use strict';

	var salesnrichInvoiceReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	$(document).ready(function() {

		SalesnrichInvoiceReport.filter();
		$('#downloadXls').on('click', function() {
			downloadXls();
		});
	});


	function downloadXls() {
		// var salesnrichInvoiceReportHeaderPids = [];
		var salesnrichInvoiceReportHeaderPids = "";
		$("input[type='checkbox']:checked")
				.each(
						function() {
							var salesnrichInvoiceReportPid = $(this).val();
							if (salesnrichInvoiceReportPid != "on") {
								salesnrichInvoiceReportHeaderPids += salesnrichInvoiceReportPid
										+ ",";
							}
						});

		if (salesnrichInvoiceReportHeaderPids == "") {
			alert("please select accounting vouchers");
		} else {
			window.location.href = salesnrichInvoiceReportContextPath
					+ "/download-accounting-xls?salesnrichInvoiceReportHeaderPids="
					+ salesnrichInvoiceReportHeaderPids;
			console.log("sucess.......");
		}
	}

	function showSalesnrichInvoiceReport(pid) {
		$('#tblInvoiceDetails').html("");
		$.ajax({
			url : salesnrichInvoiceReportContextPath + "/" + pid,
			method : 'GET',
			success : function(data) {
				$('#lbl_invoiceNumber').text(data.invoiceNumber);
				$('#lbl_activeUsers').text(data.activeUserCount);
				$('#lbl_checkedUsers').text(data.checkedUserCount);
				$('#lbl_totalUsers').text(data.totalUserCount);
				$('#lbl_createdDate').text(
						convertDateTimeFromServer(data.invoiceDate));
				$('#lbl_gstAmt').text(data.gstAmount);
				$('#lbl_totalAmount').text(data.totalAmount);
				$('#lbl_subTotalAmount').text(data.subTotal);
				$('#lbl_gstPercent').text(data.gstPercentage + " %");
				$.each(data.salesnrichInvoiceDetailDTOs, function(index,
						salesnrichInvoiceDTO) {
					$('#tblInvoiceDetails').append(
							"<tr><td>" + salesnrichInvoiceDTO.particulars
									+ "</td><td>"
									+ salesnrichInvoiceDTO.quantity
									+ "</td><td>" + salesnrichInvoiceDTO.price
									+ "</td><td>" + salesnrichInvoiceDTO.total
									+ "</td>");

				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	SalesnrichInvoiceReport.filter = function() {
		$('#tBodySalesnrichInvoiceReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : salesnrichInvoiceReportContextPath + "/filter",
					type : 'GET',
					data : {
						companyPid : $('#dbCompany').val()
					},
					success : function(salesnrichInvoiceReport) {
						$('#tBodySalesnrichInvoiceReport').html("");
						if (salesnrichInvoiceReport.length == 0) {
							$('#tBodySalesnrichInvoiceReport')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										salesnrichInvoiceReport,
										function(index, salesnrichInvoiceReport) {
											$('#tBodySalesnrichInvoiceReport')
													.append(
															"<tr><td>"
																	+ salesnrichInvoiceReport.invoiceNumber
																	+ "</td><td>"
																	+ convertDateTimeFromServer(salesnrichInvoiceReport.invoiceDate)
																	+ "</td><td>"
																	+ salesnrichInvoiceReport.checkedUserCount
																	+ "</td><td>"
																	+ salesnrichInvoiceReport.gstPercentage
																	+ " %"
																	+ "</td><td>"
																	+ salesnrichInvoiceReport.totalAmount
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='SalesnrichInvoiceReport.showModalPopup($(\"#viewModal\"),\""
																	+ salesnrichInvoiceReport.id
																	+ "\",0);'>View Details</button></td></tr>");
										});
					}
				});
	}

	SalesnrichInvoiceReport.showDatePicker = function() {
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

	SalesnrichInvoiceReport.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showSalesnrichInvoiceReport(pid);
				break;
			}
		}
		el.modal('show');
	}

	SalesnrichInvoiceReport.closeModalPopup = function(el) {
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