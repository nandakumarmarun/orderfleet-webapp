if (!this.CompanyDeactiveUsersCount) {
	this.CompanyDeactiveUsersCount = {};
}

(function() {
	'use strict';

	$(document).ready(function() {

		$('#btnload')
		.on(
				'click',
				function() {
					if ($('#dbCompany').val() == "no") {
						alert("Please select Company");
						return;
					}
					getActivatedUser();	
				});

		$('#btnloadAll')
		.on(
				'click',
				function() {
					
					getAllCompaniesUser();	
				});
		
		
		$('.selectpicker').selectpicker();
	});

	var companyDeactiveUsersCountContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	
	
	


	 function getActivatedUser()
	{
		 
		var companyid = $('#dbCompany').val();
		$.ajax({
			url : companyDeactiveUsersCountContextPath + "/loadUserCount",
			type : "GET",
			data : {
				companyId : companyid
				},
				
		success : function(usercount) {
			 $('#tableCompanyUserCount').html("");
			 if(usercount.length==0)
				 {
				 $('#tableCompanyUserCount')
					.html(
							"<tr><td colspan='6' align='center'>No data available</td></tr>");
			return;
				 }
			 $.each(usercount,
					 function(index,userscount){
				 var content = "<tr><td>"
					           +userscount.companyName
					           +"</td><td>"
					           +userscount.userCount
					           +"</td><td>";
				 $('#tableCompanyUserCount').append(content);	
			 });
				
				console.log("finished");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});

	}
	 function getAllCompaniesUser()
		{
			 
			
			$.ajax({
				url : companyDeactiveUsersCountContextPath + "/loadAll",
				type : "GET",
					
			success : function(companyUser) {
				 $('#tableCompanyUserCount').html("");
				 if(companyUser.length==0)
					 {
					 $('#tableCompanyUserCount')
						.html(
								"<tr><td colspan='6' align='center'>No data available</td></tr>");
				return;
					 }
				 $.each(companyUser,
						 function(index,companyuser){
					 var content = "<tr><td>"
						           +companyuser.companyName
						           +"</td><td>"
						           +companyuser.userCount
						           +"</td><td>";
					 $('#tableCompanyUserCount').append(content);	
				 });
					
					console.log("finished");
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});

		}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = companyDeactiveUsersCountContextPath;
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

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
})();