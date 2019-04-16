// Create a DynamicReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.CopyAppData) {
	this.CopyAppData = {};
}

(function() {
	'use strict';
	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		$('#sbCompany').on('change', function() {
			loadCompanyUsers();
		});
		$('#sbToUsers').multiselect({
			enableCaseInsensitiveFiltering : true,
			includeSelectAllOption : true,
			maxHeight : 400
		});
		$('#btnCopyUserData').on('click', function() {
			copyUserData();
		});
		
		//check box events
		$('#cbWebMobileMenuItems').on('change', function() {
			$("input[name='cbWMMenuItems']").prop('checked', this.checked);
		});
		$('#cbUserWiseFC').on('change', function() {
			$("input[name='cbUserFC']").prop('checked', this.checked);
		});
		$('#cbUserWiseDC').on('change', function() {
			$("input[name='cbUserDC']").prop('checked', this.checked);
		});
		$('#cbEmployeeManagement').on('change', function() {
			$("input[name='cbEM']").prop('checked', this.checked);
		});
	});
	
	function loadCompanyUsers() {
		var companyId = $('#sbCompany').val();
		if(companyId == "-1") {
			return;
		}
		$('#sbFromUser').html('<option value="">Loading...</option>');
		$.ajax({
			url : contextPath + "/web/management/users/company/" + companyId,
			method : 'GET',
			success : function(users) {
				$('#sbFromUser').html('<option value="-1">Select a User</option>');
				$('#sbToUsers').html('');
				$.each(users, function(index, user) {
					$('#sbFromUser').append('<option value="'+ user.pid +'">'+ user.login +'</option>');
					$('#sbToUsers').append('<option value="'+ user.pid +'">'+ user.login +'</option>');
				});
				
				$('#sbToUsers').multiselect('rebuild');
			},
			error : function(xhr, error) {
				console.log("Error loading user by company");
			}
		});
	}
	
	function copyUserData() {
		if($('#sbCompany').val() == "-1"){
			alert("Select a company");
			return;
		}
		if($('#sbFromUser').val() == "-1"){
			alert("Select copy from user");
			return;
		}
		if($('#sbToUsers').val() == null){
			alert("Select copy to users");
			return;
		}
		var checked = []
		$("input[name='cbWMMenuItems']:checked").each(function () {
		    checked.push($(this).val());
		});
		$("input[name='cbUserFC']:checked").each(function () {
		    checked.push($(this).val());
		});
		$("input[name='cbUserDC']:checked").each(function () {
		    checked.push($(this).val());
		});
		$("input[name='cbEM']:checked").each(function () {
		    checked.push($(this).val());
		});
		var copyAppDataModel = {
				fromUserPid : $('#sbFromUser').val(),
				toUsersPid : $('#sbToUsers').val(),
				dataToCopy : checked
		};
		$("#btnCopyUserData").html("Copying...")
		$("#btnCopyUserData").prop("disabled", true);
		$.ajax({
			url : contextPath + "/web/admin/copy-data",
			method : 'POST',
			contentType:"application/json;charset:utf-8",
			data : JSON.stringify(copyAppDataModel),
			success : function(data) {
				alert("Data copied successfully");
				$('#btnCopyUserData').html("Copy data")
				$("#btnCopyUserData").prop("disabled", false);
			},
			error : function(xhr, error) {
				console.log("Error copying data " + xhr.status);
				console.log("Error copying data " + xhr.responseText);
				console.log("Error copying data " + error);
				$('#btnCopyUserData').html("Copy data")
				$("#btnCopyUserData").prop("disabled", false);
			}
		});
	}
})();