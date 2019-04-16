// Create a ProductGroupInfoSection object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ProductGroupInfoSection) {
	this.ProductGroupInfoSection = {};
}

(function() {
	'use strict';

	var productGroupInfoSectionContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#productGroupInfoSectionForm");
	var deleteForm = $("#deleteForm");
	var productGroupInfoSectionModel = {
		pid : null,
		name : null,
		productGroupPid : null,
		richText : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		productGroupPid : {
			valueNotEquals : "-1"
		},
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		productGroupPid : {
			valueNotEquals : "This field is required."
		},
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateProductGroupInfoSection(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteProductGroupInfoSection(e.currentTarget.action);
		});
	});

	function createUpdateProductGroupInfoSection(el) {
		console.log("createUpdateProductInfoSection.......");
		productGroupInfoSectionModel.name = $('#field_name').val();
		productGroupInfoSectionModel.productGroupPid = $('#field_productGroup').val();
		productGroupInfoSectionModel.richText =  $('#field_richText').summernote('code');
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(productGroupInfoSectionModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showProductGroupInfoSection(id) {
		$.ajax({
			url : productGroupInfoSectionContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lblName').text(data.name);
				$('#lblProduct').text(data.productGroupName);
				$('#divRichText').html(data.richText);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editProductGroupInfoSection(id) {
		$('#field_richText').summernote('code',"Please wait...");
		$.ajax({
			url : productGroupInfoSectionContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_productGroup').val(data.productGroupPid);
				$('#field_richText').summernote('code', data.richText);
				// set pid
				productGroupInfoSectionModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteProductGroupInfoSection(actionurl, id) {
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
		window.location = productGroupInfoSectionContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = productGroupInfoSectionContextPath;
	}

	ProductGroupInfoSection.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showProductGroupInfoSection(id);
				break;
			case 1:
				editProductGroupInfoSection(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', productGroupInfoSectionContextPath + "/"
						+ id);
				break;
			}
		}
		el.modal('show');
	}

	ProductGroupInfoSection.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		productGroupInfoSectionModel.pid = null; // reset productGroupInfoSection model;
		$('#field_richText').summernote('code',"");
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