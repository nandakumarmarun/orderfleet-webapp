// Create a SalesSummaryAchievment object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesSummaryAchievment) {
	this.SalesSummaryAchievment = {};
}

(function() {
	'use strict';
	$( document ).ready(function() {
	$('#txtMonth').MonthPicker({
		MonthFormat: 'M, yy',
		ShowIcon : false
	});
	

	$('input[type=month]').MonthPicker().css('backgroundColor',
			'lightyellow');
	$('#applyBtn').click(function(){
		SalesSummaryAchievment.filter();
	});
	
	$('#btnProductNoTarget').click(function(){
		SalesSummaryAchievment.ProductNoTargets();
	});
	
});
	
	var salesSummaryAchievmentContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var achievedDate=null;
	var employeePid=null;
	var date=null;
	SalesSummaryAchievment.filter = function() {

		
		
		var fromDate = $('#txtMonth').MonthPicker('GetSelectedMonthYear');
		console.log(fromDate);
		if($('#dbEmployee').val() == "-1"){
			alert("Please Select Employee");
			return;
		}
		if($('#dbLocation').val() == "-1"){
			alert("Please Select Location");
			return;
		}
		if(fromDate == null){
			alert("Please Select Month");
			return;
		}
		employeePid=$('#dbEmployee').val();
		
		$('#tblSalesSummaryAchievment').html(
				"<tr><td colspan='2'>Please wait...</td></tr>");
		var [fromMonth, fromYear] = fromDate.split('/');
		fromDate = new Date(fromYear, fromMonth - 1, 1);
		fromDate=convertLocalDateToServer(fromDate);
		achievedDate=fromDate;
		
		console.log(fromDate);
		$.ajax({
			url : salesSummaryAchievmentContextPath + "/monthly-data",
			type : 'GET',
			data : {
				employeePid : employeePid,
				date : achievedDate,
				locationPid:$('#dbLocation').val(),
			},
			success : function(salesSummaryAchievments) {
				addRowsToTable(salesSummaryAchievments);
			}
		});
	}

	
	SalesSummaryAchievment.onLoadLocation = function(){
		if($('#dbEmployee').val()=="-1"){
			return;
		}
		$("#dbLocation").html('<option>Loading...</option>');
		$.ajax({
			url:salesSummaryAchievmentContextPath + "/loadLocation",
			type:"GET",
			data:{
				employeePid:$('#dbEmployee').val(),
			},
			success : function(data){
				$("#dbLocation").html('<option value="-1">Select Location</option>');
				if (data != null && data.length > 0) {
					$.each(data, function(index, location) {
						$("#dbLocation").append(
								'<option value="' + location.pid + '">' + location.name
										+ '</option>');
// $("#dbLocation").val(location.pid);
					});
				}
			},
			
		});
	}
	
/*
 * SalesSummaryAchievment.ProductNoTargets = function() {
 * 
 * var fromDate = $('#txtMonth').MonthPicker('GetSelectedMonthYear');
 * console.log(fromDate); if($('#dbEmployee').val() == "-1"){ return; }
 * if(fromDate == null){ return; } employeePid=$('#dbEmployee').val();
 * 
 * $('#tblSalesSummaryAchievment').html("<tr><td colspan='2'>Please wait...</td></tr>");
 * var [fromMonth, fromYear] = fromDate.split('/'); fromDate = new
 * Date(fromYear, fromMonth - 1, 1);
 * fromDate=convertLocalDateToServer(fromDate); achievedDate=fromDate; $.ajax({
 * url : salesSummaryAchievmentContextPath + "/with-out-target/monthly-data",
 * type : 'GET', data : { employeePid : employeePid, date : achievedDate },
 * success : function(salesSummaryAchievments) {
 * addRowsToTable(salesSummaryAchievments); } }); }
 */
	
	
	SalesSummaryAchievment.save = function() {
		$("#save").prop('disabled', true);
		$(".error-msg").html("");

		var salesSummaryAchievments = [];

		$("#tblSalesSummaryAchievment").find('tr').each(function(i, el) {
			var pid = $(this).find('td').eq(0).find('input.pid').val();
			var groupPid = $(this).find('td').eq(0).find('input.group').val();
			var amount = $(this).find('td').eq(1).find('input').val();
			salesSummaryAchievments.push({
				pid : pid,
				employeePid : $('#dbEmployee').val(),
				salesTargetGroupPid : groupPid,
				amount : amount,
				achievedDate : achievedDate,
				locationPid:$('#dbLocation').val()
			})
		});

		console.log(salesSummaryAchievments);

		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : salesSummaryAchievmentContextPath,
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(salesSummaryAchievments),
			success : function(result) {
				SalesSummaryAchievment.filter();
				$("#save").prop('disabled', false)
			},
			error : function(textStatus, errorThrown) {
				$("#save").prop('disabled', false);
			},
		});
	}

	function convertLocalDateToServer (date) {
		if (date) {
			return moment(date).format('YYYY-MM-DD');
		} else {
			return "";
		}
	}
	
	function addRowsToTable(salesSummaryAchievments) {

		if (salesSummaryAchievments) {
			$('#tblSalesSummaryAchievment').html("");
			$.each(salesSummaryAchievments, function(index,
					salesSummaryAchievment) {
				$('#tblSalesSummaryAchievment').append(
						"<tr><td><input type='hidden' class='group' value='"
								+ salesSummaryAchievment.salesTargetGroupPid
								+ "'/><input type='hidden' class='pid' value='"
								+ salesSummaryAchievment.pid + "'/>"
								+ salesSummaryAchievment.salesTargetGroupName
								+ "</td><td>" + "<input type='number' value='"
								+ salesSummaryAchievment.amount + "'/></tr>");
			});
		} else {
			$('#tblSalesSummaryAchievment').html(
					"<tr><td colspan='2'>Activity not assigned</td></tr>")
		}
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = activityUserTargetContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = activityUserTargetContextPath;
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