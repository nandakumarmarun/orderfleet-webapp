// Create a InventoryVoucher object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InventoryVoucher) {
	this.InventoryVoucher = {};
}

(function() {
	'use strict';

	var inventoryVoucherContextPath = location.protocol + '//' + location.host
			+ location.pathname;

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

	});

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
																	+ (voucherDetail.editOrder ? "<div class='input-group'><input type='number' id='updatedQty-"
																			+ voucherDetail.detailId
																			+ "' class='form-control' value='"
																			+ (voucherDetail.updatedStatus ? voucherDetail.updatedQty
																					: voucherDetail.quantity)
																			+ "'><div class='input-group-btn'><input type='button' class='btn btn-info'  onClick='InventoryVoucher.updateInventory(\""
																			+ pid
																			+ "\",\""
																			+ voucherDetail.detailId
																			+ "\");'  value='update'></div></div>"
																			: voucherDetail.quantity)
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
																	+ (voucherDetail.updatedStatus ? (voucherDetail.updatedRowTotal)
																			.toFixed(2)
																			: voucherDetail.rowTotal)
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

	InventoryVoucher.updatePaymentReceived = function(ivhPid) {
		$.ajax({
			url : inventoryVoucherContextPath + "/updatePaymentReceived",
			data : {
				ivhPid : ivhPid,
				paymentReceived : $('#pmtReceieved-' + ivhPid).val()
			},
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			method : 'GET',
			success : function(data) {
				$("#loader").modal('hide');
				InventoryVoucher.filter();
			}

		});
	}

	InventoryVoucher.updateBookingId = function(ivhPid) {
		$.ajax({
			url : inventoryVoucherContextPath + "/updateBookingId",
			data : {
				ivhPid : ivhPid,
				bookingId : $('#bookingId-' + ivhPid).val()
			},
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			method : 'GET',
			success : function(data) {
				$("#loader").modal('hide');
				InventoryVoucher.filter();
			}

		});
	}

	InventoryVoucher.updateDeliveryDate = function(ivhPid) {
		$.ajax({
			url : inventoryVoucherContextPath + "/updateDeliveryDate",
			data : {
				ivhPid : ivhPid,
				deliveryDate : $('#deliveryDate-' + ivhPid).val()
			},
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			method : 'GET',
			success : function(data) {
				$("#loader").modal('hide');
				InventoryVoucher.filter();
			}

		});
	}

	InventoryVoucher.updateAll = function(ivhPid) {
		$.ajax({
			url : inventoryVoucherContextPath + "/updateAll",
			data : {
				ivhPid : ivhPid,
				bookingId : $('#bookingId-' + ivhPid).val(),
				paymentReceived : $('#pmtReceieved-' + ivhPid).val(),
				deliveryDate : $('#deliveryDate-' + ivhPid).val()
			},
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			method : 'GET',
			success : function(data) {
				$("#loader").modal('hide');
				InventoryVoucher.filter();
			}

		});
	}

	InventoryVoucher.filter = function() {

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
						processFlowStatus : "READYATPS_NOTDELIVERED",
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

											var docTotal = (inventoryVoucher.updatedStatus ? inventoryVoucher.documentTotalUpdated
													: inventoryVoucher.documentTotal);
											var paymentReceived = inventoryVoucher.paymentReceived;

											var paymentPercentage = 0;

											if (paymentReceived != 0) {
												paymentPercentage = (paymentReceived / docTotal) * 100;
											}
											var bgColor = "";

											if (paymentPercentage >= 30) {
												bgColor = "#9DF781";
											}

											if (paymentPercentage >= 100) {
												paymentPercentage = 100;
											}

											if (inventoryVoucher.deliveryDate != "") {

												var noOfdays = inventoryVoucher.deliveryDateDifference;

												console.log("No of Days= "
														+ noOfdays);

												if (noOfdays >= 30
														&& noOfdays <= 45) {
													bgColor = "#7DFF33";
												}

												if (noOfdays >= 15
														&& noOfdays < 30) {
													bgColor = "#FFFC33";
												}

												if (noOfdays > 0
														&& noOfdays < 15) {
													bgColor = "#FFB833";
												}

												if (noOfdays <= 0) {
													bgColor = "#FF3933 ";
												}

											}

											$(
													"#deliveryDate-"
															+ inventoryVoucher.pid)
													.datepicker({
														dateFormat : "dd-mm-yy"
													});
											
											var content = "No Image";

											if (inventoryVoucher.imageButtonVisible) {
												content = "<button type='button' class='btn btn-blue' onclick='InventoryVoucher.showModalPopup($(\"#imagesModal\"),\""
														+ inventoryVoucher.dynamicDocumentPid
														+ "\",1);'>View Images</button>";
											} else {
												content = "No Dynamic Document";
											}

											$('#tBodyInventoryVoucher')
													.append(
															"<tr style='background-color:"
																	+ bgColor
																	+ "'><td><input type='checkbox' class='check-one' value='"
																	+ inventoryVoucher.pid
																	+ "' />"
																	+ "</td><td>"
																	+ spanProcessFlowStatus(
																			inventoryVoucher.pid,
																			inventoryVoucher.processFlowStatus)
																	+ "<br><input type='button' class='btn btn-info'  onClick='InventoryVoucher.updateAll(\""
																	+ inventoryVoucher.pid
																	+ "\");'  value='Update'></td><td><input type='text' id='bookingId-"
																	+ inventoryVoucher.pid
																	+ "' value='"
																	+ inventoryVoucher.bookingId
																	+ "'/>"
																	+ "</td><td>"
																	+ inventoryVoucher.receiverAccountName
																	+ "</td><td><input type='date' id='deliveryDate-"
																	+ inventoryVoucher.pid
																	+ "' value='"
																	+ inventoryVoucher.deliveryDate
																	+ "'/>"
																	+ "</td><td><input type='number' id='pmtReceieved-"
																	+ inventoryVoucher.pid
																	+ "' value='"
																	+ inventoryVoucher.paymentReceived
																	+ "'/>"
																	+ "</td><td>"
																	+ paymentPercentage
																			.toFixed(2)
																	+ " %"
																	+ "</td><td>"
																	+ (inventoryVoucher.updatedStatus ? inventoryVoucher.documentTotalUpdated
																			.toFixed(2)
																			: inventoryVoucher.documentTotal
																					.toFixed(2))
																	+ "</td><td>"
																	+ content
																	+ "</td><td>"
																	+ inventoryVoucher.supplierAccountName
																	+ "</td><td>"
																	+ inventoryVoucher.totalVolume
																			.toFixed(2)
																	+ "</td><td>"
																	+ inventoryVoucher.employeeName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(inventoryVoucher.createdDate)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='InventoryVoucher.showModalPopup($(\"#viewModal\"),\""
																	+ inventoryVoucher.pid
																	+ "\",0);'>View Details</button>"
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
	
	function showDynamicDocumentImages(pid) {
		$
				.ajax({
					url : inventoryVoucherContextPath + "/images/" + pid,
					method : 'GET',
					success : function(filledFormFiles) {
						
						console.log(filledFormFiles.length);

						$('#divDynamicDocumentImages').html("");

						if (filledFormFiles.length > 0) {
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
												$('#divDynamicDocumentImages')
														.append(table);
											});
						} else {
							$('#divDynamicDocumentImages').html(
									"<td><label class='error-msg' style='color: red;'>No Images Found For Dynamic Document</label></td>");
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
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

	function spanProcessFlowStatus(inventoryVoucherPid, status) {

		var defaultStatus = "'" + 'DEFAULT' + "'";
		var notDelivered = "'" + 'NOT_DELIVERED' + "'";
		var delivered = "'" + 'DELIVERED' + "'";
		var readyToDispatchAtPS = "'" + 'READY_TO_DISPATCH_AT_PS' + "'";
		var spanProcessFlowStatus = "";
		var pid = "'" + inventoryVoucherPid + "'";
		switch (status) {
		case 'DEFAULT':
			spanProcessFlowStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'DEFAULT<span class="caret"></span></span>'
					+ '<ul class="dropdown-menu">'
					+ '<li onclick="InventoryVoucher.setProcessFlowStatus('
					+ pid
					+ ','
					+ delivered
					+ ')" style="cursor: pointer;"><a>DELIVERED</a></li>'
					+ '<li onclick="InventoryVoucher.setProcessFlowStatus('
					+ pid
					+ ','
					+ notDelivered
					+ ')" style="cursor: pointer;"><a>NOT_DELIVERED</a></li>'
					+ '</ul></div>';
			break;
		case 'READY_TO_DISPATCH_AT_PS':
			spanProcessFlowStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'DEFAULT<span class="caret"></span></span>'
					+ '<ul class="dropdown-menu">'
					+ '<li onclick="InventoryVoucher.setProcessFlowStatus('
					+ pid
					+ ','
					+ delivered
					+ ')" style="cursor: pointer;"><a>DELIVERED</a></li>'
					+ '<li onclick="InventoryVoucher.setProcessFlowStatus('
					+ pid
					+ ','
					+ notDelivered
					+ ')" style="cursor: pointer;"><a>NOT_DELIVERED</a></li>'
					+ '</ul></div>';
			break;
		case 'NOT_DELIVERED':
			spanProcessFlowStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'NOT_DELIVERED<span class="caret"></span></span>'
					+ '<ul class="dropdown-menu">'
					+ '<li onclick="InventoryVoucher.setProcessFlowStatus('
					+ pid
					+ ','
					+ delivered
					+ ')" style="cursor: pointer;"><a>DELIVERED</a></li>'
					+ '</ul></div>';
			break;
		}

		/*
		 * if (status) { spanStatus = '<span class="label label-success"
		 * style="cursor: pointer;">Processed</span>'; } else { spanStatus = '<span
		 * class="label label-default" onclick="InventoryVoucher.setStatus(' +
		 * pid + ',' + !status + ')" style="cursor: pointer;">Pending</span>'; }
		 */
		return spanProcessFlowStatus;
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = inventoryVoucherContextPath;
	}

	InventoryVoucher.setProcessFlowStatus = function(pid, processFlowStatus) {

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : inventoryVoucherContextPath + "/changeProcessFlowStatus",
				method : 'GET',
				data : {
					pid : pid,
					processFlowStatus : processFlowStatus
				},
				beforeSend : function() {
					// Show image container
					$("#loader").modal('show');

				},
				success : function(data) {
					console.log(data);
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

	InventoryVoucher.showDatePicker = function() {
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
			case 1:
				showDynamicDocumentImages(pid);
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