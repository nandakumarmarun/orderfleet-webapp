// Create a MobileMenuItemGroup object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.MobileMenuItemGroup) {
	this.MobileMenuItemGroup = {};
}

(function() {
	'use strict';

	var mobileMenuItemGroupContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#mobileMenuItemGroupForm");
	var deleteForm = $("#deleteForm");
	var mobileMenuItemGroupModel = {
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
		},
		description : {
			maxlength : "This field cannot be longer than 250 characters."
		}
	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateMobileMenuItemGroup(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteMobileMenuItemGroup(e.currentTarget.action);
				});
				$('#btnSaveMenuItems').on('click', function() {
					saveAssignedMenuItems();
				});

				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
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
		var table = $('#tblMenuItems');
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

	function loadMenuItems(pid) {

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		$("#menuItemsCheckboxes input:checkbox").attr('checked', false);
		mobileMenuItemGroupModel.pid = pid;
		// clear all check box
		$.ajax({
			url : mobileMenuItemGroupContextPath + "/menuItems/" + pid,
			type : "GET",
			success : function(menuItems) {
				if (menuItems) {
					$.each(menuItems, function(index, menuItem) {
						$("#menuItemsCheckboxes input:checkbox[value="+ menuItem.id + "]").prop("checked", true);
						$("#txtLabel" + menuItem.id).val(menuItem.label);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedMenuItems() {
		$(".error-msg").html("");
		var menuItems = [];
		var status = true;
		$.each($("input[name='mobileMenuItem']:checked"), function() {
			var menuItemId = $(this).val();
			var label = $("#txtLabel" + menuItemId).val();
			if (label == "") {
				$(".error-msg").html("Enter valid label");
				status = false;
				return;
			}
			menuItems.push({
				id : menuItemId,
				label : label
			})
		});
		if (!status) {
			return;
		}
		if (menuItems.length == 0) {
			$(".error-msg").html("Please select Menu Item");
			return;
		}
		$.ajax({
			url : mobileMenuItemGroupContextPath + "/menuItems/"+mobileMenuItemGroupModel.pid ,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(menuItems),
			success : function(status) {
				$("#menuItemsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function createUpdateMobileMenuItemGroup(el) {
		mobileMenuItemGroupModel.name = $('#field_name').val();
		mobileMenuItemGroupModel.alias = $('#field_alias').val();
		mobileMenuItemGroupModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(mobileMenuItemGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showMobileMenuItemGroup(id) {
		$.ajax({
			url : mobileMenuItemGroupContextPath + "/" + id,
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

	function editMobileMenuItemGroup(id) {
		$.ajax({
			url : mobileMenuItemGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				// set pid
				mobileMenuItemGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteMobileMenuItemGroup(actionurl, id) {
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
		window.location = mobileMenuItemGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = mobileMenuItemGroupContextPath;
	}

	MobileMenuItemGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showMobileMenuItemGroup(id);
				break;
			case 1:
				editMobileMenuItemGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', mobileMenuItemGroupContextPath + "/"
						+ id);
				break;
			case 3:
				loadMenuItems(id);
				break;
			}
		}
		el.modal('show');
	}

	MobileMenuItemGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		mobileMenuItemGroupModel.pid = null; // reset mobileMenuItemGroup
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