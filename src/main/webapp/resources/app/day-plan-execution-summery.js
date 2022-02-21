// Create a DayPlanExecutionSummery object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DayPlanExecutionSummery) {
	this.DayPlanExecutionSummery = {};
}

(function() {
	'use strict';

	var dayPlanExecutionSummeryContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {
		//date range picker
		var start = moment();
	    var end = moment();
	    function setDateRange(start, end) {
	        $('#daterangePicker span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
	    }
		$('#daterangePicker').daterangepicker({
			startDate: start,
			endDate: end,
			ranges: {
				'Today': [moment(), moment()],
				'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
				'This Week': [moment().startOf('week'), moment().endOf('week')],
		        'This Month': [moment().startOf('month'), moment().endOf('month')],
		        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
			}
		}, setDateRange);
		setDateRange(start, end);
		
		$('#btnApply').on('click', function() {
			loadPlanExecutionSummaryBetween($('#daterangePicker').data('daterangepicker').startDate.format('YYYY-MM-DD'), $('#daterangePicker').data('daterangepicker').endDate.format('YYYY-MM-DD'));
		});
		
	});
	
	function loadPlanExecutionSummaryBetween(startDate, endDate) {
		$('#tblBody').html("<tr><td colspan='4' align='center'>Loading...</td></tr>");
		$.ajax({
			url : dayPlanExecutionSummeryContextPath + "/filter",
			method : 'GET',
			data : {
				employeePid : $("#dbEmployee").val(),
				startDate : startDate,
				endDate : endDate
			},
			success : function(response) {
				$('#tblBody').html("");
				if (response.length == 0) {
					$('#tblBody')
							.html(
									"<tr><td colspan='4' align='center'>No data available</td></tr>");
					return;
				}
				var html = "";
				var index = 0;
				var innerIndex = 100;
				//date wise
				$.each(response, function(key, value) {
					 var dpeSummary = JSON.parse(key);
					 index += 1;
					 $("#tblBody").append('<tr style="background: beige;" data-id="' + index + '" data-parent="">'
					 	+ '<td class="janvary">' + moment(dpeSummary.date).format('DD MMM YYYY') + '</td>'
						+ '<td>'+ dpeSummary.scheduled +'</td>'
						+ '<td>'+ dpeSummary.achieved +'</td>'
			    		+ '<td>'+ dpeSummary.percentage +'</td>'
						+ '</tr>');
					 $.each(value, function(userkey, usergroup) {
						 var dayPlanExecution = JSON.parse(userkey);
						 innerIndex += 1;
						 //user wise
						 if(dayPlanExecution.alias==null || dayPlanExecution.alias==""){
						 $("#tblBody").append(
								'<tr style="background: rgba(255, 228, 196, 0.43);" data-id="'
								+ innerIndex + '" data-parent="' + index + '">'
								+ '<td class="janvary">'+ dayPlanExecution.date +'</td>'
					    		+ '<td>'+ dayPlanExecution.scheduled +'</td>'
					    		+ '<td>'+ dayPlanExecution.achieved +'</td>'
					    		+ '<td>'+ dayPlanExecution.percentage +'</td>'
								+ '</tr>');
						 }else{
							 console.log(dayPlanExecution.alias);
							 $("#tblBody").append(
										'<tr style="background: rgba(255, 228, 196, 0.43);" data-id="'
										+ innerIndex + '" data-parent="' + index + '">'
										+ '<td class="janvary">'+ dayPlanExecution.date +' - '+dayPlanExecution.alias+'</td>'
							    		+ '<td>'+ dayPlanExecution.scheduled +'</td>'
							    		+ '<td>'+ dayPlanExecution.achieved +'</td>'
							    		+ '<td>'+ dayPlanExecution.percentage +'</td>'
										+ '</tr>');
						 }
						// generate execution
						$.each(usergroup, function(index, dayPlanExecution) {
							var executionOrder;
		    				if(dayPlanExecution.sortOrder > 0) {
								executionOrder = dayPlanExecution.sortOrder;
		    				} else {
								executionOrder = "<span style='color:red'>X</span>";
		    				}
		    				if(dayPlanExecution.taskListAlias==null || dayPlanExecution.taskListAlias==""){
		    					$("#tblBody").append('<tr data-id="'+ dayPlanExecution.date +'" data-parent="' + innerIndex + '">'
			    						+ '<td class="janvary">'+ dayPlanExecution.activityName +' - '+ dayPlanExecution.accountProfileName +'</td>'
							    		+ '<td>'+ executionOrder +'</td>'
							    		+ '<td>'+ dayPlanExecution.taskPlanStatus +'</td>'
							    		+ '<td>'+ dayPlanExecution.userRemarks +'</td>'
							    		+ '<td class="janvary">' + moment(dayPlanExecution.createdDate).format('DD MMM YYYY hh:mm') + '</td>'
							    		+ '</tr>');
		    				}else{
		    				$("#tblBody").append('<tr data-id="'+ dayPlanExecution.date +'" data-parent="' + innerIndex + '">'
		    						+ '<td class="janvary">'+ dayPlanExecution.activityName +' - '+ dayPlanExecution.accountProfileName +' - '+dayPlanExecution.taskListAlias+'</td>'
						    		+ '<td>'+ executionOrder +'</td>'
						    		+ '<td>'+ dayPlanExecution.taskPlanStatus +'</td>'
						    		+ '<td>'+ dayPlanExecution.userRemarks +'</td>'
						    		+ '<td class="janvary">' + moment(dayPlanExecution.createdDate).format('DD MMM YYYY hh:mm') + '</td>'
						    		+ '</tr>');
		    				}
						});
					 });
				});
				
				$('.collaptable').aCollapTable({
					startCollapsed : true,
					addColumn : false,
					plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
					minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
				});
			},
			error : function() {
				$('#tblBody').html("");
			}
		});
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