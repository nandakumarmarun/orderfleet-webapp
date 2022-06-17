// Create a InvoiceWiseReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DeliveryWiseReport) {
	this.DeliveryWiseReport = {};
}

(function() {
	'use strict';

	var deliveryWiseReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	var dashboardEmployees = [];
	var otherEmployees = [];
	var allEmployeesMap = new Object();
	

	
	$(document).ready(function() {
		
		let filterBy = getParameterByName('filterBy');
		if (filterBy) {
			$('#dbDateSearch').val(filterBy);
			DeliveryWiseReport.filter();
		}

		$('.selectpicker').selectpicker();
	});


	
	DeliveryWiseReport.downloadXls = function() {
		
		
		var excelName = "acvts_txns_"+ new Date().toISOString().replace(/[\-\:\.]/g, "");
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblInvoiceWiseReport'),excelName);
		 
	}

	DeliveryWiseReport.filter = function() {
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

		$('#tBodyDeliveryWiseReport').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : deliveryWiseReportContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						activityPid : $("#dbActivity").val(),
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						
					},
					success : function(deliveryWiseReports) {
						$('#tBodyDeliveryWiseReport').html("");

						
						if (deliveryWiseReports.length == 0) {
							$('#tBodyDeliveryWiseReport')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						
						$
								.each(
										deliveryWiseReports,
										function(index, invoiceWiseReport) {
											var content = "";
											
											if (invoiceWiseReport.imageButtonVisible) {
												content = "<td><button type='button' class='btn btn-info' onclick='DeliveryWiseReport.showModalPopup($(\"#imagesModal\"),\""
														+ invoiceWiseReport.pid
														+ "\",0);'>View Images</button></td>";
											} else {
												content = "<td></td>";
											}


											
											$('#tBodyDeliveryWiseReport')
													.append(
															"<tr style='"
															+ invoiceWiseReport.pid
															+ "' data-parent=\"\"><td class='tableexport-string target'>"
															+ invoiceWiseReport.employeeName
															+ "</td><td>"
															+ invoiceWiseReport.accountProfileName
															+ "</td><td>"
															+ invoiceWiseReport.invoiceNo
															+ "</td><td>"
															+ invoiceWiseReport.totalSalesOrderAmount
															+ "</td><td class='tableexport-string target'>"
															+ formatDate(
																	invoiceWiseReport.createdDate,
																	'MMM DD YYYY, h:mm:ss a')
															+ "</td><td>"
															+ (invoiceWiseReport.vehicleRegistrationNumber == null ? ""
																	: invoiceWiseReport.vehicleRegistrationNumber)
															+ "</td><td class='tableexport-string target'>"
															+ formatDate(
																	invoiceWiseReport.sendDate,
																	'MMM DD YYYY, h:mm:ss a')
															
															+ "</td></tr>");
											
										});

						
					}
				});
	}

	
	

	
	
	DeliveryWiseReport.searchTable = function(inputVal, table) {
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

	
	DeliveryWiseReport.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showDeliveryImage(pid);
				break;
			}
		}
		el.modal('show');
	}

	function showDeliveryImage(pid) {
		$
				.ajax({
					url : deliveryWiseReportContextPath + "/images/" + pid,
					method : 'GET',
					success : function(filledFormFiles) {

						$('#divAttendanceImages').html("");
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
											$('#divAttendanceImages')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}
	DeliveryWiseReport.showDatePicker = function() {
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

	DeliveryWiseReport.closeModalPopup = function(el) {
		el.modal('hide');
	}

})();