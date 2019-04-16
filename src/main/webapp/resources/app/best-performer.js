// Create a BestPerformer object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.BestPerformer) {
	this.BestPerformer = {};
}
(function() {
	'use strict';

	var bestPerformerContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// load today data
		BestPerformer.filter();

		// table search
		$('#search').keyup(function() {
			searchTable($(this).val());
		});

	});

	BestPerformer.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tbl_salesBestPerformers').html(
				"<tr><td colspan='2' align='center'>Please wait...</td></tr>");
		$('#tbl_receiptBestPerformers').html(
				"<tr><td colspan='2' align='center'>Please wait...</td></tr>");

		$
				.ajax({
					url : bestPerformerContextPath + "/filter",
					type : 'GET',
					data : {
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(bestPerformer) {
						console.log(bestPerformer);
						$('#tbl_salesBestPerformers').html("");
						$('#tbl_receiptBestPerformers').html("");
						if(isEmpty(bestPerformer.salesPerformer)) {
							$('#tbl_salesBestPerformers').html("<tr><td colspan='2' align='center'>No data available</td></tr>");
						} else {
							var isFirst = true;
							$.each(bestPerformer.salesPerformer, function(key, value){
								if(isFirst){
									$("#sPerformerName").html(key);
									isFirst = false;
								}
								$('#tbl_salesBestPerformers').append("<tr><td>" + key + "</td><td>" + value + "</td></tr>");
							});
						}
						if(isEmpty(bestPerformer.receiptPerformer)) {
							$('#tbl_receiptBestPerformers').html("<tr><td colspan='2' align='center'>No data available</td></tr>");
						} else {
							var isFirst = true;
							$.each(bestPerformer.receiptPerformer, function(key, value){
								if(isFirst){
									$("#rPerformerName").html(key);
									isFirst = false;
								}
								$('#tbl_receiptBestPerformers').append("<tr><td>" + key + "</td><td>" + value + "</td></tr>");
							});
						}
					}
				});
	}

	BestPerformer.showDatePicker = function() {
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

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
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
	
	function isEmpty(obj) {
	    for(var key in obj) {
	        if(obj.hasOwnProperty(key))
	            return false;
	    }
	    return true;
	}
})();