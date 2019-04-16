// Create a LocationWiseSales object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.LocationWiseSales) {
	this.LocationWiseSales = {};
}

(function() {
	'use strict';

	var locationWiseSalesContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// load today data
		LocationWiseSales.filter();
	});

	function showLocationWiseSales(pid) {
		$.ajax({
			url : locationWiseSalesContextPath + "/" + pid,
			method : 'GET',
			success : function(data) {

				$('#lbl_documentNumber').text(data.documentNumberLocal);
				$('#lbl_user').text(data.userName);
				$('#lbl_document').text(data.documentName);
				$('#lbl_documentDate').text(
						convertDateTimeFromServer(data.createdDate));
				$('#lbl_receiver').text(data.receiverAccountName);
				$('#lbl_supplier').text(data.supplierAccountName);
				$('#lbl_documentTotal').text(data.documentTotal);
				$('#tblVoucherDetails').html("");
				$.each(data.inventoryVoucherDetails, function(index,
						voucherDetail) {
					$('#tblVoucherDetails').append(
							"<tr><td>" + voucherDetail.productName
									+ "</td><td>" + voucherDetail.quantity
									+ "</td><td>" + voucherDetail.freeQuantity
									+ "</td><td>" + voucherDetail.sellingRate
									+ "</td><td>" + voucherDetail.taxPercentage
									+ "</td><td>"
									+ voucherDetail.discountPercentage
									+ "</td><td>" + voucherDetail.batchNumber
									+ "</td><td>" + voucherDetail.rowTotal
									+ "</td></tr>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	LocationWiseSales.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyLocationWiseSales').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : locationWiseSalesContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						locationPid : $("#dbLocation").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(locationWiseSaless) {
						$('#tBodyLocationWiseSales').html("");
						if (locationWiseSaless.length == 0) {
							$('#tBodyLocationWiseSales')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										locationWiseSaless,
										function(index, locationWiseSales) {

											$('#tBodyLocationWiseSales')
													.append(
															"<tr><td>"
																	+ locationWiseSales.employeeName
																	+ "</td><td>"
																	+ locationWiseSales.receiverAccountName
																	+ "</td><td>"
																	+ locationWiseSales.supplierAccountName
																	+ "</td><td>"
																	+ locationWiseSales.documentName
																	+ "</td><td>"
																	+ locationWiseSales.documentTotal
																	+ "</td><td>"
																	+ convertDateTimeFromServer(locationWiseSales.createdDate)
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='LocationWiseSales.showModalPopup($(\"#viewModal\"),\""
																	+ locationWiseSales.pid
																	+ "\",0);'>View Details</button></td></tr>");
										});
					}
				});
	}

	LocationWiseSales.showDatePicker = function() {
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

	LocationWiseSales.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showLocationWiseSales(pid);
				break;
			}
		}
		el.modal('show');
	}

	LocationWiseSales.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
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
})();