if (!this.PriceLevelAccountEcomProductGroup) {
	this.PriceLevelAccountEcomProductGroup = {};
}

(function() {
	'use strict';

	var priceLevelAccountEcomProductGroupContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#priceLevelAccountEcomProductGroupForm");
	var deleteForm = $("#deleteForm");
	var priceLevelAccountEcomProductGroupModel = {
		pid : null,
		accountPid : null,
		productGroupPid : null,
		priceLevelPid : null,

	};

	// Specify the validation rules
	var validationRules = {

		accountPid : {
			valueNotEquals : "-1"
		},
		productGroupPid : {
			valueNotEquals : "-1"
		},
		priceLevelPid : {
			valueNotEquals : "-1"
		},

	};

	// Specify the validation error messages
	var validationMessages = {
		accountPid : {
			valueNotEquals : "This field is required."
		},
		productGroupPid : {
			valueNotEquals : "This field is required."
		},
		priceLevelPid : {
			valueNotEquals : "This field is required."
		},
	};

	$(document).ready(function() {

		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdatePriceLevelAccountProductGroup(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deletePriceLevelAccountProductGroup(e.currentTarget.action);
		});
		
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

	});

	function searchTable(inputVal) {
		var table = $('#tbodyPriceLevelAccountProductGroups');
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
	
	PriceLevelAccountEcomProductGroup.loadProductGroup = function() {
		$("#divProdcutGroup").html("");
		if ($("#dbPriceLevel").val() == "-1") {
			return;
		}
		$
				.ajax({
					url : priceLevelAccountEcomProductGroupContextPath
							+ "/load-productgroup-by-price",
					type : 'GET',
					data : {
						priceLevelPid : $("#dbPriceLevel").val()
					},
					success : function(productGroups) {
						if (productGroups != null && productGroups.length > 0) {
							$
									.each(
											productGroups,
											function(index, productGroup) {
												$("#divProdcutGroup")
														.append(
																'<div class="col-md-4"><div class="checkbox"><label> <input type="checkbox" value="'
																		+ productGroup.pid
																		+ '">'
																		+ productGroup.name
																		+ '</label></div></div>');
											});
						}else{
							$("#divProdcutGroup")
							.append(
									'<div class="col-md-4"><h3>No Data Available</h3></div>');
						}
					}
				});
	}

	PriceLevelAccountEcomProductGroup.saveAccountAndProductGroup = function() {
		var accountPids = [];
		var productGroupPids = [];
		$("#account").find('input[type="checkbox"]:checked').each(function() {
			accountPids.push($(this).val());
		});
		$("#productGroup").find('input[type="checkbox"]:checked').each(
				function() {
					productGroupPids.push($(this).val());
				});

		if (accountPids.length == 0) {
			alert("Please Select Account");
			return;
		}

		if (productGroupPids.length == 0) {
			alert("Please Select Product Group");
			return;
		}

		$.ajax({
			url : priceLevelAccountEcomProductGroupContextPath
					+ "/saveAccountAndProductGroup",
			method : 'POST',
			data : {
				accountPids : accountPids.join(","),
				productGroupPids : productGroupPids.join(","),
				priceLevelPid : $('#dbPriceLevel').val(),
			},

			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function createUpdatePriceLevelAccountProductGroup(el) {
		priceLevelAccountEcomProductGroupModel.accountPid = $('#dbAccount').val();
		priceLevelAccountEcomProductGroupModel.productGroupPid = $(
				'#dbProductGroup').val();
		priceLevelAccountEcomProductGroupModel.priceLevelPid = $('#dbPriceLevel1')
				.val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(priceLevelAccountEcomProductGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showPriceLevelAccountProductGroup(pid) {
		$.ajax({
			url : priceLevelAccountEcomProductGroupContextPath + "/" + pid,
			method : 'GET',
			success : function(data) {
				$('#lbl_account').text(data.accountName);
				$('#lbl_productgroup').text(data.productGroupName);
				$('#lbl_pricelevel').text(data.priceLevelName);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editPriceLevelAccountProductGroup(pid) {
		$.ajax({
			url : priceLevelAccountEcomProductGroupContextPath + "/" + pid,
			method : 'GET',
			success : function(data) {
				$('#dbAccount').val(data.accountPid);
				$('#dbProductGroup').val(data.productGroupPid);
				$('#dbPriceLevel1').val(data.priceLevelPid);
				// set pid
				priceLevelAccountEcomProductGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deletePriceLevelAccountProductGroup(actionurl, pid) {
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
		window.location = priceLevelAccountEcomProductGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = priceLevelAccountEcomProductGroupContextPath;
	}

	PriceLevelAccountEcomProductGroup.showModalPopup = function(el, pid, action) {
		resetForm();
		if (pid) {
			switch (action) {
			case 0:
				showPriceLevelAccountProductGroup(pid);
				break;
			case 1:
				editPriceLevelAccountProductGroup(pid);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action',
						priceLevelAccountEcomProductGroupContextPath + "/" + pid);
				break;
			}
		}
		el.modal('show');
	}

	PriceLevelAccountEcomProductGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		priceLevelAccountEcomProductGroupModel.pid = null; // reset
		// priceLevelAccountProductGroup
		// model;
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