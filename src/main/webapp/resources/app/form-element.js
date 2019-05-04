// Create a FormElement object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.FormElement) {
	this.FormElement = {};
}
(function() {
	'use strict';
	var contextPath = location.protocol + '//' + location.host;
	var createEditForm = $("#formElementForm");
	var deleteForm = $("#deleteForm");
	var formElementModel = {
		pid : null,
		name : null,
		formElementTypeId : null,
		formElementValues : [],
		formLoadFromMobile : false,
		formLoadMobileData : null

	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 1000
		},
		formElementTypeId : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 1000 characters."
		},
		formElementTypeId : {
			valueNotEquals : "This field is required."
		}
	};

	$(document).ready(
			function() {

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateFormElement(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteFormElement(e.currentTarget.action);
				});

				$('#btnAddOption').on('click', function() {
					addToOption();
				});

				$('#field_type').on('change', function() {
					onChangeType();
				});

				$('#btnShowMasterData').on('click', function() {
					showSelectedMasterTableData();
				});

				$('#btnAddMasterOptions').on('click', function() {
					addMasterOptions();
				});

				$('#dbMasterType').on('change', function() {
					onChangeMasterType();
				});

				$('#btnActivateFormElement').on('click', function() {
					activateAssignedFormElement();
				});
				
				$('#btnDefaultValue').on('click', function() {
					saveDefaultValue();
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
				$('#divLoadFromMobileData').hide();
				$('#divLoadMobileData').hide();
				
			});

	
	var textboxIsActive =false;
	FormElement.setDefaultValue = function(pid, defaultValue) {
		$('.alert').hide();
		$('#divTxtDefaultValue').hide();
		$('#divDbDefaultValue').hide();
		
		// set pid
		formElementModel.pid = pid;
		$.ajax({
			url : contextPath + "/web/formElements/" + pid,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				if (data.formElementTypeName == "dropdown" || data.formElementTypeName == "checkBox" || data.formElementTypeName == "radioButton") {
					$('#dbDefaultValue').html('<option value="no">Select Default Value</option>');
					$.each(data.formElementValues,	function(index, formElementValue) {
						$('#dbDefaultValue').append('<option value="'+formElementValue.name+'">'+formElementValue.name+'</option>');
					});
					if(defaultValue != ''){
						$('#dbDefaultValue').val(defaultValue);
					}
					$('#divDbDefaultValue').show();
					textboxIsActive  = false ;
				} else {
					$('#txtDefaultValue').val(defaultValue);
					$('#divTxtDefaultValue').show();
					textboxIsActive  = true ;
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		$('#defaultValueModal').modal('show');
	}
	
	function saveDefaultValue() {
		var value = "";
		if(textboxIsActive){
			value = $('#txtDefaultValue').val();
		}else{
			value =  $('#dbDefaultValue').val();
		}
		if(value == "no" || value == ""){
			return;
		}
		$.ajax({
			url : contextPath + "/web/formElements/setDefaultValue",
			type : "POST",
			data : {
				pid : formElementModel.pid,
				defaultValue : value
			},
			success : function(status) {
				$("#defaultValueModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function activateAssignedFormElement() {
		$(".error-msg").html("");
		var selectedFormElement = "";

		$.each($("input[name='formelement']:checked"), function() {
			selectedFormElement += $(this).val() + ",";
		});

		if (selectedFormElement == "") {
			$(".error-msg").html("Please select FormElement");
			return;
		}
		$.ajax({
			url : contextPath + "/web/formElements/" + "/activateFormElement",
			type : "POST",
			data : {
				formelements : selectedFormElement,
			},
			success : function(status) {
				$("#enableBankModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function createUpdateFormElement(el) {
		formElementModel.name = $('#field_name').val();
		formElementModel.formElementTypeId = $('#field_type').val();
		formElementModel.formElementValues = [];
		formElementModel.formLoadFromMobile=$("#loadFromMobile").is(":checked");
		formElementModel.formLoadMobileData= $('#loadMobileData').val();
		console.log(formElementModel);
		$('#tblOptions tr td:first-child').each(function() {
			var optionObj = {};
			optionObj["id"] = null;
			optionObj["name"] = $(this).text().trim();
			formElementModel.formElementValues.push(optionObj);
		});
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(formElementModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showFormElement(id) {
		$
				.ajax({
					url : contextPath + "/web/formElements/" + id,
					method : 'GET',
					success : function(data) {
						$('#lbl_name').text(data.name);
						$('#lbl_type').text(data.formElementTypeName);
						$('#tb_Questions').html("");
						var tRow = "";
						$
								.each(
										data.formElementValues,
										function(index, formElementValue) {
											tRow += ("<tr><td>"
													+ formElementValue.name + "</td></tr>");
										});
						if (tRow != "") {
							var table = "<thead><tr><th>Options</th></tr></thead><tbody id='tblViewOptions'>"
									+ tRow + "</tbody>";
							$('#tb_Questions').append(table);
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function editFormElement(id) {
		$.ajax({
			url : contextPath + "/web/formElements/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_type').val(data.formElementTypeId);
				$('#loadFromMobile').prop("checked",data.formLoadFromMobile);
				$('#loadMobileData').val(data.formLoadMobileData);
				if (data.formElementTypeName == "dropdown"
						|| data.formElementTypeName == "checkBox") {
					$('#divMasterTable').show();
					$('#divLoadFromMobileData').show();
				} else {
					$('#divMasterTable').hide();
					$('#divLoadFromMobileData').hide();
					$('#divLoadMobileData').hide();
				}
				
				if (data.formLoadFromMobile==true) {
			    	$('#divLoadMobileData').show();
			    	$('#divMasterTable').hide();
			    }else{
			    	$('#divLoadMobileData').hide();
			    	$('#divMasterTable').show();
			    }
				
				createOptionsTableView(data.formElementValues);
				// set pid
				formElementModel.pid = data.pid;
				formElementModel.formElementValues = data.formElementValues;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteFormElement(actionurl, id) {
		console.log("deleteFormElement............");
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

	function addToOption() {
		var option = $('#field_option').val();
		if (option == "") {
			return;
		}
		var optionObj = {};
		optionObj["id"] = null;
		optionObj["name"] = option;

		remove(formElementModel.formElementValues, option);
		formElementModel.formElementValues.push(optionObj);
		createOptionsTableView(formElementModel.formElementValues);
		$('#field_option').val("");
		console.log(formElementModel.formElementValues);
	}

	function addMasterOptions() {
		$.each($("input[name='masterOption']:checked"), function() {
			var option = $(this).val();
			console.log(option);
			var optionObj = {};
			optionObj["id"] = null;
			optionObj["name"] = option;

			remove(formElementModel.formElementValues, option);
			formElementModel.formElementValues.push(optionObj);
			createOptionsTableView(formElementModel.formElementValues);
		});
		$('#masterOptionsModal').modal('hide');
	}

	function createOptionsTableView(options) {
		$('#tblOptions').html("");
		$
				.each(
						options,
						function(index, option) {
							$('#tblOptions')
									.append(
											"<tr><td>"
													+ option.name
													+ "</td><td><button type='button' onclick='FormElement.removeOption(this,\""
													+ option.name
													+ "\")'>&times;</button></td></tr>");
						});
	}

	FormElement.removeOption = function(obj, option) {
		remove(formElementModel.formElementValues, option);
		$(obj).closest('tr').remove();
		console.log("after");
		console.log(formElementModel.formElementValues);
	}

	function onChangeType() {
		var type = $('#field_type option:selected').text()
		if (type == "dropdown" || type == "checkBox") {
			$('#divMasterTable').show();
			$('#divLoadFromMobileData').show();
		} else {
			$('#divMasterTable').hide();
			$('#divLoadFromMobileData').hide();
			$('#divLoadMobileData').hide();
		}
	}
	
	$('#loadFromMobile').change(function () {
	    if ($(this).prop("checked")) {
	    	$('#divLoadMobileData').show();
	    	$('#divMasterTable').hide();
	    }else{
	    	$('#divLoadMobileData').hide();
	    	$('#divMasterTable').show();
	    }
	});

	function showSelectedMasterTableData() {
		var selectedMaster = $('#field_master').val()
		if (selectedMaster == "-1") {
			console.log("Please select table");
			return;
		}
		$('#tblMasterOptions').html("");

		if (selectedMaster != "OTHER") {
			$('#divMasterType').hide();
		} else {
			$('#divMasterType').show();
			$('#dbMasterType').html(
					'<option value="-1">Select Master Type</option>');
		}
		$.ajax({
			url : contextPath + "/web/formElements/master-table-data/"
					+ selectedMaster,
			method : 'GET',
			success : function(options) {
				$.each(options, function(index, option) {
					if (selectedMaster != "OTHER") {
						$('#tblMasterOptions').append(
								"<tr><td><input name='masterOption' type='checkbox' value='"
										+ option + "' /></td><td>" + option
										+ "</td></tr>");
					} else {
						var type = option.split("~");
						$('#dbMasterType').append(
								'<option value="' + type[0] + '">' + type[1]
										+ '</option>');
					}
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		$('#masterOptionsModal').modal('show');
	}

	function onChangeMasterType() {
		console.log("onChangeMasterType");
		$('#tblMasterOptions').html("");
		if ($('#dbMasterType').val() == "-1") {
			return;
		}
		$.ajax({
			url : contextPath + "/web/formElements/form-element-masters/"
					+ $('#dbMasterType').val(),
			method : 'GET',
			success : function(options) {
				$.each(options, function(index, option) {
					$('#tblMasterOptions').append(
							"<tr><td><input name='masterOption' type='checkbox' value='"
									+ option + "' /></td><td>" + option
									+ "</td></tr>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function remove(array, option) {
		for (var i = 0; i < array.length; i++) {
			var value = array[i];
			if (value.name == option) {
				array.splice(i, 1);
				break;
			}
		}
	}

	FormElement.setActive = function(name, pid, active) {
		formElementModel.name = name;
		formElementModel.pid = pid;
		formElementModel.activated = active;

		if (confirm("Are you confirm?")) {
			$.ajax({
				url : contextPath + "/web/formElements/" + "changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(formElementModel),
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
		if (typeof (result.formPid) !== 'undefined') {
			window.location = contextPath + "/web/other-tasks";
		} else {
			window.location = contextPath + "/web/formElements";
		}
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath + "/web/formElements";
	}

	FormElement.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showFormElement(id);
				break;
			case 1:
				editFormElement(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', contextPath + "/web/formElements/"
						+ id);
				break;
			}
		}
		el.modal('show');
	}

	FormElement.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		formElementModel.pid = null; // reset formElement model;
		formElementModel.formElementValues = [];
		$('#tblOptions').html("");
		
		$('#divMasterTable').hide();
		$('#divLoadFromMobileData').hide();
		$('#divLoadMobileData').hide();
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