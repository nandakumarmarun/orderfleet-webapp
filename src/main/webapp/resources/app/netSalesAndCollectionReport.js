// Create a StockDetails object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.NetSalesAndCollectionReport) {
	this.NetSalesAndCollectionReport = {};
}

(function() {
	'use strict';

	var netSalesAndCollectionContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	// Specify the validation rules

	// Specify the validation error messages

	$(document).ready(function() {

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		NetSalesAndCollectionReport.filter();

		$('#btnApply').on('click', function() {
			NetSalesAndCollectionReport.filter();

		});

		$('#btnPrint').on('click', function() {

			NetSalesAndCollectionReport.printStock();

		});

	});

	NetSalesAndCollectionReport.filter = function() {

		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}

		let empPids = $('#dbEmployee').val();

		if ("all" == empPids) {
			empPids = $('#dbEmployee option').map(function() {
				return $(this).val();
			}).get().join(',');
		}

		$('#tBodyStockDetails').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : netSalesAndCollectionContextPath + "/filter",
			type : 'GET',
			data : {
				employeePids : empPids,
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val(),
			},
			success : function(data) {

				$("#lblNetSalesAmount").text(data.netSaleAmount.toFixed(2));
				$("#lblNetCollectionAmount").text(
						data.netCollectionAmount.toFixed(2));
				$("#lblNetCollectionAmountCash").text(
						data.netCollectionAmountCash.toFixed(2));
				$("#lblNetCollectionAmountCheque").text(
						data.netCollectionAmountCheque.toFixed(2));
				$("#lblNetCollectionAmountRtgs").text(
						data.netCollectionAmountRtgs.toFixed(2));

			}
		});
	}

	NetSalesAndCollectionReport.printStock = function() {

		var newWin = "";

		var divToPrint = document.getElementById("tableStock");
		newWin = window.open("");
		newWin.document.write(divToPrint.outerHTML);
		newWin.print();
		newWin.close();

		// var printContents = document.getElementById(tableStock);
		// w=window.open();
		// w.document.write(printContents);
		// w.print();
		// w.close();
		//	
	}

	NetSalesAndCollectionReport.showDatePicker = function() {
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