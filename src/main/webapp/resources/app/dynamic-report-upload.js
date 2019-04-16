if (!this.DynamicReportUpload) {
	this.DynamicReportUpload = {};
}

(function() {
	'use strict';

	var dynamicReportUploadContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#dynamicReportUploadForm");
	var deleteForm = $("#deleteForm");
	var dynamicReportUploadModel = {
		id:null,
		name : null,
		overwrite : null
		
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateDynamicReportUpload(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDynamicReportUpload(e.currentTarget.action);
		});
		
		$('#assignAccountColumnNumbers').on('click', function() {
			assignAccountColumnNumbers();
		});
		
	
	});

	
	function createUpdateDynamicReportUpload(el) {
		dynamicReportUploadModel.name = $('#field_name').val();
		console.log(dynamicReportUploadModel.id);
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(dynamicReportUploadModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	var dynamicReportNameId=null;
	function assignAccountColumnNumbers() {
		var file=$('#uploadFile')[0].files[0];
		if ($('#uploadFile').val()=="") {
			$(".alert").html("please select file");
			$('.alert').show();
			return false;
		}

		var validExts = new Array(".xlsx", ".xls");
		var fileExt = $('#uploadFile').val();
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if (validExts.indexOf(fileExt) < 0) {
			$(".alert").html("Invalid file selected, valid files are  "
					+ validExts.toString() + " types.");
			$('.alert').show();
			return false;
		}
		$('#assignAccountColumnNumbers').prop("disabled", true);
		$('#uploadId').css("display", "block");
		var formData = new FormData();
		formData.append('file', $('#uploadFile')[0].files[0]);
		
		$.ajax({
			method : 'POST',
			url : dynamicReportUploadContextPath +"/upload-file/"+dynamicReportNameId ,
			contentType : "application/json; charset=utf-8",
			data : formData,
			processData : false, // tell jQuery not to process the data
			contentType : false, // tell jQuery not to set contentType
			success : function(data) {
				$('#assignAccountColumnNumbers').removeAttr('disabled');
				$('#uploadId').css("display", "none");
				$('#upload').modal("hide");
				$('#alertMessage').html(data);
				$('#alertBox').modal("show");
				
			},
			error : function(xhr, error) {
				$('#assignAccountColumnNumbers').removeAttr('disabled');
				$('#uploadId').css("display", "none");
				onError(xhr, error);
			}
		});
	}
	
	function deleteDynamicReportUpload(actionurl, id) {
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
		window.location = dynamicReportUploadContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = dynamicReportUploadContextPath;
	}

  function	editDynamicReportUpload(id){
	  $.ajax({
		 url:dynamicReportUploadContextPath+"/getDynamicReportUpload" ,
		 type:'GET',
		 data:{
			 dynamicReportUploadId:id,
		 },
		 success:function(result){
			$('#field_name').val(result.name);
			
			dynamicReportUploadModel.id=result.id;
		 },
		 error:function(xhr, error){
			 onError(xhr, error);
		 },
	  });
	  
  }
	
	DynamicReportUpload.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				dynamicReportNameId=id;
				
				break;
			case 1:
				editDynamicReportUpload(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dynamicReportUploadContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	DynamicReportUpload.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		dynamicReportUploadModel.pid = null; // reset bank model;
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