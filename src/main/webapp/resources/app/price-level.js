// Create a PriceLevel object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PriceLevel) {
	this.PriceLevel = {};
}

(function() {
	'use strict';

	var priceLevelContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#priceLevelForm");
	var deleteForm = $("#deleteForm");
	var priceLevelModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		sortOrder : 0
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
				createUpdatePriceLevel(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deletePriceLevel(e.currentTarget.action);
		});
		
		$('#btnActivatePriceLevel').on('click', function() {
			activateAssignedPriceLevel();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activateAssignedPriceLevel() {
		$(".error-msg").html("");
		var selectedPriceLevel = "";

		$.each($("input[name='pricelevel']:checked"), function() {
			selectedPriceLevel += $(this).val() + ",";
		});

		if (selectedPriceLevel == "") {
			$(".error-msg").html("Please select PriceLevel");
			return;
		}
		$.ajax({
			url : priceLevelContextPath + "/activatePriceLevel",
			type : "POST",
			data : {
				pricelevels : selectedPriceLevel,
			},
			success : function(status) {
				$("#enablePriceLevelModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdatePriceLevel(el) {
		priceLevelModel.name = $('#field_name').val();
		priceLevelModel.alias = $('#field_alias').val();
		priceLevelModel.description = $('#field_description').val();
		priceLevelModel.sortOrder = $('#field_sortOrder').val()

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(priceLevelModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showPriceLevel(id) {
		$.ajax({
			url : priceLevelContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_description').text(data.description);
				$('#lbl_sortOrder').text(data.sortOrder);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editPriceLevel(id) {
		$.ajax({
			url : priceLevelContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				$('#field_sortOrder').val((data.sortOrder));
				// set pid
				priceLevelModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deletePriceLevel(actionurl, id) {
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

	PriceLevel.setActive=function(name,pid,active){
		priceLevelModel.pid=pid;
		priceLevelModel.activated=active;
		priceLevelModel.name=name;
		if(confirm("Are you confirm?")){
			$.ajax({
				url:priceLevelContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(priceLevelModel),
				success:function(data){
					onSaveSuccess(data);
				},
				error:function(xhr,error){
					onError(xhr, error);
				}
			});
		}
		
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = priceLevelContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = priceLevelContextPath;
	}

	PriceLevel.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showPriceLevel(id);
				break;
			case 1:
				editPriceLevel(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', priceLevelContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	PriceLevel.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		priceLevelModel.pid = null; // reset priceLevel model;
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