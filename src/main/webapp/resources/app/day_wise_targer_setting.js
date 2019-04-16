// Create a DayWiseUserTargetSetting object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DayWiseUserTargetSetting) {
	this.DayWiseUserTargetSetting = {};
}

(function() {
	'use strict';

	$("#txtToDate").datepicker({
		minDate : 0,
		dateFormat : "dd-mm-yy"
	});
	$("#txtFromDate").datepicker({
		minDate : 0,
		dateFormat : "dd-mm-yy"
	});

	$("#txtStartDate").datepicker({
		dateFormat : "dd-mm-yy"
	});
	$("#txtEndDate").datepicker({
		dateFormat : "dd-mm-yy"
	});

	var dayWiseUserTargetSettingContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#dayWiseUserTargetSettingForm");
	var deleteForm = $("#deleteForm");
	var dayWiseUserTargetSettingModel = {
		pid : null,
		fromDate : null,
		toDate : null,
		salesTargetGroupPid : null,
		userPid : null,
		amount : 0,
		volume : 0
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnViewDayWiseUserTargetSetting').on('click', function() {
			$("#btncommonQTY").fadeOut("slow");
			viewDayWiseUserTargetSetting();
		});

		$('#btnSaveDayWiseUserTargetSetting').on('click', function() {
			createUpdateDayWiseUserTargetSetting();
		});

		$('#myFormSubmit').on('click', function() {
			saveDayWiseUserTargetSetting();
		});

		$('#btncommonQTY').on('click', function() {
			$("#defaultValueModal").modal('show');
		});
		$('#btnSetValue').on('click', function() {
			setvaluestoTextBoxes();
		});
		$('#btnApply').on('click', function() {
			loadDatasFromServerToTable();
		});

	});

	function loadDatasFromServerToTable() {

		if ($('#dbEmployee').val() == "no") {
			alert("please select user");
			return;
		}
		if ($('#dbDataSalesTargetGroup').val() == "no") {
			alert("please select sales target group");
			return;
		}
		if ($('#txtStartDate').val() == "") {
			alert("please select from date");
			return;
		}
		if ($('#txtEndDate').val() == "") {
			alert("please select to date");
			return;
		}

		$('#tbMainDayWiseUserTargetSetting').html(
				"<tr><td colspan='5' align='center'>loading...</td></tr>");
		$
				.ajax({
					url : dayWiseUserTargetSettingContextPath + "/filter",
					method : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						salesTargetGroupPid : $("#dbDataSalesTargetGroup")
								.val(),
						fromDate : $("#txtStartDate").val(),
						toDate : $("#txtEndDate").val()
					},
					success : function(salesTargetGroupUserTargets) {
						$('#tbMainDayWiseUserTargetSetting').html("");

						if (salesTargetGroupUserTargets.length == 0) {
							$('#tbMainDayWiseUserTargetSetting')
									.html(
											"<tr><td colspan='5' align='center'>no data available</td></tr>");
						}

						$
								.each(
										salesTargetGroupUserTargets,
										function(index,
												salesTargetGroupUserTarget) {
											$('#tbMainDayWiseUserTargetSetting')
													.append(
															"<tr><td>"
																	+ salesTargetGroupUserTarget.userName
																	+ "</td><td>"
																	+ salesTargetGroupUserTarget.salesTargetGroupName
																	+ "</td><td>"
																	+ salesTargetGroupUserTarget.fromDate
																	+ "</td><td>"
																	+ salesTargetGroupUserTarget.day
																	+ "</td><td>"
																	+ salesTargetGroupUserTarget.volume
																	+ "</td></tr>");
										});

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function setvaluestoTextBoxes() {
		var defaultvalue = $("#field_defaultValue").val();
		$('#tbDayWiseUserTargetSetting tr input[type= number]').each(
				function() {
					$(this).val(defaultvalue);
				});
		$("#defaultValueModal").modal('hide');
	}

	function saveDayWiseUserTargetSetting() {

		var dayWiseUserTargetSettingArray = [];

		$("#tbleDayWiseUserTarget tr:gt(0)").each(
				function() {
					var this_row = $(this);
					var day = $.trim(this_row.find('td:eq(1)').html());
					var date = $.trim(this_row.find('td:eq(0)').html());
					var volId = $.trim(this_row.find('td:eq(3)').html());
					var pid = $.trim(this_row.find('td:eq(4)').html());
					if (day != 'SUNDAY') {
						dayWiseUserTargetSettingModel.fromDate = date;
						dayWiseUserTargetSettingModel.toDate = date;
						dayWiseUserTargetSettingModel.salesTargetGroupPid = $(
								'#dbsalesTargetGroup').val();
						dayWiseUserTargetSettingModel.userPid = $('#dbUserId')
								.val();
						dayWiseUserTargetSettingModel.volume = $('#' + volId)
								.val();
						dayWiseUserTargetSettingModel.pid = pid;
						dayWiseUserTargetSettingArray
								.push(dayWiseUserTargetSettingModel);
						dayWiseUserTargetSettingModel = {};
					}

				});

		$.ajax({
			method : 'POST',
			url : dayWiseUserTargetSettingContextPath,
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(dayWiseUserTargetSettingArray),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function viewDayWiseUserTargetSetting() {
		$('#tbDayWiseUserTargetSetting')
				.html(
						"<tr><td colspan='3' align='center'>No data available</td></tr>");
		$("#myModal").modal('show');
	}
	function createUpdateDayWiseUserTargetSetting() {

		if ($('#dbUserId').val() == "no") {
			alert("please select employee");
			return;
		}
		if ($('#dbsalesTargetGroup').val() == "no") {
			alert("please select sales target group");
			return;
		}

		$('#tbDayWiseUserTargetSetting').html(
				"<tr><td colspan='3' align='center'>loading...</td></tr>");

		$
				.ajax({
					url : dayWiseUserTargetSettingContextPath + "/load",
					method : 'GET',
					data : {
						employeePid : $("#dbUserId").val(),
						salesTargetGroupPid : $("#dbsalesTargetGroup").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(salesTargetGroupUserTargets) {
						$('#tbDayWiseUserTargetSetting').html("");
						var uniqueno = 0;
						$
								.each(
										salesTargetGroupUserTargets,
										function(index,
												salesTargetGroupUserTarget) {
											uniqueno = uniqueno + 1;
											var backStyle = "";
											var read = "";
											if (salesTargetGroupUserTarget.day == "SUNDAY") {
												backStyle = "#fbcfcf";
												read = "readonly";
											}

											$('#tbDayWiseUserTargetSetting')
													.append(
															"<tr style='background-color: "
																	+ backStyle
																	+ ";'><td>"
																	+ salesTargetGroupUserTarget.fromDate
																	+ "</td><td>"
																	+ salesTargetGroupUserTarget.day
																	+ "</td><td><input type='number' class='form-control colCategory' name='volume' id='"
																	+ uniqueno
																	+ "' placeholder='Volume' value='"
																	+ salesTargetGroupUserTarget.volume
																	+ "' "
																	+ read
																	+ "></td><td style='display:none;'>"
																	+ uniqueno
																	+ "</td><td style='display:none;'>"
																	+ salesTargetGroupUserTarget.pid
																	+ "</td></tr>");
										});
						$("#btncommonQTY").fadeIn("slow");
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function onSaveSuccess(result) {
		// / / reloading page to see the updated data
		window.location = dayWiseUserTargetSettingContextPath;
	}

	function onDeleteSuccess(result) {
		// / / reloading page to see the updated data
		window.location = dayWiseUserTargetSettingContextPath;
	}

	DayWiseUserTargetSetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDayWiseUserTargetSetting(id);
				break;
			case 1:
				editDayWiseUserTargetSetting(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dayWiseUserTargetSettingContextPath
						+ "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // / / clear form fields
		createEditForm.validate().resetForm(); // / / clear validation messages
		createEditForm.attr('method', 'POST'); // / / set default method
		dayWiseUserTargetSettingModel.pid = null; // / / reset
		// / / dayWiseUserTargetSetting
		// / / model;
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function onError(httpResponse, exception) {
		var i;
		switch (httpResponse.status) {
		// / / connection refused, server not reachable
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