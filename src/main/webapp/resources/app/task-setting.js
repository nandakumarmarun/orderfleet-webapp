// Create a TaskSetting object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.TaskSetting) {
	this.TaskSetting = {};
}

(function() {
	'use strict';

	var taskSettingContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#taskSettingForm");
	var deleteForm = $("#deleteForm");
	var taskSettingModel = {
		pid : null,
		activityPid : null,
		documentPid : null,
		activityEvent : null,
		taskActivityPid : null,
		formElementPid : null,
		startDateColumn : null,
		script : null,
		required : false,
		createPlan : false
	};

	// Specify the validation rules
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
		taskActivityPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
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
		taskActivityPid : {
			valueNotEquals : "This field is required.",
		}
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateTaskSetting(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteTaskSetting(e.currentTarget.action);
		});
	});

	function createUpdateTaskSetting(el) {

		taskSettingModel.activityPid = $('#field_activity').val();
		taskSettingModel.documentPid = $('#field_document').val();
		taskSettingModel.activityEvent = $('#field_activityEvent').val();
		taskSettingModel.taskActivityPid = $('#field_taskActivity').val();
		taskSettingModel.script = $('#field_script').val();
		taskSettingModel.required = $('#field_required').prop('checked');
		taskSettingModel.createPlan = $('#field_createPlan').prop('checked');

		if ($('#field_startDateColumn').val() != "-1") {
			taskSettingModel.formElementPid = $('#field_startDateColumn').val();
			var selected = $("#field_startDateColumn option:selected").text();
			taskSettingModel.startDateColumn = selected;
		} else {
			taskSettingModel.formElementPid = null;
			taskSettingModel.startDateColumn = null;
		}

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(taskSettingModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showTaskSetting(id) {
		$.ajax({
			url : taskSettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_activity').text(data.activityName);
				$('#lbl_document').text(data.documentName);
				$('#lbl_activityEvent').text(data.activityEvent);
				$('#lbl_taskActivity').text(data.taskActivityName);
				$('#lbl_required').text(data.required);
				$('#lbl_createPlan').text(data.createPlan);
				$('#lbl_script').text(data.script);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editTaskSetting(id) {
		$.ajax({
			url : taskSettingContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_activity').val(data.activityPid);
				TaskSetting.loadDocuments();
				$('#field_document').val(data.documentPid);
				$('#field_activityEvent').val(data.activityEvent);
				$('#field_taskActivity').val(data.taskActivityPid);
				$('#field_script').val(data.script);
				$('#field_required').prop('checked', data.required);
				$('#field_createPlan').prop('checked', data.createPlan);
				TaskSetting.onChangeDocument();
				if (data.formElementPid != null) {
					$('#field_startDateColumn').val(data.formElementPid);
				}
				// set pid
				taskSettingModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteTaskSetting(actionurl, id) {
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

	TaskSetting.loadDocuments = function() {
		$('#field_document')
				.html("<option value='-1'>Select Document</option>");
		var pid = $('#field_activity').val();
		if (pid == '-1') {
			return;
		}
		$('#field_document').html(
				"<option value='no'>Document loading...</option>");
		$.ajax({
			url : taskSettingContextPath + "/load-documents",
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

	TaskSetting.onChangeDocument = function() {
		var documentType = $("#field_document option:selected").attr('class');
		$('#field_startDateColumn').html(
				"<option value='-1'>Select Start Date Column</option>");
		if (documentType == "DYNAMIC_DOCUMENT") {

			$('#divStartDateColumn').show();
			var pid = $('#field_document').val();
			if (pid == '-1') {
				return;
			}
			$('#field_startDateColumn')
					.html(
							"<option value='no'>Start Date Columns loading...</option>");
			$
					.ajax({
						url : taskSettingContextPath
								+ "/load-start-date-columns",
						async : false,
						method : 'GET',
						data : {
							pid : pid
						},
						success : function(startDateColumns) {
							$('#field_startDateColumn')
									.html(
											"<option value='-1'>Select Start Date Column</option>");
							$
									.each(
											startDateColumns,
											function(index, column) {
												$('#field_startDateColumn')
														.append(
																"<option  value='"
																		+ column.formElementPid
																		+ "'>"
																		+ column.formElementName
																		+ "- "
																		+ column.formName
																		+ "</option>");
											});
						},
						error : function(xhr, error) {
							onError(xhr, error);
						}
					});

		} else {
			$('#divStartDateColumn').hide();
		}
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = taskSettingContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = taskSettingContextPath;
	}

	TaskSetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showTaskSetting(id);
				break;
			case 1:
				editTaskSetting(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', taskSettingContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	TaskSetting.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		taskSettingModel.pid = null; // reset taskSetting model;
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