// Create a DocumentStockLocationDestination object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DocumentStockLocationDestination) {
	this.DocumentStockLocationDestination = {};
}

(function() {
	'use strict';

	var documentStockLocationDestinationContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var documentStockLocationDestinationModel = {
		documentPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveStockLocationDestination').on('click', function() {
			saveAssignedStockLocationDestinations();
		});
	});

	function loadDocumentStockLocationDestination(documentPid) {
		console.log(documentPid);
		$("#stockLocationDestinationsCheckboxes input:checkbox").attr('checked', false);
		$("#stockLocationDestinationsCheckboxes input:checkbox[name='defaultStockLocation']").prop("disabled", true);
		documentStockLocationDestinationModel.documentPid = documentPid;
		// clear all check box
		$.ajax({
			url : documentStockLocationDestinationContextPath + "/"
					+ documentPid,
			type : "GET",
			success : function(documentStockLocations) {
				if (documentStockLocations) {
					$
							.each(
									documentStockLocations,
									function(index,documentStockLocation) {
										console.log(documentStockLocation);
										$(
												"#stockLocationDestinationsCheckboxes input:checkbox[name='stockLocationDestination'][value="
														+ documentStockLocation.stockLocationPid
														+ "]").prop(
												"checked", true);

										$(
												"#stockLocationDestinationsCheckboxes input:checkbox[name='defaultStockLocation'][value="
														+ documentStockLocation.stockLocationPid
														+ "]").prop(
												"disabled", false);
										$(
												"#stockLocationDestinationsCheckboxes input:checkbox[name='defaultStockLocation'][value="
														+ documentStockLocation.stockLocationPid
														+ "]")
												.prop(
														"checked",
														documentStockLocation.isDefault);

									});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedStockLocationDestinations() {
		$(".error-msg").html("");
		var selectedStockLocationDestinations = "";

		$.each($("input[name='stockLocationDestination']:checked"), function() {
			selectedStockLocationDestinations += $(this).val() + ",";
		});

		if (selectedStockLocationDestinations == "") {
			$(".error-msg").html("Please select Stock Location Destination");
			return;
		}
		
		var defaultStockLocationPid = $("input[name='defaultStockLocation']:checked").val();
		if(!defaultStockLocationPid)
			defaultStockLocationPid = "no";
		
		$(".error-msg").html("Please wait.....");
		$
				.ajax({
					url : documentStockLocationDestinationContextPath + "/save",
					type : "POST",
					data : {
						documentPid : documentStockLocationDestinationModel.documentPid,
						assignedStockLocationDestinations : selectedStockLocationDestinations,
						defaultStockLocationPid : defaultStockLocationPid
					},
					success : function(status) {
						$("#stockLocationDestinationsModal").modal("hide");
						onSaveSuccess(status);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentStockLocationDestinationContextPath;
	}

	DocumentStockLocationDestination.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadDocumentStockLocationDestination(id);
				break;
			}
		}
		el.modal('show');
	}

	DocumentStockLocationDestination.onSelectStockLocation = function(obj) {
		var tr = $(obj).closest("tr");
		if (obj.checked) {
			$(tr).find('td').eq(2).find("input").prop('disabled', false);
		} else {
			$(tr).find('td').eq(2).find("input").prop('disabled', true);
		}
	}
	DocumentStockLocationDestination.onSelectDefaultStockLocation = function(obj) {
		if (obj.checked) {
			$("#stockLocationDestinationsCheckboxes input:checkbox[name='defaultStockLocation']")
					.prop("checked", false);
			$(obj).prop("checked", true);
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