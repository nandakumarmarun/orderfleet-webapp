// Create a InventoryVoucher object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InventoryVoucher) {
	this.InventoryVoucher = {};
}

(function() {
	'use strict';

	var inventoryVoucherContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	
	var InventoryVoucherHeaderModel = {
			pid : null,
			ivhd : [],
	};
	var inventoryPidsPdf = "";

	$(document).ready(function() {
		
		$('.selectpicker').selectpicker();

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// load today data
		// InventoryVoucher.filter();

		$('#selectAll').on('click', function() {
			selectAllInventoryVoucher(this);
		});
		$('#downloadXls').on('click', function() {
			downloadXls();
		});
		if ($("#dbDocumentType").val != "no") {
			loadAllDocumentByDocumentType();
		}
		$("#dbDocumentType").change(function() {
			loadAllDocumentByDocumentType();
		});

		$('#packingSlipByCustomer').on('click', function() {
			packingSlipByCustomer();
		});

		$("#btnApply").on('click', function() {
			InventoryVoucher.filter();
		});
		
		$('#sendTransactionsSapPravesh').on('click', function() {
			sendTransactionsSapPravesh();
		});

	});
	
	function sendTransactionsSapPravesh() {

		$(".loader").addClass('show');

		if (confirm("Are you sure?")) {

			$.ajax({
				url : inventoryVoucherContextPath + "/sendTransactionsSapPravesh",
				method : 'GET',
				beforeSend : function() {
					// Show image container
					$("#loader").modal('show');

				},
				success : function(data) {
					$("#loader").modal('hide');
					InventoryVoucher.filter();
					
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
			url : inventoryVoucherContextPath + "/load-document",
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

				InventoryVoucher.filter();

			}
		});
	}

	function showInventoryVoucher(pid) {
		var ivhPid = pid;
		$
				.ajax({
					url : inventoryVoucherContextPath + "/" + pid,
					method : 'GET',
					success : function(data) {
						
						$('#lbl_Pid').text(data.pid);
						$('#lbl_documentNumber').text(data.documentNumberLocal);
						$('#lbl_user').text(data.userName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_documentDate').text(
								convertDateTimeFromServer(data.createdDate));
						$('#lbl_receiver').text(data.receiverAccountName);
						$('#lbl_supplier').text(data.supplierAccountName);
						$('#lbl_documentTotal')
								.text(
										data.updatedStatus ? (data.documentTotalUpdated)
												.toFixed(2)
												: data.documentTotal);
						$('#lbl_documentVolume')
								.text(
										data.updatedStatus ? (data.documentVolumeUpdated)
												.toFixed(2)
												: data.documentVolume);
						$('#lbl_documentDiscountAmount').text(
								data.docDiscountAmount);
						$('#lbl_documentDiscountPercentage').text(
								data.docDiscountPercentage);
						
						$('#tblVoucherDetails').html("");
						
						$
								.each(
										data.inventoryVoucherDetails,
										function(index, voucherDetail) {
											let unitQty = 1;
											if (voucherDetail.productUnitQty) {
												unitQty = voucherDetail.productUnitQty;
											}
											let totQty = Math
													.round(((voucherDetail.updatedStatus ? voucherDetail.updatedQty
															: voucherDetail.quantity) * unitQty) * 100) / 100;
											$('#tblVoucherDetails')
													.append(
															"<tr data-id='"
																	+ index
																	+ "'><td>"
																	+ voucherDetail.productName
																	+ "</td><td>"
//																	+ (voucherDetail.editOrder ? "<div class='input-group'><input type='number' id='updatedQty-"
//																			+ voucherDetail.detailId
//																			+ "' class='form-control' value='"
//																			+  voucherDetail.quantity							
//																			+ "'><div class='input-group-btn'><input type='button' class='btn btn-info'  onClick='InventoryVoucher.saveUp(\""
//																	        + pid
//																        	+ "\",\""
//																	        + voucherDetail.detailId
//																	        + "\");'  value='Save'></div></div>"
																			+ voucherDetail.quantity
																	+ "</td><td>"
																	+ unitQty
																	+ voucherDetail.itemtype
																	+ "</td><td>"
																	+ totQty
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
						
						/*
						 * $('.collaptable') .aCollapTable( { startCollapsed :
						 * true, addColumn : false, plusButton : '<span><i
						 * class="entypo-down-open-mini"></i></span>',
						 * minusButton : '<span><i
						 * class="entypo-up-open-mini"></i></span>' });
						 */
						
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	InventoryVoucher.updateInventory = function(ivhPid, ivdId) {
		$.ajax({
			url : inventoryVoucherContextPath + "/updateInventory",
			data : {
				id : ivdId,
				ivhPid : ivhPid,
				quantity : $('#updatedQty-' + ivdId).val()
			},
			method : 'GET',
			success : function(data) {
				showInventoryVoucher(ivhPid);
				InventoryVoucher.filter();
			}

		});
	}
	

	InventoryVoucher.saveUp = function(ivhPid, ivdId) {
		if(InventoryVoucherHeaderModel.pid == null){
			InventoryVoucherHeaderModel.pid = ivhPid;
		}
		InventoryVoucherHeaderModel.ivhd.push({
			"id" : ivdId,
			"qnty" : $('#updatedQty-' + ivdId).val(),
		})
	}
	
	$('#myFormCancle').on(
			'click',
			function() {
				InventoryVoucherHeaderModel.pid = null;
				InventoryVoucherHeaderModel.ivhd =[];
				})
			
	$('#myFormSubmit').on(
			'click',
			function() {
				if(InventoryVoucherHeaderModel.pid == null){
					alert("Please Save Value");
					return;
				}
				alert(InventoryVoucherHeaderModel.pid);
				InventoryVoucherHeaderModel.ivhd.forEach(element => {
				  alert(element.id);
				  alert(element.qnty);
				});
				$.ajax({
					url : inventoryVoucherContextPath + "/updateInventory",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(InventoryVoucherHeaderModel),
					method : 'POST',
					success : function(data) {
						InventoryVoucherHeaderModel.pid = null;
						InventoryVoucherHeaderModel.ivhd =[];
						alert("Sucsess");
						window.location = inventoryVoucherContextPath;
					}
				});
			})
	InventoryVoucher.filter = function() {
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
		$('#tBodyInventoryVoucher').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		/*
		 * console.log($("#dbStatus").val() + "----" + empPids + "----" +
		 * $("#dbAccount").val() + "---" + $("#dbDateSearch").val() + "---" +
		 * $("#txtFromDate").val() + "---" + $("#txtToDate").val() + "---" +
		 * $("#dbDocumentType").val() + "---" + docPids);
		 */
		$
				.ajax({
					url : inventoryVoucherContextPath + "/filter",
					type : 'GET',
					data : {
						tallyDownloadStatus : $("#dbStatus").val(),
						employeePids : empPids,
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						voucherType : $("#dbDocumentType").val(),
						documentPids : docPids,
					},
					success : function(inventoryVouchers) {
						$("#lblCounts").text("0");
						$('#tBodyInventoryVoucher').html("");
						if (inventoryVouchers.length == 0) {
							$('#tBodyInventoryVoucher')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var counts = 0;
						let totAmount = 0;
						let totVolume = 0;
						inventoryPidsPdf = "";
						$
								.each(
										inventoryVouchers,
										function(index, inventoryVoucher) {
											counts += 1;
											totAmount += (inventoryVoucher.updatedStatus ? inventoryVoucher.documentTotalUpdated
													: inventoryVoucher.documentTotal);
											totVolume += (inventoryVoucher.updatedStatus ? inventoryVoucher.documentVolumeUpdated
													: inventoryVoucher.totalVolume);

											var button = "";

											var sendSalesOrderSapButton = "";

											if (inventoryVoucher.sendSalesOrderSapButtonStatus) {

												if (inventoryVoucher.tallyDownloadStatus == "PENDING") {
													sendSalesOrderSapButton = "<br><br><button type='button' class='btn btn-primary' id='salesOrderSapButton' onclick='InventoryVoucher.downloadSalesOrderSap(\""
															+ inventoryVoucher.pid
															+ "\",\""
															+ inventoryVoucher.salesManagementStatus
															+ "\");'>Send Sales Order</button>";
												} else if (inventoryVoucher.tallyDownloadStatus == "PROCESSING") {
													sendSalesOrderSapButton = "<br><br><button type='button' class='btn btn-danger'>Sending Failed</button>";
												} else if (inventoryVoucher.tallyDownloadStatus == "COMPLETED") {
													sendSalesOrderSapButton = "<br><br><button type='button' class='btn btn-success'>Sending Success</button>";
												}
											}

											if (inventoryVoucher.pdfDownloadButtonStatus) {

												if ($("#dbAccount").val() != '-1') {
													$(
															".packingSlipByCustomerClass")
															.addClass('show');
													$(
															".packingSlipByCustomerClass")
															.removeClass('hide');

													if (inventoryVoucher.salesManagementStatus == 'APPROVE') {

														inventoryPidsPdf += inventoryVoucher.pid
																+ ",";
													}
												} else {
													$(
															".packingSlipByCustomerClass")
															.addClass('hide');
													$(
															".packingSlipByCustomerClass")
															.removeClass('show');
												}

												if (inventoryVoucher.pdfDownloadStatus) {

													button = "<br><br><button type='button' class='btn btn-primary' onclick='InventoryVoucher.downloadSalesorderPdf(\""
															+ inventoryVoucher.pid
															+ "\");'>Packing Slip-"
															+ inventoryVoucher.orderNumber
															+ "</button>";
												} else {
													button = "<br><br><button type='button' class='btn btn-success' onclick='InventoryVoucher.downloadSalesorderPdf(\""
															+ inventoryVoucher.pid
															+ "\");'>Packing Slip-"
															+ inventoryVoucher.orderNumber
															+ "</button>";
												}

											}

											$('#tBodyInventoryVoucher')
													.append(
															"<tr><td><input type='checkbox' class='check-one' value='"
																	+ inventoryVoucher.pid
																	+ "' />"
																	+ "</td><td>"
																	+ inventoryVoucher.employeeName
																	+ "</td><td>"
																	+ inventoryVoucher.receiverAccountName
																	+ "</td><td>"
																	+ inventoryVoucher.receiverAccountLocation
																	+ "</td><td>"
																	+ inventoryVoucher.documentName
																	+ "</td><td>"
																	+ (inventoryVoucher.updatedStatus ? inventoryVoucher.documentTotalUpdated
																			: inventoryVoucher.documentTotal)
																	+ "</td><td>"
																	+ inventoryVoucher.totalVolume
																	+ "</td><td>"
																	+ (inventoryVoucher.updatedStatus ? inventoryVoucher.documentVolumeUpdated
																			: inventoryVoucher.documentVolume)
																	+ "</td><td>"
																	+ convertDateTimeFromServer(inventoryVoucher.createdDate)
																	+ "</td><td>"
																	+ spanSalesManagementStatus(
																			inventoryVoucher.pid,
																			inventoryVoucher.salesManagementStatus,
																			inventoryVoucher.tallyDownloadStatus)
																	+ "</td><td>"
																	+ spanStatus(
																			inventoryVoucher.pid,
																			inventoryVoucher.tallyDownloadStatus,
																			inventoryVoucher.salesManagementStatus)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='InventoryVoucher.showModalPopup($(\"#viewModal\"),\""
																	+ inventoryVoucher.pid
																	+ "\",0);'>View Details</button>"
																	+ button
																	+ ""
																	+ sendSalesOrderSapButton
																	+ "</td><td>"
																	+ inventoryVoucher.visitRemarks
																	+ "</td></tr>");
										});
						$("#lblCounts").text(counts);
						if (totAmount != 0) {
							$("#totalDocument").html(
									"(" + totAmount.toFixed(2) + ")");
						}
						if (totVolume != 0) {
							$("#totalVolume").html(
									"(" + totVolume.toFixed(2) + ")");
						}
					}
				});
	}

	function packingSlipByCustomer() {
		$(".loader").addClass('show');

		if (confirm("Are you sure?")) {

			$(".loader").removeClass('show');

			console.log(inventoryPidsPdf);

			window.open(inventoryVoucherContextPath
					+ "/downloadPdf?inventoryPid=" + inventoryPidsPdf);
			InventoryVoucher.filter();

		}

		setTimeout(function() {

		}, 1000);

	}

	InventoryVoucher.downloadSalesorderPdf = function(inventoryPid) {

		if (confirm("Are you sure?")) {

			/*
			 * window.location.href = inventoryVoucherContextPath +
			 * "/downloadPdf?inventoryPid=" + inventoryPid;
			 */

			window.open(inventoryVoucherContextPath
					+ "/downloadPdf?inventoryPid=" + inventoryPid);

		}

		setTimeout(function() {
			InventoryVoucher.filter();
		}, 1000);
		// location.reload();

	}

	InventoryVoucher.downloadSalesOrderSap = function(inventoryPid,
			managementStatus) {

		if (managementStatus == "APPROVE") {

			if (confirm("Are you sure?")) {
				$("#salesOrderSapButton").prop('disabled', true);

				$.ajax({
					url : inventoryVoucherContextPath
							+ "/downloadSalesOrderSap",
					method : 'GET',
					data : {
						inventoryPid : inventoryPid,
					},
					beforeSend : function() {
						// Show image container
						$("#loader").modal('show');

					},
					success : function(data) {

						$("#loader").modal('hide');
						$("#salesOrderSapButton").prop('disabled', false);
						InventoryVoucher.filter();

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

			}
		} else {
			alert("Please Verify and Approve Your Sales Order");
			return;
		}

	}

	function spanStatus(inventoryVoucherPid, status, managementStatus) {

		var pending = "'" + 'PENDING' + "'";
		var processing = "'" + 'PROCESSING' + "'";
		var completed = "'" + 'COMPLETED' + "'";
		var spanStatus = "";
		var pid = "'" + inventoryVoucherPid + "'";

		var pointerEnable = '';

		var pointerDisable = '';

		switch (status) {
		case 'PENDING':
			pointerEnable = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer" >';
			pointerDisable = '<div class="dropdown"><span class="label label-default dropdown-toggle" >';

			spanStatus = (managementStatus == "APPROVE" ? pointerEnable
					: pointerDisable)
					+ 'PENDING <span></span></span></div>';
			// <span class="'
			// + (managementStatus == "APPROVE" ? 'caret' : '')
			// + '"></span></span>'
			// + '<ul class="dropdown-menu">'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + processing
			// + ')" style="cursor: pointer;"><a>PROCESSING</a></li>'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + completed
			// + ')" style="cursor: pointer;"><a>COMPLETED</a></li>'
			// + '</ul></div>';

			break;
		case 'PROCESSING':
			pointerEnable = '<div class="dropdown"><span class="label label-warning dropdown-toggle" data-toggle="dropdown" style="cursor: pointer" >';
			pointerDisable = '<div class="dropdown"><span class="label label-warning dropdown-toggle" >';

			spanStatus = (managementStatus == "APPROVE" ? pointerEnable
					: pointerDisable)
					+ 'PROCESSING <span></span></span></div>';
			// <span class="'
			// + (managementStatus == "APPROVE" ? 'caret' : '')
			// + '"></span></span>'
			// + '<ul class="dropdown-menu">'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + pending
			// + ')" style="cursor: pointer;"><a>PENDING</a></li>'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + completed
			// + ')" style="cursor: pointer;"><a>COMPLETED</a></li>'
			// + '</ul></div>';

			break;
		case 'COMPLETED':

			pointerEnable = '<div class="dropdown"><span class="label label-success dropdown-toggle" data-toggle="dropdown" style="cursor: pointer" >';
			pointerDisable = '<div class="dropdown"><span class="label label-success dropdown-toggle" >';
			spanStatus = (managementStatus == "APPROVE" ? pointerEnable
					: pointerDisable)
					+ 'COMPLETED <span></span></span></div>';
			break;
		}

		/*
		 * if (status) { spanStatus = '<span class="label label-success"
		 * style="cursor: pointer;">Processed</span>'; } else { spanStatus = '<span
		 * class="label label-default" onclick="InventoryVoucher.setStatus(' +
		 * pid + ',' + !status + ')" style="cursor: pointer;">Pending</span>'; }
		 */
		return spanStatus;
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
					+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ approve
					+ ')" style="cursor: pointer;"><a>APPROVE</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
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
						+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
						+ pid
						+ ','
						+ hold
						+ ')"  style="cursor: pointer;"><a>HOLD</a></li>'
						+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
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
					+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ hold
					+ ')"  style="cursor: pointer;"><a>HOLD</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
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
					+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ hold
					+ ')"  style="cursor: pointer;"><a>HOLD</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
					+ pid
					+ ','
					+ approve
					+ ')" style="cursor: pointer;"><a>APPROVE</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesManagementStatus('
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = inventoryVoucherContextPath;
	}

	InventoryVoucher.setStatus = function(pid, tallyDownloadStatus) {

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : inventoryVoucherContextPath + "/changeStatus",
				method : 'GET',
				data : {
					pid : pid,
					tallyDownloadStatus : tallyDownloadStatus
				},
				success : function(data) {
					InventoryVoucher.filter();
					// onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	InventoryVoucher.setSalesManagementStatus = function(pid,
			salesManagementStatus) {

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : inventoryVoucherContextPath
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
					InventoryVoucher.filter();
					// onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	InventoryVoucher.showDatePicker = function() {
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

	function selectAllInventoryVoucher(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}
	
	
			

	function downloadXls() {
		// var inventoryVoucherHeaderPids = [];
		var inventoryVoucherHeaderPids = "";
		$("input[type='checkbox']:checked").each(function() {
			var inventoryVoucherPid = $(this).val();
			if (inventoryVoucherPid != "on") {
				inventoryVoucherHeaderPids += inventoryVoucherPid + ",";
			}
		});

		if (inventoryVoucherHeaderPids == "") {
			alert("please select inventory vouchers");
		} else {
			window.location.href = inventoryVoucherContextPath
					+ "/download-inventory-xls?inventoryVoucherHeaderPids="
					+ inventoryVoucherHeaderPids;
			console.log("sucess.......");
		}
	}

	InventoryVoucher.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showInventoryVoucher(pid);
				break;
			}
		}
		el.modal('show');
	}

	InventoryVoucher.closeModalPopup = function(el) {
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