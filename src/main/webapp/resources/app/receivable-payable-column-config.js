// methods in a closure to avoid creating global variables.

if (!this.ReceivablePayableColumnConfig) {
	this.ReceivablePayableColumnConfig = {};
}

(function() {
	'use strict';

	var rpColumnConfigContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	$(document).ready(
			function() {
				$('#enableConfigColumn').on('click', function() {
					$("#tbodyRPColumn tr").each(function() {
						 var tdVal = $(this).find("td:first-child").html();
						 $("input:checkbox[value='"+ tdVal +"']").prop('checked', true);
					});
					$('#rpColumnsModal').modal('show');
				});

				$('#btnSaveColumnConfigs').on('click', function() {
					saveAssignedColumns();
				});

				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				// select all checkbox in table tblProducts
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function searchTable(inputVal) {
		var table = $('#tbodyInventoryVourCol');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
					var regExp = new RegExp(inputVal, 'i');
					if (regExp.test($(td).text())) {
						found = true;
						return false;
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function saveAssignedColumns() {
		$(".error-msg").html("");
		var rpColumns = $("input:checkbox[name='enabled']:checked").map(function() {return this.value;}).get().join(',');
		if (rpColumns.length == "") {
			$(".error-msg").html("Please select Receivable Payable Columns");
			return;
		}
		$.ajax({
			url : rpColumnConfigContextPath + "/save?configColumns="+rpColumns,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			success : function(status) {
				$("#rpColumnsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		window.location = rpColumnConfigContextPath;
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