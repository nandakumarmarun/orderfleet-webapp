// Create a SalesTargetAchievedReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesGroupTargetComparison) {
	this.SalesGroupTargetComparison = {};
}

(function() {
	'use strict';

	var salesGroupTargetComparisonContextPath = location.protocol + '//'
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
			loadSalesTargetAchievedReport();
		});
		
	});

	function loadSalesTargetAchievedReport() {
		$('#tblSalesTargetAchievedReport').css('display','none');
		$('#loadingData').css('display','block');
		
		var productGroupPids = $("#dbProduct").val();
	    var period;
		if(productGroupPids == "no") {
			$('#dbProduct option').each(function() {
				productGroupPids += $(this).val() + ",";
		    });
		}
		var fromDate = $('#txtFromMonth').MonthPicker('GetSelectedMonthYear');
		var fromDate1 = $('#txtToMonth').MonthPicker('GetSelectedMonthYear');
		var toDate;
		var toDate1;
		console.log("FromDate :"+fromDate +"To Date :"+ fromDate1);
		// validate data
      
		if(fromDate === null){
			alert("please select from month");
			return;
		}
		if(fromDate1 === null){
			alert("please select from month and year to compare");
			return;
		}
		
		$('#hLoadId').html("Loading...");
		var [fromMonth, fromYear] = fromDate.split('/');
		var [fromMonth1, fromYear1] = fromDate1.split('/');
		if($('#dbDateSearch').val() == "MONTHLY" ){
         fromDate = new Date(fromYear, fromMonth - 1, 1);// jan 1 2023
         toDate =new Date(fromYear, fromMonth ,0);// jan 31 2023
         fromDate1 = new Date(fromYear1, fromMonth1-1, 1);// jan 1 2022
         toDate1 = new Date(fromYear1, fromMonth1, 0);// jan 31 2022
        }else if($('#dbDateSearch').val() == "QUARTERLY" ){
         toDate = addMonthsToDate(fromDate);
         var [toMonth, toYear] = toDate.split('/');
         fromDate = new Date(fromYear, fromMonth - 1, 1);
         toDate =new Date(toYear, toMonth ,0);
          toDate1 = addMonthsToDate(fromDate1);
         fromDate1 = new Date(fromYear1, fromMonth1-1, 1)
         var [toMonth2, toYear2] = toDate1.split('/');
         toDate1 = new Date(toYear2, toMonth2 ,0);
          }else if($('#dbDateSearch').val() == "HALFYEARLY") {
           toDate = addMonthsToDate(fromDate);
           var [toMonth, toYear] = toDate.split('/');
          fromDate = new Date(fromYear, fromMonth - 1, 1);
          toDate =new Date(toYear, toMonth ,0);
          toDate1 = addMonthsToDate(fromDate1);
          fromDate1 = new Date(fromYear1, fromMonth1-1, 1)
          var [toMonth2, toYear2] = toDate1.split('/');
          toDate1 = new Date(toYear2, toMonth2 ,0);
          }else if($('#dbDateSearch').val() == "YEARLY"){
           toDate = addMonthsToDate(fromDate);
           var [toMonth, toYear] = toDate.split('/');
           fromDate = new Date(fromYear, fromMonth - 1, 1);
           toDate =new Date(toYear, toMonth ,0);
           toDate1 = addMonthsToDate(fromDate1);
           fromDate1 = new Date(fromYear1, fromMonth1-1, 1)
           var [toMonth2, toYear2] = toDate1.split('/');
             toDate1 = new Date(toYear2, toMonth2 ,0);
          }



		$.ajax({
			url : salesGroupTargetComparisonContextPath + "/load-data",
			type : 'GET',
			data : {
               
				productGroupPids : productGroupPids,
				fromDate : convertLocalDateToServer(fromDate),
				toDate : convertLocalDateToServer(toDate),
				fromDate1 : convertLocalDateToServer(fromDate1),
		      	toDate1 : convertLocalDateToServer(toDate1),
				period : $('#dbDateSearch').val(),
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
		
		var newRowContent = "";
		var firstRowContent = "";
		var secondRowContent = "";
		var thirdRowContent= "";
		var fourthRowContent = "";
		var totalRow = "";
		$
       		.each(
        			targetData,
        				function(index, data) {
        				console.log("Period :"+data.period)
	if(data.period=="MONTHLY"){

			firstRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate1 + "</td>";
            secondRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate2 + "</td>";
            thirdRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate1 + "</td>";
            fourthRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate2 + "</td>";
   }else if(data.period =="QUARTERLY"||"HALFYEARLY"||"YEARLY")
   {
        firstRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate1+" to "+ data.toDate + "</td>";
        secondRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate2+" to "+data.toDate1 + "</td>";
        thirdRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate1+" to "+ data.toDate + "</td>";
        fourthRowContent += "<td style='color: white; background-color: #35aa47; text-align: center; vertical-align: middle;'>"+ data.fromDate2+" to "+ data.toDate1+ "</td>";
      }
		newRowContent += firstRowContent +=secondRowContent +=thirdRowContent +=fourthRowContent;
		$("#txtRowHeader").html(newRowContent);
		$
               		.each(
               		data.salesComparisonDTOS,
               		function(index,salescomparison){
               		$('#tblSalesTargetAchievedReport')
                    							.append(
                    									"<tr data-id='"
                    									+ index
                    									+ "'><td>"
                    									+ (salescomparison.salesGroupName == null ? ""
                    									 : salescomparison.salesGroupName)
                    									+ "</td><td>"
                    									+ (salescomparison.targetTotal1 == null ? "":(salescomparison.targetTotal1).toFixed(2))
                    									+ "</td><td>"
                    									+ (salescomparison.targetTotal2 == null ? "":(salescomparison.targetTotal2).toFixed(2))
                                                        + "</td><td>"
                                                        + (salescomparison.achievedTotal1 == null ? "" :(salescomparison.achievedTotal1).toFixed(2))
                                                      	+ "</td><td>"
                                                      	+ (salescomparison.achievedTotal2 == null ? "" :(salescomparison.achievedTotal2).toFixed(2))
                                               			+ "</td></tr>");
               		});

		

	});}
	SalesGroupTargetComparison.showDatePicker = function() {
    		$("#txtFromMonth").val("");
    		$("#txtToMonth").val("");
    		if ($('#dbDateSearch').val() == "MONTHLY" || "QUARTERLY" ||"HALFYEARLY"||"YEARLY") {
    			$(".custom_date1").addClass('show');
    			$(".custom_date2").addClass('show');
    			$(".custom_date1").removeClass('hide');
    			$(".custom_date2").removeClass('hide');
    			$('#divDatePickers').css('display', 'initial');
    		} else {
    			$(".custom_date1").addClass('hide');
    			$(".custom_date1").removeClass('show');
    			$(".custom_date2").addClass('hide');
    			$(".custom_date2").removeClass('show');
    			$('#divDatePickers').css('display', 'none');
    		}

    	}

	
	function convertLocalDateToServer (date) {
		if (date) {
			return moment(date).format('YYYY-MM-DD');
		} else {
			return "";
		}
	}

	function addMonthsToDate(date)
	{
	 const [month, year] = date.split('/').map(Number);
          const inputDate = new Date(year, month - 1, 1, 0, 0, 0, 0);
           if($('#dbDateSearch').val() == "QUARTERLY"){
           inputDate.setMonth(inputDate.getMonth() + 2);
           }else if($('#dbDateSearch').val() == "HALFYEARLY"){
           inputDate.setMonth(inputDate.getMonth() + 5);
           }else if($('#dbDateSearch').val() == "YEARLY"){
           inputDate.setMonth(inputDate.getMonth() + 11);
           }
           const resultDateStr = `${inputDate.getMonth() + 1}/${inputDate.getFullYear()}`;
               return resultDateStr;
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