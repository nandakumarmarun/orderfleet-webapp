// Create a UnitOfMeasure object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UnitOfMeasure) {
	this.UnitOfMeasure = {};
}

(function() {
	'use strict';

	var unitOfMeasureContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#unitOfMeasureForm");
	var deleteForm = $("#deleteForm");
	var unitOfMeasureModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		uomId : null,
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
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
		}
	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateUnitOfMeasure(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteUnitOfMeasure(e.currentTarget.action);
				});

				// Search for Product Group
				$('#btnSearchProduct').click(function() {
					searchTableProduct($("#searchProduct").val());
				});

				$('#btnSaveProducts').on('click', function() {
					saveAssignedProducts();
				});

				$('#btnSaveEcomProducts').on('click', function() {
					saveAssignedEcomProducts();
				});

				$('#btnSaveTax').on('click', function() {
					saveAssignedTax();
				});

				$('#btnSaveUnitQty').on('click', function() {
					$(".error-msg").html('');
					saveAssignedUnitQty();
				});
				
				// validate image
				$('#field_image').bind(
						'change',
						function() {
							console.log("field_Image................");
							var size = this.files[0].size;
							// in bytes
							/*
							 * if (size > 555120) { $('#field_Image').val('')
							 * addErrorAlert('image size exeeded max limit of 5
							 * KB'); return; } $('.alert').hide();
							 */

							// image preview and set bytes and contentType
							var file = $(this)[0].files[0];
							var fileReader = new FileReader();
							fileReader.readAsDataURL(file);

							fileReader.onload = function(e) {
								$('#previewImage').attr('src',
										fileReader.result);
								var base64Data = e.target.result
										.substr(e.target.result
												.indexOf('base64,')
												+ 'base64,'.length);
								unitOfMeasureModel.image = base64Data;
								unitOfMeasureModel.imageContentType = file.type;
							};

						});

				// table search
				$('#btnSearch').click(
						function() {
							searchTable($("#search").val(), $('#tblProducts'),
									'filter');
						});
				// select all checkbox in table tblProducts
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]:visible').prop(
									'checked', $(this).prop('checked'));
						});

				// table search ecom product
				$('#btnEcomSearch').click(
						function() {
							searchTable($("#searchEcom").val(),
									$('#tblEcomProducts'), 'filterEcom');
						});

				// select all checkbox in table tblEcomProducts
				$('input:checkbox.allcheckboxEcom').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]:visible').prop(
									'checked', $(this).prop('checked'));
						});
				$('#btnNotAssigned').click(function() {
					$(this).html('Loading...');
					$("#tblProducts").hide();
					loadNotAssignedProducts();
				});

				$('#btnActivateUnitOfMeasure').on('click', function() {
					activateAssignedUnitOfMeasure();
				});

				

			});

	function searchTableProduct(inputVal) {
		var table = $('#tBodyUnitOfMeasure');
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

	function activateAssignedUnitOfMeasure() {
		$(".error-msg").html("");
		var selectedUnitOfMeasure = "";

		$.each($("input[name='productgroup']:checked"), function() {
			selectedUnitOfMeasure += $(this).val() + ",";
		});

		if (selectedUnitOfMeasure == "") {
			$(".error-msg").html("Please select UnitOfMeasure");
			return;
		}
		$.ajax({
			url : unitOfMeasureContextPath + "/activateUnitOfMeasure",
			type : "POST",
			data : {
				productgroups : selectedUnitOfMeasure,
			},
			success : function(status) {
				$("#enableUnitOfMeasureModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function loadNotAssignedProducts() {
		$.ajax({
			url : unitOfMeasureContextPath + "/not-assigned-products",
			type : "GET",
			success : function(data) {
				if (data) {
					$.each(data, function(index, product) {
						$(
								"#tblProducts input:checkbox[value="
										+ product.pid + "]").closest('tr')
								.hide();
					});
				}
				$('#btnNotAssigned').html('Load Not Assigned Products');
				$("#tblProducts").show();
			},
			error : function(xhr, error) {
				$('#btnNotAssigned').html('Load Not Assigned Products');
				$("#tblProducts").show();
				onError(xhr, error);
			},
		});
	}

	function searchTable(inputVal, table, filter) {
		var filterBy = $("input[name='" + filter + "']:checked").val();
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

	function loadproducts(pid) {

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("", $('#tblProducts'), 'filter');

		// clear all check box
		$("#productsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : unitOfMeasureContextPath + "/findProducts/" + pid,
			type : "GET",
			success : function(data) {
				unitOfMeasureModel.pid = pid;
				if (data) {
					$.each(data, function(index, product) {
						$(
								"#productsCheckboxes input:checkbox[value="
										+ product.pid + "]").prop("checked",
								true);
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
			url : unitOfMeasureContextPath + "/assignProducts",
			type : "POST",
			data : {
				pid : unitOfMeasureModel.pid,
				assignedproducts : selectedProducts,
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

	function createUpdateUnitOfMeasure(el) {
		unitOfMeasureModel.name = $('#field_name').val();
		unitOfMeasureModel.alias = $('#field_alias').val();
		unitOfMeasureModel.description = $('#field_description').val();
		unitOfMeasureModel.uomId = $('#field_uomId').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(unitOfMeasureModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	UnitOfMeasure.setActive = function(name, productPid, isActivated) {
		unitOfMeasureModel.pid = productPid;
		unitOfMeasureModel.activated = isActivated;
		unitOfMeasureModel.name = name;
		if (confirm("Are you sure?")) {
			// update status;changeStatus
			$.ajax({
				url : unitOfMeasureContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(unitOfMeasureModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	UnitOfMeasure.setThirdpartyUpdate=function(name,pid,thirdpartyUpdate){
		unitOfMeasureModel.pid=pid;
		unitOfMeasureModel.thirdpartyUpdate=thirdpartyUpdate;
		unitOfMeasureModel.name=name;
		if(confirm("Are you confirm?")){
		$.ajax({
			url:unitOfMeasureContextPath+"/changeThirdpartyUpdateStatus",
			method:'POST',
			contentType:"application/json; charset:utf-8",
			data:JSON.stringify(unitOfMeasureModel),
			success:function(data){
				console.log(data);
				onSaveSuccess(data);
			},
			error:function(xhr,error){
				onError(xhr, error)
			}
		});
		}
	}
	
	function showUnitOfMeasure(id) {
		$.ajax({
			url : unitOfMeasureContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data.image);
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_description').text(data.description);
				$('#lbl_uomId').text(data.uomId);
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editUnitOfMeasure(id) {
		$.ajax({
			url : unitOfMeasureContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				$('#field_uomId').val(data.uomId);
				
				// set pid
				unitOfMeasureModel.pid = data.pid;
				
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteUnitOfMeasure(actionurl, id) {
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

	function loadEcomProducts(pid) {

		$("input[name='filterEcom'][value='all']").prop("checked", true);
		$("#searchEcom").val("");
		searchTable("", $('#tblEcomProducts'), 'filterEcom');

		// clear all check box
		$("#ecomProductsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : unitOfMeasureContextPath + "/findEcomProducts/" + pid,
			type : "GET",
			success : function(data) {
				unitOfMeasureModel.pid = pid;
				if (data) {
					$.each(data, function(index, ecomProduct) {
						$(
								"#ecomProductsCheckboxes input:checkbox[value="
										+ ecomProduct.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedEcomProducts() {

		$(".error-msg").html("");
		var selectedEcomProducts = "";

		$.each($("input[name='ecomProduct']:checked"), function() {
			selectedEcomProducts += $(this).val() + ",";
		});

		if (selectedEcomProducts == "") {
			$(".error-msg").html("Please select Ecom Products");
			return;
		}
		$.ajax({
			url : unitOfMeasureContextPath + "/assignEcomProducts",
			type : "POST",
			data : {
				pid : unitOfMeasureModel.pid,
				assignedEcomProducts : selectedEcomProducts,
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

	function saveAssignedTax() {

		if ($("#dbUnitOfMeasures").val() == null && $("#dbUnitOfMeasure1s").val()== null) {
			$(".error-msg").html("Please select product groups");
			return;
		}
		var selectedTaxMasters = "";
		var prodGrpPid=$("#dbUnitOfMeasure1s").val();
		
		$.each($("input[name='taxMaster']:checked"), function() {
			selectedTaxMasters += $(this).val() + ",";
		});

		if ($("#txtTax").val() == "" && selectedTaxMasters == "") {
			if($("#dbUnitOfMeasures").val() == null){
			$(".error-msg").html("Please select tax Master");
			return;
			}else{
				$(".error-msg").html("Please enter tax rate");
				return;
			}
		}
		$(".error-msg").html("Please wait...");

		var taxRate = {
			unitOfMeasures : $("#dbUnitOfMeasures").val(),
			taxRate : $("#txtTax").val()

		};
		if($("#txtTax").val() != ""){
		$.ajax({
			url : unitOfMeasureContextPath + "/assign-tax",
			type : "POST",
			data : JSON.stringify(taxRate),
			contentType : "application/json; charset=utf-8",
			success : function(status) {
				$("#assignTaxModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
		if(selectedTaxMasters != ""){
			
			$.ajax({
				url : unitOfMeasureContextPath + "/assign-tax-master/"+prodGrpPid,
				type : "POST",
				data : {
					selectedTax : selectedTaxMasters
				},
				success : function(status) {
					$("#assignTaxModal").modal("hide");
					onSaveSuccess(status);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		}
	
	}
	
	function saveAssignedUnitQty() {
		let prodGrpPids = $("#dbUQUnitOfMeasures").val();
		let unitQty = $("#txtUnitQty").val();
		
		if (prodGrpPids == null) {
			$(".error-msg").html("Please select product groups");
			return;
		}
		if (unitQty < 0) {
			$(".error-msg").html("Enter Unit Quantity");
			return;
		}
		$(".error-msg").html("Please wait...");
		var setUnitQty = {
			unitOfMeasures : prodGrpPids,
			taxRate : unitQty

		};
		$.ajax({
			url : unitOfMeasureContextPath + "/assign-unit-quantity",
			type : "POST",
			data : JSON.stringify(setUnitQty),
			contentType : "application/json; charset=utf-8",
			success : function(status) {
				$("#assignTaxModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = unitOfMeasureContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = unitOfMeasureContextPath;
	}

	UnitOfMeasure.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showUnitOfMeasure(id);
				break;
			case 1:
				editUnitOfMeasure(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', unitOfMeasureContextPath + "/" + id);
				break;
			case 3:
				loadproducts(id);
				break;
			case 4:
				loadEcomProducts(id);
				break;
			case 5:
				loadTaxMasters();
			}

		}
		el.modal('show');
	}
	
	function loadTaxMasters() {
		$.ajax({
			url : unitOfMeasureContextPath + "/findTaxMaster" ,
			type : "GET",
			success : function(data) {
				if (data) {
					var array=[];
					$.each(data, function(index, group) {
					array.push(group.pid);
						$.each(group.unitOfMeasureTaxMasterDTOs, function(index, taxMaster) {
						$("#taxcheck input[type='checkbox'][value="+ taxMaster.pid + "]").prop("checked",true);
						});
						
					});
					$("#dbUnitOfMeasure1s").val(array);
//					$("#dbUnitOfMeasure1s").multiselect("refresh");
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	UnitOfMeasure.loadUnitOfMeasures=function() {
		var selectedTaxMasters = "";
		
		$.each($("input[name='taxMaster']:checked"), function() {
			selectedTaxMasters += $(this).val() + ",";
		});
		console.log(1);
		$.ajax({
			url : unitOfMeasureContextPath + "/loadUnitOfMeasureByTaxMaster" ,
			type : "GET",
			data : {
				selectedTax : selectedTaxMasters
			},
			success : function(data) {
				if (data) {
					var array=[];
					$.each(data, function(index, group) {
					array.push(group.pid);
					});
					$("#dbUnitOfMeasure1s").val(array);
//					$("#dbUnitOfMeasure1s").multiselect("refresh");
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
	}
	
	UnitOfMeasure.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		unitOfMeasureModel.pid = null; // reset unitOfMeasure model;
		unitOfMeasureModel.image = null;
		unitOfMeasureModel.imageContentType = null;
		$('#previewImage').attr('src', '');

		$("#dbUnitOfMeasures").val(null);
		$("#txtTax").val("");
		$(".error-msg").html("");
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