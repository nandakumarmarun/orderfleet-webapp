// Create a UserGrowthReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserGrowthReport) {
	this.UserGrowthReport = {};
}

(function() {
	'use strict';

	var userGrowthReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	
	
	$( document ).ready(function() {
		
		$('#txtFromMonth').MonthPicker({
			MonthFormat: 'M, yy',
			ShowIcon : false,
			SelectedMonth: 0
		});
		$('#txtToMonth').MonthPicker({
			MonthFormat: 'M, yy',
			ShowIcon : false,
			SelectedMonth: 0
		});

		$('input[type=month]').MonthPicker().css('backgroundColor',
				'lightyellow');
		
		$('#applyBtn').click(function(){
			loadUserGrowthReport();
		});
		loadUserGrowthReport();
	});
	
	function loadUserGrowthReport() {
		$('#tblUserGrowthReport').css('display','none');
		$('#loadingData').css('display','block');
		
		var companyPid= $('#dbCompany').val();
		var fromMonth = $('#txtFromMonth').MonthPicker('GetSelectedMonthYear');
		var toMonth = $('#txtToMonth').MonthPicker('GetSelectedMonthYear');
		
		// validate data
		if(fromMonth === null){
			alert("please select from month");
			return;
		}
		if(toMonth === null){
			alert("please select to month");
			return;
		}
		$('#hLoadId').html("Loading...");
		var [fromMonth, fromYear] = fromMonth.split('/');
		var [toMonth, toYear] = toMonth.split('/');
		fromMonth = new Date(fromYear, fromMonth - 1, 1);
		toMonth = new Date(toYear, toMonth, 0);
		$.ajax({
			url : userGrowthReportContextPath + "/load-data",
			type : 'GET',
			data : {
				companyPid:companyPid,
				fromMonth : convertLocalDateToServer(fromMonth),
				toMonth : convertLocalDateToServer(toMonth)
			},
			success : function(response) {
				$('#loadingData').css('display','none');
				console.log(response);
				if (response) {
					$("#tblUserGrowthReport").show();
					fillTargetData(response);
				}else {
					$("#tblUserGrowthReport tbody").html("<tr><td colspan='5' align='center'>No data available</td></tr>");
				}
			}
		});
	}
	
	function fillTargetData(targetData) {
		$("#tblUserGrowthReport tbody").html("");
		createTableHeaders(targetData.htmlTblHeadermonths);
		createTableBody(targetData.companyMonthWiseCount);
	}
	
	function createTableHeaders(tblHeaderData) {
		let thStyle = "color: white; background-color: #35aa47; text-align: center; vertical-align: middle; height: 50px";
		let thHeader = "<tr><th style='"+ thStyle +"'>Company</th>";
		$.each(tblHeaderData, function(index, head) {
			thHeader += "<th style='"+ thStyle +"'>"+ head + "</th>";
		});
		thHeader += "<th id='totalUser' style='"+ thStyle +"'>Total</th></tr>";
		$("#tblUserGrowthReport thead").html(thHeader);
	}
	
	function createTableBody(tblBodyData) {
		let rowHtml = "";
		let totalUserCount = 0;
		$.each(tblBodyData, function( key, value ) {
			rowHtml += "<tr><td style='height: 50px; '>"+ key + "</td>";
			 let rowCount = 0;
			 $.each(value, function(key, val) {
				 let colour="";
				 if(val > 0) {
					 colour="red";
				 }
				 rowCount += val;
				 rowHtml += "<td style=' text-align: center; vertical-align: middle; height: 50px; color: "+colour+";'>"+ val + "</td>";
				 totalUserCount += val;
			});
			rowHtml += "<td style=' text-align: center; vertical-align: middle; height: 50px; color: red'>"+ rowCount + "</td></tr>";
			$("#tblUserGrowthReport tbody").html(rowHtml);
		});
		$("#totalUser").html("Total (" + totalUserCount + ")");
		//$("#lbl_totalUSR").html(totalUserCount);
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