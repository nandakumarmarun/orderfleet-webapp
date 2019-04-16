if (!this.LocationSalesTargetAchievedReport) {
	this.LocationSalesTargetAchievedReport = {};
}

(function() {
	'use strict';

	var salesLocationwiseReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	
	var dashboardEmployees = [];
	var otherEmployees = [];
	var allEmployeesMap = new Object();
	
	$( document ).ready(function() {
		
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		
		$('#applyBtn').click(function(){
			loadLocationWiseSales();
		});
		
	});
	
	function loadLocationWiseSales() {
		$('#tblSalesTargetAchievedReport').css('display','none');
		$('#loadingData').css('display','block');
		
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		
		// validate data
		/*if(employeePid === "no"){
			alert("please select employee");
			return;
		}
		var productGroupPid = $("#dbProduct").val();
		if(productGroupPid === "no"){
			alert("please select productgroup");
			return;
		}*/
		$('#hLoadId').html("Loading...");
		
		$.ajax({
			url : salesLocationwiseReportContextPath + "/load-data",
			type : 'GET',
			data : {
				territoryId:$("#dbLocation").val(),
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val(),
				displayMode:$("#dbDisplayMode").val()
			},
			success : function(response) {
				$('#loadingData').css('display','none');
				if (response) {
					$("#tblSalesTargetAchievedReport").show();
					fillTargetData(response);
				} else {
					console.log("Product group not assigned for this user....");
					var trRow = "<tr style='color:red; font-size: large;'><td><b>"
							+ "Product Group not assigned"
							+ "</b></td></tr>";
					$("#tblBody").html(trRow);
					$("#tblSalesTargetAchievedReport").show();
				}
			}
		});
	}
	
	function fillTargetData(targetData) {
		
		$("#tblSalesTargetAchievedReport tbody").html("");
		console.log(targetData);
		var count = targetData.dateHeaderList.length;
		$("#th_target").removeAttr("colspan");
		$("#th_target").attr("colspan", count);
		$("#th_achived").removeAttr("colspan");
		$("#th_achived").attr("colspan", count);
		
		var newRowContent = "";
		$.each(targetData.dateHeaderList,function(index, name) {
			newRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ name + "</td>";
		});
		$("#txtRowHeader").html(newRowContent);
		var performanceRow = "";
		$.each(targetData.salesGroupAchieved, function(groupName, achievements) {
			var achivedRow = "";
			var achivedTotal = 0;
			for(var i = 0, size = achievements.length; i < size ; i++) {
				var aAmount = achievements[i];
				achivedTotal += aAmount;
				var aAmount1 = (aAmount).toFixed(2);
				achivedRow += "<td>"+ aAmount1 +"</td>";
			}
			var achivedTotal1 = (achivedTotal).toFixed(2);
			if(achivedTotal != 0){
				performanceRow += "<tr><td>" + groupName + "</td>";
				achivedRow += "<td>"+ achivedTotal1 +"</td>";
				
				performanceRow += achivedRow;
				performanceRow += "</tr>";
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

	LocationSalesTargetAchievedReport.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
			if($('#dbDateSearch').val() == "TYR" || $('#dbDateSearch').val() == "LYR"){
				$("#dbDisplayMode").html('<option value="QuarterWise">Quarterly</option><option value="MonthWise">Monthly</option>');
			}else if ($('#dbDateSearch').val()=="TMT" || $('#dbDateSearch').val()=="LMT") {
				$("#dbDisplayMode").html('<option value="MonthWise">Monthly</option>');
				/*$("#dbDisplayMode").html('<option value="MonthWise">Monthly</option><option value="WeekWise">Weekly</option>');*/
			} else if ($('#dbDateSearch').val()=="TWK" || $('#dbDateSearch').val()=="LWK") {
				$("#dbDisplayMode").html('<option value="WeekWise">Weekly</option><option value="DayWise">Daily</option>');
			}else {
				$("#dbDisplayMode").html('<option value="DayWise">Daily</option>');
			}
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}
})();