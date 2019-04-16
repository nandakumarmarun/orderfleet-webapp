// Create a DistanceFare object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DistanceFare) {
	this.DistanceFare = {};
}

(function() {
	'use strict';

	var distanceFareContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#distanceFareForm");
	var deleteForm = $("#deleteForm");
	var distanceFareModel = {
			pid : null,
			vehicleType : null,
			fare : null
		};
	
	// Specify the validation rules
	var validationRules = {
		vehicleType : {
			required : true,
			maxlength : 255
		},
		fare : {
			maxlength : 55
		}
	};
	
	// Specify the validation error messages
	var validationMessages = {
			vehicleType : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		fare : {
			maxlength : "This field cannot be longer than 55 characters."
		}
	};
	
	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateDistanceFare(form);
			}
		});
		
		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDistanceFare(e.currentTarget.action);
		});
		
	});
	
	function createUpdateDistanceFare(el) {
		distanceFareModel.vehicleType = $('#field_vehicleType').val();
		distanceFareModel.fare = $('#field_fare').val();
		
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(distanceFareModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function editDistanceFare(id) {
		$.ajax({
			url : distanceFareContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_vehicleType').val(data.vehicleType);
				$('#field_fare').val(data.fare);
				// set pid
				distanceFareModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function deleteDistanceFare(actionurl, id) {
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
	
	DistanceFare.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 1:
				editDistanceFare(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', distanceFareContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}
	
	DistanceFare.closeModalPopup = function(el) {
		el.modal('hide');
	}
	
	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		distanceFareModel.pid = null; // reset distanceFare model;
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = distanceFareContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = distanceFareContextPath;
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