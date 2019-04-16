if (!this.SalesTargetAchievedBlockReport) {
	this.SalesTargetAchievedBlockReport = {};
}

(function() {
	'use strict';
	
	var salesTargetAchievedBlockReportContextPath = location.protocol + '//'
	+ location.host + location.pathname;
	
	$( document ).ready(function() {
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
	loadSalesTargetAchievedBlockReport();
		});
	});

		function loadSalesTargetAchievedBlockReport() {
			$('#tblSalesTargetAchievedReport').css('display','none');
			$('#loadingData').css('display','block');
			var employeePid = $("#dbEmployee").val();
			var productGroupPid = $("#dbProduct").val();
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
			var [fromMonth, fromYear] = fromDate.split('/');
			var [toMonth, toYear] = toDate.split('/');
			fromDate = new Date(fromYear, fromMonth - 1, 1);
			toDate = new Date(toYear, toMonth, 0);
			$.ajax({
				url : salesTargetAchievedBlockReportContextPath + "/load-data",
				type : 'GET',
				data : {
					employeePid : employeePid,
					productGroupPid:productGroupPid,
					fromDate : convertLocalDateToServer(fromDate),
					toDate : convertLocalDateToServer(toDate)
				},
				success : function(response) {
					$('#loadingData').css('display','none');
					if (response) {
						$("#tblSalesTargetAchievedReport").show();
						fillTargetData(response);
						$('.collaptable').aCollapTable({
							startCollapsed : true,
							addColumn : false,
							plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
							minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
						});
					} else {
						console.log("activity not assigned for this user....");
						var trRow = "<tr style='color:red; font-size: large;'><td><b>"
								+ "Activity not assigned"
								+ "</b></td></tr>";
						$("#tblBody").html(trRow)
					}
				}
			});
		}
		
		function fillTargetData(targetData) {
			$("#tblSalesTargetAchievedReport tbody").html("");
			var count = targetData.monthList.length;
			$("#th_target").removeAttr("colspan");
			$("#th_target").attr("colspan", count);
			$("#th_achived").removeAttr("colspan");
			$("#th_achived").attr("colspan", count);
			
			var newRowContent = "";
			var noOfMonths=0;
			$.each(targetData.monthList,function(index, month) {
				noOfMonths=noOfMonths+1;
				newRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ month + "</td>";
			});
			newRowContent += newRowContent;
			$("#txtRowHeader").html(newRowContent);
			
			var performanceRow = "";
			$("#tblBody").html("");
			var tblIndex = 1;
			var dataId = 1000;
			$.each(targetData.salesTargetGroupUserTargets, function(salesTargetGroupName, targets) {
				var grandTarget = 0;
				var grandAchieved = 0;
				performanceRow += '<tr data-id="'+ tblIndex +'" data-parent=""><td>' + salesTargetGroupName + '</td>';
				var targetRow = "";
				var achivedRow = "";
				var	dayWiseMonth="";
				
				for(var i=0;i<noOfMonths;i++){
				var target = targets[i];
				var targetTotal = (target.totalTarget).toFixed(2);
				var achivedTotal = (target.achievedAmount).toFixed(2);
				
				targetRow += "<td>"+targetTotal +"</td>";
				achivedRow += "<td>"+ achivedTotal +"</td>";
				
				grandTarget += target.totalTarget;
				grandAchieved += target.achievedAmount;
				
				// day wise data
				$.each(target.salesTargetAchievedDTOs, function(val, dayWiseTargetAchieved) {
					
					var achivedDayWisePercentage = (dayWiseTargetAchieved.achievedAmountVolume / dayWiseTargetAchieved.targetAmountVolume) * 100;
					achivedDayWisePercentage =	Math.round(achivedDayWisePercentage);
					if(achivedDayWisePercentage >= 100){
						achivedDayWisePercentage = 100;
					}else if(dayWiseTargetAchieved.achievedAmountVolume==0 && dayWiseTargetAchieved.targetAmountVolume==0){
						achivedDayWisePercentage=0;
					}
					var	colTd="";
					if(achivedDayWisePercentage == 100){
						colTd+= "<td style='color: white;background-color: green;'>"+ achivedDayWisePercentage+"%" +"</td>";
					}else if(achivedDayWisePercentage>=80 && achivedDayWisePercentage<100){
						colTd += "<td style='color: black;background-color: yellow;'>"+ achivedDayWisePercentage+"%" +"</td>";
					}else{
						colTd += "<td style='color: white;background-color: red;'>"+ achivedDayWisePercentage+"%" +"</td>";
					}
					
					dayWiseMonth += '<tr data-id="'+ dataId +'" data-parent="' + tblIndex + '">'
					+ '<td class="janvary">'+ moment(dayWiseTargetAchieved.dayDate,
					"YYYY/MM/DD").format("DD-MM-YYYY") +'</td>'
					+ '<td>'+ dayWiseTargetAchieved.targetAmountVolume +'</td>'
					+ '<td></td>'
					+ '<td>'+ dayWiseTargetAchieved.achievedAmountVolume +'</td>'
					+ '<td></td>'+ colTd
					+ '</tr>';
				});
				}
				tblIndex += 1;
				dataId +=1;
				performanceRow+=targetRow;
				performanceRow+="<td>"+(grandTarget).toFixed(2) +"</td>";
				performanceRow+=achivedRow;
				performanceRow+="<td>"+(grandAchieved).toFixed(2) +"</td>";
				
				var achivedPercentage = (grandAchieved / grandTarget) * 100;
				achivedPercentage =	Math.round(achivedPercentage);
				if(achivedPercentage >= 100){
					achivedPercentage = 100;
				}else if(grandAchieved==0 && grandTarget==0){
					achivedPercentage=0;
				}
				
				if(achivedPercentage == 100){
					performanceRow += "<td style='color: white;background-color: green;'>"+ achivedPercentage+"%" +"</td>";
				}else if(achivedPercentage>=80 && achivedPercentage<100){
					performanceRow += "<td style='color: black;background-color: yellow;'>"+ achivedPercentage+"%" +"</td>";
				}else{
				performanceRow += "<td style='color: white;background-color: red;'>"+ achivedPercentage+"%" +"</td>";
				}
				performanceRow += "</tr>";
				
				performanceRow+=dayWiseMonth;
				$('#loadingData').css('display','none');
				$("#tblBody").html(performanceRow);
			});
		}

		function convertLocalDateToServer (date) {
			if (date) {
				return moment(date).format('YYYY-MM-DD');
			} else {
				return "";
			}
		}
})();