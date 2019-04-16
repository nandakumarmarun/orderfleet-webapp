if (!this.ProductGroupSalesTargetGroup) {
	this.ProductGroupSalesTargetGroup = {};
}

(function() {
	'use strict';

	var productGroupSalesTargetGroupContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(
			function() {

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
				$('#myFormSubmit').on('click', function() {
					createUpdateProductGroupSalesTargetGroup();
				});
			});

	function createUpdateProductGroupSalesTargetGroup() {
		
		var productGroupPid = $('#field_productGroup').val();
		var selectedSalesGroups = "";

		$.each($("input[name='salesTargetGroup']:checked"), function() {
			selectedSalesGroups += $(this).val() + ",";
		});
		if (productGroupPid == "no") {
			alert("Please select Product Group");
			return;
		}
		if (selectedSalesGroups == "") {
			alert("Please select Sales Target Groups");
			return;
		}

		$.ajax({
			method : 'POST',
			url : productGroupSalesTargetGroupContextPath
					+ "/saveProGroSalesTrgGro",
			data : {
				productGroupPid : productGroupPid,
				selectedSalesGroups : selectedSalesGroups,
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error:function(xhr,error){
				onError(xhr, error);
			}

		});
	}
	ProductGroupSalesTargetGroup.load = function() {
		$("#tbodyProductGroupSalesTargetGroup").html("");
		var id = $('#productGroup').val();
		$
				.ajax({
					url : productGroupSalesTargetGroupContextPath + "/" + id,
					method : 'GET',
					success : function(data) {

						
						var productGroupSalesTargetGroupData = "";
						if (data.length == 0) {
							$('#tbodyProductGroupSalesTargetGroup')
									.html(
											"<tr><td colspan='2' align='center'>No data available</td></tr>");
							return;
						} else if (data.length == 1
								&& data[0].productGroupName == null) {
							$('#tbodyProductGroupSalesTargetGroup')
									.html(
											"<tr><td colspan='2' align='center'>No data available</td></tr>");
							return;
						}
						$.each(data, function(index, value) {
							var salesTargetGroups = "";
							$.each(value.salesTargetGroups, function(index,
									salesTargetGroup) {
								salesTargetGroups += "<div>"
										+ salesTargetGroup.name + "</div>";
							});
							productGroupSalesTargetGroupData += "<tr><td>"
									+ value.productGroupName + "</td><td>"
									+ salesTargetGroups + "</td></tr>";
						});

						$("#tbodyProductGroupSalesTargetGroup").append(
								productGroupSalesTargetGroupData);
					},
					error:function(xhr,error){
						onError(xhr, error);
					}
				});
	}

	ProductGroupSalesTargetGroup.showModalPopup = function(el) {
		el.modal('show');
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = productGroupSalesTargetGroupContextPath;
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