// Create a UploadXls object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.xlsFileUploader) {
	this.xlsFileUploader = {};
}

(function() {
	'use strict';

	var uploadXlsContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	
	
	$(document).ready(function() {

		$('.selectpicker').selectpicker();

		$('#assignAccountColumnNumbers').on('click', function() {
			assignAccountColumnNumbers();
		});
		$('#uploadAccount').on('click', function() {
			saveAccountXls();
		});
		
		$('#assignProductColumnNumbers').on('click', function() {
			assignProductColumnNumbers()
		});
		$('#uploadProduct').on('click', function() {
			saveProductXls();
		});
		$('#assignInvoiceColumnNumbers').on('click', function() {
			assignInvoiceColumnNumbers();
		});
		$('#uploadInvoice').on('click', function() {
			saveInvoiceXls();
		});
	});

	function saveAccountXls() {

		$(".error-msg").html("Saving....");

		var accountNumbers = "";
		var nameNumber = $('#nameColumnNumber').val();
		var addressNumber = $('#addressColumnNumber').val();
		var cityNumber = $('#cityColumnNumber').val();
		var locationNumber = $('#locationColumnNumber').val();
		var pinNumber = $('#pinColumnNumber').val();
		var phone1Number = $('#phoneColumnNumber').val();

		var eMail1Number = $('#eMailColumnNumber').val();

		var descriptionNumber = $('#descriptionColumnNumber').val();
		var contactPersonNumber = $('#contactPersonColumnNumber').val();
		var accountTypeNumber = $('#accountTypeColumnNumber').val();
		var territoryNumber = $('#territoryColumnNumber').val();
		var aliasNumber = $("#aliasColumnNumber").val();
		var closingBalanceNumber = $("#closingBalanceColumnNumber").val();
		var creditDaysNumber = $("#creditDaysColumnNumber").val();
		var creditLimitNumber = $("#creditLimitColumnNumber").val();
		var priceLevelNumber = $("#priceLevelColumnNumber").val();
		var tinNoNumber = $("#tinNoColumnNumber").val();

		var customerIdNumber = $("#customerIdColumnNumber").val();
		var customerCodeNumber = $("#customerCodeColumnNumber").val();
		var countryNumber = $("#countryColumnNumber").val();
		var stateNumber = $("#stateColumnNumber").val();
		var districtNumber = $("#districtColumnNumber").val();
		var latitudeNumber = $("#latitudeColumnNumber").val();
		var longitudeNumber = $("#longitudeColumnNumber").val();
console.log("number:"+longitudeNumber)
		if (nameNumber == '') {
			$('#alertMessage').html("please assign name column number");
			$('#alertBox').modal("show");
			return false;
		}
		if (addressNumber == '') {
			addressNumber = -1;
		}
		if (cityNumber == '') {
			cityNumber = -1;
		}
		if (locationNumber == '') {
			locationNumber = -1;
		}
		if (pinNumber == '') {
			pinNumber = -1;
		}
		if (phone1Number == '') {
			phone1Number = -1;
		}

		if (eMail1Number == '') {
			eMail1Number = -1;
		}

		if (descriptionNumber == '') {
			descriptionNumber = -1;
		}
		if (contactPersonNumber == '') {
			contactPersonNumber = -1;
		}
		if (accountTypeNumber == '') {
			accountTypeNumber = -1;
		}
		if (territoryNumber == '') {
			territoryNumber = -1;
		}
		if (aliasNumber == '') {
			aliasNumber = -1;
		}
		if (closingBalanceNumber == '') {
			closingBalanceNumber = -1;
		}
		if (creditDaysNumber == '') {
			creditDaysNumber = -1;
		}
		if (creditLimitNumber == '') {
			creditLimitNumber = -1;
		}
		if (priceLevelNumber == '') {
			priceLevelNumber = -1;
		}
		if (closingBalanceNumber == '') {
			closingBalanceNumber = -1;
		}
		if (tinNoNumber == '') {
			tinNoNumber = -1;
		}
		if (customerIdNumber == '') {
			customerIdNumber = -1;
		}
		if (customerCodeNumber == '') {
			customerCodeNumber = -1;
		}
		if (countryNumber == '') {
			countryNumber = -1;
		}
		if (stateNumber == '') {
			stateNumber = -1;
		}
		if (districtNumber == '') {
			districtNumber = -1;
		}
		if(latitudeNumber == '')
		{
		latitudeNumber = -1;
		}
		if(longitudeNumber == '')
		{
		longitudeNumber = -1;
		}
		accountNumbers = nameNumber + "," + addressNumber + "," + cityNumber
				+ "," + locationNumber + "," + pinNumber + "," + phone1Number
				+ "," + eMail1Number + "," + descriptionNumber + ","
				+ contactPersonNumber + "," + accountTypeNumber + ","
				+ territoryNumber + "," + aliasNumber + ","
				+ closingBalanceNumber + "," + creditDaysNumber + ","
				+ creditLimitNumber + "," + priceLevelNumber + ","
				+ tinNoNumber + "," + customerIdNumber + ","
				+ customerCodeNumber + "," + countryNumber + "," + stateNumber
				+ "," + districtNumber + "," + latitudeNumber + "," + longitudeNumber;

		console.log("Column numbers " + accountNumbers);
		if (accountNumbers == '') {
			$('#alertMessage').html("please assign column numbers");
			$('#alertBox').modal("show");
			return false;
		}

		var accountXls = new FormData();

		accountXls.append("file", $('#txtAccountFile')[0].files[0]);
		accountXls.append('companyId', $('#field_company').val());
		accountXls.append('accountNumbers', accountNumbers);
		$
				.ajax({
					type : 'POST',
					url : uploadXlsContextPath + "/saveAccountXls",
					data : accountXls,
					cache : false,
					contentType : false,
					processData : false,

					success : function(data) {
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading excel .................");
						$(".error-msg").html(
								"Error uploading excel .................");
					}
				});

	}

	function saveProductXls() {

		$(".error-msg").html("Saving....");

		var productNumbers = "";
		var nameNumber = $('#productNameColumnNumber').val();
		var alias = $('#productAliasColumnNumber').val();
		var descriptionNumber = $('#productDescriptionColumnNumber').val();
		var priceNumber = $('#productPriceColumnNumber').val();
		var skuNumber = $('#productSkuColumnNumber').val();
		var unitQuantityNumber = $('#productUnitQuantityColumnNumber').val();
		var TaxRateNumber = $('#productTaxRateColumnNumber').val();
		var sizeNumber = $('#productSizeColumnNumber').val();
	    var openingStockNumber = $('#openingStockColumnNumber').val();
		var productGroupNumber = $('#productGroupColumnNumber').val();
		var mrpNumber = $('#mrpColumnNumber').val();
		var productCategoryNumber = $('#productCategoryColumnNumber').val();
		var hsnCodeNumber = $('#hsnColumnNumber').val();
		var productIdNumber = $('#productIdColumnNumber').val();
		var compoundUnitNumber = $('#compoundUnitColumnNumber').val();
		var productCodeNumber = $('#productCodeColumnNumber').val();
		var unitsNumber =$('#unitsColumnNumber').val();

		if (nameNumber == '') {
			$('#alertMessage').html("please assign product name column number");
			$('#alertBox').modal("show");
			return false;
		}
		if (alias == '') {
			alias = -1;
		}
		if (descriptionNumber == '') {
			descriptionNumber = -1;
		}
		if (priceNumber == '') {
			priceNumber = -1;
		}
		if (skuNumber == '') {
			skuNumber = -1;
		}
		if (unitQuantityNumber == '') {
			unitQuantityNumber = -1;
		}
		if (TaxRateNumber == '') {
			TaxRateNumber = -1;
		}
		if (sizeNumber == '') {
			sizeNumber = -1;
		}
		if (openingStockNumber == '') {
			openingStockNumber = -1;
		}
		if (productGroupNumber == '') {
			productGroupNumber = -1;
		}
		if (mrpNumber == '') {
			mrpNumber = -1;
		}
		if (productCategoryNumber == '') {
			productCategoryNumber = -1;
		}
		if (hsnCodeNumber == '') {
			hsnCodeNumber = -1;
		}
		if (productIdNumber == '') {
			productIdNumber = -1;
		}
		if (compoundUnitNumber == '') {
			compoundUnitNumber = -1;
		}
		if (productCodeNumber == '') {
			productCodeNumber = -1;
		}
		if (unitsNumber == '') {
			unitsNumber = -1;
		}

		productNumbers = nameNumber + "," + alias + "," + descriptionNumber
				+ "," + priceNumber + "," + skuNumber + ","
				+ unitQuantityNumber + "," + TaxRateNumber + "," + sizeNumber+ "," + openingStockNumber
				 + "," + productGroupNumber + ","
				+ mrpNumber + "," + productCategoryNumber + "," + hsnCodeNumber
				+ "," + productIdNumber + "," + compoundUnitNumber + ","
				+ productCodeNumber+","+unitsNumber;
		if (productNumbers == '') {
			$('#alertMessage').html("please assign column numbers");
			$('#alertBox').modal("show");
			return false;
		}

		var productXls = new FormData();
		productXls.append("file", $('#txtProductFile')[0].files[0]);
		productXls.append('companyId', $('#field_company').val());
		productXls.append('productNumbers', productNumbers);
		$
				.ajax({
					type : 'POST',
					url : uploadXlsContextPath + "/saveProductXls",
					data : productXls,
					cache : false,
					contentType : false,
					processData : false,

					success : function(data) {
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading excel .................");
						$(".error-msg").html(
								"Error uploading excel .................");
					}
				});

	}


	
	function saveInvoiceXls() {

		$(".error-msg").html("Saving....");

		var invoiceNumbers = "";
		var nameNumber = $('#customerNameColumnNumber').val();
		var customerIdNumber = $('#documentNoColumnNumber').val();
		var docNoNumber = $('#documentNumberColumnNumber').val();
		var docDateNumber = $('#documentDateColumnNumber').val();
		var docAmountNumber = $('#documentAmountColumnNumber').val();
		var balanceAmountNumber = $('#balanceAmountColumnNumber').val();

		
		if (nameNumber == '') {
			$('#alertMessage').html("please assign name column number");
			$('#alertBox').modal("show");
			return false;
		}
//		if (customerIdNumber == '') {
//			customerIdNumber = -1;
//		}
		if (docNoNumber == '') {
			docNoNumber = -1;
		}
		if (docDateNumber == '') {
			docDateNumber = -1;
		}
		if (docAmountNumber == '') {
			docAmountNumber = -1;
		}
		if (balanceAmountNumber == '') {
			balanceAmountNumber = -1;
		}

		invoiceNumbers = nameNumber + "," + customerIdNumber  +","+ docNoNumber
				+ "," + docDateNumber + "," + docAmountNumber + "," + balanceAmountNumber;
				

		console.log("Column numbers " + invoiceNumbers);
		if (invoiceNumbers == '') {
			$('#alertMessage').html("please assign column numbers");
			$('#alertBox').modal("show");
			return false;
		}

		var invoiceXls = new FormData();

		invoiceXls.append("file", $('#txtInvoiceDetails')[0].files[0]);
		invoiceXls.append('companyId', $('#field_company').val());
		invoiceXls.append('invoiceNumbers', invoiceNumbers);
		$
				.ajax({
					type : 'POST',
					url : uploadXlsContextPath + "/saveInvoiceXls",
					data : invoiceXls,
					cache : false,
					contentType : false,
					processData : false,

					success : function(data) {
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading excel .................");
						$(".error-msg").html(
								"Error uploading excel .................");
					}
				});

	}
	function assignAccountColumnNumbers() {
		$(".error-msg").html("");
		if ($('#field_company').val() == -1) {
			$('#alertMessage').html("please select company");
			$('#alertBox').modal("show");
			return false;
		}
		if ($('#txtAccountFile').val() == '') {
			$('#alertMessage').html("please select file");
			$('#alertBox').modal("show");
			return false;
		}

		var validExts = new Array(".xlsx", ".xls");
		var fileExt = $('#txtAccountFile').val();
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if (validExts.indexOf(fileExt) < 0) {
			$('#alertMessage').html(
					"Invalid file selected, valid files are  "
							+ validExts.toString() + " types.");
			$('#alertBox').modal("show");
			return false;
		}

		$('#accountColumnNumbers').modal('show');
	}

	function assignProductColumnNumbers() {
		$(".error-msg").html("");
		if ($('#field_company').val() == -1) {
			$('#alertMessage').html("please select company");
			$('#alertBox').modal("show");
			return false;
		}
		if ($('#txtProductFile').val() == '') {
			$('#alertMessage').html("please select file");
			$('#alertBox').modal("show");
			return false;
		}

		var validExts = new Array(".xlsx", ".xls");
		var fileExt = $('#txtProductFile').val();
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if (validExts.indexOf(fileExt) < 0) {
			$('#alertMessage').html(
					"Invalid file selected, valid files are  "
							+ validExts.toString() + " types.");
			$('#alertBox').modal("show");
			return false;
		}

		$('#productColumnNumbers').modal('show');
	}
	function assignInvoiceColumnNumbers() {
		$(".error-msg").html("");
		if ($('#field_company').val() == -1) {
			$('#alertMessage').html("please select company");
			$('#alertBox').modal("show");
			return false;
		}
		if ($('#txtInvoiceDetails').val() == '') {
			$('#alertMessage').html("please select file");
			$('#alertBox').modal("show");
			return false;
		}

		var validExts = new Array(".xlsx", ".xls");
		var fileExt = $('#txtInvoiceDetails').val();
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if (validExts.indexOf(fileExt) < 0) {
			$('#alertMessage').html(
					"Invalid file selected, valid files are  "
							+ validExts.toString() + " types.");
			$('#alertBox').modal("show");
			return false;
		}

		$('#receivablePayableColumnNumbers').modal('show');
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = uploadXlsContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = uploadXlsContextPath;
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		uploadXlsModel.pid = null; // reset uploadXls model;
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
