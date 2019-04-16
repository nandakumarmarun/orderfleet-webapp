// Create a UserAccountProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserAccountProfile) {
	this.UserAccountProfile = {};
}
var userPid = "";

(function() {
	'use strict';

	var userAccountProfileContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var userAccountProfileModel = {
		userPid : null
	};

	$(document).ready(function() {
		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveUser').on('click', function() {
			saveAssignedUser();
		});
	});

	function loadUserAccountProfile(employeeName) {

		$("#userName").html(employeeName);

		$.ajax({
			url : userAccountProfileContextPath + "/" + userPid,
			type : "GET",
			success : function(userAccountProfile) {
				console.log(userAccountProfile);
				if (userAccountProfile.accountProfilePid != null) {
					$('#accountProfilePid').val(
							userAccountProfile.accountProfilePid);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedUser() {

		$(".error-msg").html("");

		var accountProfilePid = $("#accountProfilePid").val();
		if (accountProfilePid == "-1"
				|| $('#accountProfilePid :selected').text() == '') {
			$(".error-msg").html("Please select Account Profile");
			return;
		}
		$.ajax({
			url : userAccountProfileContextPath + "/save",
			type : "POST",
			data : {
				accountProfilePid : accountProfilePid,
				userPid : userPid,
			},
			success : function(status) {
				$("#usersModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				console.log(".............................");
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userAccountProfileContextPath;
	}

	UserAccountProfile.showModalPopup = function(el, pid, action, employeeName) {
		resetForm();
		if (pid) {
			switch (action) {
			case 0:
				userPid = pid;
				loadUserAccountProfile(employeeName);
				break;
			}
		}
		el.modal('show');
	}

	function resetForm() {
		$('.alert').hide();
		$(".error-msg").html("");
		$('#accountProfilePid').val("-1");
		userPid = null; // reset userPid;
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