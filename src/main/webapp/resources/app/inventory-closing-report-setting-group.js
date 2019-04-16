if (!this.InventoryClosingReportSettingGroup) {
	this.InventoryClosingReportSettingGroup = {};
}

(function() {
	'use strict';

	var inventoryClosingReportSettingGroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#inventoryClosingReportSettingGroupForm");
	var deleteForm = $("#deleteForm");
	var inventoryClosingReportSettingGroupModel = {
		pid : null,
		name : null,
		alias : null,
		sortOrder : null,
		flow : null,
//		description : null
		
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
		flow : {
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
		flow : {
			valueNotEquals : "This field is required."
		}
	};

	$(document).ready(function() {
		$.validator.addMethod("valueNotEquals", function(value,
				element, arg) {
			return arg != value;
		}, "");
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateInventoryClosingReportSettingGroup(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteInventoryClosingReportSettingGroup(e.currentTarget.action);
		});
		
		$('#btnActivateInventoryClosingReportSettingGroup').on('click', function() {
			activateAssignedInventoryClosingReportSettingGroup();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activateAssignedInventoryClosingReportSettingGroup() {
		$(".error-msg").html("");
		var selectedInventoryClosingReportSettingGroup = "";

		$.each($("input[name='inventoryClosingReportSettingGroup']:checked"), function() {
			selectedInventoryClosingReportSettingGroup += $(this).val() + ",";
		});

		if (selectedInventoryClosingReportSettingGroup == "") {
			$(".error-msg").html("Please select InventoryClosingReportSettingGroup");
			return;
		}
		$.ajax({
			url : inventoryClosingReportSettingGroupContextPath + "/activateInventoryClosingReportSettingGroup",
			type : "POST",
			data : {
				inventoryClosingReportSettingGroups : selectedInventoryClosingReportSettingGroup,
			},
			success : function(status) {
				$("#enableInventoryClosingReportSettingGroupModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateInventoryClosingReportSettingGroup(el) {
		inventoryClosingReportSettingGroupModel.name = $('#field_name').val();
		inventoryClosingReportSettingGroupModel.alias = $('#field_alias').val();
//		inventoryClosingReportSettingGroupModel.description = $('#field_description').val();
		inventoryClosingReportSettingGroupModel.flow = $('#field_flow').val();
		inventoryClosingReportSettingGroupModel.sortOrder = $('#field_sortorder').val();
		console.log(inventoryClosingReportSettingGroupModel);
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(inventoryClosingReportSettingGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showInventoryClosingReportSettingGroup(id) {
		$.ajax({
			url : inventoryClosingReportSettingGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
//				$('#lbl_description').text((data.description == null ? "" : data.description));
				$('#lbl_flow').text(data.flow);
				$('#lbl_sortorder').text(data.sortOrder);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editInventoryClosingReportSettingGroup(id) {
		$.ajax({
			url : inventoryClosingReportSettingGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
//				$('#field_description').val((data.description == null ? "" : data.description));
				$('#field_sortorder').val(data.sortOrder);
				$('#field_flow').val(data.flow);
				// set pid
				inventoryClosingReportSettingGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteInventoryClosingReportSettingGroup(actionurl, id) {
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

	InventoryClosingReportSettingGroup.setActive=function(name,pid,active){
		inventoryClosingReportSettingGroupModel.pid=pid;
		inventoryClosingReportSettingGroupModel.activated=active;
		inventoryClosingReportSettingGroupModel.name=name;
		if(confirm("Are you confirm?")){
			$.ajax({
				url:inventoryClosingReportSettingGroupContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(inventoryClosingReportSettingGroupModel),
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
		window.location = inventoryClosingReportSettingGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = inventoryClosingReportSettingGroupContextPath;
	}

	InventoryClosingReportSettingGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showInventoryClosingReportSettingGroup(id);
				break;
			case 1:
				editInventoryClosingReportSettingGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', inventoryClosingReportSettingGroupContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	InventoryClosingReportSettingGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		inventoryClosingReportSettingGroupModel.pid = null; // reset inventoryClosingReportSettingGroup model;
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