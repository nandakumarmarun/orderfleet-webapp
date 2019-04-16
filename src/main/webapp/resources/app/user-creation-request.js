// Create a UserCreationRequest object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserCreationRequest) {
	this.UserCreationRequest = {};
}

(function() {
	'use strict';
	
	var ContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	
	UserCreationRequest.createUsers = function(pid,count,id) {
		
		var adminUserExist = false;
		$.ajax({
			url : ContextPath + '/admin-users-exist',
			method : 'GET',
			data : {
				companyPid : pid
			},
			success : function(data) {
				console.log(data);
				console.log(data == 0);
				if(data == 0 ){
					adminUserExist = false;
				}else{
					adminUserExist =  true;
				}
				
				console.log(adminUserExist);
				if(adminUserExist) {
					$.ajax({
						url : ContextPath + '/create-users',
						method : 'GET',
						data : {
							companyPid : pid,
							usersCount : count,
							id : id
						},
						success : function(data) {
							onSaveSuccess(data);
						}
					});
				}else{
					alert("Create Admin User First");
				}
			}
		});
	}
	
	UserCreationRequest.createAdminUsers = function(pid) {
		$.ajax({
			url : ContextPath + '/create-admin-users',
			method : 'GET',
			data : {
				companyPid : pid
			},
			success : function(data) {
				onSaveSuccess(data);
			}
		});
	}
	

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = ContextPath;
	}
	
})();