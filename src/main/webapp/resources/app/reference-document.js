// Create a ReferenceDocument object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ReferenceDocument) {
	this.ReferenceDocument = {};
}

(function() {
	'use strict';

	var documentProductGroupContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var documentProductGroupModel = {
		documentPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveReferenceDocument').on('click', function() {
			saveAssignedReferenceDocument();
		});
	});

	function loadReferenceDocument(documentPid) {
		console.log(documentPid);
		$("#referenceDocumentsCheckboxes input:checkbox")
				.attr('checked', false);
		documentProductGroupModel.documentPid = documentPid;
		// clear all check box
		$.ajax({
			url : documentProductGroupContextPath + "/" + documentPid,
			type : "GET",
			success : function(documents) {
				if (documents) {
					$.each(documents, function(index, document) {
						$(
								"#referenceDocumentsCheckboxes input:checkbox[value="
										+ document.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedReferenceDocument() {
		$(".error-msg").html("");
		var selectedProductGroups = "";

		$.each($("input[name='document']:checked"), function() {
			selectedProductGroups += $(this).val() + ",";
		});

		if (selectedProductGroups == "") {
			$(".error-msg").html("Please select Refference Documents");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : documentProductGroupContextPath + "/save",
			type : "POST",
			data : {
				documentPid : documentProductGroupModel.documentPid,
				assignedProductGroups : selectedProductGroups,
			},
			success : function(status) {
				$("#referenceDocumentsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentProductGroupContextPath;
	}

	ReferenceDocument.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadReferenceDocument(id);
				break;
			}
		}
		el.modal('show');
	}

	ReferenceDocument.closeModalPopup = function(el) {
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