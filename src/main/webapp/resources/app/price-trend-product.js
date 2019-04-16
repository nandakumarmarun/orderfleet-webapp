// Create a Price Trend Product object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PriceTrendProduct) {
	this.PriceTrendProduct = {};
}

(function() {
	'use strict';

	var priceTrendProductContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#priceTrendProductForm");
	var deleteForm = $("#deleteForm");
	var priceTrendProductModel = {
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

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdatePriceTrendProduct(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deletePriceTrendProduct(e.currentTarget.action);
		});

		$('#btnSaveCompetitors').on('click', function() {
			saveAssignedCompetitors();
		});
		
		$('#btnActivatePriceTrendProduct').on('click', function() {
			activatePriceTrendProduct();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activatePriceTrendProduct(){
		$('.error-msg').html("");
		var selectedPriceTrendProduct="";
		
		$.each($("input[name='pricetrendproduct']:checked"),function(){
			selectedPriceTrendProduct +=$(this).val()+",";
		});
		
		if(selectedPriceTrendProduct==""){
			$('.error-msg').html("Please Select Price Trend Product");
			return;
		}
		
		$.ajax({
			url:priceTrendProductContextPath+"/activatePriceTrendProduct",
			method:'POST',
			data:{
				pricetrendproduct:selectedPriceTrendProduct,
			},
			success:function(data){
				onSaveSuccess(data);
			},
			error:function(xhr,error){
				onError(xhr, error);
			}
		});
	}
	
	function createUpdatePriceTrendProduct(el) {
		priceTrendProductModel.name = $('#field_name').val();
		priceTrendProductModel.alias = $('#field_alias').val();
		priceTrendProductModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(priceTrendProductModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showPriceTrendProduct(id) {
		$.ajax({
			url : priceTrendProductContextPath + "/" + id,
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

	function editPriceTrendProduct(id) {
		$.ajax({
			url : priceTrendProductContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				// set pid
				priceTrendProductModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deletePriceTrendProduct(actionurl, id) {
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

	PriceTrendProduct.assignCompetitors = function(el, pid, type) {
		priceTrendProductModel.pid = pid;

		// clear all check box
		$("#divCompetitors input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : priceTrendProductContextPath + "/competitors",
			type : "GET",
			data : {
				priceTrendProductPid : priceTrendProductModel.pid
			},
			success : function(assignedCompetitors) {
				if (assignedCompetitors) {
					$.each(assignedCompetitors, function(index, competitor) {
						$(
								"#divCompetitors input:checkbox[value="
										+ competitor.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		el.modal('show');

	}

	function saveAssignedCompetitors() {

		$(".error-msg").html("");
		var selectedCompetitors = "";

		$.each($("input[name='competitor']:checked"), function() {
			selectedCompetitors += $(this).val() + ",";
		});

		if (selectedCompetitors == "") {
			$(".error-msg").html("Please select Price Level");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : priceTrendProductContextPath + "/assign-competitors",
			type : "POST",
			data : {
				priceTrendProductPid : priceTrendProductModel.pid,
				assignedCompetitors : selectedCompetitors
			},
			success : function(status) {
				$("#competitorsModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	PriceTrendProduct.setActive=function(name,pid,active){
		priceTrendProductModel.pid=pid;
		priceTrendProductModel.activated=active;
		priceTrendProductModel.name=name;
		if(confirm("Are you confirm?")){
			$.ajax({
				url:priceTrendProductContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(priceTrendProductModel),
				success:function(data){
					onSaveSuccess(data);
				},
				error:function(xhr,error){
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = priceTrendProductContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = priceTrendProductContextPath;
	}

	PriceTrendProduct.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showPriceTrendProduct(id);
				break;
			case 1:
				editPriceTrendProduct(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', priceTrendProductContextPath + "/"
						+ id);
				break;
			}
		}
		el.modal('show');
	}

	PriceTrendProduct.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		priceTrendProductModel.pid = null; // reset priceTrendProduct model;
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