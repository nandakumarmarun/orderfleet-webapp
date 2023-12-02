// Create a TaskExecutionSaveOffline object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserMobileConfiguration) {
	this.UserMobileConfiguration = {};
}

(function() {
	'use strict';

	var userWiseMobileConfigurationDTO = {
		pid : null,
		companyPid : null,
		companyName : null,
		userPid : null,
		userName : null,
		liveRouting :false
	};

	$(document).ready(function() {
      console.log("*************************************")
		$('.selectpicker').selectpicker();
		$('#dbCompany').on('change', function() {
		console.log("Enter in on change")
        			var companyid = $('#dbCompany').val();
        			loadUserByCompanyId(companyid);
        		});
        		$('#dbUsers').on('change', function() {
                			getAssignedConfigurationConfig($('#dbUsers').val());
                		});

		$('#mobilesModal').on('click', function() {
			$("#assignMobileConfigurationsModal").modal("show");
			$("#dbCompany").val("-1");
			$("#dbCompany").prop("disabled", false);
		});

		$('#btnSaveMobileConfigurations').on('click', function() {
			saveMobileConfiguration();
		});

	$('#btnDelete').on('click', function() {
			deleteMobileConfiguration();
		});
	});

	var contextPath = location.protocol + '//' + location.host
			+ location.pathname;
          $('#dbCompanys').on('change', function() {
		     console.log("Enter in on change")
        			var companyid = $('#dbCompanys').val();
        			loadUserByCompanyId(companyid);
        		});
             function getAssignedConfigurationConfig(userPid){
                    $('input[type="checkbox"]:checked').prop('checked', false);
$
				.ajax({
					url : contextPath + "/" + userPid,
					method : 'GET',
					success : function(data) {
						$('#liveRouting').prop("checked",data.liveRouting);
					},
					error : function(xhr, error) {
                    						onError(xhr, error);
                    					}
               });
               }
function loadUserByCompanyId(companyid) {
console.log("Eneter here................")
		$("#dbUser").html("<option>Users loading...</option>")
		$.ajax({
			url : contextPath + "/loadUsers/"
					+ companyid,
			type : 'GET',
			success : function(users) {
				$("#dbUser").html("<option value='no'>Select User</option>")
				$.each(users, function(key, user) {
					$("#dbUser").append(
							"<option value='" + user.pid + "'>" + user.login
									+ "</option>");
				});

                				$.each(users, function(key, user) {
                					$("#dbUsers").append(
                							"<option value='" + user.pid + "'>" + user.login
                									+ "</option>");
                				});
			}
		});
	}
	function saveMobileConfiguration() {

		if ($("#dbCompanys").val() == -1) {
			alert("Please select company");
			return;
		}
		console.log("Company :"+$("#dbCompany").val());
		userWiseMobileConfigurationDTO.companyPid = ($("#dbCompanys").val());
		userWiseMobileConfigurationDTO.userPid = ($("#dbUsers").val());
		userWiseMobileConfigurationDTO.liveRouting = $("#liveRouting").is(
                                                    		":checked");;
		console.log("userWiseMobileConfigurationDTO :"+userWiseMobileConfigurationDTO);
		$.ajax({
			url : contextPath,
			method : 'POST',
			contentType : "application/json; charset:utf-8",
			data : JSON.stringify(userWiseMobileConfigurationDTO),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	UserMobileConfiguration.getMobileConfigurationConfig = function() {
		$
				.ajax({
					url : contextPath + "/" + $("#dbUser").val(),
					method : 'GET',
					success : function(data) {

					$('#tblConfig').html("");
                  		$('#tblConfig')
                    		.append(
                    									"<tr><td>"
                                                     	+ data.userName
                    									+ "</td><td>"
                    									+ data.companyName
                    									+ "</td><td>"
                    									+ data.liveRouting
                    									+"</td><td><button type='button' class='btn btn-danger' onclick=\"UserMobileConfiguration.deleteConfig('" + data.pid + "');\">Delete</button>"
                    									+"</td></tr>");
                    											},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}
	var mobileConfigPid = "";
	UserMobileConfiguration.deleteConfig = function(pid) {
		mobileConfigPid = pid;
		$('#alertMessage').html("Are You Sure...?");
		$('#alertBox').modal("show");
	}

	function deleteMobileConfiguration() {
		$.ajax({
			url : contextPath + "/delete/" + mobileConfigPid,
			method : 'DELETE',
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
		window.location = contextPath;

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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath;
	}

	UserMobileConfiguration.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		syncOperationModel.pid = null; // reset syncOperation model;
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

})();