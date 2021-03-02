// Create a UploadOdoo object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.uploadOdoo) {
	this.uploadOdoo = {};
}

(function() {
	'use strict';

	var uploadOdooContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {


		$('#uploadAll').on('click', function() {
			uploadAll();
		});
		$('#uploadAccountProfiles').on('click', function() {
			uploadAccountProfiles();
		});
		$('#uploadTaxLists').on('click', function() {
			uploadTaxList();
		});
		$('#uploadProductProfileProfiles').on('click', function() {
			uploadProductProfiles();
		});
		$('#uploadUsers').on('click', function() {
			uploadUsers();
		});
		$('#uploadStockLocations').on('click', function() {
			uploadStockLocations();
		});
		
		$('#uploadPriceLists').on('click', function() {
			uploadPriceLevel();
        });
        
        $('#uploadOutstandingInvoice').on('click', function() {
			uploadOutstandingInvoice();
		});
	
	});
	
	function uploadAll() {
		uploadUsers1();
	}
	
	
	
	function uploadUsers() {

		$(".error-msg").html("Uploading Users....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadUsers",
					method : 'GET',
					success : function(data) {
						alert("Upload Users Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading users .................");
						$(".error-msg").html(
								"Error uploading users .................");
					}
				});

	}
	
	function uploadUsers1() {

		$(".error-msg").html("Uploading Users....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadUsers",
					method : 'GET',
					success : function(data) {
						$(".error-msg").html("");
						uploadTaxList1();
					},
					error : function(xhr, error) {
						console.log("Error uploading users .................");
						$(".error-msg").html(
								"Error uploading users .................");
					}
				});

	}
	
	function uploadStockLocations() {

		$(".error-msg").html("Uploading Stock Locations....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadStockLocations",
					method : 'GET',
					success : function(data) {
						alert("Upload Stock Location Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading stock Locations .................");
						$(".error-msg").html(
								"Error uploading stock locations .................");
					}
				});

	}
	
	function uploadStockLocations1() {

		$(".error-msg").html("Uploading Stock Locations....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadStockLocations",
					method : 'GET',
					success : function(data) {
						$(".error-msg").html("");
						alert("Upload All Masters Success")
						onSaveSuccess(data);
					},
					error : function(xhr, error) {
						console.log("Error uploading stock Locations .................");
						$(".error-msg").html(
								"Error uploading stock locations .................");
					}
				});

	}

	function uploadAccountProfiles() {

		$(".error-msg").html("Uploading Account Profiles....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadAccountProfiles",
					method : 'GET',
					success : function(data) {
						alert("Upload Account Profile Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading account profiles .................");
						$(".error-msg").html(
								"Error uploading account profiles .................");
					}
				});

	}
	
	function uploadAccountProfiles1() {

		$(".error-msg").html("Uploading Account Profiles....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadAccountProfiles",
					method : 'GET',
					success : function(data) {
						$(".error-msg").html("");
						uploadOutstandingInvoice1();
					},
					error : function(xhr, error) {
						console.log("Error uploading account profiles .................");
						$(".error-msg").html(
								"Error uploading account profiles .................");
					}
				});

	}

	function uploadTaxList() {

		$(".error-msg").html("Uploading Tax List....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadTaxList",
					method : 'GET',
					success : function(data) {
						alert("Upload Tax List Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading tax list .................");
						$(".error-msg").html(
								"Error uploading tax list .................");
					}
				});

	}
	
	function uploadTaxList1() {

		$(".error-msg").html("Uploading Tax List....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadTaxList",
					method : 'GET',
					success : function(data) {
						$(".error-msg").html("");
						uploadProductProfiles1();
					},
					error : function(xhr, error) {
						console.log("Error uploading tax list .................");
						$(".error-msg").html(
								"Error uploading tax list .................");
					}
				});

	}

	function uploadProductProfiles() {

		$(".error-msg").html("Uploading Product Profiles....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadProductProfiles",
					method : 'GET',
					success : function(data) {
						alert("Upload Product Profile Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading product profiles .................");
						$(".error-msg").html(
								"Error uploading product profiles .................");
					}
				});

	}
	
	function uploadProductProfiles1() {

		$(".error-msg").html("Uploading Product Profiles....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadProductProfiles",
					method : 'GET',
					success : function(data) {
						$(".error-msg").html("");
						uploadPriceLevel1();
					},
					error : function(xhr, error) {
						console.log("Error uploading product profiles .................");
						$(".error-msg").html(
								"Error uploading product profiles .................");
					}
				});

	}
	
	function uploadPriceLevel() {

		$(".error-msg").html("Uploading Price List....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadPriceLevel",
					method : 'GET',
					success : function(data) {
						alert("Upload Price Level Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading price list.................");
						$(".error-msg").html(
								"Error uploading price list .................");
					}
				});

	}
	
	function uploadPriceLevel1() {

		$(".error-msg").html("Uploading Price List....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadPriceLevel",
					method : 'GET',
					success : function(data) {
						$(".error-msg").html("");
						uploadAccountProfiles1();
					},
					error : function(xhr, error) {
						console.log("Error uploading price list.................");
						$(".error-msg").html(
								"Error uploading price list .................");
					}
				});

	}
	
	function uploadOutstandingInvoice() {

		$(".error-msg").html("Uploading Outstanding Invoices....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadOutstandingInvoices",
					method : 'GET',
					success : function(data) {
						alert("Upload Outstanding Invoices Success")
						onSaveSuccess(data);
						$(".error-msg").html("");
					},
					error : function(xhr, error) {
						console.log("Error uploading outstanding invoices.................");
						$(".error-msg").html(
								"Error uploading outstanding invoices .................");
					}
				});

	}
	
	
	function uploadOutstandingInvoice1() {

		$(".error-msg").html("Uploading Outstanding Invoices....");
		$
				.ajax({
					url : uploadOdooContextPath + "/uploadOutstandingInvoices",
					method : 'GET',
					success : function(data) {
						$(".error-msg").html("");
						uploadStockLocations1();
					},
					error : function(xhr, error) {
						console.log("Error uploading outstanding invoices.................");
						$(".error-msg").html(
								"Error uploading outstanding invoices .................");
					}
				});

	}

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = uploadOdooContextPath;
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