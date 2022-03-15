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


	
	InvoiceWiseReport.downloadXls = function() {
	   
		        	var	employeePid = $('#dbEmployee').val();
					var	documentPid = $("#dbDocument").val();
					var	activityPid = $("#dbActivity").val();
					var	accountPid = $("#dbAccount").val();
					var filterBy = $("#dbDateSearch").val();
					var	fromDate = $("#txtFromDate").val();
					var	toDate = $("#txtToDate").val();
					var	inclSubordinate = $('#inclSubOrdinates').is(":checked");
					
	     window.location.href = invoiceWiseReportContextPath+"/downloadxls?&employeePid="+employeePid+'&documentPid='+documentPid+'&activityPid='+activityPid+
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

											var mockLocatation = "";

											var withCustomer = "Remote Visit"

											if (invoiceWiseReport.withCustomer) {
												withCustomer = "Counter Visit"
											}

											if (invoiceWiseReport.mockLocationStatus) {
												mockLocatation = "Enabled"
											}

											if (invoiceWiseReport.location == "No Location"
													&& invoiceWiseReport.latitude != 0) {
												locationName = "<span class='btn btn-success'  id='"
														+ invoiceWiseReport.pid
														+ "' onClick='InvoiceWiseReport.getLocation(this)' >get location</span>";
											}

											if (invoiceWiseReport.towerLocation == "Not Found"
													&& invoiceWiseReport.mcc != 0
													|| invoiceWiseReport.towerLocation == null
													&& invoiceWiseReport.mcc != 0) {
												towerLocationName = "<span class='btn btn-success'  id='"
														+ invoiceWiseReport.pid
														+ "' onClick='InvoiceWiseReport.getTowerLocation(this)' >get location</span>";
											}

											/*
											 * if(invoiceWiseReport.locationType=="GpsLocation" &&
											 * invoiceWiseReport.latitude==0 ||
											 * invoiceWiseReport.locationType=="TowerLocation" &&
											 * invoiceWiseReport.latitude==0 ||
											 * invoiceWiseReport.location=="Unable
											 * to find location"){
											 * locationName="<span class='btn
											 * btn-success'
											 * id='"+invoiceWiseReport.pid+"'
											 * onClick='InvoiceWiseReport.getLocation(this)'
											 * >get location</span>"; }
											 */

											/*
											 * if(invoiceWiseReport.locationType=="GpsLocation" &&
											 * invoiceWiseReport.latitude==0 ||
											 * invoiceWiseReport.locationType=="TowerLocation" &&
											 * invoiceWiseReport.latitude==0 ||
											 * invoiceWiseReport.location=="Unable
											 * to find location"){
											 * locationName="<span class='btn
											 * btn-success'
											 * id='"+invoiceWiseReport.pid+"'
											 * onClick='InvoiceWiseReport.getLocation(this)'
											 * >get location</span>"; }
											 */
											// if(invoiceWiseReport.locationType=="GpsLocation"
											// && invoiceWiseReport.latitude==0
											// ||
											// invoiceWiseReport.location=="Unable
											// to find location"){
											// locationName="<span class='btn
											// btn-success'
											// id='"+invoiceWiseReport.pid+"'
											// onClick='InvoiceWiseReport.getLocation(this)'
											// >get location</span>";
											// }
											var locationIcon = locationIcons(invoiceWiseReport);
											var activityStatusTd = "<td>"
													+ invoiceWiseReport.activityStatus
													+ "</td>";
											if (invoiceWiseReport.activityStatus == 'ACCEPTED') {
												activityStatusTd = "<td><font color='green'>"
														+ invoiceWiseReport.activityStatus
														+ "</td>";
											} else if (invoiceWiseReport.activityStatus == 'REJECTED') {
												activityStatusTd = "<td id='rejectReason' title='"
														+ invoiceWiseReport.rejectReasonRemark
														+ "'><font color='red'>"
														+ invoiceWiseReport.activityStatus
														+ "" + "</td>";
											}
											var changeAccount = "";
											if (interimSave == "true") {
												changeAccount = "&nbsp;&nbsp;<button type='button' class='btn btn-blue' onclick='InvoiceWiseReport.changeAccountProfile(\""
														+ invoiceWiseReport.pid
														+ "\");'>Change Account Profile</button>"
											} else {
												changeAccount = "";
											}
											var geoTag = "";
											if (distanceTarvel == "true") {
												geoTag = "&nbsp;&nbsp;<button type='button' title='geo tag to account' class='btn btn-orange entypo-map' aria-label='entypo-map' onclick='InvoiceWiseReport.setGeoTag(\""
														+ invoiceWiseReport.pid
														+ "\",\""
														+ invoiceWiseReport.accountProfileLatitude
														+ "\",\""
														+ invoiceWiseReport.accountProfileLongitude
														+ "\",\""
														+ invoiceWiseReport.accountProfileLocation
														+ "\",\""
														+ invoiceWiseReport.latitude
														+ "\",\""
														+ invoiceWiseReport.longitude
														+ "\",\""
														+ invoiceWiseReport.location
														+ "\",this);'></button>";
											}

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
																	+ "</td><td class=tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.punchInDate,
																			'MMM DD YYYY,h:mm:ss a')
																	+ "</td><td class='tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.sendDate,
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
																	+ mockLocatation
																	+ "</td>"
																	+ "<td>"
																	+ towerLocationName
																	+ "</td><td>"
																	+ withCustomer
																	+ "</td><td>"
																	+ invoiceWiseReport.totalSalesOrderAmount
																	+ "</td>"
																	+ "<td>"
																	+ invoiceWiseReport.totalRecieptAmount
																	+ "</td><td>"
																	+ (invoiceWiseReport.remarks == null ? ""
																			: invoiceWiseReport.remarks)
																	+ "</td></tr>");

											$
													.each(
															invoiceWiseReport.invoiceWiseReportDetailViews,
															function(index,
																	executionDetail) {
																if (executionDetail.documentType == "INVENTORY_VOUCHER") {
																	transactionAmount += executionDetail.documentTotal;
																	transactionVolume += executionDetail.documentVolume;
																} else if (executionDetail.documentType == "ACCOUNTING_VOUCHER") {
																	totalPayments += executionDetail.documentTotal;
																}

																var editButton = "";
																if (interimSave == "true") {
																	editButton = "<button type='button' class='btn btn-white' onclick='InvoiceWiseReport.editDetails(\""
																			+ invoiceWiseReport.pid
																			+ "\",\""
																			+ executionDetail.documentType
																			+ "\",\""
																			+ executionDetail.pid
																			+ "\");'>Edit</button>"
																} else {
																	editButton = "";
																}

																var dynamicvalue = "";
																if (executionDetail.documentType != "DYNAMIC_DOCUMENT") {
																	$(
																			'#tBodyInvoiceWiseReport')
																			.append(
																					"<tr class='tableexport-ignore' style='background: rgba(225, 225, 225, 0.66);' data-id='"
																							+ executionDetail.pid
																							+ "1' data-parent='"
																							+ invoiceWiseReport.pid
																							+ "'><td>&nbsp;</td><td colspan='3'>"
																							+ executionDetail.documentName
																							+ "</td><td>"
																							+ executionDetail.documentTotal
																							+ "</td><td><button type='button' class='btn btn-white' onclick='InvoiceWiseReport.viewDetails(\""
																							+ executionDetail.pid
																							+ "\",\""
																							+ executionDetail.documentType
																							+ "\");'>View Details</button>"
																							+ editButton
																							+ "</td></tr>");
																} else {
																	var elmViewImage = "";
																	if (executionDetail.imageSaved) {
																		elmViewImage = "<td><button type='button' class='btn btn-blue' onclick='InvoiceWiseReport.showModalPopup($(\"#imagesModal\"),\""
																				+ executionDetail.pid
																				+ "\",0);'>View Images</button></td>";
																	}
																	$(
																			'#tBodyInvoiceWiseReport')
																			.append(
																					"<tr style='background: rgba(225, 225, 225, 0.66);' data-id='"
																							+ executionDetail.pid
																							+ "1' data-parent='"
																							+ invoiceWiseReport.pid
																							+ "'><td>&nbsp;</td><td colspan='3'>"
																							+ executionDetail.documentName
																							+ "</td><td>"
																							+ dynamicvalue
																							+ "</td><td><button type='button' class='btn btn-white' onclick='InvoiceWiseReport.viewDetails(\""
																							+ executionDetail.pid
																							+ "\",\""
																							+ executionDetail.documentType
																							+ "\");'>View Details</button>"
																							+ editButton
																							+ "</td>"
																							+ elmViewImage
																							+ "</tr>");
																}
															});
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

	InvoiceWiseReport.setGeoTag = function(pid, accLat, accLon, accLoc, newLat,
			newLon, newLoc, obj) {

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

	InvoiceWiseReport.saveNewGeoLocation = function() {
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

	InvoiceWiseReport.editDetails = function(pid, documentType, documentTypePid) {
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
	InvoiceWiseReport.changeAccountProfile = function(pid) {
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

				InvoiceWiseReport.getForms();
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

	InvoiceWiseReport.newAccount = function() {
		$("#newAccount").css("display", "block");
		$("#oldAccounts").css("display", "none");
	};

	InvoiceWiseReport.oldAccount = function() {
		$("#oldAccounts").css("display", "block");
		$("#newAccount").css("display", "none");
	};

	InvoiceWiseReport.changeAccount = function() {

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
				InvoiceWiseReport.filter();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	};

	InvoiceWiseReport.createAndChangeAccount = function() {
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
				InvoiceWiseReport.filter();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});

	};

	InvoiceWiseReport.getForms = function() {
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

	InvoiceWiseReport.loadAccountFromForm = function() {
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

	InvoiceWiseReport.accept = function(pid, obj) {
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

	InvoiceWiseReport.reject = function() {
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

	function showDynamicDocumentImages(pid) {
		$('#divInvoiceWiseReportImages')
				.html(
						'<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		$
				.ajax({
					url : location.protocol + '//' + location.host
							+ "/web/dynamic-documents/images/" + pid,
					type : 'GET',
					success : function(filledFormFiles) {
						$('#divInvoiceWiseReportImages').html("");
						if (filledFormFiles.length == 0) {
							var table = '<table class="table  table-striped table-bordered"><tr><td style="font-weight: bold;">Image Not Found</td></tr></table>';
							$('#divInvoiceWiseReportImages').append(table);
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
											$('#divInvoiceWiseReportImages')
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