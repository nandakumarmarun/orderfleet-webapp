// Create a ClientAppLogFilesReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ClientAppLogFilesReport) {
	this.ClientAppLogFilesReport = {};
}

(function() {
	'use strict';

	var clientAppLogFileContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		$('.selectpicker').selectpicker();
	});

	ClientAppLogFilesReport.filter = function() {
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

		$('#tBodyClentAppLog').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : clientAppLogFileContextPath + "/filter",
					type : 'GET',
					data : {
						companyPid : $("#dbCompanies").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(clientAppLogs) {
						$('#tBodyClentAppLog').html("");

						if (clientAppLogs.length > 0) {
							$('#tBodyClentAppLog')
									.html(
											"<tr><td colspan='4' align='center'>No Data Available</td></tr>");
							return;
						}
						console.log(clientAppLogs);

						$
								.each(
										clientAppLogs,
										function(index, clientAppLog) {

											$('#tBodyClentAppLog')
													.append(
															"<tr><td>"
																	+ clientAppLog.logName
																	+ "</td><td>"
																	+ convertDateFromServer(clientAppLog.logDate)
																	+ "</td><td>"
																	+ convertDateTimeFromServer(clientAppLog.createdDate)
																	+ "</td><td>"
																	+ convertDateTimeFromServer(clientAppLog.lastModifiedDate)
																	+ "</td><td><button type='button' class='btn btn-success' onclick='ClientAppLogFilesReport.download(\""
																	+ clientAppLog.pid
																	+ "\");'>Download</button><button type='button' class='btn btn-danger' onclick='ClientAppLogFilesReport.delete(\""
																	+ clientAppLog.pid
																	+ "\");'>Delete</button>"

																	+ "</td></tr>");

										});

					}
				});

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

	ClientAppLogFilesReport.setGeoTag = function(pid, accLat, accLon, accLoc,
			newLat, newLon, newLoc, obj) {

		$("#oldLatitude").text(
				(accLat == "null" || accLat == null ? "" : accLat));
		$("#oldLongitude").text(
				(accLon == "null" || accLon == null ? "" : accLon));
		$("#oldGeoLocation").text(
				(accLoc == "null" || accLoc == null ? "" : accLoc));

		$("#tblAccountProfileGeoLocation").html(
				"<tr><input type='hidden' name='geoLocation' id='geoLocation' value='"
						+ pid + "' >" + "<td>" + newLat + "</td><td>" + newLon
						+ "</td><td>" + newLoc + "</td></tr>");

		$("#viewGeoTagingModal").modal("show");
		$("#hdn_lbl").val(pid);

	}

	ClientAppLogFilesReport.saveNewGeoLocation = function() {
		var pid = $("#hdn_lbl").val();
		$.ajax({
			url : invoiceWiseReportContextPath + "/updateGeoTag/" + pid,
			method : 'GET',
			success : function(location) {
				$("#lbl_" + pid).text("");
				$("#lbl_" + pid).html(location);
				$("#viewGeoTagingModal").modal("hide");
			},
			error : function(xhr, error) {
				$("#viewGeoTagingModal").modal("hide");
				onError(xhr, error);
			}
		});
	}

	ClientAppLogFilesReport.getActivities = function(activityType) {
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

	ClientAppLogFilesReport.editDetails = function(pid, documentType,
			documentTypePid) {
		if (documentType == "INVENTORY_VOUCHER") {
			window.location = location.protocol + '//' + location.host
					+ "/web/inventory-voucher-transaction?etePid=" + pid
					+ "&invPid=" + documentTypePid;
		} else if (documentType == "DYNAMIC_DOCUMENT") {
			window.location = location.protocol + '//' + location.host
					+ "/web/other-voucher-transaction?etePid=" + pid
					+ "&dynPid=" + documentTypePid;
		} else if (documentType == "ACCOUNTING_VOUCHER") {
			window.location = location.protocol + '//' + location.host
					+ "/web/accounting-voucher-transaction?etePid=" + pid
					+ "&accPid=" + documentTypePid;
		}
	};

	var savePidForAccountChanging;
	ClientAppLogFilesReport.changeAccountProfile = function(pid) {
		$.ajax({
			url : invoiceWiseReportContextPath + "/getDynamicDocument/" + pid,
			method : 'GET',
			async : false,
			success : function(data) {

				$("#field_dynamicDocument").html(
						'<option value="-1">Select Dynamic Document</option>');
				if (data != null && data.length > 0) {
					$.each(data,
							function(index, dynamicDoc) {
								$("#field_dynamicDocument").append(
										'<option value='
												+ dynamicDoc.documentPid + '>'
												+ dynamicDoc.documentName
												+ '</option>');
								$("#field_dynamicDocument").val(
										dynamicDoc.documentPid);
							});
				}

				ClientAppLogFilesReport.getForms();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

		$(".error-msg").html("");
		savePidForAccountChanging = pid;
		$("#field_address").val("Address");
		$("#accountProfileModal").modal("show");
	};

	ClientAppLogFilesReport.searchTable = function(inputVal, table) {
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

	ClientAppLogFilesReport.newAccount = function() {
		$("#newAccount").css("display", "block");
		$("#oldAccounts").css("display", "none");
	};

	ClientAppLogFilesReport.oldAccount = function() {
		$("#oldAccounts").css("display", "block");
		$("#newAccount").css("display", "none");
	};

	ClientAppLogFilesReport.changeAccount = function() {

		$(".error-msg").html("");
		var accountProfilePid = "";

		accountProfilePid = $('input[name="account"]:checked').val();

		if (accountProfilePid == "") {
			$(".error-msg").html("Please select Account Profile");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : invoiceWiseReportContextPath + "/changeAccountProfile",
			type : "POST",
			data : {
				accountProfilePid : accountProfilePid,
				exeTaskPid : savePidForAccountChanging
			},
			success : function(status) {
				$("#accountProfileModal").modal("hide");
				ClientAppLogFilesReport.filter();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	};

	ClientAppLogFilesReport.createAndChangeAccount = function() {
		if ($("#field_name").val() == "") {
			alert("Please Fill Name");
			return;
		}
		if ($("#field_city").val() == "") {
			alert("Please Fill City");
			return;
		}
		if ($("#field_address").val() == "") {
			alert("Please Fill Address");
			return;
		}
		if ($("#field_accountType").val() == "-1") {
			alert("Please Select Account Type");
			return;
		}
		if ($("#field_territory").val() == null) {
			alert("Please Select Territory");
			return;
		}

		accountProfile.name = $('#field_name').val();
		accountProfile.alias = $('#field_alias').val();
		accountProfile.accountTypePid = $('#field_accountType').val();
		accountProfile.defaultPriceLevelPid = $('#field_priceLevel').val();
		accountProfile.city = $('#field_city').val();
		accountProfile.location = $('#field_location').val();
		accountProfile.pin = $('#field_pin').val();
		accountProfile.phone1 = $('#field_phone1').val();
		accountProfile.phone2 = $('#field_phone2').val();
		accountProfile.email1 = $('#field_email1').val();
		accountProfile.email2 = $('#field_email2').val();
		accountProfile.address = $('#field_address').val();
		accountProfile.description = $('#field_description').val();
		accountProfile.creditDays = $('#field_creditDays').val();
		accountProfile.creditLimit = $('#field_creditLimit').val();
		accountProfile.contactPerson = $('#field_contactPerson').val();
		accountProfile.defaultDiscountPercentage = $(
				'#field_defaultDiscountPercentage').val();
		accountProfile.closingBalance = $('#field_closingBalance').val();

		$.ajax({
			url : invoiceWiseReportContextPath
					+ "/createAndChangeAccountProfile/"
					+ savePidForAccountChanging + "/"
					+ $("#field_territory").val(),
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountProfile),
			success : function(status) {
				$("#accountProfileModal").modal("hide");
				$('#field_name').val("");
				$('#field_alias').val("");
				$('#field_city').val("City");
				$('#field_location').val("");
				$('#field_pin').val("");
				$('#field_phone1').val("");
				$('#field_phone2').val("");
				$('#field_email1').val("");
				$('#field_email2').val("");
				$('#field_address').val("Address");
				$('#field_description').val("");
				$('#field_creditDays').val("");
				$('#field_creditLimit').val("");
				$('#field_contactPerson').val("");
				$('#field_defaultDiscountPercentage').val("");
				$('#field_closingBalance').val("");
				ClientAppLogFilesReport.filter();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});

	};

	ClientAppLogFilesReport.getForms = function() {
		var dynamicDocumentPid = $("#field_dynamicDocument").val();
		if (dynamicDocumentPid == "-1") {
			$("#field_form").html('<option value="-1">Select Form</option>');
			return;
		}
		$.ajax({
			url : invoiceWiseReportContextPath + "/getForms/"
					+ dynamicDocumentPid,
			method : 'GET',
			success : function(data) {

				$("#field_form")
						.html('<option value="-1">Select Form</option>');
				if (data != null && data.length > 0) {
					$.each(data, function(index, formDoc) {
						$("#field_form").append(
								'<option value=' + formDoc.formPid + '>'
										+ formDoc.formName + '</option>');
						$("#field_form").val(formDoc.formPid);
					});
				}

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	ClientAppLogFilesReport.loadAccountFromForm = function() {
		$('#field_name').val("");
		$('#field_alias').val("");
		$('#field_city').val("City");
		$('#field_location').val("");
		$('#field_pin').val("");
		$('#field_phone1').val("");
		$('#field_phone2').val("");
		$('#field_email1').val("");
		$('#field_email2').val("");
		$('#field_address').val("Address");
		$('#field_description').val("");
		$('#field_creditDays').val("");
		$('#field_creditLimit').val("");
		$('#field_contactPerson').val("");
		$('#field_defaultDiscountPercentage').val("");
		$('#field_closingBalance').val("");
		var formPid = $("#field_form").val();
		var dynamicDocumentPid = $("#field_dynamicDocument").val();
		if (formPid == "-1") {
			alert("select form");
			return;
		}
		$.ajax({
			url : invoiceWiseReportContextPath + "/getAccountFromForm",
			method : 'GET',
			data : {
				formPid : formPid,
				dynamicDocumentPid : dynamicDocumentPid,
				exePid : savePidForAccountChanging,
			},
			success : function(data) {

				$.each(data, function(key, value) {
					if (key == "Name") {
						$('#field_name').val(value);
					} else if (key == "Alias") {
						$('#field_alias').val(value);
					} else if (key == "accountTypePid") {
						$('#field_accountType').val(value);
					} else if (key == "defaultPriceLevelPid") {
						if (value) {
							$('#field_priceLevel').val(value);
						}
					} else if (key == "City") {
						$('#field_city').val(value);
					} else if (key == "Location") {
						$('#field_location').val(value);
					} else if (key == "Pin") {
						$('#field_pin').val(value);
					} else if (key == "Phone 1") {
						$('#field_phone1').val(value);
					} else if (key == "Phone 2") {
						$('#field_phone2').val(value);
					} else if (key == "E-Mail 1") {
						$('#field_email1').val(value);
					} else if (key == "E-Mail 2") {
						$('#field_email2').val(value);
					} else if (key == "Address") {
						$('#field_address').val(value);
					} else if (key == "Description") {
						$('#field_description').val(value);
					} else if (key == "Credit Days	") {
						$('#field_creditDays').val(value);
					} else if (key == "Closing Balance") {
						$('#field_closingBalance').val(value);
					} else if (key == "Credit Limits") {
						$('#field_creditLimit').val(value);
					} else if (key == "Default Discount Percentage") {
						$('#field_defaultDiscountPercentage').val(value);
					} else if (key == "Contact Person") {
						$('#field_contactPerson').val(value);
					}

				});

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	};

	ClientAppLogFilesReport.getLocation = function(obj) {
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

	ClientAppLogFilesReport.getTowerLocation = function(obj) {
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

	ClientAppLogFilesReport.accept = function(pid, obj) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/verification/accept/" + pid,
			method : 'PUT',
			success : function(status) {
				if (status) {
					$(object).closest('tr').find('td').eq(6).prop('title', "");
					$(obj).closest('tr').find('td').eq(6).text("ACCEPTED");
					$(obj).closest('tr').find('td').eq(6).css({
						"color" : "green"
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	ClientAppLogFilesReport.reject = function() {
		var reasons = $("#field_reason").val();

		if (reasons == null) {
			$("#reasonAlert").css('display', 'block');
			return;
		}
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/verification/reject",
			method : 'PUT',
			data : {
				pid : invoiceWiseReportPid,
				reason : reasons,
			},
			success : function(status) {

				if (status) {
					$(object).closest('tr').find('td').eq(6).prop('title',
							reasons);
					$(object).closest('tr').find('td').eq(6).text("REJECTED");
					$(object).closest('tr').find('td').eq(6).css({
						"color" : "red"
					});
				}
				$("#myModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	ClientAppLogFilesReport.showModal = function(pid, obj) {
		object = obj;
		invoiceWiseReportPid = pid;
		$("#field_reason").val("");
		$("#myModal").modal("show");

	}

	ClientAppLogFilesReport.viewDetails = function(pid, documentType) {
		if (documentType == "INVENTORY_VOUCHER") {
			showInventoryVoucher(pid);
		} else if (documentType == "ACCOUNTING_VOUCHER") {
			showAccountingVoucher(pid);
		} else if (documentType == "DYNAMIC_DOCUMENT") {
			showDynamicDocument(pid);
		}
	}

	ClientAppLogFilesReport.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showDynamicDocumentImages(pid);
				break;
			}
		}
		el.modal('show');
	}

	function showDynamicDocumentImages(pid) {
		$('#divClientAppLogFilesReportImages')
				.html(
						'<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		$
				.ajax({
					url : location.protocol + '//' + location.host
							+ "/web/dynamic-documents/images/" + pid,
					type : 'GET',
					success : function(filledFormFiles) {
						$('#divClientAppLogFilesReportImages').html("");
						if (filledFormFiles.length == 0) {
							var table = '<table class="table  table-striped table-bordered"><tr><td style="font-weight: bold;">Image Not Found</td></tr></table>';
							$('#divClientAppLogFilesReportImages')
									.append(table);
						}
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
											$(
													'#divClientAppLogFilesReportImages')
													.append(table);

										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function showInventoryVoucher(pid) {
		$
				.ajax({
					url : contextPath + "/web/primary-sales-performance/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumberIc').text(
								data.documentNumberLocal);
						$('#lbl_userIc').text(
								(data.userName == null ? "" : data.userName));
						$('#lbl_documentIc').text(
								(data.documentName == null ? ""
										: data.documentName));
						$('#lbl_documentDateIc').text(
								formatDate(data.createdDate,
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
						$('#lbl_documentDiscountAmountIc').text(
								(data.docDiscountAmount == null ? ""
										: data.docDiscountAmount));
						$('#lbl_documentDiscountPercentageIc').text(
								(data.docDiscountPercentage == null ? ""
										: data.docDiscountPercentage));

						$('#tblVoucherDetailsIc').html("");
						$
								.each(
										data.inventoryVoucherDetails,
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
																			: voucherDetail.rowTotal)
																	+ "</td></tr>");
											$
													.each(
															voucherDetail.inventoryVoucherBatchDetails,
															function(index1,
																	inventoryVoucherBatch) {
																$(
																		'#tblVoucherDetailsIc')
																		.append(
																				"<tr style='background: rgba(225, 225, 225, 0.66);' data-parent='"
																						+ index
																						+ "' >"
																						+ "<td colspan='2'>Batch Number : "
																						+ (inventoryVoucherBatch.batchNumber == null ? ""
																								: inventoryVoucherBatch.batchNumber)
																						+ "</td><td colspan='3'>Stock Location : "
																						+ (inventoryVoucherBatch.stockLocationName == null ? ""
																								: inventoryVoucherBatch.stockLocationName)
																						+ "</td><td colspan='2'>Quantity : "
																						+ (inventoryVoucherBatch.quantity == null ? ""
																								: inventoryVoucherBatch.quantity)
																						+ "</td></tr>");
															});
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
					url : contextPath + "/web/accounting-vouchers/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumberAc').text(
								(data.documentNumberLocal == null ? ""
										: data.documentNumberLocal));
						$('#lbl_userAc').text(
								(data.userName == null ? "" : data.userName));
						$('#lbl_accountAc').text(
								(data.accountProfileName == null ? ""
										: data.accountProfileName));
						$('#lbl_documentAc').text(
								(data.documentName == null ? ""
										: data.documentName));
						$('#lbl_createdDateAc').text(
								formatDate(data.createdDate,
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
										data.accountingVoucherDetails,
										function(index, voucherDetail) {

											$('#tblVoucherDetailsAc')
													.append(
															"<tr data-id='"
																	+ index
																	+ "'><td>"
																	+ (voucherDetail.amounte == null ? ""
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
																	+ (voucherDetail.byAccountNamee == null ? ""
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

											$
													.each(
															voucherDetail.accountingVoucherAllocations,
															function(index1,
																	voucherAllocation) {
																let remarks = '';
																if (voucherAllocation.remarks != null) {
																	remarks = (voucherAllocation.remarks)
																			.replace(
																					"~",
																					"</td><td colspan='2'>");
																}
																$(
																		'#tblVoucherDetailsAc')
																		.append(
																				"<tr style='background: rgba(225, 225, 225, 0.66);' data-parent='"
																						+ index
																						+ "' >"
																						+ "<td colspan='2'>Ref No : "
																						+ (voucherAllocation.referenceNumber == null ? ""
																								: voucherAllocation.referenceNumber)
																						+ "<td colspan='2'>Payment Mode : "
																						+ (voucherAllocation.mode == null ? ""
																								: voucherAllocation.mode)
																						+ "</td>"
																						+ "<td colspan='2'>Amount : "
																						+ (voucherAllocation.amount == null ? ""
																								: voucherAllocation.amount)
																						+ "</td>"
																						+ "<td colspan='2'>"
																						+ remarks
																						+ "</td>"
																						+ "</tr>");
															});
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
					url : contextPath + "/web/dynamic-documents/" + pid,
					method : 'GET',
					success : function(data) {
						$('#lbl_documentNumber').text(
								(data.documentNumberLocal == null ? ""
										: data.documentNumberLocal));
						$('#lbl_user').text(
								(data.userName == null ? "" : data.userName));
						$('#lbl_activity').text(
								(data.activityName == null ? ""
										: data.activityName));
						$('#lbl_account').text(
								(data.accountName == null ? ""
										: data.accountName));
						$('#lbl_accountph').text(
								(data.accountPhone == null ? ""
										: data.accountPhone));
						$('#lbl_document').text(
								(data.documentName == null ? ""
										: data.documentName));
						$('#lbl_documentDate').text(
								formatDate(data.createdDate,
										'MMM DD YYYY, h:mm:ss a'));
						$('#divDynamicDocumentDetails').html("");
						$
								.each(
										data.filledForms,
										function(index, filledForm) {
											var table = '<table class="table  table-striped table-bordered"><tr><td colspan="2" style="font-weight: bold;">'
													+ filledForm.formName
													+ '</td></tr>';
											$
													.each(
															filledForm.filledFormDetails,
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
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
		$('#viewModalDynamicDocument').modal('show');
	}

	ClientAppLogFilesReport.showDatePicker = function() {
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

})();