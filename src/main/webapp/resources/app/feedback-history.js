if (!this.FeedbackHistory) {
	this.FeedbackHistory = {};
}

(function() {
	'use strict';

	var feedbackHistoryContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	console.log(location.protocol + '//' + location.host);
	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// load data
		FeedbackHistory.filter();
		FeedbackHistory.statusValues();
		
		
	});
	
	function showFeedbackHistory(pid) {
		$
				.ajax({
					url : location.protocol + '//' + location.host + "/web/feedbacks/" + pid,
					method : 'GET',
					success : function(data) {
						
						$('#lbl_documentNumber').text(data.documentNumberLocal);
						$('#lbl_user').text(data.userName);
						$('#lbl_activity').text(data.activityName);
						$('#lbl_account').text(data.accountName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_documentDate').text(
								convertDateTimeFromServer(data.createdDate));
						$('#divFeedbackDetails').html("");
						$
								.each(
										data.filledForms,
										function(index, filledForm) {
											var table = '<table class="table  table-striped table-bordered "><tr><td colspan="2" style="font-weight: bold;">'
													+ filledForm.formName
													+ '</td></tr>';
											$
													.each(
															filledForm.filledFormDetails,
															function(index,
																	formDetail) {
																table += "<tr><td>"
																		+ formDetail.formElementName
																		+ "</td><td>"
																		+ formDetail.value
																		+ "</td>";
															});
											table += '</table>';
											$('#divFeedbackDetails')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function showFeedbackHistoryImages(pid) {
		$
				.ajax({
					url : location.protocol + '//' + location.host + "/web/feedbacks/images/" + pid,
					method : 'GET',
					success : function(filledFormFiles) {
						$('#divFeedbackHistoryImages').html("");
						$
								.each(
										filledFormFiles,
										function(index, filledFormFile) {
											var table = '<table class="table  table-striped table-bordered"><tr><td style="font-weight: bold;">'
													+ filledFormFile.formName
													+ '</td></tr>';
											$
													.each(
															filledFormFile.files,
															function(index,
																	file) {
																table += '<tr><th>'
																		+ file.fileName
																		+ '</th></tr>';
																table += '<tr><td><img width="100%" src="data:image/png;base64,'
																		+ file.content
																		+ '"/></td></tr>';
															});
											table += '</table>';
											$('#divFeedbackHistoryImages')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	FeedbackHistory.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showFeedbackHistory(pid);
				break;
			case 1:
				showFeedbackHistoryImages(pid);
				break;
			}
		}
		el.modal('show');
	}
	
	FeedbackHistory.statusValues = function() {
		var groupPid = $("#dbFeedbackGroup").val();
		$("#dbStatus").html('<option value="no">All Status</option>');
		if(groupPid == null || groupPid == 'no'){
			return;
		}
		$.ajax({
			url : location.protocol + '//' + location.host + "/web/feedbacks/status-values",
			type : 'GET',
			data : {
				feedbackGroupPid : $("#dbFeedbackGroup").val()
			},
			success : function(values) {
				$.each(values, function(index, value) {
					$("#dbStatus").append('<option value="'+value.name+'">'+value.name+'</option>');
				});
			}
		});
	}


	FeedbackHistory.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if($("#dbFeedbackGroup").val() == null){
			$("#dbFeedbackGroup").html("<option value='no'>Feedback Group</option>")
			return;
		}
		$('#tBodyFeedbackHistory').html("<tr><td colspan='5' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : location.protocol + '//' + location.host + "/web/feedbacks/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						feedbackGroupPid : $("#dbFeedbackGroup").val(),
						status : $("#dbStatus").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(feedbacks) {
						$('#tBodyFeedbackHistory').html("");
						if (feedbacks.length == 0) {
							$('#tBodyFeedbackHistory')
									.html(
											"<tr><td colspan='5' align='center'>No data available</td></tr>");
							return;
						}
						$.each(feedbacks,
										function(index, feedback) {
							console.log(feedback);
											$('#tBodyFeedbackHistory')
													.append(
															"<tr><td data-title='User'>"
																	+ feedback.employeeName
																	+ "</td><td data-title='Account'>"
																	+ feedback.accountName
																	+ "</td><td data-title='Activity'>"
																	+ feedback.activityName
																	+ "</td><td data-title='Date'>"
																	+ convertDateTimeFromServer(feedback.createdDate)
																	+ "</td><td data-title='Action'><button type='button' class='btn btn-primary' onclick='FeedbackHistory.showModalPopup($(\"#viewModal\"),\""
																	+ feedback.pid
																	+ "\",0);'>View Details</button></td><td><button type='button' class='btn btn-blue' onclick='FeedbackHistory.showModalPopup($(\"#imagesModal\"),\""
																	+ feedback.pid
																	+ "\",1);'>View Images</button></td></tr>");
										});
					}
				});
	}

	FeedbackHistory.showDatePicker = function() {
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