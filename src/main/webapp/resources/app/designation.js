// Create a Designation object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Designation) {
	this.Designation = {};
}

(function() {
	'use strict';

	var designationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#designationForm");
	var deleteForm = $("#deleteForm");
	var designationModel = {
		pid : null,
		name : null,
		alias : null,
		description : null
		
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		}
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateDesignation(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDesignation(e.currentTarget.action);
		});
		
		$('#btnActivateDesignation').on('click', function() {
			activateAssignedDesignation();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activateAssignedDesignation() {
		$(".error-msg").html("");
		var selectedDesignation = "";

		$.each($("input[name='designation']:checked"), function() {
			selectedDesignation += $(this).val() + ",";
		});

		if (selectedDesignation == "") {
			$(".error-msg").html("Please select Designation");
			return;
		}
		$.ajax({
			url : designationContextPath + "/activateDesignation",
			type : "POST",
			data : {
				designations : selectedDesignation,
			},
			success : function(status) {
				$("#enableDesignationModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateDesignation(el) {
		designationModel.name = $('#field_name').val();
		designationModel.alias = $('#field_alias').val();
		designationModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(designationModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showDesignation(id) {
		$.ajax({
			url : designationContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_description').text((data.description == null ? "" : data.description));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editDesignation(id) {
		$.ajax({
			url : designationContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_description').val((data.description == null ? "" : data.description));
				// set pid
				designationModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteDesignation(actionurl, id) {
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

	Designation.setActive=function(name,pid,activated){
		designationModel.pid=pid;
		designationModel.activated=activated;
		designationModel.name=name;
		
		if(confirm("Are you confirm?")){
		$.ajax({
			url:designationContextPath+"/changeStatus",
			method:'POST',
			contentType : "application/json; charset=utf-8",
				data:JSON.stringify(designationModel),
				success:function(data){
					onSaveSuccess(data)
				},
		
				error:function(xhr,error){
					onError(xhr, error)
				}
		});
	}}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = designationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = designationContextPath;
	}

	Designation.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDesignation(id);
				break;
			case 1:
				editDesignation(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', designationContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	Designation.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		designationModel.pid = null; // reset designation model;
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