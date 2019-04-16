if (!this.LeadToCashReport) {
	this.LeadToCashReport = {};
}

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		let employeePid = getParameterByName('user-key-pid');
		//getEmployees(employeePid); // common js
		$('#dbDateSearch').val("TILLDATE");
		//load default
		LeadToCashReport.filter();
	});
	
	LeadToCashReport.filter = function() {
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
		loadReports();
	}
	
	function loadReports() {
		$('#tBodyLeadToCashReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		let empPid = $('#dbEmployee').val();
		var employeePids = ""; 
		if(empPid == "Dashboard Employee" || empPid == "no"){
			employeePids = $('#dbEmployee option').map(function() {
		        return $(this).val();
		    }).get().join(',');
		}
		if(empPid == "Dashboard Employee" || empPid == "no") {
			employeePids = employeePids.replace("Dashboard Employee,","").replace("no,", "");
		} else {
			employeePids = empPid;
		}
		$
				.ajax({
					url : contextPath + "/web/lead-to-cash-report/filter",
					type : 'GET',
					data : {
						employeePids : employeePids,
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
					},
					success : function(reportDatas) {
						if (jQuery.isEmptyObject(reportDatas)) {
							$('#tBodyLeadToCashReport')
								.html(
									"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						} else {
							$('#tBodyLeadToCashReport').html("");
						}
						if(reportDatas.length == 0){
							$('#tBodyLeadToCashReport')
							.html(
								"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						
						var searchDate = $("#dbDateSearch").val();
						$.each(reportDatas, function(empName, columns){
							var empArray = empName.split("~")
							var rowHtml = "<tr><td><a style='text-decoration:underline' " +
									"href='executive-wise-leads?dbDateSearch="+searchDate+"&user-key-pid="+empArray[1]+"'>" + empArray[0] + "</a></td>";
							$.each(columns, function(indx, column){
								rowHtml += "<td>"+ column +"</td>"
							});
							rowHtml += "</tr>";
							$('#tBodyLeadToCashReport').append(rowHtml);
						});
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}
	
	LeadToCashReport.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
	}
	
	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD, YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

})();