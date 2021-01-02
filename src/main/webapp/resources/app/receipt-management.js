// Create a AccountVoucher object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountVoucher) {
	this.AccountVoucher = {};
}

(function() {
	'use strict';

	var accountVoucherContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// load today data
		AccountVoucher.filter();

		$('#selectAll').on('click', function() {
			selectAllAccountVoucher(this);
		});
		$('#downloadXls').on('click', function() {
			downloadXls();
		});
		
		$('#sendTransactionsSapPravesh').on('click', function() {
			sendTransactionsSapPravesh();
		});
		/*
		 * if($("#dbDocumentType").val!="no"){ loadAllDocumentByDocumentType(); }
		 */
		/*
		 * $("#dbDocumentType").change(function() {
		 * loadAllDocumentByDocumentType(); });
		 */
	});
	
	function sendTransactionsSapPravesh() {

		$(".loader").addClass('show');

		if (confirm("Are you sure?")) {

			$.ajax({
				url : accountVoucherContextPath + "/sendTransactionsSapPravesh",
				method : 'GET',
				beforeSend : function() {
					// Show image container
					$("#loader").modal('show');

				},
				success : function(data) {
					$("#loader").modal('hide');
					AccountVoucher.filter();
					
					// onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	function loadAllDocumentByDocumentType() {
		if ($('#dbDocumentType').val() == "no") {
			$("#dbDocument").html("<option>All</option>");
			alert("Please Select Document Type");
			return;
		}
		var documentType = $('#dbDocumentType').val();
		$("#dbDocument").html("<option>Documents loading...</option>")
		$.ajax({
			url : accountVoucherContextPath + "/load-document",
			type : 'GET',
			data : {
				voucherType : documentType,
			},
			success : function(documents) {
				$("#dbDocument").html("<option value='no'>All</option>")
				$.each(documents, function(key, document) {
					$("#dbDocument").append(
							"<option value='" + document.pid + "'>"
									+ document.name + "</option>");
				});
			}
		});
	}

	function showAccountVoucher(pid) {
		$
				.ajax({
					url : accountVoucherContextPath + "/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumber').text(data.documentNumberLocal);
						$('#lbl_user').text(data.userName);
						$('#lbl_account').text(data.accountProfileName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_createdDate').text(
								convertDateTimeFromServer(data.createdDate));
						$('#lbl_totalAmount').text(data.totalAmount);
						$('#lbl_remarks').text(
								(data.remarks == null ? "" : data.remarks));

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
																	+ (voucherDetail.instrumentNumber == null ? ""
																			: voucherDetail.instrumentNumber)
																	+ "</td><td>"
																	+ convertDateFromServer(voucherDetail.instrumentDate)
																	+ "</td><td>"
																	+ (voucherDetail.bankName == null ? ""
																			: voucherDetail.bankName)
																	+ "</td><td>"
																	+ voucherDetail.byAccountName
																	+ "</td><td>"
																	+ voucherDetail.toAccountName
																	+ "</td><td>"
																	+ (voucherDetail.incomeExpenseHeadName == null ? ""
																			: voucherDetail.incomeExpenseHeadName)
																	+ "</td><td>"
																	+ voucherDetail.voucherNumber
																	+ "</td><td>"
																	+ convertDateFromServer(voucherDetail.voucherDate)
																	+ "</td><td>"
																	+ (voucherDetail.referenceNumber == null ? ""
																			: voucherDetail.referenceNumber)
																	+ "</td><td>"
																	+ (voucherDetail.provisionalReceiptNo == null ? ""
																			: voucherDetail.provisionalReceiptNo)
																	+ "</td><td>"
																	+ (voucherDetail.remarks == null ? ""
																			: voucherDetail.remarks)
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

	AccountVoucher.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if ($("#dbDocumentType").val() == "no") {
			alert("Please Select Document Type")
			return;
		}

		let docPids = $('#dbDocument').val();
		let empPids = $('#dbEmployee').val();
		if ("no" == docPids) {
			docPids = $('#dbDocument option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		if ("no" == empPids) {
			empPids = $('#dbEmployee option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		$('#tBodyAccountVoucher').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$("#lblCounts").text(0.00);
		$('#lblTotalChequeAmount').text(0.00);
		$('#lblTotalCashAmount').text(0.00);
		$('#lblTotalAmount').text(0.00);
		$
				.ajax({
					url : accountVoucherContextPath + "/filter",
					type : 'GET',
					data : {
						tallyDownloadStatus : $("#dbStatus").val(),
						employeePids : empPids,
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						documentPids : $("#dbDocument").val(),
					},
					success : function(accountVouchers) {

						$("#lblCounts").text("0");
						$('#tBodyAccountVoucher').html("");
						if (accountVouchers.length == 0) {
							$('#tBodyAccountVoucher')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var counts = 0;
						let totAmount = 0;
						var chequeTotal = 0.00;
						var cashTotal = 0.00;

						$
								.each(
										accountVouchers,
										function(index, accountVoucher) {
											console.log(accountVoucher.salesManagementStatus);
											counts += 1;
											totAmount += accountVoucher.totalAmount;
											chequeTotal += accountVoucher.chequeAmount
											cashTotal += accountVoucher.cashAmount
											var content = "";

											if (accountVoucher.imageButtonVisible) {
												content = "<td><button type='button' class='btn btn-info' onclick='AccountVoucher.showModalPopup($(\"#imagesModal\"),\""
														+ accountVoucher.pid
														+ "\",1);'>View Images</button></td>";
											} else {
												content = "<td>No Image</td>";
											}

											$('#tBodyAccountVoucher')
													.append(
															"<tr><td><input type='checkbox' class='check-one' value='"
																	+ accountVoucher.pid
																	+ "' />"
																	+ "</td><td>"
																	+ accountVoucher.employeeName
																	+ "</td><td>"
																	+ accountVoucher.accountProfileName
																	+ "</td><td>"
																	+ accountVoucher.documentName
																	+ "</td><td>"
																	+ accountVoucher.totalAmount
																	+ "</td><td>"
																	+ convertDateTimeFromServer(accountVoucher.createdDate)
																	+ "</td><td>"
																	+ spanSalesManagementStatus(
																			accountVoucher.pid,
																			accountVoucher.salesManagementStatus,
																			accountVoucher.tallyDownloadStatus)
																	+ "</td><td>"
																	+ spanStatus(
																			accountVoucher.pid,
																			accountVoucher.tallyDownloadStatus)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='AccountVoucher.showModalPopup($(\"#viewModal\"),\""
																	+ accountVoucher.pid
																	+ "\",0);'>View Details</button></td>"
																	+ content
																	+ "</tr>");
										});
						$("#lblCounts").text(counts);
						$('#lblTotalChequeAmount').text(chequeTotal);
						$('#lblTotalCashAmount').text(cashTotal);

						if (totAmount != 0) {
							// $("#totalDocument").html("(" +
							// totAmount.toFixed(2) + ")");
							$('#lblTotalAmount').text(totAmount.toFixed(2));
						}
					}
				});
	}
	
	function spanSalesManagementStatus(inventoryVoucherPid, status, tallyStatus) {

		var hold = "'" + 'HOLD' + "'";
		var approve = "'" + 'APPROVE' + "'";
		var reject = "'" + 'REJECT' + "'";
		var spanSalesManagementStatus = "";
		var pid = "'" + inventoryVoucherPid + "'";
		switch (status) {
		case 'HOLD':
			spanSalesManagementStatus = '<div class="dropdown"><span class="label label-warning dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'ON HOLD<span class="caret"></span></span>'
					+ '<ul class="dropdown-menu">'
					+ '<li onclick="AccountVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ approve
					+ ')" style="cursor: pointer;"><a>APPROVE</a></li>'
					+ '<li onclick="AccountVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ reject
					+ ')" style="cursor: pointer;"><a>REJECT</a></li>'
					+ '</ul></div>';
			break;
		case 'APPROVE':

			if (tallyStatus == 'COMPLETED') {
				spanSalesManagementStatus = '<div ><span class="label label-success dropdown-toggle" data-toggle="dropdown" >'
						+ 'APPROVED <span></span></span></div>';

			} else {
				spanSalesManagementStatus = '<div class="dropdown"><span class="label label-success dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
						+ 'APPROVED <span class="caret"></span></span>'
						+ '<ul class="dropdown-menu">'
						+ '<li onclick="AccountVoucher.setSalesManagementStatus('
						+ pid
						+ ','
						+ hold
						+ ')"  style="cursor: pointer;"><a>HOLD</a></li>'
						+ '<li onclick="AccountVoucher.setSalesManagementStatus('
						+ pid
						+ ','
						+ reject
						+ ')" style="cursor: pointer;"><a>REJECT</a></li>'
						+ '</ul></div>';
			}
			break;
		case 'REJECT':
			spanSalesManagementStatus = '<div class="dropdown"><span class="label label-danger dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'REJECTED <span class="caret"></span></span>'
					+ '<ul class="dropdown-menu">'
					+ '<li onclick="AccountVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ hold
					+ ')"  style="cursor: pointer;"><a>HOLD</a></li>'
					+ '<li onclick="AccountVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ approve
					+ ')" style="cursor: pointer;"><a>APPROVE</a></li>'
					+ '</ul></div>';
			break;
		case 'DEFAULT':
			spanSalesManagementStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'SELECT STATUS <span class="caret"></span></span>'
					+ '<ul class="dropdown-menu">'
					+ '<li onclick="AccountVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ hold
					+ ')"  style="cursor: pointer;"><a>HOLD</a></li>'
					+ '<li onclick="AccountVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ approve
					+ ')" style="cursor: pointer;"><a>APPROVE</a></li>'
					+ '<li onclick="AccountVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ reject
					+ ')"  style="cursor: pointer;"><a>REJECT</a></li>'
					+ '</ul></div>';
			break;
		}

		/*
		 * if (status) { spanStatus = '<span class="label label-success"
		 * style="cursor: pointer;">Processed</span>'; } else { spanStatus = '<span
		 * class="label label-default" onclick="InventoryVoucher.setStatus(' +
		 * pid + ',' + !status + ')" style="cursor: pointer;">Pending</span>'; }
		 */
		return spanSalesManagementStatus;
	}

	function showReceiptImages(pid) {
		$
				.ajax({
					url : accountVoucherContextPath + "/images/" + pid,
					method : 'GET',
					success : function(filledFormFiles) {

						$('#divReceiptImages').html("");
						$
								.each(
										filledFormFiles,
										function(index, filledFormFile) {
											var table = '<table class="table  table-striped table-bordered"><tr><td style="font-weight: bold;">'
													+ filledFormFile.formName
													+ '</td></tr>';
											$
													.each(
															filledFormFile.files,
															function(index,
																	file) {
																table += '<tr><th>'
																		+ file.fileName
																		+ '</th></tr>';
																table += '<tr><td><img width="100%" src="data:image/png;base64,'
																		+ file.content
																		+ '"/></td></tr>';
															});
											table += '</table>';
											$('#divReceiptImages')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function spanStatus(accountVoucherPid, status) {

		var pending = "'" + 'PENDING' + "'";
		var processing = "'" + 'PROCESSING' + "'";
		var completed = "'" + 'COMPLETED' + "'";
		var spanStatus = "";
		var pid = "'" + accountVoucherPid + "'";
		switch (status) {
		case 'PENDING':
			spanStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'PENDING</span></div>';
			break;
		case 'PROCESSING':
			spanStatus = '<div class="dropdown"><span class="label label-warning dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'PROCESSING</span></div>';
			break;
		case 'COMPLETED':
			spanStatus = '<div class="dropdown"><span class="label label-success dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'COMPLETED</span></div>';
			break;
		}

		/*
		 * if (status) { spanStatus = '<span class="label label-success"
		 * style="cursor: pointer;">Processed</span>'; } else { spanStatus = '<span
		 * class="label label-default" onclick="AccountVoucher.setStatus(' + pid +
		 * ',' + !status + ')" style="cursor: pointer;">Pending</span>'; }
		 */
		return spanStatus;
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountVoucherContextPath;
	}

	AccountVoucher.setStatus = function(pid, tallyDownloadStatus) {

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : accountVoucherContextPath + "/changeStatus",
				method : 'GET',
				data : {
					pid : pid,
					tallyDownloadStatus : tallyDownloadStatus
				},
				success : function(data) {
					AccountVoucher.filter();
					// onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}
	
	AccountVoucher.setSalesManagementStatus = function(pid,
			salesManagementStatus) {

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : accountVoucherContextPath
						+ "/changeSalesManagementStatus",
				method : 'GET',
				data : {
					pid : pid,
					salesManagementStatus : salesManagementStatus
				},
				success : function(data) {
					console.log(data);
					if (data == 'failed') {
						alert("Cannot update a downloaded order");
					}
					AccountVoucher.filter();
					// onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	AccountVoucher.showDatePicker = function() {
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

	function selectAllAccountVoucher(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}

	function downloadXls() {
		// var accountVoucherHeaderPids = [];
		var accountVoucherHeaderPids = "";
		$("input[type='checkbox']:checked").each(function() {
			var accountVoucherPid = $(this).val();
			if (accountVoucherPid != "on") {
				accountVoucherHeaderPids += accountVoucherPid + ",";
			}
		});

		if (accountVoucherHeaderPids == "") {
			alert("please select inventory vouchers");
		} else {
			window.location.href = accountVoucherContextPath
					+ "/download-accounting-xls?accountVoucherHeaderPids="
					+ accountVoucherHeaderPids;
			console.log("sucess.......");
		}
	}

	AccountVoucher.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showAccountVoucher(pid);
				break;
			case 1:
				showReceiptImages(pid);
				break;
			}
		}
		el.modal('show');
	}

	AccountVoucher.closeModalPopup = function(el) {
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