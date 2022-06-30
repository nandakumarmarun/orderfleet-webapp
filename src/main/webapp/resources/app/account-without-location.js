// Create a AccountProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountWithoutLocation) {
	this.AccountWithoutLocation = {};
}

(function() {
	'use strict';

	var accountWithoutLocationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	
	var accountProfileModel = {
		pid : null,
		name : null,
		
	};

	
	$(document)
			.ready(
					function() {

						$('.selectpicker').selectpicker();

						
						AccountWithoutLocation.loadAccountProfiles();

						$('#btnSaveLocations').on('click', function() {
							saveAssignedLocations();
						});
						
					});

	AccountWithoutLocation.loadAccountProfiles = function() {
		

		$('#tBodyAccountProfile').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : accountWithoutLocationContextPath + "/load",
			method : 'GET',
			data : {
				
			},
			success : function(accountProfiles) {
				addTableBodyvalues(accountProfiles);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

		function addTableBodyvalues(accountProfiles) {

		$('#tBodyAccountProfile').html("");
		if (accountProfiles.length == 0) {
			$('#tBodyAccountProfile')
					.html(
							"<tr><td colspan='9' align='center'>No data available</td></tr>");
			return;
		}
		$('#tBodyAccountProfile').html("");
		
		$
				.each(
						accountProfiles,
						function(index, accountProfile) {
							
							$('#tBodyAccountProfile')
									.append(
											"<tr>"
													+ accountProfile.pid
													+"<td>"
													+ accountProfile.name
													+ "</span></td>"
													+ "<td><button type='button' class='btn btn-info' onclick='AccountWithoutLocation.assignLocations($(\"#assignLocationModal\"),\""
													+ accountProfile.pid
													+ "\");'>Assign Location</button></td></tr>");
						});
	}

	var accProfilePid = "";

	AccountWithoutLocation.assignLocations = function(el, pid, type) {
		// locationModel.pid = pid;

		accProfilePid = pid;

//		$("input[name='filter'][value='all']").prop("checked", true);
//		$("#search").val("");
//		searchTable("");
//
		// clear all check box
		$("#divLocations input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : accountWithoutLocationContextPath + "/locations",
			type : "GET",
			data : {
				accountProfilePid : accProfilePid
			},
			success : function(locations) {
				if (locations) {
					$.each(locations, function(index, location) {
						$(
								"#divLocations input:checkbox[value="
										+ location.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		el.modal('show');

	}

	function saveAssignedLocations() {

		$(".error-msg").html("");
		var selectedLocations = "";

		$.each($("input[name='location']:checked"), function() {
			selectedLocations += $(this).val() + ",";
		});

		console.log(selectedLocations);

		var str_array = selectedLocations.split(',');

		console.log(str_array.length);

		if ((str_array.length - 1) > 1) {
			$(".error-msg").html("Please select only one Location");
			return;
		}

		if (selectedLocations == "") {
			$(".error-msg").html("Please select Locations");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : accountWithoutLocationContextPath + "/assign-locations",
			type : "POST",
			data : {
				locationPid : selectedLocations,
				assignedAccountProfiles : accProfilePid
			},
			success : function(status) {
//				$("#assignLocationModal").modal("hide");
				window.location = accountWithoutLocationContextPath;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
			
		});
	}

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountWithoutLocationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = accountWithoutLocationContextPath;
	}

	

	
	
	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
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