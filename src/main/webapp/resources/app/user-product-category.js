// Create a UserProductCategory object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserProductCategory) {
	this.UserProductCategory = {};
}

(function() {
	'use strict';

	var userProductCategoryContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var userProductCategoryModel = {
		userPid : null
	};

	$(document).ready(
			function() {

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				$('#btnSaveProductCategory').on('click', function() {
					saveAssignedProductCategories();
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
		var table = $('#tblProductCategories');
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

	function loadUserProductCategory(userPid) {

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		$("#productCategoriesCheckboxes input:checkbox").attr('checked', false);
		userProductCategoryModel.userPid = userPid;
		// clear all check box
		$.ajax({
			url : userProductCategoryContextPath + "/" + userPid,
			type : "GET",
			success : function(productCategories) {
				if (productCategories) {
					$.each(productCategories, function(index, productCategory) {
						$(
								"#productCategoriesCheckboxes input:checkbox[value="
										+ productCategory.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedProductCategories() {
		$(".error-msg").html("");
		var selectedProductCategories = "";

		$.each($("input[name='productCategory']:checked"), function() {
			selectedProductCategories += $(this).val() + ",";
		});

		if (selectedProductCategories == "") {
			$(".error-msg").html("Please select ProductCategories");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : userProductCategoryContextPath + "/save",
			type : "POST",
			data : {
				userPid : userProductCategoryModel.userPid,
				assignedProductCategories : selectedProductCategories,
			},
			success : function(status) {
				$("#productCategoriesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userProductCategoryContextPath;
	}

	UserProductCategory.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserProductCategory(id);
				break;
			}
		}
		el.modal('show');
	}

	UserProductCategory.closeModalPopup = function(el) {
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