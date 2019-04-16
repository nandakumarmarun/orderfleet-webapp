// Create a SnrichProductRate object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SnrichProductRate) {
	this.SnrichProductRate = {};
}

(function() {
	'use strict';
	
	var ContextPath = location.protocol + '//' + location.host + location.pathname;
	var createEditForm = $("#createEditForm");
	var deleteForm = $("#deleteForm");
	var snrichProductRateModel = {
			pid: null,
			snrichProductPid : null,
			snrichProductName : null,
			orderProPaymentMode : null,
			rate : 0
		};
	
	// Specify the validation rules
	var validationRules = {
		snrichProductPid : {
			valueNotEquals : "-1"
		},
		orderProPaymentMode : {
			valueNotEquals : "-1"
		},
		rate : {
			required : true
		}
	};
	
	// Specify the validation error messages
	var validationMessages = {
		snrichProductPid : {
			required : "This field is required."
		},
		orderProPaymentMode : {
			required : "This field is required."
		},
		rate : {
			required : "This field is required."
		}
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
					createUpdateSnrichProductRate(form);
				}
			});
				
			deleteForm.submit(function(e) {
				// prevent Default functionality
				e.preventDefault();
				// pass the action-url of the form
				deleteSnrichProductRate(e.currentTarget.action);
			});
		});
	
	function createUpdateSnrichProductRate(el) {

		snrichProductRateModel.snrichProductPid = $("#field_snrich_product").val();
		snrichProductRateModel.orderProPaymentMode = $("#field_paymentMode").val();
		snrichProductRateModel.rate = $("#field_rate").val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(snrichProductRateModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function editSnrichProductRate(id) {
		$.ajax({
			url : ContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_snrich_product').val(data.snrichProductPid);
				$('#field_paymentMode').val(data.orderProPaymentMode);
				$('#field_rate').val(data.rate);

				// set pid
				snrichProductRateModel.pid = data.pid;

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteSnrichProductRate(actionurl, id) {
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
		window.location = ContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = ContextPath;
	}

	SnrichProductRate.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				//showPartner(id);
				break;
			case 1:
				editSnrichProductRate(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', ContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	SnrichProductRate.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		snrichProductRateModel.pid = null; // reset partner model;
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