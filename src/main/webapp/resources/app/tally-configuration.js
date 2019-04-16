// Create a TaskExecutionSaveOffline object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.TallyConfiguration) {
	this.TallyConfiguration = {};
}

(function() {
	'use strict';
	
	var createEditForm = $("#createEditForm");

	var tallyConfigurationDTO = {

		pid : null,
		companyPid : null,
		companyName : null,
		tallyCompanyName : null,
		tallyProductKey : false,
		dynamicDate : null,
		orderNumberWithEmployee : false,
		actualBillStatus : false,
		itemRemarksEnabled : false,
		gstNames : null,
		staticGodownNames : null,
		roundOffLedgerName : null,
		salesLedgerName : null,
		receiptVoucherType : null,
		bankReceiptType : null,
		bankName : null,
		cashReceiptType : null,
		transactionType : null,
		pdcVoucherType : null

	};

	$(document).ready(function() {

		$('#tallysModal').on('click', function() {
			resetForm();
			$("#assignTallyConfigurationsModal").modal("show");
			$("#dbCompany").val("-1");
			$("#dbCompany").prop("disabled", false);
		});

		$('#btnSaveTallyConfigurations').on('click', function() {
			saveTallyConfiguration();
		});

		$('#dbCompany').on('change', function() {
			getTallyConfigurationConfig();
		});

		$('#btnDelete').on('click', function() {
			deleteTallyConfiguration();
		});
	});

	var contextPath = location.protocol + '//' + location.host
			+ location.pathname;

	function saveTallyConfiguration() {
		if ($("#dbCompany").val() == -1) {
			alert("Please select company");
			return;
		}
		if ($("#tallyCompanyName").val() == "") {
			alert("Please enter Tally Company Name ");
			return;
		}
		if ($("#tallyProductKey").val() == "") {
			alert("Please enter Tally Product Key ");
			return;
		}
		/*if ($("#dynamicDate").val() == "") {
			alert("Please enter Dynamic date ");
			return;
		}*/
		/*if ($("#gstNames").val() == "") {
			alert("Please enter GST Names ");
			return;
		}*/
		/*if ($("#staticGodownNames").val() == "") {
			alert("Please enter Static Godown Names ");
			return;
		}
		if ($("#staticSalesOrderDate").val() == "") {
			alert("Please enter Static Sales Order Date ");
			return;
		}*/
		if ($("#salesLedgerName").val() == "") {
			alert("Please enter Sales Ledger Name ");
			return;
		}
		if ($("#receiptVoucherType").val() == "") {
			alert("Please enter Receipt Voucher Type ");
			return;
		}
		if ($("#bankReceiptType").val() == "") {
			alert("Please enter Bank Receipt Type ");
			return;
		}
		if ($("#bankName").val() == "") {
			alert("Please enter Bank Name ");
			return;
		}
		if ($("#cashReceiptType").val() == "") {
			alert("Please enter Cash Receipt Type ");
			return;
		}
		if ($("#transactionType").val() == "") {
			alert("Please enter Transaction Type ");
			return;
		}
		if ($("#pdcVoucherType").val() == "") {
			alert("Please enter PDC Voucher Type ");
			return;
		}
		tallyConfigurationDTO.companyPid = ($("#dbCompany").val());
		tallyConfigurationDTO.tallyCompanyName = $("#tallyCompanyName").val();
		tallyConfigurationDTO.tallyProductKey = $("#tallyProductKey").val();
		tallyConfigurationDTO.dynamicDate = $("#dynamicDate").val();
		tallyConfigurationDTO.orderNumberWithEmployee = $(
				"#orderNumberWithEmployee").is(":checked");
		tallyConfigurationDTO.actualBillStatus = $("#actualBillStatus").is(
				":checked");
		tallyConfigurationDTO.itemRemarksEnabled = $("#itemRemarksEnabled").is(
				":checked");
		tallyConfigurationDTO.gstNames = $("#gstNames").val();
		tallyConfigurationDTO.staticGodownNames = $("#staticGodownNames").val();
		tallyConfigurationDTO.roundOffLedgerName = $("#roundOffLedgerName")
				.val();
		tallyConfigurationDTO.salesLedgerName = $("#salesLedgerName").val();
		tallyConfigurationDTO.receiptVoucherType = $("#receiptVoucherType")
				.val();
		tallyConfigurationDTO.bankReceiptType = $("#bankReceiptType").val();
		tallyConfigurationDTO.bankName = $("#bankName").val();
		tallyConfigurationDTO.cashReceiptType = $("#cashReceiptType").val();
		tallyConfigurationDTO.transactionType = $("#transactionType").val();
		tallyConfigurationDTO.pdcVoucherType = $("#pdcVoucherType").val();

		$.ajax({
			url : contextPath,
			method : 'POST',
			contentType : "application/json; charset:utf-8",
			data : JSON.stringify(tallyConfigurationDTO),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function getTallyConfigurationConfig() {
		$('input[type="checkbox"]:checked').prop('checked', false);
		$.ajax({
			url : contextPath + "/" + $("#dbCompany").val(),
			method : 'GET',
			success : function(data) {
				
				$("#tallyCompanyName").val(data.tallyCompanyName);
				$("#tallyProductKey").val(data.tallyProductKey);
				$("#dynamicDate").val(data.dynamicDate);
				$('#orderNumberWithEmployee').prop("checked",data.orderNumberWithEmployee);
				$('#actualBillStatus').prop("checked",data.actualBillStatus);
				$('#itemRemarksEnabled').prop("checked",data.itemRemarksEnabled);
				$("#gstNames").val(data.gstNames);
				$("#staticGodownNames").val(data.staticGodownNames);
				$("#roundOffLedgerName").val(data.roundOffLedgerName);
				$("#salesLedgerName").val(data.salesLedgerName);
				$("#receiptVoucherType").val(data.receiptVoucherType);
				$("#bankReceiptType").val(data.bankReceiptType);
				$("#bankName").val(data.bankName);
				$("#cashReceiptType").val(data.cashReceiptType);
				$("#transactionType").val(data.transactionType);
				$("#pdcVoucherType").val(data.pdcVoucherType);
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var tallyConfigPid = "";
	TallyConfiguration.deleteConfig = function(pid) {
		tallyConfigPid = pid;
		$('#alertMessage').html("Are You Sure...?");
		$('#alertBox').modal("show");
	}

	function deleteTallyConfiguration() {
		$.ajax({
			url : contextPath + "/delete/" + tallyConfigPid,
			method : 'DELETE',
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	TallyConfiguration.edit = function(pid, companyPid) {
		$("#dbCompany").val(companyPid);

		getTallyConfigurationConfig();
		$("#dbCompany").prop("disabled", true);
		$("#assignTallyConfigurationsModal").modal("show");
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

	TallyConfiguration.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		tallyConfigurationDTO.pid = null; // reset syncOperation model;
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

})();