// Create a LeadManagement object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.LeadManagement) {
	this.LeadManagement = {};
}

(function() {
	'use strict';
	
	var leadManagementContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	var createEditForm = $("#leadManagementForm");
	var accountProfileModel = {
			pid : null,
			name : null,
			alias : null,
			accountTypePid : null,
			accountTypeName : null,
			locationPid : null,
			locationName : null,
			phone : null,
			address : null,
			contactPerson : null,
			remarks : null
		};
	
	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		},
		phone1 : {
			maxlength : 20
		},
		address : {
			required : true,
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
		phone1 : {
			maxlength : "This field cannot be longer than 20 characters."
		},
		address : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 250 characters."
		}
	};
	
	$(document).ready(function() {
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateAccountProfile(form);
			}
		});
		LeadManagement.loadAccountProfiles();
		
		$('#field_territory').on('change', function() {
			var territoryPid = $('#field_territory').val();
			loadUserByTerritoryId(territoryPid);
		});

		$('.selectpicker').selectpicker();
	});
	function loadUserByTerritoryId(territoryPid) {
		$("#dbUser").html("<option>Users loading...</option>")
		$.ajax({
			url : leadManagementContextPath+ "/loadUsers/"
					+ territoryPid,
			type : 'GET',
			success : function(users) {
				$("#dbUser").html("<option value='no'>Select User</option>")
				$.each(users, function(key, user) {
					$("#dbUser").append(
							"<option value='" + user.pid + "'>" + user.login
									+ "</option>");
				});
			}
		});
	}
	LeadManagement.loadAccountProfiles = function() {
		$('#tBodyLeadManagement').html(
		"<tr><td colspan='13' align='center'>Please wait...</td></tr>");
		$
		.ajax({
			url : leadManagementContextPath + "/load",
			method : 'GET',
			success : function(accountProfiles) {
				addTableBodyvalues(accountProfiles);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function addTableBodyvalues(accountProfiles){
		
		 $('#tBodyLeadManagement').html("");
			if (accountProfiles.length == 0) {
				$('#tBodyLeadManagement').html("<tr><td colspan='13' align='center'>No data available</td></tr>");
				return;
			}
		$('#tBodyLeadManagement').html("");
		/*
		 * &nbsp;<button type='button' class='btn btn-danger'
		 * onclick='AccountProfile.showModalPopup($(\"#deleteModal\"),\"" +
		 * accountProfile.pid + "\",2);'>Delete</button>
		 */	 
		 $
			.each(
					accountProfiles,
					function(index, accountProfile) {
						$('#tBodyLeadManagement')
								.append(
										"<tr><td>"
												+ accountProfile.name
												+ "</td><td>"
												+ accountProfile.alias
												+ "</td><td>"
												+ accountProfile.address
												+ "</td><td>"
												+ (accountProfile.phone == null ? "" : accountProfile.phone)
												+ "</td><td>"
												+ accountProfile.contactPerson
												+ "</td><td><i class='btn btn-blue entypo-pencil' title='Edit Lead Management' onclick='LeadManagement.showModalPopup($(\"#myModal\"),\""
														+ accountProfile.pid
														+ "\",1);'></i></td></tr>");
					});
	 }
	
	function createUpdateAccountProfile(el) {
		accountProfileModel.name = $('#field_name').val();
		accountProfileModel.alias = $('#field_alias').val();
		accountProfileModel.phone = $('#field_phone1').val();
		accountProfileModel.address = $('#field_address').val();
		accountProfileModel.contactPerson = $('#field_contactPerson').val();
		accountProfileModel.locationPid = $('#field_territory').val();
		accountProfileModel.userPid = $('#dbUser').val();
		accountProfileModel.date = $("#txtFromDate").val(),
		accountProfileModel.locationName = $('#field_territory option:selected').text();
		accountProfileModel.remarks = $('#field_remarks').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountProfileModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function editAccountProfile(id) {
		$.ajax({
			url : leadManagementContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_pin').val(data.pin);
				$('#field_phone1').val(data.phone);
				$('#field_address').val(data.address);
				$('#field_contactPerson').val(data.contactPerson);
				$('#field_territory').val(data.locationPid);
				 $('#dbUser').val(data.userPid);
				$('#field_remarks').val(data.remarks);
				// set pid
				accountProfileModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = leadManagementContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = leadManagementContextPath;
	}
	
	LeadManagement.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showAccountProfile(id);
				break;
			case 1:
				editAccountProfile(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', leadManagementContextPath + "/" + id);
				break;
			case 3:
				showAccountProfileLocation(id);
				break;
			}
		}
		el.modal('show');
	}
	
	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		accountProfileModel.pid = null; // reset accountProfile model;
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
