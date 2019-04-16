if (!this.SetUpTrial) {
	this.SetUpTrial = {};
}
(function() {
	'use strict';

	var setUpTrialContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#companyTrialSetUpForm");
	var setUpTrialModel = {
		name : null,
		email : null,
		password : null,
		companyName : null,
		shortName : null,
		country : null,
		gstNo : null,
		partnerPid : null
	};

	var validationRules = {
		name : {
			required : true,
			maxlength : 55
		},
		password : {
			required : true,
			minlength : 4,
			maxlength : 60
		},
		email : {
			required : true,
			maxlength : 55
		},
		companyName : {
			required : true,
			maxlength : 255
		},
		shortName : {
			required : true,
			maxlength : 55
		},
		partnerPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		password : {
			required : "This field is required.",
			minlength : "This field cannot be smaller than 4 characters",
			maxlength : "This field cannot be longer than 60 characters."
		},
		email : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
		companyName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		shortName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		},
	};
	$(document).ready(function(e) {
		
		$.validator.addMethod("valueNotEquals", function(value,
				element, arg) {
			return arg != value;
		}, "");
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateSetUpTrial(form);
			}
		});
		$(".img-check").click(function() {
			$(this).toggleClass("check");
		});

		$("input[type='image']").click(function() {
			$("input[id='my_file']").click();
		});

		$('#btnSale').on('click', function() {
			loadCreateAccountPage(1);
		});
		$('#btnReceipt').on('click', function() {
			loadCreateAccountPage(2);
		});
		$('#btnDynamic').on('click', function() {
			loadCreateAccountPage(3);
		});
		$('#loginPage').on('click', function() {
			loginPage();
		});

		$('#btnAddUser').on('click', function() {
			addUser();
		});

	});

	var productValue;
	SetUpTrial.loadCreateAccountPage = function(value) {
		productValue = value;
		window.location = location.protocol + '//' + location.host
				+ "/trial/setup";
	}

	function loginPage() {
		window.location = location.protocol + '//' + location.host + "/login";
	}

	SetUpTrial.myFunction = function() {

	}

	function createUpdateSetUpTrial(el) {
		var name = $('#field_name').val();
		var email = $('#field_email').val();
		var password = $('#field_password').val();
		var companyName = $('#field_companyName').val();
		var shortName = $('#field_shortName').val();
		var country = $('#field_country').val();
		var gstNo = $('#field_gstNo').val();

		if (name == "") {
			alert("Name must be filled out");
			return false;
		}
		if (email == "") {
			alert("Email must be filled out");
			return false;
		}
		if (password == "") {
			alert("Password must be filled out");
			return false;
		}
		console.log(password.length);
		if (password.length < 4) {
			alert("Password must be longer than 4");
			return false;
		}
		if (password.length > 60) {
			alert("Password must be smaller than 60");
			return false;
		}
		if (companyName == "") {
			alert("Company Name must be filled out");
			return false;
		}
		if (shortName == "") {
			alert("Short Name must be filled out");
			return false;
		}
		if (country == "no") {
			alert("Please Select Country");
			return false;
		}

		setUpTrialModel.name = $('#field_name').val();
		setUpTrialModel.email = $('#field_email').val();
		setUpTrialModel.password = $('#field_password').val();
		setUpTrialModel.companyName = $('#field_companyName').val();
		setUpTrialModel.shortName = $('#field_shortName').val();
		setUpTrialModel.selectedProduct = $('#selected').val();
		setUpTrialModel.country = $('#field_country').val();
		setUpTrialModel.gstNo = $('#field_gstNo').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(setUpTrialModel),
			success : function(datas) {
				$('#myModal').modal({
					backdrop : 'static',
					keyboard : false
				})
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(data) {
		window.location = setUpTrialContextPath;
	}

	function confirmPlan() {
		$("#confirmPlan").css("display", "none");
		$("#updateCompany").css("display", "block");

	}
	function updateCompany() {
		$("#confirmPlan").css("display", "none");
		$("#updateCompany").css("display", "none");
		$("#addUser").css("display", "block");

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