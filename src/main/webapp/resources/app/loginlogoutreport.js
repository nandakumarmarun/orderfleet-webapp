// Create a AttendanceReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.LoginLogOut) {
	this.LoginLogOut = {};
}
(function() {
	'use strict';

	var loginlogoutReportContextPath = location.protocol + '//' + location.host
		+ location.pathname;

	$(document).ready(function() {
		var employeePid = getParameterByName('user-key-pid');
		getEmployees(employeePid); // common js

		$("#txtToDate").datepicker({
			dateFormat: "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat: "dd-mm-yy"
		});

		let empPid = getParameterByName('empPid');
		if (empPid) {
			$('#dbEmployee').val(empPid);
		}
		// load today data
		LoginLogOut.filter();

		// table search
		$('#search').keyup(function() {
			searchTable($(this).val());
		});

		$('#btnDownload').on('click', function() {
			var tblLoginLogOut = $("#tblLoginLogOut tbody");
			if (tblLoginLogOut.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblLoginLogOut[0].textContent == "No data available") {
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
		var clonedTable = $("#tblLoginLogOut").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "LoginLogOutReport";

		clonedTable.table2excel({
			// exclude CSS class
			// exclude : ".odd .even",
			// name : "Dynamic Document Form",
			filename: excelName, // do not include extension
			// fileext : ".xls",
			// exclude_img : true,
			// exclude_links : true,
			// exclude_inputs : true
		});
	}

	LoginLogOut.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyAttendanceReport').html(
			"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url: loginlogoutReportContextPath + "/filter",
				type: 'GET',
				data: {
					employeePid: $('#dbEmployee').val(),
					attStatus: $("#dbAttStatus").val(),
					filterBy: $("#dbDateSearch").val(),
					fromDate: $("#txtFromDate").val(),
					toDate: $("#txtToDate").val(),
					inclSubordinate: $('#inclSubOrdinates').is(":checked")
				},
				success: function(loginLogOutList) {
					$('#tBodyLoginLogOut').html("");
					if (loginLogOutList.length == 0) {
						$('#tBodyLoginLogOut')
							.html(
								"<tr><td colspan='6' align='center'>No data available</td></tr>");
						return;
					}
					$
						.each(
							loginLogOutList,
							function(index, loginLogOut) {
								
								var content = "";
								var pcontent = "";
                               var mockLocation = "";
                               if(loginLogOut.mockLocationStatus)
                               {
                               mockLocation = "Enabled"
                               }
                               else

								if (loginLogOut.imageButtonVisibleAtt) {
									content = "<td><button type='button' class='btn btn-info' onclick='LoginLogOut.showModalPopup($(\"#imagesModal\"),\""
											+ loginLogOut.attendancePid
											+ "\",0);'>Attendance Images</button></td>";
								} else {
									content = "<td></td>";
								}
								
								if (loginLogOut.imageButtonVisiblePun) {
									pcontent = "<td><button type='button' class='btn btn-info' onclick='LoginLogOut.showModalPopupPunchOut($(\"#imagesModal\"),\""
											+ loginLogOut.punchoutPid
											+ "\",0);'>Punchout Images</button></td>";
								} else {
									pcontent = "<td></td>";
								}
								$('#tBodyLoginLogOut')
									.append(
										"<tr><td>"
										+ formatDate(
											loginLogOut.attendanceDay,
											'dddd, MMM Do YYYY')
										+ "</td><td>"
										+ loginLogOut.employeeName
										+ "</td><td>"
										+ loginLogOut.attendanceStatus
										+ "</td><td>"
										+ formatDate(
											loginLogOut.plannedDate,
											'MMM DD YYYY, h:mm:ss a')
										+ "</td><td>"
										+ formatDate(
											loginLogOut.createdDate,
											'MMM DD YYYY, h:mm:ss a')
										+ "</td><td>"
										+ loginLogOut.attendaceOdooMeter
										+ "</td><td>"
										+ (loginLogOut.remarks == null ? ""
											: loginLogOut.remarks)
										+ "</td><td>"
										+ (loginLogOut.vehicleType == null ? ""
											: loginLogOut.vehicleType)	
										+ "</td><td>"
										+ loginLogOut.punchoutStatus
										+ "</td><td>"
										+ formatDate(
											loginLogOut.punchoutClientDate,
											'MMM DD YYYY, h:mm:ss a')
										+ "</td><td>"
										+ formatDate(
											loginLogOut.punchoutServerDate,
											'MMM DD YYYY, h:mm:ss a')
										+ "</td><td>"
										+ loginLogOut.punchoutOdoMeter
										+ "</td><td>"
										+ (loginLogOut.punchoutRemarks == null ? ""
											: loginLogOut.punchoutRemarks)
										+ "</td><td>"
										+ loginLogOut.totalOdoMeter
										+ "</td><td>"
                                        + loginLogOut.noOfVisits
                                        + "</td><td>"
                                        + mockLocation
                                        + "</td>"
										+ content
										+ pcontent
										+ "</tr>");
							});
				}
			});
	}

	LoginLogOut.showDatePicker = function() {
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

	LoginLogOut.showModalPopup = function(el, pid, action) {
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
				url: loginlogoutReportContextPath + "/images/" + pid,
				method: 'GET',
				success: function(filledFormFiles) {

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
				error: function(xhr, error) {
					onError(xhr, error);
				}
			});
	}

	LoginLogOut.showModalPopupPunchOut = function(el, pid, action) {
		if (pid) {
			switch (action) {
				case 0:
					showPunchOutImage(pid);
					break;
			}
		}
		el.modal('show');
	}

	function showPunchOutImage(pid) {
		$
			.ajax({
				url: loginlogoutReportContextPath + "/PunchOutImages/" + pid,
				method: 'GET',
				success: function(filledFormFiles) {

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
				error: function(xhr, error) {
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