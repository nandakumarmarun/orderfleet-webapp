// Create a InvoiceWiseLocationReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InvoiceWiseLocationReport) {
	this.InvoiceWiseLocationReport = {};
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

	$(document).ready(function() {
		let filterBy = getParameterByName('filterBy');
		if (filterBy) {
			$('#dbDateSearch').val(filterBy);
           InvoiceWiseLocationReport.skippedCustomerList();
		}

		$('.selectpicker').selectpicker();
	});

	InvoiceWiseLocationReport.downloadXls = function() {
		var excelName = "SkippedCustomer"

		var instance = $('#tblInvoiceWiseLocationReport').tableExport({
			formats : [ 'xlsx' ],
			filename : excelName,
			exportButtons : false
		});
		var exportData = instance.getExportData()['tblInvoiceWiseLocationReport']['xlsx'];
		instance.export2file(exportData.data, exportData.mimeType,
				exportData.filename, exportData.fileExtension);
	}


	InvoiceWiseLocationReport.getActivities = function(activityType) {
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




	InvoiceWiseLocationReport.searchTable = function(inputVal, table) {
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


  InvoiceWiseLocationReport.skippedCustomerList = function() {
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

		$('#tBodyInvoiceWiseLocationReport').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : invoiceWiseReportContextPath
							+ "/filterSkippedCustomers",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						documentPid : $("#dbDocument").val(),
						activityPid : $("#dbActivity").val(),
						locationPid : $("#dbLocation").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")

					},
					success : function(accountProfile) {
						$('#tBodyInvoiceWiseLocationReport').html("");

						if (accountProfile.length == 0) {
							$('#tBodyInvoiceWiseLocationReport')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										accountProfile,
										function(index, accounts) {
											$('#tBodyInvoiceWiseLocationReport')
													.append(
															"<tr><td>"
																	+ accounts.accountType
																	+ "</td><td>"
																	+ accounts.location
																	+ "</td><td>"
																	+ accounts.accountProfileName
																	+ "</td><td>"
																	+ accounts.address
																	+ "</td><td>"
																	+ (accounts.geotagLocation == null ? " ":accounts.geotagLocation)
																	+ "</td><td>"
																	+ (accounts.closingBalance == null ? " ":accounts.closingBalance)
																	+ "</td><td>"
																	+ (accounts.district== null ? " ":accounts.district)
																	+ "</td><td>"
																	+(accounts.phoneNo == null ? " ":accounts.phoneNo)
																	+ "</td><td>"
																	+ (accounts.email == null ? " ":accounts.email)
																    + "</td></tr>");
										});

					}
				});

	}


	InvoiceWiseLocationReport.showDatePicker = function() {
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

	InvoiceWiseLocationReport.closeModalPopup = function(el) {
		el.modal('hide');
	}

})();