if (!this.OrderStatus) {
	this.OrderStatus = {};
}

(function() {
	'use strict';

	var orderStatusContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#orderStatusForm");
	var deleteForm = $("#deleteForm");
	var orderStatusModel = {
		id : null,
		name : null,
		documentType : null,
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		documentType : {
			valueNotEquals : "-1"
		},

	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		documentType : {
			valueNotEquals : "This field is required."
		},
	};

	$(document).ready(function() {
		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value,
				element, arg) {
			return arg != value;
		}, "");
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateOrderStatus(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteOrderStatus(e.currentTarget.action);
		});

	});

	function createUpdateOrderStatus(el) {
		orderStatusModel.name = $('#field_name').val();
		orderStatusModel.documentType = $('#field_documentType').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(orderStatusModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showOrderStatus(id) {
		$.ajax({
			url : orderStatusContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_documentType').text(data.documentType);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editOrderStatus(id) {
		$.ajax({
			url : orderStatusContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				// set id
				orderStatusModel.id = data.id;
				$('#field_documentType').val(data.documentType);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteOrderStatus(actionurl, id) {
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
		window.location = orderStatusContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = orderStatusContextPath;
	}

	OrderStatus.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showOrderStatus(id);
				break;
			case 1:
				editOrderStatus(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', orderStatusContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	OrderStatus.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		orderStatusModel.id = null; // reset orderStatus model;
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