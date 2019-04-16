if (!this.TaskNotificationSetting) {
	this.TaskNotificationSetting = {};
}

(function() {
	'use strict';

	var taskNotificationSettingContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#taskNotificationSettingForm");
	var deleteForm = $("#deleteForm");

	var taskNotificationSettingModel = {
		pid : null,
		activityPid : null,
		documentPid : null,
		activityEvent : null,
		notificationSettingColumns : [],
	};
	var validationRules = {
		activityPid : {
			valueNotEquals : "-1"
		},
		documentPid : {
			valueNotEquals : "-1"
		},
		activityEvent : {
			valueNotEquals : "-1"
		},
	};

	var validationMessages = {
		activityPid : {
			valueNotEquals : "This field is required.",
		},
		documentPid : {
			valueNotEquals : "This field is required.",
		},
		activityEvent : {
			valueNotEquals : "This field is required.",
		},
	};

	$(document).ready(function() {
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$("#checkboxDocument").css("display", "none");
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateTaskNotificationSetting(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteTaskNotificationSetting(e.currentTarget.action);
		});
	});

	function createUpdateTaskNotificationSetting(el) {
		taskNotificationSettingModel.activityPid = $('#field_activity').val();
		taskNotificationSettingModel.documentPid = $('#field_document').val();
		taskNotificationSettingModel.activityEvent = $('#field_activityEvent')
				.val();
//		$.each($("#checkboxDocument input[type='checkbox']:checked"),
//				function() {
//					var column = {
//						columnName : null,
//						columnLabel : null,
//					};
//					column.columnName = $(this).val();
//					// var result = $(this).val().split(' ').join('');
//					column.columnLabel = $(this).nextAll("input[type='text']")
//							.first().val();
//					if (column.columnName == "" || column.columnName == null
//							|| column.columnLabel == ""
//							|| column.columnLabel == null) {
//						$(".error-msg").html("Please select Checkbox");
//						return;
//					} else {
//						taskNotificationSettingModel.notificationSettingColumns
//								.push(column);
//					}
//				});
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(taskNotificationSettingModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showTaskNotificationSetting(id) {
		$.ajax({
			url : taskNotificationSettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data);
				$('#lbl_activity').text(data.activityName);
				$('#lbl_document').text(data.documentName);
				$('#lbl_activityEvent').text(data.activityEvent);
//				var columnName="";
//				$.each(data.notificationSettingColumns,
//						function(index, value) {
//					columnName+=value.columnName+"<br>";
//						});
//				$('#lbl_columnLabel').html(columnName);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editTaskNotificationSetting(id) {
		$.ajax({
			url : taskNotificationSettingContextPath + "/" + id,
			method : 'GET',
			async : false,
			success : function(data) {
				$('#field_activity').val(data.activityPid);
				TaskNotificationSetting.loadDocuments();
				$('#field_document').val(data.documentPid);
				$('#field_activityEvent').val(data.activityEvent);
//				TaskNotificationSetting.onChangeDocument();
//				$.each(data.notificationSettingColumns,
//						function(index, value) {
//							var elm = $('input:checkbox[name="'
//									+ escapeSpecialCharacters(value.columnName)
//									+ '"]');
//							elm.attr('checked', true);
//							$(elm).nextAll("input[type='text']").first().val(
//									value.columnLabel)
//						});
				// set pid
				taskNotificationSettingModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteTaskNotificationSetting(actionurl, id) {
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

	TaskNotificationSetting.loadDocuments = function() {
		$("#checkboxDocument").css("display", "none");
		$('#field_document')
				.html("<option value='-1'>Select Document</option>");
		var pid = $('#field_activity').val();
		if (pid == '-1') {
			return;
		}
		$('#field_document').html(
				"<option value='no'>Document loading...</option>");
		$.ajax({
			url : taskNotificationSettingContextPath + "/load-documents",
			async : false,
			method : 'GET',
			data : {
				pid : pid
			},
			success : function(documents) {
				$('#field_document').html(
						"<option value='-1'>Select Document</option>");
				$.each(documents, function(index, document) {
					$('#field_document').append(
							"<option class='" + document.documentType
									+ "' value='" + document.pid + "'>"
									+ document.name + "</option>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

//	TaskNotificationSetting.onChangeDocument = function() {
//		$("#checkboxDocument").css("display", "none");
//		var documentType = $("#field_document option:selected").attr('class');
//		var checkBoxes = "";
//		$("#checkboxDocument").html("");
//		if (documentType == "DYNAMIC_DOCUMENT") {
//
//			var pid = $('#field_document').val();
//			if (pid == '-1') {
//				return;
//			}
//			$
//					.ajax({
//						url : taskNotificationSettingContextPath
//								+ "/load-dynamic-documents",
//						async : false,
//						method : 'GET',
//						data : {
//							pid : pid
//						},
//						success : function(formElements) {
//
//							$
//									.each(
//											formElements,
//											function(index, column) {
//												var result = column.name.split(
//														' ').join('');
//												checkBoxes += '<input type="checkbox" style="margin-left: 2%;" name="'
//														+ escapeSpecialCharacters(column.name)
//														+ '" id="'
//														+ column.name
//														+ '" value="'
//														+ column.name
//														+ '"/>'
//														+ '<label class="control-label" for="field_dynamic_document">'
//														+ column.name
//														+ '</label>&nbsp;'
//														+ '<input type="text" style=" width: 40%;" name="'
//														+ result
//														+ '" id="field_text'
//														+ result
//														+ '" value="'
//														+ column.name
//														+ '" /><br>';
//											});
//							$("#checkboxDocument").html(checkBoxes);
//							$("#checkboxDocument").css("display", "block");
//						},
//						error : function(xhr, error) {
//							onError(xhr, error);
//						}
//					});
//
//		} else if (documentType == "INVENTORY_VOUCHER") {
//			var pid = $('#field_document').val();
//			if (pid == '-1') {
//				return;
//			}
//			$
//					.ajax({
//						url : taskNotificationSettingContextPath
//								+ "/load-inventory-vouchers",
//						async : false,
//						method : 'GET',
//						data : {
//							pid : pid
//						},
//						success : function(inventoryVoucherColumn) {
//							$
//									.each(
//											inventoryVoucherColumn,
//											function(index, column) {
//												var result = column.inventoryVoucherColumn
//														.split(' ').join('');
//												checkBoxes += '<input type="checkbox" style="margin-left: 2%;" name="'
//														+ escapeSpecialCharacters(column.inventoryVoucherColumn)
//														+ '" id="'
//														+ column.inventoryVoucherColumn
//														+ '" value="'
//														+ column.inventoryVoucherColumn
//														+ '"/>'
//														+ '<label class="control-label" for="field_inventory-vouchers">'
//														+ column.inventoryVoucherColumn
//														+ '</label>&nbsp;'
//														+ '<input type="text" style=" width: 40%;" name="'
//														+ result
//														+ '" id="field_text'
//														+ result
//														+ '" value="'
//														+ column.inventoryVoucherColumn
//														+ '" /><br>';
//											});
//							$("#checkboxDocument").html(checkBoxes);
//							$("#checkboxDocument").css("display", "block");
//						},
//						error : function(xhr, error) {
//							onError(xhr, error);
//						}
//					});
//
//		} else if (documentType == "ACCOUNTING_VOUCHER") {
//			var pid = $('#field_document').val();
//			if (pid == '-1') {
//				return;
//			}
//			$
//					.ajax({
//						url : taskNotificationSettingContextPath
//								+ "/load-accounting-vouchers",
//						async : false,
//						method : 'GET',
//						data : {
//							pid : pid
//						},
//						success : function(accountingVoucherColumn) {
//							$
//									.each(
//											accountingVoucherColumn,
//											function(index, column) {
//												var result = column.accountingVoucherColumn
//														.split(' ').join('');
//												checkBoxes += '<input type="checkbox" style="margin-left: 2%;" name="'
//														+ escapeSpecialCharacters(column.accountingVoucherColumn)
//														+ '" id="'
//														+ column.accountingVoucherColumn
//														+ '" value="'
//														+ column.accountingVoucherColumn
//														+ '"/>'
//														+ '<label class="control-label" for="field_accounting-vouchers">'
//														+ column.accountingVoucherColumn
//														+ '</label>&nbsp;'
//														+ '<input type="text"style=" width: 40%;" name="'
//														+ result
//														+ '" id="field_text'
//														+ result
//														+ '" value="'
//														+ column.accountingVoucherColumn
//														+ '" /><br>';
//											});
//							$("#checkboxDocument").html(checkBoxes);
//							$("#checkboxDocument").css("display", "block");
//						},
//						error : function(xhr, error) {
//							onError(xhr, error);
//						}
//					});
//
//		}
//
//	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = taskNotificationSettingContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = taskNotificationSettingContextPath;
	}
	TaskNotificationSetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showTaskNotificationSetting(id);
				break;
			case 1:
				editTaskNotificationSetting(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', taskNotificationSettingContextPath
						+ "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	TaskNotificationSetting.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		taskNotificationSettingModel.pid = null;
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

	function escapeSpecialCharacters(name) {
		//if forward slash
		name = name.replace(/[^a-zA-Z0-9_-]/g, '');
		return name;

	}
})();