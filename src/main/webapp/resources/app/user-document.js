// Create a UserDocument object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserDocument) {
	this.UserDocument = {};
}

(function() {
	'use strict';

	var userDocumentContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var userDocumentModel = {
		userPid : null,
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveDocument').on('click', function() {
			saveAssignedDocuments();
		});
	});

	function loadUserDocument(userPid) {
		$("#documentsCheckboxes input:checkbox").attr('checked', false);
		userDocumentModel.userPid = userPid;
		// clear all check box
		$.ajax({
			url : userDocumentContextPath + "/" + userPid,
			type : "GET",
			success : function(documents) {
				if (documents) {
					$.each(documents, function(index, document) {
						$(
								"#documentsCheckboxes input:checkbox[value="
										+ document.documentPid + "]").prop(
								"checked", true);
						$("#imageOption" + document.documentPid).prop(
								"checked", document.imageOption);

					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedDocuments() {
		$(".error-msg").html("");
		var assignedDocuments = [];
		$.each($("input[name='document']:checked"), function() {
			var documentpid = $(this).val();
			var selectedimageOption = $("#imageOption" + $(this).val()).prop(
					"checked");
			assignedDocuments.push({
				documentPid : documentpid,
				imageOption : selectedimageOption,
			});
		});
		if (assignedDocuments == "") {
			$(".error-msg").html("Please select Documents");
			return;
		}
		var userPid = userDocumentModel.userPid;
		$.ajax({
			url : userDocumentContextPath + "/save/" + userPid,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(assignedDocuments),
			success : function(status) {
				$("#assignDocumentsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userDocumentContextPath;
	}

	UserDocument.showModalPopup = function(el, id, action) {
		userDocumentModel.userPid = id;
		if (id) {
			switch (action) {
			case 0:
				loadUserDocument(id);
				break;
			}
		}
		el.modal('show');
	}

	UserDocument.closeModalPopup = function(el) {
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