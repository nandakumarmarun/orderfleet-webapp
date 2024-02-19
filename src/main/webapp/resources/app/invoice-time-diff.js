// Create a InvoiceWiseReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InvoiceTimeDiff) {
	this.InvoiceTimeDiff = {};
}

(function() {
	'use strict';

	var invoiceTimeDiffContextPath = location.protocol + '//' + location.host
			+ location.pathname;


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

	$('#downloadXls').on('click', function() {
            			var tblSale = $("#tblInvoiceWiseReport tbody");
            			if (tblSale.children().length == 0) {
            				alert("no values available");
            				return;
            			}
            			downloadXls();
            		});

		$('.selectpicker').selectpicker();
	});

	function downloadXls() {
    		// Avoid last column in each row
    //		$("#tblVerifyAccountProfile th:last-child, #tblVerifyAccountProfile td:last-child").hide();
    		var excelName = "TransactionREport";
    		var table2excel = new Table2Excel();
    		    table2excel.export(document.getElementById('tblInvoiceWiseReport'),excelName);
    		$("#tblInvoiceWiseReport th:last-child, #tblInvoiceWiseReport td:last-child").show();
    	}

	InvoiceTimeDiff.filter = function() {
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
		if ($("#dbEmployee").val() == "no") {
                			alert("Please Select Employee")
                			return;
                		}

		$('#tBodyInvoiceWiseReport').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : invoiceTimeDiffContextPath + "/filter",
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
						if (invoiceWiseReports.length == 0) {
							$('#tBodyInvoiceWiseReport')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}

						$
								.each(
										invoiceWiseReports,
										function(index, invoiceWiseReport) {

											var locationName = invoiceWiseReport.location;
                                         if (invoiceWiseReport.location == "No Location"
													&& invoiceWiseReport.latitude != 0) {
												locationName = "<span class='btn btn-success'  id='"
														+ invoiceWiseReport.pid
														+ "' onClick='InvoiceTimeDiff.getLocation(this)' >get location</span>";
											}

                                         var locationIcon = locationIcons(invoiceWiseReport);
											var mockLocation = "";

											if (invoiceWiseReport.mockLocationStatus) {
												mockLocation = "Enabled"
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
																	+ "</td><td class='tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.punchInDate,
																			'MMM DD YYYY, h:mm:ss a')
																	+ "</td><td class='tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.sendDate,
																			'MMM DD YYYY, h:mm:ss a')
																	+ "</td><td class='tableexport-string target'>"
																	+invoiceWiseReport.timeBetweenOrder
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
																	+ mockLocation
																	+ "</td>"
																	+ "<td>"
																     + (invoiceWiseReport.remarks == null ? ""
																			: invoiceWiseReport.remarks)
																	+ "</td></tr>");





										});


					}
				});
	}



	InvoiceTimeDiff.getActivities = function(activityType) {
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
			url : invoiceTimeDiffContextPath + "/getActivities/"
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







	InvoiceTimeDiff.getLocation = function(obj) {
		var pid = $(obj).attr("id");
		$(obj).html("loading...");
		$.ajax({
			url : invoiceTimeDiffContextPath + "/updateLocation/" + pid,
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








	InvoiceTimeDiff.showDatePicker = function() {
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

	InvoiceTimeDiff.closeModalPopup = function(el) {
		el.modal('hide');
	}

})();