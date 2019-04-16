// Create a IntakeComparison object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.IntakeComparison) {
	this.IntakeComparison = {};
}

(function() {
	'use strict';

	var intakeComparisonContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

	});

	function searchTable(inputVal) {
		$('#tBodyIntakeComparison').find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
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

	IntakeComparison.filter = function() {

		if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
			return;
		}
		$('#tHeadIntakeComparison').html("");
		$('#tBodyIntakeComparison').html(
				"<tr><td align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : intakeComparisonContextPath + "/filter",
					type : 'GET',
					data : {
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(intakeComparisons) {
						$('#tBodyIntakeComparison').html("");
						if (intakeComparisons.length == 0) {
							$('#tBodyIntakeComparison')
									.html(
											"<tr><td align='center'>No data available</td></tr>");
							return;
						}
						$('#tHeadIntakeComparison').html("<tr><th>Product</th></tr>");
						
						$.each(intakeComparisons,  function(index, intakeComparison) {
											var tr = "<tr><td>" + intakeComparison.productName + "</td>"
											var highSale = 0;
											var highMonth = "";
											
											var lowSale = 0;
											var lowMonth = "";
											
											$.each(intakeComparison.months, function(key, month) {
												// create header
												if (index == 0) {
													$('#tHeadIntakeComparison tr').append("<th>"+ month.month + "</th>");
												}
												tr += "<td>"+ month.quantity + "</td>";
												
												if (key == 0) {
													highSale = month.quantity;
													highMonth = month.month;
													
													lowSale = month.quantity;
													lowMonth = month.month;
												} else {
													if(month.quantity > highSale ){
														highSale = month.quantity;
														highMonth = month.month;
													}
													if(month.quantity < lowSale ){
														lowSale = month.quantity;
														lowMonth = month.month;
													}
												}
											});
											if(highSale == 0 && lowSale == 0){
												highMonth = "";
												lowMonth = "";
											}
											tr += "<td>"+ highSale + "</td>";
											tr += "<td>"+ highMonth + "</td>";
											tr += "<td>"+ lowMonth + "</td>";
											tr += "</tr>";
											$('#tBodyIntakeComparison').append(tr);
										});
						$('#tHeadIntakeComparison tr').append("<th>High Sale Qty</th><th>High Sale Month</th><th>Low Sale Month</th>");
					}
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