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
		$("#txtDate").datepicker({ dateFormat: "dd-mm-yy" });
	});
	DayPlanExecutionReport.filter = function() {
		if ($("#txtDate").val() == "" || $("#dbEmployee").val() == "no") {
			alert("Please select a Employee and Date");
			return;
		}
		$('#tBodyDayPlans').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");

		$
				.ajax({
					url : dayPlanExecutionReportContextPath + "/web/day-plan-execution-report/load",
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
											var executionOrder;
											if(dayPlanExecution.sortOrder > 0) {
												executionOrder = dayPlanExecution.sortOrder;
											} else {
												executionOrder = "<span style='color:red'>X</span>";
											}
											$('#tBodyDayPlans')
													.append(
															"<tr><td>"
																	+ dayPlanExecution.activityName
																	+ " - "
																	+ dayPlanExecution.accountProfileName
																	+ "</td><td>"
																	+ executionOrder
																	+ "</td><td>"
																	+ dayPlanExecution.taskPlanStatus
																	+ "</td><td>"
																	+ dayPlanExecution.userRemarks
																	+ "</td><td class='janvary'>"
																	+  moment(dayPlanExecution.createdDate).format('DD MMM YYYY hh:mm')
																	+ "</td></tr>");
										});
					},
					error : function() {
						$('#tBodyDayPlans').html("");
					}
				});
	}

})();