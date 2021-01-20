// Create a BestPerformer object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.BestPerformer) {
	this.BestPerformer = {};
}

(function() {
	'use strict';

	var bestPerformanceContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var bestPerformanceForm = $("#bestPerformanceForm");
	
	var bestPerformanceModel = {
		pid : null,
		logo : null, 
		logoContentType : null
	};

	// Specify the validation rules
	var validationRules = {
		logo : {
			required : true
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		logo : {
			required : "This field is required."
		}
	};

	$(document).ready(function() {
		bestPerformanceForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateBestPerformance(form);
			}
		});
		let pid = $('#bestPerformerPid').val();
		getBestFormer(pid);
	});
	
	
	function getBestFormer(id) {
		$('#bestPerformerImage').attr('src', '');
		$.ajax({
			url : bestPerformanceContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				if (data.logo != null) {
					$('#bestPerformerImage').attr('src',
							'data:image/png;base64,' + data.logo);
				}
				// set image byte array
				bestPerformanceModel.logo = data.logo;
				// companyModel.logo = base64Data;
				bestPerformanceModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	
	function createUpdateBestPerformance(el) {
		let methodName = '';
		if (bestPerformanceModel.pid != '') {
			methodName = "post";
		} else {
			methodName = "put";
		}
		$.ajax({
			method : methodName,
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(bestPerformanceModel),
			success : function(data) {
				onSaveSuccess(data);

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	$('#bestPerformerImageInput').on(
			'change',
			function() {
				var file = $(this)[0].files[0]; // only one file exist
				var fileReader = new FileReader();
				fileReader.readAsDataURL(file);

				fileReader.onload = function(e) {
					$('#previewImage').attr('src', fileReader.result);
					$('#previewImage').css({ 'display' : ''});
					var base64Data = e.target.result.substr(e.target.result
							.indexOf('base64,')
							+ 'base64,'.length);
					bestPerformanceModel.logo = base64Data;
					bestPerformanceModel.logoContentType = file.type;
				};

			});


	
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = bestPerformanceContextPath;
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