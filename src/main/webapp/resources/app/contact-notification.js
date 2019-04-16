// Create a ReceivablePayable object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ContactNotification) {
	this.ContactNotification = {};
}

(function() {
	'use strict';

	var contactNotificationContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		// load data
		ContactNotification.loadData();
	});

	ContactNotification.loadData = function() {

		$('#tBodyNotificationMessage').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : contactNotificationContextPath + "/load",
					type : 'GET',
					success : function(notificationMessages) {
						console.log(notificationMessages);
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
										function(index, notificationMessage) {
											console.log(notificationMessage.pid);
											$('#tBodyNotificationMessage')
													.append(
															"<tr data-toggle='collapse' data-target='#"
																	+ notificationMessage.pid
																	+ "'><td class='entypo-down-open-mini'>"
																	+ notificationMessage.messageData
																	+ "</td><td>"
																	+ convertDateTimeFromServer(notificationMessage.createdDate)
																	+ "</td></tr>"
																	+"<tr class='collapse' id='"+ notificationMessage.pid+ "'>"
																	+"<td colspan='2'>"
																	+"<table class='table table-striped table-bordered'>"
																	+"<tr><th>User</th><th>Status</th></tr>"
																	+"<tbody id='tblusers-"+notificationMessage.pid+"'></tbody></table></td></tr>");
															
															$.each(notificationMessage.notificationReceipts,function(index, user) {
																$('#tblusers-'+notificationMessage.pid)
																.append("<tr><td>"
																		+user.userName
																		+"</td><td>"
																		+user.messageStatus
																		+"</td></tr>");
															});
	
											});
				
					}
				});
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