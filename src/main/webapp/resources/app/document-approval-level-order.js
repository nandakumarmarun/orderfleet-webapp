// Create a DocumentApprovalLevelOrder object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DocumentApprovalLevelOrder) {
	this.DocumentApprovalLevelOrder = {};
}

(function() {
	'use strict';

	var documentApprovalLevelOrderContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var documentApprovalLevelOrderModel = {
		name : null,
		approvalOrder : null,
		documentPid : null,
		documentName : null
	};

	$(document).ready(function() {
		$('#btnDocumentApprovalLevelOrder').on('click', function() {
			saveAssignedDocumentApprovalLevels();
		});
	});

	function loadDocumentApprovalLevelOrder(documenyPid) {
		$.ajax({
			url : documentApprovalLevelOrderContextPath + "/" + documenyPid,
			type : "GET",
			success : function(documentApprovalLevels) {
				var rows = "";
				if (documentApprovalLevels) {
					$.each(documentApprovalLevels, function(index,
							documentApprovalLevel) {
						rows += "<tr><td><input type='hidden' value="
								+ documentApprovalLevel.pid + ">"
								+ documentApprovalLevel.name + "</td>"
								+ "<td><input type='text' name="
								+ documentApprovalLevel.approvalOrder
								+ " value="
								+ documentApprovalLevel.approvalOrder
								+ "></td></tr>";
					});
					$('#documentApprovalLevelOrders').html(rows);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedDocumentApprovalLevels() {
		$(".error-msg").html("");
		var documentApprovalLevelModels = [];
		var zeroVAlue = true;
		$('#tbldocumentApprovalLevelOrders > tbody  > tr').each(
				function() {
					var row = $(this);
					documentApprovalLevelOrderModel = {};
					documentApprovalLevelOrderModel.pid = row.find('td:eq(0)')
							.find('input').val();
					documentApprovalLevelOrderModel.approvalOrder = row.find(
							'td:eq(1)').find('input').val();

					if (row.find('td:eq(1)').find('input').val() == 0) {
						zeroVAlue = false;
						return;
					}
					documentApprovalLevelModels
							.push(documentApprovalLevelOrderModel);
				});

		if (zeroVAlue == false) {
			alert("order no '0' exist");
			return false;
		}

		$.ajax({
			url : documentApprovalLevelOrderContextPath + "/saveOrder",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(documentApprovalLevelModels),
			success : function(status) {
				$("#documentApprovalLevelsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	// function avoidDuplicateOrderNumbers(documentOrder,
	// documentApprovalLevelModels) {
	// var duplicate = true;
	// $.each(
	// documentApprovalLevelModels,
	// function(index, objDocumentApproval) {
	// if (objDocumentApproval.approvalOrder == documentApprovalLevelModels) {
	// duplicate = false;
	// return duplicate;
	// }
	// });
	// return duplicate;
	//
	// }

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentApprovalLevelOrderContextPath;
	}

	DocumentApprovalLevelOrder.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadDocumentApprovalLevelOrder(id);
				break;
			}
		}
		el.modal('show');
	}

	DocumentApprovalLevelOrder.closeModalPopup = function(el) {
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