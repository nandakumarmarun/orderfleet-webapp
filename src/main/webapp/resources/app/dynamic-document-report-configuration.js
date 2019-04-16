// Create a DynamicDocumentReportConfiguration object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DynamicDocumentReportConfiguration) {
	this.DynamicDocumentReportConfiguration = {};
}

(function() {
	'use strict';
	var dynamicDocumentReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#dynamicDocumentReportConfigurationsForm");
	var deleteForm = $("#deleteForm");
	var dynamicDocumentSettingsHeaderPid = "";

	var dynamicDocumentReportSettingModel = {
		pid : null,
		name : null,
		title : null,
		documentPid : null,
		documentName : null,
		documentSettingsColumnsDTOs : [],
		documentSettingsRowColourDTOs : [],
		dynamicDocumentReportDetailDTOs : []
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		title : {
			required : true,
			maxlength : 55
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		title : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		}
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateDynamicDocumentReportSetting(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDynamicDocumentReportSetting(e.currentTarget.action);
		});

		$('#btnRight').click(function(e) {
			$('select').moveToListAndDelete('#lstBox1', '#lstBox2');
			e.preventDefault();
		});

		$('#btnAllRight').click(function(e) {
			$('select').moveAllToListAndDelete('#lstBox1', '#lstBox2');
			e.preventDefault();
		});

		$('#btnLeft').click(function(e) {
			$('select').moveToListAndDelete('#lstBox2', '#lstBox1');
			e.preventDefault();
		});

		$('#btnAllLeft').click(function(e) {
			$('select').moveAllToListAndDelete('#lstBox2', '#lstBox1');
			e.preventDefault();
		});

		$('#btnAvengerUp').click(function(e) {
			$('select').moveUpDown('#lstBox2', true, false);
			e.preventDefault();
		});

		$('#btnAvengerDown').click(function(e) {
			$('select').moveUpDown('#lstBox2', false, true);
			e.preventDefault();
		});

		$('#dbDocuments').on('change', function() {
			getFormElementsByDocument();
		});

	});

	function getFormElementsByDocument() {
		var documentPid = $('#dbDocuments').val();

		if (documentPid == "-1") {
			alert("please select document");
			return;
		}
		$.ajax({
			url : dynamicDocumentReportContextPath + "/loadFormElements",
			method : 'GET',
			async : false,
			data : {
				documentPid : documentPid,
			},
			success : function(data) {
				$('#lstBox1').empty();
				if (data) {
					var selectBox = "";
					$.each(data, function(index, ff) {
						selectBox += "<option  value=" + ff.pid + ">" + ff.name
								+ "</option>";
					});
					$('#lstBox1').append(selectBox);
					$('#chk_filledForm').attr("checked", "checked");
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function editDynamicDocumentReportConfiguration(id) {
		$.ajax({
			url : dynamicDocumentReportContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_title').val((data.title == null ? "" : data.title));
				if (data.documentPid != null && data.documentPid != "-1") {
					$('#dbDocuments').val(data.documentPid);
					$('#dbDocuments').attr("disabled", true);
				}
				// set pid
				dynamicDocumentReportSettingModel.pid = data.pid;
				getFormElementsByDocument();

				$('#lstBox2').empty();
				if (data.dynamicDocumentReportDetailDTOs) {
					var selectBox = "";
					$.each(data.dynamicDocumentReportDetailDTOs, function(
							index, ff) {
						selectBox += "<option  value=" + ff.formElementPid
								+ ">" + ff.formElementName + "</option>";
						$("#lstBox1 option[value='" + ff.formElementPid + "']")
								.remove();
					});
					$('#lstBox2').append(selectBox);
					$('#chk_filledForm').attr("checked", "checked");
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function createUpdateDynamicDocumentReportSetting(el) {

		var docPid = $('#dbDocuments').val();
		var name = $('#field_name').val();
		var title = $('#field_title').val();
		var DynamicDocumentSettingsColumnsDTOs = [];
		var DynamicDocumentSettingsRowColourDTOs = [];
		var DynamicDocumentReportDetailDTOs = [];
		if (docPid == "-1") {
			$('#alertMessage').html("please select document");
			$('#alertBox').modal("show");
			return;
		}
		if (name == "") {
			$('#alertMessage').html("please enter name");
			$('#alertBox').modal("show");
			return;
		}
		if (title == "") {
			$('#alertMessage').html("please enter title");
			$('#alertBox').modal("show");
			return;
		}

		var sortOrder = 0;
		$.each($(".country option"), function() {
			sortOrder = sortOrder + 1;
			var Pid = $(this).val();
			DynamicDocumentReportDetailDTOs.push({
				formElementPid : Pid,
				sortOrder : sortOrder,
				tableName : "FORM_ELEMENT",
				columnName : "test",
				displayLabel : "test"
			});
		});

		dynamicDocumentReportSettingModel.name = name;
		dynamicDocumentReportSettingModel.title = title;
		dynamicDocumentReportSettingModel.documentPid = docPid;
		dynamicDocumentReportSettingModel.documentName = $(
				'#dbDocuments option:selected').text();
		dynamicDocumentReportSettingModel.documentSettingsColumnsDTOs = DynamicDocumentSettingsColumnsDTOs;
		dynamicDocumentReportSettingModel.documentSettingsRowColourDTOs = DynamicDocumentSettingsRowColourDTOs;
		dynamicDocumentReportSettingModel.dynamicDocumentReportDetailDTOs = DynamicDocumentReportDetailDTOs;
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(dynamicDocumentReportSettingModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	DynamicDocumentReportConfiguration.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDynamicDocumentReportConfiguration(id);
				break;
			case 1:
				editDynamicDocumentReportConfiguration(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dynamicDocumentReportContextPath
						+ "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	function deleteDynamicDocumentReportSetting(actionurl, id) {
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
		window.location = location.protocol + '//' + location.host
				+ location.pathname;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = dynamicDocumentReportContextPath;
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		dynamicDocumentReportSettingModel.pid = null; // reset bank model;

		$("#tb_FormElementValues").html("");
		$("#tb_ElementValues").html("");
		$("#dbSelect").hide();

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
