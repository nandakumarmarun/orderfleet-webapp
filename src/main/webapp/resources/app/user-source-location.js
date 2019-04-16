if (!this.UserSourceLocation) {
	this.UserSourceLocation = {};
}

(function() {
	'use strict';

	var userSourceLocationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#userSourceLocationForm");
	var deleteForm = $("#deleteForm");
	var userSourceLocationModel = {
		id : null,
		userPid : null,
		
	};
	$(document).ready(function() {
		$('#btnSave').on('click', function() {
			createUpdateUserSourceLocation();
		});
		
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function createUpdateUserSourceLocation() {
	var	selectedLocation=$("input[name='stockLocation']:checked").val();
	if(selectedLocation==null){
		$(".error-msg").html("Please select Source Location");
		return;
	}
		$.ajax({
			url : userSourceLocationContextPath + "/saveStockLocation",
			type : "POST",
			data : {
				userPid : userSourceLocationModel.userPid,
				stockLocation : selectedLocation,
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	function loadUserSourceLocation(userPid) {
		userSourceLocationModel.userPid = userPid;
		
		$("#documentsCheckboxes input:radio").attr('checked', false);
		$.ajax({
			url : userSourceLocationContextPath + "/" + userPid,
			type : "GET",
			success : function(userSourceLocation) {
						    $("#documentsCheckboxes input:radio[name='stockLocation'][value="+ userSourceLocation.stockLocationPid + "]").prop("checked",true);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});

	}

	UserSourceLocation.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserSourceLocation(id);
				break;
			}
		}
		el.modal('show');
	}

	UserSourceLocation.closeModalPopup = function(el) {
		el.modal('hide');
	}


	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userSourceLocationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userSourceLocationContextPath;
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		userSourceLocationModel.pid = null; // reset userSourceLocation model;
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