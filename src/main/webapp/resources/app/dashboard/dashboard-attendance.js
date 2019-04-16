let dashboardContextPath = location.protocol + '//' + location.host
			+ location.pathname;

function loadAttendanceBasedDashboard() {
	let loadingImg = '<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">';
	$("#divDaySummary").html(loadingImg);
	$("#divWeekSummary").html(loadingImg);
	$("#divMonthSummary").html(loadingImg);
	loadAttendanceHeaderData();
	loadUsersSummary();
}

function loadAttendanceHeaderData() {
	
	
	var activeDate = $('#txtDate').val();
	$
			.ajax({
				url : dashboardContextPath + "/summary/attendance",
				type : "GET",
				data : {
					date : formatDate($('#txtDate').val(), 'YYYY-MM-DD'),
				},
				success : function(dashboardData) {
					$("#divDaySummary").html(
							getSummaryHtml(dashboardData.daySummaryDatas,
									"day", ""));
					$("#divWeekSummary").html(
							getSummaryHtml(dashboardData.weekSummaryDatas,
									"week", ""));
					$("#divMonthSummary").html(
							getSummaryHtml(dashboardData.monthSummaryDatas,
									"month", ""));
				},
				error : function(xhr, error) {
					console
							.log("Error in load attendance header Summary..................");
				}
			});
}
