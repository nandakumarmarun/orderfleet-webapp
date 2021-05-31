if (!this.VisitsReport) {
	this.VisitsReport = {};
}

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		var employeePid = getParameterByName('user-key-pid');
		getEmployees(employeePid); // common js
		
		$('#downloadXls').on('click', function() {
			var tblSale = $("#tblVisitReport tbody");
			if (tblSale.children().length == 0) {
				alert("no values available");
				return;
			}
			downloadXls();
		});
		
		//load default
		VisitsReport.filter();
	});
	
	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}
	
	function downloadXls() {
		var clonedTable = $("#tblVisitReport").clone();
		clonedTable.find('[style*="display: none"]').remove();
		var excelName = "employees_visit_report";
		clonedTable.table2excel({
			filename : excelName,
		});
	}

	VisitsReport.filter = function() {
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
		loadReports("Executive wise visit report");
	}
	
	function loadReports(rName) {
		$('#title').text(rName);
		$('#tHeadVisitReport').html("");
		$("#lblTotal").text("0");
		
		getVisitsReport();
	}
	
	function getVisitsReport() {
		$('#tBodyVisitReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : contextPath + "/web/visits-report/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")
					},
					success : function(visits) {
						if (visits == null || visits == "") {
							$('#tBodyVisitReport')
								.html(
									"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						} else {
							$('#tBodyVisitReport').html("");
						}
						var headerRow = '<tr><th style="min-width: 80px;">Employee</th>';
						$.each(visits.reportHeaders, function(indx, name){
							headerRow += '<th>'+ name +'</th>'
						});
						headerRow += '<th>No Order</th><th>Volume</th></tr>';

						$('#tHeadVisitReport').html(headerRow);
						
						if(visits.reportValues.length == 0){
							$('#tBodyVisitReport')
							.html(
								"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$.each(visits.reportValues, function(indx, values){
							var rowHtml = "<tr>";
							$.each(values, function(indx, value){
								if(!isNaN(value)){
									value = Math.round(value * 100) / 100;
								}
								rowHtml += "<td>"+ value +"</td>"
							});
							rowHtml += "</tr>";
							$('#tBodyVisitReport').append(rowHtml);
						});
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}

		
	VisitsReport.showDatePicker = function() {
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
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}

})();