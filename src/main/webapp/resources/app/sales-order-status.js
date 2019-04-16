if (!this.SalesOrderStatus) {
	this.SalesOrderStatus = {};
}

(function() {
	'use strict';

	var salesOrderStatusContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });		
		// load today data
		SalesOrderStatus.filter();

		$('#selectAll').on('click', function() {
			selectAllSalesOrderStatus(this);
		});
		
	});

	var documentPidTemp="";
	var filterByTemp="";
	var fromDateTemp="";
	var toDateTemp="";
	SalesOrderStatus.updateStatus=function(){
		
		var salesOrderPids = "";
		$("input[type='checkbox']:checked").each(function() {
			var inventoryVoucherPid = $(this).val();
			if (inventoryVoucherPid != "on") {
				salesOrderPids += inventoryVoucherPid + ",";
			}
		});

		if (salesOrderPids == "") {
			alert("please select sales Order");
		} else {
			$
			.ajax({
				url : salesOrderStatusContextPath + "/updateProcessStatus",
				type : 'GET',
				data : {
					salesOrderPids:salesOrderPids,
					status:$("#dbStatus").val()
				},
				success : function(result) {
					SalesOrderStatus.filter();	
						},
				error : function(xhr, error) {
					onError(xhr, error);
				},
		});
	}
	}

	SalesOrderStatus.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		documentPidTemp= $("#dbDocuments").val();
		filterByTemp=$("#dbDateSearch").val();
		fromDateTemp=$("#txtFromDate").val();
		toDateTemp=$("#txtToDate").val();
		$('#tBodySalesOrderStatus').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : salesOrderStatusContextPath + "/filter",
					type : 'GET',
					data : {
						documentPid : documentPidTemp,
						filterBy :filterByTemp,
						fromDate : fromDateTemp,
						toDate : toDateTemp
					},
					success : function(salesOrderStatus) {
						$('#tBodySalesOrderStatus').html("");
						if (salesOrderStatus.length == 0) {
							$('#tBodysalesOrderStatus')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										salesOrderStatus,
										function(index, salesOrder) {
											
											$('#tBodySalesOrderStatus')
													.append(
															"<tr><td><input type='checkbox' class='check-one' value='"
																	+ salesOrder.pid
																	+ "' />"
																	+ "</td><td>"
																	+ salesOrder.processStatus
																	+ "</td><td>"
																	+ convertDateTimeFromServer(salesOrder.createdDate)
																	+ "</td><td>"
																	+ salesOrder.userName
																	+ "</td><td>"
																	+ salesOrder.receiverAccountName
																	+ "</td><td>"
																	+ salesOrder.documentTotal
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='SalesOrderStatus.showModalPopup($(\"#viewModal\"),\""
																	+ salesOrder.pid
																	+ "\",0);'>View Details</button></td></tr>");
										});
					}
				});
	}
	
	function showSalesOrderStatus(pid) {
		$
				.ajax({
					url : salesOrderStatusContextPath + "/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumber').text(data.documentNumberLocal);
						$('#lbl_user').text(data.userName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_documentDate').text(
								convertDateTimeFromServer(data.createdDate));
						$('#lbl_receiver').text(data.receiverAccountName);
						$('#lbl_supplier').text(data.supplierAccountName);
						$('#lbl_documentTotal').text(data.documentTotal);
						$('#lbl_documentVolume').text(data.documentVolume);
						$('#lbl_documentDiscountAmount').text(
								data.docDiscountAmount);
						$('#lbl_documentDiscountPercentage').text(
								data.docDiscountPercentage);

						$('#tblVoucherDetails').html("");
						$
								.each(
										data.inventoryVoucherDetails,
										function(index, voucherDetail) {
											$('#tblVoucherDetails')
													.append(
															"<tr data-id='"
																	+ index
																	+ "'><td>"
																	+ voucherDetail.productName
																	+ "</td><td>"
																	+ voucherDetail.quantity
																	+ "</td><td>"
																	+ voucherDetail.freeQuantity
																	+ "</td><td>"
																	+ voucherDetail.sellingRate
																	+ "</td><td>"
																	+ voucherDetail.taxPercentage
																	+ "</td><td>"
																	+ voucherDetail.discountPercentage
																	+ "</td><td>"
																	+ voucherDetail.rowTotal
																	+ "</td></tr>");

											$
													.each(
															voucherDetail.inventoryVoucherBatchDetails,
															function(index1,
																	inventoryVoucherBatch) {
																$(
																		'#tblVoucherDetails')
																		.append(
																				"<tr style='background: rgba(225, 225, 225, 0.66);' data-parent='"
																						+ index
																						+ "' >"
																						+ "<td colspan='2'>Batch Number : "
																						+ inventoryVoucherBatch.batchNumber
																						+ "</td><td colspan='3'>Stock Location : "
																						+ inventoryVoucherBatch.stockLocationName
																						+ "</td><td colspan='2'>Quantity : "
																						+ inventoryVoucherBatch.quantity
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
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = salesOrderStatusContextPath;
	}
	
	

	SalesOrderStatus.showDatePicker = function() {
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

	function selectAllSalesOrderStatus(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}

	

	SalesOrderStatus.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showSalesOrderStatus(pid);
				break;
			}
		}
		el.modal('show');
	}

	SalesOrderStatus.closeModalPopup = function(el) {
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