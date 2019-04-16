
(function() {
	'use strict';
	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		$(".sortOrder").val(0);
		$(".reportOrder").val(0);
		var formPid = Orderfleet.getParameterByName('pid');
		if (formPid != null && formPid != "") {
			$('#sbForm').val(formPid);
			//set read only
			$('#sbForm').attr("disabled", true); 
		} else {
			$('#sbForm').val("-1");
		}
		loadAssignedQuestions();
		
		$('#btnNewQuestion').click(function() {
			if($('#sbForm').val() == null || $('#sbForm').val() == "-1"){
				alert("No form selected.");
				return;
			}
			if($('#sbForm').val() != "-1") {
				$('#newQuestionModal').modal('show');
				$('#newQuestionModal .modal-title').html("Create Question under form : <strong>" + $('#sbForm option:selected').text() + "</strong>");	
			}
		});
		
		// table search
		/*$('#btnSearch').click(function() {
			searchFormTable($("#search").val());
		});*/
		
		//check boxes
		$('#tblQuestions input:checkbox.allcheckbox').click(function() {
			$(this).closest('table').find('tbody tr td input[type="checkbox"][name="question"]').prop('checked', $(this).prop('checked'));
		});
		
		//popup search
		$('#btnPopUpSearch').click(function() {
			searchPopUpTable($("#txtPopUpsearch").val());
		});
		
		// on change form selectbox search
		$('#sbForm').change(function() {
			if($(this).val() != "-1"){
				loadAssignedQuestions();
			}
		});
		$("#myFormElementSubmit").on("click", function(e){
		    e.preventDefault();
		    var pid = $('#sbForm').val();
		    $('#formElementForm').attr('action', "/web/other-tasks/formElements?formpid="+pid).submit();
		});
		
		// on user change pop up
		$('#sbUsers').change(function() {
			//filter
			$("input[name='popUpfilter'][value='all']").prop("checked", true);
			if($(this).val() == "-1"){
				$('#tBodyAccountProfile').html('');
			} else {
				loadUserAccountProfiles();
			}
		});
		// select all checkbox in table tBodyAccountProfile
		$('#tblAccounts input:checkbox.allcheckbox').click(function() {
			$(this).closest('table').find('tbody tr td input[type="checkbox"]').prop('checked', $(this).prop('checked'));
		});
		
		$('#btnSaveAccountProfiles').on('click', function() {
			saveAssignedAccountProfiles();
		});
	});
	
	function saveAssignedAccountProfiles() {
		var selectedAccountProfiles = "";
		var userPid = $('#sbUsers').val();
		$.each($("input[name='accountProfile']:checked"), function() {
			selectedAccountProfiles += $(this).val() + ",";
		});
		if (selectedAccountProfiles == "") {
			alert("Please select account profile");
			return;
		}
		if (userPid == "-1") {
			alert("Please select user");
			return;
		}
		$.ajax({
			url : contextPath + "/web/other-tasks/assign-accounts",
			type : "POST",
			data : {
				formElementPid : $('#hdnQuestionPid').val(),
				userPid : userPid,
				assignedAccountProfiles : selectedAccountProfiles
			},
			success : function(status) {
				$("#assignUserAccountModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function loadUserAccountProfiles() {
		var userPid = $('#sbUsers').val();
		$('#tBodyAccountProfile').html('Loading...');
		//load user account profile
		$.ajax({
			url : contextPath + "/web/accountProfiles/user/" + userPid,
			type : "GET",
			success : function(accountprofiles) {
				$('#tBodyAccountProfile').html('');
				if (accountprofiles) {
					var html = "";
					$.each(accountprofiles,function(index, account){
						html += "<tr>"
							+ "<td><input name='accountProfile' type='checkbox' value='"+ account.pid +"' /></td>"
							+ "<td>"+ account.name +"</td>"
							+ "<td>"+ account.description +"</td>"
							+ "<td>"+ account.address +"</td>"
							+ "</tr>";
					});
					//check the checkbox for already saved account profile
					checkSavedAccountProfile(html);
				}
			},
			error : function(xhr, error) {
				console.log(error);
			}
		});
	}
	
	function checkSavedAccountProfile(html) {
		var userPid = $('#sbUsers').val();
		$.ajax({
			url : contextPath + "/web/other-tasks/form-element-accounts",
			type : "GET",
			data : {
				formElementPid : $('#hdnQuestionPid').val(),
				userPid : userPid
			},
			success : function(accountprofiles) {
				$('#tBodyAccountProfile').html(html);
				if(accountprofiles.length > 0){
					for (var i = 0; i < accountprofiles.length; i++) {
						$("#tBodyAccountProfile input:checkbox[value=" + accountprofiles[i] + "]").prop("checked", true);
					}
				}
			},
			error : function(xhr, error) {
				console.log(error);
			}
		});
	}
	
	function loadAssignedQuestions() {
		var pid = $('#sbForm').val();
		$('#tblQuestions').hide();
		//load assigned questions and sort order
		$.ajax({
			url : contextPath + "/web/forms/form-elements/" + pid,
			type : "GET",
			success : function(formElements) {
				//unselect all
				$("#tblQuestions input:checkbox").prop("checked", false);
				if (formElements) {
					$.each(formElements,function(index, formElement){
						$("#tblQuestions input:checkbox[value="+ formElement.formElementPid+ "]").prop("checked", true);
						$("#"+ formElement.formElementPid + "").val(formElement.sortOrder);
						$("#reportOrder"+ formElement.formElementPid+ "").val(formElement.reportOrder);
						$("#editable"+ formElement.formElementPid+ "").prop("checked",formElement.editable);
						$("#validationEnabled"+ formElement.formElementPid+ "").prop("checked",formElement.validationEnabled);
					});
				}
				$('#tblQuestions').show();
				//filter
				$("input[name='filter'][value='all']").prop("checked", true);
				searchFormTable($("#search").val());
			},
			error : function(xhr, error) {
				console.log(error);
			}
		});
	}
	
	function searchFormTable(inputVal) {
		var table = $('#tbodyQuestions');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
					var regExp = new RegExp(inputVal, 'i');
					if (regExp.test($(td).text())) {
						found = true;
						return false;
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}
	
	function searchPopUpTable(inputVal) {
		var table = $('#tBodyAccountProfile');
		var filterBy = $("input[name='popUpfilter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
					var regExp = new RegExp(inputVal, 'i');
					if (regExp.test($(td).text())) {
						found = true;
						return false;
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}
	
})();