// Create a UserBalance object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserBalance) {
	this.UserBalance = {};
}

(function() {
	'use strict';

	var userBalanceContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#userBalanceForm");
	var deleteForm = $("#deleteForm");
	var userBalanceModel = {
		id : null,
		userPid : null,
		dateString : null,
		amount : null,
		remarks : null
		
	};

	// Specify the validation rules
	var validationRules = {
			userPid : {
				valueNotEquals : "-1"
		},
		amount : {
			required : true,
		}
	};

	// Specify the validation error messages
	var validationMessages = {
			userPid : {
				valueNotEquals : "This field is required."
		},
		amount : {
			required : "This field is required.",
		}
	};

	$(document).ready(function() {
		
		$("#txtDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value,
				element, arg) {
			return arg != value;
		}, "");
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateUserBalance(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteUserBalance(e.currentTarget.action);
		});
		
		
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	
	
	function createUpdateUserBalance(el) {
		userBalanceModel.userPid = $('#userPid').val();
		userBalanceModel.amount = $('#field_amount').val();
		userBalanceModel.remarks = $('#field_remarks').val();
		userBalanceModel.dateString = $('#txtDate').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userBalanceModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showUserBalance(id) {
		$.ajax({
			url : userBalanceContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_user').text(data.userName);
				var localDate = convertUTCDateToDate(data.dateTime);
				$('#lbl_date').text(localDate);
				$('#lbl_amount').text( data.amount);
				$('#lbl_remarks').text((data.remarks == null ? "" : data.remarks));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editUserBalance(id) {
		$.ajax({
			url : userBalanceContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#userPid').val(data.userPid);
				var localDate = convertUTCDateToDate(data.dateTime);
				$('#txtDate').val(localDate);
				$('#field_amount').text( data.amount);
				$('#field_remarks').val((data.remarks == null ? "" : data.remarks));
				// set id
				userBalanceModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteUserBalance(actionurl, id) {
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
		window.location = userBalanceContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userBalanceContextPath;
	}

	UserBalance.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showUserBalance(id);
				break;
			case 1:
				editUserBalance(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', userBalanceContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	UserBalance.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		userBalanceModel.id = null; // reset userBalance model;
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}
	function convertUTCDateToDate(utcDate) {
		var date = new Date(utcDate)
		var dd = date.getDate();
		var mm = date.getMonth() + 1;
		var yyyy = date.getFullYear();
		if (dd < 10) {
			dd = '0' + dd
		}
		if (mm < 10) {
			mm = '0' + mm
		}
		;
		return utcDate = dd + '-' + mm + '-' + yyyy
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