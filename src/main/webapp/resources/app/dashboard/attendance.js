function loadAttendanceOnMiddleBar() {
	console.log("loading attendance summary............");
	$.ajax({
		url : appContextPath + "/web/dashboard/attendance",
		type : "GET",
		data : {
			date : formatDate($('#txtDate').val(), 'YYYY-MM-DD'),
		},
		success : function(attendance) {
			$("#lblTotalUsers").html(attendance.totalUsers);
			$("#lblAttendedUsers").html(attendance.attendedUsers);
		},
		error : function(xhr, error) {
			console.log("<< Error in loading attendance summary >>");
		}
	});
}