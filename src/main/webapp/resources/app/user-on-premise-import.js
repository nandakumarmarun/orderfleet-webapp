// Create a UserOnPremiseImport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserOnPremiseImport) {
	this.UserOnPremiseImport = {};
}

(function() {
	'use strict';
	
	var ContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	
	$(document).ready(function() {
		$('#loadingDiv').hide();
		$('#btnImportUsers').click(function() {
			saveUsersFromServer();
		});
	});
	
	UserOnPremiseImport.showCompanyUrl = function(){
		$('#errorMessage').text("");
		var pid = $('#field_company').val();
		
		$.ajax({
			url : ContextPath + "/" + pid,
			method : 'GET',
			success : function(data) {
				$('#field_url').val(data.apiUrl);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		
		loadUsers();
		
	}
	
	
	
	UserOnPremiseImport.updateCompanyApi = function() {
		var api = $('#field_url').val();
		if(api == ""){
			 $('#errorMessage').text("*This field is required.");
			return;
		}
		var id = $('#field_company').val();
		var companyView = {
				pid : id,
				legalName : name,
				apiUrl : api
		}
		
		$.ajax({
			url : ContextPath + "/companyUpdate",
			method : 'PUT',
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(companyView),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	function saveUsersFromServer(){
		var pid = $('#field_company').val();
		$('#loadingDiv').show();
		$.ajax({
			url : ContextPath + "/import-users/" + pid,
			method : 'GET',
			success : function(data) {
			$('#loadingDiv').hide();
			$('#tblUserDetails').html("<tr><td colspan='13' align='center'>Please wait...</td></tr>");
				UserOnPremiseImport.showCompanyUrl();
			},
			error : function(xhr, error) {
				$('#loadingDiv').hide();
				onError(xhr, error);
			}
		});
	}
	
	function loadUsers(){
		var pid = $('#field_company').val();
		
		$.ajax({
			url : ContextPath + "/load-user/" + pid,
			method : 'GET',
			success : function(data) {
				
				var userList ="";
				$.each(data,function(index, user) {
					userList += "<tr><td>"+user+"</td></tr>";
				});
				$('#tblUserDetails').html(userList);
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = ContextPath;
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
	
	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}
	
})();