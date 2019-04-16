// Create a DocumentInventoryVoucherColumn object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DocumentInventoryVoucherColumn) {
	this.DocumentInventoryVoucherColumn = {};
}

(function() {
	'use strict';

	var documentInventoryVoucherColumnContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var documentInventoryVoucherColumnModel = {
		documentPid : null
	};

	$(document).ready(
			function() {

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				$('#btnSaveInventoryVoucherColumn').on('click', function() {
					saveAssignedInventoryVoucherColumns();
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

	function loadDocumentInventoryVoucherColumn(documentPid) {

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		$(
				"#inventoryVoucherColumnsCheckboxes input:checkbox[name='inventoryVoucherColumn']")
				.attr('checked', false);
		$("#inventoryVoucherColumnsCheckboxes input:checkbox[name='enabled']")
				.attr('checked', true);
		documentInventoryVoucherColumnModel.documentPid = documentPid;
		// clear all check box
		$
				.ajax({
					url : documentInventoryVoucherColumnContextPath + "/"
							+ documentPid,
					type : "GET",
					success : function(inventoryVoucherColumns) {
						if (inventoryVoucherColumns) {
							$.each(inventoryVoucherColumns, function(index,
									inventoryVoucherColumn) {
								$(
										"#inventoryVoucherColumnsCheckboxes input:checkbox[value="
												+ inventoryVoucherColumn.name
												+ "]").prop("checked", true);
								$("#" + inventoryVoucherColumn.name + "").prop(
										"checked",
										inventoryVoucherColumn.enabled);
								$("#lbl" + inventoryVoucherColumn.name + "")
										.val(inventoryVoucherColumn.label);
							});
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function saveAssignedInventoryVoucherColumns() {
		$(".error-msg").html("");
		var inventoryVoucherColumns = [];

		$.each($("input[name='inventoryVoucherColumn']:checked"), function() {
			var name = $(this).val();
			var enabled = $("#" + name + "").prop("checked");
			var label = $("#lbl" + name + "").val();

			inventoryVoucherColumns.push({
				name : name,
				enabled : enabled,
				label : label
			});
		});
		if (inventoryVoucherColumns.length == "") {
			$(".error-msg").html("Please select Inventory Voucher Columns");
			return;
		}
		var documentInventoryVoucherColumn = {
			documentPid : documentInventoryVoucherColumnModel.documentPid,
			inventoryVoucherColumns : inventoryVoucherColumns
		}
		console.log(documentInventoryVoucherColumn);
		$.ajax({
			url : documentInventoryVoucherColumnContextPath + "/save",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(documentInventoryVoucherColumn),
			success : function(status) {
				$("#inventoryVoucherColumnsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentInventoryVoucherColumnContextPath;
	}

	DocumentInventoryVoucherColumn.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadDocumentInventoryVoucherColumn(id);
				break;
			}
		}
		el.modal('show');
	}

	DocumentInventoryVoucherColumn.closeModalPopup = function(el) {
		el.modal('hide');
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