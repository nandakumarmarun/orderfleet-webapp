if (!this.AccountingVoucherUI) {
	this.AccountingVoucherUI = {};
}

(function() {
	'use strict';

	var accountingVoucherUIContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#accountingVoucherUIForm");
	var accountingVoucherUIModel = {
			paymentMode : null,
			documentPid : null,
			activityPid:null,
			date:null,
			remark:null,
			amount:null,
			byAccount:null,
			toAccount:null
		};
	$(document).ready(function() {
		if(location.search!=""){
			var name=location.search.split("=")[1];
			loadAccountingVoucherUI(name);
		}
		createEditForm.validate({
			submitHandler : function(form) {
				createUpdateAccountingVoucherUI(form);
			}
		});
		$("#date").datepicker({
			dateFormat : "dd-mm-yy"
		});
	});
	
	function createUpdateAccountingVoucherUI(el) {
		accountingVoucherUIModel.documentPid = $('#dbDocument').val();
		accountingVoucherUIModel.date = $('#date').val();
		accountingVoucherUIModel.paymentMode = $('#field_paymentMode').val();
		accountingVoucherUIModel.remark = $('#remarks').val();
		accountingVoucherUIModel.activityPid = $('#field_activity').val();
		accountingVoucherUIModel.amount = $('#amount').val();
		accountingVoucherUIModel.byAccount = $('#field_byaccount').val();
		accountingVoucherUIModel.toAccount = $('#field_toaccount').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountingVoucherUIModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			
		});
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountingVoucherUIContextPath;
	}
	
	function loadAccountingVoucherUI(name){
		$.ajax({
			url : accountingVoucherUIContextPath + "/loadAccountingVoucherUISettings",
			type : 'GET',
			async:false,
			data : {
				name : name,
			},
			success : function(accountingVoucher) {
				$("#titleName").html(accountingVoucher.title);
				
				$("#field_activity").val(accountingVoucher.activityPid);
				$('#field_activity').attr("disabled", true); 
				$('#dbDocument').attr("disabled", true); 
				AccountingVoucherUI.onChangeActivity();
				$("#dbDocument").val(accountingVoucher.documentPid);
				$("#field_paymentMode").val(accountingVoucher.paymentMode);
				AccountingVoucherUI.loadBuyToAccount();
				$('#field_paymentMode').attr("disabled", true);
			}
		});
	}
	
	AccountingVoucherUI.loadAccountVoucher=function(){
		if($('#field_activity').val()=="-1"){
			alert("Please Select Activity");
			return;
		}
		if($('#dbDocument').val()=="no"){
			alert("Please Select Document");
			return;
		}
		if($('#field_paymentMode').val()=="-1"){
			alert("Please Select PaymentMode");
			return;
		}
		if($('#field_byaccount').val()=="-1"){
			alert("Please Select By account");
			return;
		}
		if($('#field_toaccount').val()=="-1"){
			alert("Please Select To account");
			return;
		}
		$("#tblAccountingDetail").html("<tr><td colspan='3'>Please Wait..</td></tr>");
		$.ajax({
			url : accountingVoucherUIContextPath + "/loadaccountvoucher",
			type : 'GET',
			async:false,
			data : {
				documentPid : $('#dbDocument').val(),
				activityPid : $('#field_activity').val(),
				paymentMode : $('#field_paymentMode').val(),
				byAccount : $('#field_byaccount').val(),
				toAccount : $('#field_toaccount').val()
			},
			success : function(datas) {
				if (datas.length > 0) {
					$("#tblAccountingDetail").html("");
					var row = "";
					$.each(datas, function(index, data) {
						row +="<tr><td>"+convertDateFromServer(data.date)+"</td><td>"+data.amount+"</td><td>"+data.remark+"</td></tr>";
					});
					$("#tblAccountingDetail").html(row);
				} else {
					$("#tblAccountingDetail").html("<tr><td colspan='3' align='center'>No Data Available</td></tr>");
				}
			}
		});
	}
	
	AccountingVoucherUI.showModal =function(){
		if($('#field_activity').val()=="-1"){
			alert("Please Select Activity");
			return;
		}
		if($('#dbDocument').val()=="no"){
			alert("Please Select Document");
			return;
		}
		if($('#field_paymentMode').val()=="-1"){
			alert("Please Select PaymentMode");
			return;
		}
		if($('#field_byaccount').val()=="-1"){
			alert("Please Select By account");
			return;
		}
		if($('#field_toaccount').val()=="-1"){
			alert("Please Select To account");
			return;
		}
		$("#accountingVoucherModal").modal('show');
	}
	
	AccountingVoucherUI.loadBuyToAccount = function(){
		var documentPid=$("#dbDocument").val();
		$("#field_byaccount").html('<option>Loading...</option>');
		$("#field_toaccount").html('<option>Loading...</option>');
		$.ajax({
			url : accountingVoucherUIContextPath + "/loadbuytoaccount",
			type : 'GET',
			async:false,
			data : {
				documentPid : documentPid
			},
			success : function(byToAccount) {
				$("#field_byaccount").html('<option value="-1">Select</option>');
				$("#field_toaccount").html('<option value="-1">Select</option>');
				if (byToAccount.buyAccountProfiles != null && byToAccount.buyAccountProfiles.length > 0) {
					$.each(byToAccount.buyAccountProfiles, function(index, buyAccountProfile) {
						$("#field_byaccount").append(
								'<option value="' + buyAccountProfile.pid + '">' + buyAccountProfile.name
										+ '</option>');
						$("#field_byaccount").val(buyAccountProfile.pid);
					});
				}
				if (byToAccount.toAccountProfiles != null && byToAccount.toAccountProfiles.length > 0) {
					$.each(byToAccount.toAccountProfiles, function(index, toAccountProfile) {
						$("#field_toaccount").append(
								'<option value="' + toAccountProfile.pid + '">' + toAccountProfile.name
										+ '</option>');
						$("#field_toaccount").val(toAccountProfile.pid);
					});
				}

			}
		});
	}
	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}
	AccountingVoucherUI.onChangeActivity = function() {
		if($('#field_activity').val()=="-1"){
			$(".error-msg").html("Please select Activity");
			return;
		}
		$("#dbDocument").html('<option>Loading...</option>');
		$.ajax({
			url : accountingVoucherUIContextPath + "/loaddocument",
			type : 'GET',
			async:false,
			data : {
				activityPid:$('#field_activity').val()
			},
			success : function(documents) {
				$("#dbDocument").html('<option value="no">Select Document</option>');
				if (documents != null && documents.length > 0) {
					$.each(documents, function(index, document) {
						$("#dbDocument").append(
								'<option value="' + document.pid + '">' + document.name
										+ '</option>');
						$("#dbDocument").val(document.pid);
					});
				}
				AccountingVoucherUI.loadBuyToAccount();
			}
		});
	}
})();