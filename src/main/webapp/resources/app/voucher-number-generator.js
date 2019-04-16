// Create a VoucherNumberGenerator object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.VoucherNumberGenerator) {
	this.VoucherNumberGenerator = {};
}

(function() {
	'use strict';

	var voucherNumberGeneratorContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#VoucherNumberGeneratorForm");
	var deleteForm = $("#deleteForm");
	var voucherNumberGeneratorModel = {
		id : null,
		prefix : null,
		startWith : null,
		userPid : null,
		documentPid:null
		
	};

	// Specify the validation rules
	var validationRules = {
		prefix : {
			maxlength : 10
		},
		userPid : {
			valueNotEquals : "no"
		},
		documentPid : {
			valueNotEquals : "no"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		prefix : {
			maxlength : "This field cannot be longer than 10 characters."
		},
		userPid : {
			valueNotEquals : "This field is required."
		},
		documentPid : {
			valueNotEquals : "This field is required."
		}
	};

	$(document).ready(function() {
		$.validator.addMethod("valueNotEquals", function(value,
				element, arg) {
			return arg != value;
		}, "This field is required.");
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateVoucherNumberGenerator(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteVoucherNumberGenerator(e.currentTarget.action);
		});
		
	});

	
	VoucherNumberGenerator.onChangeUser = function(){
		if ($("#dbUser").val() == "no") {
			return;
		}
		$("#dbDocument").html('<option>Loading...</option>');
		$.ajax({
			url : voucherNumberGeneratorContextPath + "/loaddocument",
			type : 'GET',
			async: false,
			data : {
				userPid : $("#dbUser").val()
			},
			success : function(documents) {
				$("#dbDocument").html('<option value="no">Select Document</option>');
				if (documents != null && documents.length > 0) {
					$.each(documents, function(index, document) {
						$("#dbDocument").append(
								'<option value="' + document.pid + '">' + document.name
										+ '</option>');
						$("#dbDocument").val(document.pid);
					});
				}

			}
		});
	}
	
	function createUpdateVoucherNumberGenerator(el) {
		voucherNumberGeneratorModel.prefix = $('#field_prefix').val();
		voucherNumberGeneratorModel.startWith = $('#field_startWith').val();
		voucherNumberGeneratorModel.documentPid = $('#dbDocument').val();
		voucherNumberGeneratorModel.userPid = $('#dbUser').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(voucherNumberGeneratorModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showVoucherNumberGenerator(id) {
		$.ajax({
			url : voucherNumberGeneratorContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_user').text(data.userName);
				$('#lbl_document').text(data.documentName);
				$('#lbl_prefix').text((data.prefix == null ? "" : data.prefix));
				$('#lbl_startWith').text((data.startWith == 0 ? 0 : data.startWith));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editVoucherNumberGenerator(id) {
		$.ajax({
			url : voucherNumberGeneratorContextPath + "/" + id,
			method : 'GET',
			 async: false,
			success : function(data) {
				$('#dbUser').val(data.userPid);
				VoucherNumberGenerator.onChangeUser();
				$('#field_startWith').val((data.startWith == null ? "" : data.startWith));
				$('#field_prefix').val((data.prefix == null ? "" : data.prefix));
				$('#dbDocument').val(data.documentPid);
				// set pid
				voucherNumberGeneratorModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteVoucherNumberGenerator(actionurl, id) {
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

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = voucherNumberGeneratorContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = voucherNumberGeneratorContextPath;
	}

	VoucherNumberGenerator.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showVoucherNumberGenerator(id);
				break;
			case 1:
				editVoucherNumberGenerator(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', voucherNumberGeneratorContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	VoucherNumberGenerator.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
//		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		voucherNumberGeneratorModel.id = null; // reset voucherNumberGenerator model;
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