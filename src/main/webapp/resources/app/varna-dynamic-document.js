// Create a DynamicDocument object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.VarnaDynamicDocument) {
	this.VarnaDynamicDocument = {};
}

(function() {
	'use strict';

	var varnaDynamicDocumentContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// call from menu item
		var documentName = getParameterByName('name');
		$("#documentTitle").html(documentName);
		$('#btnApply').on('click', function() {
			if ($("#dbEmployee").val() == "no") {
				alert("Please select Employee.");
				return;
			}
			if ($("#dbWorkType").val() == "no") {
				alert("Please select Work Type.");
				return;
			}
			VarnaDynamicDocument.filter();
		});

	});

	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}

	VarnaDynamicDocument.filter = function() {
		$('#tHeadVarnaDynamicDocument').html("");
		$('#tBodyVarnaDynamicDocument').html("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		var workType=$("#dbWorkType").val();
		$('#tHeadVarnaDynamicDocument').html(
				"<tr><td colspan='5' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : varnaDynamicDocumentContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						workType : workType,
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(varnaDynamicDocuments) {
						$('#tHeadVarnaDynamicDocument').html("");
						var thead = "<tr><th>Date</th><th>Order No</th><th>Customer Name</th><th>Item</th><th>Quantity</th>"
						$('#tBodyVarnaDynamicDocument').html("");
						if (varnaDynamicDocuments.length == 0) {
							thead += "</tr>";
							$('#tBodyVarnaDynamicDocument')
									.html(
											"<tr><td colspan='5' align='center'>No data available</td></tr>");
							return;
						}
						if (workType == "Polishing") {
							thead += "<th>Polishing Type</th><th>Sub Type</th><th>Polishing</th></tr>";
							$('#tHeadVarnaDynamicDocument').append(thead);
						} else if (workType == "Etching") {
							thead += "<th>Etching Type</th><th>Etching</th></tr>";
							$('#tHeadVarnaDynamicDocument').append(thead);
						} else if (workType == "Acid_Works") {
							thead += "<th>Acid Works Type</th><th>Acid Works</th></tr>";
							$('#tHeadVarnaDynamicDocument').append(thead);
						} else if (workType == "Holes") {
							thead += "<th>Holes</th></tr>";
							$('#tHeadVarnaDynamicDocument').append(thead);
						} else if (workType == "Cut_Outs") {
							thead += "<th>Cut Outs</th></tr>";
							$('#tHeadVarnaDynamicDocument').append(thead);
						}
						
						$
								.each(
										varnaDynamicDocuments,
										function(index, varnaDynamicDocument) {
											console.log(varnaDynamicDocument);
											if (varnaDynamicDocument.polishing != null) {

												$('#tBodyVarnaDynamicDocument')
														.append(
																"<tr><td>"
																		+ convertDateTimeFromServer(varnaDynamicDocument.dateTime)
																		+ "</td><td>"
																		+ varnaDynamicDocument.orderedNo
																		+ "</td><td>"
																		+ varnaDynamicDocument.customerName
																		+ "</td><td>"
																		+ varnaDynamicDocument.glass
																		+ "</td><td>"
																		+ varnaDynamicDocument.quantity
																		+ "</td><td>"
																		+ varnaDynamicDocument.type
																		+ "</td><td>"
																		+ varnaDynamicDocument.polishTypeValue
																		+ "</td><td>"
																		+ varnaDynamicDocument.polishing
																		+ "</td></tr>");
											} else if (varnaDynamicDocument.etching != null) {

												$('#tBodyVarnaDynamicDocument')
														.append(
																"<tr><td>"
																		+ convertDateTimeFromServer(varnaDynamicDocument.dateTime)
																		+ "</td><td>"
																		+ varnaDynamicDocument.orderedNo
																		+ "</td><td>"
																		+ varnaDynamicDocument.customerName
																		+ "</td><td>"
																		+ varnaDynamicDocument.glass
																		+ "</td><td>"
																		+ varnaDynamicDocument.quantity
																		+ "</td><td>"
																		+ varnaDynamicDocument.type
																		+ "</td><td>"
																		+ varnaDynamicDocument.etching
																		+ "</td></tr>");
											} else if (varnaDynamicDocument.acidWorks != null) {

												$('#tBodyVarnaDynamicDocument')
														.append(
																"<tr><td>"
																		+ convertDateTimeFromServer(varnaDynamicDocument.dateTime)
																		+ "</td><td>"
																		+ varnaDynamicDocument.orderedNo
																		+ "</td><td>"
																		+ varnaDynamicDocument.customerName
																		+ "</td><td>"
																		+ varnaDynamicDocument.glass
																		+ "</td><td>"
																		+ varnaDynamicDocument.quantity
																		+ "</td><td>"
																		+ varnaDynamicDocument.type
																		+ "</td><td>"
																		+ varnaDynamicDocument.acidWorks
																		+ "</td></tr>");
											} else if (varnaDynamicDocument.cutOuts != null) {

												$('#tBodyVarnaDynamicDocument')
														.append(
																"<tr><td>"
																		+ convertDateTimeFromServer(varnaDynamicDocument.dateTime)
																		+ "</td><td>"
																		+ varnaDynamicDocument.orderedNo
																		+ "</td><td>"
																		+ varnaDynamicDocument.customerName
																		+ "</td><td>"
																		+ varnaDynamicDocument.glass
																		+ "</td><td>"
																		+ varnaDynamicDocument.quantity
																		+ "</td><td>"
																		+ varnaDynamicDocument.cutOuts
																		+ "</td></tr>");
											} else if (varnaDynamicDocument.holes != null) {

												$('#tBodyVarnaDynamicDocument')
														.append(
																"<tr><td>"
																		+ convertDateTimeFromServer(varnaDynamicDocument.dateTime)
																		+ "</td><td>"
																		+ varnaDynamicDocument.orderedNo
																		+ "</td><td>"
																		+ varnaDynamicDocument.customerName
																		+ "</td><td>"
																		+ varnaDynamicDocument.glass
																		+ "</td><td>"
																		+ varnaDynamicDocument.quantity
																		+ "</td><td>"
																		+ varnaDynamicDocument.holes
																		+ "</td></tr>");
											}

										});
					}
				});
	}

	VarnaDynamicDocument.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

	function clearAndHideErrorBox() {
		$(".alert > p").html("");
		$('.alert').hide();
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
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