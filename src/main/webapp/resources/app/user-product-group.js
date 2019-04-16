// Create a UserProductGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserProductGroup) {
	this.UserProductGroup = {};
}

(function() {
	'use strict';

	var userProductGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var userProductGroupModel = {
		userPid : null
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

	function loadUserProductGroup(userPid) {
		
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");
		
		$("#productGroupsCheckboxes input:checkbox").attr('checked', false);
		userProductGroupModel.userPid = userPid;
		// clear all check box
		$.ajax({
			url : userProductGroupContextPath + "/" + userPid,
			type : "GET",
			success : function(productGroups) {
				if (productGroups) {
					$.each(productGroups, function(index, productGroup) {
						$(
								"#productGroupsCheckboxes input:checkbox[value="
										+ productGroup.pid + "]").prop(
								"checked", true);
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
		var selectedProductGroups = "";

		$.each($("input[name='productGroup']:checked"), function() {
			selectedProductGroups += $(this).val() + ",";
		});

		if (selectedProductGroups == "") {
			$(".error-msg").html("Please select ProductGroups");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : userProductGroupContextPath + "/save",
			type : "POST",
			data : {
				userPid : userProductGroupModel.userPid,
				assignedProductGroups : selectedProductGroups,
			},
			success : function(status) {
				$("#productGroupsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userProductGroupContextPath;
	}

	UserProductGroup.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserProductGroup(id);
				break;
			}
		}
		el.modal('show');
	}

	UserProductGroup.closeModalPopup = function(el) {
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