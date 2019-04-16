// Create a LeadManagement object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.LeadToCashReportConfig) {
	this.LeadToCashReportConfig = {};
}

(function() {
	'use strict';
	
	var leadToCashReportConfigContextPath = location.protocol + '//' + location.host
	+ location.pathname;
/*	var createEditForm = $("#leadToCashReportConfigForm");
	var deleteForm = $("#deleteForm");*/
	var leadToCashReportConfigModel = {
			pid : null,
			name : null,
			sortOrder : 0,
			stagetPid : null,
			stageName : null,
		};

	
	$(document).ready(function() {
		

		LeadToCashReportConfig.loadLeadToCashReportConfig();
		
	});
	
	LeadToCashReportConfig.loadLeadToCashReportConfig = function() {
		
		$
		.ajax({
			url : leadToCashReportConfigContextPath + "/load",
			method : 'GET',
			success : function(leadToCashReportConfigs) {
				addTableBodyValues(leadToCashReportConfigs);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	
	function addTableBodyValues(leadToCashReportConfigs) {
		console.log(leadToCashReportConfigs);
		for(var i=1; i <= leadToCashReportConfigs.length; i++) {
			var sortOrder = leadToCashReportConfigs[i-1].sortOrder
			$('#stageCol'+sortOrder).val(leadToCashReportConfigs[i-1].stagetPid);
		}
	}
	
	
	LeadToCashReportConfig.saveConfig = function(stageId) {
		if($('#'+stageId).val() == '-1'){
			alert("Select a stage");
			return;
		}
		if(stageId == "stageCol1") {
			leadToCashReportConfigModel.name = "COLUMN1";
			leadToCashReportConfigModel.sortOrder = 1;
			leadToCashReportConfigModel.stagetPid = $('#stageCol1').val();
		}
		if(stageId == "stageCol2") {
			leadToCashReportConfigModel.name = "COLUMN2";
			leadToCashReportConfigModel.sortOrder = 2;
			leadToCashReportConfigModel.stagetPid = $('#stageCol2').val();
		}
		if(stageId == "stageCol3") {
			leadToCashReportConfigModel.name = "COLUMN3";
			leadToCashReportConfigModel.sortOrder = 3;
			leadToCashReportConfigModel.stagetPid = $('#stageCol3').val();
		}
		if(stageId == "stageCol4") {
			leadToCashReportConfigModel.name = "COLUMN4";
			leadToCashReportConfigModel.sortOrder = 4;
			leadToCashReportConfigModel.stagetPid = $('#stageCol4').val();
		}
		console.log(leadToCashReportConfigModel);
		$.ajax({
			method : 'POST',
			url : leadToCashReportConfigContextPath,
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(leadToCashReportConfigModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		
	}
	

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = leadToCashReportConfigContextPath;
	}

	
	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		leadToCashReportConfigModel.pid = null; // reset accountProfile model;
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
