// Create a ReceiptHistoryReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ReceiptHistoryReport) {
	this.ReceiptHistoryReport = {};
}

(function() {
	'use strict';

	var receiptHistoryReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$('#dbEmployee').change(function() {
			loadAccountProfileByEmployee(this.value);
		});
		getHeaders();
	});

	function getHeaders() {
		$('#tHeadReceiptHistoryReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : receiptHistoryReportContextPath + "/getHeaders",
					method : 'GET',
					success : function(data) {
						$('#tHeadReceiptHistoryReport').html("");
						if (data.length == 0) {
							$('#tHeadReceiptHistoryReport')
									.html(
											"<tr><td colspan='6' align='center'>Please Configure Settings</td></tr>");
							return;
						}
						var headValues = "";
						$.each(data, function(index, head) {
							headValues += "<th>" + head + "</th>";
						});
						$('#tHeadReceiptHistoryReport').append(
								"<tr><th>Customer Name</th>" + headValues
										+ "</tr>");
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	ReceiptHistoryReport.filter = function() {
		if($('#dbEmployee').val() == "-1"){
			alert("Please Select Employee");
		}
		$('#tBodyReceiptHistoryReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : receiptHistoryReportContextPath + "/filter",
			type : 'GET',
			data : {
				userPid : $("#dbEmployee").val(),
				accountPid : $("#dbAccounts").val(),
			},
			success : function(data) {
				$('#tBodyReceiptHistoryReport').html("");

				$.map(data, function(val, key) {
					var tbody = "";
					$.each(val.salesTargetBlocks, function(index, rh) {
						tbody += "<td>" + rh.achievedAmount + "</td>";
					});
					$('#tBodyReceiptHistoryReport').append(
							"<tr><td>" + key + "</td>" + tbody + "</tr>");
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function loadAccountProfileByEmployee(empPid){
		$('#dbAccounts').html('<option value="-1">Loading...</option>');
		$
		.ajax({
			url : receiptHistoryReportContextPath + "/accountProfile",
			type : 'GET',
			data : {
				employeePid : empPid
			},
			success : function(accounts) {
				$('#dbAccounts').html('');
				$('#dbAccounts').append('<option value="-1">All Contacts</option>');
				if (accounts != null && accounts.length > 0) {
					$.each(accounts, function(indx, account){
						$('#dbAccounts').append('<option value='+ account.pid +'>'+ account.name +'</option>');
					});
				}
			},
			error : function(xhr, error) {
				console.log("Error loading account profile : " + error)
			}
		});
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