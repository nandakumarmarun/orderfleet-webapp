// Create a DocumentProductGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DocumentProductGroup) {
	this.DocumentProductGroup = {};
}

(function() {
	'use strict';

	var documentProductGroupContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var documentProductGroupModel = {
		documentPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveProductGroup').on('click', function() {
			saveAssignedProductGroups();
		});
		
		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		// select all checkbox in table tblProductGroups
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
		
	});
	
	function searchTable(inputVal) {
		var table = $('#tblProductGroups');
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

	function loadDocumentProductGroup(documentPid) {
		
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");
		
		console.log(documentPid);
		$("#productGroupsCheckboxes input:checkbox").attr('checked', false);
		documentProductGroupModel.documentPid = documentPid;
		// clear all check box
		$.ajax({
			url : documentProductGroupContextPath + "/" + documentPid,
			type : "GET",
			success : function(documentProductGroups) {
				if (documentProductGroups) {
					$.each(documentProductGroups, function(index, documentProductGroup) {
						$("#productGroupsCheckboxes input:checkbox[value="+ documentProductGroup.productGroupPid + "]").prop("checked", true);
						$("#txtOrder" + documentProductGroup.productGroupPid).val(documentProductGroup.sortOrder);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedProductGroups() {
		$(".error-msg").html("");
		var order = [];
		var documentProductGroups = [];
		var status = true;
		$.each($("input[name='productGroup']:checked"), function() {
			var productGroupPid = $(this).val();
			var sortOrder = $("#txtOrder" + productGroupPid).val();
			if (sortOrder == "" || sortOrder < 1) {
				$(".error-msg").html("Enter valid order");
				status = false;
				return;
			}
			order.push(sortOrder);
			var duplicate = hasDuplicates(order);
			if (duplicate) {
				$(".error-msg").html("Duplicates in order");
				status = false;
				return;
			}
			documentProductGroups.push({
				documentPid : documentProductGroupModel.documentPid,
				productGroupPid : productGroupPid,
				sortOrder : sortOrder
			})
		});
		if (!status) {
			return;
		}
		if (documentProductGroups.length == 0) {
			$(".error-msg").html("Please select Product Groups");
			return;
		}
		$.ajax({
			url : documentProductGroupContextPath + "/save",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(documentProductGroups),
			success : function(status) {
				$("#productGroupsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function hasDuplicates(array) {
		var valuesSoFar = Object.create(null);
		for (var i = 0; i < array.length; ++i) {
			var value = array[i];
			if (value in valuesSoFar) {
				return true;
			}
			valuesSoFar[value] = true;
		}
		return false;
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentProductGroupContextPath;
	}

	DocumentProductGroup.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadDocumentProductGroup(id);
				break;
			}
		}
		el.modal('show');
	}

	DocumentProductGroup.closeModalPopup = function(el) {
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