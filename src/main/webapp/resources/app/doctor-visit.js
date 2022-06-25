if (!this.DoctorVisit) {
	this.DoctorVisit = {};
}

(function() {
	'use strict';

	var doctorVisitDetailReportPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document)
			.ready(
					function() {

						$("#txtToDate").datepicker({
							dateFormat : "dd-mm-yy"
						});
						$("#txtFromDate").datepicker({
							dateFormat : "dd-mm-yy"
						});
						$('#btnSearch').click(function() {
							searchTable($("#search").val());
						});

						// GeoLocationVariance.filter();
//						$('#btnDownload')
//								.on(
//										'click',
//										function() {
//											var tbltblKilometerCalculation = $("#tblKilometerCalculation tbody");
//											if (tbltblKilometerCalculation
//													.children().length == 0) {
//												alert("no values available");
//												return;
//											}
//											var excelName = "kilometer_report";
//											var instance = $(
//													'#tblKilometerCalculation')
//													.tableExport({
//														formats : [ 'xlsx' ],
//														filename : excelName,
//														exportButtons : false
//													});
//											var exportData = instance
//													.getExportData()['tblKilometerCalculation']['xlsx'];
//											instance.export2file(
//													exportData.data,
//													exportData.mimeType,
//													exportData.filename,
//													exportData.fileExtension);
//										});

					});

	
	DoctorVisit.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}
	}

	DoctorVisit.filter = function() {
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		$('#tBodyDoctorVisit').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : doctorVisitDetailReportPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbUser").val(),
						// accountProfilePid : $("#dbAccountProfile").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate :$("#txtFromDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")
					},
					success : function(data) {
						
						$('#tBodyDoctorVisit').html("");
						if (data.length == 0) {
							
							$('#tBodyDoctorVisit')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}

						$
								.each(
										data,
										function(index, doctorVisitDTO) {
											

											$('#tBodyDoctorVisit')
													.append(
															"<tr><td>"
																	+ doctorVisitDTO.empName
																	+ "</td><td>"
																	+ doctorVisitDTO.doctorVisitNo
																	+ "</td><td class='tableexport-string target'>"
																	+ doctorVisitDTO.attendance
																	+ "</td><td>"
																	+ doctorVisitDTO.startTime
																	+ "</td><td>"
																	+ doctorVisitDTO.endTime
																	+ "</td><td>"
																	+ doctorVisitDTO.route
																	+ "</td><td>"
																	+  doctorVisitDTO.totalDistance
																			.toFixed(2)
																	+ "</td><td>"
																	+ doctorVisitDTO.totalTravelled
																	+ "</td><td>"
																	+ doctorVisitDTO.fare
																	+ "</td><td>"
																	+ doctorVisitDTO.travelExpense
																	+ "</td><td>"
																	+ doctorVisitDTO.foodExpense
																	+ "</td><td>"
																	+ doctorVisitDTO.totalExpense 
																	+ "</td></tr>");

											

										});

						
						

					}
				});
	}

	

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = doctorVisitDetailReportPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = doctorVisitDetailReportPath;
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