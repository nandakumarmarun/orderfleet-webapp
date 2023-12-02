if (!this.ItemSummaryEmployeeWise) {
	this.ItemSummaryEmployeeWise = {};
}

(function() {
	'use strict';

	var ContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		var employeePid = getParameterByName('user-key-pid');
		getEmployees(employeePid);

		$("#dbDocumentType").change(function() {
			loadAllDocumentByDocumentType();
		});

		$("#dbDateSearch").change(function() {
			ItemSummaryEmployeeWise.showDatePicker();
		});

		$('#downloadXls').on('click', function() {
			var tblItemWiseSummary = $("#tblItemWiseSummary tbody");
			if (tblItemWiseSummary.children().length == 0) {
				alert("no values available");
				return;
			}
			downloadXls();
		});

	});

	ItemSummaryEmployeeWise.changeToSort = function() {
		$("#sortClass").attr('class', 'active');
		$("#filterClass").attr('class', '');
		$("#filter").css("display", "none");
		$("#sort").css("display", "block");
	}

	ItemSummaryEmployeeWise.changeToFilter = function() {
		$("#filterClass").attr('class', 'active');
		$("#sortClass").attr('class', '');
		$("#sort").css("display", "none");
		$("#filter").css("display", "block");
	}
	//set the date selection to 3 months
		$("#txtToDate").datepicker({
								 dateFormat: 'mm-dd-yy',
                                            minDate: 0,
                                onSelect: function(selectedDate) {
                                var toDate = new Date(selectedDate);
                                var fromDate = new Date(toDate);
                                fromDate.setMonth(toDate.getMonth() - 3);
                                $("#txtFromDate").datepicker("option", "minDate", fromDate);
                                $("#txtFromDate").datepicker("option", "maxDate",toDate);

                                                                                        }
							});
							$("#txtFromDate").datepicker({
								dateFormat : "mm-dd-yy",
								onSelect: function(selectedDate) {

                                                var fromDate = new Date(selectedDate);
                                                console.log("fromDate :"+fromDate)
                                                var toDate = new Date(fromDate);
                                                toDate.setMonth(toDate.getMonth() + 3);
                                            $("#txtToDate").datepicker("option", "minDate", fromDate);
                                                $("#txtToDate").datepicker("option", "maxDate",toDate);

                                            }
							});
	function downloadXls() {
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblItemWiseSummary").clone();
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
			url : ContextPath + "/load-document",
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

	ItemSummaryEmployeeWise.filter = function() {
		var value = "";
		var empPids = $('#dbEmployee').val();
		var accountPids = $('#dbAccount').val();
		$('#lblTotal').text("0");
		if ($("#includeStockLocationDetails").is(':checked')) {
			value = $('#includeStockLocationDetails').val();
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}

		if ("no" == accountPids) {
			accountPids = $('#dbAccount option').map(function() {
				return $(this).val();
			}).get().join(',');
		}

		if ("no" == empPids || "Dashboard Employee" == empPids) {
			empPids = $('#dbEmployee option').map(function() {
				return $(this).val();
			}).get().join(',');
		}

		if ($("#dbDocumentType").val() == "no") {
			alert("Please Select Document Type")
			return;
		}
		var rows = "";
		$('#tHeadItemWiseSummary').html("");
		if (value == "no") {
			$('#tHeadItemWiseSummary')
					.html(
							"<tr><th>Item</th><th>Quantity</th><th>Source</th><th>Destination</th></tr>");
			$('#tBodyItemWiseSummary')
					.html(
							"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		} else {
			$('#tHeadItemWiseSummary').html(
					"<tr><th>Item</th><th>Quantity</th></tr>");
			$('#tBodyItemWiseSummary')
					.html(
							"<tr><td colspan='2' align='center'>Please wait...</td></tr>");
		}
		$
				.ajax({
					url : ContextPath + "/filter",
					type : 'GET',
					data : {
						sort : $('input[name=sorting]:checked').val(),
						order : $('input[name=order]:checked').val(),
						categoryPids : "",
						groupPids : "",
						voucherType : $("#dbDocumentType").val(),
						documentPid : $("#dbDocument").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDates : $("#txtFromDate").val(),
						toDates : $("#txtToDate").val(),
						stockLocations : "",
						profilePids : "",
						territoryPids : "",
						employeePid : empPids,
						statusSearch : $("#dbStatusSearch").val(),
						accountPids : $('#dbAccount').val()
					},
					success : function(itemWiseSummaries) {
						$('#tBodyItemWiseSummary').html("");

						if (itemWiseSummaries.length == 0) {
							if (value == "no") {
								$('#tBodyItemWiseSummary')
										.html(
												"<tr><td colspan='4' align='center'>No data available</td></tr>");
							} else {
								$('#tBodyItemWiseSummary')
										.html(
												"<tr><td colspan='2' align='center'>No data available</td></tr>");
							}

							return;
						} else {
							var total = 0;
							if (value == "no") {
								$
										.each(
												itemWiseSummaries,
												function(key, itemWiseSummary) {
													rows += "<tr><td>"
															+ itemWiseSummary.productName
															+ "</td><td>"
															+ itemWiseSummary.quantity
															+ "</td><td>"
															+ (itemWiseSummary.sourceStockLocationName == null ? ""
																	: itemWiseSummary.sourceStockLocationName)
															+ "</td><td>"
															+ (itemWiseSummary.destinationStockLocationName == null ? ""
																	: itemWiseSummary.destinationStockLocationName)
															+ "</td><tr>";
												});
							} else {
								$.each(itemWiseSummaries, function(key,
										itemWiseSummary) {
									rows += "<tr><td>"
											+ itemWiseSummary.productName
											+ "</td><td>"
											+ itemWiseSummary.quantity
											+ "</td></tr>";
								});
							}
						}
						$('#tBodyItemWiseSummary').html(rows);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	ItemSummaryEmployeeWise.filterByCategoryAndGroup = function() {
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
		var territoryPids = [];
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
		$("#pTerritory").find('input[type="checkbox"]:checked').each(
				function() {
					territoryPids.push($(this).val());
				});
		var rows = "";
		$('#tHeadItemWiseSummary').html("");
		if (value == "no") {
			$('#tHeadItemWiseSummary')
					.html(
							"<tr><th>Item</th><th>Quantity</th><th>Source</th><th>Destination</th></tr>");
			$('#tBodyItemWiseSummary')
					.html(
							"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		} else {
			$('#tHeadItemWiseSummary').html(
					"<tr><th>Item</th><th>Quantity</th></tr>");
			$('#tBodyItemWiseSummary')
					.html(
							"<tr><td colspan='2' align='center'>Please wait...</td></tr>");
		}
		$
				.ajax({
					url : ContextPath + "/filter",
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
						territoryPids : territoryPids.join(","),
						employeePid : $('#dbEmployee').val(),
						statusSearch : $("#dbStatusSearch").val()

					},
					success : function(itemWiseSummaries) {
						$('#tBodyItemWiseSummary').html("");
						console.log(itemWiseSummaries);
						if (itemWiseSummaries.length == 0) {
							if (value == "no") {
								$('#tBodyItemWiseSummary')
										.html(
												"<tr><td colspan='4' align='center'>No data available</td></tr>");
							} else {
								$('#tBodyItemWiseSummary')
										.html(
												"<tr><td colspan='2' align='center'>No data available</td></tr>");
							}
							return;
						} else {
							if (value == "no") {
								$
										.each(
												itemWiseSummaries,
												function(key, itemWiseSummary) {
													console
															.log(itemWiseSummary);
													rows += "<tr><td>"
															+ itemWiseSummary.productName
															+ "</td><td>"
															+ itemWiseSummary.quantity
															+ "</td><td>"
															+ (itemWiseSummary.sourceStockLocationName == null ? ""
																	: itemWiseSummary.sourceStockLocationName)
															+ "</td><td>"
															+ (itemWiseSummary.destinationStockLocationName == null ? ""
																	: itemWiseSummary.destinationStockLocationName)
															+ "</td></tr>";
												});
							} else {
								$.each(itemWiseSummaries, function(key,
										itemWiseSummary) {
									console.log(itemWiseSummary);
									rows += "<tr><td>"
											+ itemWiseSummary.productName
											+ "</td><td>"
											+ itemWiseSummary.quantity
											+ "</td></tr>";
								});
							}
						}
						$('#tBodyItemWiseSummary').html(rows);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	ItemSummaryEmployeeWise.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
        			$(".custom_date1").addClass('show');
        			$(".custom_date2").addClass('show');
        			$(".custom_date1").removeClass('hide');
        			$(".custom_date2").removeClass('hide');
        			$('#divDatePickers').css('display', 'initial');
        		}else {
        			$(".custom_date1").addClass('hide');
        			$(".custom_date1").removeClass('show');
        			$(".custom_date2").addClass('hide');
        			$(".custom_date2").removeClass('show');
        			$('#divDatePickers').css('display', 'none');
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