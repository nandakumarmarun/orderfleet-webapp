// Create a OtherVoucherTransaction object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.OtherVoucherTransaction) {
	this.OtherVoucherTransaction = {};
}
(function() {
	'use strict';
	
    var documentPID=null;
    var dynamicDocumentPID=null;
    
	var otherVoucherTransactionContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var executiveTaskSubmission = {
		executiveTaskExecutionDTO : {
			activity : null,
			accountProfilePid : null
		},
		dynamicDocuments : [ {
			pid : null,
			employeePid :null,
			documentPid : null,
			filledForms : []
		} ]
	}

	
	// for storing dynamic scripts (jscode) in forms
	var dynamicScript = [];
	$(document).ready(function() {
		$('#field_email_subject').val($('#tittleName').html());
						$("#dbDocument").change(
										function() {
											$('#divDynamicForms').hide();
											if($('#dbDocument').val()== "no"){
												$("#dbAccount").html("<option>Select Account</option>");
												return;
											}
											var documentPid = $('#dbDocument').val().split("~")[0];
											var activityPid = $('#dbDocument').val().split("~")[1];

											loadAccountProfileByActivityId(activityPid);

											// set activity and document pid
											executiveTaskSubmission.executiveTaskExecutionDTO.activityPid = activityPid;
											executiveTaskSubmission.dynamicDocuments[0].documentPid = documentPid;
										});

						
						$(".btnSubmitForms").click(function() {
							$('.btnSubmitForms').attr('disabled','disabled');
							submitFilledForms();
						});
						
						$('#btnSaveFilledFormImage').click(function() {
							uploadFilledFormImage();
						});
						
						$('#dbForm').change(function() {
							getFilledFormImages();
						});
						
						$('#btnSearch').click(function() {
							searchTable($("#search").val());
						});
						
						$('#btnRemoveImage').click(function() {
							removeImage();
						});
						
						
						$("#myFormSubmit").click(function() {
							$('#myFormSubmit').attr('disabled','disabled');
							saveAccountprofile();
						});
						
						
						// call from menu item
						var docAndActPid = Orderfleet.getParameterByName("dod-act-pid");
						if (docAndActPid != null && docAndActPid != "") {
							$('#dbDocument').val(docAndActPid);
							
							var activityPid = $('#dbDocument').val().split("~")[1];
							loadAccountProfileByActivityId(activityPid);
						}
						
						$('#addNewAccountProfile').click(function() {
							$('#addAccountModal').modal('hide');
							$('#myModal').modal('show');
						});
						$('#printButton').click(function() {
							$('#printModal').modal('show');
						});
						
						// validate image
						$('#field_image').bind(
								'change',
								function() {
										
									var val = $(this).val().toLowerCase();
										var regex = new RegExp("(.*?)\.(docx|doc|pdf|xml|bmp|ppt|xls)$");
										 if((regex.test(val))) {
										$(this).val('');
										alert('Please select image only...!');
										return;
										}  
									
									var size = this.files[0].size;
									var file = $(this)[0].files[0];
									var fileReader = new FileReader();
									fileReader.readAsDataURL(file);

									fileReader.onload = function(e) {
										$('#previewImage').attr('src', fileReader.result);
										var base64Data = e.target.result
												.substr(e.target.result
														.indexOf('base64,')
														+ 'base64,'.length);
										
									};
									changeSpan();

								});
						
						var tittle = getParameterByName('name');
						if(tittle!=null){
							$("#tittleName").text("");
							$("#tittleName").text(tittle);
						}
						
						var etePid = getParameterByName('etePid');
						var dynPid = getParameterByName('dynPid');
						if (etePid != null && dynPid != null) {
							loadDynamic(etePid, dynPid);
						}
					});

	OtherVoucherTransaction.returnToTransaction = function(){
		var previousURL = document.referrer;
		if(previousURL.indexOf("executive-task-executions") !== -1) {
			window.history.back();
		} else {
			window.location = location.protocol + '//' + location.host + "/web/executive-task-executions";
		}
	}

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
	
	function loadDynamic(pid, dynPid) {
		$.ajax({
			url :otherVoucherTransactionContextPath+ "/loadExecutiveTaskExecution",
			type : "GET",
			data : {
				pid : pid,
				dynPid : dynPid,
			},
			success : function(status) {
				$("#dbEmployee").val(status.employeePid);
				$("#dbDocument").val(
						status.documentPid + "~" + status.activityPid);
				loadAccountProfileByActivityId(status.activityPid);
				$("#dbAccount").val(status.accountPid);
				OtherVoucherTransaction.editFilledForm(dynPid);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	OtherVoucherTransaction.print = function () {
		var dynamicDocumentPid = $('#hdnPrintDocPid').val();
		var name = $('#sbPrint').val();
		if(name == -1){
			alert("Please select print name");
			return;
		}
		var url =  location.protocol + '//' +location.host+ "/web/print/"+ dynamicDocumentPid + "?name=" + name;
		window.location = url;
	}
	
	OtherVoucherTransaction.email = function (dynamicDocument) {
		$('#searchModal').modal('hide');
		var dynamicDocumentPid=dynamicDocument.split(",")[0];
		var dynamicDocumentAccountMail=dynamicDocument.split(",")[1];
		$('#hdnDynamicDocumentPid').val(dynamicDocumentPid);
		$('#field_email').val(dynamicDocumentAccountMail);
		$('#emailModal').modal('show');
	}
	
	OtherVoucherTransaction.sendMail = function () {
		if($('#field_email').val() == ""){
			alert("Please enter Email Id");
			return;
		}
		var name = $('#sbEmailPrint').val();
		if(name == -1){
			alert("Please select print name");
			return;
		}
		var dynamicDocumentPid = $('#hdnDynamicDocumentPid').val();
		var url =  location.protocol + '//' +location.host+ "/web/email/"+dynamicDocumentPid +"?subject=" + $('#field_email_subject').val() +"&toEmails=" +$('#field_email').val() + "&name=" + name;
		$.ajax({
			url : url,
			type : 'GET',
			success : function(status) {
				$('#emailModal').modal('hide');
				if(status === true) {
					onSaveSuccess(status)
				} else {
					console.log("mail voucher template not configured properly.")
					addErrorAlert("Email send failed");
				}
				
			}
		});
	}
	
	function checkToEmails(){
		var emails = document.getElementById("field_email").value;
		var emailArray = emails.split(",");
		var hasErrors = false;
		var errorMessage="";
		for(i = 0; i <= (emailArray.length - 1); i++){
			if(checkEmail(emailArray[i])){
				// Do what ever with the email.
			}else{
				hasErrors=true;
				errorMessage+="invalid email: " + emailArray[i]+"\n\r";
			}
		}
		if(hasErrors){
			alert(errorMessage);
			return false;
		}
		return true;
	}
	
	function checkEmail(email) {
		var regExp = /(^[a-z]([a-z_\.]*)@([a-z_\.]*)([.][a-z]{3})$)|(^[a-z]([a-z_\.]*)@([a-z_\.]*)(\.[a-z]{3})(\.[a-z]{2})*$)/i;
		return regExp.test(email);
	}
	
	
	function submitFilledForms() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			addErrorAlert("Please select document and account");
			return;
		}
		executiveTaskSubmission.executiveTaskExecutionDTO.accountProfilePid = $("#dbAccount").val();
		var employeePid = $("#dbEmployee").val();
		executiveTaskSubmission.dynamicDocuments[0].employeePid = employeePid;
		$.each(formFormElementList, function(index, formFormElement) {
							var filledForm = {
								pid : $("#hdn"+ formFormElement.form.pid).val(),
								formPid : formFormElement.form.pid,
								filledFormDetails : [],
								imageRefNo : formFormElement.form.pid
							};
							if(formFormElement.form.multipleRecord) {
								$('#tbl'+ formFormElement.form.pid +' > tbody  > tr').each(function(){
									$(this).find('td').each(function() {
										var $td = $(this);
										var $th = $td.closest('table').find('th').eq($td.index());
										if($th.text().indexOf("Action") == "-1"){
											filledForm.filledFormDetails.push({
												id : $td.attr("id"),
												value : $td.text(),
												formElementPid : $th.attr("elemId"),
												formElementName : $th.attr("name")
											});
										}
									});
								});
								executiveTaskSubmission.dynamicDocuments[0].filledForms.push(filledForm);
								return true;
							} 
							$.each(formFormElement.formFormElements, function(index2, formFormElement2) {
												var elementId = formFormElement.form.pid
														+ "-"
														+ formFormElement2.formElementPid;
												if (formFormElement2.formElementTypeName == "textBox"
														|| formFormElement2.formElementTypeName == "numeric"
														|| formFormElement2.formElementTypeName == "textarea"
														|| formFormElement2.formElementTypeName == "dropdown"
														|| formFormElement2.formElementTypeName == "datePicker"
														|| formFormElement2.formElementTypeName == "yearPicker") {
													filledForm.filledFormDetails.push({
														id : 0,
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
													$
															.each(
																	$("input[name='"
																			+ elementId
																			+ "']:checked"),
																	function() {
																		filledForm.filledFormDetails
																				.push({
																					value : $(
																							this)
																							.val(),
																					formElementPid : formFormElement2.formElementPid,
																					formElementName : formFormElement2.formElementName
																				});
																	});
												}
											});
							executiveTaskSubmission.dynamicDocuments[0].filledForms
									.push(filledForm);
						});
		// clear validation message
		clearAndHideErrorBox();
		if(dynamicScript.length > 0 && typeof validateForm === "function") {
			var errors = validateForm(executiveTaskSubmission.dynamicDocuments[0].filledForms);
			if(errors.length > 0) {
				executiveTaskSubmission = {
						executiveTaskExecutionDTO : {
							activityPid : null,
							accountProfilePid : null
						},
						dynamicDocuments : [ {
							pid : null,
							employeePid :null,
							documentPid : null,
							filledForms : []
						} ]
					};
				var errMsg = "<li>";
				for (let err of errors) {
					errMsg = errMsg.concat(err.message + "</li><li>");
				}
				$(".alert > p").html(errMsg);
				$('.alert li:empty').remove();
				$('.alert').show();
				
				// enable submit button
				$('.btnSubmitForms').removeAttr('disabled');
		
				return;
			}
		}
		
		$.ajax({
			method : 'POST',
			url : otherVoucherTransactionContextPath + "/filled-forms",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(executiveTaskSubmission),
			success : function(data) {
				$('.btnSubmitForms').removeAttr('disabled');
				$("#divDynamicForms").hide();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	OtherVoucherTransaction.setvalue= function(documentpid , dynamicDocumentpid){
		documentPID=documentpid;
		dynamicDocumentPID=dynamicDocumentpid;
	}
	
	OtherVoucherTransaction.search = function() {
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			alert("Please select document and account");
			return;
		}
		clearAndHideErrorBox();
		$('#divDynamicForms').hide();
		$('#lblDocument').text($('#dbDocument option:selected').text());
		$('#lblAccount').text($('#dbAccount option:selected').text());
		$('#tblDynamicDocuments').html('<tr><td colspan="4">Please wait...</td></tr>');
		$
				.ajax({
					url : otherVoucherTransactionContextPath + "/search",
					type : 'GET',
					async:false,
					data : {
						documentPid : $("#dbDocument").val().split("~")[0],
						activityPid : $("#dbDocument").val().split("~")[1],
						accountPid : $("#dbAccount").val()
					},
					success : function(dynamicDocuments) {
						if (dynamicDocuments.length > 0) {
							$('#tblDynamicDocuments').html('');
							$.each(dynamicDocuments, function(key, dynamicDocument) {
								var printNames = dynamicDocument.printEmailDocumentNames.split(',');
								var options = '<option value="-1">Print Options</option>';
							    for(var i = 0; i < printNames.length; i++) {
							    	var name = printNames[i];
							    	if(name != "") {
							    		options += '<option value="'+ name +'">'+ name +'</option>'
							    	}
							    }
							    $("#sbPrint").html(options);
							    $("#sbEmailPrint").html(options);
							    
							    var chngeImage="$('#previewImage').attr('src','');$('#previewImage').attr('alt','');";
												$('#tblDynamicDocuments').append('<tr data-id="'
														+ dynamicDocument.pid
														+ '" data-parent=""><td>'
																		+ convertDateTimeFromServer(dynamicDocument.documentDate)
																		+ '</td><td>'
																		+ dynamicDocument.documentName
																		+ '</td><td>'
																		+ dynamicDocument.documentNumberLocal
																		+ '</td><td><button type="button" class="btn" onclick="OtherVoucherTransaction.editFilledForm(\''
																		+ dynamicDocument.pid
																		+ '\')"><i class="entypo-pencil" title="select"></i></button>&nbsp;<button  type="button" class="btn btn-blue" onclick="$(\'#searchModal\').modal(\'hide\');$(\'#printModal\').modal(\'show\');$(\'#hdnPrintDocPid\').val(\''+ dynamicDocument.pid + '\')"><i class="entypo-print" title="print"></i></button>'
																		+ '&nbsp;<button type="button" class="btn btn-orange" onclick="OtherVoucherTransaction.email(\''+ dynamicDocument.pid +","+ dynamicDocument.accountEmail+'\')"> <i class="entypo-mail" title="mail"></i></button>'
																		+ '&nbsp;<button type="button" class="btn btn-green" onclick="'+chngeImage+' $(\'#searchModal\').modal(\'hide\');$(\'#oploadImage\').modal(\'show\');OtherVoucherTransaction.uploadImage(\''+ dynamicDocument.documentPid+'\',\''+ dynamicDocument.documentName+'\',\''+ dynamicDocument.pid+'\')"> <i class="entypo-upload" title="upload"></i></button>'+ '</td></tr>');
												if (dynamicDocument.history != null) {
												$.each(dynamicDocument.history, function(index ,history) {
													var i = "-&nbsp;";
													if(index == 0){
														i += "&nbsp;";
													}
													$('#tblDynamicDocuments').append('<tr style="background: rgba(225, 225, 225, 0.66);" data-id="'
															+ index
															+ '" data-parent="'
															+ dynamicDocument.pid
															+ '"><td>'+i
																+ convertDateTimeFromServer(history.documentDate)
																+ '</td><td>'
																+ history.documentName
																+ '</td><td>'
																+ history.documentNumberLocal
																+ '</td><td>&nbsp;</td></tr>');
												});
												}
											});
							$('.collaptable').aCollapTable({
								startCollapsed : true,
								addColumn : false,
								plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
								minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
					});
							
						} else {
							$('#tblDynamicDocuments')
									.html(
											'<tr><td colspan="4">No data available</td></tr>');
						}
					}
				});
		$('#searchModal').modal('show');
	}

	var activity=null;
	var accountProfilePid=null;
	var employee=null;
	OtherVoucherTransaction.Account=function(){
		if ($("#dbDocument").val() == "no") {
			alert("Please select document");
			return;
		}
		employee=$("#dbEmployee").val();
		 activity= $("#dbDocument").val().split("~")[1];
		clearAndHideErrorBox();
		$('#tblAccountProfiles').html('<tr><td colspan="5">Please wait...</td></tr>');
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/load-account-profiles/" + activity,
			type : 'GET',
			success : function(accounts) {
				$('#tblAccountProfiles').html("");
				$.each(accounts, function(key, account) {
					$("#tblAccountProfiles").append("<tr><td>"
							+ account.name
							+ "</td><td>"
							+ account.address
							+ "</td><td>"
							+ account.email1
							+ "</td><td>"
							+ account.phone1
							+ "</td><td>"
							+'<button type="button" class="btn btn-danger" onclick="OtherVoucherTransaction.Select(\''
																		+ account.pid
																		+ '\');">select</button></td></tr>');
					
				});
			}
		});
		$('#addAccountModal').modal('show');
	}
	
	function searchTable(inputVal) {
		var table = $('#tblAccountProfiles');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}
	var dynamicDocumentPid="";
	OtherVoucherTransaction.uploadImage=function(docPid,docName,ddPid){
		changeSpan();
		$("#showImages").html("");
		dynamicDocumentPid=ddPid;
		$("#lbl_docName").text($('#dbDocument option:selected').text());
		$("#dbForm").html("<option>Forms loading...</option>")
		$.ajax({
			url : otherVoucherTransactionContextPath + "/load-forms/" + dynamicDocumentPid,
			type : 'GET',
			success : function(forms) {
				$("#dbForm").html("<option value='no'>Select Form</option>")
				$.each(forms, function(key, form) {
					$("#dbForm").append("<option value='" + form.pid + "'>" + form.name + "</option>");
				});
			}
		});
	}
	
	function removeImage(){
		$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var imagePid =$("#previewImage").attr("alt");
		var image=$('#field_image')[0].files[0];
		var formPid=$("#dbForm").val();
		if(imagePid=="" && image==null){
				alert("please select image");
				$("#divImgeUpload").html("");
				return;
		}
		if(imagePid=="" && image!=null){
			$('#previewImage').attr('src','');
			$('#previewImage').attr('alt','');
			$("#divImgeUpload").html("");
			changeSpan();
			return;
		}
		$('#previewImage').attr('src','');
		$('#previewImage').attr('alt','');
		$("#"+imagePid).remove(); 
		$("#divImgeUpload").html("");
		changeSpan();
			$.ajax({
				url : otherVoucherTransactionContextPath + "/delete-image",
				type : 'GET',
				data:{
					filePid : imagePid,
					dynamicDocumentPid : dynamicDocumentPid,
					formPid : formPid,
				},
				success : function(datas) {
					$("#divImgeUpload").html("");
				},
				error : function(xhr, error) {
					$("#divImgeUpload").html("");
					onError(xhr, error);
				}
			});
		
	}
	
	
	function uploadFilledFormImage() {
	
	var ImgFilePid= $('#previewImage').attr("alt")	
	var formPid=$("#dbForm").val();
		
	if(formPid==null || formPid=="no"){
		alert ("please select Form");
		return;
	}
	var image=$('#field_image')[0].files[0];
	if(image == null && ImgFilePid ==""){
		alert ("please select image");
		return;
	}
	
	if(image == null && ImgFilePid !=""){
		$('#previewImage').attr('src','');
		$('#previewImage').attr('alt','');
		changeSpan();
		return;
	}
	
	if(image != null && ImgFilePid !=""){
		uploadEditedImageFile(image,dynamicDocumentPid,formPid,ImgFilePid);
	}
	else{
		uploadNewImageFile(image,dynamicDocumentPid,formPid);
	}
	}
	
	function uploadEditedImageFile(image,dynamicDocumentPid,formPid,ImgFilePid){
		$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var uploadImage = new FormData();
		uploadImage.append("file", image);
		uploadImage.append('dynamicDocumentPid', dynamicDocumentPid);
		uploadImage.append('formPid', formPid);
		uploadImage.append('ImgFilePid', ImgFilePid);
		
			$.ajax({
				type : 'POST',
				url : otherVoucherTransactionContextPath + "/upload/filled-form-edit-image",
				data :uploadImage,
				cache : false,
				contentType : false,
				processData : false,
				success : function(data) {
					$("#divImgeUpload").html("");
					$.each(data, function(key, filePid) {
						$("#"+filePid).remove(); 
						var imgesrc=$("#previewImage").attr('src');
						$("#showImages").append('<div id=div'+filePid+'"; class="col-md-4"><img onclick="OtherVoucherTransaction.editImage(\''+ filePid +'\');" style="width: 200px;" id="'+filePid+'"; class="img-thumbnail" src="' + imgesrc + '"/></div><br/>');
						$('#previewImage').attr('src','');
						$('#previewImage').attr('alt','');
						changeSpan();
						});
				},
				error : function(xhr, error) {
					$("#divImgeUpload").html("");
					onError(xhr, error);
				}
			});
		
	}
	
function uploadNewImageFile(image,dynamicDocumentPid,formPid){
	$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var uploadImage = new FormData();
		uploadImage.append("file", image);
		uploadImage.append('dynamicDocumentPid', dynamicDocumentPid);
		uploadImage.append('formPid', formPid);
		
		$.ajax({
			type : 'POST',
			url : otherVoucherTransactionContextPath + "/upload/filled-form-image",
			data :uploadImage,
			cache : false,
			contentType : false,
			processData : false,
			success : function(data) {
				$("#divImgeUpload").html("");
				$.each(data, function(key, filePid) {
				var imgesrc=$("#previewImage").attr('src');
				$("#showImages").append('<div id=div'+filePid+'";class="col-md-4"><img onclick="OtherVoucherTransaction.editImage(\''+ filePid +'\');" style="width: 200px;" id="'+filePid+'"; class="img-thumbnail" src="' + imgesrc + '"/></div><br/>');
				$('#previewImage').attr('src','');
				$('#previewImage').attr('alt','');
				changeSpan();
				});
			},
			error : function(xhr, error) {
				$("#divImgeUpload").html("");
				onError(xhr, error);
			}
		});
		
	}
	
	function getFilledFormImages(){
		$('#previewImage').attr('src','');
		$('#previewImage').attr('alt','');
		changeSpan();
		$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var formPid=$("#dbForm").val();
		$.ajax({
			url : otherVoucherTransactionContextPath + "/upload/filled-form-image",
			type : 'GET',
			data :{
				dynamicDocumentPid : dynamicDocumentPid,
				formPid : formPid,
			},
			success : function(forms) {
				$("#showImages").html('');
				if(forms!=null && forms.files != null){
					$.each(forms.files, function(key, form) {
						$("#showImages").append('<div id=div'+form.filePid+'"; class="col-md-4"><img onclick="OtherVoucherTransaction.editImage(\''+ form.filePid +'\');" style="width: 200px;" id="'+form.filePid+'"; class="img-thumbnail" src="data:image/png;base64,' + form.content + '"/></div><br/>');
					});
				}
				$("#divImgeUpload").html("");
			},
			error : function(xhr, error) {
				$("#divImgeUpload").html("");
				onError(xhr, error);
			}
		});
	}
	
	OtherVoucherTransaction.editImage=function(filePid){
		var imgesrc=$("#"+filePid).attr('src');
		$('#previewImage').attr('src',imgesrc);
		$('#previewImage').attr('alt',filePid);
		changeSpan();
	}
	
	OtherVoucherTransaction.Select=function(pid){
		accountProfilePid=pid;
		$('#addAccountModal').modal('hide');
		loadAccountProfileByActivityId(activity);
	}
	OtherVoucherTransaction.editFilledForm = function(dynamicDocumentPid) {
		// load forms
		OtherVoucherTransaction.loadForms();
		// load filled data
		$.ajax({
			url : otherVoucherTransactionContextPath + "/filled-forms/"
					+ dynamicDocumentPid,
			method : 'GET',
			success : function(dynamicDocument) {
				executiveTaskSubmission.dynamicDocuments[0].pid = dynamicDocument.pid;
				$.each(dynamicDocument.filledForms, function(key, filledForm) {
					$("#hdn"+ filledForm.formPid).val(filledForm.pid);
					if(filledForm.multipleRecord) {
						var tbl = $('#tbl'+ filledForm.formPid +'');
						var tblBody = $(tbl).find('tbody');
						var tblHead = $(tbl).find('thead > tr > th')
						var thCount = $(tblHead).length;
						// create a row
						var tblRow = $('<tr></tr>');
						for(var i = 0; i < thCount-1;i ++) {
							$(tblRow).append("<td></td>");
						}
						$(tblRow).append('<td><button type="button" onclick="OtherVoucherTransaction.multipleRecordFormEditRowClicked(this)" class="btn btn-blue btn-xs">Edit</button>&nbsp;&nbsp;<button type="button" onclick="OtherVoucherTransaction.multipleRecordFormDeleteRowClicked(this)" class="btn btn-danger btn-xs">Delete</button></td>');
						var limit = 0;
						$(tblBody).append(tblRow);
						$.each(filledForm.filledFormDetails, function(index,filledFormDetail) {
							limit +=1;
							if(limit < thCount) {
								for(var i = 0; i < thCount;i++) {
									var $th = $(tbl).find('th').eq(i);
									if($th.text() == filledFormDetail.formElementName) {
										var td = $(tblBody).find("tr").last().find('td').eq(i);
										$(td).attr("id", filledFormDetail.id);
										$(td).text(filledFormDetail.value);
										break;
									}
								}
							} else {
								limit = 1
								var row = $('<tr></tr>');
								for(var j = 0; j < thCount-1;j++) {
									$(row).append("<td></td>");
								}
								$(row).append('<td><button type="button" onclick="OtherVoucherTransaction.multipleRecordFormEditRowClicked(this)" class="btn btn-blue btn-xs">Edit</button>&nbsp;&nbsp;<button type="button" onclick="OtherVoucherTransaction.multipleRecordFormDeleteRowClicked(this)" class="btn btn-danger btn-xs">Delete</button></td>');
								$(tblBody).append(row);
								for(var k = 0; k < thCount;k++) {
									var $th = $(tbl).find('th').eq(k);
									if($th.text() == filledFormDetail.formElementName) {
										var td = $(tblBody).find("tr").last().find('td').eq(k);
										$(td).attr("id", filledFormDetail.id);
										$(td).text(filledFormDetail.value);
										break;
									}
								}
							}
						});
						return true; // continue next iteration
					}
					$.each(filledForm.filledFormDetails, function(index,filledFormDetail) {
						var elementId = filledForm.formPid+ "-"+ filledFormDetail.formElementPid;
						if (filledFormDetail.formElementType == "textBox"
							|| filledFormDetail.formElementType == "numeric"
							|| filledFormDetail.formElementType == "textarea"
							|| filledFormDetail.formElementType == "dropdown"
							|| filledFormDetail.formElementType == "datePicker"
							|| filledFormDetail.formElementType == "yearPicker") {
							$("#"+ elementId).val(filledFormDetail.value);
						} else if (filledFormDetail.formElementType == "radioButton") {
							$("input[name='"+ elementId+ "'][value='" + filledFormDetail.value + "']").prop('checked', true);
						} else if (filledFormDetail.formElementType == "checkBox") {
							$("input:checkbox[name='"+ elementId+ "'][value='"+ filledFormDetail.value + "']").prop("checked", true);
						}
					});
				});
				$('#divDocumentNumber').text(dynamicDocument.documentNumberLocal);
				// post loaded dynamic function
				afterEditFilledFormLoaded();
			}
		});
		$('#searchModal').modal('hide');
	}
	
	function afterEditFilledFormLoaded() {
		if (typeof postFormCompleteEvent === "function") { 
		    // safe to use the function
			postFormCompleteEvent();
		}
	}
	
	function changeSpan(){
		if($("#previewImage").attr('src')!=''){
			$("#btnSltImg").text("Update Image");
		}else{
			$("#btnSltImg").text("Select Image");
		}
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = otherVoucherTransactionContextPath;
	}

	var formFormElementList = [];
	OtherVoucherTransaction.loadForms = function() {
		executiveTaskSubmission.dynamicDocuments[0].pid = null;
		executiveTaskSubmission.dynamicDocuments[0].filledForms =  [];
		if ($("#dbDocument").val() == "no" || $("#dbAccount").val() == "no") {
			alert("Please select dbDocument and dbAccount");
			return;
		}
		
		// set headers
		setDocumentNameAndPreviousDocumentNumber();
		
		$('#divForms').html("Please wait...");
		$
				.ajax({
					async : false,
					url : otherVoucherTransactionContextPath + "/load-forms",
					type : 'GET',
					data : {
						documentPid : $("#dbDocument").val().split("~")[0],
						accountPid : $("#dbAccount").val()
					},
					success : function(formFormElementMap) {
						$('#divForms').html("");
						console.log(formFormElementMap);
						var divForms = ""
						formFormElementList = [];
						$.each(formFormElementMap,function(formKey, formFormElements) {
											var form = JSON.parse(formKey);
											formFormElementList.push({
														form : form,
														formFormElements : formFormElements
											});
											if (formFormElements.length > 0) {
												// append tab content
												divForms +='<div class="panel panel-primary" data-collapsed="0"><div class="panel-heading"><div class="panel-title" style="font-weight: bold;">'+form.name+'</div>'
														       +' <div class="panel-options">'
														       +'<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a></div></div>'
														       +'<div class="panel-body"><form role="form" class="form-horizontal form-groups-bordered" name="'+ form.pid +'">';
												
												// add input hidden for keep
												// filled form pid
												divForms += '<input type="hidden" id="hdn'+form.pid+'" />';
												
												var tblThNames = ""; // for
																		// multiple
																		// record
																		// forms
												// append form elements
												$.each(formFormElements, function(key,formFormElement) {
																	var visibility = "display: block";
																	if (!formFormElement.visibility) {
																		visibility = "display: none";
																	}
																	divForms +='<div class="form-group" style="'+ visibility +'">'
																			  +'<label for="field-1" class="col-sm-3 control-label">'+formFormElement.formElementName+'</label>'
																			  +'<div class="col-sm-5">';
																	
																	var formElementType = formFormElement.formElementTypeName;
																	var formElementValues = formFormElement.formElementValues;
																	var elementId = form.pid + "-" + formFormElement.formElementPid;
																	tblThNames += '<th id="'+ elementId +'" elemId="'+ formFormElement.formElementPid +'" name="'+ formFormElement.formElementName +'">'+ formFormElement.formElementName +'</th>';
																	var readOnly = "";
																	if (!formFormElement.editable) {
																		readOnly = "readonly='readonly'";
																	}
																	
																	if (formElementType == "textBox") {
																		divForms += "<input id='"+ elementId + "' type=\"text\" class=\"form-control\" name='"
																				+ formFormElement.formElementName
																				+ "'  " + readOnly+ "  />";
																	} else if (formElementType == "numeric") {
																		divForms += "<input id='"+ elementId + "' type=\"number\" min='0' class=\"form-control\" name='"
																		+ formFormElement.formElementName
																		+ "'  " + readOnly+ "  />";
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
																			$.each(formElementValues, function(key,formElementValue) {
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
																			$.each(formElementValues, function(key, formElementValue) {
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
												// add multiple record option
												// add a table
												if(form.multipleRecord){
													tblThNames += '<th>Action</th>';
													divForms += "<input type='button' onclick='OtherVoucherTransaction.multipleRecordFormAddRowClicked(this, \"" + form.pid + "\")' class='btn btn-info' value='Add Row' style='float: right;'>";
													divForms += "<table id='tbl"+ form.pid +"' class ='table table-bordered mulRec'>";
													divForms +="<thead><tr>"+ tblThNames +"</tr></thead><tbody></tbody></table>";
												}
												divForms += "</form></div></div>";
											}
											// load dynamic script
											if(form.jsCode) {
												dynamicScript.push(form.jsCode);
											}
										});
						$('#divForms').html(divForms);
					/*
					 * $('#divlabel').html(divlabel);
					 * $('#divsubmit1').html(divsubmit1);
					 * $('#divsubmit2').html(divsubmit2);
					 */
						$('#divDynamicForms').show();

						$('.datePicker').datepicker();

						$('.yearPicker')
								.datepicker(
										{
											changeMonth : false,
											changeYear : true,
											dateFormat : 'yy',
											onChangeMonthYear : function(y, m,
													i) {
												var year = $(
														"#ui-datepicker-div .ui-datepicker-year :selected")
														.val();
												$(this).datepicker('setDate',
														new Date(year, 0, 1));
												$(this).datepicker("hide");
											}
										});
						loadDynamicScript();
					}
					
				});
	}
	
	OtherVoucherTransaction.multipleRecordFormAddRowClicked = function(elem, formPid) {
		var obj = $('form[name="'+ formPid +'"]')[0];
		var markup = '<tr>';
		var rowNum = $(elem).next("table.mulRec").attr("selectedrow");
		// if update, remove and add
		if(rowNum > 0) {
			rowNum = rowNum-1;
			$(elem).next("table.mulRec").find("tbody").find("tr:eq('"+ rowNum +"')").remove();
		}
		$(elem).next("table.mulRec").attr("selectedrow", '');
		$($(obj).prop('elements')).each(function(){
		    if (this.type === "text" || this.type === "textarea") {
		    	markup += '<td>'+ this.value +'</td>'
				this.value = "";
		    }
		});
		markup += '<td><button type="button" onclick="OtherVoucherTransaction.multipleRecordFormEditRowClicked(this)" class="btn btn-blue btn-xs">Edit</button>&nbsp;&nbsp;'
		markup += '<button type="button" onclick="OtherVoucherTransaction.multipleRecordFormDeleteRowClicked(this)" class="btn btn-danger btn-xs">Delete</button></td>'
		markup += '</tr>';
		$(elem).next("table.mulRec").find("tbody").append(markup);
		
		// if dynamic script
		if(dynamicScript.length > 0) {
			calculateTotalAmountAndShow(elem);
		}
	}
	
	OtherVoucherTransaction.multipleRecordFormEditRowClicked = function(elem) {
		$(elem).parents("tr").find("td").each(function() {
			var elementId = $(this).closest('table').find('th').eq($(this).index()).attr("id");
			$("#"+ elementId).val($(this).html());
		}); 
		$(elem).parents('table').attr('selectedrow', $(elem).parents("tr")[0].rowIndex);
	}
		
	OtherVoucherTransaction.multipleRecordFormDeleteRowClicked = function(elem) {
		if(confirm("Are you sure?")){
			$(elem).parents("tr").remove();
		}
		
	}
	function setDocumentNameAndPreviousDocumentNumber() {
		$('#divDocumentNumber').text("---");
		$('#divDocumentName').text($('#dbDocument option:selected').text());
		
		var documentPid=$('#dbDocument option:selected').val().split("~");
		// get previous document number
		$.ajax({
			url : otherVoucherTransactionContextPath+ "/previous-document-number",
			type : 'GET',
			data :{
				documentPid : documentPid[0]
				},
			success : function(previousDocumentNumber) {
				$('#divPreviousDocumentNumber').text(previousDocumentNumber);
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

	function loadAccountProfileByActivityId(activityPid) {
		$("#dbAccount").html("<option>Accounts loading...</option>")
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/load-account-profiles/" + activityPid,
			type : 'GET',
			async:false,
			success : function(accounts) {
				$("#dbAccount").html(
						"<option value='no'>Select Account</option>")
				$.each(accounts, function(key, account) {
					$("#dbAccount").append(
							"<option value='" + account.pid + "'>"
									+ account.name + "</option>");
					
						if(accountProfilePid!=null){
						if(accountProfilePid==account.pid){
						$("#dbAccount").val(account.pid);
						}
						}else{
							$("#dbAccount").val(account.pid);
						}
					
				});
			}
		});
	}
	
	
	var accountProfileModel = {
			pid : null,
			name : null,
			alias : null,
			accountTypePid : null,
			city : null,
			phone1 : null,
			email1 : null,
			address : null,
		};
	
	function saveAccountprofile(){
		 if ($('#field_name').val() == "") {
		        alert("Name must be filled out");
		        return ;
		    }
		 if ($('#field_city').val() == "") {
		        alert("City must be filled out");
		        return ;
		    }
		 if ($('#field_phone1').val() == "") {
		        alert("Phone must be filled out");
		        return ;
		    }
		 if ($('#field_email1').val() == "") {
		        alert("Mail must be filled out");
		        return ;
		    }
		 if ($('#field_address').val() == "") {
		        alert("Address must be filled out");
		        return ;
		    }
		accountProfileModel.name = $('#field_name').val();
		accountProfileModel.alias = $('#field_name').val();
		accountProfileModel.accountTypePid = $('#field_accountType').val();
		accountProfileModel.city = $('#field_city').val();
		accountProfileModel.phone1 = $('#field_phone1').val();
		accountProfileModel.email1 = $('#field_email1').val();
		accountProfileModel.address = $('#field_address').val();
		$.ajax({
			type : 'POST',
			url : otherVoucherTransactionContextPath + "/saveAccountProfile/" + employee,
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountProfileModel),
			success : function(data) {
            onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	
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