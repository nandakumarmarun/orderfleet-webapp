// Create a PriceLevelList object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PriceLevelList) {
	this.PriceLevelList = {};
}

(function() {
	'use strict';

	var priceLevelListContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#priceLevelListForm");
	var deleteForm = $("#deleteForm");
	var priceLevelListModel = {
		pid : null,
		priceLevelPid : null,
		productProfilePid : null,
		price : null,
		rangeFrom : null,
		rangeTo : null
	};

	// Specify the validation rules
	var validationRules = {
		priceLevelPid : {
			valueNotEquals : "-1"
		},
		productProfilePid : {
			valueNotEquals : "-1",
		/*
		 * valueNotEquals : { depends : function() { if (priceLevelListModel.pid ==
		 * null) { return "-1"; }else{ return "0"; } } }
		 */
		},
		price : {
			required : true,
			maxlength : 10
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		priceLevelPid : {
			valueNotEquals : "This field is required."
		},
		productProfilePid : {
			valueNotEquals : "This field is required."
		},
		price : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 10 characters."
		},
	};

	$(document).ready(function() {

		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdatePriceLevelList(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deletePriceLevelList(e.currentTarget.action);
		});

		$("input[name='filterBy']").click(function() {
			$('#field_category').val("-1");
			$('#field_group').val("-1");
			$('#field_product').html("<option value='-1'>No Data</option>");
			if ($(this).val() == 'Category') {
				$('#field_category').show();
				$('#field_group').hide();
			} else {
				$('#field_category').hide();
				$('#field_group').show();
			}
		});

		$("input[name='searchFilterBy']").click(function() {
			$('#dbCategory').val("no");
			$('#dbGroup').val("no");
			$('#dbProduct').html("<option value='no'>No Data</option>");
			if ($(this).val() == 'Category') {
				$('#dbCategory').show();
				$('#dbGroup').hide();
			} else {
				$('#dbCategory').hide();
				$('#dbGroup').show();
			}
		});

		// load initila data
		PriceLevelList.filter();
	});

	PriceLevelList.loadProducts = function() {

		var filterBy = $('input:radio[name=filterBy]:checked').val();
		console.log(filterBy + " :- filterBy");
		var pid = "";
		if (filterBy == 'Category') {
			pid = $('#field_category').val();
		} else {
			pid = $('#field_group').val();
		}
		$('#field_product').html(
				"<option value='-1'>Product loading...</option>");
		$.ajax({
			url : priceLevelListContextPath + "/load-products",
			method : 'GET',
			data : {
				filterBy : filterBy,
				pid : pid
			},
			success : function(products) {
				if (products.length > 0) {
					$('#field_product').html(
							"<option value='-1'>Select	Product</option>");
				} else {
					$('#field_product').html(
							"<option value='-1'>No Data</option>");
				}
				$.each(products, function(index, product) {
					$('#field_product').append(
							"<option value='" + product.pid + "'>"
									+ product.name + "</option>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function createUpdatePriceLevelList(el) {
		priceLevelListModel.priceLevelPid = $('#field_priceLevel').val();
		priceLevelListModel.productProfilePid = $('#field_product').val();
		priceLevelListModel.price = $('#field_price').val();
		priceLevelListModel.rangeFrom = $('#field_rangeFrom').val();
		priceLevelListModel.rangeTo = $('#field_rangeTo').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(priceLevelListModel),
			success : function(data) {
				if($(el).attr('method') == 'PUT'){
					$("#myModal").modal('hide');
					$('tr[id='+data.pid+'] td:eq(0)').text(data.priceLevelName);
					$('tr[id='+data.pid+'] td:eq(1)').text(data.productProfileName);
					$('tr[id='+data.pid+'] td:eq(2)').text(data.rangeFrom);
					$('tr[id='+data.pid+'] td:eq(3)').text(data.rangeTo);
					$('tr[id='+data.pid+'] td:eq(4)').text(data.price);
				} else {
					onSaveSuccess(data);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showPriceLevelList(id) {
		$.ajax({
			url : priceLevelListContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_priceLevel').text(data.priceLevelName);
				$('#lbl_productProfile').text(data.productProfileName);
				$('#lbl_price').text(data.price);
				$('#lbl_rangeFrom').text(data.rangeFrom);
				$('#lbl_rangeTo').text(data.rangeTo);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editPriceLevelList(id) {
		$('.divProduct').hide();
		$.ajax({
			url : priceLevelListContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_priceLevel').val(data.priceLevelPid);
				$('#field_price').val(data.price);
				$('#field_rangeFrom').val(data.rangeFrom);
				$('#field_rangeTo').val(data.rangeTo);

				// set pid
				priceLevelListModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deletePriceLevelList(actionurl, id) {
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

	/***************************************************************************
	 * Filter price level list by price level and product
	 **************************************************************************/
	PriceLevelList.searchLoadProducts = function() {

		var searchFilterBy = $('input:radio[name=searchFilterBy]:checked')
				.val();
		console.log(searchFilterBy + " :- searchFilterBy");
		var pid = "";
		if (searchFilterBy == 'Category') {
			pid = $('#dbCategory').val();
		} else {
			pid = $('#dbGroup').val();
		}
		$('#dbProduct').html("<option value='no'>Product loading...</option>");
		$
				.ajax({
					url : priceLevelListContextPath + "/load-products",
					method : 'GET',
					data : {
						filterBy : searchFilterBy,
						pid : pid
					},
					success : function(products) {
						if (products.length > 0) {
							$('#dbProduct')
									.html(
											"<option value='no'>Select	Product</option>");
						} else {
							$('#dbProduct').html(
									"<option value='no'>No Data</option>");
						}
						$.each(products, function(index, product) {
							$('#dbProduct').append(
									"<option value='" + product.pid + "'>"
											+ product.name + "</option>");
						});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}

	PriceLevelList.filter = function() {
		$('#tBodyPriceLevelList').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		var filterBy = $('input:radio[name=searchFilterBy]:checked').val();
		var filterByPid = "";
		if (filterBy == 'Category') {
			filterByPid = $('#dbCategory').val();
		} else {
			filterByPid = $('#dbGroup').val();
		}
		console.log(filterBy);
		$
				.ajax({
					url : priceLevelListContextPath + "/filter",
					type : 'GET',
					data : {
						priceLevelPid : $("#dbPriceLevel").val(),
						productPid : $("#dbProduct").val(),
						filterBy:filterBy,
						filterByPid:filterByPid,
					},
					success : function(priceLevelLists) {
						$('#tBodyPriceLevelList').html("");
						if (priceLevelLists.length == 0) {
							$('#tBodyPriceLevelList')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										priceLevelLists,
										function(index, priceLevelList) {

											$('#tBodyPriceLevelList')
													.append(
															"<tr id=\""+ priceLevelList.pid +"\"><td>"
																	+ priceLevelList.priceLevelName
																	+ "</td><td>"
																	+ priceLevelList.productProfileName
																	+ "</td><td>"
																	+ priceLevelList.rangeFrom
																	+ "</td><td>"
																	+ priceLevelList.rangeTo
																	+ "</td><td>"
																	+ priceLevelList.price
																	+ "</td><td>"
																	+ priceLevelList.discount
																	+ "</td><td><button type='button' class='btn btn-blue' onclick='PriceLevelList.showModalPopup($(\"#viewModal\"),\""
																	+ priceLevelList.pid
																	+ "\",0);'>View</button>&nbsp;<button type='button' class='btn btn-blue' onclick='PriceLevelList.showModalPopup($(\"#myModal\"),\""
																	+ priceLevelList.pid
																	+ "\",1);'>Edit</button>&nbsp;<button type='button' class='btn btn-danger' onclick='PriceLevelList.showModalPopup($(\"#deleteModal\"),\""
																	+ priceLevelList.pid
																	+ "\",2);'>Delete</button></td></tr>");
										});

					}
				});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = priceLevelListContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = priceLevelListContextPath;
	}

	PriceLevelList.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showPriceLevelList(id);
				break;
			case 1:
				editPriceLevelList(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', priceLevelListContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	function searchTable(inputVal) {
		console.log(inputVal);
		var table = $('#tBodyPriceLevelList');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 5) {
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

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		priceLevelListModel.pid = null; // reset priceLevelList model;

		$('.divProduct').show();
		$('#field_category').show();
		$('#field_group').hide();
		$('#field_product').html("<option value='-1'>No Data</option>");
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