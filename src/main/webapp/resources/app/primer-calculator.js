// Create a Designation object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PrimerCalculator) {
	this.PrimerCalculator = {};
}

(function() {
	'use strict';

	var primerCalculatorContextPath = location.protocol + '//' + location.host
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
		
		$('#bag-wt-1 , #bag-rt-1 , #free-scheme-1 , #qty-disc-1 ').keyup(function(){
			var litre = $('#bag-wt-1').val();
			var litreRate = $('#bag-rt-1').val();
			var qtyDiscount = $('#free-scheme-1').val();
			var discAmount = (litre * litreRate) * qtyDiscount/100;
			var netRateDisc =(litre * litreRate) - ((litre * litreRate) * qtyDiscount/100);
			var sellingRate = $('#qty-disc-1').val();
			var marginPerLtr = sellingRate -(netRateDisc/litre)
			
			$('#per-kg-rate-1').text(discAmount.toFixed(2));
			$('#free-bag-amt-1').text(netRateDisc.toFixed(2));
			
			$('#discount-amt-1').text(marginPerLtr.toFixed(2));
			
		});
	
		
		$('#bag-wt-2 , #bag-rt-2 , #free-scheme-2 , #qty-disc-2 ').keyup(function(){
			var litre = $('#bag-wt-2').val();
			var litreRate = $('#bag-rt-2').val();
			var qtyDiscount = $('#free-scheme-2').val();
			var discAmount = (litre * litreRate) * qtyDiscount/100;
			var netRateDisc =(litre * litreRate) - ((litre * litreRate) * qtyDiscount/100);
			var sellingRate = $('#qty-disc-2').val();
			var marginPerLtr = sellingRate -(netRateDisc/litre)
			
			$('#per-kg-rate-2').text(discAmount.toFixed(2));
			$('#free-bag-amt-2').text(netRateDisc.toFixed(2));
			
			$('#discount-amt-2').text(marginPerLtr.toFixed(2));
			
		});
		
		$('#bag-wt-3 , #bag-rt-3 , #free-scheme-3 , #qty-disc-3 ').keyup(function(){
			var litre = $('#bag-wt-3').val();
			var litreRate = $('#bag-rt-3').val();
			var qtyDiscount = $('#free-scheme-3').val();
			var discAmount = (litre * litreRate) * qtyDiscount/100;
			var netRateDisc =(litre * litreRate) - ((litre * litreRate) * qtyDiscount/100);
			var sellingRate = $('#qty-disc-3').val();
			var marginPerLtr = sellingRate -(netRateDisc/litre)
			
			$('#per-kg-rate-3').text(discAmount.toFixed(2));
			$('#free-bag-amt-3').text(netRateDisc.toFixed(2));
			
			$('#discount-amt-3').text(marginPerLtr.toFixed(2));
		});
		$('#bag-wt-4 , #bag-rt-4 , #free-scheme-4 , #qty-disc-4 ').keyup(function(){
			var litre = $('#bag-wt-4').val();
			var litreRate = $('#bag-rt-4').val();
			var qtyDiscount = $('#free-scheme-4').val();
			var discAmount = (litre * litreRate) * qtyDiscount/100;
			var netRateDisc =(litre * litreRate) - ((litre * litreRate) * qtyDiscount/100);
			var sellingRate = $('#qty-disc-4').val();
			var marginPerLtr = sellingRate -(netRateDisc/litre)
			
			$('#per-kg-rate-4').text(discAmount.toFixed(2));
			$('#free-bag-amt-4').text(netRateDisc.toFixed(2));
			
			$('#discount-amt-4').text(marginPerLtr.toFixed(2));
		});
		
		
		
		
			$('.btn-default').click(function(){
			
			$('#bag-wt-'+this.id).val(0.0);
			$('#bag-rt-'+this.id).val(0.0);
			$('#free-scheme-'+this.id).val(0.0);
			$('#qty-disc-'+this.id).val(0.0);
			
			
			$('#per-kg-rate-'+this.id).text('');
			$('#free-bag-amt-'+this.id).text('');
			$('#discount-amt-'+this.id).text('');
			
			
					
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