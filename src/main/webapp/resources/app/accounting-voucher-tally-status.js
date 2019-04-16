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
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// load today data
		AccountVoucher.filter();

		$('#selectAll').on('click', function() {
			selectAllAccountVoucher(this);
		});
		$('#downloadXls').on('click', function() {
			downloadXls();
		});
		/*if($("#dbDocumentType").val!="no"){
			loadAllDocumentByDocumentType();
		}*/
		/*$("#dbDocumentType").change(function() {
			loadAllDocumentByDocumentType();
		});*/
	});

	function loadAllDocumentByDocumentType(){
		if ($('#dbDocumentType').val() == "no") {
			$("#dbDocument").html("<option>All</option>");
			alert("Please Select Document Type");
			return;
		}
		var documentType = $('#dbDocumentType').val();
		$("#dbDocument").html("<option>Documents loading...</option>")
		$.ajax({
			url : accountVoucherContextPath
					+ "/load-document",
			type : 'GET',
			data : {
				voucherType : documentType,
			},
			success : function(documents) {
				$("#dbDocument").html(
						"<option value='no'>All</option>")
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

	AccountVoucher.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if($("#dbDocumentType").val()=="no"){
			alert("Please Select Document Type")
			return;
		}
		
		let docPids = $('#dbDocument').val();
		let empPids = $('#dbEmployee').val();
		if("no" == docPids){
			docPids = $('#dbDocument option').map(function() {
		        return $(this).val();
		    }).get().join(',');
		}
		if("no" == empPids){
			empPids = $('#dbEmployee option').map(function() {
		        return $(this).val();
		    }).get().join(',');
		}
		$('#tBodyAccountVoucher').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$("#lblCounts").text(0.00);
		$('#lblTotalChequeAmount').text(0.00);
		$('#lblTotalCashAmount').text(0.00);
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
							$("#totalDocument").html("");
							$('#tBodyAccountVoucher')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var counts = 0;
						let totAmount = 0;
						let totVolume = 0;
						var chequeTotal = 0.00;
						var cashTotal = 0.00;
						$
								.each(
										accountVouchers,
										function(index, accountVoucher) {
											counts += 1;
											totAmount += accountVoucher.totalAmount;
											chequeTotal += accountVoucher.chequeAmount
											cashTotal += accountVoucher.cashAmount
											/*totVolume += accountVoucher.totalVolume;*/
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
																	+ spanStatus(accountVoucher.pid,accountVoucher.tallyDownloadStatus)
																	+"</td><td><button type='button' class='btn btn-blue' onclick='AccountVoucher.showModalPopup($(\"#viewModal\"),\""
																	+ accountVoucher.pid
																	+ "\",0);'>View Details</button></td></tr>");
										});
						$("#lblCounts").text(counts);
						$('#lblTotalChequeAmount').text(chequeTotal);
						$('#lblTotalCashAmount').text(cashTotal);
						if(totAmount != 0){
							$("#totalDocument").html("(" + totAmount.toFixed(2) + ")");
						}
						if(totVolume != 0){
							$("#totalVolume").html("(" + totVolume.toFixed(2) + ")");
						}
					}
				});
	}
	function spanStatus(accountVoucherPid,status) {
		
		var pending ="'" + 'PENDING' + "'" ;
		var processing = "'" + 'PROCESSING' + "'" ;
		var completed = "'" + 'COMPLETED' + "'" ;
		var spanStatus = "";
		var pid = "'" + accountVoucherPid + "'";
		switch(status){
		case 'PENDING':
			spanStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
						+'PENDING<span class="caret"></span></span>'
						+'<ul class="dropdown-menu">'
						+'<li onclick="AccountVoucher.setStatus('+pid+','+processing+')" style="cursor: pointer;"><a>PROCESSING</a></li>'
						+'<li onclick="AccountVoucher.setStatus('+pid+','+completed+')" style="cursor: pointer;"><a>COMPLETED</a></li>'
						+'</ul></div>';
			break;
		case 'PROCESSING':
			spanStatus = '<div class="dropdown"><span class="label label-warning dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
						+'PROCESSING <span class="caret"></span></span>'
						+'<ul class="dropdown-menu">'
						+'<li onclick="AccountVoucher.setStatus('+pid+','+pending+')"  style="cursor: pointer;"><a>PENDING</a></li>'
						+'<li onclick="AccountVoucher.setStatus('+pid+','+completed+')" style="cursor: pointer;"><a>COMPLETED</a></li>'
						+'</ul></div>';
			break;
		case 'COMPLETED':
			spanStatus = '<div class="dropdown"><span class="label label-success dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
						+'COMPLETED <span class="caret"></span></span>'
						+'<ul class="dropdown-menu">'
						+'<li onclick="AccountVoucher.setStatus('+pid+','+pending+')"  style="cursor: pointer;"><a>PENDING</a></li>'
						+'<li onclick="AccountVoucher.setStatus('+pid+','+processing+')" style="cursor: pointer;"><a>PROCESSING</a></li>'
						+'</ul></div>';
			break;
		}
		
		
		
		/*if (status) {
			spanStatus = '<span class="label label-success" style="cursor: pointer;">Processed</span>';
		} else {
			spanStatus = '<span class="label label-default" onclick="AccountVoucher.setStatus('
					+ pid
					+ ','
					+ !status
					+ ')" style="cursor: pointer;">Pending</span>';
		}*/
		return spanStatus;
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountVoucherContextPath;
	}
	
	AccountVoucher.setStatus = function(pid,tallyDownloadStatus) {
		
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
					//onSaveSuccess(data);
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
					+ "/download-inventory-xls?accountVoucherHeaderPids="
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