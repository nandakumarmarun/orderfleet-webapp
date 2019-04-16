// Create a TaskExecution object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.TaskExecution) {
	this.TaskExecution = {};
}

(function() {
	'use strict';

	var taskExecutionContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var taskExecution = {
		activityPid : null,
		accountProfilePid : null,
		remarks : null
	};

	var inventoryVouchers = [];
	var accountingVouchers = [];

	$(document).ready(function() {

	});

	function createUpdateTaskExecution(el) {

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(taskExecutionModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	TaskExecution.showDocuments = function(activityPid, accountProfilePid) {
		taskExecution.activityPid = activityPid;
		taskExecution.accountProfilePid = accountProfilePid;
		inventoryVouchers = [];
		accountingVouchers = [];
		console.log(taskExecution)
		$('#userDocuments').show();
		$('#executiveTaskPlans').hide();
		$
				.ajax({
					url : taskExecutionContextPath + "/user-documents",
					method : 'GET',
					success : function(documents) {
						$('#tblDocuments').html("");
						$
								.each(
										documents,
										function(index, document) {
											$('#tblDocuments')
													.append(
															"<tr><td>"
																	+ document.name
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='TaskExecution.showForm(\""
																	+ document.pid
																	+ "\",\""
																	+ document.documentType
																	+ "\",\""
																	+ accountProfilePid
																	+ "\");'>Select</button></td></tr>");
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	// show user documents div
	TaskExecution.showForm = function(documentPid, documentType,
			accountProfilePid) {
		$('#userDocuments').hide();
		if (documentType == "INVENTORY_VOUCHER") {
			documentInventoryVoucher(documentPid, accountProfilePid);
		} else if (documentType == "ACCOUNTING_VOUCHER") {
			documentAccountingVoucher(documentPid, accountProfilePid);
		} else if (documentType == "DYNAMIC_DOCUMENT") {
			$('#dynamicForms').show();
		}

	}

	/** Inventory Voucher Management START */
	function documentInventoryVoucher(documentPid, accountProfilePid) {
		// create or update inventoryVoucher
		manageInventoryVoucherList(documentPid, accountProfilePid);

		$('#inventoryVoucherForm').show();
		$
				.ajax({
					url : taskExecutionContextPath + "/products",
					method : 'GET',
					success : function(products) {
						console.log(products)
						$('#tblProducts').html("");
						$
								.each(
										products,
										function(index, product) {
											$('#tblProducts')
													.append(
															"<tr><td>"
																	+ product.name
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='TaskExecution.showInventoryModal(\""
																	+ product.pid
																	+ "\",\""
																	+ documentPid
																	+ "\",this);'>Select</button></td></tr>");
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function manageInventoryVoucherList(documentPid, receiverAccountPid) {
		var isNew = true;
		$.each(inventoryVouchers, function(index, inventoryVoucher) {
			if (inventoryVoucher.documentPid == documentPid) {
				isNew = false;
				return;
			}
		});
		if (isNew) {
			var inventoryVoucher = {
				documentPid : documentPid,
				receiverAccountPid : receiverAccountPid,
				inventoryVoucherDetails : []
			}
			inventoryVouchers.push(inventoryVoucher)
		}
	}

	// show Inventory detail popup
	TaskExecution.showInventoryModal = function(productPid, documentPid, obj) {

		clearInventoryModal();

		var tr = obj.closest("tr");
		var productName = $(tr).find("td").eq(0).html();

		$('#hdnDocumentPid').val(documentPid);
		$('#hdnProductPid').val(productPid);
		$('#lblProductName').text(productName);

		// set vlues to modal
		onEditInventoryDetails(documentPid, productPid);

		$('#inventoryVoucherModal').modal('show');

		$.ajax({
			url : taskExecutionContextPath + "/documentInventoryVoucherColumns"
					+ "/" + documentPid,
			method : 'GET',
			success : function(inventoryVoucherColumns) {
				console.log(inventoryVoucherColumns);
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

	function onEditInventoryDetails(documentPid, productPid) {
		$.each(inventoryVouchers, function(index, inventoryVoucher) {
			if (inventoryVoucher.documentPid == documentPid) {
				$.each(inventoryVoucher.inventoryVoucherDetails, function(
						index, inVoucherDetail) {
					if (inVoucherDetail.productPid == productPid) {
						$('#QUANTITY').val(inVoucherDetail.quantity);
						$('#FREE_QUANTITY').val(inVoucherDetail.freeQuantity);
						$('#SELLING_RATE').val(inVoucherDetail.sellingRate);
						$('#DISCOUNT_PERCENTAGE').val(
								inVoucherDetail.discountPercentage);
						return;
					}
				});
			}
		});
	}

	function clearInventoryModal() {
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
	}

	TaskExecution.addToInventoryDetails = function() {
		var inventoryVoucherDetail = {
			productPid : $('#hdnProductPid').val(),
			quantity : $('#QUANTITY').val(),
			freeQuantity : $('#FREE_QUANTITY').val(),
			sellingRate : $('#SELLING_RATE').val(),
			discountPercentage : $('#DISCOUNT_PERCENTAGE').val(),
			itemAmount : $('#QUANTITY').val() * $('#SELLING_RATE').val()
		};

		$
				.each(
						inventoryVouchers,
						function(index, inventoryVoucher) {
							if (inventoryVoucher.documentPid == $('#hdnDocumentPid').val()) {
								var isNew = true;
								console.log(isNew);
								$
										.each(
												inventoryVoucher.inventoryVoucherDetails,
												function(index, inVoucherDetail) {
													if (inVoucherDetail.productPid == inventoryVoucherDetail.productPid) {
														inVoucherDetail.quantity = inventoryVoucherDetail.quantity;
														inVoucherDetail.freeQuantity = inventoryVoucherDetail.freeQuantity;
														inVoucherDetail.sellingRate = inventoryVoucherDetail.sellingRate;
														inVoucherDetail.discountPercentage = inventoryVoucherDetail.discountPercentage;
														inVoucherDetail.itemAmount = inventoryVoucherDetail.itemAmount;
														isNew = false;
														return;
													}
												});
								console.log(isNew);
								if (isNew) {
									inventoryVoucher.inventoryVoucherDetails.push(inventoryVoucherDetail)
								}
							}
						});
		console.log(inventoryVouchers);
		$('#inventoryVoucherModal').modal('hide');
	}
	/** ****Inventory Voucher Management END***** */

	/** *****Accounting Voucher Management ***** */
	function documentAccountingVoucher(documentPid, accountProfilePid) {

		// create or update accountingVoucher
		manageAccountingVoucherList(documentPid, accountProfilePid);

		// clear and hide elements
		clearAccountingVoucherColumns();
		hideAccountingVoucherColumns();

		$('#accountingVoucherForm').show();
		$('#hdnAccountingDocumentPid').val(documentPid);

		$.ajax({
			url : taskExecutionContextPath
					+ "/documentAccountingVoucherColumns" + "/" + documentPid,
			method : 'GET',
			success : function(accountingVoucherColumns) {
				console.log(accountingVoucherColumns);
				$.each(accountingVoucherColumns, function(index,
						accountingVoucherColumn) {
					$('#div' + accountingVoucherColumn.name).show();
					$('#' + accountingVoucherColumn.name).prop('readonly',
							!accountingVoucherColumn.enabled);
				});
				if (accountingVoucherColumns.length > 0) {
					$('#divAccountingBtn').show();
				} else {
					$('#divAccountingBtn').hide();
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	TaskExecution.addToAccountingDetails = function() {
		var accountingVoucherDetail = {
			mode : $('#TYPE').val(),
			amount : $('#AMOUNT').val(),
			instrumentNumber : $('#CHEQUE_NO').val(),
			instrumentDate : $('#CHEQUE_DATE').val(),
			bankName : $('#BANK_NAME').val(),
			byAccountPid : $('#BY').val(),
			toAccountPid : $('#TO').val()
		};

		$.each(accountingVouchers,
						function(index, accountingVoucher) {
							if (accountingVoucher.documentPid == $('#hdnAccountingDocumentPid').val()) {
								var isNew = true;
								console.log(isNew);
								/*$.each(accountingVoucher.accountingVoucherDetails,
												function(index, inVoucherDetail) {
													if (inVoucherDetail.productPid == accountingVoucherDetail.productPid) {
														inVoucherDetail.quantity = accountingVoucherDetail.quantity;
														inVoucherDetail.freeQuantity = accountingVoucherDetail.freeQuantity;
														inVoucherDetail.sellingRate = accountingVoucherDetail.sellingRate;
														inVoucherDetail.discountPercentage = accountingVoucherDetail.discountPercentage;
														inVoucherDetail.itemAmount = accountingVoucherDetail.itemAmount;
														isNew = false;
														return;
													}
												});
								console.log(isNew);*/
								if (isNew) {
									accountingVoucher.accountingVoucherDetails.push(accountingVoucherDetail)
								}
							}
						});
		console.log(accountingVouchers);
		clearAccountingVoucherColumns();
	}

	function clearAccountingVoucherColumns() {
		$('#AMOUNT').val("");
		$('#TYPE').val("Cash");
		$('#BANK_NAME').val("");
		$('#CHEQUE_DATE').val("");
		$('#CHEQUE_NO').val("");
		$('#BY').val("");
		$('#TO').val("");
	}
	
	function hideAccountingVoucherColumns() {
		$('#divAMOUNT').hide();
		$('#divTYPE').hide();
		$('#divBANK_NAME').hide();
		$('#divCHEQUE_DATE').hide();
		$('#divCHEQUE_NO').hide();
		$('#divBY').hide();
		$('#divTO').hide();

	}

	function manageAccountingVoucherList(documentPid, receiverAccountPid) {
		var isNew = true;
		$.each(accountingVouchers, function(index, accountingVoucher) {
			if (accountingVoucher.documentPid == documentPid) {
				isNew = false;
				return;
			}
		});
		if (isNew) {
			var accountingVoucher = {
				documentPid : documentPid,
				receiverAccountPid : receiverAccountPid,
				accountingVoucherDetails : []
			}
			accountingVouchers.push(accountingVoucher)
		}
	}
	
	function onEditAccountingDetails(documentPid) {
		$.each(inventoryVouchers, function(index, inventoryVoucher) {
			if (inventoryVoucher.documentPid == documentPid) {
				$.each(inventoryVoucher.inventoryVoucherDetails, function(
						index, inVoucherDetail) {
					if (inVoucherDetail.productPid == productPid) {
						$('#QUANTITY').val(inVoucherDetail.quantity);
						$('#FREE_QUANTITY').val(inVoucherDetail.freeQuantity);
						$('#SELLING_RATE').val(inVoucherDetail.sellingRate);
						$('#DISCOUNT_PERCENTAGE').val(
								inVoucherDetail.discountPercentage);
						return;
					}
				});
			}
		});
	}
	

	TaskExecution.back = function(backFrom) {
		$('#' + backFrom).hide()
		if (backFrom == "userDocuments") {
			$('#executiveTaskPlans').show()
		} else if (backFrom == "inventoryVoucherForm"
				|| backFrom == "accountingVoucherForm"
				|| backFrom == "dynamicForms") {
			$('#userDocuments').show()
		}
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = taskExecutionContextPath;
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