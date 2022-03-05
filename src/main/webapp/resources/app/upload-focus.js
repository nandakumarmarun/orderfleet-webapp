// Create a UploadOdoo object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.uploadFocus) {
	this.uploadFocus = {};
}

(function() {
	'use strict';

	var uploadFocusContextPath = location.protocol + '//' + location.host
			+ location.pathname;
			
	var ajaxes = [
		{name: 'product_profiles', path: '/uploadProductProfiles', alias: 'Product Profiles'},
		{name: 'account_profiles', path: '/uploadAccountProfiles', alias: 'Account Profiles'},
		{name: 'receivable_payable', path: '/uploadReceivablePayable',alias: 'receivable payable'}
		
	];
	
	function findUrl(name) {
		for (let i=0; i<ajaxes.length; i++) {
			if (ajaxes[i].name === name) {
				return ajaxes[i];
			}
		}
		return null;
	}

	$(document).ready(function() {
		$('#selectAll').on('click', function() {
			selectAllStocks(this);
		});
		
		$('#uploadAll').on('click', function() {
			uploadAll();
		});
		
	});
	
	$(".check-one").change(function(){
    	if ($('.check-one:checked').length == $('.check-one').length) {
    		$('#selectAll').prop('checked', true);
	    } else {
	    	$('#selectAll').prop('checked', false);
	    }
	});
	
	function selectAllStocks(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}
	
	
	
	
	var selectedMasters = [];
	function uploadAll() {
		selectedMasters = [];
		$.each($("input[name='uploadMasters']:checked"), function() {
			let v = findUrl($(this).val());
			if (v != null) {
				selectedMasters.push(v);
			}
		});
		
		if (selectedMasters.length == 0) {
			alert("Please select Masters to Upload");
			return;
		}
		
		doAjax(selectedMasters.reverse());
	}
	
	function doAjax(selectedMasters) {
		if (selectedMasters.length > 0) {
			var m = selectedMasters.pop()
			$(".error-msg").html("Uploading "+m.alias+"....");
	        $.ajax({
	            url      : uploadFocusContextPath + m.path,
	            method : 'GET',
	            success  : function (serverResponse) {
					$(".error-msg").html("Uploading "+m.alias+" success....");                
	                doAjax(selectedMasters);
	            },
	            error : function(xhr, error) {
					$(".error-msg").html(
							"Error uploading "+m.alias);
					selectedMasters = [];
				}
	        });
		} else {
			alert("Upload Complete")
			onSaveSuccess(selectedMasters);
		}
	}
//	function doAjax(selectedMasters) {
//		if (selectedMasters.length > 0) {
//			var m = selectedMasters.pop()
//			$(".error-msg").html("Uploading "+m.alias+"....");
//	        $.ajax({
//	            url      : uploadFocusContextPath + m.path,
//	            method : 'POST',
//	            success  : function (serverResponse) {
//					$(".error-msg").html("Uploading "+m.alias+" success....");                
//	                doAjax(selectedMasters);
//	            },
//	            error : function(xhr, error) {
//					$(".error-msg").html(
//							"Error uploading "+m.alias);
//					selectedMasters = [];
//				}
//	        });
//		} else {
//			alert("Upload Complete")
//			onSaveSuccess(selectedMasters);
//		}
//	}

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = uploadFocusContextPath;
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