// Create a ReceivablePayable object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.NotificationReply) {
	this.NotificationReply = {};
}

(function() {
	'use strict';

	var notificationReplyContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		// load data
		NotificationReply.loadData();
	});

	NotificationReply.loadData = function() {

		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}

		$('#tBodyNotificationMessage').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : notificationReplyContextPath + "/filter",
					type : 'GET',
					data : {
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(notificationMessages) {
						
						$('#tBodyNotificationMessage').html("");
						if (jQuery.isEmptyObject(notificationMessages)) {
							$('#tBodyNotificationMessage')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										notificationMessages,
										function(accountPid, notifications) {
											var pid = accountPid.split("~")[0];
											var title = accountPid.split("~")[1];
											var message = accountPid.split("~")[2];
											
											$('#tBodyNotificationMessage')
													.append(
															"<tr data-toggle='collapse' data-target='#"
																	+ pid
																	+ "'><td class='entypo-down-open-mini'>"
																	+ title
																	+ "</td><td>"
																	+ message
																	+ "</td><td>"
																	+ convertDateTimeFromServer(notifications[0].notification.createdDate)
																	+ "</td></tr>"
																	+"<tr class='collapse' id='"+pid+"'>"
																	+"<td colspan='3'>"
																	+"<table class='table table-striped table-bordered '>"
																	+"<tr class='info'><th><b>User</b></th>" 
																	+"<th><b>Status</b></th>" 
																	+"<th><b>Reply</b></th></tr>"
																	+"<tbody id='tblusers-"+pid+"'></tbody></table></td></tr>"
															);
														
															$.each(notifications,function(index, val) {
																var replyString = "";
																$.each(val.notificationReplyDtoList,function(index,reply) {
																	replyString += convertDateTimeFromServer(reply.createdDate) +" : "+reply.message+"<br>";
																});
																
																$('#tblusers-'+pid)
																.append("<tr><td>"
																		+ val.userName
																		+ "</td><td>"
																		+ val.messageStatus
																		+ "</td><td>"
																		+ replyString
																		+ "</td></tr>");
															});
	
											});
				
					}
				});
	}

	
	NotificationReply.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
	}
	
	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
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