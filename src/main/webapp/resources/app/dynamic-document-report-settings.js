// Create a DynamicDocumentReportSetting object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DynamicDocumentReportSetting) {
	this.DynamicDocumentReportSetting = {};
}

(function() {
	'use strict';
	var dynamicDocumentReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#dynamicDocumentReportSettingsForm");
	var deleteForm = $("#deleteForm");
	var dynamicDocumentSettingsHeaderPid = "";
	var dynamicDocumentReportSettingModel = {
		pid : null,
		name : null,
		title : null,
		documentPid : null,
		documentName : null,
		documentSettingsColumnsDTOs : [],
		documentSettingsRowColourDTOs : []
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

		$('#dbDocuments').on('change', function() {
			getFormElementsByDocument();
		});

		$('#btnSaveDocumentSettingsColumns').on('click', function() {
			saveDocumentSettingsColumn();
		});

		$('#btnSaveDocumentSettingsRowColour').on('click', function() {
			saveDocumentSettingsRowColour();
		});
	});

	var totDataArray = [];
	function getFormElementsByDocument() {
		var documentPid = $('#dbDocuments').val();

		if (documentPid == "-1") {
			alert("please select document");
			return;
		}

		$
				.ajax({
					url : dynamicDocumentReportContextPath
							+ "/loadFormElements",
					method : 'GET',
					data : {
						documentPid : documentPid,
					},
					success : function(data) {
						$('#tb_FormElementValues').html("");
						totDataArray = data;
						$
								.each(
										data,
										function(index, filledForm) {
											var table = '<tr><td><input type="checkbox" name="chk_filledForm" value="'
													+ filledForm.pid
													+ '"/></td><td>'
													+ filledForm.name
													+ '</td><td>'
													+ filledForm.formElementTypeName
													+ '</td><td><input type="number" id="txt-'
													+ filledForm.pid
													+ '"</td></tr>';
											$('#tb_FormElementValues').append(
													table);
										});
						var dynaSelectBoxs = [];

						$.each(data, function(index, filledForm) {
							if (filledForm.formElementValues.length > 0) {
								dynaSelectBoxs.push({
									name : filledForm.name,
									pid : filledForm.pid
								});
							}
						});
						$('#tb_ElementValues').html("");
						$('#div_selectBox').html("");
						if (dynaSelectBoxs.length > 0) {
							var selectBox = "<select id='dbSelect' onchange='DynamicDocumentReportSetting.getChildDataFunctions()' class='form-control'> <option value='-1'>---select---</option>";
							$.each(dynaSelectBoxs, function(index, dynaSelect) {
								selectBox += "<option  value=" + dynaSelect.pid
										+ ">" + dynaSelect.name + "</option>";
							});
							selectBox += "</select>";
							$('#div_selectBox').html(selectBox);
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}

	DynamicDocumentReportSetting.getChildDataFunctions = function() {
		var colorSelect = "<select id='db_color'class='form-control'> <option value='no-color'>no color</option><option value='red'>Red</option><option value='green'>Green</option><option value='yellow'>Yellow</option></select>";
		var name = $('#dbSelect option:selected').text();
		$('#tb_ElementValues').html("");
		var tbValue = "";
		$
				.each(
						totDataArray,
						function(index, filledForm) {
							if (filledForm.name == name) {
								$
										.each(
												filledForm.formElementValues,
												function(index, fForm) {
													tbValue += "<tr><td><input type='checkbox' name='chk_fForm' value='"
															+ fForm.id
															+ "'/></td><td>"
															+ fForm.name
															+ "</td><td>"
															+ colorSelect
															+ "</td></tr>";
												});
							}
							$('#tb_ElementValues').html(tbValue);
						});
	}

	function createUpdateDynamicDocumentReportSetting(el) {

		var docPid = $('#dbDocuments').val();
		var name = $('#field_name').val();
		var title = $('#field_title').val();
		var DynamicDocumentSettingsColumnsDTOs = [];
		var DynamicDocumentSettingsRowColourDTOs = [];
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

		var orderNoExist=false;
		$.each($("input[name='chk_filledForm']:checked"), function() {
			var Pid = $(this).val();
			var sortOrder = $("#txt-" + Pid).val();
			if(sortOrder==""){
				orderNoExist=true;
			}
			DynamicDocumentSettingsColumnsDTOs.push({
				formElementPid : Pid,
				sortOrder : sortOrder
			});
		});
		
		if(orderNoExist){
			$(".alert > p").html("please add sort order to all selected checkboxes");
			$('.alert').show();
			return;
		}

		$.each($("input[name='chk_fForm']:checked"), function() {
			var id = $(this).val();
			DynamicDocumentSettingsRowColourDTOs.push({
				formElementPid : $('#dbSelect').val(),
				formElementValueId : id,
				formElementValueName : $(this).closest("tr").find('td:eq(1)')
						.text(),
				formElementName : $('#dbSelect option:selected').text(),
				colour : $(this).closest("tr")
						.find('#db_color option:selected').val()
			});
		});

		dynamicDocumentReportSettingModel.name = name;
		dynamicDocumentReportSettingModel.title = title;
		dynamicDocumentReportSettingModel.documentPid = docPid;
		dynamicDocumentReportSettingModel.documentName = $(
				'#dbDocuments option:selected').text();
		dynamicDocumentReportSettingModel.documentSettingsColumnsDTOs = DynamicDocumentSettingsColumnsDTOs;
		dynamicDocumentReportSettingModel.documentSettingsRowColourDTOs = DynamicDocumentSettingsRowColourDTOs;
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

	function editDynamicDocumentReportSetting(id) {
		$.ajax({
			url : dynamicDocumentReportContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data);
				$('#field_name').val(data.name);
				$('#field_title').val((data.title == null ? "" : data.title));
				if (data.documentPid != null && data.documentPid != "-1") {
					$('#dbDocuments').val(data.documentPid);
					$('#dbDocuments').attr("disabled", true);
				}
				// set pid
				dynamicDocumentReportSettingModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showDynamicDocumentReportSetting(id) {
		$
				.ajax({
					url : dynamicDocumentReportContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#lbl_name').text(data.name);
						$('#lbl_title').text(data.title);
						$('#lbl_document').text(data.documentName);

						$('#viewTbodyDocumentSettingsColumns').html("");
						$('#viewTbodydocumentSettingsRowColour').html("");

						if (data.documentSettingsColumnsDTOs.length > 0) {
							$
									.each(
											data.documentSettingsColumnsDTOs,
											function(index,
													documentSettingsColumn) {
												var table = "<tr><td>"
														+ documentSettingsColumn.formElementName
														+ "</td><td>"
														+ documentSettingsColumn.sortOrder
														+ "</td></tr>";
												$(
														'#viewTbodyDocumentSettingsColumns')
														.append(table);
											});
						}

						if (data.documentSettingsRowColourDTOs.length > 0) {
							$
									.each(
											data.documentSettingsRowColourDTOs,
											function(index,
													documentSettingsRowColour) {
												console
														.log(documentSettingsRowColour);
												var table = "<tr><td>"
														+ documentSettingsRowColour.formElementName
														+ "</td><td>"
														+ documentSettingsRowColour.formElementValueName
														+ "</td><td>"
														+ documentSettingsRowColour.colour
														+ "</td></tr>";
												$(
														'#viewTbodydocumentSettingsRowColour')
														.append(table);
											});
						}

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
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

	function saveDocumentSettingsColumn() {
		$(".error-msg").html("");
		var selectedDocumentSettingsColumns = [];
		$
				.each(
						$("input[name='chk_filledForm']:checked"),
						function() {
							var Pid = $(this).val();
							var sortOrder = $("#txt-" + Pid).val();
							selectedDocumentSettingsColumns
									.push({
										formElementPid : Pid,
										sortOrder : sortOrder,
										dynamicDocumentSettingsHeaderPid : dynamicDocumentSettingsHeaderPid
									});
						});
		if (selectedDocumentSettingsColumns.length == 0) {
			$(".error-msg").html("Please select Document Settings Columns");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : dynamicDocumentReportContextPath
					+ "/assignDocumentSettingsColumns",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedDocumentSettingsColumns),
			success : function(status) {
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveDocumentSettingsRowColour() {
		$(".error-msg").html("");
		var selectedDocumentSettingsRowColour = [];

		$
				.each(
						$("input[name='chk_fForm']:checked"),
						function() {
							var id = $(this).val();
							selectedDocumentSettingsRowColour
									.push({
										formElementPid : $('#lblFillefFormpid')
												.attr('name'),
										// formElementName : $('#dbSelect
										// option:selected').text(),
										formElementValueId : id,
										formElementValueName : $(this).closest(
												"tr").find('td:eq(1)').text(),
										colour : $(this).closest("tr").find(
												'#db_color option:selected')
												.val(),
										dynamicDocumentSettingsHeaderPid : dynamicDocumentSettingsHeaderPid
									});
						});

		console.log(selectedDocumentSettingsRowColour);
		if (selectedDocumentSettingsRowColour.length == 0) {
			$(".error-msg").html("Please select Document Settings Columns");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : dynamicDocumentReportContextPath
					+ "/assignDynamicDocumentSettingsRowColours",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedDocumentSettingsRowColour),
			success : function(status) {
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function loadDocumentSettingsRowColours(id, docPid) {
		dynamicDocumentSettingsHeaderPid = id;
		$
				.ajax({
					url : dynamicDocumentReportContextPath
							+ "/getDynamicDocumentSettingsRowColours",
					method : 'GET',
					data : {
						headPid : id,
						documentPid : docPid
					},
					success : function(data) {
						$('#tbodyDocumentSettingsRowColour').html("");
						var table = "";
						var colourId = "";
						$
								.each(
										data.allDynamicDocumentSettingsColumns,
										function(index, filledForm) {
											// console.log(filledForm);
											var lbl_div = "<label id='lblFillefFormpid'class='control-label' name="
													+ filledForm.pid
													+ "></label>";
											$('#field_filledForm').append(
													lbl_div);

											$
													.each(
															filledForm.formElementValues,
															function(index,
																	fForm) {

																var colorSelect = "<select id='db_color'class='form-control'> <option value='no-color'>no color</option><option value='red'>Red</option><option value='green'>Green</option><option value='yellow'>Yellow</option></select>";
																var chkBox = '<input type="checkbox" name="chk_fForm" value="'
																		+ fForm.id
																		+ '"/>';
																$
																		.each(
																				data.truedynamicDocumentSettingsRowColourDTOs,
																				function(
																						index,
																						trueDynaDocSett) {

																					if (fForm.id == trueDynaDocSett.formElementValueId) {
																						chkBox = '<input type="checkbox" name="chk_fForm" value="'
																								+ fForm.id
																								+ '" checked/>';
																						colorSelect = "<select id='db_color'class='form-control'> <option value='no-color'>no color</option><option value='red'>Red</option><option value='green'>Green</option><option value='yellow'>Yellow</option><option value="
																								+ trueDynaDocSett.colour
																								+ " selected='selected'>"
																								+ trueDynaDocSett.colour
																								+ "</option></select>"
																					}
																				});
																table += "<tr><td>"
																		+ chkBox
																		+ "</td><td>"
																		+ fForm.name
																		+ "</td><td>"
																		+ colorSelect
																		+ "</td></tr>";
															});
											$('#tbodyDocumentSettingsRowColour')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function loadDocumentSettingsColumns(id, docPid) {
		dynamicDocumentSettingsHeaderPid = id;
		$
				.ajax({
					url : dynamicDocumentReportContextPath
							+ "/getdynamicDocumentSettingsColumns",
					method : 'GET',
					data : {
						headPid : id,
						documentPid : docPid
					},
					success : function(data) {
						$('#tbodyDocumentSettingsColumns').html("");
						$
								.each(
										data.allDynamicDocumentSettingsColumns,
										function(index, filledForm) {
											var chkBox = '<input type="checkbox" name="chk_filledForm" value="'
													+ filledForm.pid + '"/>';
											var orderNum = '<input type="number" id="txt-'
													+ filledForm.pid + '"/>';
											$
													.each(
															data.trueDynamicDocumentSettingsColumns,
															function(index,
																	trueDynaDocSett) {
																if (filledForm.pid == trueDynaDocSett.formElementPid) {
																	chkBox = '<input type="checkbox" name="chk_filledForm" value="'
																			+ filledForm.pid
																			+ '" checked/>';
																	orderNum = '<input type="number" id="txt-'
																			+ filledForm.pid
																			+ '" value="'
																			+ trueDynaDocSett.sortOrder
																			+ '"/>';
																}
															});

											var table = '<tr><td>'
													+ chkBox
													+ '</td><td>'
													+ filledForm.name
													+ '</td><td>'
													+ filledForm.formElementTypeName
													+ '</td><td>' + orderNum
													+ '</td></tr>';
											$('#tbodyDocumentSettingsColumns')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}

	DynamicDocumentReportSetting.showListModalPopup = function(el, id, docPid,
			action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				loadDocumentSettingsColumns(id, docPid);
				break;
			case 1:
				loadDocumentSettingsRowColours(id, docPid);
				break;
			}
		}
		el.modal('show');
	}

	DynamicDocumentReportSetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDynamicDocumentReportSetting(id);
				break;
			case 1:
				editDynamicDocumentReportSetting(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dynamicDocumentReportContextPath
						+ "/" + id);
				break;

			}
		}
		$('#dbDocuments').attr("disabled", false);
		el.modal('show');
	}

	DynamicDocumentReportSetting.closeModalPopup = function(el) {
		el.modal('hide');
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
