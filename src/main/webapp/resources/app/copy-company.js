// Create a CopyCompany object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.CopyCompany) {
	this.CopyCompany = {};
}

(function() {
	'use strict';

	var companyContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var copyCompanyModel = {
		fromCompanyPid : null,
		legalName : null,
		email : null,
		users : null
	};

	$(document).ready(function() {

		CopyCompany.onChangeCompany();

		$('#btnFinish').on('click', function() {
			finshCopy();
		});

	});

	function finshCopy() {
		copyCompanyModel.fromCompanyPid = $('#field_company').val();
		copyCompanyModel.legalName = $('#field_legalName').val();
		copyCompanyModel.email = $('#field_email').val();

		copyCompanyModel.users = [];

		// set users
		var status = true;
		$.each($("input[name='user']:checked"), function() {
			var userPid = $(this).val();
			var login = $("#txtLogin" + userPid).val();
			if (login == "") {
				status = false;
				addErrorAlert("Please Enter User Name");
				return false;
			}
			var email = $("#txtEmail" + userPid).val();
			if (email == "") {
				console.log("Enter email");
				addErrorAlert("Please Enter Email");
				status = false;
				return false;
			}
			copyCompanyModel.users.push({
				pid : userPid,
				login : login,
				email : email
			});
		});
		if (!status) {
			return;
		}
		if (copyCompanyModel.users.length == 0) {
			addErrorAlert("Please Select Users");
			return;
		}
		console.log(copyCompanyModel);

		$.ajax({
			url : companyContextPath,
			method : 'POST',
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(copyCompanyModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	CopyCompany.onChangeCompany = function() {
		var companyPid = $('#field_company').val();
		$('#tbodyCompanyUsers').html("");
		$
				.ajax({
					url : companyContextPath + "/load-users/" + companyPid,
					method : 'GET',
					success : function(users) {
						$
								.each(
										users,
										function(index, user) {
											$('#tbodyCompanyUsers')
													.append('<tr><td><input type="checkbox" name="user" value="'
																	+ user.pid
																	+ '"/>&nbsp;'
																	+ user.login
																	+ '</td><td><input type="text" id="txtLogin'
																	+ user.pid
																	+ '" class="form-control" placeholder="New User Name" /></td><td><input type="email" id="txtEmail'
																	+ user.pid
																	+ '" class="form-control" placeholder="Email" maxlength="100" /></td></tr>');
										});

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = companyContextPath;
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
				addErrorAlert(httpResponse.getResponseHeader('X-orderfleetwebApp-message'),errorHeader, {
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