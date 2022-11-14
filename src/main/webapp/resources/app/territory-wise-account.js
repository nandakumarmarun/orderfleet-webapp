if (!this.TerritoryWiseAccount) {
	this.TerritoryWiseAccount = {};
}

(function() {
	'use strict';

	var territoryWiseAccountContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		
		$( "#dbActivated" ).change(function() {
			changeButton();
			});
		// load today data
		TerritoryWiseAccount.filter();
		
		changeButton();
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
		
		$('#btnDownload').on('click', function() {
			var tblTerritoryWiseAccounts = $("#tblTerritoryWiseAccounts tbody");
			
			if (tblTerritoryWiseAccounts
					.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblTerritoryWiseAccounts[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			downloadXls();
			
		});
		
		$('#btnSaveLocations').on('click', function() {
			saveAssignedLocations();
		});
	});

	function changeButton(){
		if($('#dbActivated').val()=="true"){
			$("#buttonActivated").text("Deactivate");
			 $('#buttonActivated').removeClass('btn-success').addClass('btn-danger');
		}else{
			$("#buttonActivated").text("Activate");
			 $('#buttonActivated').removeClass('btn-danger').addClass('btn-success');
		}
	}

	TerritoryWiseAccount.changeStatus = function() {
		var status=false;
		if($('#dbActivated').val()=="true"){
			status=false;
		}else{
			status=true;
		}
		$(".error-msg").html("");
		var selectedAccountProfile = "";

		$.each($("input[name='accountprofile']:checked"), function() {
			selectedAccountProfile += $(this).val() + ",";
		});

		if (selectedAccountProfile == "") {
			alert("Please Account Profile");
			return;
		}
		$.ajax({
			url : territoryWiseAccountContextPath + "/activateAccountProfile",
			type : "POST",
			data : {
				accountprofiles : selectedAccountProfile,
				status:status
			},
			success : function(status) {
				$('input:checkbox.allcheckbox').removeAttr('checked');
				TerritoryWiseAccount.filter();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	TerritoryWiseAccount.filter = function() {
		
		$('#tBodyTerritoryWiseAccount').html(
				"<tr><td colspan='5' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : territoryWiseAccountContextPath + "/filter",
					type : 'GET',
					data : {
						territoryPid : $("#dbTerrtory").val(),
						activated : $("#dbActivated").val()
					},
					success : function(accountProfileDTOs) {
						$('#tBodyTerritoryWiseAccount').html("");
						if (accountProfileDTOs.length == 0) {
							$('#tBodyTerritoryWiseAccount')
									.html(
											"<tr><td colspan='5' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										accountProfileDTOs,
										function(index, territoryWiseAccount) {
											$('#tBodyTerritoryWiseAccount')
													.append(
															"<tr><td><input type='checkbox' name='accountprofile' value='"+territoryWiseAccount.pid+"'/>"
																	+ "</td><td>"
																	+ territoryWiseAccount.name
																	+ "</td><td>"
																	+ territoryWiseAccount.address
																	+ "</td><td>"
																	+ territoryWiseAccount.phone1
																	+ "</td><td>"
																	+ (territoryWiseAccount.alias == null ? "" : territoryWiseAccount.alias)
																	+ "</td><td>"
																	+ (territoryWiseAccount.description == null ? "" : territoryWiseAccount.description)
																	+ "</td><td><button type='button' class='btn btn-info' onclick='TerritoryWiseAccount.assignLocations($(\"#assignLocationModal\"),\""
													                + territoryWiseAccount.pid
												                  	+ "\");'>Assign Location</button></td></tr>");
										});
					}
				});
	}

	function downloadXls() {
		// Avoid last column in each row
		$("#tblTerritoryWiseAccounts th:first-child, #tblTerritoryWiseAccounts td:first-child").hide();
		
		var excelName = "territoryWiseAccounts";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblTerritoryWiseAccounts'),excelName);
		 $("#tblTerritoryWiseAccounts th:first-child, #tblTerritoryWiseAccounts td:first-child").show();
	}
	
	var accProfilePid = "";

	TerritoryWiseAccount.assignLocations = function(el, pid, type) {
		// locationModel.pid = pid;

		accProfilePid = pid;
		// clear all check box
		$("#divLocations input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : territoryWiseAccountContextPath + "/locations",
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
			url : territoryWiseAccountContextPath + "/assign-locations",
			type : "POST",
			data : {
				locationPid : selectedLocations,
				assignedAccountProfiles : accProfilePid
			},
			success : function(status) {
				$("#assignLocationModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
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