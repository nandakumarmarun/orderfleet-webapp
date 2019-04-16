// Create a UploadXls object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.salesValueReportXlsUploader) {
	this.salesValueReportXlsUploader = {};
}

(function() {
	'use strict';

	var uploadXlsContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		$('#uploadXLS').on('click', function() {
			saveSalesValueReportXls(this);
		});

		$('#uploadIntakeXLS').on('click', function() {
			saveIntakeReportXls(this);
		});

	});

	function saveSalesValueReportXls(obj) {
		if ($('#txtSalesUpload').val() == '') {
			alert("please select file");
			return false;
		}
		var validExts = new Array(".xlsx", ".xls");
		var fileExt = $('#txtSalesUpload').val();
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if (validExts.indexOf(fileExt) < 0) {
			alert("Invalid file selected, valid files are  "
					+ validExts.toString() + " types.");
			return false;
		}
		var $btn = $(obj);
		$btn.button('loading');

		var file = $('#txtSalesUpload').get(0).files[0];
		var formData = new FormData();
		formData.append('file', file)
		$.ajax({
			url : uploadXlsContextPath,
			type : 'POST',
			data : formData,
			processData : false,
			contentType : false,
			success : function(data) {
				$btn.button('reset');
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				$btn.button('reset');
				onError(xhr, error);
			}
		});
	}

	function saveIntakeReportXls(obj) {
		if ($('#txtIntakeUpload').val() == '') {
			alert("please select file");
			return false;
		}
		var validExts = new Array(".xlsx", ".xls");
		var fileExt = $('#txtIntakeUpload').val();
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if (validExts.indexOf(fileExt) < 0) {
			alert("Invalid file selected, valid files are  "
					+ validExts.toString() + " types.");
			return false;
		}
		var $btn = $(obj);
		$btn.button('loading');

		var file = $('#txtIntakeUpload').get(0).files[0];
		var formData = new FormData();
		formData.append('file', file)
		$.ajax({
			url : uploadXlsContextPath+"/intakeReport",
			type : 'POST',
			data : formData,
			processData : false,
			contentType : false,
			success : function(data) {
				$btn.button('reset');
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				$btn.button('reset');
				onError(xhr, error);
			}
		});
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