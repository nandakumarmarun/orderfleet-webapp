// Create a Verification object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Verification) {
	this.Verification = {};
}

(function() {
	'use strict';

	var verificationContextPath = location.protocol + '//' + location.host
	+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// load today data
		Verification.filter();
		
		$('#myFormSubmit').on('click', function() {
			Verification.reject();
		});
		
		// table search
		$('#search').keyup(function() {
			searchTable($(this).val());
		});
	});

	Verification.accept = function(pid, obj) {
		$.ajax({
			url : verificationContextPath + "/accept/" + pid,
			method : 'PUT',
			success : function(status) {
				if (status) {
					$(object).closest('tr').find('td').eq(5).prop('title', "");
					$(obj).closest('tr').find('td').eq(5).text("ACCEPTED");
					$(obj).closest('tr').find('td').eq(5).css({
						"color" : "green"
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	Verification.reject = function() {
		var reasons=$("#field_reason").val();
		
		if(reasons==null){
			$("#reasonAlert").css('display', 'block');
			return;
		}
		$.ajax({
			url : verificationContextPath + "/reject",
			method : 'PUT',
			data:{
				pid:executiveTaskExecutionPid,
				reason:reasons,
			},
			success : function(status) {
				console.log(reasons)
				if (status) {
					$(object).closest('tr').find('td').eq(5).prop('title', reasons);
					$(object).closest('tr').find('td').eq(5).text("REJECTED");
					$(object).closest('tr').find('td').eq(5).css({
						"color" : "red"
					});
				}
				$("#myModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	var object = null;
	var executiveTaskExecutionPid = null;

	Verification.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyVerification').html(
			"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : verificationContextPath + "/filter",
				type : 'GET',
				data : {
					employeePid : $("#dbEmployee").val(),
					activityPid : $("#dbActivity").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val()
				},
				success : function(executiveTaskExecutions) {
					$('#tBodyVerification').html("");
					if (executiveTaskExecutions.length == 0) {
						$('#tBodyVerification')
							.html(
								"<tr><td colspan='7' align='center'>No data available</td></tr>");
						return;
					}
					$
						.each(
							executiveTaskExecutions,
							function(index, executiveTaskExecution) {
								var activityStatusTd = "<td>"
									+ executiveTaskExecution.activityStatus
									+ "</td>";
								if (executiveTaskExecution.activityStatus == 'ACCEPTED') {
									activityStatusTd = "<td><font color='green'>"
										+ executiveTaskExecution.activityStatus
										+ "</td>";
								} else if (executiveTaskExecution.activityStatus == 'REJECTED') {
									activityStatusTd = "<td id='rejectReason' title='"+executiveTaskExecution.rejectReasonRemark+"'><font color='red'>"+ executiveTaskExecution.activityStatus+""
										+ "</td>";
								}

								$('#tBodyVerification')
									.append(
										"<tr data-id='"
										+ executiveTaskExecution.pid
										+ "' data-parent=\"\"><td>"
										+ executiveTaskExecution.employeeName
										+ "</td><td>"
										+ executiveTaskExecution.accountProfileName
										+ "</td><td>"
										+ executiveTaskExecution.activityName
										+ "</td><td>"
										+ convertDateTimeFromServer(executiveTaskExecution.createdDate)
										+ "</td><td>"
										+ executiveTaskExecution.location
										+ "</td>"
										+ activityStatusTd
										+ "<td><button type='button' title='accept' class='btn btn-green entypo-thumbs-up' aria-label='entypo-thumbs-up' onclick='Verification.accept(\""
										+ executiveTaskExecution.pid
										+ "\",this);'></button>&nbsp;&nbsp;<button type='button' title='reject' class='btn btn-red entypo-thumbs-down' aria-label='entypo-thumbs-down' onclick='Verification.showModalPopup(\"" + executiveTaskExecution.pid + "\",this);'></button></td></tr>");
							});
				}
			});
	}

	Verification.showModalPopup = function(pid, obj) {
		object = obj;
		executiveTaskExecutionPid = pid;
		$("#field_reason").val("") ;
		$("#myModal").modal("show");
		
	}

	Verification.showDatePicker = function() {
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

	function searchTable(inputVal) {
		console.log(inputVal);
		var table = $('#tBodyVerification');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index < 6) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
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
})();