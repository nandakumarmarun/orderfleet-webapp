if (!this.Units) {
	this.Units = {};
}

(function() {
	'use strict';
	
	var unitsContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	var createEditForm = $("#createEditForm");
	var deleteForm = $("#deleteForm");
	var unitsModel = {
		pid : null,
		name : null,
		shortName : null,
		alias : null,
		unitId : null,
		unitCode : null
	};
	
	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		}
	};
	
	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};
	
	$(document).ready(
			function() {
				
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateUnits(form);
					}
				});
				
				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteUnits(e.currentTarget.action);
				});
			}
	);
	
	function createUpdateUnits(el) {

		unitsModel.name = $("#field_name").val();
		unitsModel.shortName = $("#field_shortName").val();
		unitsModel.alias = $("#field_alias").val();
		unitsModel.unitId = $("#field_unitId").val();
		unitsModel.unitCode = $("#field_unitCode").val();
		
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(unitsModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function showUnits(id) {
		$.ajax({
			url : unitsContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name == null ? "" : data.name);
				$('#lbl_registrationNumber').text(data.shortName == null ? "" : data.shortName);
				$('#lbl_description').text(data.alias == null ? "" : data.alias);
				$('#lbl_stockLocation').text(data.unitId == null ? "" : data.unitId);
				$('#lbl_unitCode').text(data.unitCode == null ? "" : data.unitCode);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editUnits(id) {
		$.ajax({
			url : unitsContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$("#field_name").val(data.name);
				$("#field_shortName").val(data.shortName);
				$("#field_alias").val(data.alias);
				$("#field_unitId").val(data.unitId);
				$("#field_unitCode").val(data.unitCode);

				// set pid
				unitsModel.pid = data.pid;

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteUnits(actionurl, id) {
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
		window.location = unitsContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = unitsContextPath;
	}

	Units.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showUnits(id);
				break;
			case 1:
				editUnits(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', unitsContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	Units.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		unitsModel.pid = null; // reset vehicle model;
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