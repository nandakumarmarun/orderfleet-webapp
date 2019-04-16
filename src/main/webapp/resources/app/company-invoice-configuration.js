// Create a CompanyInvoiceConfiguration object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.CompanyInvoiceConfiguration) {
	this.CompanyInvoiceConfiguration = {};
}

(function() {
	'use strict';

	var companyInvoiceConfigurationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#companyInvoiceConfigurationForm");
	var deleteForm = $("#deleteForm");
	var companyInvoiceConfigurationModel = {
		id : null,
		address : null,
		amountPerUser : null,
		companyPid : null,
		companyName : null
	};
	
	// Specify the validation rules
	var validationRules = {
		address : {
			required : true,
			maxlength : 255
		},
		amountPerUser : {
			required : true,
			maxlength : 8
		},
		companyPid : {
			valueNotEquals : "-1"
		}
		
	};

	// Specify the validation error messages
	var validationMessages = {
			address : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		amountPerUser : {
			required : "This field is required.",
		},
	};


	$(document).ready(
			function() {
				
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateCompanyInvoiceConfiguration(form);
					}
				});
				
				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteCompanyInvoiceConfiguration(e.currentTarget.action);
				});
			});


	function createUpdateCompanyInvoiceConfiguration(el) {
		companyInvoiceConfigurationModel.address = $('#field_address').val();
		companyInvoiceConfigurationModel.amountPerUser = $('#field_amtPerUser').val();
		companyInvoiceConfigurationModel.companyPid = $("#field_company").val();
		companyInvoiceConfigurationModel.companyName = $("#field_company option:selected").text();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(companyInvoiceConfigurationModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showCompanyInvoiceConfiguration(id) {
		$.ajax({
			url : companyInvoiceConfigurationContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_address').text(data.address);
				$('#lbl_amtPerUser').text(data.amountPerUser);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editCompanyInvoiceConfiguration(id) {
		$.ajax({
			url : companyInvoiceConfigurationContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_address').val(data.address);
				$('#field_amtPerUser').val(data.amountPerUser);
				$("#field_company").val(data.companyPid);
				// set id
				companyInvoiceConfigurationModel.id = data.id;
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteCompanyInvoiceConfiguration(actionurl) {
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
		window.location = companyInvoiceConfigurationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = companyInvoiceConfigurationContextPath;
	}

	CompanyInvoiceConfiguration.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showCompanyInvoiceConfiguration(id);
				break;
			case 1:
				editCompanyInvoiceConfiguration(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', companyInvoiceConfigurationContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	CompanyInvoiceConfiguration.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		companyInvoiceConfigurationModel.id = null; // reset companyInvoiceConfiguration model;
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