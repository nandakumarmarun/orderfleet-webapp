// Create a DayPlanExecutionReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DayPlanExecutionReport) {
	this.DayPlanExecutionReport = {};
}

(function() {
	'use strict';

	var dayPlanExecutionReportContextPath = location.protocol + '//'
			+ location.host;
	$(document).ready(function() {
		$("#txtDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		
		$('#btnDownload').on('click', function() {
			var tblCompany = $("#tblDayPlans tbody");
			if (tblCompany.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblCompany[0].textContent == "No data available") {
				alert("no values available");
				return;
			}

			downloadXls();
		});
	});
	
	function downloadXls() {

		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblDayPlans").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "dayPlans";

		clonedTable.table2excel({
			// exclude CSS class
			// exclude : ".odd .even",
			// name : "Dynamic Document Form",
			filename : excelName, // do not include extension
		// fileext : ".xls",
		// exclude_img : true,
		// exclude_links : true,
		// exclude_inputs : true
		});
	
	}
	DayPlanExecutionReport.filter = function() {
		if ($("#txtDate").val() == "" || $("#dbEmployee").val() == "no") {
			alert("Please select a Employee and Date");
			return;
		}
		$('#tBodyDayPlans').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");

		$
				.ajax({
					url : dayPlanExecutionReportContextPath
							+ "/web/day-plan-execution-report/load",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						date : $("#txtDate").val()
					},
					success : function(dayPlanExecutions) {
						$('#tBodyDayPlans').html("");
						if (dayPlanExecutions.length == 0) {
							$('#tBodyDayPlans')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										dayPlanExecutions,
										function(index, dayPlanExecution) {
											
										
											
											
											var locationName = dayPlanExecution.geoLocation;
											var geoLocationName =dayPlanExecution.geoTagLocation;
											var executionOrder;
											if (dayPlanExecution.sortOrder > 0) {
												executionOrder = dayPlanExecution.sortOrder;
											} else {
												executionOrder = "<span style='color:red'>X</span>";
											}
											if (dayPlanExecution.geoLocation == "No Location"
													&& dayPlanExecution.executionLatitude != 0) {
												locationName = "<span class='btn btn-success'  id='"
														+ dayPlanExecution.executiveTaskExecutionPid
														+ "' onClick='DayPlanExecutionReport.getLocation(this)' >Geo location</span>";
											}
											if (dayPlanExecution.accountLatitude != 0) {
											geoLocationName = "<span class='btn btn-success'  id='"
													+ dayPlanExecution.accountProfilePid
													+ "' onClick='DayPlanExecutionReport.getGeoLocation(this)' >Geo Tag</span>";
										}
											
											var actualOrder = index + 1;
											$('#tBodyDayPlans')
													.append(
															"<tr>"
																	+ "<td class='janvary'>"
																	+ (dayPlanExecution.createdDate == null ? "" : moment(
																			dayPlanExecution.createdDate)
																			.format(
																					'DD MMM YYYY hh:mm a'))
																	+ "</td><td>"
																	+ dayPlanExecution.activityName
																	+ " - "
																	+ dayPlanExecution.accountProfileName
																	+"</td><td>"
																	+ (dayPlanExecution.serverDate == null ? "" : moment(dayPlanExecution.serverDate).format('DD MMM YYYY hh:mm a'))
																	+ "</td><td>"
																	+ actualOrder
																	+ "</td><td>"
																	+ executionOrder
																	+ "</td><td>"
																	+ dayPlanExecution.taskPlanStatus
																	+ "</td><td>"
																	+ (dayPlanExecution.userRemarks == null ? "" : dayPlanExecution.userRemarks)
																	+ "</td><td>"
																	+(locationName == null ? "" :locationName)
																	+"</td><td>"
																	+(geoLocationName == null ? "" :geoLocationName)
																	+"</td><td>"
																	+(dayPlanExecution.variance == null ? "":dayPlanExecution.variance)

																	+ "</td></tr>");
										});
					},
					error : function() {
						$('#tBodyDayPlans').html("");
					}
				});
	}
	
	DayPlanExecutionReport.getLocation = function(obj) {
		var pid = $(obj).attr("id");
		$(obj).html("loading...");
		$.ajax({
			url : dayPlanExecutionReportContextPath + "/web/day-plan-execution-report/updateLocation/" + pid,
			method : 'GET',
			success : function(data) {
				console.log(data.geoLocation)
				$(obj).html(data.geoLocation);
				$(obj).removeClass("btn-success");
				$(obj).removeClass("btn");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}
	
	DayPlanExecutionReport.getGeoLocation = function(obj) {
		var pid = $(obj).attr("id");
		$(obj).html("loading...");
		$.ajax({
			url : dayPlanExecutionReportContextPath + "/web/day-plan-execution-report/updateGeoLocation/" + pid,
			method : 'GET',
			success : function(data) {
			
				$(obj).html(data.geoTagLocation);
				$(obj).removeClass("btn-success");
				$(obj).removeClass("btn");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	
	
	


})();