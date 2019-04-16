// Create a GstLedgerConfig object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.GstLedgerConfig) {
	this.GstLedgerConfig = {};
}

(function() {
	'use strict';
	
	var ContextPath = location.protocol + '//' + location.host + location.pathname;
	var url = window.location.href;
	var createEditForm = $("#createEditForm");
	var deleteForm = $("#deleteForm");
	var gstLedgerConfigModel = {
		id : 0,
		name : null,
		taxType : null,
		taxRate : 0,
		accountType : null,
		activated : null
	};
	
	$(document).ready(
			function() {
				
				createEditForm.validate({
					submitHandler : function(form) {
						createUpdateGstLedgerConfig(form);
					}
				});
				
				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteGstLedgerConfig(e.currentTarget.action);
				});
				
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
				
			}
	);
	
	function createUpdateGstLedgerConfig(el) {

		gstLedgerConfigModel.name = $("#field_name").val();
		gstLedgerConfigModel.taxType = $("#field_taxType").val();
		gstLedgerConfigModel.taxRate = $("#field_taxRate").val();
		gstLedgerConfigModel.accountType = $("#field_accountType").val();
		
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(gstLedgerConfigModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	GstLedgerConfig.changeStatus = function (){
		
		var gstLedgers = new Array();
			$.each($("input[name='ledger']:checked"), function() {
				gstLedgerConfigModel = {};
				gstLedgerConfigModel.id = parseInt($(this).val());
				gstLedgerConfigModel.activated = true;
				gstLedgers.push(gstLedgerConfigModel);
			});
			$.each($("input[name='ledger']:not(:checked)"), function() {
				gstLedgerConfigModel = {};
				gstLedgerConfigModel.id = parseInt($(this).val());
				gstLedgerConfigModel.activated = false;
				gstLedgers.push(gstLedgerConfigModel);
			});
		var datas = JSON.stringify(gstLedgers);
		$.ajax({
			url : ContextPath + "/activation",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : datas,
			success : function(status) {
				$('input:checkbox.allcheckbox').removeAttr('checked');
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function editGstLedgerConfig(id) {
		$.ajax({
			url : ContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$("#field_name").val(data.name);
				$("#field_registrationNumber").val(data.registrationNumber);
				$("#field_description").val(data.description);
				$("#field_stockLocation").val(data.stockLocationPid);

				// set pid
				gstLedgerConfigModel.pid = data.pid;

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteGstLedgerConfig(actionurl, id) {
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
		window.location = url;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		//window.location = ContextPath;
		window.location = url;
	}

	GstLedgerConfig.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showVehicle(id);
				break;
			case 1:
				editGstLedgerConfig(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', ContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	GstLedgerConfig.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		gstLedgerConfigModel.pid = null; // reset vehicle model;
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