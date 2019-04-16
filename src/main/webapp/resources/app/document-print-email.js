//sdasd

if (!this.DocumentPrintEmail) {
	this.DocumentPrintEmail = {};
}


(function() {
	'use strict';

	var documentPrintEmailContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	var createEditForm = $("#documentPrintEmailForm");
	var documentPrintEmailModel = {
		companyPid : null,
		documentPid : null,
		name : null,
		printFilePath : null,
		emailFilePath : null,
		documentPrintEmailId : null
	};
	//	var companyPID=-1;
	//	var documentPID=null;
	var validationRules = {
		printFilePath : {
			required : true,
			maxlength : 255
		},
		name : {
			required : true,
			maxlength : 255
		},
		documentPid : {
			valueNotEquals : "-1"
		},
		emailFilePath : {
			required : true,
			maxlength : 255
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		printFilePath : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		emailFilePath : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};


	$(document).ready(
		function() {

			$('#field_company').on('change', function() {
				getDocumentPrintEmail();
			});

			$('#slt_field_company').on('change', function() {
				var companypid = $('#slt_field_company').val();
				getAlldocuments(companypid);
			});


			$.validator.addMethod("valueNotEquals", function(value, element, arg) {
				return arg != value;
			}, "");

			createEditForm.validate({
				rules : validationRules,
				messages : validationMessages,
				submitHandler : function(form) {
					createUpdateDocumentPrintEmail(form);
				}
			});
		});

	function createUpdateDocumentPrintEmail(el) {
		documentPrintEmailModel.name = $('#field_name').val();
		documentPrintEmailModel.printFilePath = $('#field_printFilePath').val();
		documentPrintEmailModel.emailFilePath = $('#field_emailFilePath').val();
		documentPrintEmailModel.companyPid = $('#slt_field_company').val();
		documentPrintEmailModel.documentPid = $('#field_document').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(documentPrintEmailModel),
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
		window.location = documentPrintEmailContextPath;
	}

	function getDocumentPrintEmail() {
		$('#maintable').html("");
		var data = "";
		var companyPid = $('#field_company').val();
		if (companyPid != -1) {
			$.ajax({
				url : documentPrintEmailContextPath + "/getDocumentPrintEmail",
				type : "GET",
				data : {
					companyPid : companyPid,
				},
				success : function(result) {
					$.each(result, function(index, documentPrintEmail) {
						var urlId = documentPrintEmail.documentPid + '~' + documentPrintEmail.documentPrintEmailId;
						data += "<tr><td>" + documentPrintEmail.documentName + "</td><td>" + documentPrintEmail.name + "</td><td>" + documentPrintEmail.printFilePath + "</td><td>" + documentPrintEmail.emailFilePath + "</td><td><button type='button' class='btn btn-info' onclick='DocumentPrintEmail.showModalPopup($(\"#addModal\"),\""
							+ urlId
							+ "\",1);'>Edit</button></td></tr>";
					});
					$('#maintable').html(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		}
	}

	DocumentPrintEmail.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			editDocumentPrintEmail(id);
			createEditForm.attr('method', 'PUT');
		}
		$('#slt_field_company').prop('disabled', false);
		el.modal('show');
	}

	function editDocumentPrintEmail(id) {
		var documentPrintEmailId = id.split("~")[1];

		var companypid = $('#field_company').val();
		getAlldocuments(companypid);

		$.ajax({
			url : documentPrintEmailContextPath + "/" + documentPrintEmailId,
			method : 'GET',
			success : function(data) {
				$('#slt_field_company').val(data.companyPid);
				$('#slt_field_company').prop('disabled', true);
				$('#field_document').val(data.documentPid);
				$('#field_printFilePath').val(data.printFilePath);
				$('#field_name').val(data.name);
				$('#field_emailFilePath').val(data.emailFilePath);
				documentPrintEmailModel.documentPrintEmailId = data.documentPrintEmailId;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function getAlldocuments(companypid) {
		if (companypid != -1) {
			$.ajax({
				url : documentPrintEmailContextPath + "/getDocuments",
				type : "GET",
				data : {
					companyPid : companypid,
				},
				success : function(result) {
					$("#field_document").html(
						"<option value='-1'>Select Document</option>");
					$.each(result, function(key, document) {
						$("#field_document").append(
							"<option value='" + document.pid + "'>"
							+ document.name + "</option>");
					});
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		}
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
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