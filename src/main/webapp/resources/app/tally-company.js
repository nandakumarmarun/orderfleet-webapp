// Create a TaskExecutionSaveOffline object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.TallyCompany) {
	this.TallyCompany = {};
}

(function() {
	'use strict';

	$(document).ready(function() {
		
		$('.selectpicker').selectpicker();
		
		$('#companysModal').on('click', function() {
			$("#assignTallyCompanysModal").modal("show");
		});
		$('#btnSaveTallyCompanys').on('click', function() {
			saveTallyCompany();
		});

		$('#dbCompany').on('change', function() {
			getTallyCompany();
		});

		$('#btnDelete').on('click', function() {
			deleteTallyCompany();
		});
	});

	var contextPath = location.protocol + '//' + location.host
			+ location.pathname;

	function saveTallyCompany() {
		if ($("#dbCompany").val() == -1) {
			alert("Please select company");
			return;
		}
		var tallyCompanyName = $("#field_name").val();
		if(tallyCompanyName==""){
		alert("please enter a tally company name");
			return;}
		
		var description = $("#field_description").val();
		
		$.ajax({
			url : contextPath,
			method : 'POST',
			data : {
				companyPid : $("#dbCompany").val(),
				tallyCompanyName : tallyCompanyName,
				description : description,
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function getTallyCompany() {
		$('input[type="checkbox"]:checked').prop('checked', false);
		$.ajax({
			url : contextPath + "/" + $("#dbCompany").val(),
			method : 'GET',
			success : function(data) {
			 $("#field_name").val(data.tallyCompanyName);
			 $("#field_description").val(data.description);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var companyPid = "";
	TallyCompany.delete = function(pid) {
		companyPid = pid;
		$('#alertMessage').html("Are You Sure...?");
		$('#alertBox').modal("show");
	}

	function deleteTallyCompany() {
		$.ajax({
			url : contextPath + "/" + companyPid,
			method : 'DELETE',
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;
	}

	TallyCompany.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		syncOperationModel.pid = null; // reset syncOperation model;
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