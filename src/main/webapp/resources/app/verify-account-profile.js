if (!this.VerifyAccountProfile) {
	this.VerifyAccountProfile = {};
}

(function() {
	'use strict';

	var verifyAccountProfileContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	var createEditForm = $("#accountProfileForm");
	var accountProfileModel = {
			pid : null,
			name : null,
			alias : null,
			accountTypePid : null,
			defaultPriceLevelPid : null,
			city : null,
			location : null,
			pin : null,
			phone1 : null,
			phone2 : null,
			email1 : null,
			email2 : null,
			whatsAppNo : null,
			address : null,
			description : null,
			creditDays : null,
			creditLimit : null,
			leadToCashStage : null,
			contactPerson : null,
			defaultDiscountPercentage:null,
			closingBalance:0
			
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
			accountTypePid : {
				valueNotEquals : "-1"
			},
			city : {
				required : true,
				maxlength : 100
			},
			location : {
				maxlength : 100
			},
			pin : {
				maxlength : 15
			},
			phone1 : {
				maxlength : 20
			},
			phone2 : {
				maxlength : 20
			},
			email1 : {
				maxlength : 100
			},
			email2 : {
				maxlength : 100
			},
			address : {
				required : true,
				maxlength : 250
			},
			description : {
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
			city : {
				required : "This field is required.",
				maxlength : "This field cannot be longer than 100 characters."
			},
			location : {
				maxlength : "This field cannot be longer than 100 characters."
			},
			pin : {
				maxlength : "This field cannot be longer than 15 characters."
			},
			phone1 : {
				maxlength : "This field cannot be longer than 20 characters."
			},
			phone2 : {
				maxlength : "This field cannot be longer than 20 characters."
			},
			email1 : {
				maxlength : "This field cannot be longer than 100 characters."
			},
			email2 : {
				maxlength : "This field cannot be longer than 100 characters."
			},
			address : {
				required : "This field is required.",
				maxlength : "This field cannot be longer than 250 characters."
			},
			description : {
				maxlength : "This field cannot be longer than 250 characters."
			}
		};
	
	
	

	$(document).ready(function() {
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateAccountProfile(form);
			}
		});
		
// VerifyAccountProfile.loadAccountProfiles();
		VerifyAccountProfile.filter();

		$('input:checkbox.allcheckbox').click(
			function() {
				$(this).closest('table').find(
					'tbody tr td input[type="checkbox"]:visible').prop(
					'checked', $(this).prop('checked'));
			});
		$('#btnDownload')
			.on(
				'click',
				function() {
					var tblVerifyAccountProfile = $("#tblVerifyAccountProfile tbody");
					if (tblVerifyAccountProfile
							.children().length == 0) {
						alert("no values available");
						return;
					}
					if (tblVerifyAccountProfile[0].textContent == "No data available") {
						alert("no values available");
						return;
					}

					downloadXls();
					
				});
		
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

	});

	function downloadXls() {
		// Avoid last column in each row
		$("#tblVerifyAccountProfile th:last-child, #tblVerifyAccountProfile td:last-child").hide();
		var excelName = "verifyAccountProfile";
		var table2excel = new Table2Excel();
		    table2excel.export(document.getElementById('tblVerifyAccountProfile'),excelName);
		$("#tblVerifyAccountProfile th:last-child, #tblVerifyAccountProfile td:last-child").show();
	}

	function searchTable(inputVal) {
		var table = $('#tBodyVerifyAccountProfile');
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

/*
 * VerifyAccountProfile.loadAccountProfiles = function() { var accounttypepid =
 * ""; var value = ""; if ($("#includeInactive").is(':checked')) { value =
 * $('#includeInactive').val(); }
 * 
 * $('#tBodyVerifyAccountProfile').html( "<tr><td colspan='12' align='center'>Please
 * wait...</td></tr>"); $ .ajax({ url : verifyAccountProfileContextPath +
 * "/filterByAccountType", method : 'GET', data : { accountTypePids :
 * accounttypepid, includeInActive : value, }, success :
 * function(accountProfiles) { createtableData(accountProfiles); }, error :
 * function(xhr, error) { onError(xhr, error); } }); }
 */

	VerifyAccountProfile.ImportedStatus = function() {
		var selectedAccount = "";
		$.each($("input[name='accountprofile']:checked"), function() {
			selectedAccount += $(this).val() + ",";
		});
		if (selectedAccount == "") {
			alert("Please Select Account");
			return;
		}
		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : verifyAccountProfileContextPath + "/changeImportedStatus",
				method : 'POST',
				data : {
					accountPids : selectedAccount,
				},
				success : function(data) {

					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	
	function editAccountProfile(id) {
		$.ajax({
			url : verifyAccountProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_accountType').val(data.accountTypePid);
				if (data.defaultPriceLevelPid)
					$('#field_priceLevel').val(data.defaultPriceLevelPid);
				$('#field_city').val(data.city);
				$('#field_location').val(data.location);
				$('#field_pin').val(data.pin);
				$('#field_phone1').val(data.phone1);
				$('#field_phone2').val(data.phone2);
				$('#field_email1').val(data.email1);
				$('#field_email2').val(data.email2);
				$('#field_whatsAppNo').val(data.whatsAppNo);
				$('#field_address').val(data.address);
				$('#field_description').val(data.description);
				$('#field_creditDays').val(data.creditDays);
				$('#field_creditLimit').val(data.creditLimit);
				$('#field_contactPerson').val(data.contactPerson);
				$('#field_defaultDiscountPercentage').val(data.defaultDiscountPercentage);
				$('#field_closingBalance').val(data.closingBalance);
				// set pid
				accountProfileModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function createUpdateAccountProfile(el) {
		accountProfileModel.name = $('#field_name').val();
		accountProfileModel.alias = $('#field_alias').val();
		accountProfileModel.accountTypePid = $('#field_accountType').val();
		accountProfileModel.defaultPriceLevelPid = $('#field_priceLevel').val();
		accountProfileModel.city = $('#field_city').val();
		accountProfileModel.location = $('#field_location').val();
		accountProfileModel.pin = $('#field_pin').val();
		accountProfileModel.phone1 = $('#field_phone1').val();
		accountProfileModel.phone2 = $('#field_phone2').val();
		accountProfileModel.email1 = $('#field_email1').val();
		accountProfileModel.email2 = $('#field_email2').val();
		accountProfileModel.whatsAppNo = $('#field_whatsAppNo').val();
		accountProfileModel.address = $('#field_address').val();
		accountProfileModel.description = $('#field_description').val();
		accountProfileModel.creditDays = $('#field_creditDays').val();
		accountProfileModel.creditLimit = $('#field_creditLimit').val();
		accountProfileModel.contactPerson = $('#field_contactPerson').val();
		accountProfileModel.defaultDiscountPercentage=$('#field_defaultDiscountPercentage').val();
		accountProfileModel.closingBalance=$('#field_closingBalance').val();
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
	
	function createtableData(accountProfiles){
	
		$('#tBodyVerifyAccountProfile')
		.html(
			"<tr><td colspan='13' align='center'>please wait...</td></tr>");
		if (accountProfiles.length == 0) {
			$('#tBodyVerifyAccountProfile')
				.html(
					"<tr><td colspan='13' align='center'>No data available</td></tr>");
			return;
		}
		$('#tBodyVerifyAccountProfile').html("");
		$
			.each(
				accountProfiles,
				function(index, accountProfile) {
					$('#tBodyVerifyAccountProfile')
						.append(
							"<tr><td><input name='accountprofile' type='checkbox' value='" + accountProfile.pid + "' /></td><td>"
							+ accountProfile.userName
							+ "</td><td>"
							+ accountProfile.name
							+ "</td><td>"
							+ accountProfile.alias
							+ "</td><td>"
							+ accountProfile.address
							+ "</td><td>"
							+ accountProfile.accountTypeName
							+ "</td><td>"
							+ accountProfile.phone1
							+ "</td><td>"
							+ accountProfile.email1
							+ "</td><td>"
							+ accountProfile.city
							+ "</td><td>"
							+ accountProfile.location
							+ "</td><td>"
							+ convertDateTimeFromServer(accountProfile.createdDate)
							+ "</td><td>"
							+ (accountProfile.tinNo==null ? "" : accountProfile.tinNo)
							+ "</td><td>"
							+ accountProfile.activated
							+ "</td><td>"
							+"<i class='btn btn-blue entypo-pencil' title='Edit AccountProfile' onclick='VerifyAccountProfile.showModalPopup($(\"#myModal\"),\""
													+ accountProfile.pid
													+ "\",1);'"
							+ "</td></tr>");
				});
	}
	
	
	VerifyAccountProfile.showModalPopup = function(el, id, action) {
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
				deleteForm.attr('action', accountProfileContextPath + "/" + id);
				break;
			case 3:
				showAccountProfileLocation(id);
				break;
			}
		}
		el.modal('show');
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = verifyAccountProfileContextPath;
	}

	VerifyAccountProfile.Deactivated = function() {
		var selectedAccount = "";
		$.each($("input[name='accountprofile']:checked"), function() {
			selectedAccount += $(this).val() + ",";
		});

		if (selectedAccount == "") {
			alert("Please Select Account");
			return;
		}
		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : verifyAccountProfileContextPath + "/changeActivatedStatus",
				method : 'POST',
				data : {
					accountPids : selectedAccount,
				},
				success : function(data) {

					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}

	VerifyAccountProfile.filterByAccountType = function() {
		var accountTypePids = [];
		var value = "";
		console.log(2);
		$("#paccountType").find('input[type="checkbox"]:checked').each(function() {
			accountTypePids.push($(this).val());
		});
		$('#tBodyVerifyAccountProfile').html(
			"<tr><td colspan='13' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : verifyAccountProfileContextPath + "/filterByAccountType",
				method : 'GET',
				data : {
					accountTypePids : accountTypePids.join(","),
					includeInActive : value,
				},

				success : function(accountProfiles) {
					createtableData(accountProfiles);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
	}
	
	
 VerifyAccountProfile.filter = function() {
	 
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyVerifyAccountProfile').html(
			"<tr><td colspan='13' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : verifyAccountProfileContextPath + "/filter",
				type : 'GET',
				data : {
					employeePid : $("#dbEmployee").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val(),
					active : $('#includeInactive').prop('checked')
				},
				success : function(accountProfiles) {
					createtableData(accountProfiles);
				}
			});
 }

	VerifyAccountProfile.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
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

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		accountProfileModel.pid = null; // reset accountProfile model;
	}
	
	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
})();