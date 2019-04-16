if (!this.MobileMenuItem) {
	this.MobileMenuItem = {};
}

(function() {
	'use strict';

	var mobileMenuItemContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#mobileMenuItemForm");
	var deleteForm = $("#deleteForm");
	var mobileMenuItemModel = {
		id : null,
		name : null,
		label : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 55
		},
		label : {
			required : true,
			maxlength : 55
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		label : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateMobileMenuItem(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteMobileMenuItem(e.currentTarget.action);
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function createUpdateMobileMenuItem(el) {
		mobileMenuItemModel.name = $('#field_name').val();
		mobileMenuItemModel.label = $('#field_label').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(mobileMenuItemModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showMobileMenuItem(id) {
		$.ajax({
			url : mobileMenuItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_label').text(data.label);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editMobileMenuItem(id) {
		$.ajax({
			url : mobileMenuItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_label').val(data.label);
				// set pid
				mobileMenuItemModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteMobileMenuItem(actionurl, id) {
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
		window.location = mobileMenuItemContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = mobileMenuItemContextPath;
	}

	MobileMenuItem.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showMobileMenuItem(id);
				break;
			case 1:
				editMobileMenuItem(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', mobileMenuItemContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	MobileMenuItem.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		mobileMenuItemModel.id = null; // reset bank model;
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