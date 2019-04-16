// Create a SalesTargetReportSettings object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesTargetReportSettings) {
	this.SalesTargetReportSettings = {};
}

(function() {
	'use strict';

	var salesTargetReportSettingsContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#salesTargetReportSettingsForm");
	var deleteForm = $("#deleteForm");
	var salesTargetReportSettingsModel = {
		pid : null,
		name : null,
		accountWiseTarget : false,
		targetPeriod : null,
		targetType : null,
		mobileUIName : null,
		monthlyAverageWise : false,
		targetSettingType : null

	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		targetSettingType : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateSalesTargetReportSettings(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteSalesTargetReportSettings(e.currentTarget.action);
				});

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				// select all checkbox in table tblPriceLevels
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
				// save assigned target blocks
				$('#btnSaveTargetBlocks').click(function() {
					saveAssignedTargetBlocks();
				});

			});

	function loadAssignedTargetBlocks(pid, type) {

		console.log(type);

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		$(".error-msg").html("");
		$("#divTargetBlock input:checkbox").attr('checked', false);

		$('#tbodyTargetBlock').html(
				"<tr><td colspan='2' align='center'>Please wait...</td></tr>");

		salesTargetReportSettingsModel.pid = pid;
		// clear all check box
		$
				.ajax({
					url : Orderfleet.contextPath
							+ "/web/salesTargetReportSettings/targetBlocks/"
							+ salesTargetReportSettingsModel.pid,
					type : "GET",
					data : {
						targetSettingType : type
					},
					success : function(targetBlocks) {
						console.log(targetBlocks);

						if (!targetBlocks.length > 0) {
							$('#tbodyTargetBlock')
									.html(
											"<tr><td colspan='2' align='center'>No data available</td></tr>");
							return;
						}
						if (targetBlocks) {
							$('#tbodyTargetBlock').html("");
							$
									.each(
											targetBlocks,
											function(index, targetBlock) {
												var checked = "";
												if (targetBlock.description == 'TRUE') {
													checked = "checked"
												}
												$('#tbodyTargetBlock')
														.append(
																"<tr><td><input name='targetBlock' type='checkbox' value="
																		+ targetBlock.pid
																		+ " "
																		+ checked
																		+ "/></td> <td>"
																		+ targetBlock.name
																		+ "</td><td><input type='number' id='txtOrder"
																		+ targetBlock.pid
																		+ "' value="
																		+ targetBlock.sortOrder
																		+ " /></td></tr>");
											});

							// $.each(targetBlocks,function(index, targetBlock)
							// {
							// $("#divTargetBlock input:checkbox[value="+
							// targetBlock.salesTargetBlockPid+
							// "]").prop("checked", true);
							// $("#txtOrder"+
							// targetBlock.salesTargetBlockPid).val(targetBlock.sortOrder);});

						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function saveAssignedTargetBlocks() {
		$(".error-msg").html("");
		var selectedTargetBlocks = [];
		$.each($("input[name='targetBlock']:checked"), function() {
			var targetBlockPid = $(this).val();
			var sortOrder = $("#txtOrder" + targetBlockPid).val();
			selectedTargetBlocks.push({
				salesTargetBlockPid : targetBlockPid,
				sortOrder : sortOrder
			})
		});
		if (selectedTargetBlocks.length == 0) {
			$(".error-msg").html("Please select Target blocks");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : Orderfleet.contextPath
					+ "/web/salesTargetReportSettings/assignTargetBlocks/"
					+ salesTargetReportSettingsModel.pid,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedTargetBlocks),
			success : function(status) {
				$("#assignAccountTypesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function createUpdateSalesTargetReportSettings(el) {
		salesTargetReportSettingsModel.name = $('#field_name').val();

		var radioValue = $("input[name='accountWiseTarget']:checked").val();
		salesTargetReportSettingsModel.accountWiseTarget = radioValue;

		var amount = $('#field_createDynamicLabel_amount').prop("checked");
		var volume = $('#field_createDynamicLabel_volume').prop("checked");

		if (amount == true && volume == true) {
			salesTargetReportSettingsModel.targetType = "AMOUNT_VOLUME";
		}
		if (amount == false && volume == true) {
			salesTargetReportSettingsModel.targetType = "VOLUME";
		}
		if (amount == true && volume == false) {
			salesTargetReportSettingsModel.targetType = "AMOUNT";
		}
		if (amount == false && volume == false) {
			salesTargetReportSettingsModel.targetType = "NONE";
		}
		salesTargetReportSettingsModel.targetPeriod = $(
				'#field_targetPeriod option:selected').val();

		var mobileName = $('#field_mobileUIName option:selected').val();
		if (mobileName != "SELECT") {
			salesTargetReportSettingsModel.mobileUIName = $(
					'#field_mobileUIName option:selected').val();
		}
		salesTargetReportSettingsModel.monthlyAverageWise = $(
				'#field_monthlyAverageWise').prop("checked");

		salesTargetReportSettingsModel.targetSettingType = $(
				'#field_targetSettingType').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(salesTargetReportSettingsModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editSalesTargetReportSettings(id) {
		$
				.ajax({
					url : salesTargetReportSettingsContextPath + "/" + id,
					method : 'GET',
					success : function(data) {

						console.log(data);
						$('#field_name').val(data.name);

						if (data.accountWiseTarget) {
							$('#field_accountWiseTarget_account').prop(
									"checked", true);
						} else {
							$('#field_accountWiseTarget_user').prop("checked",
									true);
						}
						if (data.targetType == "AMOUNT_VOLUME") {
							$('#field_createDynamicLabel_amount').prop(
									"checked", true);
							$('#field_createDynamicLabel_volume').prop(
									"checked", true);
						}
						if (data.targetType == "AMOUNT") {
							$('#field_createDynamicLabel_amount').prop(
									"checked", true);
							$('#field_createDynamicLabel_volume').prop(
									"checked", false);
						}
						if (data.targetType == "VOLUME") {
							$('#field_createDynamicLabel_volume').prop(
									"checked", true);
							$('#field_createDynamicLabel_amount').prop(
									"checked", false);
						}

						$('#field_monthlyAverageWise').prop("checked",
								data.monthlyAverageWise);

						$('#field_targetPeriod').val(data.targetPeriod);
						if (data.mobileUIName != null) {
							$('#field_mobileUIName').val(data.mobileUIName);
						}

						$('#field_targetSettingType').val(
								data.targetSettingType);

						// set pid
						salesTargetReportSettingsModel.pid = data.pid;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function deleteSalesTargetReportSettings(actionurl, id) {
		$.ajax({
			url : actionurl,
			method : 'DELETE',
			success : function(data) {
				onDeleteSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = salesTargetReportSettingsContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = salesTargetReportSettingsContextPath;
	}

	SalesTargetReportSettings.showModalPopup = function(el, id, action, type) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showSalesTargetReportSettings(id);
				break;
			case 1:
				editSalesTargetReportSettings(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', salesTargetReportSettingsContextPath
						+ "/" + id);
				break;
			case 3:
				loadAssignedTargetBlocks(id, type);
				break;
			}
		}
		el.modal('show');
	}

	SalesTargetReportSettings.onchangeStartMonth = function() {
		var value = $('#field_startMonth').val();
		if (value == 0) {
			$("#field_startMonthMinus").attr("readonly", false);
		} else {
			$("#field_startMonthMinus").val(0);
			$("#field_startMonthMinus").attr("readonly", true);
		}
	}

	SalesTargetReportSettings.onchangeEndMonth = function() {
		var value = $('#field_endMonth').val();
		if (value == 0) {
			$("#field_endMonthMinus").attr("readonly", false);
		} else {
			$("#field_endMonthMinus").val(0);
			$("#field_endMonthMinus").attr("readonly", true);
		}
	}

	function searchTable(inputVal) {
		var table = $('#tbodyTargetBlock');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
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

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		salesTargetReportSettingsModel.pid = null; // reset
		// salesTargetReportSettings
		// model;
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