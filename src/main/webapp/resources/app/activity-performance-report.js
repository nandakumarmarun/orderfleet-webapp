// Create a ActivityPerformanceReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ActivityPerformanceReport) {
	this.ActivityPerformanceReport = {};
}

(function() {
	'use strict';

	var activityPerformanceContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	$( document ).ready(function() {
		
		getEmployees();
		
		$('#txtFromMonth').MonthPicker({
			MonthFormat: 'M, yy',
			ShowIcon : false
		});
		$('#txtToMonth').MonthPicker({
			MonthFormat: 'M, yy',
			ShowIcon : false
		});

		$('input[type=month]').MonthPicker().css('backgroundColor',
				'lightyellow');
		
		$('#applyBtn').click(function(){
			loadActivityPerformanceData();
		});
	});
	
	function loadActivityPerformanceData() {
		var employeePid = $("#dbEmployee").val();
		var activityTypePid = $("#dbActivityType").val();
		var fromDate = $('#txtFromMonth').MonthPicker('GetSelectedMonthYear');
		var toDate = $('#txtToMonth').MonthPicker('GetSelectedMonthYear');
		// validate data
		if(employeePid === "no"){
			alert("Please select Employee");
			return;
		}
		if(fromDate === null){
			alert("Please select from date");
			return;
		}
		if(toDate === null){
			alert("Please select to date");
			return;
		}
		
		var trRow = "<tr style='color:black; font-size: large;'><td><b>" + "loading..." + "</b></td></tr>";
		$("#tblBody").html(trRow)
		
		
		var [fromMonth, fromYear] = fromDate.split('/');
		var [toMonth, toYear] = toDate.split('/');
		fromDate = new Date(fromYear, fromMonth - 1, 1);
		toDate = new Date(toYear, toMonth, 0);
		$.ajax({
			url : activityPerformanceContextPath + "/load-data",
			type : 'GET',
			data : {
				employeePid : employeePid,
				activityTypePid:activityTypePid,
				fromDate : convertLocalDateToServer(fromDate),
				toDate : convertLocalDateToServer(toDate)
			},
			success : function(response) {
				console.log(response);
				if (response) {
					$("#tblMarketingPerformance").show();
					fillTargetData(response);
				} else {
					console.log("activity not assigned for this user....");
					var trRow = "<tr style='color:red; font-size: large;'><td><b>"
							+ "Activity not assigned"
							+ "</b></td></tr>";
					console.log("1");
					$("#tblBody").html(trRow);
					$("#tblMarketingPerformance").show();
				}
			}
		});
	}
	
	function fillTargetData(targetData) {
		$("#tblMarketingPerformance tbody").html("");
		
		var count = targetData.monthList.length;
		$("#th_target").removeAttr("colspan");
		$("#th_target").attr("colspan", count);
		$("#th_achived").removeAttr("colspan");
		$("#th_achived").attr("colspan", count);
		
		var newRowContent = "";
		$.each(targetData.monthList,function(index, month) {
			newRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ month + "</td>";
		});
		newRowContent += newRowContent;
		$("#txtRowHeader").html(newRowContent);
		
		var performanceRow = "";
		$.each(targetData.activityUserTargets, function(activityName, targets) {
			performanceRow += "<tr><td>" + activityName + "</td>";
			
			var targetRow = "";
			var achivedRow = "";
			var targetTotal = 0;
			var achivedTotal = 0;
			for(var i = 0, size = targets.length; i < size ; i++) {
				targetRow += "<td>"+ targets[i].targetNumber +"</td>";
				achivedRow += "<td>"+ targets[i].achivedNumber +"</td>";
				
				targetTotal += targets[i].targetNumber;
				achivedTotal += targets[i].achivedNumber;
			}
			targetRow += "<td>"+targetTotal +"</td>";
			achivedRow += "<td>"+ achivedTotal +"</td>";
			
			performanceRow += targetRow += achivedRow;
			
			var achivedPercentage = Math.round((achivedTotal / targetTotal) * 100);
			achivedPercentage =	achivedPercentage > 0 ? achivedPercentage : 0;
			achivedPercentage =	achivedPercentage < 100 ? achivedPercentage :100;
			if(achivedPercentage==100){
				performanceRow += "<td style='color: white;background-color: green;'>"+ achivedPercentage+"%" +"</td>";
			}else if(achivedPercentage>=80 && achivedPercentage<100){
				performanceRow += "<td style='color: black;background-color: yellow;'>"+ achivedPercentage+"%" +"</td>";
			}else{
			performanceRow += "<td style='color: white;background-color: red;'>"+ achivedPercentage+"%" +"</td>";
			}
			performanceRow += "</tr>"
		});
		$("#tblBody").html(performanceRow);
	}
	

	function convertLocalDateToServer (date) {
		if (date) {
			return moment(date).format('YYYY-MM-DD');
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