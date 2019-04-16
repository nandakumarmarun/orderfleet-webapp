if (!this.SyncOperationTime) {
	this.SyncOperationTime = {};
}

(function() {
	'use strict';

	var syncOperationTimeContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		$('#field_company').on('change', function() {
			getSyncOperationTimes();
		});
	});

	function getSyncOperationTimes() {
		$('#maintable').html("");
		var data = "";
		var companyPid = $('#field_company').val();
		var date1 = null;
		var date2 = null;
		if (companyPid != -1) {
			$
					.ajax({
						url : syncOperationTimeContextPath
								+ "/getSyncOperationTimes",
						type : "GET",
						data : {
							companyPid : companyPid,
						},
						success : function(result) {

							$
									.each(
											result,
											function(index, syncOperation) {
												
													var complete = "";
													if (syncOperation.completed) {
														complete = "<span class='label label-success'style='cursor: pointer;''>TRUE</span>";
													} else {
														complete = "<span class='label label-info'style='cursor: pointer;'>FALSE</span>";
													}
												if (syncOperation.lastSyncStartedDate != null && syncOperation.lastSyncCompletedDate != null) {
													date1 = getDateTime(syncOperation.lastSyncStartedDate);
													date2 = getDateTime(syncOperation.lastSyncCompletedDate);
												}
												data += "<tr><td>"
														+ syncOperation.operationType
														+ "</td><td>"
														+ date1
														+ "</td><td>"
														+ date2
														+ "</td><td>"
														+ syncOperation.lastSyncTime
														+ "</td><td>"
														+ millisToMinutesAndSeconds(syncOperation.lastSyncTime)
														+ "</td><td>"
														+ complete
														+ "</td></tr>";
											});
							$('#maintable').html(data);
						},
						error : function(xhr, error) {
							onError(xhr, error);
						},
					});
		}
	}
	
	function millisToMinutesAndSeconds(millis) {
		  var minutes = Math.floor(millis / 60000);
		  var seconds = ((millis % 60000) / 1000).toFixed(0);
		  return minutes + ":" + (seconds < 10 ? '0' : '') + seconds;
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

	
	
	function getDateTime(date1) {
		var date = new Date(date1);
		var month;
		var day = date.getDate();
		var monthIndex = date.getMonth() + 1;
		var year = date.getFullYear();
		var hour = date.getHours();
		var minute = date.getMinutes();
		var second = date.getSeconds();
		if (monthIndex == 1) {
			month = "Jan";
		} else if (monthIndex == 2) {
			month = "Feb";
		} else if (monthIndex == 3) {
			month = "Mar";
		} else if (monthIndex == 4) {
			month = "Apr";
		} else if (monthIndex == 5) {
			month = "May";
		} else if (monthIndex == 6) {
			month = "Jun";
		} else if (monthIndex == 7) {
			month = "Jul";
		} else if (monthIndex == 8) {
			month = "Aug";
		} else if (monthIndex == 9) {
			month = "Sep";
		} else if (monthIndex == 10) {
			month = "Oct";
		} else if (monthIndex == 11) {
			month = "Nov";
		} else if (monthIndex == 12) {
			month = "Dec";
		}

		var dateString = day + "-" + month + "-" + year + "  " + hour + ":"
				+ minute + ":" + second;
		return dateString;
	}
})();