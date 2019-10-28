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
	});

	function saveAccountXls() {
		var accountNumbers = "";
		var nameNumber = $('#nameColumnNumber').val();
		var addressNumber = $('#addressColumnNumber').val();
		var cityNumber = $('#cityColumnNumber').val();
		var locationNumber = $('#locationColumnNumber').val();
		var pinNumber = $('#pinColumnNumber').val();
		var phoneNumber = $('#phoneColumnNumber').val();
		var eMailNumber = $('#eMailColumnNumber').val();
		var descriptionNumber = $('#descriptionColumnNumber').val();
		var contactPersonNumber = $('#contactPersonColumnNumber').val();
		var accountTypeColumnNumber = $('#accountTypeColumnNumber').val();
		var territoryColumnNumber = $('#territoryColumnNumber').val();

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
		if (phoneNumber == '') {
			phoneNumber = -1;
		}
		if (eMailNumber == '') {
			eMailNumber = -1;
		}
		if (descriptionNumber == '') {
			descriptionNumber = -1;
		}
		if (contactPersonNumber == '') {
			contactPersonNumber = -1;
		}
		if (accountTypeColumnNumber == '') {
			accountTypeColumnNumber = -1;
		}
		if (territoryColumnNumber == '') {
			territoryColumnNumber = -1;
		}
		accountNumbers = nameNumber + "," + addressNumber + "," + cityNumber
				+ "," + locationNumber + "," + pinNumber + "," + phoneNumber
				+ "," + eMailNumber + "," + descriptionNumber + "," + contactPersonNumber + "," + accountTypeColumnNumber+ "," + territoryColumnNumber;
		if (accountNumbers == '') {
			$('#alertMessage').html("please assign column numbers");
			$('#alertBox').modal("show");
			return false;
		}

		var accountXls = new FormData();
		
		accountXls.append("file", $('#txtAccountFile')[0].files[0]);
		accountXls.append('companyId', $('#field_company').val());
		accountXls.append('accountNumbers', accountNumbers);
		$.ajax({
			type : 'POST',
			url : uploadXlsContextPath + "/saveAccountXls",
			data : accountXls,
			cache : false,
			contentType : false,
			processData : false,

			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				console.log("Error uploading excel .................");
			}
		});

	}

	function saveProductXls() {
		var productNumbers = "";
		var nameNumber = $('#productNameColumnNumber').val();
		var alias = $('#productAliasColumnNumber').val();
		var descriptionNumber = $('#productDescriptionColumnNumber').val();
		var priceNumber = $('#productPriceColumnNumber').val();
		var skuNumber = $('#productSkuColumnNumber').val();
		var unitQuantityNumber = $('#productUnitQuantityColumnNumber').val();
		var TaxRateNumber = $('#productTaxRateColumnNumber').val();
		var sizeNumber = $('#productSizeColumnNumber').val();

		if (nameNumber == '') {
			$('#alertMessage')
					.html("please assign product name column number");
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

		productNumbers = nameNumber + "," + alias + "," + descriptionNumber
				+ "," + priceNumber + "," + skuNumber + ","
				+ unitQuantityNumber + "," + TaxRateNumber + "," + sizeNumber;
		if (productNumbers == '') {
			$('#alertMessage').html("please assign column numbers");
			$('#alertBox').modal("show");
			return false;
		}

		var productXls = new FormData();
		productXls.append("file", $('#txtProductFile')[0].files[0]);
		productXls.append('companyId', $('#field_company').val());
		productXls.append('productNumbers', productNumbers);
		$.ajax({
			type : 'POST',
			url : uploadXlsContextPath + "/saveProductXls",
			data : productXls,
			cache : false,
			contentType : false,
			processData : false,

			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function assignAccountColumnNumbers() {
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