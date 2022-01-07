// Create a AccountProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountProfileCSDManagement) {
	this.AccountProfileCSDManagement = {};
}

(function() {
	'use strict';

	var accountProfile_CSD_ManagementContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var accountProfileCSDmanagementModel = {
		pid : null,
		name : null,
		address : null,
		countryId : null,
		stateId : null,
		districtId : null

	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		address : {
			required : true,
			maxlength : 250
		},
		countryId : {
			valueNotEquals : "-1"
		},
		stateId : {
			valueNotEquals : "-1"
		},
		districtId : {
			valueNotEquals : "-1"
		}

	};

	// Specify the validation error messages
//	var validationMessages = {
//		name : {
//			required : "This field is required.",
//			maxlength : "This field cannot be longer than 255 characters."
//		},
//
//		address : {
//			required : "This field is required.",
//			maxlength : "This field cannot be longer than 250 characters."
//		}
//
//	};

	$(document).ready(function() {
		console.log("Entered in to js")

//		$("#dbCountrycreate-" +pid ).change(function() {
//			loadStatescreate();
//		});

//		$("#dbStatecreate-" + pid).change(function() {
//			loadDistrictscreate();
//		});

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

	});

	var $row = $('#tblAccountProfile tr');
	$("#search").on("keyup", function() {
		var value = $(this).val().toUpperCase();

		$("#tblAccountProfile tr").each(function(index) {

			if (index !== 0) {
				$row = $(this);

				var $tdElement = $row.find("td:first");
				var id = $tdElement.text();

				var matchedIndex = id.indexOf(value);

				if (matchedIndex != 0) {
					$row.hide();
				} else {

					$row.show();
				}
			}
		});
	});

//	function loadStatescreate(id) {
//
//		var countryId = $("#dbCountrycreate-" +id).val();
//        console.log(countryId);
//		
//		$.ajax({
//			url : accountProfile_CSD_ManagementContextPath + "/loadStates",
//			method : 'GET',
//			data : {
//				countryId : countryId
//			},
//			success : function(states) {
//				$("#dbStatecreate-" + id).html(
//						"<option value='-1'>  Select States </option>")
//				$.each(states, function(index, state) {
//
//					$("#dbStatecreate-" + id).append(
//							"<option value='" + state.id + "'>" + state.name
//									+ "</option>");
//				});
//			},
//			error : function(xhr, error) {
//
//				onError(xhr, error);
//			}
//		});
//
//	}
//
//	function loadDistrictscreate(id) {
//
//		var stateId = $("#dbStatecreate-" +id).val();
//		console.log(stateId)
//		$.ajax({
//			url : accountProfile_CSD_ManagementContextPath + "/loadDistricts",
//			method : 'GET',
//			data : {
//				stateId : stateId
//			},
//			success : function(districts) {
//				$("#dbDistrictcreate-"+ id).html(
//						"<option value='-1'>  Select District  </option>")
//				$.each(districts, function(index, district) {
//
//					$("#dbDistrictcreate-"+ id).append(
//							"<option value='" + district.id + "'>"
//									+ district.name + "</option>");
//				});
//			},
//			error : function(xhr, error) {
//
//				onError(xhr, error);
//			}
//		});
//
//	}

	function saveAccountProfiles(id)
	{
		console.log("updating");
		$(".error-msg").html("");
	
		 if ($('#dbCountrycreate').val() == '-1') {
		 $(".error-msg").html("Please select Country");
		 return false;
		 }
		 if ($('#dbStatecreate').val() == '-1') {
		 $(".error-msg").html("Please select State");
		 return false;
		 }
		 if ($('#dbDistrictcreate').val() == '-1') {
		 $(".error-msg").html("Please select District");
		 return false;
		 }
		
		var countryId = $("#dbCountrycreate-"+id).val();
		var stateId =  $("#dbStatecreate-" +id).val();
		var districtId = $("#dbDistrictcreate-"+id).val();
		$.ajax({
			url : accountProfile_CSD_ManagementContextPath +"/save-csd",
			type : "POST",
			 data : {
				 countryId:countryId,
				 stateId : stateId,
				 districtId :  districtId,
				    accountPid:id
				},
			 success : function(status) {
//				 console.log(Success);
				 window.location = accountProfile_CSD_ManagementContextPath;
				  },
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
	}
	// function createUpdateAccountProfile(el) {
	//
	// $(".error-msg").html("");
	// if ($('#dbCountrycreate').val() == '-1') {
	// $(".error-msg").html("Please select Country");
	// return false;
	// }
	// if ($('#dbStatecreate').val() == '-1') {
	// $(".error-msg").html("Please select State");
	// return false;
	// }
	// if ($('#dbDistrictcreate').val() == '-1') {
	// $(".error-msg").html("Please select District");
	// return false;
	// }
	// accountProfileModel.name = $('#field_name').val();
	//		
	// accountProfileModel.countryId = $('#dbCountrycreate').val();
	// accountProfileModel.stateId = $('#dbStatecreate').val();
	// accountProfileModel.districtId = $('#dbDistrictcreate').val();
	//		
	//		
	// accountProfileModel.address = $('#field_address').val();
	//		
	// $.ajax({
	// method : $(el).attr('method'),
	// url : $(el).attr('action'),
	// contentType : "application/json; charset=utf-8",
	// data : JSON.stringify(accountProfileModel),
	// success : function(data) {
	// onSaveSuccess(data);
	// },
	// error : function(xhr, error) {
	// onError(xhr, error);
	// }
	// });
	// }
	//
	//	
	//	

	

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountProfile_CSD_ManagementContextPath;
	}
		
	AccountProfileCSDManagement.showModalPopup = function(el, id, action) {
	 $(".error-msg").html("");
	 resetForm();
	 if (id) {
	 switch (action) {
	 case 0:
	 saveAccountProfiles(id);
	 break;
	 case 1:
		 loadStatescreate(id);
	 break;
	 case 2:
		 loadDistrictscreate(id);
	
	 }
	 }
	 el.modal('show');
	 }

	function resetForm() {
		$('.alert').hide();
		accountProfileCSDmanagementModel.pid = null; // reset accountProfile
														// model;
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