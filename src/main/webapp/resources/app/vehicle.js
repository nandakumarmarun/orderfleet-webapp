// Create a Vehicle object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Vehicle) {
	this.Vehicle = {};
}

(function() {
	'use strict';
	
	var vehicleContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	var createEditForm = $("#createEditForm");
	var deleteForm = $("#deleteForm");
	var vehicleModel = {
		pid : null,
		name : null,
		registrationNumber : null,
		description : null,
		stockLocationPid : null,
		stockLocationName : null
	};
	
	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		registrationNumber : {
			required : true,
			maxlength : 15
		}
	};
	
	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		registrationNumber : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 15 characters."
		}
	};
	
	$(document).ready(
			function() {
				
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateVehicle(form);
					}
				});
				
				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteVehicle(e.currentTarget.action);
				});
			}
	);
	
	function createUpdateVehicle(el) {

		vehicleModel.name = $("#field_name").val();
		vehicleModel.registrationNumber = $("#field_registrationNumber").val();
		vehicleModel.description = $("#field_description").val();
		vehicleModel.stockLocationPid = $("#field_stockLocation").val();
		
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(vehicleModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function showVehicle(id) {
		$.ajax({
			url : vehicleContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name == null ? "" : data.name);
				$('#lbl_registrationNumber').text(data.registrationNumber == null ? "" : data.registrationNumber);
				$('#lbl_description').text(data.description == null ? "" : data.description);
				$('#lbl_stockLocation').text(data.stockLocationName == null ? "" : data.stockLocationName);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editVehicle(id) {
		$.ajax({
			url : vehicleContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$("#field_name").val(data.name);
				$("#field_registrationNumber").val(data.registrationNumber);
				$("#field_description").val(data.description);
				$("#field_stockLocation").val(data.stockLocationPid);

				// set pid
				vehicleModel.pid = data.pid;

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteVehicle(actionurl, id) {
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
		window.location = vehicleContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = vehicleContextPath;
	}

	Vehicle.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showVehicle(id);
				break;
			case 1:
				editVehicle(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', vehicleContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	Vehicle.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		vehicleModel.pid = null; // reset vehicle model;
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