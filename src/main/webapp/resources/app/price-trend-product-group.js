// Create a PriceTrendProductGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PriceTrendProductGroup) {
	this.PriceTrendProductGroup = {};
}

(function() {
	'use strict';

	var priceTrendProductGroupContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#priceTrendProductGroupForm");
	var deleteForm = $("#deleteForm");
	var priceTrendProductGroupModel = {
		pid : null,
		name : null,
		alias : null,
		description : null

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
						createUpdatePriceTrendProductGroup(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deletePriceTrendProductGroup(e.currentTarget.action);
				});

				$('#btnSaveProducts').on('click', function() {
					saveAssignedProducts();
				});

				$('#btnActivatePriceTrendProductGroup').on('click', function() {

					activatePriceTrendProductGroup();
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function activatePriceTrendProductGroup() {
		$('.error-msg').html("");

		var selectedPriceTrendProductGroup = "";

		$.each($("input[name='pricetrendproductgroup']:checked"), function() {

			selectedPriceTrendProductGroup += $(this).val() + ",";

		});

		if (selectedPriceTrendProductGroup == "") {
			$('.error-msg').html("Please Select Price Trend Product Group");
			return;
		}
		$.ajax({
			url : priceTrendProductGroupContextPath
					+ "/activatepriceTrendProductGroup",
			method : 'POST',
			data : {
				pricetrendproductgroup : selectedPriceTrendProductGroup,
			},
			success : function(data) {

				$('#enablePriceTrendProductGroupModal').modal('hide');
				onSaveSuccess(data);
			},
			error : function(xhr, error) {

				onError(xhr, error);
			}
		});
	}

	function createUpdatePriceTrendProductGroup(el) {
		priceTrendProductGroupModel.name = $('#field_name').val();
		priceTrendProductGroupModel.alias = $('#field_alias').val();
		priceTrendProductGroupModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(priceTrendProductGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showPriceTrendProductGroup(id) {
		$.ajax({
			url : priceTrendProductGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_description').text(data.description);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editPriceTrendProductGroup(id) {
		$.ajax({
			url : priceTrendProductGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				// set pid
				priceTrendProductGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deletePriceTrendProductGroup(actionurl, id) {
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

	function loadProducts(pid) {
		// clear all check box
		$("#divProducts input:checkbox").attr('checked', false);
		$.ajax({
			url : priceTrendProductGroupContextPath + "/" + pid,
			type : "GET",
			success : function(data) {
				console.log(data);
				priceTrendProductGroupModel.pid = data.pid;
				if (data.priceTrendProducts) {
					$.each(data.priceTrendProducts, function(index, product) {
						$(
								"#divProducts input:checkbox[value="
										+ product.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
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
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : priceTrendProductGroupContextPath + "/assign-products",
			type : "POST",
			data : {
				pid : priceTrendProductGroupModel.pid,
				assignedProducts : selectedProducts,
			},
			success : function(status) {
				$("#assignProductsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	PriceTrendProductGroup.setActive = function(name, pid, active) {
		priceTrendProductGroupModel.pid = pid;
		priceTrendProductGroupModel.activated = active;
		priceTrendProductGroupModel.name = name;
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : priceTrendProductGroupContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(priceTrendProductGroupModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = priceTrendProductGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = priceTrendProductGroupContextPath;
	}

	PriceTrendProductGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showPriceTrendProductGroup(id);
				break;
			case 1:
				editPriceTrendProductGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', priceTrendProductGroupContextPath
						+ "/" + id);
				break;
			case 3:
				loadProducts(id);
				break;
			}
		}
		el.modal('show');
	}

	PriceTrendProductGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		priceTrendProductGroupModel.pid = null; // reset priceTrendProductGroup
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