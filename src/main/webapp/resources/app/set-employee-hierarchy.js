if (!this.SetEmployeeHierarchy) {
	this.SetEmployeeHierarchy = {};
}

(function() {
	'use strict';

	var setEmployeeHierarchyContextPath = location.protocol + '//' + location.host
		+ "/web/set-employee-hierarchy-root";
	var createEditForm = $("#setEmployeeHierarchyForm");
	var setEmployeeHierarchyModel = {
			employeePid : null,
			parentPid : null,
	};

	// Specify the validation rules
	var validationRules = {
		employeePid : {
			valueNotEquals : "-1"
		},
	};

	// Specify the validation error messages
	var validationMessages = {
		employeePid : {
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
				createUpdateSetEmployeeHierarchy(form);
			}
		});
	});
	var oldValue=null;
	function createUpdateSetEmployeeHierarchy(el) {
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			data : {
				employeePid : $('#field_employee').val(),
				parentPid : oldValue
			},
			success : function(data) {
				console.log(data);
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = setEmployeeHierarchyContextPath;
	}

	SetEmployeeHierarchy.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				editEmployeeHierarchy(id);
				createEditForm.attr('method', 'PUT');
				break;
			}
		}
		el.modal('show');
	}

	function editEmployeeHierarchy(id) {
		oldValue=id;
		$.ajax({
            url: setEmployeeHierarchyContextPath + "/" + id,
            method: 'GET',
            success: function(data) {
            	$('#field_employee').val(data.employeePid);
            	//set pid
            },
            error: function(xhr, error) { 
            	onError(xhr, error);
            }
        });
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