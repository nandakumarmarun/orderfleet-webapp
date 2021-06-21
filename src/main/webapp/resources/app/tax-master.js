if (!this.TaxMaster) {
	this.TaxMaster = {};
}

(function() {
	'use strict';

	var taxMasterContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#taxMasterForm");
	var deleteForm = $("#deleteForm");
	var taxMasterModel = {
		pid : null,
		vatName : null,
		vatPercentage : null,
		description : null,
		vatClass : null,
		taxId : null,
		taxCode : null

	};

	// Specify the validation rules
	var validationRules = {
		vatName : {
			required : true,
			maxlength : 255
		},

	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},

	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateTaxMaster(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteTaxMaster(e.currentTarget.action);
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function createUpdateTaxMaster(el) {
		taxMasterModel.vatName = $('#field_vatName').val();
		taxMasterModel.vatPercentage = $('#field_vatPercentage').val();
		taxMasterModel.description = $('#field_description').val();
		taxMasterModel.vatClass = $('#field_class').val();
		taxMasterModel.taxId = $('#field_taxId').val();
		taxMasterModel.taxCode = $('#field_taxCode').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(taxMasterModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showTaxMaster(id) {
		$
				.ajax({
					url : taxMasterContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#lbl_vatName').text(data.vatName);
						$('#lbl_vatPercentage').text(
								(data.vatPercentage == null ? ""
										: data.vatPercentage));
						$('#lbl_description').text(
								(data.description == null ? ""
										: data.description));
						$('#lbl_class').text(
								(data.vatClass == null ? "" : data.vatClass));
						$('#lbl_taxId').text(
								(data.taxId == null ? ""
										: data.taxId));
						$('#lbl_taxCode').text(
								(data.taxCode == null ? ""
										: data.taxCode));
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function editTaxMaster(id) {
		$
				.ajax({
					url : taxMasterContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#field_vatName').val(data.vatName);
						$('#field_vatPercentage').val(
								(data.vatPercentage == null ? ""
										: data.vatPercentage));
						$('#field_description').val(
								(data.description == null ? ""
										: data.description));
						$('#field_class').val(
								(data.vatClass == null ? "" : data.vatClass));
						$('#field_taxCode').val(
								(data.taxCode == null ? "" : data.taxCode));
						$('#field_taxId').val(
								(data.taxId == null ? "" : data.taxId));
						// set pid
						taxMasterModel.pid = data.pid;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function deleteTaxMaster(actionurl, id) {
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
		window.location = taxMasterContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = taxMasterContextPath;
	}

	TaxMaster.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showTaxMaster(id);
				break;
			case 1:
				editTaxMaster(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', taxMasterContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	TaxMaster.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		taxMasterModel.pid = null; // reset taxMaster model;
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