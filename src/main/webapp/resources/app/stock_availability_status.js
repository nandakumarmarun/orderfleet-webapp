// Create a StockAvailabilityStatus object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.StockAvailabilityStatus) {
	this.StockAvailabilityStatus = {};
}

(function() {
	'use strict';

	var stockAvailabilityStatusContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(
			function() {
				$('#btnMainSearch').on(
						'click',
						function() {
							searchTable($('#searchText').val(),
									$('#tBodyProductProfile'));
						});
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
							StockAvailabilityStatus.showMultipleDiv()
						});

				$('input[name="chk_productProfile"]').click(function() {
					StockAvailabilityStatus.showMultipleDiv();
				});

			});

	StockAvailabilityStatus.showMultipleDiv = function()  {
		var chkbox = $(this).is(':checked');
		var count = 0;
		$.each($("input[name='chk_productProfile']:checked"), function() {
			count = count + 1;
		});
		if (count > 0) {
			$('#multipleDiv').fadeIn();
		} else {
			$('#multipleDiv').fadeOut();
			$('.allcheckbox').prop('checked', false);
		}
	}

	StockAvailabilityStatus.filter = function() {

		var pGroupPid = $('#dbProductGroup').val();
		var filterValue = $('#dbFilter').val();

		$('#tBodyProductProfile').html(
				"<tr><td colspan='3' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : stockAvailabilityStatusContextPath + "/filterByGroup",
			method : 'GET',
			data : {
				groupPid : pGroupPid,
				filter : filterValue
			},
			success : function(productProfiles) {
				addTableBodyvalues(productProfiles);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function addTableBodyvalues(productProfiles) {
		$('#tBodyProductProfile').html("");
		// select box for set size
		if (productProfiles.length == 0) {
			$('#tBodyProductProfile')
					.html(
							"<tr><td colspan='3' align='center'>No data available</td></tr>");
			return;
		}

		$
				.each(
						productProfiles,
						function(index, productProfile) {

							$('#tBodyProductProfile')
									.append(
											'<tr><td><input name="chk_productProfile" onchange="StockAvailabilityStatus.showMultipleDiv()" type="checkbox"value="'
													+ productProfile.pid
													+ '"/></td><td>'
													+ productProfile.name
													+ '</td><td>'
													+ (productProfile.alias != null ? productProfile.alias
															: '') + '</td><td>'
													+ statusDiv(productProfile)
													+ '</td></tr>');

						});

	}

	function statusDiv(productProfile) {
		var status = productProfile.stockAvailabilityStatus;
		var pid = productProfile.pid;
		var classDiv = '';

		if (status == 'AVAILABLE') {
			classDiv = "btn dropdown-toggle btn-success entypo-check";
		} else {
			classDiv = "btn dropdown-toggle btn-danger entypo-cancel-circled";
		}

		var div = " <div class='btn-group'> <span data-toggle='dropdown' aria-haspopup='true' id='btn_"
				+ productProfile.pid
				+ "' aria-expanded='false' class='"
				+ classDiv
				+ "'  onclick='' style='cursor: pointer;'>"
				+ status
				+ "</span>  <div class='dropdown-menu dropdown-menu-right' style='background-color: #F0F0F0'> <div> <a class='btn btn-default dropdown-item entypo-check' style='width: 100%; text-align: left; color: green' onclick='StockAvailabilityStatus.changeStatus(\""
				+ pid
				+ "\",this);'>AVAILABLE</a> </div> <div> <a class='btn btn-default dropdown-item entypo-cancel-circled' style='width: 100%; text-align: left; color: red;' onclick='StockAvailabilityStatus.changeStatus(\""
				+ pid + "\",this);'>OUT_OFF_STOCK</a> </div> </div> </div> ";
		return div;
	}

	StockAvailabilityStatus.statusChange = function(obj) {
		var pids = "";

		$.each($("input[name='chk_productProfile']:checked"), function() {
			pids += $(this).val() + ",";

		});
		if (pids == "") {
			alert("select product");
			return;
		}

		if (confirm("are you confirm?")) {

			$.ajax({
				url : stockAvailabilityStatusContextPath
						+ "/changeMultipleStatus",
				method : 'POST',
				data : {
					productPids : pids,
					status : $(obj).text()
				},
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	StockAvailabilityStatus.changeStatus = function(pid, obj) {
		var name = $("#btn_" + pid).text();
		if (name == $(obj).text()) {
			console.log("same");
			return;
		}

		if (confirm("are you confirm?")) {

			$.ajax({
				url : stockAvailabilityStatusContextPath
						+ "/changeStockAvailabilityStatus",
				method : 'POST',
				data : {
					productPid : pid,
					status : $(obj).text()
				},
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}

	function searchTable(inputVal, table) {
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = stockAvailabilityStatusContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = stockAvailabilityStatusContextPath;
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