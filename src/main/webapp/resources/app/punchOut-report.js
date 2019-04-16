// Create a PunchOutReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PunchOutReport) {
	this.PunchOutReport = {};
}
(function() {
	'use strict';

	var punchOutReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// load today data
		PunchOutReport.filter();

		// table search
		$('#search').keyup(function() {
			searchTable($(this).val());
		});

		$('#btnDownload').on('click', function() {
			var tblPunchOutReport = $("#tblPunchOutReport tbody");
			if (tblPunchOutReport.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblPunchOutReport[0].textContent == "No data available") {
				alert("no values available");
				return;
			}

			downloadXls();
		});
	});

	function downloadXls() {
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblPunchOutReport").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "punchOutReport";

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

	PunchOutReport.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyPunchOutReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");

		$
				.ajax({
					url : punchOutReportContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(punchOutList) {
						$('#tBodyPunchOutReport').html("");
						if (punchOutList.length == 0) {
							$('#tBodyPunchOutReport')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										punchOutList,
										function(index, punchOut) {

											$('#tBodyPunchOutReport')
													.append(
															"<tr><td>"
																	+ punchOut.employeeName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(punchOut.punchOutDate)
																	+ "</td><td>"
																	+ convertDateTimeFromServer(punchOut.createdDate)
																	+ "</td><td>"
																	+ (punchOut.punchOutRemarks == null ? ""
																			: punchOut.punchOutRemarks)
																	+ "</td><td>"
																	+ (punchOut.location == null ? ""
																			: punchOut.location)
																	+ "</td></tr>");

										});
					}
				});
	}

	PunchOutReport.showDatePicker = function() {
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
		var table = $('#tBodyPunchOutReport');
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
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function onError(httpResponse, exception) {
		var i;
		switch (httpResponse.status) {
		// connection refused, server not reachable
		case 0:
			addErrorAlert('Server not reachable', 'error.server.not.reachable');
			break;
		case 400:
			var errorHeader = httpResponse
					.getResponseHeader('X-orderfleetwebApp-error');
			var entityKey = httpResponse
					.getResponseHeader('X-orderfleetwebApp-params');
			if (errorHeader) {
				var entityName = entityKey;
				addErrorAlert(errorHeader, errorHeader, {
					entityName : entityName
				});
			} else if (httpResponse.responseText) {
				var data = JSON.parse(httpResponse.responseText);
				if (data && data.fieldErrors) {
					for (i = 0; i < data.fieldErrors.length; i++) {
						var fieldError = data.fieldErrors[i];
						var convertedField = fieldError.field.replace(
								/\[\d*\]/g, '[]');
						var fieldName = convertedField.charAt(0).toUpperCase()
								+ convertedField.slice(1);
						addErrorAlert(
								'Field ' + fieldName + ' cannot be empty',
								'error.' + fieldError.message, {
									fieldName : fieldName
								});
					}
				} else if (data && data.message) {
					addErrorAlert(data.message, data.message, data);
				} else {
					addErrorAlert(data);
				}
			} else {
				addErrorAlert(exception);
			}
			break;
		default:
			if (httpResponse.responseText) {
				var data = JSON.parse(httpResponse.responseText);
				if (data && data.description) {
					addErrorAlert(data.description);
				} else if (data && data.message) {
					addErrorAlert(data.message);
				} else {
					addErrorAlert(data);
				}
			} else {
				addErrorAlert(exception);
			}
		}
	}
})();