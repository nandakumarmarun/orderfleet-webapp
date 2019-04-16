if (!this.AttendanceSubgroupApprovalRequest) {
	this.AttendanceSubgroupApprovalRequest = {};
}

(function() {
	'use strict';

	var attendanceSubgroupApprovalRequestContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		AttendanceSubgroupApprovalRequest.filter();
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#acceptAttendance").click(function() {
			approveAttendance();
		});
		$("#rejectAttendance").click(function() {
			rejectAttendance();
		});
	});
	
	AttendanceSubgroupApprovalRequest.filter=function(){
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$("#tbodyAttendanceSubgroupApprovalRequest").html(
		"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		$
		.ajax({
			url : attendanceSubgroupApprovalRequestContextPath + "/filter",
			type : 'GET',
			data : {
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val()
			},
			success : function(attendanceSubgroupApprovalRequests) {
				$('#tbodyAttendanceSubgroupApprovalRequest').html("");
				if (attendanceSubgroupApprovalRequests.length == 0) {
					$('#tbodyAttendanceSubgroupApprovalRequest')
							.html(
									"<tr><td colspan='8' align='center'>No data available</td></tr>");
					return;
				}
				$
						.each(
								attendanceSubgroupApprovalRequests,
								function(index, attendanceSubgroupApprovalRequest) {
									var today = new Date();
									var serverDate = new Date(attendanceSubgroupApprovalRequest.requestedDate);
									today.setHours(0,0,0,0);
									serverDate.setHours(0,0,0,0);
									if (today.getTime() <= serverDate.getTime()) {
										if(attendanceSubgroupApprovalRequest.approverUserName == null){
										$(
												'#tbodyAttendanceSubgroupApprovalRequest')
												.append(
														"<tr><td>"
																+ attendanceSubgroupApprovalRequest.attendanceStatusSubgroupName
																+ "</td><td>"
																+ attendanceSubgroupApprovalRequest.requestUserName
																+ "</td><td>"
																+ convertDateTimeFromServer(attendanceSubgroupApprovalRequest.requestedDate)
																+ "</td><td>"
																+ (attendanceSubgroupApprovalRequest.approverUserName == null ? ""
																		: attendanceSubgroupApprovalRequest.approverUserName)
																+ "</td><td>"
																+ convertDateTimeFromServer(attendanceSubgroupApprovalRequest.approvedDate)
																+ "</td><td>"
																+ (attendanceSubgroupApprovalRequest.approverUserMessage == null ? ""
																		: attendanceSubgroupApprovalRequest.approverUserMessage)
																+ "</td><td><button type='button' class='btn btn-primary' onclick='AttendanceSubgroupApprovalRequest.showModalPopup($(\"#approveModal\"),\""
																+ attendanceSubgroupApprovalRequest.id
																+ "\",0);'>Approve</button><button type='button' class='btn btn-danger' onclick='AttendanceSubgroupApprovalRequest.showModalPopup($(\"#rejectModal\"),\""
																+ attendanceSubgroupApprovalRequest.id
																+ "\",1);'>Reject</button></td></tr>");
										}else{
											$(
											'#tbodyAttendanceSubgroupApprovalRequest')
											.append(
													"<tr><td>"
															+ attendanceSubgroupApprovalRequest.attendanceStatusSubgroupName
															+ "</td><td>"
															+ attendanceSubgroupApprovalRequest.requestUserName
															+ "</td><td>"
															+ convertDateTimeFromServer(attendanceSubgroupApprovalRequest.requestedDate)
															+ "</td><td>"
															+ (attendanceSubgroupApprovalRequest.approverUserName == null ? ""
																	: attendanceSubgroupApprovalRequest.approverUserName)
															+ "</td><td>"
															+ convertDateTimeFromServer(attendanceSubgroupApprovalRequest.approvedDate)
															+ "</td><td>"
															+ (attendanceSubgroupApprovalRequest.approverUserMessage == null ? ""
																	: attendanceSubgroupApprovalRequest.approverUserMessage)
															+ "</td><td>"+attendanceSubgroupApprovalRequest.approvalStatus+"</td></tr>");
										}
									} else {
										$(
												'#tbodyAttendanceSubgroupApprovalRequest')
												.append(
														"<tr><td>"
																+ attendanceSubgroupApprovalRequest.attendanceStatusSubgroupName
																+ "</td><td>"
																+ attendanceSubgroupApprovalRequest.requestUserName
																+ "</td><td>"
																+ convertDateTimeFromServer(attendanceSubgroupApprovalRequest.requestedDate)
																+ "</td><td>"
																+ (attendanceSubgroupApprovalRequest.approverUserName == null ? ""
																		: attendanceSubgroupApprovalRequest.approverUserName)
																+ "</td><td>"
																+ convertDateTimeFromServer(attendanceSubgroupApprovalRequest.approvedDate)
																+ "</td><td>"
																+ (attendanceSubgroupApprovalRequest.approverUserMessage == null ? ""
																		: attendanceSubgroupApprovalRequest.approverUserMessage)
																+ "</td><td></td></tr>");
									}
								});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
	});
		}

	

	function approveAttendance() {
		$.ajax({
			url : attendanceSubgroupApprovalRequestContextPath + "/approve",
			type : 'POST',
			data : {
				message : $("#field_message").val(),
				attendanceSubgroupApprovalRequestId : attendanceSubgroupApprovalRequestId
			},
			success : function(result) {
				onSaveSuccess(result);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	AttendanceSubgroupApprovalRequest.showDatePicker = function() {
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


	function rejectAttendance() {
		$.ajax({
			url : attendanceSubgroupApprovalRequestContextPath + "/reject",
			type : 'POST',
			data : {
				message : $("#message").val(),
				attendanceSubgroupApprovalRequestId : attendanceSubgroupApprovalRequestId
			},
			success : function(result) {
				onSaveSuccess(result);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = attendanceSubgroupApprovalRequestContextPath;
	}

	var attendanceSubgroupApprovalRequestId;
	AttendanceSubgroupApprovalRequest.showModalPopup = function(el, id, action) {
		attendanceSubgroupApprovalRequestId = id;
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