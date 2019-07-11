// Create a SalesTargetGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesTargetGroup) {
	this.SalesTargetGroup = {};
}

(function() {
	'use strict';

	var salesTargetGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#salesTargetGroupForm");
	var deleteForm = $("#deleteForm");
	var salesTargetGroupModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		targetUnit : null,
		targetSettingType : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		},
		targetUnit : {
			required : true,
			maxlength : 100
		},
		targetSettingType : {
			valueNotEquals : "-1"
		}

	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		},
		targetUnit : {
			maxlength : "This field cannot be longer than 55 characters."
		}
	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateSalesTargetGroup(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteSalesTargetGroup(e.currentTarget.action);
				});

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				$('#btnSaveDocument').on('click', function() {
					saveAssignedDocuments();
				});

				$('#btnSaveProducts').on('click', function() {
					saveAssignedProducts();
				});
				
				$('#btnSaveLocations').on('click', function() {
					saveAssignedLocations();
				});
				
				$('#btnSearchLocations').click(function() {
					searchTableLocations($("#searchLocations").val());
				});
				// table search
				$('#btnSearch').click(
						function() {
							if ($('#tblProducts').find('tr').length == 0) {
								$(".error-msg").html("");
								$("input[name='filter'][value='all']").prop(
										"checked", true);
								$('#search').val("");
								SalesTargetGroup.filterByCategoryAndGroup();
								searchTable($("#search").val());
							} else {
								$(".error-msg").html("");
								searchTable($("#search").val());
							}
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
		var table = $('#tblProducts');
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
	
	function searchTableLocations(inputVal) {
		var table = $('#tBodyLocation');
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
	
	SalesTargetGroup.assignLocations = function(el, pid, type) {
		salesTargetGroupModel.pid = pid;

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#searchLocations").val("");
		searchTableLocations("");

		// clear all check box
		$("#divLocations input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : salesTargetGroupContextPath + "/getLocations",
			type : "GET",
			data : {
				salesTargetGroupPid : salesTargetGroupModel.pid
			},
			success : function(assignedLocations) {
				if (assignedLocations) {
					$.each(assignedLocations, function(index,
							location) {
						$(
								"#divLocations input:checkbox[value="
										+ location.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		el.modal('show');

	}

	function loadUserDocument(salesTargetGroupPid, type) {
		$("#documentsCheckboxes input:checkbox").attr('checked', false);

		$('#tbl_documentChkBoxs').html(
				"<tr><td colspan='2' align='center'>Please wait...</td></tr>");

		salesTargetGroupModel.pid = salesTargetGroupPid;
		// clear all check box
		$
				.ajax({
					url : salesTargetGroupContextPath + "/getDocuments/"
							+ salesTargetGroupPid,
					type : "GET",
					data : {
						targetSettingType : type
					},
					success : function(documents) {

						if (!documents.length > 0) {
							$('#tbl_documentChkBoxs')
									.html(
											"<tr><td colspan='2' align='center'>No data available</td></tr>");
							return;
						}
						if (documents) {
							$('#tbl_documentChkBoxs').html("");
							$.each(documents, function(index, document) {
								var checked = "";
								if (document.alias == 'TRUE') {
									checked = "checked"
								}
								$('#tbl_documentChkBoxs').append(
										"<tr><td><input name='document' type='checkbox'value="
												+ document.pid + " " + checked
												+ "/></td><td>" + document.name
												+ "</td></tr>");
							});
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function saveAssignedDocuments() {
		$(".error-msg").html("");
		var selectedDocuments = "";

		$.each($("input[name='document']:checked"), function() {
			selectedDocuments += $(this).val() + ",";
		});

		if (selectedDocuments == "") {
			$(".error-msg").html("Please select Documents");
			return;
		}
		$.ajax({
			url : salesTargetGroupContextPath + "/saveDocuments",
			type : "POST",
			data : {
				salesTargetGroupPid : salesTargetGroupModel.pid,
				assignedDocuments : selectedDocuments,
			},
			success : function(status) {
				$("#documentsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	var saleTargetGroupPid = "";
	function loadproducts(pid) {
		saleTargetGroupPid = pid;
		$(".error-msg")
				.html(
						"No items found in the list with the given keyword, remove keywords and filters and try search to list more items.");
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");
		$('#tblProducts').html("");
		// clear all check box
		$("#productsCheckboxes input:checkbox").attr('checked', false);

	}
	

	SalesTargetGroup.filterByCategoryAndGroup = function() {
		$(".error-msg").html("");
		var categoryPids = [];
		var groupPids = [];
		var pid = saleTargetGroupPid;
		$("#pCategory").find('input[type="checkbox"]:checked').each(function() {
			categoryPids.push($(this).val());
		});
		$("#pGroup").find('input[type="checkbox"]:checked').each(function() {
			groupPids.push($(this).val());
		});
		$
				.ajax({
					url : salesTargetGroupContextPath
							+ "/filterByCategoryGroup/" + pid,
					type : "GET",
					data : {
						categoryPids : categoryPids.join(","),
						groupPids : groupPids.join(",")
					},
					success : function(data) {
						if (data) {
							$.each(data.allProductProfile, function(index,
									product) {
								$('#tblProducts').append(
										'<tr><td><input name="product" type="checkbox" value="'
												+ product.pid + '" /></td>'
												+ '<td>' + product.name
												+ '</td>' + '</tr></tbody>');
							});

							$.each(data.assignedProductProfile, function(index,
									product) {
								$(
										"#productsCheckboxes input:checkbox[value="
												+ product.pid + "]").prop(
										"checked", true);
							});
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function saveAssignedProducts() {

		$(".error-msg").html("");
		var selectedProducts = "";

		$.each($("input[name='product']:checked"), function() {
			selectedProducts += $(this).val() + ",";
		});
		if (selectedProducts == "") {
			$(".error-msg").html("Please select Products");
			return;
		}
		$.ajax({
			url : salesTargetGroupContextPath + "/saveProducts",
			type : "POST",
			data : {
				salesTargetGroupPid : saleTargetGroupPid,
				assignedProducts : selectedProducts,
			},
			success : function(status) {
				$("#tasksModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function saveAssignedLocations() {
		
		var saleTargetGroupPid = salesTargetGroupModel.pid ;

		$(".error-msg").html("");
		var selectedLocations = "";

		$.each($("input[name='location']:checked"), function() {
			selectedLocations += $(this).val() + ",";
		});
		
		console.log(selectedLocations);
		if (selectedLocations == "") {
			$(".error-msg").html("Please select Locations");
			return;
		}
		$.ajax({
			url : salesTargetGroupContextPath + "/saveLocations",
			type : "POST",
			data : {
				salesTargetGroupPid : saleTargetGroupPid,
				assignedLocations : selectedLocations,
			},
			success : function(status) {
				$("#tasksModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function createUpdateSalesTargetGroup(el) {
		salesTargetGroupModel.name = $('#field_name').val();
		salesTargetGroupModel.alias = $('#field_alias').val();
		salesTargetGroupModel.description = $('#field_description').val();
		salesTargetGroupModel.targetUnit = $('#field_target_unit').val();
		salesTargetGroupModel.targetSettingType = $('#field_targetSettingType').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(salesTargetGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showSalesTargetGroup(id) {
		$.ajax({
			url : salesTargetGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_description').text(data.description);
				$('#lbl_targetUnit').text(data.targetUnit);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editSalesTargetGroup(id) {
		$.ajax({
			url : salesTargetGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				$('#field_target_unit').val(data.targetUnit);
				$('#field_targetSettingType').val(data.targetSettingType);
				// set pid
				salesTargetGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteSalesTargetGroup(actionurl, id) {
		$.ajax({
			url : actionurl,
			method : 'DELETE',
			success : function(data) {
				onDeleteSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = salesTargetGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = salesTargetGroupContextPath;
	}

	SalesTargetGroup.showModalPopup = function(el, id, action, type) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showSalesTargetGroup(id);
				break;
			case 1:
				editSalesTargetGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', salesTargetGroupContextPath + "/"
						+ id);
				break;
			case 3:
				loadUserDocument(id, type);
				break;
			case 4:
				$(".error-msg").html("");
				$('#tblProducts').html("");
				loadproducts(id);
				break
			}
		}
		el.modal('show');
	}

	SalesTargetGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		salesTargetGroupModel.pid = null; // reset salesTargetGroup model;
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