// Create a InventoryVoucherTransaction object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InventoryVoucherTransaction) {
	this.InventoryVoucherTransaction = {};
}
(function() {
	'use strict';

	var inventoryVoucherTransactionContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var inventoryVoucher = {
		pid : null,
		documentPid : null,
		employeePid : null,
		receiverAccountPid : null,
		supplierAccountPid : null,
		inventoryVoucherDetails : [],
		documentTotal : 0.0,
		orderStatusId : null
	};
	var inventoryVoucherColumns = [];
	$(document).ready(function() {

		$("#dbDocument").change(function() {
			$('#divInventory').hide();

			// load account profiles
			loadAccountProfileByActivityId();

			// load Inventory Voucher Columns
			loadInventoryVoucherColumns();

			loadStaticJsCodeFromServer();
		});

		$("#btnSubmitInventoryVoucher").click(function() {
			submitInventoryVoucher();
		});

		// staticJsCode executing events
		$("#SELLING_RATE").keyup(function() {
			calculateAndSetTotal();
		});
		$("#QUANTITY").keyup(function() {
			calculateAndSetTotal();
		});
		$("#DISCOUNT_PERCENTAGE").keyup(function() {
			calculateAndSetTotal();
		});

		var etePid = getParameterByName('etePid');
		var invPid = getParameterByName('invPid');
		if (etePid != null && invPid != null) {
			loadInventory(etePid, invPid);
		}
	});

	function loadInventory(pid, invPid) {
		$.ajax({
			url : inventoryVoucherTransactionContextPath
					+ "/loadExecutiveTaskExecution",
			type : "GET",
			data : {
				pid : pid,
				invPid : invPid,
			},
			success : function(status) {
				$("#dbEmployee").val(status.employeePid);
				$("#dbDocument").val(
						status.documentPid + "~" + status.activityPid);
				loadAccountProfileByActivityId();
				$("#dbAccount").val(status.accountPid);
				InventoryVoucherTransaction.editInventoryVoucher(invPid);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	InventoryVoucherTransaction.returnToTransaction = function() {
		var previousURL = document.referrer;
		if (previousURL.indexOf("executive-task-executions") !== -1) {
			window.history.back();
		} else {
			window.location = location.protocol + '//' + location.host
					+ "/web/executive-task-executions";
		}
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

	function calculateAndSetTotal() {
		var data = {};
		data.selling_rate = $('#SELLING_RATE').val();
		data.quantity = $('#QUANTITY').val();
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
			url : inventoryVoucherTransactionContextPath + "/static-js-code"
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
	var hasDefaultAccount = true;
	function loadAccountProfileByActivityId() {
		hasDefaultAccount = true;
		if ($('#dbDocument').val() == "no") {
			$("#dbAccount").html("<option>Select Account</option>");
			return;
		}
		var documentActivity = $('#dbDocument').val();
		// var activityPid = $('#dbDocument').val().split("~")[1];
		$("#dbAccount").html("<option>Accounts loading...</option>")
		$
				.ajax({
					url : inventoryVoucherTransactionContextPath
							+ "/load-account-profiles/" + documentActivity,
					type : 'GET',
					async : false,
					success : function(accounts) {
						$("#dbAccount").html(
								"<option value='no'>Select Account</option>")

						$
								.each(
										accounts,
										function(key, account) {
											if (account.hasDefaultAccountInventory == hasDefaultAccount) {
												hasDefaultAccount = false;
												$("#dbAccount").append(
														"<option value='"
																+ account.pid
																+ "'>"
																+ account.name
																+ "</option>");
												$("#dbAccount")
														.val(account.pid);
												$(
														'select[name="accoutProfile"]')
														.attr('disabled',
																'disabled');

											} else {
												$("#dbAccount").append(
														"<option value='"
																+ account.pid
																+ "'>"
																+ account.name
																+ "</option>");
											}
										});
					}
				});
	}

	function loadInventoryVoucherColumns() {
		hideInventoryDetailModalElemens();
		if ($('#dbDocument').val() == "no") {
			return;
		}
		var documentPid = $('#dbDocument').val().split("~")[0];
		$.ajax({
			url : inventoryVoucherTransactionContextPath
					+ "/documentInventoryVoucherColumns" + "/" + documentPid,
			method : 'GET',
			success : function(inventoryVoucherColumnList) {
				inventoryVoucherColumns = inventoryVoucherColumnList;
				$.each(inventoryVoucherColumns, function(index,
						inventoryVoucherColumn) {
					$('#div' + inventoryVoucherColumn.name).show();
					$('#' + inventoryVoucherColumn.name).prop('readonly',
							!inventoryVoucherColumn.enabled);
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var sourceStockLocation;
	var destinationStockLocation;
	InventoryVoucherTransaction.saveStockLocation = function() {
		sourceStockLocation = $("#dbSourceStockLocation").val();
		destinationStockLocation = $("#dbDestinationStockLocation").val();
		if (sourceStockLocation != null && destinationStockLocation != null) {
			$("#sourceDestinationLocationPrompt").css("display", "block");
			$("#source").html(
					$(
							"#dbSourceStockLocation option[value='"
									+ $("#dbSourceStockLocation").val() + "']")
							.text());
			$("#destination").html(
					$(
							"#dbDestinationStockLocation option[value='"
									+ $("#dbDestinationStockLocation").val()
									+ "']").text());
		}
	}

	InventoryVoucherTransaction.showInventoryForm = function() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}
		inventoryVoucher = {
			pid : null,
			documentPid : null,
			employeePid : null,
			receiverAccountPid : null,
			supplierAccountPid : null,
			inventoryVoucherDetails : [],
			documentTotal : 0.0
		};
		var documentPid = $('#dbDocument').val().split("~")[0];
		var activityPid = $('#dbDocument').val().split("~")[1];
		inventoryVoucher.documentPid = documentPid;

		// load source destination stock location
		loadSourceDestinationStockLocation(documentPid);
		// set headers
		setDocumentNameAndPreviousDocumentNumber();

		// load Receiver An dSupplier accounts
		loadReceiverAndSupplierAccounts(activityPid, documentPid, true);

		$('#tbodyInventoryDetails').html(
				'<tr><td colspan="6">No data avilable</td></tr>');
		$('#divInventory').show();
		$('#promptStockLocationModal').modal('show');
		clearAndHideErrorBox();
	}

	function loadSourceDestinationStockLocation(documentPid) {
		$("#dbSourceStockLocation").html(
				"<option>Source Location loading...</option>");
		$("#dbDestinationStockLocation").html(
				"<option>Destination Location loading...</option>");
		$
				.ajax({
					url : inventoryVoucherTransactionContextPath
							+ "/load-source-destination-location/"
							+ documentPid,
					type : 'GET',
					async : true,
					success : function(object) {
						$("#dbSourceStockLocation")
								.html(
										"<option value='no'>Select Source Stock Location</option>");
						$("#dbDestinationStockLocation")
								.html(
										"<option value='no'>Select Destination Stock Location</option>");

						$.each(object.sourceStockLocationDTOs, function(key,
								sourceStock) {
							$("#dbSourceStockLocation").append(
									"<option value='" + sourceStock.pid + "'>"
											+ sourceStock.name + "</option>");
						});

						$.each(object.destinationStockLocationDTOs, function(
								key1, destinationStock) {
							$("#dbDestinationStockLocation").append(
									"<option value='" + destinationStock.pid
											+ "'>" + destinationStock.name
											+ "</option>");
						});
					},
				});
	}

	function loadReceiverAndSupplierAccounts(activityPid, documentPid, async) {
		$("#dbReceiver").html("<option>Receivers loading...</option>");
		$("#dbSupplier").html("<option>Suppliers loading...</option>");
		$.ajax({
			url : inventoryVoucherTransactionContextPath
					+ "/load-receiver-supplier-account/" + activityPid + "/"
					+ documentPid,
			type : 'GET',
			async : async,
			success : function(object) {
				$("#dbReceiver").html(
						"<option value='no'>Select Receiver</option>");
				$("#dbSupplier").html(
						"<option value='no'>Select Supplier</option>");
				var valueForDisableReceiver = 0;
				if (!hasDefaultAccount) {
					$.each(object.receivers, function(key, receiver) {
						if (valueForDisableReceiver == 0) {
							valueForDisableReceiver = 1;
							$("#dbReceiver").append(
									"<option value='" + receiver.pid + "'>"
											+ receiver.name + "</option>");
							$("#dbReceiver").val(receiver.pid);
							$('select[name="receiver-account"]').attr(
									'disabled', 'disabled');
						}
					});
				} else {

					$.each(object.receivers, function(key, receiver) {
						$("#dbReceiver").append(
								"<option value='" + receiver.pid + "'>"
										+ receiver.name + "</option>");
					});
				}
				var valueForDisableSupplier = 0;
				if (!hasDefaultAccount) {
					$.each(object.suppliers, function(key1, supplier) {
						if (valueForDisableSupplier == 0) {
							valueForDisableSupplier = 1;
							$("#dbSupplier").append(
									"<option value='" + supplier.pid + "'>"
											+ supplier.name + "</option>");

							$("#dbSupplier").val(supplier.pid);
							$('select[name="supplier-account"]').attr(
									'disabled', 'disabled');
						}
					});
				} else {

					$.each(object.suppliers, function(key1, supplier) {
						$("#dbSupplier").append(
								"<option value='" + supplier.pid + "'>"
										+ supplier.name + "</option>");
					});
				}
			}
		});
	}

	function setDocumentNameAndPreviousDocumentNumber() {
		$('#divDocumentNumber').text("---");
		$('#divDocumentName').text($('#dbDocument option:selected').text());
		// get previous document number
		$.ajax({
			url : inventoryVoucherTransactionContextPath
					+ "/previous-document-number",
			type : 'GET',
			success : function(previousDocumentNumber) {
				$('#divPreviousDocumentNumber').text(previousDocumentNumber);
			}
		});
	}

	// show Inventory detail popup
	InventoryVoucherTransaction.showInventoryModal = function() {

		var productPid = $('#dbProduct option:selected').val();
		var productName = $('#dbProduct option:selected').text();
		if (productPid == "no") {
			return;
		}

		// get product details
		$.ajax({
			url : inventoryVoucherTransactionContextPath + "/product-details/"
					+ productPid,
			type : 'GET',
			success : function(product) {
				$('#SELLING_RATE').val(product.price);
				$('#SIZE').val(product.size);
				$('#UNIT_QUANTITY').val(product.unitQty);
				$('#lblSKU').text(product.sku);
			}
		});

		// clear popup
		clearInventoryDetailModalElementValues();

		$('#hdnProductPid').val(productPid);
		$('#lblProductName').text(productName);
		console.log(productPid + "---" + productName);
		// check and set vlues to modal
		onEditInventoryDetails(productPid);

		$('#inventoryVoucherModal').modal('show');
	}

	function hideInventoryDetailModalElemens() {
		$('#divQUANTITY').hide();
		$('#divMRP').hide();
		$('#divSELLING_RATE').hide();
		$('#divSTOCK').hide();
		$('#divFREE_QUANTITY').hide();
		$('#divDISCOUNT_PERCENTAGE').hide();
		$('#divDISCOUNT_AMOUNT').hide();
		$('#divTAX_AMOUNT').hide();
		$('#divVOLUME').hide();
		$('#divOFFER_STRING').hide();
		$('#divREMARKS').hide();
		$('#divSIZE').hide();
		$('#divUNIT_QUANTITY').hide();
	}

	function clearInventoryDetailModalElementValues() {
		$('#QUANTITY').val("");
		$('#MRP').val("");
		$('#SELLING_RATE').val("");
		$('#STOCK').val("");
		$('#FREE_QUANTITY').val("");
		$('#DISCOUNT_PERCENTAGE').val("");
		$('#DISCOUNT_AMOUNT').val("");
		$('#TAX_AMOUNT').val("");
		$('#VOLUME').val("");
		$('#OFFER_STRING').val("");
		$('#REMARKS').val("");
		$('#SIZE').val("");
		$('#UNIT_QUANTITY').val("");
		$('#lblSKU').text("");
	}

	function onEditInventoryDetails(productPid) {
		console.log(productPid);
		$.each(inventoryVoucher.inventoryVoucherDetails, function(index,
				inVoucherDetail) {
			if (inVoucherDetail.productPid == productPid) {
				$('#QUANTITY').val(inVoucherDetail.quantity);
				$('#MRP').val(inVoucherDetail.mrp);
				$('#SELLING_RATE').val(inVoucherDetail.sellingRate);
				$('#STOCK').val(inVoucherDetail.stock);
				$('#FREE_QUANTITY').val(inVoucherDetail.freeQuantity);
				$('#DISCOUNT_PERCENTAGE').val(
						inVoucherDetail.discountPercentage);
				$('#DISCOUNT_AMOUNT').val(inVoucherDetail.discountAmount);
				$('#TAX_AMOUNT').val(inVoucherDetail.taxAmount);
				// $('#VOLUME').val(inVoucherDetail.volume);
				$('#REMARKS').val(inVoucherDetail.remarks);
				$('#SIZE').val(inVoucherDetail.size);
				return;
			}
		});
	}

	InventoryVoucherTransaction.addToInventoryDetails = function() {
		if ($('#QUANTITY').val() == 0 || $('#QUANTITY').val() == "") {
			return;
		}
		var inventoryVoucherDetail = {
			productPid : $('#hdnProductPid').val(),
			productName : $('#lblProductName').text(),
			quantity : $('#QUANTITY').val(),
			freeQuantity : $('#FREE_QUANTITY').val(),
			sellingRate : $('#SELLING_RATE').val(),
			discountPercentage : $('#DISCOUNT_PERCENTAGE').val(),
			size : $('#SIZE').val(),
			rowTotal : $('#QUANTITY').val() * $('#SELLING_RATE').val(),
			destinationStockLocationPid : destinationStockLocation,
			sourceStockLocationPid : sourceStockLocation,
			inventoryVoucherBatchDetails : []
		};

		var isNew = true;
		$
				.each(
						inventoryVoucher.inventoryVoucherDetails,
						function(index, inVoucherDetail) {
							if (inVoucherDetail.productPid == inventoryVoucherDetail.productPid) {
								inVoucherDetail.quantity = inventoryVoucherDetail.quantity;
								inVoucherDetail.freeQuantity = inventoryVoucherDetail.freeQuantity;
								inVoucherDetail.sellingRate = inventoryVoucherDetail.sellingRate;
								inVoucherDetail.discountPercentage = inventoryVoucherDetail.discountPercentage;
								inVoucherDetail.size = inventoryVoucherDetail.size;
								inVoucherDetail.rowTotal = inventoryVoucherDetail.rowTotal;
								inVoucherDetail.destinationStockLocationPid = inventoryVoucherDetail.destinationStockLocationPid;
								inVoucherDetail.sourceStockLocationPid = inventoryVoucherDetail.sourceStockLocationPid;
								isNew = false;
								return;
							}
						});
		if (isNew) {
			inventoryVoucher.inventoryVoucherDetails
					.push(inventoryVoucherDetail);
		}

		// update view
		updateInventoryDetailsView();

		$('#inventoryVoucherModal').modal('hide');
	}

	function updateInventoryDetailsView() {
		var documentTotal = 0;
		var totalSellingRate=0;
		var totalFreeQuantity=0;
		var totalQuantity=0;
		
		$("#lblTotalQuantity").text(totalQuantity);
		$("#lblTOtalSellingRate").text(totalSellingRate);
		$("#lblTotalFreeQuantity").text(totalFreeQuantity);
		
		
		$('#tbodyInventoryDetails').html("");
		$
				.each(
						inventoryVoucher.inventoryVoucherDetails,
						function(index, inVoucherDetail) {
							documentTotal += inVoucherDetail.rowTotal;
							
							totalQuantity += inVoucherDetail.quantity;
							totalSellingRate += inVoucherDetail.sellingRate;
							totalFreeQuantity += inVoucherDetail.freeQuantity;
							
							console.log(totalQuantity);
							
							$('#tbodyInventoryDetails')
									.append(
											"<tr><td>"
													+ inVoucherDetail.productName
													+ "</td><td>"
													+ inVoucherDetail.quantity
													+ "</td><td>"
													+ inVoucherDetail.sellingRate
													+ "</td><td>"
													+ inVoucherDetail.freeQuantity
													+ "</td><td>"
													+ inVoucherDetail.discountPercentage
													+ "</td><td><button type='button' class='btn btn-blue' onclick='InventoryVoucherTransaction.editInventoryDetail(\""
													+ inVoucherDetail.productPid
													+ "\",\""
													+ inVoucherDetail.productName
													+ "\");'>Edit</button>&nbsp;<button type='button' class='btn btn-blue' onclick='InventoryVoucherTransaction.enterBatch(\""
													+ inVoucherDetail.productPid
													+ "\",\""
													+ inVoucherDetail.productName
													+ "\");'>Batch</button>"
													+ "	<button type='button' class='btn btn-danger' onclick='InventoryVoucherTransaction.removeInventoryDetail(\""
													+ inVoucherDetail.productPid
													+ "\",this);'>Remove</button></td></tr>");
						});

		inventoryVoucher.documentTotal = documentTotal;
		$("#lblTotalQuantity").text(totalQuantity);
		$("#lblTOtalSellingRate").text(totalSellingRate.toFixed(2));
		$("#lblTotalFreeQuantity").text(totalFreeQuantity);
	}

	InventoryVoucherTransaction.enterBatch = function(productPid, productName) {

		// clear
		clearAndHideErrorBoxBatch();

		// set
		$('#lblBatchProductName').text(productName);
		$('#hdnBatchProductPid').val(productPid);

		$('#dbBatch').html('<option value="no">Select Batch</option>');
		// get product batchs
		$.ajax({
			url : inventoryVoucherTransactionContextPath + "/batch/"
					+ productPid,
			type : 'GET',
			success : function(batchs) {
				if (batchs.length > 0) {
					$.each(batchs, function(key, batch) {
						$('#dbBatch').append(
								'<option value="' + batch.batchNumber + "~"
										+ batch.quantity + "~"
										+ batch.stockLocationPid + "~"
										+ batch.stockLocationName + '">'
										+ batch.batchNumber + "["
										+ batch.stockLocationName + ']' + "["
										+ batch.quantity + ']' + '</option>');
					});
				}
			}
		});

		// show added batchs
		$
				.each(
						inventoryVoucher.inventoryVoucherDetails,
						function(key, inventoryVoucherDetail) {
							if (inventoryVoucherDetail.productPid == productPid) {
								// update view
								if (inventoryVoucherDetail.inventoryVoucherBatchDetails != null) {
									updateBatchTableView(inventoryVoucherDetail.inventoryVoucherBatchDetails);
								}
							}
						});

		$('#batchModal').modal('show');
	}

	InventoryVoucherTransaction.addBatchToInventoryDetail = function() {

		if ($('#dbBatch').val() == 'no' || $('#txtBatchQuantity').val() == 0) {
			addErrorAlertBatch("Select batch number and enter quantity");
			return;
		}

		var productPid = $('#hdnBatchProductPid').val();
		var batchNumber = $('#dbBatch').val().split("~")[0];
		var stock = $('#dbBatch').val().split("~")[1];
		var stockLocationPid = $('#dbBatch').val().split("~")[2];
		var stockLocationName = $('#dbBatch').val().split("~")[3];
		var quantity = parseFloat($('#txtBatchQuantity').val());

		// check stock
		if (stock < quantity) {
			addErrorAlertBatch("No Stock");
			return;
		}
		var inventoryVoucherBatchDetail = {
			productPid : productPid,
			stockLocationPid : stockLocationPid,
			stockLocationName : stockLocationName,
			batchNumber : batchNumber,
			quantity : quantity
		};
		$
				.each(
						inventoryVoucher.inventoryVoucherDetails,
						function(key, inventoryVoucherDetail) {
							if (inventoryVoucherDetail.productPid == productPid) {
								inventoryVoucherDetail.inventoryVoucherBatchDetails
										.push(inventoryVoucherBatchDetail);

								// update inventoryVoucherDetail item quantity
								if (isReferenceDocumentSearch) {
									var quantity = 0;
									$
											.each(
													inventoryVoucherDetail.inventoryVoucherBatchDetails,
													function(key1, batchDetail) {
														quantity = (batchDetail.quantity + quantity);
													});
									inventoryVoucherDetail.quantity = quantity;
									inventoryVoucherDetail.rowTotal = (quantity * inventoryVoucherDetail.sellingRate);

									// update inventoryVoucherDetail view
									updateInventoryDetailsView();
								}

								// update view
								updateBatchTableView(inventoryVoucherDetail.inventoryVoucherBatchDetails);
							}
						});
		// clear
		clearAndHideErrorBoxBatch();

	}

	function updateBatchTableView(inventoryVoucherBatchDetails) {
		$('#tblBatchs').html("");
		$.each(inventoryVoucherBatchDetails, function(index, batchDetail) {
			$('#tblBatchs').append(
					'<tr><td>' + batchDetail.batchNumber + " "
							+ batchDetail.stockLocationName
							+ '</td><td colspan="2">' + batchDetail.quantity
							+ '</td></tr>');
		});
	}

	InventoryVoucherTransaction.editInventoryDetail = function(productPid,
			productName) {
		console.log(1);
		$('#hdnProductPid').val(productPid);
		$('#lblProductName').text(productName);
		onEditInventoryDetails(productPid);
		$('#inventoryVoucherModal').modal('show');
	}

	InventoryVoucherTransaction.removeInventoryDetail = function(productPid,
			obj) {
		var index = 0;
		$.each(inventoryVoucher.inventoryVoucherDetails, function(key,
				inVoucherDetail) {
			if (inVoucherDetail.productPid == productPid) {
				index = key;
				return;
			}
		});
		inventoryVoucher.inventoryVoucherDetails.splice(index, 1);
		$(obj).closest('tr').remove();
		if (inventoryVoucher.inventoryVoucherDetails.length == 0) {
			$('#tbodyInventoryDetails').html(
					"<tr><td colspan='6'>No data avilable</td></tr>");
		}
	}

	function submitInventoryVoucher() {
		
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}
		if ($("#dbReceiver").val() == "no" || $("#dbSupplier").val() == "no") {
			addErrorAlert("Please select receiver and supplier");
			return;
		}
		if (inventoryVoucher.inventoryVoucherDetails.length == 0) {
			addErrorAlert("Please select products");
			return;
		}

		var activityPid = $('#dbDocument option:selected').val().split("~")[1];
		var accountPid = $("#dbAccount").val();
		inventoryVoucher.receiverAccountPid = accountPid;

		var employeePid = $("#dbEmployee").val();
		inventoryVoucher.employeePid = employeePid;

		var receiverAccountPid = $("#dbReceiver").val();
		inventoryVoucher.receiverAccountPid = receiverAccountPid;

		var supplierAccountPid = $("#dbSupplier").val();
		inventoryVoucher.supplierAccountPid = supplierAccountPid;

		var documentPid = $('#dbDocument').val().split("~")[0];
		inventoryVoucher.documentPid = documentPid;
		console.log($("#dbStatus").val());
		if($("#dbStatus").val()!=null){
			inventoryVoucher.orderStatusId=$("#dbStatus").val();
		}

		var executiveTaskSubmission = {
			executiveTaskExecutionDTO : {
				activityPid : activityPid,
				accountProfilePid : accountPid
			},
			inventoryVouchers : [ inventoryVoucher ]
		};
		$.ajax({
			method : 'POST',
			url : inventoryVoucherTransactionContextPath,
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
	InventoryVoucherTransaction.search = function() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}
		isReferenceDocumentSearch = false;
		var documentPid = $("#dbDocument").val().split("~")[0];
		searchInventoryVouchers(documentPid);
	}

	InventoryVoucherTransaction.showReferenceDocuments = function() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}

		isReferenceDocumentSearch = true;
		$("#dbReferenceDocument").html(
				"<option>Reference Documents loading...</option>");
		$
				.ajax({
					url : inventoryVoucherTransactionContextPath
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

	InventoryVoucherTransaction.searchByReferenceDocument = function() {
		$('#referenceDocumentModal').modal('hide');
		if ($("#dbReferenceDocument").val() == 'no') {
			return;
		}
		var documentPid = $("#dbReferenceDocument").val();
		searchInventoryVouchers(documentPid);
	}

	function searchInventoryVouchers(documentPid) {
		$('#divInventory').hide();
		$('#lblDocument').text($('#dbDocument option:selected').text());
		$('#lblAccount').text($('#dbAccount option:selected').text());
		$('#tblInventoryVouchers').html(
				'<tr><td colspan="4">Please wait...</td></tr>');
		$
				.ajax({
					url : inventoryVoucherTransactionContextPath + "/search",
					type : 'GET',
					data : {
						documentPid : documentPid,
						activityPid : $("#dbDocument").val().split("~")[1],
						accountPid : $("#dbAccount").val()
					},
					success : function(inventoryVouchers) {
						if (inventoryVouchers.length > 0) {
							$('#tblInventoryVouchers').html('');
							$
									.each(
											inventoryVouchers,
											function(key, inventoryVoucher) {
												$('#tblInventoryVouchers')
														.append(
																'<tr data-id="'
																		+ inventoryVoucher.pid
																		+ '" data-parent=""><td>'
																		+ convertDateTimeFromServer(inventoryVoucher.documentDate)
																		+ '</td><td>'
																		+ inventoryVoucher.documentName
																		+ '</td><td>'
																		+ inventoryVoucher.documentNumberLocal
																		+ '</td><td><button type="button" class="btn btn-blue" onclick="InventoryVoucherTransaction.editInventoryVoucher(\''
																		+ inventoryVoucher.pid
																		+ '\')">Select</button></td></tr>');

												if (inventoryVoucher.history != null) {
													$
															.each(
																	inventoryVoucher.history,
																	function(
																			index,
																			history) {
																		var i = "-&nbsp;";
																		if (index == 0) {
																			i += "&nbsp;";
																		}
																		$(
																				'#tblInventoryVouchers')
																				.append(
																						'<tr style="background: rgba(225, 225, 225, 0.66);" data-id="'
																								+ index
																								+ '" data-parent="'
																								+ inventoryVoucher.pid
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
							$('#tblInventoryVouchers')
									.html(
											'<tr><td colspan="4">No data available</td></tr>');
						}
					}
				});
		clearAndHideErrorBox();
		$('#searchModal').modal('show');
	}
	
	InventoryVoucherTransaction.editInventoryVoucher = function(
			inventoryVoucherPid) {
		inventoryVoucherPidForSetOrder=inventoryVoucherPid;
		// set headers
		setDocumentNameAndPreviousDocumentNumber();

		var documentPid = $('#dbDocument').val().split("~")[0];
		var activityPid = $('#dbDocument').val().split("~")[1];

		// load Receiver An dSupplier accounts
		loadReceiverAndSupplierAccounts(activityPid, documentPid, false);

		var sourceLocationEdit;
		var destinationLocationEdit;
		$
				.ajax({
					url : inventoryVoucherTransactionContextPath + "/"
							+ inventoryVoucherPid,
					method : 'GET',
					success : function(inventoryVoucher1) {
						console.log(inventoryVoucher1);
						if(inventoryVoucher1.orderStatusId!=null){
							$("#dbStatus").val(inventoryVoucher1.orderStatusId);
						}
						inventoryVoucher = inventoryVoucher1;
						// set referece
						if (isReferenceDocumentSearch) {
							$
									.each(
											inventoryVoucher.inventoryVoucherDetails,
											function(key,
													inventoryVoucherDetail) {
												inventoryVoucherDetail.referenceInventoryVoucherHeaderPid = inventoryVoucher1.pid;
												inventoryVoucherDetail.referenceInventoryVoucherDetailId = inventoryVoucherDetail.detailId;
											});
							inventoryVoucher.pid = null;
						}
						updateInventoryDetailsView();
						$("#dbReceiver").val(
								inventoryVoucher1.receiverAccountPid);
						$("#dbSupplier").val(
								inventoryVoucher1.supplierAccountPid);
						$('#divDocumentNumber').text(
								inventoryVoucher1.documentNumberLocal);
						$
								.each(
										inventoryVoucher.inventoryVoucherDetails,
										function(index, inVoucherDetail) {
											sourceLocationEdit = inVoucherDetail.sourceStockLocationName;
											destinationLocationEdit = inVoucherDetail.destinationStockLocationName;
										});
						if (sourceLocationEdit != null
								&& destinationLocationEdit != null) {

							$("#sourceDestinationLocationPrompt").css(
									"display", "block");
							$("#source").html(sourceLocationEdit);
							$("#destination").html(destinationLocationEdit);
						}
					}
				});

		$('#searchModal').modal('hide');
		$('#divInventory').show();
	}

	var inventoryVoucherPidForSetOrder;
	InventoryVoucherTransaction.setOrderStatus = function() {
		
		if($("#dbStatus").val()==-1){
			return;
		}

		$.ajax({
			url : inventoryVoucherTransactionContextPath + "/changeOrderStatus",
			method : 'POST',
			data : {
				inventoryPid : inventoryVoucherPidForSetOrder,
				status : $("#dbStatus").val()
			},
			success : function(data) {
//				onSaveSuccess(data);
				InventoryVoucherTransaction.editInventoryVoucher();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	InventoryVoucherTransaction.print = function(dynamicDocumentPid) {
		var url = location.protocol + '//' + location.host + "/web/print/"
				+ dynamicDocumentPid
		window.location = url;
	}

	InventoryVoucherTransaction.email = function(dynamicDocumentPid) {
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
		window.location = inventoryVoucherTransactionContextPath;
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