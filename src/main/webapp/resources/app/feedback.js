// Create a Feedback object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Feedback) {
	this.Feedback = {};
}

(function() {
	'use strict';

	var feedbackContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// load data
		Feedback.filter();
		Feedback.statusValues();
		
		$(".btnSubmitForms").click(function() {
			submitUpdatedForms();
		});
	});

	
	var feedback = {
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
								 if(formFormElement2.editable){
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
								 }
											});
							feedback.filledForms.push(filledForm);
						});
		// clear validation message
		clearAndHideErrorBox();
		if(dynamicScript.length > 0) {
			var errors = validateForm(feedback.filledForms);
			if(errors.length > 0) {
				feedback.filledForms = [];
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
			url : feedbackContextPath + "/filled-forms",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(feedback),
			success : function(data) {
				$('#editModal').modal('hide');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	Feedback.editFeedback = function(documentPid, feedbackPid, userPid) {
		feedback.pid = feedbackPid;
		feedback.filledForms = [];
		
		// clear validation message
		clearAndHideErrorBox();
		
		$('#editModal').modal('show');
		
		// load forms
		Feedback.loadForms(documentPid,userPid);
		
		$.ajax({
			url : feedbackContextPath + "/" + feedbackPid,
			method : 'GET',
			success : function(feedback) {

				$('#lbl_activityInEdit').text(feedback.activityName);
				$('#lbl_accountInEdit').text(feedback.accountName);
				$('#lbl_documentInEdit').text(feedback.documentName);
				$('#lbl_documentDateInEdit').text(convertDateTimeFromServer(feedback.createdDate));
				
				// fill the form fields
				fillForms(feedback);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function fillForms(feedback) {

		// load filled data
		$.each(feedback.filledForms, function(key, filledForm) {
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
	Feedback.loadForms = function(documentPid,userPid) {

		$('#divForms').html("Please wait...");
		$.ajax({
					async : false,
					url : feedbackContextPath + "/load-forms",
					type : 'GET',
					data : {
						feedbackGroupPid : $("#dbFeedbackGroup").val(),
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

	function showFeedback(pid) {
		$
				.ajax({
					url : feedbackContextPath + "/" + pid,
					method : 'GET',
					success : function(data) {

						$('#lbl_documentNumber').text(data.documentNumberLocal);
						$('#lbl_user').text(data.userName);
						$('#lbl_activity').text(data.activityName);
						$('#lbl_account').text(data.accountName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_documentDate').text(
								convertDateTimeFromServer(data.createdDate));
						$('#divFeedbackDetails').html("");
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
											$('#divFeedbackDetails')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function showFeedbackImages(pid) {
		$
				.ajax({
					url : feedbackContextPath + "/images/" + pid,
					method : 'GET',
					success : function(filledFormFiles) {

						$('#divFeedbackImages').html("");
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
											$('#divFeedbackImages')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	Feedback.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showFeedback(pid);
				break;
			case 1:
				showFeedbackImages(pid);
				break;
			}
		}
		el.modal('show');
	}
	
	Feedback.statusValues = function() {
		var groupPid = $("#dbFeedbackGroup").val();
		$("#dbStatus").html('<option value="no">All Status</option>');
		if(groupPid == null || groupPid == 'no'){
			return;
		}
		$.ajax({
			url : feedbackContextPath + "/status-values",
			type : 'GET',
			data : {
				feedbackGroupPid : $("#dbFeedbackGroup").val()
			},
			success : function(values) {
				$.each(values, function(index, value) {
					$("#dbStatus").append('<option value="'+value.name+'">'+value.name+'</option>');
				});
			}
		});
	}


	Feedback.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if($("#dbFeedbackGroup").val() == null){
			$("#dbFeedbackGroup").html("<option value='no'>Feedback Group</option>")
			return;
		}
		$('#tBodyFeedback').html("<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : feedbackContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						feedbackGroupPid : $("#dbFeedbackGroup").val(),
						status : $("#dbStatus").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(feedbacks) {
						$('#tBodyFeedback').html("");
						if (feedbacks.length == 0) {
							$('#tBodyFeedback')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$.each(feedbacks,
										function(index, feedback) {
console.log(feedback);
											$('#tBodyFeedback')
													.append(
															"<tr><td>"
																	+ feedback.employeeName
																	+ "</td><td>"
																	+ feedback.accountName
																	+ "</td><td>"
																	+ feedback.activityName
																	+ "</td><td>"
																	+ feedback.documentName
																	+ "</td><td>"
																	+ convertDateTimeFromServer(feedback.createdDate)
																	+ "</td><td><button type='button' class='btn btn-primary' onclick='Feedback.showModalPopup($(\"#viewModal\"),\""
																	+ feedback.pid
																	+ "\",0);'>View Details</button></td><td><button type='button' class='btn btn-blue' onclick='Feedback.showModalPopup($(\"#imagesModal\"),\""
																	+ feedback.pid
																	+ "\",1);'>View Images</button></td><td><button type='button' class='btn btn-blue' onclick='Feedback.editFeedback(\""
																	+ feedback.documentPid
																	+ "\",\""
																	+ feedback.pid
																	+ "\",\""
																	+ feedback.userPid
																	+ "\");'>Edit</button></td></tr>");
										});
					}
				});
	}

	Feedback.showDatePicker = function() {
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