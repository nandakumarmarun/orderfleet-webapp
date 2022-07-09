// Create a SalesTargetAchievedReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.CustomerWiseItemSale) {
	this.CustomerWiseItemSale = {};
}

(function() {
	'use strict';

	var customerWiseItemSaleContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var dashboardEmployees = [];
	var otherEmployees = [];
	var allEmployeesMap = new Object();

	$(document).ready(function() {

		getEmployees();

		CustomerWiseItemSale.filter();

		$('#applyBtn').click(function() {
			CustomerWiseItemSale.filter();
		});
		$('#btnDownload').click(function()
		{
			CustomerWiseItemSale.downloadXls();
		});

	});

	
CustomerWiseItemSale.downloadXls = function() {
		// Avoid last column in each row
		// $("#tblInvoiceWiseReport th:last-child, #tblAccountProfile
		// td:last-child").hide();
		
		var excelName = "sales&Damage"+ new Date().toISOString().replace(/[\-\:\.]/g, "");
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblInvoiceWiseReport'),excelName);
		 // $("#tblAccountProfile th:last-child, #tblAccountProfile
			// td:last-child").show();
	}
	
	
	CustomerWiseItemSale.filter = function() {
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}

		$('#tblSalesTargetAchievedReport').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : customerWiseItemSaleContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),

						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtFromDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")
					},
					success : function(invoiceWiseReports) {

						$('#tBodyInvoiceWiseReport').html("");

						if (invoiceWiseReports.length == 0) {
							$('#tBodyInvoiceWiseReport')
									.html(
											"<tr><td colspan='11' align='center'>No data available</td></tr>");
							return;
						} else {
							fillTargetData(invoiceWiseReports)
						}

					}
				});
	}

	function fillTargetData(invoiceWiseReports) {

		$("#tBodyInvoiceWiseReport tbody").html("");
		var row = "";

		$.each(invoiceWiseReports, function(key, value) {
			row = "<tr><td>" + key + "</td>"
         var saledquantity=0;
			var damagequantity=0;
			var list;
			$.each(value, function(index, stockDetails) {
				list = stockDetails.productList
			});

			$.each(list, function(index, productName) {

				var flag = 0;
				$.each(value, function(index, stockDetails) {

					if (productName == stockDetails.productName) {

						flag = 1;
						saledquantity=stockDetails.saledQuantity;
						damagequantity =stockDetails.damageQty;
					}

					
				});
				console.log(flag);
				if (flag == 1) {
					row += "<td>" + saledquantity + "</td>"
					row += "<td>" + damagequantity + "</td>"
				} else {
					row += "<td>" + 0 + "</td>"
					row += "<td>" + 0 + "</td>"
				}
			});
			console.log(row);
			row += "<\tr>"
			$("#tBodyInvoiceWiseReport").append(row);
		});
	}

	CustomerWiseItemSale.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}

	}

	function convertLocalDateToServer(date) {
		if (date) {
			return moment(date).format('YYYY-MM-DD');
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