// Create a UserForm object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserForm) {
	this.UserForm = {};
}

(function() {
	'use strict';

	var userFormContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var userFormModel = {
		userPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveForm').on('click', function() {
			saveAssignedForms();
		});
	});

	function loadUserForm(userPid) {
		$("#formsCheckboxes input:checkbox").attr('checked', false);
		userFormModel.userPid = userPid;
		// clear all check box
		$.ajax({
			url : userFormContextPath + "/" + userPid,
			type : "GET",
			success : function(forms) {
				$.each(forms,
						function(index, form) {
							$(
									"#formsCheckboxes input:checkbox[value="
											+ form.formPid + "]").prop(
									"checked", true);
							$("#sortOrder" + form.formPid).val(form.sortOrder);
						});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedForms() {
		$(".error-msg").html("");
		var selectedForms = [];

		$.each($("input[name='form']:checked"), function() {
			var formPid = $(this).val();
			var sortOrder = $("#sortOrder" + formPid).val();

			selectedForms.push({
				formPid : formPid,
				sortOrder : sortOrder
			});

		});

		if (selectedForms == "") {
			$(".error-msg").html("Please select Forms");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : userFormContextPath + "/save/" + userFormModel.userPid,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedForms),
			success : function(status) {
				$("#formsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userFormContextPath;
	}

	UserForm.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserForm(id);
				break;
			}
		}
		el.modal('show');
	}

	UserForm.closeModalPopup = function(el) {
		el.modal('hide');
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