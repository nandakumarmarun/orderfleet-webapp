if (!this.UnableToFindLocations) {
	this.UnableToFindLocations = {};
}
(function() {
	'use strict';

	var UnableToFindLocationsContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var cmpnyPid = null;

	$(document).ready(function() {
		
		$('.selectpicker').selectpicker();

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

		$('#btnLocationTypeButton').on('click', function() {
			var locationType = $('#dbLocationType').val();
			loadLocationByLocationType(locationType);
		});
		UnableToFindLocations.getUnableToFindLocations();
	});

	UnableToFindLocations.getUnableToFindLocations = function() {
		$("#lbl_totalUnableLoc").html("");
		$("#lbl_totalNoLoc").html("");

		var totalUnableLocations = Number(0);
		var totalNoLocations = Number(0);
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tbodyUnableToFindLocations').html(
				"<tr><td colspan='3' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : UnableToFindLocationsContextPath + "/filter",
					type : "GET",
					data : {
						companyPid : $('#dbCompany').val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						locationType : $('#dbLocationTypes').val()
					},
					success : function(locationMaps) {
						$('#tbodyUnableToFindLocations').html("");
						if (locationMaps.length == 0) {
							$('#tbodyUnableToFindLocations')
									.html(
											"<tr><td colspan='3' align='center'>No data available</td></tr>");
							return;
						}

						$
								.map(
										locationMaps,
										function(company, unableLocation) {
											
											var loc = company.pincode != "" ? parseInt(company.pincode)
													: 0;
											var noLoc = company.address2 != "" ? parseInt(company.address2)
													: 0;
											totalUnableLocations = totalUnableLocations + loc;
											totalNoLocations = totalNoLocations + noLoc;
											$('#tbodyUnableToFindLocations')
													.append(
															"<tr><td><a style='color: #2C7BD0; cursor:pointer;' onclick='UnableToFindLocations.showModalPopup($(\"#viewModal\"),\""
																	+ company.pid
																	+ "\",0);'>"
																	+ company.legalName
																	+ "</a></td><td>"
																	+ company.pincode
																	+ "</td><td>"
																	+ company.address2
																	+ "</td></tr>");
										});
						$("#lbl_totalUnableLoc").append(totalUnableLocations);
						$("#lbl_totalNoLoc").append(totalNoLocations);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function loadLocationByLocationType(locationType) {
		exeTaskPids = "";
		$('#tbodyUnableListLocations')
				.html(
						"<tr><td colspan='4' align='center'>Loading Data....</td></tr>");
		$
				.ajax({
					url : UnableToFindLocationsContextPath + "/find",
					type : 'GET',
					data : {
						companyPid : cmpnyPid,
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						locationType : locationType
					},
					success : function(locations) {
						$('#tbodyUnableListLocations').html("");
						if (locations.length == 0) {
							$('#tbodyUnableListLocations')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
							return;
						}
						$.each(locations, function(key, location) {
							exeTaskPids += location.pid + ",";
							$('#tbodyUnableListLocations').append(
									"<tr><td>" + location.locationType
											+ "</td><td>" + location.latitude
											+ "</td><td>" + location.longitude
											+ "</td><td>" + location.location
											+ "</td></tr>");
						});
					}
				});
	}

	UnableToFindLocations.locationUpdate = function() {
		console.log(exeTaskPids);
		$("#viewModal").modal('hide');
		$('#tbodyUnableToFindLocations').html(
				"<tr><td colspan='2' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : UnableToFindLocationsContextPath + "/update",
			method : 'GET',
			data : {
				exeTaskPids : exeTaskPids,
			},
			success : function(data) {

				UnableToFindLocations.getUnableToFindLocations();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	var exeTaskPids = "";

	function showUnableToFindLocations(id) {
		cmpnyPid = id;
		$('#tbodyUnableListLocations').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : UnableToFindLocationsContextPath + "/find",
					method : 'GET',
					data : {
						companyPid : id,
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						locationType : $('#dbLocationTypes').val()
					},
					success : function(locations) {
						$('#tbodyUnableListLocations').html("");
						if (locations.length == 0) {
							$('#tbodyUnableListLocations')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
						}

						$.each(locations, function(index, location) {
							exeTaskPids += location.pid + ",";
							$('#tbodyUnableListLocations').append(
									"<tr><td>" + location.locationType
											+ "</td><td>" + location.latitude
											+ "</td><td>" + location.longitude
											+ "</td><td>" + location.location
											+ "</td></tr>");
						});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	UnableToFindLocations.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				showUnableToFindLocations(id);
				break;
			}
		}
		el.modal('show');
	}

	UnableToFindLocations.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
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

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
})();