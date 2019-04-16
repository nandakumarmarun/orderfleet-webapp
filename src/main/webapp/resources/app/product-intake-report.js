// Create a ProductInTake object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ProductInTake) {
	this.ProductInTake = {};
}

(function() {
	'use strict';

	var productInTakeContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
		$('#btnDownload').on('click',function() {
			var tblAccountProfile = $("#tblProductInTakeRep tbody");
			if (tblAccountProfile.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblAccountProfile[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			downloadXls();
		});

	});

	function downloadXls() {
		// Avoid last column in each row
		var excelName = "productIntakeReport";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblProductInTakeRep'),excelName);
	}
	
	function searchTable(inputVal) {
		$('#tBodyProductInTake').find('tr').each(function(index, row) {
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

	ProductInTake.filter = function() {

		if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
			return;
		}
		$('#tHeadProductInTake').html("");
		$('#tBodyProductInTake').html(
				"<tr><td align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : productInTakeContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(productInTakes) {
						$('#tBodyProductInTake').html("");
						if (productInTakes == null) {
							$('#tBodyProductInTake')
									.html(
											"<tr><td align='center'>No data available</td></tr>");
							return;
						}
						var index = 0;
						$('#tHeadProductInTake').html(
								"<tr><th class='verticalTableHeader'>Account</th></tr>");
						$.each(productInTakes, function(acckey, accValue) {
							var tr = "<tr><td>" + acckey + "</td>"
							$.each(accValue, function(ecomKey, ecomValue) {
								// create header
								if (index == 0) {
									$('#tHeadProductInTake tr').append(
											"<th class='verticalTableHeader' style='color:black;'>"
													+ ecomKey + "</th>");
								}
								tr += "<td>" + ecomValue + "</td>";
							});
							tr += "</tr>";
							$('#tBodyProductInTake').append(tr);
							index += 1;
						});
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