// Create a InvoiceWiseReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.TimeSpendReport) {
	this.TimeSpendReport = {};
}

(function() {
	'use strict';

	var timeSpendReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		let filterBy = getParameterByName('filterBy');
		if (filterBy) {
			$('#dbDateSearch').val(filterBy);

		}

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

		$('#btnApply').on('click', function() {

			TimeSpendReport.filter();

		});

		$('#btnDownload').on('click', function() {
			var tblTimeSpendReport = $("#tblTimeSpendReport tbody");
			if (tblTimeSpendReport.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblTimeSpendReport[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			TimeSpendReport.downloadXls();
		});

		// InvoiceWiseReport.filter();
		// if(documentType == null){
		// execute on normal page load

		// }
	});

	TimeSpendReport.downloadXls = function() {
		var excelName = "time_spend_"
				+ new Date().toISOString().replace(/[\-\:\.]/g, "");
		var instance = $('#tblInvoiceWiseReport').tableExport({
			formats : [ 'xlsx' ],
			filename : excelName,
			exportButtons : false
		});
		var exportData = instance.getExportData()['tblTimeSpendReport']['xlsx'];
		instance.export2file(exportData.data, exportData.mimeType,
				exportData.filename, exportData.fileExtension);
	}

	TimeSpendReport.filter = function() {

		if ($('#dbEmployee').val() == "-1") {
			alert("Please Select Employee");
			return

		}

		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}

		$('#tBodyTimeSpendReport').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : timeSpendReportContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
					},
					success : function(timeSpendReports) {
						$('#tBodyTimeSpendReport').html("");

						if (timeSpendReports.length == 0) {
							$('#tBodyTimeSpendReport')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}

						
						$.each(timeSpendReports, function(index,
								timeSpendReport) {
							var rowStyle = "";
							if (timeSpendReport.sortOrder == 1
									|| timeSpendReport.sortOrder == 2) {
								rowStyle = "background-color:#a3a2a2";
							}

							$('#tBodyTimeSpendReport').append(
									"<tr style=" + rowStyle + ">" + "<td>"
											+ timeSpendReport.employeeName
											+ "</td><td>"
											+ timeSpendReport.customerName
											+ "</td><td>"
											+ timeSpendReport.visitType
											+ "</td><td>"
											+ timeSpendReport.punchInTime
											+ "</td><td>"
											+ timeSpendReport.punchOutTime
											+ "</td><td>"
											+ timeSpendReport.serverTime
											+ "</td><td>"
											+ timeSpendReport.timeSpend
											+ "</td></tr>");

						});

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

	TimeSpendReport.showDatePicker = function() {
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