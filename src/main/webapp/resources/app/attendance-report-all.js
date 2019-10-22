// Create a AttendanceReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AttendanceReport) {
	this.AttendanceReport = {};
}
(function() {
	'use strict';

	var attendanceReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		var employeePid = getParameterByName('user-key-pid');
		getEmployees(employeePid); // common js

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

		let empPid = getParameterByName('empPid');
		if (empPid) {
			$('#dbEmployee').val(empPid);
		}
		// load today data
		AttendanceReport.filter();

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

	AttendanceReport.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyAttendanceReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : attendanceReportContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						attStatus : $("#dbAttStatus").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")
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
											let duration = moment.duration(end
													.diff(startTime));
											let rowColor = "";
											if (duration.asMinutes() > 10) {
												rowColor = "background-color:yellow";
											}

											var content = "";

											if (attendance.imageButtonVisible) {
												content = "<td><button type='button' class='btn btn-info' onclick='AttendanceReport.showModalPopup($(\"#imagesModal\"),\""
														+ attendance.attendancePid
														+ "\",0);'>View Images</button></td>";
											} else {
												content = "<td></td>";
											}

											if (attendance.attendanceSubGroupName == null) {
												$('#tBodyAttendanceReport')
														.append(
																"<tr style='"
																		+ rowColor
																		+ "'><td>"
																		+ formatDate(
																				attendance.attendanceDay,
																				'dddd, MMM Do YYYY')
																		+ "</td><td>"
																		+ attendance.employeeName
																		+ "</td><td>"
																		+ attendance.attendanceStatus
																		+ "</td><td>"
																		+ formatDate(
																				attendance.plannedDate,
																				'MMM DD YYYY, h:mm:ss a')
																		+ "</td><td>"
																		+ formatDate(
																				attendance.createdDate,
																				'MMM DD YYYY, h:mm:ss a')
																		+ "</td><td><a href='"
																		+ appContextPath
																		+ '/web/live-tracking-attendance?user-key-pid='
																		+ attendance.userPid
																		+ '&date='
																		+ convertDateTime(attendance.plannedDate)
																		+ "' class='icon-container'>"
																		+ attendance.location
																		+ "</a></td><td><a href='"
																		+ appContextPath
																		+ '/web/live-tracking-attendance?user-key-pid='
																		+ attendance.userPid
																		+ '&date='
																		+ convertDateTime(attendance.plannedDate)
																		+ "' class='icon-container'>"
																		+ (attendance.towerLocation == null ? ""
																				: attendance.towerLocation)
																		+ "</a></td><td>"
																		+ (attendance.remarks == null ? ""
																				: attendance.remarks)
																		+ "</td>"
																		+ content
																		+ "</tr>");
											} else {
												var taskListAlias = attendance.taskListAlias == null ? ""
														: " - "
																+ attendance.taskListAlias;
												console
														.log(attendance.attendanceSubGroupName);
												$('#tBodyAttendanceReport')
														.append(
																"<tr style='"
																		+ rowColor
																		+ "'><td>"
																		+ formatDate(
																				attendance.attendanceDay,
																				'dddd, MMM Do YYYY')
																		+ "</td><td>"
																		+ attendance.employeeName
																		+ "</td><td>"
																		+ attendance.attendanceStatus
																		+ "("
																		+ attendance.attendanceSubGroupName
																		+ ")"
																		+ taskListAlias
																		+ "</td><td>"
																		+ formatDate(
																				attendance.plannedDate,
																				'MMM DD YYYY, h:mm:ss a')
																		+ "</td><td>"
																		+ formatDate(
																				attendance.createdDate,
																				'MMM DD YYYY, h:mm:ss a')
																		+ "</td><td><a href='"
																		+ appContextPath
																		+ '/web/live-tracking-attendance?user-key-pid='
																		+ attendance.userPid
																		+ '&date='
																		+ convertDateTime(attendance.plannedDate)
																		+ "' class='icon-container'>"
																		+ attendance.location
																		+ "</a></td><td>"
																		+ "</td><td><a href='"
																		+ appContextPath
																		+ '/web/live-tracking-attendance?user-key-pid='
																		+ attendance.userPid
																		+ '&date='
																		+ convertDateTime(attendance.plannedDate)
																		+ "' class='icon-container'>"
																		+ (attendance.towerLocation == null ? ""
																				: attendance.towerLocation)
																		+ "</a></td><td>"
																		+ (attendance.remarks == null ? ""
																				: attendance.remarks)
																		+ "</td>"
																		+ content
																		+ "</tr>");
											}
										});
					}
				});
	}

	AttendanceReport.showDatePicker = function() {
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

	AttendanceReport.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showAttendanceImage(pid);
				break;
			}
		}
		el.modal('show');
	}
	
	function showAttendanceImage(pid) {
		$
				.ajax({
					url : attendanceReportContextPath + "/images/" + pid,
					method : 'GET',
					success : function(filledFormFiles) {

						$('#divAttendanceImages').html("");
						$
								.each(
										filledFormFiles,
										function(index, filledFormFile) {
											var table = '<table class="table  table-striped table-bordered"><tr><td style="font-weight: bold;">'
													+ filledFormFile.formName
													+ '</td></tr>';
											$
													.each(
															filledFormFile.files,
															function(index,
																	file) {
																table += '<tr><th>'
																		+ file.fileName
																		+ '</th></tr>';
																table += '<tr><td><img width="100%" src="data:image/png;base64,'
																		+ file.content
																		+ '"/></td></tr>';
															});
											table += '</table>';
											$('#divAttendanceImages')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
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

	function convertDateTime(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('YYYY-MM-DD');
		} else {
			return "";
		}
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format();
		} else {
			return "";
		}
	}

})();