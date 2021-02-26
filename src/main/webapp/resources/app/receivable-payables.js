// Create a ReceivablePayable object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ReceivablePayable) {
	this.ReceivablePayable = {};
}

(function() {
	'use strict';

	var receivablePayableContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document)
			.ready(
					function() {
						// load data

						$("#txtToDate").datepicker({
							dateFormat : "dd-mm-yy"
						});
						$("#txtFromDate").datepicker({
							dateFormat : "dd-mm-yy"
						});

						let filterBy = getParameterByName('filterBy');
						if (filterBy) {
							$('#dbDateSearch').val(filterBy);
							// InvoiceWiseReport.filter();
						}

						$('.selectpicker').selectpicker();

						ReceivablePayable.loadData();

						$('#btnDownload')
								.on(
										'click',
										function() {
											var tblReceivalbePayable = $("#tBodyReceivablePayable tbody");
											if (tblReceivalbePayable.children().length == 0) {
												alert("no values available");
												return;
											}
											if (tblReceivalbePayable[0].textContent == "No data available") {
												alert("no values available");
												return;
											}

											downloadXls();
											$(
													"#tBodyReceivablePayable th:last-child, #tBodyReceivablePayable td:last-child")
													.show();
										});

					});

	ReceivablePayable.loadData = function() {

		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}

		$('#tBodyReceivablePayable').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : receivablePayableContextPath + "/load",
					type : 'GET',
					data : {
						accountPid : $('#dbAccount').val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(receivablePayableMap) {
						$('#tBodyReceivablePayable').html("");
						if (jQuery.isEmptyObject(receivablePayableMap)) {
							$('#tBodyReceivablePayable')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										receivablePayableMap,
										function(accountPid, receivablePayables) {
											var receivablePayable1 = receivablePayables[0];
											$('#tBodyReceivablePayable')
													.append(
															"<tr data-toggle='collapse' data-target='#"
																	+ accountPid
																	+ "'><td class='entypo-down-open-mini'>"
																	+ receivablePayable1.accountName
																	+ "</td><td id='bAmount"
																	+ accountPid
																	+ "' align='right' style='font-weight: bold;'>"
																	+"</td><td>"
																	+ receivablePayable1.accountType
																	+ "</td><td>"
																	+ receivablePayable1.accountAddress
																	+ "</td></tr>");
											$('#tBodyReceivablePayable')
													.append(
															"<tr class='collapse' id='"
																	+ accountPid
																	+ "'><td colspan='4'><table class='table table-striped table-bordered'><tr><th>Voucher Number</th><th>Voucher Amount</th><th>Balance Amount</th><th>Voucher Date</th></tr><tbody id='tblReceivable"
																	+ accountPid
																	+ "'><tr style='background: antiquewhite;font-size: small; font-weight: bold;'><td colspan='2'>Receivables</td><td colspan='1' id='rbAmount"
																	+ accountPid
																	+ "' align='right'></td><td></td></tr></tbody>"
																	+ "<tbody id='tblPayable"
																	+ accountPid
																	+ "'><tr style='background: rgba(180, 232, 168, 0.56);font-size: small; font-weight: bold;'><td colspan='2'>Payable</td><td colspan='1' id='pbAmount"
																	+ accountPid
																	+ "' align='right'></td><td></td></tr></tbody></table></td></tr>");
											var receivablesBlanaceAmount = 0;
											var payableBlanaceAmount = 0;
											$
													.each(
															receivablePayables,
															function(index,
																	receivablePayable) {
																if (receivablePayable.receivablePayableType == "Receivable") {
																	receivablesBlanaceAmount += receivablePayable.referenceDocumentBalanceAmount;
																	$(
																			'#tblReceivable'
																					+ accountPid)
																			.append(
																					"<tr><td>"
																							+ receivablePayable.referenceDocumentNumber
																							+ "</td><td>"
																							+ receivablePayable.referenceDocumentAmount.toFixed(2)
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentBalanceAmount.toFixed(2)
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentDate
																							+ "</td></tr>");
																} else if (receivablePayable.receivablePayableType == "Payable") {
																	payableBlanaceAmount += receivablePayable.referenceDocumentBalanceAmount;
																	$(
																			'#tblPayable'
																					+ accountPid)
																			.append(
																					"<tr><td>"
																							+ receivablePayable.referenceDocumentNumber
																							+ "</td><td>"
																							+ receivablePayable.referenceDocumentAmount.toFixed(2)
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentBalanceAmount.toFixed(2)
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentDate
																							+ "</td></tr>");
																}
															});
											$('#rbAmount' + accountPid).text(
													receivablesBlanaceAmount.toFixed(2));
											$('#pbAmount' + accountPid).text(
													payableBlanaceAmount.toFixed(2));
											var blanaceAmount = (receivablesBlanaceAmount
													- payableBlanaceAmount).toFixed(2);
											$('#bAmount' + accountPid).text(
													blanaceAmount);
										});
					}
				});
	}

	ReceivablePayable.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}

	}

	function downloadXls() {
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}

		var accPid = $('#dbAccount').val();
		var filterBy = $("#dbDateSearch").val();
		var fromDate = $("#txtFromDate").val();
		var toDate = $("#txtToDate").val();

		console.log(status);
		window.location.href = receivablePayableContextPath
				+ "/download-receivalbes-xls?accountPid=" + accPid
				+ "&filterBy=" + filterBy + "&fromDate=" + fromDate
				+ "&toDate=" + toDate;
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