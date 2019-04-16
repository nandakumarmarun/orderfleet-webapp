// Create a AccountProfileDynamicDocumentAccountprofile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountProfileDynamicDocumentAccountprofile) {
	this.AccountProfileDynamicDocumentAccountprofile = {};
}

(function() {
	'use strict';

	var accountProfileDynamicDocumentAccountprofileContextPath = location.protocol
			+ '//' + location.host + location.pathname;
	var createEditForm = $("#accountProfileDynamicDocumentAccountprofileForm");
	var deleteForm = $("#deleteForm");
	
	var docPid="";var formPid="";
	var accountProfileDynamicDocumentAccountprofileModel = {
		id : null,
		accountProfleField : null,
		formElementPid : null,
		formElementName : null,
		documentPid : null,
		documentName : null,
		formPid : null,
		formName : null
	};

	$(document)
			.ready(
					function() {

						$('#field_dynamicDocument').change(function() {
							getForms();
						});

$('#field_form').change(function() {
							var formId = $("#field_form").val();
							getFormElements(formId);
						});
						
						$('#myFormSubmit')
								.click(
										function() {
											createAccountProfileDynamicDocumentAccountprofile();
										});
										
										$('#myEditFormSubmit')
								.click(
										function() {
											updateAccountProfileDynamicDocumentAccountprofile();
										});

					});

	function createAccountProfileDynamicDocumentAccountprofile() {
	
	var dyDoc=$("#field_dynamicDocument").val();
	if(dyDoc=="-1"){
		alert("please select dynamic document");
		return;
	}
	
		var accountProfileDynamicDocumentAccountprofiles = [];
		$('#tbl_accountDynamicAccount > tbody  > tr')
				.each(
						function() {

							var this_row = $(this);
							var accountProfleField = $.trim(this_row.find(
									'td:eq(0)').html());// td:eq(0) means first
							// td of this row
							var formElementPid = $.trim(this_row.find(
									'.form-control :selected').val());
							var formElementName = $.trim(this_row.find(
									'.form-control :selected').html());

							if (formElementPid != "-1") {
								
								accountProfileDynamicDocumentAccountprofileModel.accountProfleField = accountProfleField;
								accountProfileDynamicDocumentAccountprofileModel.formElementPid = formElementPid;
								accountProfileDynamicDocumentAccountprofileModel.formElementName = formElementName;
								accountProfileDynamicDocumentAccountprofileModel.documentPid = $(
										"#field_dynamicDocument").val();
								accountProfileDynamicDocumentAccountprofileModel.documentName = $(
										"#field_dynamicDocument option:selected")
										.text();
								accountProfileDynamicDocumentAccountprofileModel.formPid = $(
										"#field_form").val();
								accountProfileDynamicDocumentAccountprofileModel.formName = $(
										"#field_form option:selected").text();
								accountProfileDynamicDocumentAccountprofiles
										.push(accountProfileDynamicDocumentAccountprofileModel);
								accountProfileDynamicDocumentAccountprofileModel = {};
							}

						});
				 $
				.ajax({
					method : "POST",
					url : accountProfileDynamicDocumentAccountprofileContextPath,
					contentType : "application/json; charset=utf-8",
					data : JSON
							.stringify(accountProfileDynamicDocumentAccountprofiles),
					success : function(data) {
						onSaveSuccess(data);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}
	
	function updateAccountProfileDynamicDocumentAccountprofile() {
		var accountProfileDynamicDocumentAccountprofiles = [];
		$('#tbl_editAccountDynamicAccount > tbody  > tr')
				.each(
						function() {

							var this_row = $(this);
							var accountProfleField = $.trim(this_row.find('td:eq(0)').html());// td:eq(0) means first
							// td of this row
							var formElementPid = $.trim(this_row.find('.form-control :selected').val());
							var formElementName = $.trim(this_row.find('.form-control :selected').html());
							if (formElementPid != "-1") {
								accountProfileDynamicDocumentAccountprofileModel.accountProfleField = accountProfleField;
								accountProfileDynamicDocumentAccountprofileModel.formElementPid = formElementPid;
								accountProfileDynamicDocumentAccountprofileModel.formElementName = formElementName;
								accountProfileDynamicDocumentAccountprofileModel.documentPid = docPid;
								accountProfileDynamicDocumentAccountprofileModel.formPid = formPid;
								accountProfileDynamicDocumentAccountprofiles.push(accountProfileDynamicDocumentAccountprofileModel);
								accountProfileDynamicDocumentAccountprofileModel = {};
							}

						});
				 $
				.ajax({
					method : "POST",
					url : accountProfileDynamicDocumentAccountprofileContextPath,
					contentType : "application/json; charset=utf-8",
					data : JSON
							.stringify(accountProfileDynamicDocumentAccountprofiles),
					success : function(data) {
						onSaveSuccess(data);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}
	

	function getForms() {
		var formElementId = $("#field_dynamicDocument").val();
		if (formElementId == "-1") {
			alert("select dynamic document");
			return;
		}
		$
				.ajax({
					url : accountProfileDynamicDocumentAccountprofileContextPath
							+ "/getForms/" + formElementId,
					method : 'GET',
					success : function(data) {

						if (data.length > 0) {
							$('#field_form').html("");
						$('.slectbox option').remove();
						$('.slectbox ').append('<option value="-1">select question</option>');
						}

						var forms = "";
						$.each(data, function(index, formDoc) {
							forms += '<option value=' + formDoc.formPid + '>'
									+ formDoc.formName + '</option>';
						});
						$('#field_form').append(
								'<option value="-1">select form</option>'
										+ forms + '');
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function getFormElements(formId) {
// 	$('#tb_datas').html("");
		if (formId == "-1") {
			alert("select form");
			return;
		}
		
		$
				.ajax({
					url : accountProfileDynamicDocumentAccountprofileContextPath
							+ "/getFormElements/" + formId,
					method : 'GET',
					success : function(data) {
						var selectBox= createSelectBox(data);
						$('.slectbox option').remove();
						$('.slectbox ').append('<option value="-1">select question</option>');
						$('.slectbox').append(selectBox);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}
	
	
		function getFormElementsForEdit(formId) {
// 	$('#tb_editDatas').html("");
		if (formId == "-1") {
			alert("select form");
			return;
		}
		
		$
				.ajax({
					url : accountProfileDynamicDocumentAccountprofileContextPath
							+ "/getFormElements/" + formId,
					method : 'GET',
					success : function(data) {
						var selectBox=createSelectBox(data);
						$('.slectbox').append(selectBox);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
					async:false
				});
	}
	
	function createSelectBox(data){
			var selectBox = "";
						$.each(data, function(index, form) {
							selectBox += '<option value=' + form.formElementPid
									+ '>' + form.formElementName + '</option>';
						});
						
						return selectBox;
	}
	
	
	function editOrViewAccountProfileDynamicDocumentAccountprofile(documentPid,newFormPid){
	$('.slectbox option').remove();
	$('.slectbox ').append('<option value="-1">select question</option>');
	getFormElementsForEdit(newFormPid);
	
	docPid=documentPid;
	formPid=newFormPid;
			$.ajax({
					url : accountProfileDynamicDocumentAccountprofileContextPath+"/getOne",
					method : 'GET',
					data :{
						documentPid:documentPid,
						formPid:newFormPid
					},
					success : function(data) {
					
					setValuesToTable(data);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
					});

	}
	
	function setValuesToTable(data){
		$.each(data, function(index, accDocAcc) {
			$('#tbl_editAccountDynamicAccount > tbody  > tr').each(function() {
						var this_row = $(this);
						var accouield = $.trim(this_row.find('td:eq(0)').html());
						if(accouield==accDocAcc.accountProfleField){
							this_row.find('.slectbox').val(accDocAcc.formElementPid);
						}
						});
						});
		
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountProfileDynamicDocumentAccountprofileContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = accountProfileDynamicDocumentAccountprofileContextPath;
	}
	AccountProfileDynamicDocumentAccountprofile.showModalPopup = function(el,documentPid,formPid) {
	resetForm();
	
	if((!documentPid==null)||(!documentPid=="")){
		editOrViewAccountProfileDynamicDocumentAccountprofile(documentPid,formPid);
	}
		el.modal('show');
	}

	AccountProfileDynamicDocumentAccountprofile.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		accountProfileDynamicDocumentAccountprofileModel.id = null; // reset
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