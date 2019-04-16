// Create a AccountingVoucher object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountingVoucher) {
	this.AccountingVoucher = {};
}

(function() {
	'use strict';

	var accountingVoucherContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// load today data
		AccountingVoucher.filter();
		$('#selectAll').on('click', function() {
			selectAllInventoryVoucher(this);
		});
		$('#downloadXls').on('click', function() {
			downloadXls();
		});
	});

	function selectAllInventoryVoucher(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}
	
	function downloadXls() {
		// var accountingVoucherHeaderPids = [];
		var accountingVoucherHeaderPids = "";
		$("input[type='checkbox']:checked").each(function() {
			var accountingVoucherPid = $(this).val();
			if (accountingVoucherPid != "on") {
				accountingVoucherHeaderPids += accountingVoucherPid + ",";
			}
		});

		if (accountingVoucherHeaderPids == "") {
			alert("please select accounting vouchers");
		} else {
			window.location.href = accountingVoucherContextPath
					+ "/download-accounting-xls?accountingVoucherHeaderPids="
					+ accountingVoucherHeaderPids;
			console.log("sucess.......");
		}
	}
	
	function showAccountingVoucher(pid) {
		$
				.ajax({
					url : accountingVoucherContextPath + "/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumber').text(data.documentNumberLocal);
						$('#lbl_user').text(data.userName);
						$('#lbl_account').text(data.accountProfileName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_createdDate').text(
								convertDateTimeFromServer(data.createdDate));
						$('#lbl_totalAmount').text(data.totalAmount);
						$('#lbl_remarks').text((data.remarks == null ? "" : data.remarks));

						$('#tblVoucherDetails').html("");
						$
								.each(
										data.accountingVoucherDetails,
										function(index, voucherDetail) {
											$('#tblVoucherDetails')
													.append(
															"<tr data-id='"
																	+ index
																	+ "' ><td>"
																	+ voucherDetail.mode
																	+ "</td><td>"
																	+ voucherDetail.amount
																	+ "</td><td>"
																	+(voucherDetail.instrumentNumber == null ? "" : voucherDetail.instrumentNumber)
																	+ "</td><td>"
																	+ convertDateFromServer(voucherDetail.instrumentDate)
																	+ "</td><td>"
																	+(voucherDetail.bankName == null ? "" : voucherDetail.bankName)
																	+ "</td><td>"
																	+ voucherDetail.byAccountName
																	+ "</td><td>"
																	+ voucherDetail.toAccountName
																	+ "</td><td>"
																	+ (voucherDetail.incomeExpenseHeadName == null ? "" : voucherDetail.incomeExpenseHeadName)
																	+ "</td><td>"
																	+ voucherDetail.voucherNumber
																	+ "</td><td>"
																	+ convertDateFromServer(voucherDetail.voucherDate)
																	+ "</td><td>"
																	+(voucherDetail.referenceNumber == null ? "" : voucherDetail.referenceNumber)
																	+ "</td><td>"
																	+(voucherDetail.provisionalReceiptNo == null ? "" : voucherDetail.provisionalReceiptNo)
																	+ "</td><td>"
																	+(voucherDetail.remarks == null ? "" : voucherDetail.remarks)
																	+ "</td>");

											$
													.each(
															voucherDetail.accountingVoucherAllocations,
															function(index1,
																	voucherAllocation) {
																$(
																		'#tblVoucherDetails')
																		.append(
																				"<tr style='background: rgba(225, 225, 225, 0.66);' data-parent='"
																						+ index
																						+ "' >"
																						+ "<td>&nbsp;</td><td colspan='4'>Payment Mode : "
																						+ voucherAllocation.mode
																						+ "</td><td colspan='6'>Amount : "
																						+ voucherAllocation.amount
																						+ "</td></tr>");
															});

										});
						$('.collaptable')
								.aCollapTable(
										{
											startCollapsed : true,
											addColumn : false,
											plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
											minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	AccountingVoucher.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyAccountingVoucher').html(
				"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		$('#lblTotalByAmount').text(0.00);
		$('#lblTotalToAmount').text(0.00);
		$('#lblTotalChequeAmount').text(0.00);
		$('#lblTotalCashAmount').text(0.00);
		$
				.ajax({
					url : accountingVoucherContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						accountPid : $("#dbAccount").val(),
						documentPid : $("#dbDocument").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(accountingVouchers) {
						$('#tBodyAccountingVoucher').html("");
						if (accountingVouchers.length == 0) {
							$('#tBodyAccountingVoucher')
									.html(
											"<tr><td colspan='8' align='center'>No data available</td></tr>");
							return;
						}
						var byTotal = 0.00;
						var toTotal = 0.00;
						var chequeTotal = 0.00;
						var cashTotal = 0.00;
						$.each(accountingVouchers, function(index, accountingVoucher) {
							byTotal += accountingVoucher.byAmount;
							toTotal += accountingVoucher.toAmount;
							chequeTotal += accountingVoucher.chequeAmount
							cashTotal += accountingVoucher.cashAmount
											$('#tBodyAccountingVoucher')
													.append("<tr><td><input type='checkbox' class='check-one' value='"
																	+ accountingVoucher.pid
																	+ "' />"
																	+ "</td><td>"
																	+ accountingVoucher.employeeName
																	+ "</td><td>"
																	+ accountingVoucher.accountProfileName
																	+ "</td><td>"
																	+ accountingVoucher.documentName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(accountingVoucher.createdDate)
																	+ "</td><td>"
																	+ accountingVoucher.byAmount
																	+ "</td><td>"
																	+ accountingVoucher.toAmount
																	+ "</td><td>"
																	+ (accountingVoucher.status == false ? "<span class='label label-warning'>Pending</span>" : "<span class='label label-success'>Completed</span>")
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='AccountingVoucher.showModalPopup($(\"#viewModal\"),\""
																	+ accountingVoucher.pid
																	+ "\",0);'>View Details</button></td></tr>");
										});
						$('#lblTotalByAmount').text(byTotal);
						$('#lblTotalToAmount').text(toTotal);
						$('#lblTotalChequeAmount').text(chequeTotal);
						$('#lblTotalCashAmount').text(cashTotal);
					}
				});
	}

	AccountingVoucher.showDatePicker = function() {
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

	AccountingVoucher.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showAccountingVoucher(pid);
				break;
			}
		}
		el.modal('show');
	}

	AccountingVoucher.closeModalPopup = function(el) {
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