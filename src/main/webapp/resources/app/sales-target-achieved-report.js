// Create a SalesTargetAchievedReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesTargetAchievedReport) {
	this.SalesTargetAchievedReport = {};
}

(function() {
	'use strict';

	var salesTargetAchievedReportContextPath = location.protocol + '//'
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
			loadSalesTargetAchievedReport();
		});
		
	});
	
	function loadSalesTargetAchievedReport() {
		$('#tblSalesTargetAchievedReport').css('display','none');
		$('#loadingData').css('display','block');
		var employeePid = $("#dbEmployee").val();
		var productGroupPids = $("#dbProduct").val();
	    var inclSubordinate = $('#inclSubOrdinates').is(":checked");
		if(productGroupPids == "no") {
			$('#dbProduct option').each(function() {
				productGroupPids += $(this).val() + ",";
		    });
		}
		var fromDate = $('#txtFromMonth').MonthPicker('GetSelectedMonthYear');
		var toDate = $('#txtToMonth').MonthPicker('GetSelectedMonthYear');
		// validate data
      if(employeePid === "all"){
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
		console.log(productGroupPids);
		$.ajax({
			url : salesTargetAchievedReportContextPath + "/load-data",
			type : 'GET',
			data : {
                employeePid : employeePid,
				productGroupPids : productGroupPids,
				fromDate : convertLocalDateToServer(fromDate),
				toDate : convertLocalDateToServer(toDate),
				inclSubordinate : inclSubordinate
			},
			success : function(response) {
				$('#loadingData').css('display','none');
				if (response) {
					$("#tblSalesTargetAchievedReport").show();
					fillTargetData(response);
				} else {
					console.log("activity not assigned for this user....");
					var trRow = "<tr style='color:red; font-size: large;'><td><b>"
							+ "Activity or product Group not assigned"
							+ "</b></td></tr>";
					$("#tblBody").html(trRow);
					$("#tblSalesTargetAchievedReport").show();
				}
			}
		});
	}
	
	function fillTargetData(targetData) {
		$("#tblSalesTargetAchievedReport tbody").html("");
		
		var count = targetData.monthList.length;
		console.log(targetData.monthList) 
		$("#th_target").removeAttr("colspan");
		$("#th_target").attr("colspan", count);
		$("#th_achived").removeAttr("colspan");
		$("#th_achived").attr("colspan", count);
		
		var newRowContent = "";
		var totalRow = "";
		$.each(targetData.monthList,function(index, month) {
			newRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ month + "</td>";
		
		     });
		
		
		newRowContent += newRowContent;
		$("#txtRowHeader").html(newRowContent);
		
		
		var performanceRow = "";
         var totalRow = "";
         var targetsumRow="";
       var achivedsumRow="";
       var achievedtotalRow ="";
		var sumTarget=0;
		var sumAchieved=0;
		
	 $.each(targetData.salesTargetFinalList,function(index,salesTargetGroupUserTargetMap){
		 
	
		$.each(salesTargetGroupUserTargetMap, function(salesTargetGroupName, targets) {
		
		
			var targetRow = "";
			var achivedRow = "";
			var targetTotal = 0;
			var achivedTotal = 0;
			var cloumtotal = 0;
			var cloumn = 0 ;
			var name;
			
			for(var i = 0, size = targets.length; i < size ; i++) {
              
				if(targets[i].userName != null)
				{
				var tVolume = targets[i].volume;
				
				var tAmount = targets[i].amount;
				
			   name = targets[i].userName;
				
			   

				if(tAmount != 0){
					targetTotal += tAmount;
					 var tAmount1 = (tAmount).toFixed(2);
					 
					targetRow += "<td>"+ tAmount1 +"</td>";
					
				}else{
					targetTotal += tVolume;
					 var tVolume1 = (tVolume).toFixed(2);
					
					targetRow += "<td>"+ tVolume1 +"</td>";

				}
				
				var aAmount = targets[i].achievedAmount;
				achivedTotal += aAmount;
				var aAmount1 = (aAmount).toFixed(2);
				achivedRow += "<td>"+ aAmount1 +"</td>";
				}	
			}

			var targetTotal1 = (targetTotal).toFixed(2);
			console.log((targetTotal).toFixed(2))
			var achivedTotal1 = (achivedTotal).toFixed(2);
			if(targetTotal!=0 || achivedTotal!=0){
			performanceRow += "<tr><td>" +name+ "</td>";
			performanceRow += "<td>" +salesTargetGroupName + "</td>";
			performanceRow += "<td>"+targetTotal1 +"</td>";
			performanceRow += "<td>"+ achivedTotal1 +"</td>";
			
		
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
			performanceRow += targetRow += achivedRow;
			sumTarget +=targetTotal;	
			sumAchieved +=achivedTotal;
			performanceRow += "</tr>"
			}
		
		});
		
	 });
		
    var abc=0;
    var itemRow="";
    targetData.totalList.forEach(myfunction)
    	
    	function myfunction(item)
       {
    		abc = item;
    		
    		itemRow +="<td>"+(abc).toFixed(2) +"</td>";
    	}
    	var cba=0;
    	targetData.achievedList.forEach(sumFunction)
    	function sumFunction(item)
    	{
    		cba =item;
    		achivedsumRow += "<td>"+ (cba).toFixed(2)+"</td>";
    	}
 
    	totalRow += "<br>"
		totalRow +="<tr> <td style ='text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: bold; font-size:24px' colspan='2'>"+"Total"+"</td>";
		targetsumRow += "<td>"+(sumTarget).toFixed(2) +"</td>";
		achievedtotalRow+="<td>"+ (sumAchieved).toFixed(2) +"</td>";
		
		var percentageRow ="";
		var achivedPercentages = (sumAchieved / sumTarget) * 100;
		achivedPercentages =	Math.round(achivedPercentages);
		if(achivedPercentages>=100){
			achivedPercentages=100;
		}else if(achivedPercentages<100){
			achivedPercentages=achivedPercentages;
		}else{
			achivedPercentages=0;
		}
		if(achivedPercentages==100){
			percentageRow += "<td style='color: white;background-color: green;'>"+ achivedPercentages+"%" +"</td>";
		}else if(achivedPercentages>=80 && achivedPercentages<100){
			percentageRow += "<td style='color: black;background-color: yellow;'>"+ achivedPercentages+"%" +"</td>";
		}else{
			percentageRow += "<td style='color: white;background-color: red;'>"+ achivedPercentages+"%" +"</td>";
		}
		achivedsumRow += "</tr>" 	
		performanceRow += totalRow +=targetsumRow += achievedtotalRow += percentageRow +=itemRow += achivedsumRow;
	
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