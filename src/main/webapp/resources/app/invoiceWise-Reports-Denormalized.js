// Create a InvoiceWiseReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InvoiceWiseReport) {
	this.InvoiceWiseReport = {};
}

(function() {
	'use strict';

	var invoiceWiseReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	var dashboardEmployees = [];
	var otherEmployees = [];
	var allEmployeesMap = new Object();
	// Object for reject acccount profile
	var object = null;
	var invoiceWiseReportPid = null;

	var accountProfile = {
		pid : null,
		name : null,
		alias : null,
		accountTypePid : null,
		defaultPriceLevelPid : null,
		city : null,
		location : null,
		pin : null,
		phone1 : null,
		phone2 : null,
		email1 : null,
		email2 : null,
		address : null,
		description : null,
		creditDays : null,
		creditLimit : null,
		contactPerson : null,
		defaultDiscountPercentage : null,
		closingBalance : 0,
		towerLocation : null
	};

	$(document).ready(function() {
		
		let filterBy = getParameterByName('filterBy');
		if (filterBy) {
			$('#dbDateSearch').val(filterBy);
			InvoiceWiseReport.filter();
		}

		$('.selectpicker').selectpicker();
	});

	
	InvoiceWiseReport.testdownloadXls = function() {
		
	 var	employeePid = $('#dbEmployee').val();
     var	documentPid = $("#dbDocument").val();
     var	activityPid = $("#dbActivity").val();
     var accountPid = $("#dbAccount").val();
     var	filterBy = $("#dbDateSearch").val();
     var	fromDate = $("#txtFromDate").val();
     var	toDate = $("#txtToDate").val();
     var	inclSubordinate = $('#inclSubOrdinates').is(":checked");

      window.location.href =  invoiceWiseReportContextPath+"/downloadxls?&employeePid="+employeePid+'&documentPid='+documentPid+'&activityPid='+activityPid+
                                             '&accountPid='+accountPid+'&filterBy='+filterBy+'&fromDate='+fromDate+'&toDate='+toDate+'&inclSubordinate='+inclSubordinate;

console.log("Success.....");
	}
	InvoiceWiseReport.filter = function() {
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

		$('#tBodyInvoiceWiseReport').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : invoiceWiseReportContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						documentPid : $("#dbDocument").val(),
						activityPid : $("#dbActivity").val(),
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")
					},
					success : function(invoiceWiseReports) {
						$('#tBodyInvoiceWiseReport').html("");

						$("#lblActivities").text("0");
						$("#lblTransactionAmount").text("0");
						$("#lblTransactionVolume").text("0");
						$("#lblTotalPayments").text("0");
						if (invoiceWiseReports.length == 0) {
							$('#tBodyInvoiceWiseReport')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var activities = 0;
						var transactionAmount = 0;
						var transactionVolume = 0;
						var totalPayments = 0;
						$
								.each(
										invoiceWiseReports,
										function(index, invoiceWiseReport) {
											activities += 1;
											var locationName = invoiceWiseReport.location;

											var towerLocationName = invoiceWiseReport.towerLocation;

											var mockLocation = "";

											var withCustomer = "Remote Visit"

											if (invoiceWiseReport.withCustomer) {
												withCustomer = "Counter Visit"
											}

											if (invoiceWiseReport.mockLocationStatus) {
												mockLocation = "Enabled"
											}

											if (invoiceWiseReport.location == "No Location"
													&& invoiceWiseReport.latitude != 0) {
												locationName = "<span class='btn btn-success'  id='"
														+ invoiceWiseReport.executionPid
														+ "' onClick='InvoiceWiseReport.getLocation(this)' >get location</span>";
											}

											if (invoiceWiseReport.towerLocation == "Not Found"
													&& invoiceWiseReport.mcc != 0
													|| invoiceWiseReport.towerLocation == null
													&& invoiceWiseReport.mcc != 0) {
												towerLocationName = "<span class='btn btn-success'  id='"
														+ invoiceWiseReport.executionPid
														+ "' onClick='InvoiceWiseReport.getTowerLocation(this)' >get location</span>";
											}


											var locationIcon = locationIcons(invoiceWiseReport);

											let end = moment(invoiceWiseReport.createdDate);
											let sendTime = moment(invoiceWiseReport.sendDate);
											let duration = moment.duration(end
													.diff(sendTime));
											let rowColor = "";
											if (duration.asMinutes() > 10) {
												rowColor = "background-color:yellow";
											}
											$('#tBodyInvoiceWiseReport')
													.append(
															"<tr style='"
																	+ rowColor
																	+ "' data-id='"
																	+ invoiceWiseReport.pid
																	+ "' data-parent=\"\"><td class='tableexport-string target'>"
																	+ invoiceWiseReport.employeeName
																	+ "</td><td>"
																	+ invoiceWiseReport.accountProfileName
																	+ "</td><td>"
																	+ invoiceWiseReport.activityName
																	+ "</td><td class='tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.punchIn,
																			'MMM DD YYYY, h:mm:ss a')
																	+ "</td><td class='tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.clientDate,
																			'MMM DD YYYY, h:mm:ss a')
																	+ "</td><td class='tableexport-string target'>"
																	+ invoiceWiseReport.timeSpend
																	+ "</td><td class='tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.createdDate,
																			'MMM DD YYYY, h:mm:ss a')
																	+ "</td><td><label>GPS Location : </label> <i>"
																	+ locationName
																	+ locationIcon
																	+ "</i><br><label>Customer Location : </label><i id='lbl_"
																	+ invoiceWiseReport.pid
																	+ "'> "
																	+ (invoiceWiseReport.accountProfileLocation == null ? ""
																			: invoiceWiseReport.accountProfileLocation)
																	+ "</i></td>"
																	+ "<td>"
																	+ mockLocation
																	+ "</td>"
																	+ "<td>"
																	+ towerLocationName
																	+ "</td><td>"
																	+ withCustomer
																	+ "</td><td>"
																	+ (invoiceWiseReport.salesOrderTotalAmount == null ? "" :invoiceWiseReport.salesOrderTotalAmount)
																	+ "</td>"
																	+ "<td>"
																	+ (invoiceWiseReport.receiptAmount == null ? "" :invoiceWiseReport.receiptAmount)
																	+ "</td><td>"
																	+ (invoiceWiseReport.vehicleNumber == null ? ""
																			: invoiceWiseReport.vehicleNumber)
																	+ "</td><td>"
																	+ (invoiceWiseReport.remarks == null ? ""
																			: invoiceWiseReport.remarks)
																	+ "</td></tr>");



																	$(
																			'#tBodyInvoiceWiseReport')
																			.append(
																					"<tr class='tableexport-ignore' style='background: rgba(225, 225, 225, 0.66);' data-id='"
																							+ invoiceWiseReport.executionPid
																							+ "1' data-parent='"
																							+ invoiceWiseReport.pid
																							+ "'><td>&nbsp;</td><td colspan='3'>"
																							+ invoiceWiseReport.documentName

																							+ "</td><td><button type='button' class='btn btn-white' onclick='InvoiceWiseReport.viewDetails(\""
																							+ invoiceWiseReport.executionPid
																							+ "\",\""
																							+ invoiceWiseReport.documentType
																							+ "\");'>View Details</button>"

																							+ "</td></tr>");


										});

						$("#lblActivities").text(activities);
						$("#lblTransactionAmount").text(
								transactionAmount.toFixed(2));
						$("#lblTransactionVolume").text(transactionVolume);
						$("#lblTotalPayments").text(totalPayments);

						$('.collaptable')
								.aCollapTable(
										{
											startCollapsed : true,
											addColumn : false,
											plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
											minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
										});
					}
				});
	}



	InvoiceWiseReport.getActivities = function(activityType) {
		$("#dbActivity").children().remove();
		if (activityType == 'all') {
			$("#dbActivity").append('<option value="no">All Activity</option>');
		} else if (activityType == "planed") {
			$("#dbActivity").append(
					'<option value="planed">All Planned Activity</option>');
		} else if (activityType == "unPlaned") {
			$("#dbActivity").append(
					'<option value="unPlaned">All UnPlanned Activity</option>');
		}
		$.ajax({
			url : invoiceWiseReportContextPath + "/getActivities/"
					+ activityType,
			method : 'GET',
			success : function(activities) {
				$.each(activities, function(index, activity) {

					$("#dbActivity").append(
							'<option value="' + activity.pid + '">'
									+ activity.name + '</option>');
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}





	InvoiceWiseReport.searchTable = function(inputVal, table) {
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}


	InvoiceWiseReport.getLocation = function(obj) {
		var pid = $(obj).attr("id");
		$(obj).html("loading...");
		$.ajax({
			url : invoiceWiseReportContextPath + "/updateLocation/" + pid,
			method : 'GET',
			success : function(data) {
				$(obj).html(data.location);
				$(obj).removeClass("btn-success");
				$(obj).removeClass("btn");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	InvoiceWiseReport.getTowerLocation = function(obj) {
		var pid = $(obj).attr("id");
		$(obj).html("loading...");
		$.ajax({
			url : invoiceWiseReportContextPath + "/updateTowerLocation/" + pid,
			method : 'GET',
			success : function(data) {
				$(obj).html(data.towerLocation);
				$(obj).removeClass("btn-success");
				$(obj).removeClass("btn");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function locationIcons(userData) {

		var images = "";
		if (userData.locationType != null) {

			if (userData.locationType == "GpsLocation") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/location.png" width="20px">';
			} else if (userData.locationType == "TowerLocation") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/tower.png" width="20px">';
			} else if (userData.locationType == "FlightMode") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/flight.png" width="20px">';
			}
			// check gsp is Off
			if (userData.isGpsOff && userData.locationType != "GpsLocation") {
				images += ' <img src="'
						+ contextPath
						+ '/resources/assets/images/map/gps-off.png" width="20px">';
			}
			// check Mobile Data is Off
			if (userData.isMobileDataOff) {
				images += ' <img src="'
						+ contextPath
						+ '/resources/assets/images/map/mobile-data-off.png" width="20px">';
			}

		}
		return images;
	}




	InvoiceWiseReport.showModal = function(pid, obj) {
		object = obj;
		invoiceWiseReportPid = pid;
		$("#field_reason").val("");
		$("#myModal").modal("show");

	}

	InvoiceWiseReport.viewDetails = function(pid, documentType) {
		if (documentType == "INVENTORY_VOUCHER") {
			showInventoryVoucher(pid);
		} else if (documentType == "ACCOUNTING_VOUCHER") {
			showAccountingVoucher(pid);
		} else if (documentType == "DYNAMIC_DOCUMENT") {
			showDynamicDocument(pid);
		}
	}

	InvoiceWiseReport.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showDynamicDocumentImages(pid);
				break;
			}
		}
		el.modal('show');
	}

	function showInventoryVoucher(pid) {
	console.log("Pid :"+pid)
		$
				.ajax({
					url : invoiceWiseReportContextPath+ "/getInventoryDetails/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumberIc').text(
								data.documentNumberServer);
						$('#lbl_userIc').text(
								(data.userName == null ? "" : data.userName));
						$('#lbl_documentIc').text(
								(data.documentName == null ? ""
										: data.documentName));
						$('#lbl_documentDateIc').text(
								formatDate(data.documentDate,
										'MMM DD YYYY, h:mm:ss a'));
						$('#lbl_receiverIc').text(
								(data.receiverAccountName == null ? ""
										: data.receiverAccountName));
						$('#lbl_supplierIc').text(
								(data.supplierAccountName == null ? ""
										: data.supplierAccountName));
						$('#lbl_documentTotalIc').text(
								(data.documentTotal == null ? ""
										: data.documentTotal));
						$('#lbl_documentVolumIc').text(
								(data.documentVolume == null ? ""
										: data.documentVolume));
						$('#lbl_documentDiscountAmountIc').text(
								(data.docDiscountAmount == null ? ""
										: data.docDiscountAmount));
						$('#lbl_documentDiscountPercentageIc').text(
								(data.docDiscountPercentage == null ? ""
										: data.docDiscountPercentage));

						$('#tblVoucherDetailsIc').html("");
						$
								.each(
										data.inventoryData,
										function(index, voucherDetail) {
											$('#tblVoucherDetailsIc')
													.append(
															"<tr data-id='"
																	+ index
																	+ "'><td>"
																	+ (voucherDetail.productName == null ? ""
																			: voucherDetail.productName)
																	+ "</td><td>"
																	+ (voucherDetail.quantity == null ? ""
																			: voucherDetail.quantity)
																	+ "</td><td>"
																	+ (voucherDetail.freeQuantity == null ? ""
																			: voucherDetail.freeQuantity)
																	+ "</td><td>"
																	+ (voucherDetail.sellingRate == null ? ""
																			: voucherDetail.sellingRate)
																	+ "</td><td>"
																	+ (voucherDetail.taxPercentage == null ? ""
																			: voucherDetail.taxPercentage)
																	+ "</td><td>"
																	+ (voucherDetail.discountAmount == null ? ""
																			: voucherDetail.discountAmount)
																	+ "</td><td>"
																	+ (voucherDetail.discountPercentage == null ? ""
																			: voucherDetail.discountPercentage)
																	+ "</td><td>"
																	+ (voucherDetail.remarks == null ? ""
																			: voucherDetail.remarks)
																	+ "</td><td>"
																	+ (voucherDetail.rowTotal == null ? ""
																			: voucherDetail.rowTotal.toFixed(2))
																	+ "</td></tr>");

										});
						$('#divVoucherDetailsInventory .collaptable')
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
		$('#viewModalInventoryVoucher').modal('show');
	}

	function showAccountingVoucher(pid) {
		$
				.ajax({
					url : invoiceWiseReportContextPath + "/accounting-details/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumberAc').text(
								(data.documentNumberServer == null ? ""
										: data.documentNumberServer));
						$('#lbl_userAc').text(
								(data.userName == null ? "" : data.userName));
						$('#lbl_accountAc').text(
								(data.accountProfileName == null ? ""
										: data.accountProfileName));
						$('#lbl_documentAc').text(
								(data.documentName == null ? ""
										: data.documentName));
						$('#lbl_createdDateAc').text(
								formatDate(data.documentDate,
										'MMM DD YYYY, h:mm:ss a'));
						$('#lbl_totalAmountAc').text(
								(data.totalAmount == null ? ""
										: data.totalAmount));
						$('#lbl_outstandingAmountAc').text(
								(data.outstandingAmount == null ? ""
										: data.outstandingAmount));
						$('#lbl_remarksAc').text(
								(data.remarks == null ? "" : data.remarks));

						$('#tblVoucherDetailsAc').html("");

						$
								.each(
										data.accountingData,
										function(index, voucherDetail) {

											$('#tblVoucherDetailsAc')
													.append(
															"<tr data-id='"
																	+ index
																	+ "'><td>"
																	+ (voucherDetail.amount == null ? ""
																			: voucherDetail.amount)
																	+ "</td><td>"
																	+ (voucherDetail.mode == null ? ""
																			: voucherDetail.mode)
																	+ "</td><td>"
																	+ (voucherDetail.instrumentNumber == null ? ""
																			: voucherDetail.instrumentNumber)
																	+ "</td><td>"
																	+ formatDate(
																			voucherDetail.instrumentDate,
																			'MMM DD YYYY')
																	+ "</td><td>"
																	+ (voucherDetail.bankName == null ? ""
																			: voucherDetail.bankName)
																	+ "</td><td>"
																	+ (voucherDetail.byAccountName == null ? ""
																			: voucherDetail.byAccountName)
																	+ "</td><td>"
																	+ (voucherDetail.toAccountName == null ? ""
																			: voucherDetail.toAccountName)
																	+ "</td><td>"
																	+ (voucherDetail.incomeExpenseHeadName == null ? ""
																			: voucherDetail.incomeExpenseHeadName)
																	+ "</td><td>"
																	+ (voucherDetail.voucherNumber == null ? ""
																			: voucherDetail.voucherNumber)
																	+ "</td><td>"
																	+ formatDate(
																			voucherDetail.voucherDate,
																			'MMM DD YYYY')
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
										});
						$('#divVoucherDetailsAc .collaptable')
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
		$('#viewModalAccountingVoucher').modal('show');
	}

	function showDynamicDocument(pid) {
		$
				.ajax({
					url : invoiceWiseReportContextPath + "/dynamic-documents-details/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumber').text(
								(data.documentNumberServer == null ? ""
										: data.documentNumberServer));
						$('#lbl_user').text(
								(data.userName == null ? "" : data.userName));
						$('#lbl_activity').text(
								(data.activityname == null ? ""
										: data.activityname));
						$('#lbl_account').text(
								(data.accountName == null ? ""
										: data.accountName));
						$('#lbl_accountph').text(
								(data.accountPhNo == null ? ""
										: data.accountPhNo));
						$('#lbl_document').text(
								(data.documentName == null ? ""
										: data.documentName));
						$('#lbl_documentDate').text(
								formatDate(data.documentDate,
										'MMM DD YYYY, h:mm:ss a'));
						$('#divDynamicDocumentDetails').html("");

											var table = '<table class="table  table-striped table-bordered"><tr><td colspan="2" style="font-weight: bold;">'
													+ data.formName
													+ '</td></tr>';
											$
													.each(
															data.dynamic,
															function(index,
																	formDetail) {
																table += "<tr><td>"
																		+ formDetail.formElementName
																		+ "</td><td>"
																		+ (formDetail.value == null ? ""
																				: formDetail.value)
																		+ "</td>";
															});
											table += '</table>';
											$('#divDynamicDocumentDetails')
													.append(table);

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
		$('#viewModalDynamicDocument').modal('show');
	}

	InvoiceWiseReport.showDatePicker = function() {
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

	InvoiceWiseReport.closeModalPopup = function(el) {
		el.modal('hide');
	}

})();