// Create a DocumentAccountingVoucherColumn object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DocumentAccountingVoucherColumn) {
	this.DocumentAccountingVoucherColumn = {};
}

(function() {
	'use strict';

	var documentAccountingVoucherColumnContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var documentAccountingVoucherColumnModel = {
		documentPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveAccountingVoucherColumn').on('click', function() {
			saveAssignedAccountingVoucherColumns();
		});
	});

	function loadDocumentAccountingVoucherColumn(documentPid) {
		$(
				"#accountingVoucherColumnsCheckboxes input:checkbox[name='accountingVoucherColumn']")
				.attr('checked', false);
		$("#accountingVoucherColumnsCheckboxes input:checkbox[name='enabled']")
				.attr('checked', true);
		documentAccountingVoucherColumnModel.documentPid = documentPid;
		// clear all check box
		$.ajax({
			url : documentAccountingVoucherColumnContextPath + "/"
					+ documentPid,
			type : "GET",
			success : function(accountingVoucherColumns) {
				if (accountingVoucherColumns) {
					$.each(accountingVoucherColumns, function(index,
							accountingVoucherColumn) {
						$(
								"#accountingVoucherColumnsCheckboxes input:checkbox[value="
										+ accountingVoucherColumn.name + "]")
								.prop("checked", true);
						$("#" + accountingVoucherColumn.name + "").prop(
								"checked", accountingVoucherColumn.enabled);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedAccountingVoucherColumns() {
		$(".error-msg").html("");
		var accountingVoucherColumns = [];

		$.each($("input[name='accountingVoucherColumn']:checked"), function() {
			var name = $(this).val();
			var enabled = $("#" + name + "").prop("checked");
			console.log(enabled + "enabled");
			accountingVoucherColumns.push({
				name : name,
				enabled : enabled
			});
		});
		if (accountingVoucherColumns.length == "") {
			$(".error-msg").html("Please select Accounting Voucher Columns");
			return;
		}
		var documentAccountingVoucherColumn = {
			documentPid : documentAccountingVoucherColumnModel.documentPid,
			accountingVoucherColumns : accountingVoucherColumns
		}
		console.log(documentAccountingVoucherColumn);
		$.ajax({
			url : documentAccountingVoucherColumnContextPath + "/save",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(documentAccountingVoucherColumn),
			success : function(status) {
				$("#accountingVoucherColumnsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentAccountingVoucherColumnContextPath;
	}

	DocumentAccountingVoucherColumn.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadDocumentAccountingVoucherColumn(id);
				break;
			}
		}
		el.modal('show');
	}

	DocumentAccountingVoucherColumn.closeModalPopup = function(el) {
		el.modal('hide');
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