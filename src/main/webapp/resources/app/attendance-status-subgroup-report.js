// Create a AttendanceStatusSubgroupReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AttendanceStatusSubgroupReport) {
	this.AttendanceStatusSubgroupReport = {};
}
(function() {
	'use strict';
	
	var attendanceStatusSubgroupReportContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	
	
	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		
		// load today data
		AttendanceStatusSubgroupReport.filter();

		// table search
		$('#search').keyup(function() {
			searchTable($(this).val());
		});

		$('#btnDownload').on('click', function() {
			var tblAttendanceReport = $("#tblAttendanceReport tbody");
			if (tblAttendanceReport.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblAttendanceReport[0].textContent == "No data available") {
				alert("no values available");
				return;
			}

			downloadXls();
		});
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
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblAttendanceReport").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "attendanceReport";

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
	
	AttendanceStatusSubgroupReport.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyAttendanceReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : attendanceStatusSubgroupReportContextPath + "/filter",
					type : 'GET',
					data : {
						attStatus : $("#dbAttStatus").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(attendanceList) {
						$('#tBodyAttendanceReport').html("");
						if (attendanceList.length == 0) {
							$('#tBodyAttendanceReport')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										attendanceList,
										function(index, attendance) {
											let end = moment(attendance.createdDate);
											let startTime = moment(attendance.plannedDate);
											let duration = moment.duration(end.diff(startTime));
											let rowColor = "";
											if(duration.asMinutes() > 10){
												rowColor = "background-color:yellow";
											}
											if(attendance.attendanceSubGroupName==null){
											$('#tBodyAttendanceReport')
													.append(
															"<tr style='"+ rowColor +"'><td>"
																	+ formatDate(attendance.attendanceDay, 'dddd, MMM Do YYYY')
																	+ "</td><td>"
																	+ attendance.employeeName
																	+ "</td><td>"+ attendance.attendanceStatus+ "</td><td>"
																	+ formatDate(attendance.plannedDate, 'MMM DD YYYY, h:mm:ss a')
																	+ "</td><td>"
																	+ formatDate(attendance.createdDate,'MMM DD YYYY, h:mm:ss a')
																	+ "</td><td>"
																	+ (attendance.location == null ? "" : attendance.location)
																	+ "</td><td>"
																	+ (attendance.remarks == null ? ""
																			: attendance.remarks)
																	+ "</td></tr>");
											} else {
												var taskListAlias = attendance.taskListAlias == null ? "" : " - " + attendance.taskListAlias;
												$('#tBodyAttendanceReport')
												.append(
														"<tr style='"+ rowColor +"'><td>"
																+ attendance.employeeName
																+ "</td><td>"+ attendance.attendanceStatus +"("+attendance.attendanceSubGroupName+")" 
																+ taskListAlias
																+ "</td><td>"
																+ formatDate(attendance.plannedDate,'MMM DD YYYY, h:mm:ss a')
																+ "</td><td>"
																+ formatDate(attendance.createdDate, 'MMM DD YYYY, h:mm:ss a')
																+ "</td><td>"
																+ (attendance.remarks == null ? ""
																		: attendance.remarks)
																+ "</td><td>"
																+ (attendance.location == null ? "" : attendance.location)
																+ "</td></tr>");
											}
										});
					}
				});
	}
	
	
	AttendanceStatusSubgroupReport.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

	function searchTable(inputVal) {
		var table = $('#tBodyAttendanceReport');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					var regExp = new RegExp(inputVal, 'i');
					if (regExp.test($(td).text())) {
						found = true;
						return false;
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format();
		} else {
			return "";
		}
	}
	
})();