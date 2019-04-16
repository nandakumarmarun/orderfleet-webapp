// Create a BestPerformanceConfiguration object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.BestPerformanceConfiguration) {
	this.BestPerformanceConfiguration = {};
}

(function() {
	'use strict';

	var bestPerformanceConfigurationContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var deleteForm = $("#deleteForm");

	var bestPerformanceConfigurationModel = {
		pid : null,
		name : null,
		documentType : null
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

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteBestPerformanceConfiguration(e.currentTarget.action);
		});

		$('#btnSaveDocument').on('click', function() {
			saveAssignedDocuments();
		});

		$('#btnDocId').on('click', function() {
			$("#documentsModal").modal("show");
		});

		$('#field_documentType').on('change', function() {
			loadDocumentsByDocumentType(this.value);
		});
	});

	function deleteBestPerformanceConfiguration(actionurl, id) {
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

	function loadDocumentsByDocumentType(documentType) {
		$("#documentsCheckboxes input:checkbox").attr('checked', false);
		$
				.ajax({
					url : bestPerformanceConfigurationContextPath + "/"
							+ documentType,
					type : "GET",
					async : false,
					success : function(documents) {
						$("#tbl_documentsCheckboxes").html("");
						var tbl = "";
						if (documents) {
							$
									.each(
											documents,
											function(index, document) {
												tbl += "<tr><td><input name='document' type='checkbox' value="
														+ document.pid
														+ " /></td><td>"
														+ document.name
														+ "</td></tr>";
											});
							$("#tbl_documentsCheckboxes").html(tbl);
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function saveAssignedDocuments() {

		$(".error-msg").html("");
		var selectedDocuments = "";

		if ($("#field_bestPerformanceType").val() == "-1") {
			$(".error-msg").html("Please select Best Performance Type");
			return;
		}

		if ($("#field_documentType").val() == "-1") {
			$(".error-msg").html("Please select Document type");
			return;
		}

		$.each($("input[name='document']:checked"), function() {
			selectedDocuments += $(this).val() + ",";
		});

		if (selectedDocuments == "") {
			$(".error-msg").html("Please select Documents");
			return;
		}

		$.ajax({
			url : bestPerformanceConfigurationContextPath + "/saveDocuments",
			type : "POST",
			data : {
				assignedDocuments : selectedDocuments,
				documentType : $("#field_documentType").val(),
				bestPerformanceType : $("#field_bestPerformanceType").val()
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

	function editBestPerformanceConfiguration(bPType) {
		var docTYpe = $("#" + bPType).val();
		loadDocumentsByDocumentType(docTYpe);

		$("#field_documentType").val(docTYpe);
		$("#field_bestPerformanceType").val(bPType);

		$("#documentsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : bestPerformanceConfigurationContextPath
					+ "/getTrueDocuments/" + bPType,
			type : "GET",
			success : function(documents) {
				if (documents) {
					$.each(documents, function(index, document) {
						$(
								"#documentsCheckboxes input:checkbox[value="
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = bestPerformanceConfigurationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = bestPerformanceConfigurationContextPath;
	}

	BestPerformanceConfiguration.showModalPopup = function(el, id, action) {

		if (id) {
			switch (action) {
			case 1:
				editBestPerformanceConfiguration(id);
				// createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action',
						bestPerformanceConfigurationContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	BestPerformanceConfiguration.closeModalPopup = function(el) {
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