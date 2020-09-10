//.........................JS PAGE.............................

//Create a OpeningStock object only if one does not already exist. We create the
//methods in a closure to avoid creating global variables.

if (!this.StockLocationManagement) {
	this.StockLocationManagement = {};
}

(function() {
	'use strict';

	var stockLocationManagementContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#openingStockForm");
	var deleteForm = $("#deleteForm");
	var openingStockModel = {
		pid : null,
		productProfilePid : null,
		batchNumber : null,
		stockLocationPid : null,
		quantity : null

	};

	// Specify the validation rules
	var validationRules = {

		productProfilePid : {
			valueNotEquals : "-1"
		},
		batchNumber : {
			required : true,
			maxlength : 255
		},
		stockLocationPid : {
			valueNotEquals : "-1"
		},
		quantity : {
			maxlength : 100
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		batchNumber : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},

		quantity : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 100 characters."
		}
	};

	$(document).ready(
			function() {

				loadStockLocationDetails();

				$('#btnUpdateStock').on('click', function() {
					updateLiveOpeningStock();
				});

				$('#selectAll').on('click', function() {
					selectAllStocks(this);
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});

			});

	function selectAllStocks(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}
	function loadStockLocationDetails() {
		$('#tbodyStockLocationManagement')
				.html(
						"<tr><td colspan='6' style='text-align:center'>Loading...</td></tr>");

		$
				.ajax({
					url : stockLocationManagementContextPath
							+ "/loadStockLocationDetails",
					type : 'GET',
					success : function(stockLocationManagements) {

						if (stockLocationManagements == null) {
							$('#tbodyStockLocationManagement')
									.html(
											"<tr><td colspan='6' style='text-align:center'>No Data Available</td></tr>");
							return;
						}

						$('#tbodyStockLocationManagement').html("");

						$
								.each(
										stockLocationManagements,
										function(index, stockLocationManagement) {

											$('#tbodyStockLocationManagement')
													.append(
															"<tr><td>"
																	+ convertDateTimeFromServer(stockLocationManagement.temporaryStockLocationDate)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='StockLocationManagement.showModalPopup($(\"#viewModal\"),\""
																	+ stockLocationManagement.stockLocationPid
																	+ "\",0);'>View Stocks</button>"
																	+ "</td><td><input type='checkbox' name='stockLocation' class='check-one' value='"
																	+ stockLocationManagement.stockLocationPid
																	+ "' />&nbsp;&nbsp;"
																	+ stockLocationManagement.stockLocationName
																	+ "</td><td>"
																	+ stockLocationManagement.userName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(stockLocationManagement.liveStockLocationDate)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='StockLocationManagement.showModalPopup($(\"#viewModal\"),\""
																	+ stockLocationManagement.stockLocationPid
																	+ "\",1);'>View Stocks</button>"
																	+ "</td></tr>");
										});

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}

	function updateLiveOpeningStock() {
		var selectedStockLocation = "";

		$.each($("input[name='stockLocation']:checked"), function() {
			selectedStockLocation += $(this).val() + ",";
		});

		console.log(selectedStockLocation);

		if (selectedStockLocation == "") {
			alert("Please select Stock Locations");
			return;
		}

		if (confirm("Are you sure?")) {
			$.ajax({
				url : stockLocationManagementContextPath + "/updateStocks/"
						+ selectedStockLocation,
				method : 'GET',
				beforeSend : function() {
					// Show image container
					$("#loader").modal('show');

				},
				success : function(data) {
					$("#loader").modal('hide');
					loadStockLocationDetails();
				}
			});

		}

	}

	StockLocationManagement.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showTemporaryOpeningStock(pid);
				break;
			case 1:
				showLiveOpeningStock(pid);
				break;
			}
		}
		el.modal('show');
	}

	StockLocationManagement.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function showTemporaryOpeningStock(pid) {
		var stockLoctionPid = pid;
		var stockLocation = "Back End";
		$.ajax({
			url : stockLocationManagementContextPath
					+ "/temporaryStockLocation/" + stockLoctionPid,
			method : 'GET',
			success : function(data) {
				showOpeningStockData(data, stockLocation);
			}
		});
	}

	function showLiveOpeningStock(pid) {
		var stockLoctionPid = pid;
		var stockLocation = "Live";
		$.ajax({
			url : stockLocationManagementContextPath + "/liveStockLocation/"
					+ stockLoctionPid,
			method : 'GET',
			success : function(data) {
				showOpeningStockData(data, stockLocation);
			}
		});
	}

	function showOpeningStockData(datas, stockLocation) {

		$("#lblModalHeading").text(
				"Opening Stock Details ( " + stockLocation + " )");

		$('#tbodyOpeningStock').html("Loading....");

		if (datas.length == 0) {
			$('#tbodyOpeningStock')
					.html(
							"<tr><td colspan='3' style='text-align:center'>No Data Available</td></tr>");
			return;
		}

		$('#tbodyOpeningStock').html("");
		$.each(datas, function(index, data) {

			$('#tbodyOpeningStock').append(
					"<tr><td>" + data.productProfileName + "</td><td>"
							+ data.stockLocationName + "</td><td>"
							+ data.quantity + "</td></tr>");
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

function convertDateTimeFromServer(date) {
	if (date) {
		return moment(date).format('MMM DD YYYY, h:mm:ss a');
	} else {
		return "";
	}
}

function toTimeZone(time, zone) {
	var format = 'YYYY/MM/DD HH:mm:ss ZZ';
	return moment(time, format).tz(zone).format(format);
}
function convertUTCDateToDate(utcDate) {
	var date = new Date(utcDate)
	var dd = date.getDate();
	var mm = date.getMonth() + 1;
	var yyyy = date.getFullYear();
	if (dd < 10) {
		dd = '0' + dd
	}
	if (mm < 10) {
		mm = '0' + mm
	}
	;
	return utcDate = dd + '-' + mm + '-' + yyyy
}
