if (!this.ChangePassword) {
	this.ChangePassword = {};
}

(function() {
	'use strict';

	var companyid = null;
	$(document).ready(function() {

		$('#dbCompany').on('change', function() {
			companyid = $('#dbCompany').val();
			loadUserByCompanyId(companyid);
		});

		$('#btnSearch').click(function() {
			searchTable($("#search").val(), $('#tableCompanyUserDevice'));
		});

		$('.selectpicker').selectpicker();

		$('#myFormSubmit').on('click', function() {
			showPage();
			;
		});
	});

	var changePasswordContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	function loadUserByCompanyId(companyid) {
		console.log("enter......")
		$("#dbUser").html("<option>Users loading...</option>")
		$
				.ajax({
					url : location.protocol + '//' + location.host
							+ "/web/user-list/" + companyid,
					type : 'GET',
					success : function(users) {
						// $("#dbUser").html("<option value='no'>Select
						// User</option>")

						$('#tBodyChangePassword').html("");
						if (users.length == 0) {
							$('#tBodyChangePassword')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$('#tBodyChangePassword').html("");
						$
								.each(
										users,
										function(key, user) {
											$("#tBodyChangePassword")
													.append(
															"<tr><td>"
																	+ user.login
																	+ "</td><td>"
																	+ user.userPassword
																	+ "</td><td><button type='button' class='btn btn-orange' onclick='ChangePassword.showModalPopup($(\"#viewModal\"),\""
																	+ user.pid
																	+ "\",0);'>ChangePassword</button></td></tr>");
										});
					}
				});
	}

	function searchTable(inputVal, table) {
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	var userpid = null;
	ChangePassword.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				el.modal('show');
				userpid = pid;
				break;
			}
		}
	}
	function showPage() {
		console.log("Enter here")

		var password = $('#password').val()
		var newpassword = $('#confirmPassword').val()

		console.log(userpid)
		console.log("p : " + password + "confirm : " + newpassword)
		if (password == '') {
			alert("please enter new password")
		}
		if (newpassword == '') {
			alert("please enter confirm password")
		}
		if (password != newpassword) {
			alert("confirm password does not match")
		}

		else {

			$.ajax({
				url : location.protocol + '//' + location.host
						+ "/web/saveNewPassword",

				type : 'POST',
				data : {
					userpid : userpid,
					password : password
				},
				success : function(users) {
					alert("password changed succesfully")
					$("#viewModal").modal("hide");
					companyid = $('#dbCompany').val();
					console.log("id : " + companyid)
					loadUserByCompanyId(companyid);
					
				}
			});

		}

	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
    window.location = changePasswordContextPath;
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

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
})();