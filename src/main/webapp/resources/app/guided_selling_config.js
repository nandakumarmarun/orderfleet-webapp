// Create a guidedSellingConfig object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

(function() {
	'use strict';

	var guidedSellingConfigContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$('#btnSave').on('click', function() {
			saveGuidedSellingConfig();
		});

		getGuidedSellingConfig();
	});

	function getGuidedSellingConfig() {
		$
				.ajax({
					url : guidedSellingConfigContextPath + "/load",
					type : "GET",
					success : function(guidedSellingConfig) {
						if (guidedSellingConfig != null) {
							$("#txtGuidedSellingFilterItems")
									.prop(
											'checked',
											guidedSellingConfig.guidedSellingFilterItems);
							if (guidedSellingConfig.favouriteProductGroupPid != null)
								$("#dbFavouriteProductGroupPid")
										.val(
												guidedSellingConfig.favouriteProductGroupPid);
							$("#txtFavouriteItemCompulsory")
									.prop(
											'checked',
											guidedSellingConfig.favouriteItemCompulsory);
							if (guidedSellingConfig.promotionProductGroupPid != null)
								$("#dbPromotionProductGroupPid")
										.val(
												guidedSellingConfig.promotionProductGroupPid);
							$("#txtPromotionItemCompulsory")
									.prop(
											'checked',
											guidedSellingConfig.promotionItemCompulsory);
							if (guidedSellingConfig.guidedSellingInfoDocumentPid != null)
								$("#dbGuidedSellingInfoDocumentPid")
										.val(
												guidedSellingConfig.guidedSellingInfoDocumentPid);
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function saveGuidedSellingConfig() {

		var guidedSellingConfig = {
			guidedSellingFilterItems : $("#txtGuidedSellingFilterItems").prop('checked'),
			favouriteProductGroupPid : $("#dbFavouriteProductGroupPid").val(),
			favouriteItemCompulsory : $("#txtFavouriteItemCompulsory").prop('checked'),
			promotionProductGroupPid : $("#dbPromotionProductGroupPid").val(),
			promotionItemCompulsory : $("#txtPromotionItemCompulsory").prop('checked'),
			guidedSellingInfoDocumentPid : $("#dbGuidedSellingInfoDocumentPid").val()
		};
		console.log(guidedSellingConfig);
		$.ajax({
			url : guidedSellingConfigContextPath,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(guidedSellingConfig),
			success : function(status) {
				alert("success");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = guidedSellingConfigContextPath;
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