// Create a SalesTargetAchievedReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserCustomerGroupTargetAchievedReport) {
	this.UserCustomerGroupTargetAchievedReport = {};
}

(function() {
	'use strict';

	var userCustomerGroupTargetAchievedReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	
	var dashboardEmployees = [];
	var otherEmployees = [];
	var allEmployeesMap = new Object();
	
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
			loadUserCustomerGroupTargetAchievedReport();
		});
		
	});
	
	function loadUserCustomerGroupTargetAchievedReport() {
		$('#tblUserCustomerGroupTargetAchievedReport').css('display','none');
		$('#loadingData').css('display','block');
		var employeePid = $("#dbEmployee").val();
//		var productGroupPids = $("#dbProduct").val();
//		if(productGroupPids == "no") {
//			$('#dbProduct option').each(function() {
//				productGroupPids += $(this).val() + ",";
//		    });
//		}
		var fromDate = $('#txtFromMonth').MonthPicker('GetSelectedMonthYear');
		var toDate = $('#txtToMonth').MonthPicker('GetSelectedMonthYear');
		// validate data
		if(employeePid === "no"){
			alert("please select employee");
			return;
		}
		if(fromDate === null){
			alert("please select from month");
			return;
		}
		if(toDate === null){
			alert("please select to month");
			return;
		}
		
		$('#hLoadId').html("Loading...");
		var [fromMonth, fromYear] = fromDate.split('/');
		var [toMonth, toYear] = toDate.split('/');
		fromDate = new Date(fromYear, fromMonth - 1, 1);
		toDate = new Date(toYear, toMonth, 0);
//		console.log(productGroupPids);
		$.ajax({
			url : userCustomerGroupTargetAchievedReportContextPath + "/load-data",
			type : 'GET',
			data : {
				employeePid : employeePid,
				fromDate : convertLocalDateToServer(fromDate),
				toDate : convertLocalDateToServer(toDate)
			},
			success : function(response) {
				console.log(response);
				$('#loadingData').css('display','none');
				if (response) {
					$("#tblUserCustomerGroupTargetAchievedReport").show();
					fillTargetData(response);
				} else {
					console.log("activity not assigned for this user....");
					var trRow = "<tr style='color:red; font-size: large;'><td><b>"
							+ "Activity or product Group not assigned"
							+ "</b></td></tr>";
					$("#tblBody").html(trRow);
					$("#tblUserCustomerGroupTargetAchievedReport").show();
				}
			}
		});
	}
	
	function fillTargetData(targetData) {
		$("#tblUserCustomerGroupTargetAchievedReport tbody").html("");
		
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
		//salesTargetGroupName
		var performanceRow = "";
		$.each(targetData.customerGroupUserTargets, function(customerGroupName, targets) {
			console.log(customerGroupName +" ----" );
			console.log(targets);
			var targetRow = "";
			var achivedRow = "";
			var targetTotal = 0;
			var achivedTotal = 0;
			for(var i = 0, size = targets.length; i < size ; i++) {
				
				var tVolume = targets[i].targetNumber;
				var tAmount = 0;

				targetTotal += tVolume;
				var tVolume1 = (tVolume).toFixed(2);
				targetRow += "<td>"+ tVolume1 +"</td>";
				
				var aAmount = targets[i].achivedNumber;
				achivedTotal += aAmount;
				var aAmount1 = (aAmount).toFixed(2);
				achivedRow += "<td>"+ aAmount1 +"</td>";
			}
			var targetTotal1 = (targetTotal).toFixed(2);
			var achivedTotal1 = (achivedTotal).toFixed(2);
			if(targetTotal!=0 || achivedTotal!=0){
			performanceRow += "<tr><td>" + customerGroupName + "</td>";
			targetRow += "<td>"+targetTotal1 +"</td>";
			achivedRow += "<td>"+ achivedTotal1 +"</td>";
			
			performanceRow += targetRow += achivedRow;
			var achivedPercentage = (achivedTotal / targetTotal) * 100;
			achivedPercentage =	Math.round(achivedPercentage);
			if(achivedPercentage>=100){
				achivedPercentage=100;
			}else if(achivedPercentage<100){
				achivedPercentage=achivedPercentage;
			}else{
				achivedPercentage=0;
			}
			if(achivedPercentage==100){
				performanceRow += "<td style='color: white;background-color: green;'>"+ achivedPercentage+"%" +"</td>";
			}else if(achivedPercentage>=80 && achivedPercentage<100){
				performanceRow += "<td style='color: black;background-color: yellow;'>"+ achivedPercentage+"%" +"</td>";
			}else{
			performanceRow += "<td style='color: white;background-color: red;'>"+ achivedPercentage+"%" +"</td>";
			}
			performanceRow += "</tr>"
			}
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