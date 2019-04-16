// Create a DynamicDocument object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DynamicDocument) {
	this.DynamicDocument = {};
}

(function() {
	'use strict';

	var dynamicDocumentContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// call from menu item
		var documentPid = getParameterByName('doc');
		var userPid = getParameterByName('user');
		if (documentPid != null && documentPid != "") {
			$('#dbDocument').val(documentPid);

		}
		if (userPid != null && userPid != "") {
			$('#dbUser').val(userPid);
		}
		DynamicDocument.filter();
		
		$('#btnApply').on('click', function() {
			if ($("#dbUser").val() == "no") {
				alert("Please select User.");
				return;
			}
			if ($("#dbDocument").val() == "no") {
				alert("Please select Document.");
				return;
			}
			DynamicDocument.filter();
		});
		
		$(".btnSubmitForms").click(function() {
			submitUpdatedForms();
		});
	});

	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}

	var dynamicDocument = {
		pid : null,
		filledForms : []
	}
	
	function submitUpdatedForms() {
		$.each(formFormElementList, function(index, formFormElement) {
							var filledForm = {
								pid : $("#hdn"+ formFormElement.form.pid).val(),
								formPid : formFormElement.form.pid,
								filledFormDetails : [],
								imageRefNo : formFormElement.form.pid
							};
							$.each(formFormElement.formFormElements, function(index2, formFormElement2) {
												var elementId = formFormElement.form.pid + "-" + formFormElement2.formElementPid;
												if (formFormElement2.formElementTypeName == "textBox" 
													    || formFormElement2.formElementTypeName == "textarea"
														|| formFormElement2.formElementTypeName == "dropdown"
														|| formFormElement2.formElementTypeName == "datePicker"
														|| formFormElement2.formElementTypeName == "yearPicker") {
													filledForm.filledFormDetails.push({
																value : $("#" + elementId).val(),
																formElementPid : formFormElement2.formElementPid,
																formElementName : formFormElement2.formElementName
															});
												} else if (formFormElement2.formElementTypeName == "radioButton") {
													filledForm.filledFormDetails
															.push({
																value : $("input[name='"+ elementId+ "']:checked").val(),
																formElementPid : formFormElement2.formElementPid,
																formElementName : formFormElement2.formElementName
															});
												} else if (formFormElement2.formElementTypeName == "checkBox") {
													$.each($("input[name='"+ elementId+ "']:checked"),function() {
																		filledForm.filledFormDetails.push({
																					value : $(this).val(),
																					formElementPid : formFormElement2.formElementPid,
																					formElementName : formFormElement2.formElementName
																		});
																	});
												}
											});
							dynamicDocument.filledForms.push(filledForm);
						});
		// clear validation message
		clearAndHideErrorBox();
		if(dynamicScript.length > 0) {
			var errors = validateForm(dynamicDocument.filledForms);
			if(errors.length > 0) {
				dynamicDocument.filledForms = [];
				var errMsg = "<li>";
				for (let err of errors) {
					errMsg = errMsg.concat(err.message + "</li><li>")
				}
				$(".alert > p").html(errMsg);
				$('.alert li:empty').remove();
				$('.alert').show();
				return;
			}
		}

		$.ajax({
			method : 'POST',
			url : dynamicDocumentContextPath + "/filled-forms",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(dynamicDocument),
			success : function(data) {
				$('#editModal').modal('hide');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	DynamicDocument.onChangeUser = function(){
//		if ($("#dbEmployee").val() == "no") {
//			return;
//		}
		$("#dbDocument").html('<option>Loading...</option>');
		$.ajax({
			url : dynamicDocumentContextPath + "/loaddocument",
			type : 'GET',
			data : {
				employeePid : $("#dbEmployee").val()
			},
			success : function(documents) {
				$("#dbDocument").html('<option value="no">Select Document</option>');
				if (documents != null && documents.length > 0) {
					$.each(documents, function(index, document) {
						$("#dbDocument").append(
								'<option value="' + document.pid + '">' + document.name
										+ '</option>');
						$("#dbDocument").val(document.pid);
					});
				}

			}
		});
	}
	
	DynamicDocument.editDynamicDocument = function(documentPid, dynamicDocumentPid, userPid) {
		dynamicDocument.pid = dynamicDocumentPid;
		dynamicDocument.filledForms = [];
		
		// clear validation message
		clearAndHideErrorBox();
		
		$('#editModal').modal('show');
		
		// load forms
		DynamicDocument.loadForms(documentPid,userPid);
		
		$.ajax({
			url : dynamicDocumentContextPath + "/" + dynamicDocumentPid,
			method : 'GET',
			success : function(dynamicDocument) {

				$('#lbl_activityInEdit').text(dynamicDocument.activityName);
				$('#lbl_accountInEdit').text(dynamicDocument.accountName);
				$('#lbl_documentInEdit').text(dynamicDocument.documentName);
				$('#lbl_documentDateInEdit').text(convertDateTimeFromServer(dynamicDocument.createdDate));
				
				// fill the form fields
				fillForms(dynamicDocument);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function fillForms(dynamicDocument) {

		// load filled data
		$.each(dynamicDocument.filledForms, function(key, filledForm) {
											$("#hdn" + filledForm.formPid).val(filledForm.pid);
											$.each(filledForm.filledFormDetails, function(index, filledFormDetail) {
																var elementId = filledForm.formPid + "-" + filledFormDetail.formElementPid;
																if (filledFormDetail.formElementType == "textBox"
																		|| filledFormDetail.formElementType == "textarea"
																		|| filledFormDetail.formElementType == "dropdown"
																		|| filledFormDetail.formElementType == "datePicker"
																		|| filledFormDetail.formElementType == "yearPicker") {
																	$("#"+ elementId).val(filledFormDetail.value);
																} else if (filledFormDetail.formElementType == "radioButton") {
																	$("input[name='"+ elementId + "'][value='" + filledFormDetail.value  + "']").prop('checked', true);
																} else if (filledFormDetail.formElementType == "checkBox") {
																	$("input:checkbox[name='" + elementId + "'][value='" + filledFormDetail.value + "']").prop("checked", true);
																}
															});
										});
	}

	// for storing dynamic scripts (jscode) in forms
	var dynamicScript = [];
	var formFormElementList = [];
	DynamicDocument.loadForms = function(documentPid,userPid) {

		$('#divForms').html("Please wait...");
		$.ajax({
					async : false,
					url : dynamicDocumentContextPath + "/load-forms",
					type : 'GET',
					data : {
						documentPid : documentPid,
						userPid : userPid
					},
					success : function(formFormElementMap) {
						$('#divForms').html("");
						var divForms = ""
						var index = 0;
						formFormElementList = [];
						$.each(formFormElementMap, function(formKey, formFormElements) {
											var form = JSON.parse(formKey);
											formFormElementList.push({
														form : form,
														formFormElements : formFormElements
											});
											if (formFormElements.length > 0) {
												// append tab content
												divForms += '<div class="panel panel-primary" data-collapsed="0"><div class="panel-heading"><div class="panel-title" style="font-weight: bold;">'
														+ form.name
														+ '</div>'
														+ ' <div class="panel-options">'
														+ '<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a></div></div>'
														+ '<div class="panel-body"><form role="form" class="form-horizontal form-groups-bordered">';

												// add input hidden for keep
												// filled form pid
												divForms += '<input type="hidden" id="hdn'
														+ form.pid + '" />';

												// append form elements
												$
														.each(
																formFormElements,
																function(key,
																		formFormElement) {

																	divForms += '<div class="form-group">'
																			+ '<label for="field-1" class="col-sm-3 control-label">'
																			+ formFormElement.formElementName
																			+ '</label>'
																			+ '<div class="col-sm-5">';

																	var formElementType = formFormElement.formElementTypeName;
																	var formElementValues = formFormElement.formElementValues;
																	var elementId = form.pid
																			+ "-"
																			+ formFormElement.formElementPid;
																	var readOnly = "";
																	if (!formFormElement.editable) {
																		readOnly = "readonly='readonly'";
																	}
																	if (formElementType == "textBox") {
																		divForms += "<input id='"
																				+ elementId
																				+ "' type=\"text\" class=\"form-control\" name='"
																				+ formFormElement.formElementName
																				+ "'  "
																				+ readOnly
																				+ "  />";
																	} else if (formElementType == "textarea") {
																		divForms += "<textarea rows='3' id='"
																			+ elementId
																			+ "' type=\"text\" class=\"form-control\" name='"
																			+ formFormElement.formElementName
																			+ "'  "
																			+ readOnly
																			+ " ></textarea>";
																} else if (formElementType == "radioButton") {
																		if (formElementValues != null) {
																			divForms += "<br />";
																			$
																					.each(
																							formElementValues,
																							function(
																									key,
																									formElementValue) {
																								divForms += "<input type=\"radio\" name='"
																										+ elementId
																										+ "' value=\""
																										+ formElementValue.name
																										+ "\" />&nbsp;"
																										+ formElementValue.name
																										+ "<br />";
																							});
																		} else {
																			divForms += "<input type=\"radio\" name='"
																					+ elementId
																					+ "' value=\""
																					+ formFormElement.formElementName
																					+ "\" />"
																		}
																	} else if (formElementType == "checkBox") {
																		if (formElementValues != null) {
																			divForms += "<br />";
																			$
																					.each(
																							formElementValues,
																							function(
																									key,
																									formElementValue) {
																								divForms += "<input type=\"checkbox\" name='"
																										+ elementId
																										+ "' value=\""
																										+ formElementValue.name
																										+ "\"   />&nbsp;"
																										+ formElementValue.name
																										+ "<br />";
																							});
																		} else {
																			divForms += "<input type=\"checkbox\" name='"
																					+ elementId
																					+ "'  value=\""
																					+ formFormElement.formElementName
																					+ "\" />"
																		}
																	} else if (formElementType == "datePicker") {
																		divForms += "<input id='"
																				+ elementId
																				+ "' type=\"text\" name='"
																				+ formFormElement.formElementName
																				+ "' class=\"form-control datePicker\" "
																				+ readOnly
																				+ "  />";
																	} else if (formElementType == "yearPicker") {
																		divForms += "<input id='"
																				+ elementId
																				+ "' type=\"text\" name='"
																				+ formFormElement.formElementName
																				+ "' class=\"form-control yearPicker\" "
																				+ readOnly
																				+ "  />";
																	} else if (formElementType == "dropdown") {
																		divForms += "<select id='"
																				+ elementId
																				+ "' class=\"form-control\" name='"
																				+ formFormElement.formElementName
																				+ "'><option value='none'>Select</option>";
																		if (formElementValues != null) {
																			$
																					.each(
																							formElementValues,
																							function(
																									key,
																									formElementValue) {
																								divForms += "<option value=\""
																										+ formElementValue.name
																										+ "\">"
																										+ formElementValue.name
																										+ "</option>"
																							});
																		}
																		divForms += "</select>";
																	}
																	divForms += "</div></div>"
																});
												divForms += "</form></div></div>";

											}
											// load dynamic script
											if (form.jsCode) {
												dynamicScript.push(form.jsCode);
											}
										});
						$('#divForms').html(divForms);
						$('#divDynamicForms').show();
						$('.datePicker').datepicker();
						$('.yearPicker').datepicker({
											changeMonth : false,
											changeYear : true,
											dateFormat : 'yy',
											onChangeMonthYear : function(y, m, i) {
												var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
												$(this).datepicker('setDate', new Date(year, 0, 1));
												$(this).datepicker("hide");
											}
						});
						loadDynamicScript();
					}

				});
	}
	
	function loadDynamicScript() {
		if(dynamicScript.length > 0) {
			for(let i = 0; i < dynamicScript.length; i++){
				$("body").append(dynamicScript[i]);	
				// call method in dynamic script
				initializeForm();
			}
		}
	}

	function showDynamicDocument(pid) {
		$
				.ajax({
					url : dynamicDocumentContextPath + "/" + pid,
					method : 'GET',
					success : function(data) {

						$('#lbl_documentNumber').text(data.documentNumberLocal);
						$('#lbl_user').text(data.userName);
						$('#lbl_activity').text(data.activityName);
						$('#lbl_account').text(data.accountName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_documentDate').text(
								convertDateTimeFromServer(data.createdDate));
						$('#divDynamicDocumentDetails').html("");
						$
								.each(
										data.filledForms,
										function(index, filledForm) {
											var table = '<table class="table  table-striped table-bordered"><tr><td colspan="2" style="font-weight: bold;">'
													+ filledForm.formName
													+ '</td></tr>';
											$
													.each(
															filledForm.filledFormDetails,
															function(index,
																	formDetail) {
																table += "<tr><td>"
																		+ formDetail.formElementName
																		+ "</td><td>"
																		+ formDetail.value
																		+ "</td>";
															});
											table += '</table>';
											$('#divDynamicDocumentDetails')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function showDynamicDocumentImages(pid) {
		$
				.ajax({
					url : dynamicDocumentContextPath + "/images/" + pid,
					method : 'GET',
					success : function(filledFormFiles) {

						$('#divDynamicDocumentImages').html("");
						$
								.each(
										filledFormFiles,
										function(index, filledFormFile) {
											var table = '<table class="table  table-striped table-bordered"><tr><td style="font-weight: bold;">'
													+ filledFormFile.formName
													+ '</td></tr>';
											$
													.each(
															filledFormFile.files,
															function(index,
																	file) {
																table += '<tr><th>'
																		+ file.fileName
																		+ '</th></tr>';
																table += '<tr><td><img width="100%" src="data:image/png;base64,'
																		+ file.content
																		+ '"/></td></tr>';
															});
											table += '</table>';
											$('#divDynamicDocumentImages')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	DynamicDocument.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showDynamicDocument(pid);
				break;
			case 1:
				showDynamicDocumentImages(pid);
				break;
			}
		}
		el.modal('show');
	}

	DynamicDocument.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyDynamicDocument').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : dynamicDocumentContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						documentPid : $("#dbDocument").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(dynamicDocuments) {
						$('#tBodyDynamicDocument').html("");
						if (dynamicDocuments.length == 0) {
							$('#tBodyDynamicDocument')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$.each(dynamicDocuments,
										function(index, dynamicDocument) {

											
													var content =	"<tr><td>"
																	+ dynamicDocument.employeeName
																	+ "</td><td>"
																	+ dynamicDocument.accountName
																	+ "</td><td>"
																	+ dynamicDocument.activityName
																	+ "</td><td>"
																	+ dynamicDocument.documentName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(dynamicDocument.createdDate)
																	+ "</td><td><button type='button' class='btn btn-primary' onclick='DynamicDocument.showModalPopup($(\"#viewModal\"),\""
																	+ dynamicDocument.pid
																	+ "\",0);'>View Details</button></td>";
																	if(dynamicDocument.imageButtonVisible){
																	content	+= "<td><button type='button' class='btn btn-blue' onclick='DynamicDocument.showModalPopup($(\"#imagesModal\"),\""
																				+ dynamicDocument.pid
																				+ "\",1);'>View Images</button></td>";
																	}else{
																		content	+= "<td>No Image</td>";
																	}
																	content += "<td><button type='button' class='btn btn-blue' onclick='DynamicDocument.editDynamicDocument(\""
																				+ dynamicDocument.documentPid
																				+ "\",\""
																				+ dynamicDocument.pid
																				+ "\",\""
																				+ dynamicDocument.userPid
																				+ "\");'>Edit</button></td></tr>";
						$('#tBodyDynamicDocument').append(content);	
										});
					}
				});
	}

	DynamicDocument.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

	
	function clearAndHideErrorBox() {
		$(".alert > p").html("");
		$('.alert').hide();
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
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