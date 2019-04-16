// Create a DayPlanExecutionSummery object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;

	$(document).ready(function() {
		var employeePid = getParameterByName('user');
		getEmployees(employeePid);
		
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
			loadPlanExecutionSummaryBetweenUserWise($('#daterangePicker').data('daterangepicker').startDate.format('YYYY-MM-DD'), $('#daterangePicker').data('daterangepicker').endDate.format('YYYY-MM-DD'));
		});
		
		//Auto filter
		var employeePid = getParameterByName('user');
		if (employeePid != null && employeePid != "") {
			$('#dbEmployee').val(employeePid);
			loadPlanExecutionSummaryBetweenUserWise($('#daterangePicker').data('daterangepicker').startDate.format('YYYY-MM-DD'), $('#daterangePicker').data('daterangepicker').endDate.format('YYYY-MM-DD'));
		}
		
	});
	
	function loadPlanExecutionSummaryBetweenUserWise(startDate, endDate) {
		$('#tblBody').html("<tr><td colspan='4' align='center'>Loading...</td></tr>");
		console.log(startDate);
		console.log(endDate);
		$.ajax({
			url : contextPath + "/web/day-plans-execution-summary-user-wise/filter",
			method : 'GET',
			data : {
				employeePid : $("#dbEmployee").val(),
				startDate : startDate,
				endDate : endDate
			},
			success : function(response) {
				$('#tblBody').html("");
				if (Object.keys(response).length === 0) {
					$('#thSchdld').html('Scheduled');
					$('#thAchvd').html('Achieved ');
					$('#thPercentage').html('Percentage');
					$('#tblBody')
							.html(
									"<tr><td colspan='4' align='center'>No data available</td></tr>");
					return;
				}
				var html = "";
				var index = 0;
				var innerIndex = 100;
				
				var scheduleTot = 0;
				var achievedTot = 0;
				var percentageTot = 0;
				//user wise
				$.each(response, function(key, value) {
					 var dpeSummary = JSON.parse(key);
					 index += 1;
					 $("#tblBody").append('<tr style="background: beige;" data-id="' + index + '" data-parent="">'
					 	+ '<td class="janvary">' + dpeSummary.date + '</td>'
						+ '<td>'+ dpeSummary.scheduled +'</td>'
						+ '<td>'+ dpeSummary.achieved +'</td>'
			    		+ '<td>'+ dpeSummary.percentage +'</td>'
			    		+ '<td></td>'
						+ '</tr>');
					 scheduleTot += dpeSummary.scheduled;
					 achievedTot += dpeSummary.achieved;
					 $.each(value, function(userkey, usergroup) {
						 var dayPlanExecution = JSON.parse(userkey);
						 innerIndex += 1;
						 //date wise
							 $("#tblBody").append(
										'<tr style="background: rgba(255, 228, 196, 0.43);" data-id="'
										+ innerIndex + '" data-parent="' + index + '">'
										+ '<td class="janvary">'+ moment(dayPlanExecution.date).format('DD MMM YYYY') +'</td>'
							    		+ '<td>'+ dayPlanExecution.scheduled +'</td>'
							    		+ '<td>'+ dayPlanExecution.achieved +'</td>'
							    		+ '<td>'+ dayPlanExecution.percentage +'</td>'
							    		+ '<td>'+ dayPlanExecution.alias +'</td>'
										+ '</tr>');
						 
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
						    		+ '<td>'+ (dayPlanExecution.userRemarks==null?"":dayPlanExecution.userRemarks) +'</td>'
						    		+ '</tr>');
		    				}else{
		    					$("#tblBody").append('<tr data-id="'+ dayPlanExecution.date +'" data-parent="' + innerIndex + '">'
			    						+ '<td class="janvary">'+ dayPlanExecution.activityName +' - '+ dayPlanExecution.accountProfileName +' - '+dayPlanExecution.taskListAlias+'</td>'
							    		+ '<td>'+ executionOrder +'</td>'
							    		+ '<td>'+ dayPlanExecution.taskPlanStatus +'</td>'
							    		+ '<td>'+ (dayPlanExecution.userRemarks==null?"":dayPlanExecution.userRemarks) +'</td>'
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
				
				percentageTot = ((achievedTot * 100)/scheduleTot).toFixed(2);
				$('#thSchdld').html('Scheduled (' + scheduleTot + ')');
				$('#thAchvd').html('Achieved (' + achievedTot + ')');
				$('#thPercentage').html('Percentage (' + percentageTot + '%)');
			},
			error : function() {
				$('#tblBody').html("");
			}
		});
	}

	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
			results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}

})();