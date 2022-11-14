if (!this.ItemWiseSale) {
	this.ItemWiseSale = {};
}

(function() {
	'use strict';

	var itemWiseSaleContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		var employeePid = getParameterByName('user-key-pid');
		getEmployees(employeePid);

		$("#dbDocumentType").change(function() {
			loadAllDocumentByDocumentType();
		});

		$('#downloadXls').on('click', function() {
			var tblItemWiseSale = $("#tblItemWiseSale tbody");
			if (tblItemWiseSale.children().length == 0) {
				alert("no values available");
				return;
			}
			downloadXls();
		});
	});

	ItemWiseSale.changeToSort = function() {
		$("#sortClass").attr('class', 'active');
		$("#filterClass").attr('class', '');
		$("#filter").css("display", "none");
		$("#sort").css("display", "block");
	}

	ItemWiseSale.changeToFilter = function() {
		$("#filterClass").attr('class', 'active');
		$("#sortClass").attr('class', '');
		$("#sort").css("display", "none");
		$("#filter").css("display", "block");
	}

	ItemWiseSale.loadStockLocations = function() {

	}
	function downloadXls() {
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblItemWiseSale").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = $("#dbDocumentType option:selected").text();

		clonedTable.table2excel({
			// exclude CSS class
			// exclude : ".odd .even",
			// name : "Dynamic Document Form",
			filename : excelName, // do not include extension
		// fileext : ".xls",
		// exclude_img : true,
		// exclude_links : true,
		// exclude_inputs : true
		});
	}

	function loadAllDocumentByDocumentType() {
		if ($('#dbDocumentType').val() == "no") {
			$("#dbDocument").html("<option>All</option>");
			alert("Please Select Document Type");
			return;
		}
		var documentType = $('#dbDocumentType').val();
		$("#dbDocument").html("<option>Documents loading...</option>")
		$.ajax({
			url : itemWiseSaleContextPath + "/load-document",
			type : 'GET',
			data : {
				voucherType : documentType,
			},
			success : function(documents) {
				$("#dbDocument").html("<option value='no'>All</option>")
				$.each(documents, function(key, document) {
					$("#dbDocument").append(
							"<option value='" + document.pid + "'>"
									+ document.name + "</option>");
				});
			}
		});
	}

	ItemWiseSale.filter = function() {
		var value = "";
		var empPids = $('#dbEmployee').val();
		var accountPids = $('#dbAccount').val();
		var productGroupPids = $('#dbproductGroup').val();
		var territtoryPids = $('#dbTerittory').val();

		
		$('#lblTotal').text("0");
		if ($("#includeStockLocationDetails").is(':checked')) {
			value = $('#includeStockLocationDetails').val();
		}

		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if ("no" == empPids || "Dashboard Employee" == empPids) {
			empPids = $('#dbEmployee option').map(function() {
				return $(this).val();
			}).get().join(',');
		}

//		if ("-1" == accountPids) {
//			accountPids = $('#dbAccount option').map(function() {
//				return $(this).val();
//			}).get().join(',');
//		}

		if ("-1" == productGroupPids) {
			productGroupPids = $('#dbproductGroup option').map(function() {
				return $(this).val();
			}).get().join(',');
		}

//		if ("-1" == territtoryPids) {
//			territtoryPids = $('#dbTerittory option').map(function() {.0
//				return $(this).val();
//			}).get().join(',');
//		}

		if ($("#dbDocumentType").val() == "no") {
			alert("Please Select Document Type")
			return;
		}

		var rows = "";
		$('#tHeadItemWiseSale').html("");
		if (value == "no") {
			$('#tHeadItemWiseSale')
					.html(
							"<tr><th>Date</th><th>OrderID</th><th>Employee</th><th>Receiver Account Profile</th><th>Supplier Account Profile</th><th>Customer Location</th><th>Source</th><th>Destination</th><th>Category</th><th>Item</th><th>ProductGroup</th><th>Territory</th><th>Quantity</th><th>Amount</th></tr>");
			$('#tBodyItemWiseSale')
					.html(
							"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		} else {
			$('#tHeadItemWiseSale')
					.html(
							"<tr><th>Date</th><th>OrderID</th><th>Employee</th><th>Receiver Account Profile</th><th>Supplier Account Profile</th><th>Customer Location</th><th>Category</th><th>Item</th><th>ProductGroup</th><th>Territory</th><th>Quantity</th><th>Unit Quantity</th><th>Volume</th><th>Rate</th><th>Amount(tax,disc,etc..)</th></tr>");
			$('#tBodyItemWiseSale')
					.html(
							"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		}
		$
		.ajax({
			url : itemWiseSaleContextPath + "/filter",
			type : 'GET',
			data : {
				sort : $('input[name=sorting]:checked').val(),
				order : $('input[name=order]:checked').val(),
				categoryPids : "",
				groupPids : productGroupPids,
				terittoryPids :  $("#dbTerittory").val(),
				voucherType : $("#dbDocumentType").val(),
				documentPid : $("#dbDocument").val(),
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val(),
				stockLocations : "",
				employeePid : empPids,
				inclSubordinate : $('#inclSubOrdinates').is(":checked"),
				profilePids : "",
				accountPids : $("#dbAccount").val(),
			},
					success : function(itemWiseSales) {
						$('#tBodyItemWiseSale').html("");

						if (itemWiseSales.length == 0) {
							if (value == "no") {
								$('#tBodyItemWiseSale')
										.html(
												"<tr><td colspan='8' align='center'>No data available</td></tr>");
							} else {
								$('#tBodyItemWiseSale')
										.html(
												"<tr><td colspan='7' align='center'>No data available</td></tr>");
							}

							return;
						} else {
							var total = 0;
							if (value == "no") {
								$
										.each(
												itemWiseSales,
												function(key, itemWiseSale) {
													console
															.log(itemWiseSale.productGroup);
													console
															.log(itemWiseSale.territory);
													rows += "<tr><td>"
															+ convertDateTimeFromServer(itemWiseSale.createdDate)
															+ "</td><td>"
															+ itemWiseSale.oderID
															+ "</td><td>"
															+ itemWiseSale.employeeName
															+ "</td><td>"
															+ itemWiseSale.accountName
															+ "</td><td>"
															+ itemWiseSale.supplierAccountName
															+ "</td><td>"
															+ itemWiseSale.customerLocation
															+ "</td><td>"
															+ (itemWiseSale.sourceStockLocationName == null ? ""
																	: itemWiseSale.sourceStockLocationName)
															+ "</td><td>"
															+ (itemWiseSale.destinationStockLocationName == null ? ""
																	: itemWiseSale.destinationStockLocationName)
															+ "</td><td>"
															+ itemWiseSale.productCategory
															+ "</td><td>"
															+ itemWiseSale.productName
															+ "</td><td>"
															+ itemWiseSale.productGroup
															+ "</td><td>"
															+ itemWiseSale.territory
															+ "</td><td>"
															+ itemWiseSale.quantity
															+ "</td><td>"
															+ itemWiseSale.productUnitQty
															+ "</td><td>"
															+ itemWiseSale.volume
															+ "</td><td>"
															+ itemWiseSale.sellingRate
															+ "</td></tr>";
												});
							} else {
								$
										.each(
												itemWiseSales,
												function(key, itemWiseSale) {
													console.log("Entered here......")
													console
															.log(itemWiseSale.productGroup);
													console
															.log(itemWiseSale.territory);
													total += itemWiseSale.rowTotal;
													rows += "<tr><td>"
															+ convertDateTimeFromServer(itemWiseSale.createdDate)
															+ "</td><td>"
															+ itemWiseSale.oderID
															+ "</td><td>"
															+ itemWiseSale.employeeName
															+ "</td><td>"
															+ itemWiseSale.accountName
															+ "</td><td>"
															+ itemWiseSale.supplierAccountName
															+ "</td><td>"
															+ itemWiseSale.customerLocation
															+ "</td><td>"
															+ itemWiseSale.productCategory
															+ "</td><td>"
															+ itemWiseSale.productName
															+ "</td><td>"
															+ itemWiseSale.productGroup
															+ "</td><td>"
															+ itemWiseSale.territory
														
															+ "</td><td>"
															+ itemWiseSale.quantity
															+ "</td><td>"
															+ itemWiseSale.productUnitQty
															+ "</td><td>"
															+ itemWiseSale.volume
															+ "</td><td>"
															+ itemWiseSale.sellingRate
															+ "</td><td>"
															+ itemWiseSale.rowTotal
															+ "</td></tr>";
												});
							}
						}
						$('#tBodyItemWiseSale').html(rows);
						$('#lblTotal').text(total.toFixed(2));
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	ItemWiseSale.filterByCategoryAndGroup = function() {
		var value = "";
		if ($("#includeStockLocationDetails").is(':checked')) {
			value = $('#includeStockLocationDetails').val();
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if ($("#dbDocumentType").val() == null) {
			return;
		}
		var stockLocations = [];
		var categoryPids = [];
		var groupPids = [];
		var profilePids = [];
		$("#pCategory").find('input[type="checkbox"]:checked').each(function() {
			categoryPids.push($(this).val());
		});
		$("#pGroup").find('input[type="checkbox"]:checked').each(function() {
			groupPids.push($(this).val());
		});
		$("#stockLocation").find('input[type="checkbox"]:checked').each(
				function() {
					stockLocations.push($(this).val());
				});
		$("#pProfile").find('input[type="checkbox"]:checked').each(function() {
			profilePids.push($(this).val());
		});
		var rows = "";
		$('#tHeadItemWiseSale').html("");
		if (value == "no") {
			$('#tHeadItemWiseSale')
					.html(
							"<tr><th>Date</th><th>Employee</th><th>Source</th><th>Destination</th><th>Category</th><th>Item</th><th>Quantity</th><th>Amount</th></tr>");
			$('#tBodyItemWiseSale')
					.html(
							"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		} else {
			$('#tHeadItemWiseSale')
					.html(
							"<tr><th>Date</th><th>Employee</th><th>Category</th><th>Item</th><th>Quantity</th><th>Amount</th></tr>");
			$('#tBodyItemWiseSale')
					.html(
							"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		}
		$
				.ajax({
					url : itemWiseSaleContextPath + "/filter",
					type : 'GET',
					data : {
						sort : $('input[name=sorting]:checked').val(),
						order : $('input[name=order]:checked').val(),
						categoryPids : categoryPids.join(","),
						groupPids : groupPids.join(","),
						voucherType : $("#dbDocumentType").val(),
						documentPid : $("#dbDocument").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						stockLocations : stockLocations.join(","),
						profilePids : profilePids.join(","),
						employeePid : $('#dbEmployee').val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")

					},
					success : function(itemWiseSales) {
						$('#tBodyItemWiseSale').html("");
						console.log(itemWiseSales);
						if (itemWiseSales.length == 0) {
							if (value == "no") {
								$('#tBodyItemWiseSale')
										.html(
												"<tr><td colspan='8' align='center'>No data available</td></tr>");
							} else {
								$('#tBodyItemWiseSale')
										.html(
												"<tr><td colspan='6' align='center'>No data available</td></tr>");
							}
							return;
						} else {
							if (value == "no") {
								$
										.each(
												itemWiseSales,
												function(key, itemWiseSale) {
													console.log(itemWiseSale);
													rows += "<tr><td>"
															+ convertDateTimeFromServer(itemWiseSale.createdDate)
															+ "</td><td>"
															+ itemWiseSale.employeeName
															+ "</td><td>"
															+ (itemWiseSale.sourceStockLocationName == null ? ""
																	: itemWiseSale.sourceStockLocationName)
															+ "</td><td>"
															+ (itemWiseSale.destinationStockLocationName == null ? ""
																	: itemWiseSale.destinationStockLocationName)
															+ "</td><td>"
															+ itemWiseSale.productCategory
															+ "</td><td>"
															+ itemWiseSale.productName
															+ "</td><td>"
															+ itemWiseSale.quantity
															+ "</td><td>"
															+ itemWiseSale.sellingRate
															+ "</td></tr>";
												});
							} else {
								$
										.each(
												itemWiseSales,
												function(key, itemWiseSale) {
													console.log(itemWiseSale);
													rows += "<tr><td>"
															+ convertDateTimeFromServer(itemWiseSale.createdDate)
															+ "</td><td>"
															+ itemWiseSale.employeeName
															+ "</td><td>"
															+ itemWiseSale.productCategory
															+ "</td><td>"
															+ itemWiseSale.productName
															+ "</td><td>"
															+ itemWiseSale.quantity
															+ "</td><td>"
															+ itemWiseSale.sellingRate
															+ "</td></tr>";
												});
							}
						}
						$('#tBodyItemWiseSale').html(rows);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	ItemWiseSale.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
	}
	
	ItemWiseSale.showdeliverdateDatePicker = function() {
		$("#txtFromDeliveryDate").val("");
		$("#toFromDeliveryDate").val("");
		if ($('#dbdeliverdateDateSearch').val() != "CUSTOM") {
			$('#divdeliveryDatePickers').css('display', 'none');
		} else {
			$('#divdeliveryDatePickers').css('display', 'initial');
		}
	}

	function clearAndHideErrorBox() {
		$(".alert > p").html("");
		$('.alert').hide();
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
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