// Create a InvoiceWiseReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.LeadStatusAnalytics) {
	this.LeadStatusAnalytics = {};
}

(function() {
	'use strict';

	var leadStatusAnalyticsContextPath = location.protocol + '//' + location.host
			+ location.pathname;

var dashboardEmployees = [];
	var otherEmployees = [];
	var allEmployeesMap = new Object();
	$(document).ready(function() {
		$('#downloadXls').on('click', function() {
        			var tblSale = $("#tblLeadStatusAnalytics tbody");
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
		var excelName = "LeadStatus";
		var table2excel = new Table2Excel();
		    table2excel.export(document.getElementById('tblLeadStatusAnalytics'),excelName);
		$("#tblLeadStatusAnalytics th:last-child, #tblLeadStatusAnalytics td:last-child").show();
	}

	LeadStatusAnalytics.filter = function() {
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
			if ($("#dbDocument").val() == "no") {
                			alert("Please Select a document")
                			return;
                		}

		$('#tBodyLeadStatusAnalytics').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : leadStatusAnalyticsContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						documentPid : $("#dbDocument").val(),
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")
					},
					success : function(invoiceWiseReports) {
						$('#tBodyLeadStatusAnalytics').html("");
						if (invoiceWiseReports.length == 0) {
							$('#tBodyLeadStatusAnalytics')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var totalDealVolume = 0;
						var totalWonVolume =0;
						var totalLostVolume =0;
						var totalBalVolume = 0;
						var openCount =0;
						var wonCount = 0;
						var lostCount =0;

						$
								.each(
										invoiceWiseReports,
										function(index, invoiceWiseReport) {
                                               totalDealVolume += invoiceWiseReport.dealVolume;
                                               totalWonVolume += invoiceWiseReport.wonVolume;
                                                totalLostVolume += invoiceWiseReport.lostVolume;
                                                totalBalVolume += invoiceWiseReport.balanceDealVolume;
                                                if(invoiceWiseReport.leadStatus=='Open')
                                                {
                                                 openCount += 1;
                                                }
                                                 if(invoiceWiseReport.leadStatus=='Won')
                                                {
                                                 wonCount += 1;
                                                }
                                                if(invoiceWiseReport.leadStatus=='Lost')
                                               {
                                                lostCount += 1;
                                                }

											$('#tBodyLeadStatusAnalytics')
													.append(
															        "<tr><td>"
																	+ invoiceWiseReport.employeeName
																	+ "</td><td>"
																	+ invoiceWiseReport.accountName
																	+ "</td><td>"
																	+ formatDate(
                                                                     invoiceWiseReport.createdDate,
                                                                    'MMM DD YYYY')
										                            +"</td><td>"
																	+ formatDate(invoiceWiseReport.createdDate, 'h:mm:ss a')
																	+ "</td><td>"
																    + invoiceWiseReport.documentName
																	 +"</td><td>"
																	 +(invoiceWiseReport.leadStatus == null ? "": invoiceWiseReport.leadStatus)
																     +"</td><td>"
																	 +(invoiceWiseReport.dealVolume == null ?"":invoiceWiseReport.dealVolume)
																	  +"</td><td>"
                                                                    +(invoiceWiseReport.wonVolume == null ?"":invoiceWiseReport.wonVolume)
                                                                     +"</td><td>"
                                                                    +(invoiceWiseReport.lostVolume == null ?"":invoiceWiseReport.lostVolume)
                                                                     +"</td><td>"
                                                                   	 +(invoiceWiseReport.balanceDealVolume == null ?"":invoiceWiseReport.balanceDealVolume)
																    	+ "</td></tr>");


										});


									var btn = document.getElementById("myBtn")
										btn.onclick = function(){
                                    $('#myModal').modal('show');
                     generateChart(totalDealVolume,totalWonVolume,totalLostVolume,totalBalVolume,openCount,wonCount,lostCount)
                     }
					}
				});
}
function generateChart(a,b,c,d,e,f,g) {
    var chart = new CanvasJS.Chart("chartContainer", {

        animationEnabled: true,
        theme: "light2",
        title:{
            text: "Lead Status Analytics(MT)"
        },
        legend: {
        		cursor:"pointer",
        	},
        data: [{
            type: "column",
            name: "Volume In MT",
              legendText: "volume in MT",
              indexLabel: "{y}",
              showInLegend: true,
            dataPoints: [
                { y: a, label: "Open MT" },
                { y: b,  label: "Won MT" },
                { y: c,  label: "Lost MT" },
                { y: d,  label: "Balance MT" },

            ]
        },
        {
        		type: "column",
        		name: "Lead Status Count",
        		legendText: "Lead Count",
                 indexLabel: "{y}",
                 showInLegend: true,
        		dataPoints:[
        			{ y: e, label: "Open " },
                    { y: f,  label: "Won " },
                    { y: g,  label: "Lost " },

        		]
    }]
    });
chart.render();

}
LeadStatusAnalytics.filterRawData = function(){

	    var	employeePid = $('#dbEmployee').val();
	    var documentPid = $("#dbDocument").val();
        var accountPid = $("#dbAccount").val();
        var	filterBy = $("#dbDateSearch").val();
        var	fromDate = $("#txtFromDate").val();
        var	toDate = $("#txtToDate").val();
        var	inclSubordinate = $('#inclSubOrdinates').is(":checked");

      window.location.href =  leadStatusAnalyticsContextPath+"/downloadRawData?&employeePid="+employeePid+'&documentPid='+documentPid+
                                             '&accountPid='+accountPid+'&filterBy='+filterBy+'&fromDate='+fromDate+'&toDate='+toDate+'&inclSubordinate='+inclSubordinate;

console.log("Success.....");
}
	LeadStatusAnalytics.showDatePicker = function() {
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

	LeadStatusAnalytics.closeModalPopup = function(el) {
		el.modal('hide');
	}

})();