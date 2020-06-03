// Create a ProductGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.EcomProductGroup) {
	this.EcomProductGroup = {};
}

(function() {
	'use strict';

	var productGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#productGroupForm");
	var deleteForm = $("#deleteForm");
	var productGroupModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		image : null,
		thirdpartyUpdate:null,
		imageContentType : null
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
						createUpdateProductGroup(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteProductGroup(e.currentTarget.action);
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
								productGroupModel.image = base64Data;
								productGroupModel.imageContentType = file.type;
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

				$('#btnActivateProductGroup').on('click', function() {
					activateAssignedProductGroup();
				});

				

			});

	function searchTableProduct(inputVal) {
		var table = $('#tBodyProductGroup');
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

	function activateAssignedProductGroup() {
		$(".error-msg").html("");
		var selectedProductGroup = "";

		$.each($("input[name='productgroup']:checked"), function() {
			selectedProductGroup += $(this).val() + ",";
		});

		if (selectedProductGroup == "") {
			$(".error-msg").html("Please select ProductGroup");
			return;
		}
		$.ajax({
			url : productGroupContextPath + "/activateProductGroup",
			type : "POST",
			data : {
				productgroups : selectedProductGroup,
			},
			success : function(status) {
				$("#enableProductGroupModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function loadNotAssignedProducts() {
		$.ajax({
			url : productGroupContextPath + "/not-assigned-products",
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
			url : productGroupContextPath + "/findProducts/" + pid,
			type : "GET",
			success : function(data) {
				productGroupModel.pid = pid;
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
			url : productGroupContextPath + "/assignProducts",
			type : "POST",
			data : {
				pid : productGroupModel.pid,
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

	function createUpdateProductGroup(el) {
		productGroupModel.name = $('#field_name').val();
		productGroupModel.alias = $('#field_alias').val();
		productGroupModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(productGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	EcomProductGroup.setActive = function(name, productPid, isActivated) {
		productGroupModel.pid = productPid;
		productGroupModel.activated = isActivated;
		productGroupModel.name = name;
		if (confirm("Are you sure?")) {
			// update status;changeStatus
			$.ajax({
				url : productGroupContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(productGroupModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	EcomProductGroup.setThirdpartyUpdate=function(name,pid,thirdpartyUpdate){
		productGroupModel.pid=pid;
		productGroupModel.thirdpartyUpdate=thirdpartyUpdate;
		productGroupModel.name=name;
		if(confirm("Are you confirm?")){
		$.ajax({
			url:productGroupContextPath+"/changeThirdpartyUpdateStatus",
			method:'POST',
			contentType:"application/json; charset:utf-8",
			data:JSON.stringify(productGroupModel),
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
	
	function showProductGroup(id) {
		$.ajax({
			url : productGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data.image);
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_description').text(data.description);
				$('#div_image')
						.html(
								'<img src="data:image/png;base64,' + data.image
										+ '"/>');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editProductGroup(id) {
		$.ajax({
			url : productGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				$('#previewImage').attr('src',
						'data:image/png;base64,' + data.image);
				// set pid
				productGroupModel.pid = data.pid;
				// set image byte array
				productGroupModel.image = data.image;
				productGroupModel.imageContentType = data.imageContentType;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteProductGroup(actionurl, id) {
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
			url : productGroupContextPath + "/findEcomProducts/" + pid,
			type : "GET",
			success : function(data) {
				productGroupModel.pid = pid;
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
			url : productGroupContextPath + "/assignEcomProducts",
			type : "POST",
			data : {
				pid : productGroupModel.pid,
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

		if ($("#dbProductGroups").val() == null && $("#dbProductGroup1s").val()== null) {
			$(".error-msg").html("Please select product groups");
			return;
		}
		var selectedTaxMasters = "";
		var prodGrpPid=$("#dbProductGroup1s").val();
		
		$.each($("input[name='taxMaster']:checked"), function() {
			selectedTaxMasters += $(this).val() + ",";
		});

		if ($("#txtTax").val() == "" && selectedTaxMasters == "") {
			if($("#dbProductGroups").val() == null){
			$(".error-msg").html("Please select tax Master");
			return;
			}else{
				$(".error-msg").html("Please enter tax rate");
				return;
			}
		}
		$(".error-msg").html("Please wait...");

		var taxRate = {
			productGroups : $("#dbProductGroups").val(),
			taxRate : $("#txtTax").val()

		};
		if($("#txtTax").val() != ""){
		$.ajax({
			url : productGroupContextPath + "/assign-tax",
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
				url : productGroupContextPath + "/assign-tax-master/"+prodGrpPid,
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
		let prodGrpPids = $("#dbUQProductGroups").val();
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
			productGroups : prodGrpPids,
			taxRate : unitQty

		};
		$.ajax({
			url : productGroupContextPath + "/assign-unit-quantity",
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
		window.location = productGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = productGroupContextPath;
	}

	EcomProductGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showProductGroup(id);
				break;
			case 1:
				editProductGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', productGroupContextPath + "/" + id);
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
			url : productGroupContextPath + "/findTaxMaster" ,
			type : "GET",
			success : function(data) {
				if (data) {
					var array=[];
					$.each(data, function(index, group) {
					array.push(group.pid);
						$.each(group.productGroupTaxMasterDTOs, function(index, taxMaster) {
						$("#taxcheck input[type='checkbox'][value="+ taxMaster.pid + "]").prop("checked",true);
						});
						
					});
					$("#dbProductGroup1s").val(array);
//					$("#dbProductGroup1s").multiselect("refresh");
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	EcomProductGroup.loadProductGroups=function() {
		var selectedTaxMasters = "";
		
		$.each($("input[name='taxMaster']:checked"), function() {
			selectedTaxMasters += $(this).val() + ",";
		});
		console.log(1);
		$.ajax({
			url : productGroupContextPath + "/loadProductGroupByTaxMaster" ,
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
					$("#dbProductGroup1s").val(array);
//					$("#dbProductGroup1s").multiselect("refresh");
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
	}
	
	EcomProductGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		productGroupModel.pid = null; // reset productGroup model;
		productGroupModel.image = null;
		productGroupModel.imageContentType = null;
		$('#previewImage').attr('src', '');

		$("#dbProductGroups").val(null);
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