if (!this.UserRemoteLocation) {
	this.UserRemoteLocation = {};
}

(function() {
	'use strict';

	var userRemoteLocationContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#userRemoteLocationForm");
	var userRemoteLocationModel = {
		login : null,
		expireDateString : null,
		companyName : null,
	};

	// Specify the validation rules
	var validationRules = {
		login : {
			required : true,
			maxlength : 255
		},
		expireDateString : {
			required : true,
		},
		companyName : {
			required : true,
			maxlength : 255
		},

	};

	// Specify the validation error messages
	var validationMessages = {
		login : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		companyName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		expireDateString : {
			required : "This field is required.",
		},

	};
	$(document)
			.ready(
					function() {

						createEditForm.validate({
							rules : validationRules,
							messages : validationMessages,
							submitHandler : function(form) {
								createUpdateUserRemoteLocation(form);
							}
						});

						$("#txtDate").datepicker({
							dateFormat : "dd-mm-yy",
							minDate : 0,
							required : true,
						});

						$("#txtExpireDate").datepicker({
							dateFormat : "dd-mm-yy",
							minDate : 0,
							required : true,
						});

						$('#btnConfirmExpireDate').on('click', function() {
							updateExpireDate();
						});

						var $rows = $('#tbl_data tr');
						$("#dbCompany")
								.change(
										function() {
											filterText();

										});
					});

	function filterText() {
		var rex = new RegExp($('#dbCompany').val());
		if (rex == "/all/") {
			clearFilter()
		} else {
			$('.content').hide();
			$('.content').filter(function() {
				return rex.test($(this).text());
			}).show();
		}
	}

	function clearFilter() {
		$('.dbCompany').val('');
		$('.content').show();
	}

	function createUpdateUserRemoteLocation(el) {
		userRemoteLocationModel.login = $('#field_login').val();
		userRemoteLocationModel.expireDateString = $('#txtExpireDate').val();
		userRemoteLocationModel.companyName = $('#field_companyName').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(userRemoteLocationModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	UserRemoteLocation.changeActivated = function(id, active) {
		$.ajax({
			url : userRemoteLocationContextPath + "/updateActivated",
			type : "POST",
			data : {
				id : id,
				activated : active
			},
			success : function(status) {
				onSaveSuccess(status);
			},
		});
	}

	var userRemoteLocationId;
	UserRemoteLocation.showModalPopup = function(el, id, action) {
		if (action == 1) {
			el.modal('show');
		} else {
			var value = id.split(",");
			userRemoteLocationId = value[0];
			$("#field_login_e").val(value[1]);
			$("#txtDate").val(convertDateFromServer(value[2]));
			$("#field_companyName_e").val(value[3]);
			el.modal('show');
		}
	}

	function updateExpireDate() {

		if ($("#field_login_e").val() == "") {
			alert("please enter company name");
			return;
		}
		if ($("#field_companyName_e").val() == "") {
			alert("please enter company name");
			return;
		}

		$.ajax({
			url : userRemoteLocationContextPath + "/updateExpireDate",
			type : "POST",
			data : {
				id : userRemoteLocationId,
				login : $("#field_login_e").val(),
				company : $("#field_companyName_e").val(),
				expireDate : $("#txtDate").val(),
			},
			success : function(status) {
				onSaveSuccess(status);
			},
		});
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userRemoteLocationContextPath;
	}

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('DD-MM-YYYY');
		} else {
			return "";
		}
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