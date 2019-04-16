// Create a StockLocation object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.StockLocation) {
	this.StockLocation = {};
}

var inwardOutwardStockLocationModel = {

};

(function() {
	'use strict';
	var inwardOutwardStockLocationContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#inwardOutwardStockLocationForm");
	var deleteForm = $("#deleteForm");

	$(document).ready(
			function() {

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				$('#loadStockLocation').on('click', function() {
					loadInwardOutward();
					loadInwardOutwardStockLocation();
				});

				$('#btnSaveStockLocations').on('click', function() {
					saveInwardOutwardStockLocation();
				});

				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function searchTable(inputVal) {
		var table = $('#tbodyStockLocations');
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

	function loadInwardOutwardStockLocation() {

		$('#myModal').modal('show');

	}

	function saveInwardOutwardStockLocation() {
		//		
		$(".error-msg").html("");
		var selectedStockLocations = "";

		$.each($("input[name='stockLocation']:checked"), function() {
			selectedStockLocations += $(this).val() + ",";
		});

		if (selectedStockLocations == "") {
			$(".error-msg").html("Please select Inward Outward StockLocation");
			return;
		}
		$.ajax({
			url : inwardOutwardStockLocationContextPath
					+ "/assignStockLocations",
			type : "POST",
			data : {
				assignedStockLocations : selectedStockLocations,
			},
			success : function(status) {
				$("#myModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function loadInwardOutward() {
		// clear all check box
		$("#divStockLocations input:checkbox").attr('checked', false);
		$.ajax({
			url : inwardOutwardStockLocationContextPath + "/load",
			type : "GET",
			success : function(data) {
				console.log(data);

				if (data) {
					$.each(data, function(index, stocklocation) {
						$(
								"#divStockLocations input:checkbox[value="
										+ stocklocation.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = inwardOutwardStockLocationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = inwardOutwardStockLocationContextPath;
	}

	StockLocation.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		inwardOutwardStockLocationModel.pid = null; // reset
		// inwardOutwardStockLocation
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