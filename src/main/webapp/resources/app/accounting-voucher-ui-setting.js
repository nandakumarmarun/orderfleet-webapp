if (!this.AccountingVoucherUISetting) {
	this.AccountingVoucherUISetting = {};
}

(function() {
	'use strict';

	var accountingVoucherUISettingContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#accountingVoucherUISettingForm");
	var deleteForm = $("#deleteForm");
	var accountingVoucherUISettingModel = {
		id : null,
		name : null,
		title : null,
		paymentMode : null,
		documentPid : null,
		activityPid:null

	};

	// Specify the validation rules
	var validationRules = {
			name : {
				required : true,
				maxlength : 255
			},
			title : {
				required : true,
				maxlength : 255
			},
			activityPid : {
			valueNotEquals : "-1"
		},
		paymentMode : {
			valueNotEquals : "-1"
		},
		documentPid : {
			valueNotEquals : "-1"
		},
	};

	// Specify the validation error messages
	var validationMessages = {
			name : {
				required : "This field is required.",
				maxlength : "This field cannot be longer than 255 characters."
			},
			title : {
				required : "This field is required.",
				maxlength : "This field cannot be longer than 255 characters."
			},
		activityPid : {
			valueNotEquals : "This field is required."
		},
		paymentMode : {
			valueNotEquals : "This field is required."
		},
		documentPid : {
			valueNotEquals : "This field is required."
		}
	};

	$(document).ready(
			function() {

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateAccountingVoucherUISetting(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteAccountingVoucherUISetting(e.currentTarget.action);
				});

			});


	function createUpdateAccountingVoucherUISetting(el) {
		accountingVoucherUISettingModel.documentPid = $('#field_document').val();
		accountingVoucherUISettingModel.name = $('#field_name').val();
		accountingVoucherUISettingModel.paymentMode = $('#field_paymentMode').val();
		accountingVoucherUISettingModel.title = $('#field_title').val();
		accountingVoucherUISettingModel.activityPid = $('#field_activity').val();
		
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountingVoucherUISettingModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showAccountingVoucherUISetting(id) {
		$.ajax({
			url : accountingVoucherUISettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_document').text(data.documentName);
				$('#lbl_paymentMode').text(data.paymentMode);
				$('#lbl_name').text(data.name);
				$('#lbl_activity').text(data.activityName);
				$('#lbl_title').text(data.title);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editAccountingVoucherUISetting(id) {
		$.ajax({
			url : accountingVoucherUISettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data);
				$('#field_name').val(data.name);
				$('#field_activity').val(data.activityPid);
				$('#field_paymentMode').val(data.paymentMode);
				$('#field_title').val(data.title);
				AccountingVoucherUISetting.onChangeDocumentType();
				$('#field_document').val(data.documentPid);
				accountingVoucherUISettingModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteAccountingVoucherUISetting(actionurl, id) {
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

	AccountingVoucherUISetting.onChangeDocumentType = function() {
		if($('#field_activity').val()=="-1"){
			$(".error-msg").html("Please select Activity");
			return;
		}
		$("#field_document").html('<option>Loading...</option>');
		$.ajax({
			url : accountingVoucherUISettingContextPath + "/loaddocument",
			type : 'GET',
			async:false,
			data : {
				activityPid:$('#field_activity').val()
			},
			success : function(documents) {
				$("#field_document").html('<option value="no">Select Document</option>');
				if (documents != null && documents.length > 0) {
					$.each(documents, function(index, document) {
						$("#field_document").append(
								'<option value="' + document.pid + '">' + document.name
										+ '</option>');
						$("#field_document").val(document.pid);
					});
				}

			}
		});
	}


	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountingVoucherUISettingContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = accountingVoucherUISettingContextPath;
	}

	AccountingVoucherUISetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showAccountingVoucherUISetting(id);
				break;
			case 1:
				editAccountingVoucherUISetting(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', accountingVoucherUISettingContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	AccountingVoucherUISetting.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		accountingVoucherUISettingModel.id = null; // reset accountingVoucherUISetting model;
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