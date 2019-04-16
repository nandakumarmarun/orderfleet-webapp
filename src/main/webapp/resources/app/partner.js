// Create a Partner object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Partner) {
	this.Partner = {};
}

(function() {
	'use strict';

	var partnerContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#partnerForm");
	var deleteForm = $("#deleteForm");
	var partnerModel = {
		pid : null,
		firstName : null,
		lastName : null,
		address : null,
		country : null,
		countryCode : null,
		state : null,
		stateCode : null,
		district : null,
		districtCode : null,
		email : null,
		mobile : null,
		pincode : null,
		location : null,
		activated : null,
		login : null
	};

	// Specify the validation rules
	var validationRules = {
		firstName : {
			required : true,
			maxlength : 255
		},
		countryCode : {
			valueNotEquals : "-1"
		},
		stateCode : {
			valueNotEquals : "-1"
		},
		districtCode : {
			valueNotEquals : "-1"
		},
		location : {
			required : true,
			maxlength : 255
		},
		email : {
			required : true,
			maxlength : 55
		},
		mobile : {
			required : true,
			maxlength : 12,
			minlength : 10
		},
		login : {
			minlength : 4,
			required : true,
			maxlength : 50
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		firstName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		location : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		email : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		mobile : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 12 characters.",
			minlength : "This field cannot be less than 10 characters."
		},
		login : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 50 characters.",
			minlength : "This field cannot be less than 4 characters."
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
						createUpdatePartner(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deletePartner(e.currentTarget.action);
				});

				$('#btnActivatePartner').on('click', function() {
					activateAssignedPartner();
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function activateAssignedPartner() {
		$(".error-msg").html("");
		var selectedPartner = "";

		$.each($("input[name='partner']:checked"), function() {
			selectedPartner += $(this).val() + ",";
		});

		if (selectedPartner == "") {
			$(".error-msg").html("Please select Partner");
			return;
		}
		$.ajax({
			url : partnerContextPath + "/activatePartner",
			type : "POST",
			data : {
				partners : selectedPartner,
			},
			success : function(status) {
				$("#enablePartnerModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	Partner.onChangeCountry = function() {
		var countryCode = $('#field_country').val();
		$('#field_state').html("");

		$.ajax({
			url : partnerContextPath + "/countryChange/" + countryCode,
			method : 'GET',
			success : function(states) {
				var countryUnderStates = "";
				$.each(states, function(index, state) {
					countryUnderStates += '<option value=' + state.code + '>'
							+ state.name + '</option>';
				});
				$('#field_state').append(
						'<option value="-1">Select State</option>'
								+ countryUnderStates + '');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
			async : false
		});
	}

	Partner.onChangeState = function() {
		var stateCode = $('#field_state').val();
		$('#field_district').html("");

		$.ajax({
			url : partnerContextPath + "/stateChange/" + stateCode,
			method : 'GET',
			success : function(districts) {
				var countryUnderDistricts = "";
				$.each(districts, function(index, district) {
					countryUnderDistricts += '<option value=' + district.code
							+ '>' + district.name + '</option>';
				});
				$('#field_district').append(
						'<option value="-1">Select District</option>'
								+ countryUnderDistricts + '');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
			async : false
		});
	}

	function createUpdatePartner(el) {

		partnerModel.firstName = $("#field_firstName").val();
		partnerModel.lastName = $("#field_lastName").val();
		partnerModel.address = $("#field_address").val();
		partnerModel.country = $("#field_country option:selected").text();
		partnerModel.countryCode = $("#field_country").val();
		partnerModel.state = $("#field_state option:selected").text();
		partnerModel.stateCode = $("#field_state").val();
		partnerModel.district = $("#field_district option:selected").text();
		partnerModel.districtCode = $("#field_district").val();
		partnerModel.email = $("#field_email").val();
		partnerModel.pincode = $("#field_pinCode").val();
		partnerModel.location = $("#field_location").val();
		partnerModel.mobile = $("#field_mobile").val();
		partnerModel.login = $("#field_login").val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(partnerModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showPartner(id) {
		$.ajax({
			url : partnerContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
			$('#lbl_login').text(
						data.login == null ? "" : data.login);
				$('#lbl_firstName').text(
						data.firstName == null ? "" : data.firstName);
				$('#lbl_lastName').text(
						data.lastName == null ? "" : data.lastName);
				$('#lbl_address')
						.text(data.address == null ? "" : data.address);
				$('#lbl_countryName').text(
						data.country == null ? "" : data.country);
				$('#lbl_stateName').text(data.state == null ? "" : data.state);
				$('#lbl_districtName').text(
						data.district == null ? "" : data.district);
				$('#lbl_email').text(data.email == null ? "" : data.email);
				$('#lbl_pincode')
						.text(data.pincode == null ? "" : data.pincode);
				$('#lbl_location').text(
						data.location == null ? "" : data.location);
				$('#lbl_mobile').text(data.mobile == null ? "" : data.mobile);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editPartner(id) {
		$.ajax({
			url : partnerContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_country').val(data.countryCode);
				$('#field_country').prop("disabled", true);
				$('#field_state').prop("disabled", true);
				$('#field_state').html(
						'<option value=' + data.stateCode + '>' + data.state
								+ '</option>');
				$('#field_district').prop("disabled", true);
				$('#field_district').html(
						'<option value=' + data.districtCode + '>'
								+ data.district + '</option>');

				$("#field_lastName").val(data.lastName);
				$("#field_firstName").val(data.firstName);
				$("#field_address").val(data.address);
				$("#field_email").val(data.email);
				$("#field_pinCode").val(data.pincode);
				$("#field_location").val(data.location);
				$("#field_mobile").val(data.mobile);

				$("#field_login").val(data.login);

				// set pid
				partnerModel.pid = data.pid;

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deletePartner(actionurl, id) {
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

	Partner.setActive = function(name, pid, active) {
	
		partnerModel.pid = pid;
		partnerModel.activated = active;
		partnerModel.login = name;
		
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : partnerContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(partnerModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = partnerContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = partnerContextPath;
	}

	Partner.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showPartner(id);
				break;
			case 1:
				editPartner(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', partnerContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	Partner.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		partnerModel.pid = null; // reset partner model;
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