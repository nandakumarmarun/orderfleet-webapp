// Create a EcomProductProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.EcomProductProfile) {
	this.EcomProductProfile = {};
}

(function() {
	'use strict';

	var ecomProductProfileContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#ecomProductProfileForm");
	var deleteForm = $("#deleteForm");
	var ecomProductProfileModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		ecomDisplayAttributes : null,
		offerString : null,
		image : null,
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
		},
		offerString : {
			maxlength : 5000
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
		alias : {
			maxlength : "This field cannot be longer than 5000 characters."
		}
	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateEcomProductProfile(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteEcomProductProfile(e.currentTarget.action);
				});
				$('#btnSaveProducts').on('click', function() {
					saveAssignedProducts();
				});

				$('#btnActivateEcomProductProfile').on('click', function() {
					activateAssignedEcomProductProfile();
				});
				
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

				$('#btnSearchEcomProduct').click(function() {
					searchTableEcomProduct($("#searchEcomProduct").val());
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
								ecomProductProfileModel.image = base64Data;
								ecomProductProfileModel.imageContentType = file.type;
							};

						});
			});
	
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

	function searchTableEcomProduct(inputVal) {
		var table = $('#tBodyEcomProduct');
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

	function activateAssignedEcomProductProfile() {
		$(".error-msg").html("");
		var selectedEcomProductProfile = "";

		$.each($("input[name='ecomproductprofile']:checked"), function() {
			selectedEcomProductProfile += $(this).val() + ",";
		});

		if (selectedEcomProductProfile == "") {
			$(".error-msg").html("Please select EcomProductProfile");
			return;
		}
		$
				.ajax({
					url : ecomProductProfileContextPath
							+ "/activateEcomProductProfile",
					type : "POST",
					data : {
						ecomproductprofiles : selectedEcomProductProfile,
					},
					success : function(status) {
						$("#enableEcomProductProfileModal").modal("hide");
						onSaveSuccess(status);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	EcomProductProfile.setActive = function(name, ecomPid, isActivated) {
		ecomProductProfileModel.pid = ecomPid;
		ecomProductProfileModel.activated = isActivated;
		ecomProductProfileModel.name = name;
		if (confirm("Are you sure?")) {
			// update status;changeStatus
			$.ajax({
				url : ecomProductProfileContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(ecomProductProfileModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	function createUpdateEcomProductProfile(el) {
		ecomProductProfileModel.name = $('#field_name').val();
		ecomProductProfileModel.alias = $('#field_alias').val();
		ecomProductProfileModel.description = $('#field_description').val();
		ecomProductProfileModel.offerString = $('#field_offerString').val();
		var ecomDisplayAttributes = "";
		$.each($("input[name='ecomDisplayAttribute']:checked"), function() {
			ecomDisplayAttributes += $(this).val() + ",";
		});
		ecomProductProfileModel.ecomDisplayAttributes = ecomDisplayAttributes;
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(ecomProductProfileModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showEcomProductProfile(id) {

		console.log(ecomProductProfileContextPath + "/" + id);
		$.ajax({
			url : ecomProductProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_description').text((data.description == null ? "" : data.description));
				$('#lbl_ecomDisplayAttributes')
						.text(data.ecomDisplayAttributes);
				$('#lbl_offerString').text((data.offerString == null ? "" : data.offerString));
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

	function editEcomProductProfile(id) {
		$.ajax({
			url : ecomProductProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_description').val((data.description == null ? "" : data.description));
				$('#field_offerString').val((data.offerString == null ? "" : data.offerString));
				$('#previewImage').attr('src',
						'data:image/png;base64,' + data.image);

				if (data.ecomDisplayAttributes != null
						&& data.ecomDisplayAttributes != "") {
					var array = data.ecomDisplayAttributes.split(',');
					$.each(array, function(index, ecomDisplayAttribute) {
						$(
								"#ecomDisplayAttributesCheckboxes input:checkbox[value="
										+ ecomDisplayAttribute + "]").prop(
								"checked", true);
					});
				}
				// set pid
				ecomProductProfileModel.pid = data.pid;
				// set image byte array
				ecomProductProfileModel.image = data.image;
				ecomProductProfileModel.imageContentType = data.imageContentType;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteEcomProductProfile(actionurl, id) {
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

	function loadproducts(pid) {
		// clear all check box
		$("#productsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : ecomProductProfileContextPath + "/findProducts/" + pid,
			type : "GET",
			success : function(data) {
				ecomProductProfileModel.pid = pid;
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
			url : ecomProductProfileContextPath + "/assignProducts",
			type : "POST",
			data : {
				pid : ecomProductProfileModel.pid,
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = ecomProductProfileContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = ecomProductProfileContextPath;
	}

	EcomProductProfile.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showEcomProductProfile(id);
				break;
			case 1:
				editEcomProductProfile(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', ecomProductProfileContextPath + "/"
						+ id);
				break;
			case 3:
				loadproducts(id);
				break;

			}
		}
		el.modal('show');
	}

	EcomProductProfile.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		ecomProductProfileModel.pid = null; // reset ecomProductProfile model;
		ecomProductProfileModel.image = null;
		ecomProductProfileModel.imageContentType = null;
		$('#previewImage').attr('src', '');
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