// Create a Company object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Company) {
	this.Company = {};
}

(function() {
	'use strict';

	var companyContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#companyForm");
	var deleteForm = $("#deleteForm");
	var companyModel = {
		pid : null,
		legalName : null,
		alias : null,
		companyType : null,
		industry : null,
		gstNo : null,
		address1 : null,
		address2 : null,
		countryName : null,
		countryCode : null,
		stateName : null,
		stateCode : null,
		districtName : null,
		districtCode : null,
		email : null,
		pincode : null,
		location : null,
		logo : null,
		logoContentType : null,
		website : null,
		activated : null,
		onPremise : null,
		smsUsername : null,
		smsPassword : null
	};

	// Specify the validation rules
	var validationRules = {
		legalName : {
			required : true,
			maxlength : 255
		},
		companyType : {
			valueNotEquals : "-1"
		},
		industry : {
			valueNotEquals : "-1"
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
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		legalName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		},
		location : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateCompany(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteCompany(e.currentTarget.action);
		});
		$('#btnSearchCompany').click(function() {
			searchTableCompany($("#searchCompany").val());
		});
	});

	function searchTableCompany(inputVal) {
		var table = $('#tBodyCompany');
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

	function createUpdateCompany(el) {
		companyModel.legalName = $('#field_legalName').val();
		companyModel.alias = $('#field_alias').val();
		companyModel.companyType = $('#field_companyType').val();
		companyModel.industry = $('#field_industry').val();
		companyModel.gstNo = $('#field_gstNo').val();
		companyModel.address1 = $('#field_address1').val();
		companyModel.address2 = $('#field_address2').val();
		companyModel.countryCode = $('#field_country').val();
		companyModel.stateCode = $('#field_state').val();
		companyModel.districtCode = $('#field_district').val();
		companyModel.email = $('#field_email').val();
		companyModel.pincode = $('#field_pinCode').val();
		companyModel.location = $('#field_location').val();
		companyModel.website = $('#field_website').val();
		companyModel.onPremise = $('#field_on_premise').is(":checked")
		companyModel.smsUsername = $('#field_smsUsername').val();
		companyModel.smsPassword = $('#field_smsPassword').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(companyModel),
			success : function(data) {
				onSaveSuccess(data);

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	$('#profileImage').on(
			'change',
			function() {
				var file = $(this)[0].files[0]; // only one file exist
				var fileReader = new FileReader();
				fileReader.readAsDataURL(file);

				fileReader.onload = function(e) {
					$('#previewImage').attr('src', fileReader.result);
					var base64Data = e.target.result.substr(e.target.result
							.indexOf('base64,')
							+ 'base64,'.length);
					companyModel.logo = base64Data;
					companyModel.logoContentType = file.type;
				};

			});

	function showCompany(id) {
		$.ajax({
			url : companyContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_legalName').text(data.legalName);
				$('#lbl_alias').text(data.alias);
				$('#lbl_companyType').text(data.companyType);
				$('#lbl_industry').text(data.industry);
				if(data.gstNo!=null)
				{
					$('#lbl_gstNo').text(data.gstNo);
				}else{
					$('#lbl_gstNo').text("-");
				}
				$('#lbl_address').text(data.address1 + "\n" + data.address2);
				$('#lbl_countryName').text(data.countryName);
				$('#lbl_stateName').text(data.stateName);
				$('#lbl_districtName').text(data.districtName);
				$('#lbl_email').text(data.email);
				$('#lbl_pincode').text(data.pincode);
				$('#lbl_location').text(data.location);
				$('#lbl_website').text(data.website);
				if(data.smsUsername!= null){
					$('#lbl_smsUsername').text(data.smsUsername);
				}else{
					$('#lbl_smsUsername').text("-");
				}
				
				if(data.smsPassword!= null){
					$('#lbl_smsPassword').text(data.smsPassword);
				}else{
					$('#lbl_smsPassword').text("-");
				}

				if (data.logo != null) {
					$('#profileImageView').attr('src',
							'data:image/png;base64,' + data.logo);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editCompany(id) {
		$('#previewImage').attr('src', '');
		$.ajax({
			url : companyContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_legalName').val(data.legalName);
				$('#field_legalName').attr('readonly', true);
				$('#field_alias').val(data.alias);
				$('#field_companyType').val(data.companyType);
				$('#field_industry').val(data.industry);
				$('#field_gstNo').val(data.gstNo);
				$('#field_address1').val(data.address1);
				$('#field_address2').val(data.address2);
				$('#field_country').val(data.countryCode);
				$('#field_country').prop("disabled", true);
				$('#field_state').prop("disabled", true);
				$('#field_state').html(
						'<option value=' + data.stateCode + '>'
								+ data.stateName + '</option>');
				$('#field_district').prop("disabled", true);
				$('#field_district').html(
						'<option value=' + data.districtCode + '>'
								+ data.districtName + '</option>');
				$('#field_email').val(data.email);
				$('#field_pinCode').val(data.pincode);
				$('#field_location').val(data.location);
				$('#field_website').val(data.website);
				$('#field_on_premise').prop("checked", data.onPremise);
				$('#field_smsUsername').val(data.smsUsername);
				$('#field_smsPassword').val(data.smsPassword);
				
				if (data.logo != null) {
					$('#previewImage').attr('src',
							'data:image/png;base64,' + data.logo);
				}
				// set pid
				companyModel.pid = data.pid;

				// set image byte array
				companyModel.logo = data.logo;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	Company.onChangeIndustry = function() {
		var industry = $('#field_mainIndustry').val();

		if (industry == '-1') {
			alert("please select industry");
			return;
		}

		$.ajax({
			url : companyContextPath + "/industryChange/" + industry,
			method : 'GET',
			success : function(data) {
				loadCompanyTables(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function loadCompanyTables(companies) {

		$('#tBodyCompany').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");

		if (!companies.length > 0) {
			$('#tBodyCompany')
					.html(
							"<tr><td colspan='7' align='center'>No data available</td></tr>");
			return;
		}

		$('#tBodyCompany').html("");
		$
				.each(
						companies,
						function(index, company) {

							$('#tBodyCompany')
									.append(
											"<tr><td>"
													+ company.legalName
													+ "</td><td>"
													+ company.alias
													+ "</td><td>"
													+ company.companyType
													+ "</td><td>"
													+ company.industry
													+ "</td><td>"
													+ company.location
													+ "</td><td>"
													+ spanActivated(
															company.name,
															company.pid,
															company.activated)
													+ "</td><td><i type='button' class='btn btn-blue' title='View Company' onclick='Company.showModalPopup($(\"#viewModal\"),\""
													+ company.pid
													+ "\",0);'>View</i>&nbsp;<i type='button' class='btn btn-blue' title='Edit Company' onclick='Company.showModalPopup($(\"#myModal\"),\""
													+ company.pid
													+ "\",1);'>Edit</i></td></tr>");

						});
	}

	function spanActivated(name1, pid1, activated) {

		var spanActivated = "";
		var name = "'" + name1 + "'";
		var pid = "'" + pid1 + "'";

		if (activated) {
			spanActivated = '<span class="label label-success" onclick="Company.changeStatusCompany('
					+ pid
					+ ','
					+ !activated
					+ ')" style="cursor: pointer;">Activated</span>';
		} else {
			spanActivated = '<span class="label label-danger" onclick="Company.changeStatusCompany('
					+ pid
					+ ','
					+ !activated
					+ ')" style="cursor: pointer;">Deactivated</span>';
		}
		return spanActivated;
	}

	Company.onChangeCountry = function() {
		var countryCode = $('#field_country').val();
		$('#field_state').html("");

		$.ajax({
			url : companyContextPath + "/countryChange/" + countryCode,
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
			}
		});
	}

	Company.onChangeState = function() {
		var stateCode = $('#field_state').val();
		$('#field_district').html("");

		$.ajax({
			url : companyContextPath + "/stateChange/" + stateCode,
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
			}
		});
	}

	Company.changeStatusCompany = function(companyPid, isActivated) {
		companyModel.pid = companyPid;
		companyModel.activated = isActivated;
		if (confirm("Are you sure?")) {
			// update status;changeStatus
			$.ajax({
				url : companyContextPath + "/changeStatus",
				method : 'PUT',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(companyModel),
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
		window.location = companyContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = companyContextPath;
	}

	Company.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showCompany(id);
				break;
			case 1:
				editCompany(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				changeStatusCompany(id);
				break;
			}
		}
		el.modal('show');
	}

	Company.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		companyModel.pid = null; // reset company model;
		$('#previewImage').attr('src', '');
		$('#field_country').prop("disabled", false);
		$('#field_state').prop("disabled", false);
		$('#field_district').prop("disabled", false);
		$('#field_state').html('<option value="-1">Select State</option>');
		$('#field_district')
				.html('<option value="-1">Select District</option>');
		$('#field_legalName').attr('readonly', false);
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