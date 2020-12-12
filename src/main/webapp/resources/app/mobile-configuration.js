// Create a TaskExecutionSaveOffline object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.MobileConfiguration) {
	this.MobileConfiguration = {};
}

(function() {
	'use strict';

	var mobileConfigurationDTO = {
		pid : null,
		companyPid : null,
		companyName : null,
		taskExecutionSaveOfflineValue : true,
		promptAttendanceMarkingvalue : false,
		promptDayPlanUpdate : false,
		promptMasterDataUpdate : false,
		attendanceMarkingRequired : false,
		dayPlanDownloadRequired : false,
		masterDataUpdateRequired : false,
		buildDueDetail : false,
		includeAddressInAccountList : false,
		showAllActivityCount : false,
		createTerritory : false,
		realTimeProductPriceEnabled : false,
		hasGeoTag : false,
		hasPostDatedVoucher : false,
		promptVehicleMaster : false,
		addNewCustomer : true,
		preventNegativeStock : false,
		voucherNumberGenerationType : 'TYPE_1',
		inventoryVoucherUIType : 'TYPE_1',
		cartType : 'NORMAL',
		kfcEnabled : false,
		gpsMandatory : false,
		enableSecondarySales : false,
		enableAttendanceImage : false,
		smartSearch : false,
		salesOrderDownloadPdf : false,
		findLocation : true,
		enableDynamicUnit : false,
		enableDiscountRoundOffColumn : false,
		stockLocationProducts : false,
		salesOrderAllocation : false
	};

	$(document).ready(function() {

		$('.selectpicker').selectpicker();

		$('#mobilesModal').on('click', function() {
			$("#assignMobileConfigurationsModal").modal("show");
			$("#dbCompany").val("-1");
			$("#dbCompany").prop("disabled", false);
		});

		$('#btnSaveMobileConfigurations').on('click', function() {
			saveMobileConfiguration();
		});

		$('#dbCompany').on('change', function() {
			getMobileConfigurationConfig();
		});

		$('#btnDelete').on('click', function() {
			deleteMobileConfiguration();
		});
	});

	var contextPath = location.protocol + '//' + location.host
			+ location.pathname;

	function saveMobileConfiguration() {

		if ($("#dbCompany").val() == -1) {
			alert("Please select company");
			return;
		}
		mobileConfigurationDTO.companyPid = ($("#dbCompany").val());
		mobileConfigurationDTO.taskExecutionSaveOfflineValue = $("#saveOffline")
				.is(":checked");
		mobileConfigurationDTO.promptAttendanceMarkingvalue = $(
				"#promptAttendance").is(":checked");
		mobileConfigurationDTO.promptDayPlanUpdate = $("#promptDayPlanUpdate")
				.is(":checked");
		mobileConfigurationDTO.promptMasterDataUpdate = $(
				"#promptMasterDataUpdate").is(":checked");
		mobileConfigurationDTO.attendanceMarkingRequired = $(
				"#attendanceMarkingRequired").is(":checked");
		mobileConfigurationDTO.dayPlanDownloadRequired = $(
				"#dayPlanDownloadRequired").is(":checked");
		mobileConfigurationDTO.masterDataUpdateRequired = $(
				"#masterDataUpdateRequired").is(":checked");
		mobileConfigurationDTO.buildDueDetail = $("#buildDueDetails").is(
				":checked");
		mobileConfigurationDTO.includeAddressInAccountList = $(
				"#includeAddressInAccountList").is(":checked");
		mobileConfigurationDTO.showAllActivityCount = $("#showAllActivityCount")
				.is(":checked");
		mobileConfigurationDTO.createTerritory = $("#createTerritory").is(
				":checked");
		mobileConfigurationDTO.realTimeProductPriceEnabled = $(
				"#realTimeProductPriceEnabled").is(":checked");
		mobileConfigurationDTO.hasGeoTag = $("#hasGeoTag").is(":checked");

		mobileConfigurationDTO.hasPostDatedVoucher = $("#hasPostDatedVoucher")
				.is(":checked");
		mobileConfigurationDTO.promptVehicleMaster = $("#promptVehicleMaster")
				.is(":checked");
		mobileConfigurationDTO.addNewCustomer = $("#addNewCustomer").is(
				":checked");
		mobileConfigurationDTO.preventNegativeStock = $("#preventNegativeStock")
				.is(":checked");
		mobileConfigurationDTO.voucherNumberGenerationType = $(
				"#voucherNumberGenerationType").val();
		mobileConfigurationDTO.inventoryVoucherUIType = $(
				"#inventoryVoucherUIType").val();
		mobileConfigurationDTO.cartType = $("#cartType").val();
		mobileConfigurationDTO.kfcEnabled = $("#kfcEnabled").is(":checked");
		mobileConfigurationDTO.gpsMandatory = $("#gpsMandatory").is(":checked");
		mobileConfigurationDTO.enableSecondarySales = $("#enableSecondarySales")
				.is(":checked");
		mobileConfigurationDTO.enableAttendanceImage = $(
				"#enableAttendanceImage").is(":checked");
		mobileConfigurationDTO.smartSearch = $("#smartSearch").is(":checked");
		mobileConfigurationDTO.salesOrderDownloadPdf = $(
				"#salesOrderDownloadPdf").is(":checked");
		mobileConfigurationDTO.findLocation = $("#findLocation").is(":checked");
		mobileConfigurationDTO.enableDynamicUnit = $("#enableDynamicUnit").is(
				":checked");
		mobileConfigurationDTO.enableDiscountRoundOffColumn = $(
				"#enableDiscountRoundOffColumn").is(":checked");
		mobileConfigurationDTO.stockLocationProducts = $(
				"#stockLocationProducts").is(":checked");
		mobileConfigurationDTO.salesOrderAllocation = $(
		"#salesOrderAllocation").is(":checked");
		

		$.ajax({
			url : contextPath,
			method : 'POST',
			contentType : "application/json; charset:utf-8",
			data : JSON.stringify(mobileConfigurationDTO),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function getMobileConfigurationConfig() {
		$('input[type="checkbox"]:checked').prop('checked', false);
		$
				.ajax({
					url : contextPath + "/" + $("#dbCompany").val(),
					method : 'GET',
					success : function(data) {
						if (data == "") {
							$('#saveOffline').prop("checked", true);
							$('#addNewCustomer').prop("checked", true);
							$('#inventoryVoucherUIType').val("TYPE_1");
							$('#voucherNumberGenerationType').val("TYPE_1");
							$('#cartType').val("NORMAL");
						} else {
							$('#saveOffline').prop("checked",
									data.taskExecutionSaveOfflineValue);
							$('#promptAttendance').prop("checked",
									data.promptAttendanceMarkingvalue);
							$('#promptDayPlanUpdate').prop("checked",
									data.promptDayPlanUpdate);
							$('#promptMasterDataUpdate').prop("checked",
									data.promptMasterDataUpdate);
							$('#attendanceMarkingRequired').prop("checked",
									data.attendanceMarkingRequired);
							$("#dayPlanDownloadRequired").prop("checked",
									data.dayPlanDownloadRequired);
							$('#masterDataUpdateRequired').prop("checked",
									data.masterDataUpdateRequired);
							$('#buildDueDetails').prop("checked",
									data.buildDueDetail);
							$('#includeAddressInAccountList').prop("checked",
									data.includeAddressInAccountList);
							$('#showAllActivityCount').prop("checked",
									data.showAllActivityCount);
							$('#createTerritory').prop("checked",
									data.createTerritory);
							$('#realTimeProductPriceEnabled').prop("checked",
									data.realTimeProductPriceEnabled);
							$('#hasGeoTag').prop("checked", data.hasGeoTag);
							$('#hasPostDatedVoucher').prop("checked",
									data.hasPostDatedVoucher);
							$('#promptVehicleMaster').prop("checked",
									data.promptVehicleMaster);
							$('#addNewCustomer').prop("checked",
									data.addNewCustomer);
							$('#preventNegativeStock').prop("checked",
									data.preventNegativeStock);
							$('#voucherNumberGenerationType').val(
									data.voucherNumberGenerationType);
							$('#inventoryVoucherUIType').val(
									data.inventoryVoucherUIType);
							$('#cartType').val(data.cartType);
							$('#kfcEnabled').prop("checked", data.kfcEnabled);
							$('#gpsMandatory').prop("checked",
									data.gpsMandatory);
							$('#enableSecondarySales').prop("checked",
									data.enableSecondarySales);
							$('#enableAttendanceImage').prop("checked",
									data.enableAttendanceImage);
							$('#smartSearch').prop("checked", data.smartSearch);
							$('#salesOrderDownloadPdf').prop("checked",
									data.salesOrderDownloadPdf);
							$('#findLocation').prop("checked",
									data.findLocation);
							$('#enableDynamicUnit').prop("checked",
									data.enableDynamicUnit);
							$('#enableDiscountRoundOffColumn').prop("checked",
									data.enableDiscountRoundOffColumn);
							$('#stockLocationProducts').prop("checked",
									data.stockLocationProducts);
							$('#salesOrderAllocation').prop("checked",
									data.salesOrderAllocation);
							

						}

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}
	var mobileConfigPid = "";
	MobileConfiguration.deleteConfig = function(pid) {
		mobileConfigPid = pid;
		$('#alertMessage').html("Are You Sure...?");
		$('#alertBox').modal("show");
	}

	function deleteMobileConfiguration() {
		$.ajax({
			url : contextPath + "/delete/" + mobileConfigPid,
			method : 'DELETE',
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	MobileConfiguration.edit = function(pid, companyPid) {
		$("#dbCompany").val(companyPid);

		getMobileConfigurationConfig();
		$("#dbCompany").prop("disabled", true);
		$("#assignMobileConfigurationsModal").modal("show");
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;

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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;
	}

	MobileConfiguration.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		syncOperationModel.pid = null; // reset syncOperation model;
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

})();