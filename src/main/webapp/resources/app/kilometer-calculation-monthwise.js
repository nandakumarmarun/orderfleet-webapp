if (!this.KilometerCalculation) {
	this.KilometerCalculation = {};
}

(function() {
	'use strict';

	var kilometerCalculationPath = location.protocol + '//' + location.host
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
						$('#btnDownload')
								.on(
										'click',
										function() {
											var tbltblKilometerCalculation = $("#tblKilometerCalculation tbody");
											if (tbltblKilometerCalculation
													.children().length == 0) {
												alert("no values available");
												return;
											}
											var excelName = "kilometer_report";
											var instance = $(
													'#tblKilometerCalculation')
													.tableExport({
														formats : [ 'xlsx' ],
														filename : excelName,
														exportButtons : false
													});
											var exportData = instance
													.getExportData()['tblKilometerCalculation']['xlsx'];
											instance.export2file(
													exportData.data,
													exportData.mimeType,
													exportData.filename,
													exportData.fileExtension);
										});

					});

	/*
	 * function downloadXls(excelName) { var table2excel = new Table2Excel();
	 * table2excel.export(document.getElementById('tblKilometerCalculation'),excelName); }
	 */

	// $('#dbUser').on('change', function() {
	// $('#dbAccountProfile').html("Loading...");
	//			
	// $
	// .ajax({
	// url : kilometerCalculationPath + "/get-account-profile",
	// type : 'GET',
	// data : {
	// userPid : $("#dbUser").val(),
	// },
	// success : function(data) {
	// $('#dbAccountProfile').html("");
	// if (data.length == 0) {
	// $('#dbAccountProfile').append($('<option>', {
	// val: "no",
	// text : "No Account Profiles"
	// }));
	// return;
	// }
	// $('#dbAccountProfile').append($('<option>', {
	// val: "no",
	// text : "All Account Profile"
	// }));
	// $.each(data, function (index,accountProfile ) {
	// $('#dbAccountProfile').append($('<option>', {
	// val: accountProfile.pid,
	// text : accountProfile.name
	// }));
	// });
	// },
	//				
	// });
	// });
	KilometerCalculation.showDatePicker = function() {
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

	KilometerCalculation.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyKilometerCalculation').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : kilometerCalculationPath + "/filter",
					type : 'GET',
					data : {
						userPid : $("#dbUser").val(),
						// accountProfilePid : $("#dbAccountProfile").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(data) {
						var totalDistance = 0;
						var totalEntries = 0;
						var attendanceEntries = 0;
						var farePerKilometre = 0;
						$('#tBodyKilometerCalculation').html("");
						if (data.length == 0) {
							$('#lblTotalKilo').text(totalDistance + "KM");
							$('#lblSub').text(totalEntries);
							$('#lblTotalFare').text(0);
							$('#tBodyKilometerCalculation')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}

						$
								.each(
										data,
										function(index, kilometerCalc) {
											let lastlocation = kilometerCalc.location == null ? ""
													: kilometerCalc.location;

												var AccountName = "";
												if(kilometerCalc.accountProfileName == null){
												   if(kilometerCalc.attendence == true){
                                                    	AccountName = "Attendance"
                                                   	}
                                                   if(kilometerCalc.punchOut == true){
                                                        AccountName = "Punch Out"
                                                   }
												}
												else{
												   AccountName = kilometerCalc.accountProfileName
												}


											if (lastlocation == "No Location") {
												lastlocation = "<span class='btn btn-success'  id='"
														+ kilometerCalc.taskExecutionPid
														+ "' onClick='KilometerCalculation.getLocation(this)' >get location</span>";

											}


											$('#tBodyKilometerCalculation')
													.append(
															"<tr><td>"
																	+ kilometerCalc.employeeName
																	+ "</td><td>"
																	+ AccountName
//																	+ (kilometerCalc.isAttendence == true ? "Attendance"
//																			: kilometerCalc.accountProfileName)
																	+ "</td><td class='tableexport-string target'>"
																	+ kilometerCalc.punchingDate
																	+ "</td><td class='tableexport-string target'>"
																	+ kilometerCalc.date
																	+ "</td><td class='tableexport-string target'>"
																	+ kilometerCalc.punchingTime
																	+ "</td><td>"
																	+ lastlocation
																	+ "</td><td>"
																	+ kilometerCalc.metres
																			.toFixed(2)
																	+ "</td><td>"
																	+ kilometerCalc.kilometre
																			.toFixed(2)
																	+ "</td></tr>");

											totalDistance += kilometerCalc.kilometre;
											totalEntries += 1;
											if (kilometerCalc.location === 'Attendance') {
												attendanceEntries += 1;
											}

										});

						farePerKilometre = $(dbFareType).val();
						if (farePerKilometre == 'no') {
							farePerKilometre = 0;
						}
						$('#lblTotalKilo')
								.text(totalDistance.toFixed(2) + "KM");
						$('#lblSub').text(totalEntries - attendanceEntries);
						$('#lblTotalFare').text(
								(totalDistance * farePerKilometre).toFixed(2));

					}
				});
	}

	KilometerCalculation.getLocation = function(obj) {
		var pid = $(obj).attr("id");
		$(obj).html("loading...");
		$.ajax({
			url : kilometerCalculationPath + "/updateLocationExeTask/" + pid,
			method : 'GET',
			success : function(data) {
				$(obj).html(data.location);
				$(obj).removeClass("btn-success");
				$(obj).removeClass("btn");

				KilometerCalculation.filter();
			},
			error : function(xhr, error,data) {
			    console.log(xhr)
			    console.log(data)
			    alert(xhr.responseJSON.message + "\n Try Again Later")
				onError(xhr, error);
				var button = $('#' + pid);
				button.attr("disabled", 'disabled');
				button.prop('pointer-events','none');
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = kilometerCalculationPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = kilometerCalculationPath;
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