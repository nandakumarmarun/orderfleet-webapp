// Create a Designation object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Designation) {
	this.Designation = {};
}

(function() {
	'use strict';

	var designationContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		}
	};

	$(document).ready(function() {
		$('#bag-wt-1 , #bag-rt-1 , #bag-rt-1 , #free-scheme-1 , #qty-disc-1 , #sell-rate-per-bag-1').keyup(function(){
			var bagWeight = $('#bag-wt-1').val();
			var bagRate = $('#bag-rt-1').val();
			var freeBagScheme = $('#free-scheme-1').val();
			var perKgRate = bagRate / bagWeight;
			var freeBagAmt = bagRate * freeBagScheme;
			var netRate4Mt = (4000 * perKgRate) - freeBagAmt;
			var qtyDiscount = $('#qty-disc-1').val();
			var discAmt = netRate4Mt * (qtyDiscount / 100);
			var netRateAftrDisc = netRate4Mt - discAmt;
			var ratePerKg = netRateAftrDisc / 4000;
			var sellRatePerBag = $('#sell-rate-per-bag-1').val();
			var sellRatePerKg = sellRatePerBag / bagWeight;
			var profitPerKg = sellRatePerKg - ratePerKg;
			
			$('#per-kg-rate-1').text(perKgRate.toFixed(2));
			$('#free-bag-amt-1').text(freeBagAmt.toFixed(2));
			$('#net-rate-4mt-1').text(netRate4Mt.toFixed(2));
			$('#discount-amt-1').text(discAmt.toFixed(2));
			$('#net-rate-aftr-disc-1').text(netRateAftrDisc.toFixed(2));
			$('#rate-per-kg-1').text(ratePerKg.toFixed(2));
			$('#sell-rate-per-kg-1').text(sellRatePerKg.toFixed(2));
			$('#profit-per-kg-1').text(profitPerKg.toFixed(2));
		});
		
		$('#bag-wt-2 , #bag-rt-2 , #bag-rt-2 , #free-scheme-2 , #qty-disc-2 , #sell-rate-per-bag-2').keyup(function(){
			var bagWeight = $('#bag-wt-2').val();
			var bagRate = $('#bag-rt-2').val();
			var freeBagScheme = $('#free-scheme-2').val();
			var perKgRate = bagRate / bagWeight;
			var freeBagAmt = bagRate * freeBagScheme;
			var netRate4Mt = (4000 * perKgRate) - freeBagAmt;
			var qtyDiscount = $('#qty-disc-2').val();
			var discAmt = netRate4Mt * (qtyDiscount / 100);
			var netRateAftrDisc = netRate4Mt - discAmt;
			var ratePerKg = netRateAftrDisc / 4000;
			var sellRatePerBag = $('#sell-rate-per-bag-2').val();
			var sellRatePerKg = sellRatePerBag / bagWeight;
			var profitPerKg = sellRatePerKg - ratePerKg;
			
			$('#per-kg-rate-2').text(perKgRate.toFixed(2));
			$('#free-bag-amt-2').text(freeBagAmt.toFixed(2));
			$('#net-rate-4mt-2').text(netRate4Mt.toFixed(2));
			$('#discount-amt-2').text(discAmt.toFixed(2));
			$('#net-rate-aftr-disc-2').text(netRateAftrDisc.toFixed(2));
			$('#rate-per-kg-2').text(ratePerKg.toFixed(2));
			$('#sell-rate-per-kg-2').text(sellRatePerKg.toFixed(2));
			$('#profit-per-kg-2').text(profitPerKg.toFixed(2));
		});
		
		$('#bag-wt-3 , #bag-rt-3 , #bag-rt-3 , #free-scheme-3 , #qty-disc-3 , #sell-rate-per-bag-3').keyup(function(){
			var bagWeight = $('#bag-wt-3').val();
			var bagRate = $('#bag-rt-3').val();
			var freeBagScheme = $('#free-scheme-3').val();
			var perKgRate = bagRate / bagWeight;
			var freeBagAmt = bagRate * freeBagScheme;
			var netRate4Mt = (4000 * perKgRate) - freeBagAmt;
			var qtyDiscount = $('#qty-disc-3').val();
			var discAmt = netRate4Mt * (qtyDiscount / 100);
			var netRateAftrDisc = netRate4Mt - discAmt;
			var ratePerKg = netRateAftrDisc / 4000;
			var sellRatePerBag = $('#sell-rate-per-bag-3').val();
			var sellRatePerKg = sellRatePerBag / bagWeight;
			var profitPerKg = sellRatePerKg - ratePerKg;
			
			$('#per-kg-rate-3').text(perKgRate.toFixed(2));
			$('#free-bag-amt-3').text(freeBagAmt.toFixed(2));
			$('#net-rate-4mt-3').text(netRate4Mt.toFixed(2));
			$('#discount-amt-3').text(discAmt.toFixed(2));
			$('#net-rate-aftr-disc-3').text(netRateAftrDisc.toFixed(2));
			$('#rate-per-kg-3').text(ratePerKg.toFixed(2));
			$('#sell-rate-per-kg-3').text(sellRatePerKg.toFixed(2));
			$('#profit-per-kg-3').text(profitPerKg.toFixed(2));
		});
		
		$('#bag-wt-4 , #bag-rt-4 , #bag-rt-4 , #free-scheme-4 , #qty-disc-4 , #sell-rate-per-bag-4').keyup(function(){
			var bagWeight = $('#bag-wt-4').val();
			var bagRate = $('#bag-rt-4').val();
			var freeBagScheme = $('#free-scheme-4').val();
			var perKgRate = bagRate / bagWeight;
			var freeBagAmt = bagRate * freeBagScheme;
			var netRate4Mt = (4000 * perKgRate) - freeBagAmt;
			var qtyDiscount = $('#qty-disc-4').val();
			var discAmt = netRate4Mt * (qtyDiscount / 100);
			var netRateAftrDisc = netRate4Mt - discAmt;
			var ratePerKg = netRateAftrDisc / 4000;
			var sellRatePerBag = $('#sell-rate-per-bag-4').val();
			var sellRatePerKg = sellRatePerBag / bagWeight;
			var profitPerKg = sellRatePerKg - ratePerKg;
			
			$('#per-kg-rate-4').text(perKgRate.toFixed(2));
			$('#free-bag-amt-4').text(freeBagAmt.toFixed(2));
			$('#net-rate-4mt-4').text(netRate4Mt.toFixed(2));
			$('#discount-amt-4').text(discAmt.toFixed(2));
			$('#net-rate-aftr-disc-4').text(netRateAftrDisc.toFixed(2));
			$('#rate-per-kg-4').text(ratePerKg.toFixed(2));
			$('#sell-rate-per-kg-4').text(sellRatePerKg.toFixed(2));
			$('#profit-per-kg-4').text(profitPerKg.toFixed(2));
		});
		
		$('#bag-wt-5 , #bag-rt-5 , #bag-rt-5 , #free-scheme-5 , #qty-disc-5 , #sell-rate-per-bag-5').keyup(function(){
			var bagWeight = $('#bag-wt-5').val();
			var bagRate = $('#bag-rt-5').val();
			var freeBagScheme = $('#free-scheme-5').val();
			var perKgRate = bagRate / bagWeight;
			var freeBagAmt = bagRate * freeBagScheme;
			var netRate4Mt = (4000 * perKgRate) - freeBagAmt;
			var qtyDiscount = $('#qty-disc-5').val();
			var discAmt = netRate4Mt * (qtyDiscount / 100);
			var netRateAftrDisc = netRate4Mt - discAmt;
			var ratePerKg = netRateAftrDisc / 4000;
			var sellRatePerBag = $('#sell-rate-per-bag-5').val();
			var sellRatePerKg = sellRatePerBag / bagWeight;
			var profitPerKg = sellRatePerKg - ratePerKg;
			
			$('#per-kg-rate-5').text(perKgRate.toFixed(2));
			$('#free-bag-amt-5').text(freeBagAmt.toFixed(2));
			$('#net-rate-4mt-5').text(netRate4Mt.toFixed(2));
			$('#discount-amt-5').text(discAmt.toFixed(2));
			$('#net-rate-aftr-disc-5').text(netRateAftrDisc.toFixed(2));
			$('#rate-per-kg-5').text(ratePerKg.toFixed(2));
			$('#sell-rate-per-kg-5').text(sellRatePerKg.toFixed(2));
			$('#profit-per-kg-5').text(profitPerKg.toFixed(2));
		});
		
		$('#bag-wt-6 , #bag-rt-6 , #bag-rt-6 , #free-scheme-6 , #qty-disc-6 , #sell-rate-per-bag-6').keyup(function(){
			var bagWeight = $('#bag-wt-6').val();
			var bagRate = $('#bag-rt-6').val();
			var freeBagScheme = $('#free-scheme-6').val();
			var perKgRate = bagRate / bagWeight;
			var freeBagAmt = bagRate * freeBagScheme;
			var netRate4Mt = (4000 * perKgRate) - freeBagAmt;
			var qtyDiscount = $('#qty-disc-6').val();
			var discAmt = netRate4Mt * (qtyDiscount / 100);
			var netRateAftrDisc = netRate4Mt - discAmt;
			var ratePerKg = netRateAftrDisc / 4000;
			var sellRatePerBag = $('#sell-rate-per-bag-6').val();
			var sellRatePerKg = sellRatePerBag / bagWeight;
			var profitPerKg = sellRatePerKg - ratePerKg;
			
			$('#per-kg-rate-6').text(perKgRate.toFixed(2));
			$('#free-bag-amt-6').text(freeBagAmt.toFixed(2));
			$('#net-rate-4mt-6').text(netRate4Mt.toFixed(2));
			$('#discount-amt-6').text(discAmt.toFixed(2));
			$('#net-rate-aftr-disc-6').text(netRateAftrDisc.toFixed(2));
			$('#rate-per-kg-6').text(ratePerKg.toFixed(2));
			$('#sell-rate-per-kg-6').text(sellRatePerKg.toFixed(2));
			$('#profit-per-kg-6').text(profitPerKg.toFixed(2));
		});
		
		$('#bag-wt-7 , #bag-rt-7 , #bag-rt-7 , #free-scheme-7 , #qty-disc-7 , #sell-rate-per-bag-7').keyup(function(){
			var bagWeight = $('#bag-wt-7').val();
			var bagRate = $('#bag-rt-7').val();
			var freeBagScheme = $('#free-scheme-7').val();
			var perKgRate = bagRate / bagWeight;
			var freeBagAmt = bagRate * freeBagScheme;
			var netRate4Mt = (4000 * perKgRate) - freeBagAmt;
			var qtyDiscount = $('#qty-disc-7').val();
			var discAmt = netRate4Mt * (qtyDiscount / 100);
			var netRateAftrDisc = netRate4Mt - discAmt;
			var ratePerKg = netRateAftrDisc / 4000;
			var sellRatePerBag = $('#sell-rate-per-bag-7').val();
			var sellRatePerKg = sellRatePerBag / bagWeight;
			var profitPerKg = sellRatePerKg - ratePerKg;
			
			$('#per-kg-rate-7').text(perKgRate.toFixed(2));
			$('#free-bag-amt-7').text(freeBagAmt.toFixed(2));
			$('#net-rate-4mt-7').text(netRate4Mt.toFixed(2));
			$('#discount-amt-7').text(discAmt.toFixed(2));
			$('#net-rate-aftr-disc-7').text(netRateAftrDisc.toFixed(2));
			$('#rate-per-kg-7').text(ratePerKg.toFixed(2));
			$('#sell-rate-per-kg-7').text(sellRatePerKg.toFixed(2));
			$('#profit-per-kg-7').text(profitPerKg.toFixed(2));
		});
		
		$('.btn-default').click(function(){
			
			$('#bag-wt-'+this.id).val(0.0);
			$('#bag-rt-'+this.id).val(0.0);
			$('#free-scheme-'+this.id).val(0.0);
			$('#qty-disc-'+this.id).val(0.0);
			$('#sell-rate-per-bag-'+this.id).val(0.0);
			
			$('#per-kg-rate-'+this.id).text('');
			$('#free-bag-amt-'+this.id).text('');
			$('#net-rate-4mt-'+this.id).text('');
			$('#discount-amt-'+this.id).text('');
			$('#net-rate-aftr-disc-'+this.id).text('');
			$('#rate-per-kg-'+this.id).text('');
			$('#sell-rate-per-kg-'+this.id).text('');
			$('#profit-per-kg-'+this.id).text('');
			
					
		});
		
		$('.form-control').focus(function(){
			this.select();
		});
	});

	
	

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		designationModel.pid = null; // reset designation model;
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