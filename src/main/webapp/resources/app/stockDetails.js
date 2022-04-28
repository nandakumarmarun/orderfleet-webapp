// Create a StockDetails object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.StockDetails) {
	this.StockDetails = {};
}

(function() {
	'use strict';

	var stockDetailsContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	// Specify the validation rules
	
	// Specify the validation error messages
	

	$(document).ready(
			function() {

				$('#btnApply').on('click', function() {
					if ($("#dbEmployee").val() == "no") {
						alert("Please select Employee.");
						return;
					}
					StockDetails.filter();
					
				});
				
				$('#btnPrint').on('click', function() {
	
					StockDetails.printStock();
					
				});
				
				
			});
	
	StockDetails.filter = function() {
		
		
		$('#tBodyStockDetails').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : stockDetailsContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
					},
					success : function(stockDetails) {
						$('#tBodyStockDetails').html("");
						if (stockDetails.length == 0) {
							$('#tBodyStockDetails')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$.each(stockDetails,
										function(index, stockDetail) {

											
													var content =	"<tr><td>"
																	+ stockDetail.productName
																	+ "</td><td>"
																	+ stockDetail.openingStock
																	+ "</td><td>"
																	+ stockDetail.saleStock
																	+ "</td><td>"
																	+ stockDetail.freeQnty
																	+ "</td><td>"
																	+ stockDetail.saledQuantity
																	+ "</td><td>"
																	+ stockDetail.closingStock
																	+ "</td></tr>";
						$('#tBodyStockDetails').append(content);	
										});
					}
				});
	}
	
	StockDetails.printStock = function() {
		
		
		var newWin="";
		
		var divToPrint=document.getElementById("tableStock");
		   newWin= window.open("");
		   newWin.document.write(divToPrint.outerHTML);
		   newWin.print();
		   newWin.close();
	
		
//		var printContents = document.getElementById(tableStock);
//    w=window.open();
//    w.document.write(printContents);
//    w.print();
//    w.close();
//	
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