// Create a InvoiceWiseReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InvoiceWiseReport) {
	this.InvoiceWiseReport = {};
}

(function() {
	'use strict';

	var visitDetailReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	var dashboardEmployees = [];
	var otherEmployees = [];
	var allEmployeesMap = new Object();
	// Object for reject acccount profile
	var object = null;
	var invoiceWiseReportPid = null;

	
	$(document).ready(function() {
		console.log("entered>....");
		
		let filterBy = getParameterByName('filterBy');
		if (filterBy) {
			$('#dbDateSearch').val(filterBy);
			InvoiceWiseReport.filter();
		}

		$('.selectpicker').selectpicker();
	});


	
	InvoiceWiseReport.downloadXls = function() {
	   
		        	var	employeePid = $('#dbEmployee').val();
					var filterBy = $("#dbDateSearch").val();
					var	fromDate = $("#txtFromDate").val();
					var	toDate = $("#txtToDate").val();
					var	inclSubordinate = $('#inclSubOrdinates').is(":checked");
					
	     window.location.href =  visitDetailReportContextPath+"/downloadxls?&employeePid="+employeePid+'&filterBy='+filterBy+'&fromDate='+fromDate+'&toDate='+toDate+'&inclSubordinate='+inclSubordinate;
	     console.log("Success.....");
		
	}

	InvoiceWiseReport.filter = function() {
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		
		$('#tBodyInvoiceWiseReport').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url :  visitDetailReportContextPath+ "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
					    toDate   : $("#txtFromDate").val(),
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
											$('#tBodyInvoiceWiseReport')
													.append(
															"<tr style='"
																	
																	+ "' data-id='"
																	+ invoiceWiseReport.pid
																	+ "' data-parent=\"\"><td class='tableexport-string target'>"
																	+ invoiceWiseReport.employeeName
																	+ "</td><td>"
																	+ (invoiceWiseReport.route== null ? " " :invoiceWiseReport.route)
																	+ "</td><td class=tableexport-string target'>"
																	+ formatDate(
																			invoiceWiseReport.attndncTime,
																			'MMM DD YYYY,h:mm:ss a')
																	+ "</td></tr>");
											$
											.each(
													 invoiceWiseReport.visitDetailReportView,
												
													function(index,executionDetail ) {
														$(
														'#tBodyInvoiceWiseReport')
														.append(
																"<tr class='tableexport-ignore' style='background: rgba(225, 225, 225, 0.66);'"
																
																+ "' data-id='"
																+ invoiceWiseReport.pid
																+ "' data-parent=\"\"><td class='tableexport-string target' colspan=3 ><div class='col-md-4'><label>First Visit Time : </label> "
																	+  formatDate(executionDetail.firstVisitTime,'MMM DD YYYY, h:mm:ss a')
																	+"</div><div class='col-md-4'><label>Last Visit Time :</label>"
																	+ formatDate(executionDetail.lastVisitTime,'MMM DD YYYY, h:mm:ss a')
																	+"</div><div class='col-md-4'><label>Total Visit :</label>"
																	+(executionDetail.totalVisit== null ? 0 :executionDetail.totalVisit)
																	+"</div><br><br><div class='col-md-4'><label>First SO Time :</label>"
																	+formatDate(executionDetail.firstSoTime,'MMM DD YYYY, h:mm:ss a')
																	+"</div><div class='col-md-4'><label>Last SO Time :</label>"
																	+formatDate(executionDetail.lastSoTime,'MMM DD YYYY, h:mm:ss a')
																	+"</div><div class='col-md-4'><label>Total SO :</label>"
																	+(executionDetail.totalSo==null ? 0 :executionDetail.totalSo)
																	+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label>Total KG :</label>"
																	+executionDetail.totalKG
																	+"</div><br><br><div class='col-md-4'><label>New Ledger Created :</label>"
																	+ (executionDetail.ledgerCount==null ? 0 :executionDetail.ledgerCount)
																	+"</div><div class='col-md-4'><label>Total Value :</label>"
																	+(executionDetail.totalSaleOrderAmount).toFixed(2)
																	+"</div><div class='col-md-4'><label>Total Collection Value :</label>"
																	+(executionDetail.totalReceiptAmount).toFixed(2)
																	+"</div><br><br><div class='col-md-4'><label>Total Cash Amount :</label>"
																	+(executionDetail.totalCash).toFixed(2)
																	+"</div><div class='col-md-4'><label>Total Cheque Amount :</label>"
																	+(executionDetail.totalCheque).toFixed(2)
																	+"</div><div class='col-md-4'><label>Total Rtgs Amount :</label>"
																	+(executionDetail.totalRtgs).toFixed(2)
																	+ " </div></td></tr>");
													});
											

										});



// $('.collaptable')
// .aCollapTable(
// {
// startCollapsed : true,
// addColumn : false,
// plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
// minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
//  });
					}
				});
	}


//	InvoiceWiseReport.showModal = function(pid, obj) {
//		object = obj;
//		invoiceWiseReportPid = pid;
//		$("#field_reason").val("");
//		$("#myModal").modal("show");
//
//	}
//
//	
//
//	InvoiceWiseReport.showModalPopup = function(el, pid, action) {
//		if (pid) {
//			switch (action) {
//			case 0:
//				showDynamicDocumentImages(pid);
//				break;
//			}
//		}
//		el.modal('show');
//	}


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