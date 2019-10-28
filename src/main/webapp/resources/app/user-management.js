// Create a User object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.User) {
	this.User = {};
}

(/**
	 * 
	 */
function() {
	var userContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	var createEditForm = $("#userForm");
	var deleteForm = $("#deleteForm");
	var userModel = {
		pid : null,
		companyPid : null,
		companyName : null,
		login : null,
		firstName : null,
		lastName : null,
		email : null,
		mobile : null,
		activated : true,
		langKey : null,
		deviceKey : null,
		authorities : null,
		createdBy : null,
		createdDate : null,
		lastModifiedBy : null,
		lastModifiedDate : null,
		resetDate : null,
		resetKey : null,
		showAllUserData : true,
		dashboardUIType:null
	};

	// Specify the validation rules
	var validationRules = {
		login : {
			required : true,
			maxlength : 55
		},
		firstName : {
			required : true,
			maxlength : 55
		},
		password : {
			required : true,
			maxlength : 55
		},
		email : {
			required : true,
			maxlength : 55
		},
		mobile : {
			required : true,
			maxlength : 12
		},
		companyPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		login : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		firstName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		password : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		email : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		mobile : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 12 characters."
		}

	};

	$(document).ready(function() {
		
		$('.selectpicker').selectpicker();
		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateUser(form);
			}
		});

		$('#btnActivate').on('click', function() {
			activateAssigned();
		});
		
		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteUser(e.currentTarget.action);
		});
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activateAssigned() {
		
		$(".error-msg").html("");
		var selectedUser = "";

		$.each($("input[name='user']:checked"), function() {
			selectedUser += $(this).val() + ",";
		});

		if (selectedUser == "") {
			$(".error-msg").html("Please select User");
			return;
		}
		$.ajax({
			url : userContextPath + "/activateUsers",
			type : "POST",
			data : {
				users : selectedUser,
				activate:$("#field_activatedor").val(),
			},
			success : function(status) {
				$("#enableMultipleActivationModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function searchTable(inputVal) {
		var table = $('#tBodyUser');
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
	
	function createUpdateUser(el) {
		userModel.login = $('#field_login').val();
		userModel.password = $('#field_password').val();
		userModel.firstName = $('#field_firstName').val();
		userModel.lastName = $('#field_lastName').val();
		userModel.email = $('#field_email').val();
		userModel.mobile = $('#field_mobile').val();
		userModel.companyPid = $('#field_company').val();
		userModel.authorities = $('#field_authority').val();
		userModel.activated = $('#field_activated').prop('checked');
		userModel.showAllUserData=$('#field_show_all_user_data').is(":checked");
		userModel.dashboardUIType=$('#field_dashboardUIType').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showUser(id) {
		$.ajax({
			url : userContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_login').text(data.login);
				$('#lbl_firstName').text(data.firstName);
				$('#lbl_lastName').text(data.lastName);
				$('#lbl_company').text(data.companyName);
				$('#lbl_email').text(data.email);
				$('#lbl_mobile').text(data.mobile);

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editUser(id) {
		$.ajax({
			url : userContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_login').val(data.login);
				$('#field_password').val(data.password);
				$('#field_firstName').val(data.firstName);
				$('#field_lastName').val(data.lastName);
				$('#field_email').val(data.email);
				$('#field_mobile').val(data.mobile);
				$('#field_company').val(data.companyPid);
				$('#field_authority').val(data.authorities);
				$('#field_dashboardUIType').val(data.dashboardUIType);
				if (data.showAllUserData == true) {
					$('#field_show_all_user_data').prop("checked", true);
				} else {
					$('#field_show_all_user_data').prop("checked", false);
				}
				if (data.activated == true) {
					$('#field_activated').prop("checked", true);
				} else {
					$('#field_activated').prop("checked", false);
				}
				// set pid
				userModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteUser(actionurl, id) {
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

	User.setActive = function(userPid, isActivated) {
		userModel.pid = userPid;
		userModel.activated = isActivated;
		if (confirm("Are you sure?")) {
			// update status;changeStatus
			$.ajax({
				url : userContextPath + "/changeStatus",
				method : 'PUT',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(userModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	User.setActive = function(userPid, isActivated) {
		userModel.pid = userPid;
		userModel.activated = isActivated;
		if (confirm("Are you sure?")) {
			// update status;changeStatus
			$.ajax({
				url : userContextPath + "/changeStatus",
				method : 'PUT',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(userModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}

	User.loadActivateOrDeactivateUser = function() {
		if ($("#dbCompany").val() == "no") {
			alert("Please Select Company");
			return;
		}
		$("#tblEnableUser").html("");
		$
		.ajax({
			url : userContextPath + "/loadUser",
			method : 'GET',
			data : {
				companyPid : $("#dbCompany").val(),
			},
			success : function(users) {
				if (users != null
						&& users.length > 0) {
					$
							.each(
									users,
									function(index, user) {
										$("#tblEnableUser")
												.append(
														'<tr><td> <input name="user" value="'+ user.pid+ '" type="checkbox"></td><td> '+ user.firstName+ ''+ '</td></tr>');
									});
				}
				$("#enableMultipleActivationModal").modal("show");
			},
		});
	}
	
	User.filterUsers = function(filterBy) {

		$('#tBodyUser').html(
				"<tr><td colspan='10' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : userContextPath + "/filter",
					method : 'GET',
					data : {
						companyPid : $('#dbCompany').val()
					},
					success : function(users) {
						$('#tBodyUser').html("");
						if (users.length == 0) {
							$('#tBodyUser')
									.html(
											"<tr><td colspan='10' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										users,
										function(index, user) {
											$('#tBodyUser')
													.append(
															"<tr><td>"
																	+ user.companyName
																	+ "</td><td>"
																	+ user.firstName
																	+ " "
																	+ user.lastName
																	+ "</td><td>"
																	+ user.login
																	+ "</td><td>"
																	+ user.email
																	+ "</td><td>"
																	+ spanActivated(
																			user.activated,
																			user.pid)
																	+ "</td><td>"
																	+ spanAuthorities(user.authorities)
																	+ "</td><td>"
																	+ convertDateTimeFromServer(user.createdDate)
																	+ "</td><td>"
																	+ user.lastModifiedBy
																	+ "</td><td>"
																	+ convertDateTimeFromServer(user.lastModifiedDate)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='User.showModalPopup($(\"#viewModal\"),\""
																	+ user.pid
																	+ "\",0);'>View</button>&nbsp;<button type='button' class='btn btn-blue' onclick='User.showModalPopup($(\"#myModal\"),\""
																	+ user.pid
																	+ "\",1);'>Edit</button></td></tr>");
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function spanActivated(activated, userPid) {
		var spanActivated = "";
		var user = "'" + userPid + "'";
		if (activated) {
			spanActivated = '<span class="label label-success" onclick="User.setActive('
					+ user
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Activated</span>';
		} else {
			spanActivated = '<span class="label label-danger" onclick="User.setActive('
					+ user
					+ ','
					+ !activated
					+ ')" style="cursor: pointer;">Deactivated</span>';
		}
		return spanActivated;
	}

	function spanAuthorities(authorities) {
		var allAuthorities = "";
		if (authorities != null) {
			$.each(authorities, function(index, authority) {
				allAuthorities += "<span class='label label-info'>" + authority
						+ "</span></br>";
			});
			return allAuthorities;
		}
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userContextPath;
	}

	User.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showUser(id);
				break;
			case 1:
				editUser(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				changeStatusUser(id);
				break;
			}
		}
		el.modal('show');
	}

	User.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		userModel.pid = null; // reset user model;
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
	
	function convertDateTimeFromServer (date) {
        if (date) {
        	return moment(date).format('MMMM Do YYYY, h:mm:ss a'); 
        } else {
        	return null;
        }
		
    }
})();