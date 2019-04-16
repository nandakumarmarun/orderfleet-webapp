if (!this.RootPlanApprovalRequest) {
	this.RootPlanApprovalRequest = {};
}

(function() {
	'use strict';

	var rootPlanApprovalRequestContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		RootPlanApprovalRequest.filter();
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#acceptRootPlan").click(function() {
			approveRootPlan();
		});
		$("#rejectRootPlan").click(function() {
			rejectRootPlan();
		});
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
	});

	function searchTable(inputVal) {
		var table = $('#tbodyRootPlanApprovalRequest');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
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
	
	RootPlanApprovalRequest.filter=function(){
		console.log(1);
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tbodyRootPlanApprovalRequest').html(
			"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		$
		.ajax({
			url : rootPlanApprovalRequestContextPath + "/filter",
			type : 'GET',
			data : {
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val()
			},
			success : function(rootPlanApprovalRequests) {
				console.log(rootPlanApprovalRequests);
				$('#tbodyRootPlanApprovalRequest').html("");
				if (rootPlanApprovalRequests.length == 0) {
					$('#tbodyRootPlanApprovalRequest')
							.html(
									"<tr><td colspan='8' align='center'>No data available</td></tr>");
					return;
				}
				$
						.each(
								rootPlanApprovalRequests,
								function(index, rootPlanApprovalRequest) {
									var today = new Date();
									var serverDate = new Date(
											rootPlanApprovalRequest.plannedDate);
									today.setHours(0,0,0,0);
									serverDate.setHours(0,0,0,0);
									if (today.getTime() <= serverDate.getTime()) {
										if(rootPlanApprovalRequest.approverUserName == null){
										$(
												'#tbodyRootPlanApprovalRequest')
												.append(
														"<tr><td>"
																+ rootPlanApprovalRequest.taskListName
																+ "</td><td>"
																+ rootPlanApprovalRequest.requestUserName
																+ "</td><td>"
																+ convertDateTimeFromServer(rootPlanApprovalRequest.requestedDate)
																+ "</td><td>"
																+ (rootPlanApprovalRequest.approverUserName == null ? ""
																		: rootPlanApprovalRequest.approverUserName)
																+ "</td><td>"
																+ convertDateTimeFromServer(rootPlanApprovalRequest.approvedDate)
																+ "</td><td>"
																+ (rootPlanApprovalRequest.approverUserMessage == null ? ""
																		: rootPlanApprovalRequest.approverUserMessage)
																+ "</td><td><button type='button' class='btn btn-primary' onclick='RootPlanApprovalRequest.showModalPopup($(\"#approveModal\"),\""
																+ rootPlanApprovalRequest.id
																+ "\",0);'>Approve</button><button type='button' class='btn btn-danger' onclick='RootPlanApprovalRequest.showModalPopup($(\"#rejectModal\"),\""
																+ rootPlanApprovalRequest.id
																+ "\",1);'>Reject</button></td></tr>");
										}else{
											$(
											'#tbodyRootPlanApprovalRequest')
											.append(
													"<tr><td>"
															+ rootPlanApprovalRequest.taskListName
															+ "</td><td>"
															+ rootPlanApprovalRequest.requestUserName
															+ "</td><td>"
															+ convertDateTimeFromServer(rootPlanApprovalRequest.requestedDate)
															+ "</td><td>"
															+ (rootPlanApprovalRequest.approverUserName == null ? ""
																	: rootPlanApprovalRequest.approverUserName)
															+ "</td><td>"
															+ convertDateTimeFromServer(rootPlanApprovalRequest.approvedDate)
															+ "</td><td>"
															+ (rootPlanApprovalRequest.approverUserMessage == null ? ""
																	: rootPlanApprovalRequest.approverUserMessage)
															+ "</td><td>"+rootPlanApprovalRequest.approvalStatus+"</td></tr>");
										}
									} else {
										$(
												'#tbodyRootPlanApprovalRequest')
												.append(
														"<tr><td>"
																+ rootPlanApprovalRequest.taskListName
																+ "</td><td>"
																+ rootPlanApprovalRequest.requestUserName
																+ "</td><td>"
																+ convertDateTimeFromServer(rootPlanApprovalRequest.requestedDate)
																+ "</td><td>"
																+ (rootPlanApprovalRequest.approverUserName == null ? ""
																		: rootPlanApprovalRequest.approverUserName)
																+ "</td><td>"
																+ convertDateTimeFromServer(rootPlanApprovalRequest.approvedDate)
																+ "</td><td>"
																+ (rootPlanApprovalRequest.approverUserMessage == null ? ""
																		: rootPlanApprovalRequest.approverUserMessage)
																+ "</td><td></td></tr>");
									}
								});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
	});
		}
	
	

	function approveRootPlan() {
		$.ajax({
			url : rootPlanApprovalRequestContextPath + "/approve",
			type : 'POST',
			data : {
				message : $("#field_message").val(),
				rootPlanApprovalRequestId : rootPlanApprovalRequestId
			},
			success : function(result) {
				if(result==true){
					alert("Already Downloaded");
					onSaveSuccess(result);
				}else{
				onSaveSuccess(result);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function rejectRootPlan() {
		$.ajax({
			url : rootPlanApprovalRequestContextPath + "/reject",
			type : 'POST',
			data : {
				message : $("#message").val(),
				rootPlanApprovalRequestId : rootPlanApprovalRequestId
			},
			success : function(result) {
				onSaveSuccess(result);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	RootPlanApprovalRequest.showDatePicker = function() {
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

	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = rootPlanApprovalRequestContextPath;
	}

	var rootPlanApprovalRequestId;
	RootPlanApprovalRequest.showModalPopup = function(el, id, action) {
		rootPlanApprovalRequestId = id;
		el.modal('show');
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
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