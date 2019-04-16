// Create a AccountingVoucherTransaction object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountingVoucherTransaction) {
	this.AccountingVoucherTransaction = {};
}
(function() {
	'use strict';

	var accountingVoucherTransactionContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var accountingVoucher = {
		pid : null,
		documentPid : null,
		employeePid : null,
		accountProfilePid : null,
		accountingVoucherDetails : [],
		totalAmount : 0.0
	};
	var accountingVoucherColumns = [];
	$(document).ready(function() {


		$("#dbDocument").change(function() {
			$('#divAccounting').hide();

			// load account profiles
			loadAccountProfileByActivityId();

			// load Accounting Voucher Columns
			loadAccountingVoucherColumns();

			loadStaticJsCodeFromServer();
		});

		$("#btnSubmitAccountingVoucher").click(function() {
			submitAccountingVoucher();
		});

		// staticJsCode executing events
		/*
		 * $("#SELLING_RATE").keyup(function() { calculateAndSetTotal(); });
		 * $("#AMOUNT").keyup(function() { calculateAndSetTotal(); });
		 * $("#DISCOUNT_PERCENTAGE").keyup(function() { calculateAndSetTotal();
		 * });
		 */

		$("#TYPE").change(function() {
			hideBankDetails();
		});

		var etePid = getParameterByName('etePid');
		var accPid = getParameterByName('accPid');
		if (etePid != null && accPid != null) {
			loadAccounting(etePid, accPid);
		}
	});

	AccountingVoucherTransaction.returnToTransaction = function() {
		var previousURL = document.referrer;
		if(previousURL.indexOf("executive-task-executions") !== -1) {
			window.history.back();
		} else {
			window.location = location.protocol + '//' + location.host + "/web/executive-task-executions";
		}
	}

	function loadAccounting(pid, accPid) {
		$.ajax({
			url : accountingVoucherTransactionContextPath
					+ "/loadExecutiveTaskExecution",
			type : "GET",
			data : {
				pid : pid,
				accPid : accPid,
			},
			success : function(status) {
				$("#dbEmployee").val(status.employeePid);
				$("#dbDocument").val(
						status.documentPid + "~" + status.activityPid);
				loadAccountProfileByActivityId();
				$("#dbAccount").val(status.accountPid);
				AccountingVoucherTransaction.search();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}

	function hideBankDetails() {
		var mode = $("#TYPE").val();
		$('#BANK_NAME').val('');
		$('#CHEQUE_NO').val('');
		$('#CHEQUE_DATE').val('');
		if (mode == 'Bank') {
			$('#divBANK_NAME').show();
			$('#divCHEQUE_DATE').show();
			$('#divCHEQUE_NO').show();
		} else {
			$('#divBANK_NAME').hide();
			$('#divCHEQUE_DATE').hide();
			$('#divCHEQUE_NO').hide();
		}
	}

	function calculateAndSetTotal() {
		var data = {};
		data.selling_rate = $('#SELLING_RATE').val();
		data.quantity = $('#AMOUNT').val();
		data.discount_percentage = $('#DISCOUNT_PERCENTAGE').val();
		data.size = $('#SIZE').val();
		data.unit_quantity = $('#UNIT_QUANTITY').val();
		var outElements = calculateTotal(data);
		$('#rowTotal').html(outElements.total);
	}

	function loadStaticJsCodeFromServer() {
		if ($('#dbDocument').val() == "no") {
			$("#dbAccount").html("<option>Select Account</option>");
			return;
		}
		var documentPid = $('#dbDocument').val().split("~")[0];
		$.ajax({
			url : accountingVoucherTransactionContextPath + "/static-js-code"
					+ "/" + documentPid,
			method : 'GET',
			success : function(response) {
				$("body").append(
						'<script type="text/javascript">' + response.jsCode
								+ '</script>');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function loadAccountProfileByActivityId() {
		if ($('#dbDocument').val() == "no") {
			$("#dbAccount").html("<option>Select Account</option>");
			return;
		}
		var activityPid = $('#dbDocument').val().split("~")[1];
		$("#dbAccount").html("<option>Accounts loading...</option>")
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/load-account-profiles/" + activityPid,
			type : 'GET',
			async : false,
			success : function(accounts) {
				$("#dbAccount").html(
						"<option value='no'>Select Account</option>")
				$.each(accounts, function(key, account) {
					$("#dbAccount").append(
							"<option value='" + account.pid + "'>"
									+ account.name + "</option>");
				});
			}
		});
	}

	function loadAccountingVoucherColumns() {
		hideAccountingDetailModalElemens();
		if ($('#dbDocument').val() == "no") {
			return;
		}
		var documentPid = $('#dbDocument').val().split("~")[0];
		$.ajax({
			url : accountingVoucherTransactionContextPath
					+ "/documentAccountingVoucherColumns" + "/" + documentPid,
			method : 'GET',
			success : function(accountingVoucherColumnList) {
				accountingVoucherColumns = accountingVoucherColumnList;
				$.each(accountingVoucherColumns, function(index,
						accountingVoucherColumn) {
					$('#div' + accountingVoucherColumn.name).show();
					$('#' + accountingVoucherColumn.name).prop('readonly',
							!accountingVoucherColumn.enabled);
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	AccountingVoucherTransaction.showAccountingForm = function() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}
		accountingVoucher = {
			pid : null,
			documentPid : null,
			employeePid : null,
			accountProfilePid : null,
			accountingVoucherDetails : [],
			totalAmount : 0.0
		};
		var documentPid = $('#dbDocument').val().split("~")[0];
		var activityPid = $('#dbDocument').val().split("~")[1];
		accountingVoucher.documentPid = documentPid;

		// set headers
		setDocumentNameAndPreviousDocumentNumber();

		// load by An to accounts
		loadByAndToAccounts(activityPid, documentPid, true);

		$('#tbodyAccountingDetails').html(
				'<tr><td colspan="3">No data avilable</td></tr>');
		$('#divAccounting').show();
		clearAndHideErrorBox();
	}

	function loadByAndToAccounts(activityPid, documentPid, async) {
		$("#BY").html("<option>loading...</option>");
		$("#TO").html("<option>loading...</option>");
		$.ajax({
			url : accountingVoucherTransactionContextPath
					+ "/load-by-to-account/" + activityPid + "/" + documentPid,
			type : 'GET',
			async : async,
			success : function(object) {
				$("#BY").html("<option value='no'>Select By</option>");
				$("#TO").html("<option value='no'>Select To</option>");

				$.each(object.receivers, function(key, by) {
					$("#BY").append(
							"<option value='" + by.pid + "'>" + by.name
									+ "</option>");
				});

				$.each(object.suppliers, function(key1, to) {
					$("#TO").append(
							"<option value='" + to.pid + "'>" + to.name
									+ "</option>");
				});
			}
		});
	}

	function setDocumentNameAndPreviousDocumentNumber() {
		$('#divDocumentNumber').text("---");
		$('#divDocumentName').text($('#dbDocument option:selected').text());
		// get previous document number
		$.ajax({
			url : accountingVoucherTransactionContextPath
					+ "/previous-document-number",
			type : 'GET',
			success : function(previousDocumentNumber) {
				$('#divPreviousDocumentNumber').text(previousDocumentNumber);
			}
		});
	}

	// show Accounting detail popup
	AccountingVoucherTransaction.showAccountingModal = function() {

		// clear popup
		clearAccountingDetailModalElementValues();

		// set by account
		var accountPid = $("#dbAccount").val();
		$("#BY").val(accountPid);
		if ($("#BY").val() == null) {
			$("#BY").val('no');
		}

		$('#hdnIndex').val(-1);

		hideBankDetails();

		$('#accountingVoucherModal').modal('show');
	}
	function hideAccountingDetailModalElemens() {
		$('#divAMOUNT').hide();
		$('#divBANK_NAME').hide();
		$('#divBY').hide();
		$('#divCHEQUE_DATE').hide();
		$('#divCHEQUE_NO').hide();
		$('#divFROM').hide();
		$('#divINCOME_EXPENSE_HEAD').hide();
		$('#divOUTSTANDING_AMOUNT').hide();
		$('#divREFERENCE_NUMBER').hide();
		$('#divREMARKS').hide();
		$('#divTO').hide();
		$('#divTYPE').hide();
	}

	function clearAccountingDetailModalElementValues() {
		$('#AMOUNT').val("");
		$('#BANK_NAME').val("");
		$('#BY').val("no");
		$('#CHEQUE_DATE').val("");
		$('#CHEQUE_NO').val("");
		$('#FROM').val("");
		$('#INCOME_EXPENSE_HEAD').val("no");
		$('#OUTSTANDING_AMOUNT').val("");
		$('#REFERENCE_NUMBER').val("");
		$('#REMARKS').val("");
		$('#TO').val("no");
		$('#TYPE').val("Cash");
	}

	function onEditAccountingDetails(index) {
		$
				.each(
						accountingVoucher.accountingVoucherDetails,
						function(key, accVoucherDetail) {
							if (index == key) {

								$('#TYPE').val(accVoucherDetail.mode);
								hideBankDetails();
								new Date(accVoucherDetail.instrumentDate)
								$('#AMOUNT').val(accVoucherDetail.amount);
								$('#BANK_NAME').val(accVoucherDetail.bankName);
								$('#BY').val(accVoucherDetail.byAccountPid);
								if (accVoucherDetail.instrumentDate != null
										&& accVoucherDetail.instrumentDate != "") {
									var date = convertDateFormat(accVoucherDetail.instrumentDate)
									$('#CHEQUE_DATE')
											.val(
													date.toISOString()
															.substring(0, 10));
								}
								$('#CHEQUE_NO').val(
										accVoucherDetail.instrumentNumber);
								if (accVoucherDetail.incomeExpenseHeadPid != null)
									$('#INCOME_EXPENSE_HEAD')
											.val(
													accVoucherDetail.incomeExpenseHeadPid);
								$('#REFERENCE_NUMBER').val(
										accVoucherDetail.referenceNumber);
								$('#REMARKS').val(accVoucherDetail.remarks);
								$('#TO').val(accVoucherDetail.toAccountPid);
								return;
							}
						});
	}

	AccountingVoucherTransaction.addToAccountingDetails = function() {
		if ($('#AMOUNT').val() == 0 || $('#AMOUNT').val() == "") {
			return;
		}
		if ($('#BY').val() == "no") {
			return;
		}
		if ($('#TO').val() == "no") {
			return;
		}

		var incomeExpenseHeadPid = $('#INCOME_EXPENSE_HEAD').val() != 'no' ? $(
				'#INCOME_EXPENSE_HEAD').val() : null;
		var incomeExpenseHeadName = $('#INCOME_EXPENSE_HEAD').val() != 'no' ? $(
				'#INCOME_EXPENSE_HEAD option:selected').text()
				: null;

		var chequeDate = null;
		if (chequeDate != '') {
			chequeDate = convertToServerFormat($('#CHEQUE_DATE').val())
		}
		var accountingVoucherDetail = {
			mode : $('#TYPE').val(),
			amount : $('#AMOUNT').val(),
			instrumentNumber : $('#CHEQUE_NO').val(),
			instrumentDate : chequeDate,
			bankName : $('#BANK_NAME').val(),
			byAccountPid : $('#BY').val(),
			byAccountName : $('#BY option:selected').text(),
			toAccountPid : $('#TO').val(),
			toAccountName : $('#TO option:selected').text(),
			voucherNumber : null,
			voucherDate : null,
			referenceNumber : $('#REFERENCE_NUMBER').val(),
			remarks : $('#REMARKS').val(),
			incomeExpenseHeadPid : incomeExpenseHeadPid,
			incomeExpenseHeadName : incomeExpenseHeadName,
			accountingVoucherAllocations : []
		};

		var isNew = true;
		var index = $('#hdnIndex').val();
		if (index > -1) {
			$
					.each(
							accountingVoucher.accountingVoucherDetails,
							function(key, accVoucherDetail) {
								if (index == key) {
									accVoucherDetail.mode = accountingVoucherDetail.mode;
									accVoucherDetail.amount = accountingVoucherDetail.amount;
									accVoucherDetail.instrumentNumber = accountingVoucherDetail.instrumentNumber;
									accVoucherDetail.instrumentDate = accountingVoucherDetail.instrumentDate;
									accVoucherDetail.bankName = accountingVoucherDetail.bankName;
									accVoucherDetail.byAccountPid = accountingVoucherDetail.byAccountPid;
									accVoucherDetail.byAccountName = accountingVoucherDetail.byAccountName;
									accVoucherDetail.toAccountPid = accountingVoucherDetail.toAccountPid;
									accVoucherDetail.toAccountName = accountingVoucherDetail.toAccountName;
									accVoucherDetail.referenceNumber = accountingVoucherDetail.referenceNumber;
									accVoucherDetail.remarks = accountingVoucherDetail.remarks;
									accVoucherDetail.incomeExpenseHeadPid = accountingVoucherDetail.incomeExpenseHeadPid;
									accVoucherDetail.incomeExpenseHeadName = accountingVoucherDetail.incomeExpenseHeadName;
									isNew = false;
									return;
								}
							});
		}
		if (isNew) {
			accountingVoucher.accountingVoucherDetails
					.push(accountingVoucherDetail);
		}

		// update view
		updateAccountingDetailsView();

		$('#accountingVoucherModal').modal('hide');
	}

	function updateAccountingDetailsView() {
		var totalAmount = 0;
		$('#tbodyAccountingDetails').html("");
		$
				.each(
						accountingVoucher.accountingVoucherDetails,
						function(index, accVoucherDetail) {
							totalAmount += parseFloat(accVoucherDetail.amount);
							$('#tbodyAccountingDetails')
									.append(
											"<tr><td>"
													+ accVoucherDetail.amount
													+ "</td><td>"
													+ accVoucherDetail.mode
													+ "</td><td><button type='button' class='btn btn-blue' onclick='AccountingVoucherTransaction.editAccountingDetail("
													+ index
													+ ");'>Edit</button>&nbsp;<button type='button' class='btn btn-blue' onclick='AccountingVoucherTransaction.allocateAmount(\""
													+ accVoucherDetail.amount
													+ "\","
													+ index
													+ ");'>Allocation</button>"
													+ "	<button type='button' class='btn btn-danger' onclick='AccountingVoucherTransaction.removeAccountingDetail("
													+ index
													+ ",this);'>Remove</button></td></tr>");
						});
		accountingVoucher.totalAmount = totalAmount;
	}

	AccountingVoucherTransaction.allocateAmount = function(amount, index) {

		// clear
		clearAndHideErrorBoxBatch();

		// set
		$('#hdnAlloIndex').val(index);
		$('#lblAlloAmount').text(amount);
		$('#lblAlloBalAmount').text(amount);

		$('#tblAllocations').html(
				'<tr><td colspan="6">No data available</td></tr>');
		// get account receivables
		$
				.ajax({
					url : accountingVoucherTransactionContextPath
							+ "/receivables/" + $('#dbAccount').val(),
					type : 'GET',
					success : function(receivables) {
						if (receivables.length > 0) {
							$('#tblAllocations').html('');
							$
									.each(
											receivables,
											function(key, receivable) {
												$('#tblAllocations')
														.append(
																'<tr><td id="number'
																		+ receivable.pid
																		+ '">'
																		+ receivable.referenceDocumentNumber
																		+ '</td><td>'
																		+ receivable.referenceDocumentDate
																		+ '</td><td>'
																		+ receivable.billOverDue
																		+ '</td><td id="balance'
																		+ receivable.pid
																		+ '">'
																		+ receivable.referenceDocumentBalanceAmount
																		+ '</td><td><input type="number"	class="form-control" style="width: 110px; height: 40px"	id="txtAllAmount'
																		+ receivable.pid
																		+ '" maxlength="5" placeholder="Amount" min="1" /></td><td><button type="button" class="btn btn-success" onclick="AccountingVoucherTransaction.addBatchToAccountingDetail(\''
																		+ receivable.pid
																		+ '\') ">Add</button></td></tr>');
											});

							// show allocated amounts
							showAllocatedAmounts(index, amount);
						}
					}
				});
		$('#allocationModal').modal('show');
	}

	function showAllocatedAmounts(index, amount) {
		var balance = amount;
		$('#lblAlloBalAmount').text(amount);
		$
				.each(
						accountingVoucher.accountingVoucherDetails,
						function(key, accountingVoucherDetail) {
							if (index == key) {
								$
										.each(
												accountingVoucherDetail.accountingVoucherAllocations,
												function(key1,
														voucherAllocation) {
													$(
															'#txtAllAmount'
																	+ voucherAllocation.receivablePayablePid)
															.val(
																	voucherAllocation.amount);
													balance = balance
															- voucherAllocation.amount;
												});
							}
						});
	}

	AccountingVoucherTransaction.addBatchToAccountingDetail = function(pid) {

		var activeIndex = $('#hdnAlloIndex').val();

		var amount = $('#txtAllAmount' + pid).val();
		if (amount == 0 || amount == '') {
			addErrorAlertBatch("Enter amount");
			return;
		}

		var oldAmount = 0;
		var index = -1;
		// find already allocated amount
		$.each(accountingVoucher.accountingVoucherDetails, function(key1,
				accountingVoucherDetail) {
			if (key1 == activeIndex) {
				$.each(accountingVoucherDetail.accountingVoucherAllocations,
						function(key, allocation) {
							if (allocation.receivablePayablePid == pid) {
								oldAmount = parseFloat(allocation.amount);
								index = key;
							}
						});
			}
		});

		var totalBalance = parseFloat($('#lblAlloBalAmount').text());
		totalBalance = (totalBalance + oldAmount) - amount;
		// check stock
		if (totalBalance < 0) {
			addErrorAlertBatch("Invalid");
			return;
		}
		$('#lblAlloBalAmount').text(totalBalance);

		var balance = parseFloat($('#balance' + pid).text());
		balance = (balance + oldAmount) - amount;
		$('#balance' + pid).text(balance);

		var referenceNumber = $('#number' + pid).text();

		if (index == -1) {
			var accountingVoucherAllocation = {
				receivablePayablePid : pid,
				voucherNumber : 1,
				referenceNumber : referenceNumber,
				referenceDocumentNumber : referenceNumber,
				mode : 1,
				amount : amount,
				remarks : null
			};
			$.each(accountingVoucher.accountingVoucherDetails, function(key1,
					accountingVoucherDetail) {
				if (key1 == activeIndex) {
					accountingVoucherDetail.accountingVoucherAllocations
							.push(accountingVoucherAllocation);
				}
			});
		} else {
			$
					.each(
							accountingVoucher.accountingVoucherDetails,
							function(key1, accountingVoucherDetail) {
								if (key1 == activeIndex) {
									accountingVoucherDetail.accountingVoucherAllocations[index].amount = amount;
								}
							});
		}
		// clear
		clearAndHideErrorBoxBatch();

	}

	AccountingVoucherTransaction.editAccountingDetail = function(index) {
		$('#hdnIndex').val(index);

		onEditAccountingDetails(index);

		$('#accountingVoucherModal').modal('show');
	}

	AccountingVoucherTransaction.removeAccountingDetail = function(index, obj) {
		accountingVoucher.accountingVoucherDetails.splice(index, 1);
		$(obj).closest('tr').remove();
		if (accountingVoucher.accountingVoucherDetails.length == 0) {
			$('#tbodyAccountingDetails').html(
					"<tr><td colspan='3'>No data avilable</td></tr>");
		}
	}

	function submitAccountingVoucher() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}

		if (accountingVoucher.accountingVoucherDetails.length == 0) {
			addErrorAlert("Please add vouchers");
			return;
		}

		var activityPid = $('#dbDocument option:selected').val().split("~")[1];
		var accountPid = $("#dbAccount").val();
		accountingVoucher.accountProfilePid = accountPid;

		var employeePid = $("#dbEmployee").val();
		accountingVoucher.employeePid = employeePid;

		var documentPid = $('#dbDocument').val().split("~")[0];
		accountingVoucher.documentPid = documentPid;

		var executiveTaskSubmission = {
			executiveTaskExecutionDTO : {
				activityPid : activityPid,
				accountProfilePid : accountPid
			},
			accountingVouchers : [ accountingVoucher ]
		};
		$.ajax({
			method : 'POST',
			url : accountingVoucherTransactionContextPath,
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(executiveTaskSubmission),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	var isReferenceDocumentSearch = false;
	AccountingVoucherTransaction.search = function() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}
		isReferenceDocumentSearch = false;
		var documentPid = $("#dbDocument").val().split("~")[0];
		searchAccountingVouchers(documentPid);
	}

	AccountingVoucherTransaction.showReferenceDocuments = function() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}

		isReferenceDocumentSearch = true;
		$("#dbReferenceDocument").html(
				"<option>Reference Documents loading...</option>");
		$
				.ajax({
					url : accountingVoucherTransactionContextPath
							+ "/reference-documents/"
							+ $("#dbDocument").val().split("~")[0],
					method : 'GET',
					success : function(referenceDocuments) {
						$("#dbReferenceDocument")
								.html(
										"<option value='no'>Select Reference Document</option>");
						$.each(referenceDocuments, function(key,
								referenceDocument) {
							$("#dbReferenceDocument").append(
									"<option value='" + referenceDocument.pid
											+ "'>" + referenceDocument.name
											+ "</option>");
						});
					}
				});
		$('#referenceDocumentModal').modal('show');
	}

	AccountingVoucherTransaction.searchByReferenceDocument = function() {
		$('#referenceDocumentModal').modal('hide');
		if ($("#dbReferenceDocument").val() == 'no') {
			return;
		}
		var documentPid = $("#dbReferenceDocument").val();
		searchAccountingVouchers(documentPid);
	}

	function searchAccountingVouchers(documentPid) {
		$('#divAccounting').hide();
		$('#lblDocument').text($('#dbDocument option:selected').text());
		$('#lblAccount').text($('#dbAccount option:selected').text());
		$('#tblAccountingVouchers').html(
				'<tr><td colspan="4">Please wait...</td></tr>');
		$
				.ajax({
					url : accountingVoucherTransactionContextPath + "/search",
					type : 'GET',
					data : {
						documentPid : documentPid,
						activityPid : $("#dbDocument").val().split("~")[1],
						accountPid : $("#dbAccount").val()
					},
					success : function(accountingVouchers) {
						if (accountingVouchers.length > 0) {
							$('#tblAccountingVouchers').html('');
							$
									.each(
											accountingVouchers,
											function(key, accountingVoucher) {
												$('#tblAccountingVouchers')
														.append(
																'<tr data-id="'
																		+ accountingVoucher.pid
																		+ '" data-parent=""><td>'
																		+ convertDateTimeFromServer(accountingVoucher.documentDate)
																		+ '</td><td>'
																		+ accountingVoucher.documentName
																		+ '</td><td>'
																		+ accountingVoucher.documentNumberLocal
																		+ '</td></tr>');
												/*
												 * <td><button type="button"
												 * class="btn btn-blue"
												 * onclick="AccountingVoucherTransaction.editAccountingVoucher(\'' +
												 * accountingVoucher.pid +
												 * '\')">Select</button></td>
												 */

												if (accountingVoucher.history != null) {
													$
															.each(
																	accountingVoucher.history,
																	function(
																			index,
																			history) {
																		var i = "-&nbsp;";
																		if (index == 0) {
																			i += "&nbsp;";
																		}
																		$(
																				'#tblAccountingVouchers')
																				.append(
																						'<tr style="background: rgba(225, 225, 225, 0.66);" data-id="'
																								+ index
																								+ '" data-parent="'
																								+ accountingVoucher.pid
																								+ '"><td>'
																								+ i
																								+ convertDateTimeFromServer(history.documentDate)
																								+ '</td><td>'
																								+ history.documentName
																								+ '</td><td>'
																								+ history.documentNumberLocal
																								+ '</td><td>&nbsp;</td></tr>');
																	});
												}

											});
							$('.collaptable')
									.aCollapTable(
											{
												startCollapsed : true,
												addColumn : false,
												plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
												minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
											});

						} else {
							$('#tblAccountingVouchers')
									.html(
											'<tr><td colspan="4">No data available</td></tr>');
						}
					}
				});
		clearAndHideErrorBox();
		$('#searchModal').modal('show');
	}

	AccountingVoucherTransaction.editAccountingVoucher = function(
			accountingVoucherPid) {

		// set headers
		setDocumentNameAndPreviousDocumentNumber();

		var documentPid = $('#dbDocument').val().split("~")[0];
		var activityPid = $('#dbDocument').val().split("~")[1];

		// load Receiver An dSupplier accounts
		loadByAndToAccounts(activityPid, documentPid, false);

		$
				.ajax({
					url : accountingVoucherTransactionContextPath + "/"
							+ accountingVoucherPid,
					method : 'GET',
					success : function(accountingVoucher1) {
						accountingVoucher = accountingVoucher1;
						// set referece
						if (isReferenceDocumentSearch) {
							$
									.each(
											accountingVoucher.accountingVoucherDetails,
											function(key,
													accountingVoucherDetail) {
												accountingVoucherDetail.referenceAccountingVoucherHeaderPid = accountingVoucher1.pid;
												accountingVoucherDetail.referenceAccountingVoucherDetailId = accountingVoucherDetail.detailId;
											});
							accountingVoucher.pid = null;
						}
						updateAccountingDetailsView();
						$("#dbReceiver").val(
								accountingVoucher1.receiverAccountPid);
						$("#dbSupplier").val(
								accountingVoucher1.supplierAccountPid);
						$('#divDocumentNumber').text(
								accountingVoucher1.documentNumberLocal);
					}
				});
		$('#searchModal').modal('hide');
		$('#divAccounting').show();
	}

	AccountingVoucherTransaction.print = function(dynamicDocumentPid) {
		var url = location.protocol + '//' + location.host + "/web/print/"
				+ dynamicDocumentPid
		window.location = url;
	}

	AccountingVoucherTransaction.email = function(dynamicDocumentPid) {
		var url = location.protocol + '//' + location.host + "/web/email/"
				+ dynamicDocumentPid
		$
				.ajax({
					url : url,
					type : 'GET',
					success : function(status) {
						if (status === true) {
							onSaveSuccess(status)
						} else {
							console
									.log("mail voucher template not configured properly.")
						}

					}
				});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountingVoucherTransactionContextPath;
	}

	function clearAndHideErrorBox() {
		$(".alert > p").html("");
		$('.alert').hide();
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function addErrorAlertBatch(message, key, data) {
		$(".alert-batch > p").html(message);
		$('.alert-batch').show();
	}

	function clearAndHideErrorBoxBatch() {
		$(".alert-batch > p").html();
		$('.alert-batch').hide();

		$('#txtBatchQuantity').val(0);
		$("#dbBatch").select2("val", "no");
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

	function convertToServerFormat(date) {
		return moment(date);
	}

	function convertDateFormat(date) {
		if (date) {
			return moment(date).format('MM/DD/YYYY');
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