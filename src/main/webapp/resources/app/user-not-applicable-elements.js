// Create a UserNotApplicableElement object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserNotApplicableElement) {
	this.UserNotApplicableElement = {};
}

(function() {
	'use strict';

	var userNotApplicableElementContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {

		$('#btnSaveUsers').on('click', function() {
			saveAssignedUsers();
		});

	});

	UserNotApplicableElement.onChangeDocument = function() {
		$("#tblFroms").html("");
		$
				.ajax({
					url : userNotApplicableElementContextPath + "/loadForms",
					type : "GET",
					data : {
						documentPid : $("#dbDocument").val()
					},
					success : function(formFormElements) {
						if (formFormElements) {
							$
									.each(
											formFormElements,
											function(formKey, formElements) {
												var form = JSON.parse(formKey);
												$("#tblFroms")
														.append(
																'<tr data-id="'
																		+ form.pid
																		+ '" data-parent=\"\"><td>'
																		+ form.name
																		+ '</td><td></td></tr>');
												$
														.each(
																formElements,
																function(index,
																		formElement) {
																	$(
																			"#tblFroms")
																			.append(
																					'<tr data-id="'
																							+ formElement.pid
																							+ '" data-parent="'
																							+ form.pid
																							+ '" ><td>'
																							+ formElement.name
																							+ '</td><td><button type="button" class="btn btn-white" onclick="UserNotApplicableElement.loadUsers(\''
																							+ form.pid
																							+ '\',\''
																							+ formElement.pid
																							+ '\');" >Not Applicable Users</button></td></tr>');
																});
											});
						}
						$('.collaptable')
								.aCollapTable(
										{
											startCollapsed : true,
											addColumn : false,
											plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
											minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}

	UserNotApplicableElement.loadUsers = function(formPid, formElementPid) {

		$("#hdnFormPid").val(formPid);
		$("#hdnFormElementPid").val(formElementPid);

		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		$.ajax({
			url : userNotApplicableElementContextPath + "/users",
			type : "GET",
			data : {
				documentPid : $("#dbDocument").val(),
				formPid : formPid,
				formElementPid : formElementPid
			},
			success : function(users) {
				if (users) {
					$.each(users, function(index, user) {
						$("#divUsers input:checkbox[value=" + user.pid + "]").prop("checked", true);
					});
				}
				$("#assignUsersModal").modal("show");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function saveAssignedUsers() {

		$(".error-msg").html("");
		var selectedUsers = [];

		var formPid = $("#hdnFormPid").val();
		var formElementPid = $("#hdnFormElementPid").val();

		$.each($("input[name='user']:checked"), function() {
			selectedUsers.push({
				formPid : formPid,
				formElementPid : formElementPid,
				userPid : $(this).val()
			});
		});

		if (selectedUsers.length == 0) {
			$(".error-msg").html("Please select Users");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : userNotApplicableElementContextPath + "/users/"+ $("#dbDocument").val(),
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedUsers),
			success : function(status) {
				$("#assignUsersModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = activityContextPath;
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