// Create a SalesValueReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesValueReport) {
	this.SalesValueReport = {};
}

(function() {
	'use strict';
	var salesValueReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		getHeaders();
	});

	function getHeaders() {
		$('#tHeadSalesValueReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");
		$.ajax({
					url : salesValueReportContextPath + "/getHeaders",
					method : 'GET',
					success : function(data) {
						$('#tHeadSalesValueReport').html("");
						if (data.length == 0) {
							$('#tHeadSalesValueReport')
									.html(
											"<tr><td colspan='6' align='center'>Please Configure Purchase History Settings</td></tr>");
							return;
						}
						var headValues = "";
						$.each(data, function(index, head) {
							headValues += "<th>" + head + "</th>";
						});
						$('#tHeadSalesValueReport').append(
								"<tr><th>Customer Name</th>" + headValues
										+ "</tr>");
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	SalesValueReport.filter = function() {

		$('#tBodySalesValueReport').html(
				"<tr><td colspan='6' align='center'>Please wait...</td></tr>");

		var userPid = $("#dbEmployee").val();
		if (userPid == -1 || userPid == "no") {
			alert("pleasse select user");
			$('#tBodySalesValueReport').html("");
			return;
		}
		$.ajax({
			url : salesValueReportContextPath + "/getUserUnderValues/"
					+ userPid,
			type : 'GET',
			success : function(salesValueReports) {
				$('#tBodySalesValueReport').html("");

				for ( var k in salesValueReports) {
					if (salesValueReports.hasOwnProperty(k)) {
						// console.log("Key is " + k + ", value is" +
						// salesValueReports[k]);
						var accountName = k;
						var rowData = "";
						$.each(salesValueReports[k], function(index, svr) {
							rowData += "<td>" + svr.documentTotal + "</td>";
						});
					}

					$('#tBodySalesValueReport').append(
							"<tr>><td>" + accountName + "</td>" + rowData
									+ "</tr>");
				}

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = salesValueReportContextPath;
	}

	SalesValueReport.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadSalesValueReport(id);
				break;
			}
		}
		el.modal('show');
	}

	SalesValueReport.closeModalPopup = function(el) {
		el.modal('hide');
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