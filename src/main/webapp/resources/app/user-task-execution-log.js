// Create a UserTaskExecutionLog object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserTaskExecutionLog) {
	this.UserTaskExecutionLog = {};
}

(function() {
	'use strict';

	var userTaskExecutionLogContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		// load today data
		UserTaskExecutionLog.filter();
	});

	UserTaskExecutionLog.filter = function() {
		if ($('#dbUser').val() == "no") {
			$('#tBodyUserTaskExecutionLogs')
					.html(
							"<tr><td colspan='7' align='center'>Please select user</td></tr>");
			return;
		}
		$('#tBodyUserTaskExecutionLogs').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : userTaskExecutionLogContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
					},
					success : function(userTaskAssignments) {
						$('#tBodyUserTaskExecutionLogs').html("");
						if (userTaskAssignments.length == 0) {
							$('#tBodyUserTaskExecutionLogs')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										userTaskAssignments,
										function(index, userTaskAssignment) {
											var closeButton = "";
											if (userTaskAssignment.taskStatus == "OPENED") {
												closeButton = "<button type='button' class='btn btn-success' onclick='UserTaskExecutionLog.closeUserTaskAssignment(\""
														+ userTaskAssignment.pid
														+ "\",this);'>Close</button>"
											}
											$('#tBodyUserTaskExecutionLogs')
													.append(
															"<tr data-id='"
																	+ userTaskAssignment.pid
																	+ "' data-parent=\"\"><td>"
																	+ userTaskAssignment.taskActivityName
																	+ "</td><td>"
																	+ userTaskAssignment.taskAccountName
																	+ "</td><td>"
																	+ userTaskAssignment.priorityStatus
																	+ "</td><td>"
																	+ userTaskAssignment.startDate
																	+ "</td><td>"
																	+ userTaskAssignment.remarks
																	+ "</td><td>"
																	+ userTaskAssignment.taskStatus
																	+ "&nbsp;"
																	+ closeButton
																	+ "</td></tr>");

											$
													.each(
															userTaskAssignment.userTaskExecutionLogs,
															function(index,
																	userTaskExecutionLog) {
																$(
																		'#tBodyUserTaskExecutionLogs')
																		.append(
																				"<tr style='background: rgba(225, 225, 225, 0.66);'  data-parent='"
																						+ userTaskAssignment.pid
																						+ "'><td colspan='2'>"
																						+ convertDateTimeFromServer(userTaskExecutionLog)
																						+ "</td></td></tr>");
															});
										});

						$('.collaptable')
								.aCollapTable(
										{
											startCollapsed : true,
											addColumn : false,
											plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
											minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
										});
					}
				});
	}

	UserTaskExecutionLog.closeUserTaskAssignment = function(userTaskPid, obj) {
		if (confirm("Are you sure, You want close this user task assignment?")) {
			$.ajax({
				url : userTaskExecutionLogContextPath + "/close-user-task",
				type : 'GET',
				data : {
					userTaskPid : userTaskPid
				},
				success : function(status) {
					$(obj).closest("td").html("CLOSED");
				}
			});
		}
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
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