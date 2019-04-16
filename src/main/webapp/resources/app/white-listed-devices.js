if (!this.WhiteListedDevices) {
	this.WhiteListedDevices = {};
}
(function() {
	'use strict';

	var whiteListedDevicesContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#whiteListedDevicesForm");
	var deleteForm = $("#deleteForm");
	var whiteListedDevicesModel = {
		id : null,
		deviceName : null,
		deviceKey : null,
		deviceVerificationNotRequired : null
		
	};

	// Specify the validation rules
	var validationRules = {
			deviceName : {
			required : true,
		},
		deviceKey : {
			required : true,
		}
	};

	// Specify the validation error messages
	var validationMessages = {
			deviceName : {
			required : "This field is required.",
		},
		deviceKey : {
			required : "This field is required.",
		},
		
	};

	$(document).ready(
			function() {
				
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});
				
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateWhiteListedDevices(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteWhiteListedDevices(e.currentTarget.action);
				});
				
			});

	function searchTable(inputVal) {
		var table = $('#tblUserDevice');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}
	

	function createUpdateWhiteListedDevices(el) {
		whiteListedDevicesModel.deviceName = $('#field_deviceName').val();
		whiteListedDevicesModel.deviceKey = $('#field_deviceKey').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(whiteListedDevicesModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editWhiteListedDevices(id) {
		$.ajax({
			url : whiteListedDevicesContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_deviceName').val(data.deviceName);
				$('#field_deviceKey').val((data.deviceKey == null ? "" : data.deviceKey));
				// set pid
				whiteListedDevicesModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteWhiteListedDevices(actionurl, id) {
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

	WhiteListedDevices.setActive = function(id, active) {
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : whiteListedDevicesContextPath + "/changeStatus",
				method : 'POST',
				data:{
					id:id,
					active:active,
				},
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = whiteListedDevicesContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = whiteListedDevicesContextPath;
	}

	WhiteListedDevices.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 1:
				editWhiteListedDevices(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', whiteListedDevicesContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	WhiteListedDevices.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		whiteListedDevicesModel.id = null; // reset whiteListedDevices model;
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