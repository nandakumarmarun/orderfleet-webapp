// Create a AccountProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

(function() {
	'use strict';

	var userAssignAccountContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		// table search
		$('#btnfindAccountProfile').click(function() {
			getUserAccountProfiles();
		});
		
		$('#btnDownload').on('click', function() {
			var tblUserAssignAccountProfile = $("#tblUserAssignAccountProfile tbody");
			
			if (tblUserAssignAccountProfile
					.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblUserAssignAccountProfile[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			downloadXls();
			
		});

	});

	
	function downloadXls() {
		// Avoid last column in each row
		$("#tblUserAssignAccountProfile th:last-child, #tblUserAssignAccountProfile td:last-child").hide();
		
		var excelName = "userAssignedAccountProfile";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblUserAssignAccountProfile'),excelName);
		 $("#tblUserAssignAccountProfile th:last-child, #tblUserAssignAccountProfile td:last-child").show();
	}
	
	function getUserAccountProfiles() {
		var userPid = $('#field_user').val();
		
		if (userPid == -1) {
			alert("please select user");
			return;
		}
		$('#totalcount').text(0);
		$('#tBodyAccountProfile').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : userAssignAccountContextPath + "/getAccountProfiles/"
							+ userPid,
					method : 'GET',
					success : function(userAssignAccounts) {
						$('#tBodyAccountProfile').html("");
						if (userAssignAccounts.length == 0) {
							$('#tBodyAccountProfile')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										userAssignAccounts,
										function(index, userAssignAccount) {
											$('#totalcount').text(
													userAssignAccounts.length);

											var activated = "";
											if (userAssignAccount.activated) {
												activated = "<span class='label label-success'style='cursor: pointer;''>TRUE</span>";
											} else {
												activated = "<span class='label label-danger'style='cursor: pointer;'>FALSE</span>";
											}

											$('#tBodyAccountProfile')
													.append(
															"<tr><td>"
																	+ userAssignAccount.name
																	+ "</td><td>"
																	+ userAssignAccount.accountTypeName
																	+ "</td><td>"
																	+ userAssignAccount.city
																	+ "</td><td>"
																	+ userAssignAccount.address
																	+ "</td><td>"
																	+ userAssignAccount.phone1
																	+ "</td><td>"
																	+ userAssignAccount.email1
																	+ "</td><td>"
																	+ userAssignAccount.accountStatus
																	+ "</td><td>"
																	+ activated
																	+ "</td></tr>");
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userAssignAccountContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userAssignAccountContextPath;
	}

	function searchTable(inputVal) {
		var table = $('#tBodyAccountProfile');
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

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
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