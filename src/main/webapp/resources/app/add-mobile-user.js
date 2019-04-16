// Create a AddMobileUser object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AddMobileUser) {
	this.AddMobileUser = {};
}

(function() {
	'use strict';
	var ContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	
	var payAmount = 0;
	var paymentMode;
	var numberOfUsers;
	
	$(document).ready(function() {
		$('#loadingDiv').hide();
		getPaymentAmount();
		$('#field_paymentMode').change(function() {
			getPaymentAmount();
		});
		
		$('#field_numberOfUsers').on('keyup mouseup', function() {
			//paymentMode = $('#field_paymentMode').val();
			numberOfUsers = $('#field_numberOfUsers').val();
			if(paymentMode == "-1"){
				alert("Select a Payment Mode");
				return;
			}
			paymentAmount(paymentMode,numberOfUsers);
		});
	});
	
	function getPaymentAmount(){
		$.ajax({
			url : ContextPath + '/product-rate',
			method : 'GET',
			data : {
				orderProPaymentMode : $('#field_paymentMode').val(),
				snrichProductPid : $('#field_snrich_product').val()
			},
			success : function(data) {
				console.log(data);
				paymentMode = data;
				numberOfUsers = $('#field_numberOfUsers').val();
				paymentAmount(paymentMode,numberOfUsers);
			}
		});
	}
	
	function paymentAmount(paymentMode,numberOfUsers){
		var gstTotal = numberOfUsers*paymentMode*0.18;
		var amountTotal = numberOfUsers*paymentMode;
		var grandTotal = gstTotal+amountTotal;
		payAmount = grandTotal;
		$('#field_amountPerUser').val(paymentMode);
		$('#field_gstPerUser').val(paymentMode*0.18);
		$('#field_amountTotal').val(amountTotal);
		$('#field_gstTotal').val(gstTotal);
		$('#lbl_grandTotal').text('Total Amount : '+grandTotal);
	}
	
	AddMobileUser.payment = function(){
		$('#loadingDiv').show();
		var numberOfUsers = $('#field_numberOfUsers').val();
		//alert("Amount to be paid is "+payAmount+" for "+numberOfUsers+" users");
		
		$.ajax({
			url : ContextPath + '/create-users',
			method : 'GET',
			data : {
				usersCount : numberOfUsers
			},
			success : function(data) {
				//alert("Payment Successful and users have been created!");
				//onSaveSuccess(data);
				$('#loadingDiv').hide();
				window.location = location.protocol + '//' + location.host + "/web/orderpro/dashboard"
			}
		});
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = ContextPath;
	}
	
})();