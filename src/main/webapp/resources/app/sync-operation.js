// Create a SyncOperation object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SyncOperation) {
	this.SyncOperation = {};
}

(function() {
	'use strict';

	var syncOperationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#syncOperationForm");
	var deleteForm = $("#deleteForm");

	var syncOperationModel = {
		operationType : null,
		lastSyncStartedDate : null,
		lastSyncCompletedDate : null,
		lastSyncTime : null,
		completed : null,
		companyName : null,
		companyPid : null,
		user : null,
		document : null,
		reset : null
	};

	$(document).ready(
			function() {
				
				$('.selectpicker').selectpicker();
				
				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteSyncOperation(e.currentTarget.action);
				});

				$('#btnSaveSyncOperations').on('click', function() {
					saveSyncOperations();
				});

				$('#btnCreateSyncOPeration').on('click', function() {
					$("#assignSyncOperationsModal").modal("show");
				});

				$('#field_company').on('change', function() {
					getAssignedSyncOperations();
				});
				
				
				$('input:checkbox.allcheckbox').change(function(){
				    var status = $(this).is(":checked") ? true : false;
				    $(".chk").prop("checked",status);
				});
				
			});

	function getAssignedSyncOperations() {

		$("input[name='syncOperation']:checkbox").prop('checked', false);
		var companyPid = $('#field_company').val();
		if (companyPid != -1) {
			$
					.ajax({
						url : syncOperationContextPath
								+ "/getAssighnedSyncOperations",
						type : "GET",
						data : {
							companyPid : companyPid,
						},
						success : function(result) {
							$
									.each(
											result,
											function(index, syncOperationType) {
												$("#divSyncOperations input:checkbox[value="+ syncOperationType.operationType+ "]").prop("checked", true);
													var resetval = "reset"+ syncOperationType.operationType;
													var userval = "user"+ syncOperationType.operationType;
													var documentval = "document"+ syncOperationType.operationType;
													$("#divSyncOperations input:checkbox[value="+ resetval + "]").prop("checked",syncOperationType.reset);
													$("#divSyncOperations input:checkbox[value="+ userval + "]").prop("checked",syncOperationType.user);
													$("#divSyncOperations input:checkbox[value="+ documentval + "]").prop("checked",syncOperationType.document);
											});
						},
						error : function(xhr, error) {
							onError(xhr, error);
						},
					});
		}
	}

	function saveSyncOperations() {
		$(".error-msg").html("");

		var companyPid = $('#field_company').val();

		if (companyPid == -1 || companyPid == "") {
			$(".error-msg").html("Please select company");
			return;
		}

//		var selectedSyncOperation = "";
		
		var syncOperationDTOs=[];

		$.each($("input[name='syncOperation']:checked"), function() {
			var syncName=$(this).val();
			
			syncOperationModel.operationType=syncName;
			syncOperationModel.companyPid=companyPid;
			syncOperationModel.user=$('#user' + syncName).is(":checked");
			syncOperationModel.document=$('#document' + syncName).is(":checked");
			syncOperationModel.reset=$('#' + syncName).is(":checked");
			syncOperationDTOs.push(syncOperationModel);
			syncOperationModel={};
		});
		if (syncOperationDTOs.length == 0) {
			$(".error-msg").html("Please select SyncOperation");
			return;
		}
		
		$.ajax({
			url : syncOperationContextPath + "/saveSyncOperations",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(syncOperationDTOs),
			success : function(status) {
				$("#assignSyncOperationsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = syncOperationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = syncOperationContextPath;
	}

	SyncOperation.closeModalPopup = function(el) {
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