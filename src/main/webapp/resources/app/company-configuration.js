// Create a TaskExecutionSaveOffline object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.CompanyConfiguration) {
	this.CompanyConfiguration = {};
}

(function() {
	'use strict';

	$(document).ready(function() {

		$('.selectpicker').selectpicker();

		$('#companysModal').on('click', function() {
			$("#assignCompanyConfigurationsModal").modal("show");
		});
		$('#btnSaveCompanyConfigurations').on('click', function() {
			saveCompanyConfiguration();
		});

		$('#dbCompany').on('change', function() {
			getCompanyConfigurationConfig();
		});

		$('#btnDelete').on('click', function() {
			deleteCompanyConfiguration();
		});
	});

	var contextPath = location.protocol + '//' + location.host
			+ location.pathname;

	function saveCompanyConfiguration() {
		if ($("#dbCompany").val() == -1) {
			alert("Please select company");
			return;
		}
		console.log("H..............");
		var distanceTraveled = $("#distanceTraveled").is(":checked");
		var locationVariance = $("#locationVariance").is(":checked");
		var interimSave = $("#interimSave").is(":checked");
		var refreshProductGroupProduct = $("#refreshProductGroupProduct").is(
				":checked");
		var stageChangeAccountingVoucher = $('#stageChangeAccountingVoucher')
				.is(":checked");
		var newCustomerAlias = $('#newCustomerAlias').is(":checked");
		var chatReply = $('#chatReply').is(":checked");
		var salesPdfDownload = $('#salesPdfDownload').is(":checked");
		var visitBasedTransaction = $('#visitBasedTransaction').is(":checked");
		var salesManagement = $('#salesManagement').is(":checked");
		var receiptsManagement = $('#receiptsManagement').is(":checked");
		var salesEditEnabled = $('#salesEditEnabled').is(":checked");
		var gpsVarianceQuery = $('#gpsVarianceQuery').is(":checked");
		var sendSalesOrderEmail = $('#sendSalesOrderEmail').is(":checked");
		var sendSalesOrderSap = $('#sendSalesOrderSap').is(":checked");
		var piecesToQuantity = $('#piecesToQuantity').is(":checked");
		var sendSalesOrderOdoo = $('#sendSalesOrderOdoo').is(":checked");
		var sendTransactionsSapPravesh = $('#sendTransactionsSapPravesh').is(
				":checked");
		var addCompoundUnit = $('#addCompoundUnit').is(":checked");
		var updateStockLocation = $('#updateStockLocation').is(":checked");
		var sendToOdoo = $('#sendToOdoo').is(":checked");
		var productGroupTax = $('#productGroupTax').is(":checked");
		var aliasToName = $('#aliasToName').is(":checked");
		var descriptionToName = $('#descriptionToName').is(":checked");
		var stockApi = $('#stockApi').is(":checked");
		var employeeCreateBtn =  $('#employeeCreateBtn').is(":checked");
		/* var findLocation = $('#findLocation').is(":checked"); */
		$.ajax({
			url : contextPath,
			method : 'POST',
			data : {
				companyPid : $("#dbCompany").val(),
				distanceTraveled : distanceTraveled,
				locationVariance : locationVariance,
				interimSave : interimSave,
				refreshProductGroupProduct : refreshProductGroupProduct,
				stageChangeAccountingVoucher : stageChangeAccountingVoucher,
				newCustomerAlias : newCustomerAlias,
				chatReply : chatReply,
				salesPdfDownload : salesPdfDownload,
				visitBasedTransaction : visitBasedTransaction,
				salesManagement : salesManagement,
				receiptsManagement : receiptsManagement,
				salesEditEnabled : salesEditEnabled,
				gpsVarianceQuery : gpsVarianceQuery,
				sendSalesOrderEmail : sendSalesOrderEmail,
				sendSalesOrderSap : sendSalesOrderSap,
				piecesToQuantity : piecesToQuantity,
				sendSalesOrderOdoo : sendSalesOrderOdoo,
				sendTransactionsSapPravesh : sendTransactionsSapPravesh,
				addCompoundUnit : addCompoundUnit,
				updateStockLocation : updateStockLocation,
				sendToOdoo : sendToOdoo,
				productGroupTax : productGroupTax,
				aliasToName : aliasToName,
				descriptionToName : descriptionToName,
				stockApi : stockApi,
				employeeCreateBtn : employeeCreateBtn
			/* findLocation : findLocation */
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function getCompanyConfigurationConfig() {
		$('input[type="checkbox"]:checked').prop('checked', false);
		$
				.ajax({
					url : contextPath + "/" + $("#dbCompany").val(),
					method : 'GET',
					success : function(data) {
						$('#distanceTraveled').prop("checked",
								data.distanceTraveled);
						$('#locationVariance').prop("checked",
								data.locationVariance);
						$('#interimSave').prop("checked", data.interimSave);
						$('#refreshProductGroupProduct').prop("checked",
								data.refreshProductGroupProduct);
						$("#stageChangeAccountingVoucher").prop("checked",
								data.stageChangeAccountingVoucher);
						$("#newCustomerAlias").prop("checked",
								data.newCustomerAlias);
						$("#chatReply").prop("checked", data.chatReply);
						$("#salesPdfDownload").prop("checked",
								data.salesPdfDownload);
						$("#visitBasedTransaction").prop("checked",
								data.visitBasedTransaction);
						$("#salesManagement").prop("checked",
								data.salesManagement);
						$("#receiptsManagement").prop("checked",
								data.receiptsManagement);
						$("#salesEditEnabled").prop("checked",
								data.salesEditEnabled);
						$("#gpsVarianceQuery").prop("checked",
								data.gpsVarianceQuery);
						$("#sendSalesOrderEmail").prop("checked",
								data.sendSalesOrderEmail);
						$("#sendSalesOrderSap").prop("checked",
								data.sendSalesOrderSap);
						$("#piecesToQuantity").prop("checked",
								data.piecesToQuantity);
						$("#sendSalesOrderOdoo").prop("checked",
								data.sendSalesOrderOdoo);
						$("#sendTransactionsSapPravesh").prop("checked",
								data.sendTransactionsSapPravesh);
						$("#addCompoundUnit").prop("checked",
								data.addCompoundUnit);
						$("#updateStockLocation").prop("checked",
								data.updateStockLocation);
						$("#sendToOdoo").prop("checked", data.sendToOdoo);
						$("#productGroupTax").prop("checked",
								data.enableProductGroupTax);
						$("#aliasToName").prop("checked",
								data.aliasToName);
						$("#descriptionToName").prop("checked",
								data.descriptionToName);
						
						$("#stockApi").prop("checked",
								data.stockApi);
						$("#employeeCreateBtn").prop("checked",
								data.employeeCreateBtn);
						
						/*
						 * $("#findLocation").prop("checked",
						 * data.findLocation);
						 */
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}
	var companyPid = "";
	CompanyConfiguration.deletes = function(pid) {
		companyPid = pid;
		$('#alertMessage').html("Are You Sure...?");
		$('#alertBox').modal("show");
	}

	function deleteCompanyConfiguration() {

		$.ajax({
			url : contextPath + "/delete/" + companyPid,
			method : 'DELETE',
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
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

	CompanyConfiguration.closeModalPopup = function(el) {
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