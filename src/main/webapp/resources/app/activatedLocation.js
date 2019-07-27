// Create a SalesTargetGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ActivatedLocation) {
	this.ActivatedLocation = {};
}

(function() {
	'use strict';

	var activatedLocationsContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {

		getActivatedLocations();

		$('#btnSaveLocations').on('click', function() {
			saveAssignedLocations();
		});

		$('#btnSearchLocations').click(function() {
			searchTableLocations($("#searchLocations").val());
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});

	});

	function searchTableLocations(inputVal) {
		var table = $('#tBodyLocation');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
					var regExp = new RegExp(inputVal, 'i');
					if (regExp.test($(td).text())) {
						found = true;
						return false;
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function getActivatedLocations() {

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#searchLocations").val("");
		searchTableLocations("");

		// clear all check box
		$("#divLocations input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : activatedLocationsContextPath + "/getLocations",
			type : "GET",
			success : function(assignedLocations) {
				if (assignedLocations) {
					$.each(assignedLocations, function(index, location) {
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

	}

	function saveAssignedLocations() {

		$(".error-msg").html("");
		var selectedLocations = "";

		$.each($("input[name='location']:checked"), function() {
			selectedLocations += $(this).val() + ",";
		});

		console.log(selectedLocations);
		if (selectedLocations == "") {
			$(".error-msg").html("Please select Locations");
			return;
		}
		$.ajax({
			url : activatedLocationsContextPath + "/saveActivatedLocations",
			type : "POST",
			data : {
				assignedLocations : selectedLocations,
			},
			success : function(status) {
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = activatedLocationsContextPath;
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