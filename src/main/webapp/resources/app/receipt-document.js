// Create a ReceiptDocument object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ReceiptDocument) {
	this.ReceiptDocument = {};
}

(function() {
	'use strict';

	var receiptDocumentContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#receiptDocumentForm");
	var deleteForm = $("#deleteForm");
	var receiptDocumentModel = {
		pid : null,
		name : null,
		alias : null,
		description : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		}
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateReceiptDocument(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteReceiptDocument(e.currentTarget.action);
		});
		
		$('#btnSaveDocument').on('click', function() {
			saveAssignedDocuments();
		});
		
		$('#btnDocId').on('click', function() {
			loadUserDocument();
		});
	});

	function loadUserDocument() {
		
		/*onclick="ReceiptDocument.showModalPopup($('#myModal'));"*/
		console.log("aaaaaaaaaa");
		$("#documentsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : receiptDocumentContextPath + "/getDocuments",
			type : "GET",
			success : function(documents) {
				console.log(documents);
				if (documents) {
					$.each(documents, function(index, document) {
						$("#documentsCheckboxes input:checkbox[value=" + document.pid + "]").prop("checked", true);
					});
				}
				$("#documentsModal").modal("show");
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedDocuments() {

		$(".error-msg").html("");
		var selectedDocuments = "";

		$.each($("input[name='document']:checked"), function() {
			selectedDocuments += $(this).val() + ",";
		});

		if (selectedDocuments == "") {
			$(".error-msg").html("Please select Documents");
			return;
		}
		$.ajax({
			url : receiptDocumentContextPath + "/saveDocuments",
			type : "POST",
			data : {
				assignedDocuments : selectedDocuments,
			},
			success : function(status) {
				$("#documentsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = receiptDocumentContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = receiptDocumentContextPath;
	}

	ReceiptDocument.showModalPopup = function(el, id, action) {
		resetForm();
		el.modal('show');
	}

	ReceiptDocument.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		receiptDocumentModel.pid = null; // reset receiptDocument model;
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