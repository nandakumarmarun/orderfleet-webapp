// Create a RootCauseAnaysisReason object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.RootCauseAnalysisReason) {
	this.RootCauseAnalysisReason = {};
}

(function() {
	'use strict';
	
	var rootCauseAnalysisReasonContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	var createEditForm = $("#rootCauseAnalysisReasonForm");
	var deleteForm = $("#deleteForm");
	var rootCauseAnalysisReasonModel = {
			pid : null,
			name : null,
			alias : null,
			description : null
		};
	
	var validationRules = {
			name : {
				required : true,
				maxlength : 255
			},
			alias : {
				maxlength : 55
			},
			description : {
				maxlength : 250
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
			},
			description : {
				maxlength : "This field cannot be longer than 250 characters."
			}
		};
		
		$(document).ready(function() {
			
			createEditForm.validate({
				rules : validationRules,
				messages : validationMessages,
				submitHandler : function(form) {
					createUpdateRootCauseAnalysisReason(form);
				}
			});
			
			deleteForm.submit(function(e) {
				// prevent Default functionality
				e.preventDefault();
				// pass the action-url of the form
				deleteRootCauseAnalysisReason(e.currentTarget.action);
			});
			
//			$('#createBtn').click(function(){
//				RootCauseAnalysisReason.showModalPopup($('#myModal'));
//			});
			
		});
		
		function createUpdateRootCauseAnalysisReason(el) {
			rootCauseAnalysisReasonModel.name = $('#field_name').val();
			rootCauseAnalysisReasonModel.alias = $('#field_alias').val();
			rootCauseAnalysisReasonModel.description = $('#field_description').val();
			
			$.ajax({
				method : $(el).attr('method'),
				url : $(el).attr('action'),
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(rootCauseAnalysisReasonModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
		
		function editRootCauseAnalysisReason(id) {
			$.ajax({
				url : rootCauseAnalysisReasonContextPath + "/" + id,
				method : 'GET',
				success : function(data) {
					$('#field_name').val(data.name);
					$('#field_alias').val(data.alias);
					$('#field_description').val(data.description);
					// set pid
					rootCauseAnalysisReasonModel.pid = data.pid;
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
		
		function deleteRootCauseAnalysisReason(actionurl, id) {
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
		
		RootCauseAnalysisReason.showModalPopup = function(el, id, action) {
			resetForm();
			if (id) {
				switch (action) {
				case 1:
					editRootCauseAnalysisReason(id);
					createEditForm.attr('method', 'PUT');
					break;
				case 2:
					deleteForm.attr('action', rootCauseAnalysisReasonContextPath + "/" + id);
					break;
				}
			}
			el.modal('show');
		}
		
		RootCauseAnalysisReason.closeModalPopup = function(el) {
			el.modal('hide');
		}
		
		function resetForm() {
			$('.alert').hide();
			createEditForm.trigger("reset"); // clear form fields
			createEditForm.validate().resetForm(); // clear validation messages
			createEditForm.attr('method', 'POST'); // set default method
			rootCauseAnalysisReasonModel.pid = null; // reset rootCauseAnalysisReason model;
		}
		
		function onSaveSuccess(result) {
			// reloading page to see the updated data
			window.location = rootCauseAnalysisReasonContextPath;
		}

		function onDeleteSuccess(result) {
			// reloading page to see the updated data
			window.location = rootCauseAnalysisReasonContextPath;
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
