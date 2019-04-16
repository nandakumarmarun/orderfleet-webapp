if (!this.TimeUtilization) {
	this.TimeUtilization = {};
}

(function() {
	'use strict';

	var timeUtilizationContextPath = location.protocol + '//' + location.host
	+ location.pathname;

	$(document).ready(function() {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

	});



	TimeUtilization.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if ($("#dbUser").val() == "no") {
			alert("Please Select User")
				return;
		}
		var filterValue= $("#dbDateSearch").val();
		$('#tBodyTimeUtilization').html(
			"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : timeUtilizationContextPath + "/filter",
				type : 'GET',
				data : {
					userPid : $("#dbUser").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val()
				},
				success : function(response) {
					$('#tBodyTimeUtilization').html("");
					if (response.length == 0) {
						$('#tBodyTimeUtilization')
							.html(
								"<tr><td colspan='6' align='center'>No data available</td></tr>");
						return;
					}
					
					var html = "";
					var indexNumber = 0;
					var innerIndex = 100;
					var unspecifyTime=0;
					var activeTime=0;
					//date wise
					$.each(response, function(key, value) {
						 var dpeSummary = JSON.parse(key);
						 indexNumber += 1;
						 $("#tBodyTimeUtilization").append('<tr style="background: beige;" data-id="' + indexNumber + '" data-parent="">'
						 	+ '<td class="janvary">' + moment(dpeSummary.date).format('DD MMM YYYY') + '</td>'
							+ '<td><b>Unspecified Time -> </b>'+ dpeSummary.unspecifiedTimeSpend +'</td>'
							+ '<td><b>Active Time -> </b>'+ dpeSummary.activityTimeSpend +'</td>'
							+ '<td></td>'+ '</tr>');
						 
						 $.each(value, function(index, time) {
							 innerIndex += 1;
							 if(time.description=="Unspecified"){
								 var a = new Date(time.fromDateTime); 
								 var b = new Date(time.toDateTime);
								 if(time.fromDateTime !=null && time.toDateTime!=null){
								 var d = (b-a);
								 unspecifyTime+=d;
								 }
							 }else if(time.description=="Attendance"){
							 }else{
								 var a = new Date(time.fromDateTime); 
								 var b = new Date(time.toDateTime);
								 if(time.fromDateTime !=null && time.toDateTime!=null){
								 var d = (b-a);
								 activeTime+=d;
								 }
							 }
							 //user wise
							 $("#tBodyTimeUtilization").append(
									'<tr style="background: rgba(255, 228, 196, 0.43);" data-id="'+ innerIndex + '" data-parent="' + indexNumber + '">'
									+ '<td class="janvary">'+ time.description +'</td>'
						    		+ '<td>'+ convertDateFromServerToTime(time.fromDateTime) +'</td>'
						    		+ '<td>'+ convertDateFromServerToTime(time.toDateTime) +'</td>'
						    		+ '<td>'+ time.timeSpent +'</td>'+ '</tr>');
						 });
					});
					var unspecify=msToTime(unspecifyTime);
					var active=msToTime(activeTime);
					var total=msToTime(unspecifyTime+activeTime);
					$("#lblTotalUnspecifiedTime").html(unspecify);
					$("#lblTotalActiveTime").html(active);
					$("#lblGrandTotalTime").html(total);
					$('.collaptable').aCollapTable({
						startCollapsed : true,
						addColumn : false,
						plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
						minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
					});
					
				}
			});
	}

	function msToTime(s) {
		 function pad(n, z) {
			    z = z || 2;
			    return ('00' + n).slice(-z);
			  }
		 
		  var ms = s % 1000;
		  s = (s - ms) / 1000;
		  var secs = s % 60;
		  s = (s - secs) / 60;
		  var mins = s % 60;
		  var hrs = (s - mins) / 60;

		  return pad(hrs) + ':' + pad(mins) + ':' + pad(secs);
		}
	
	TimeUtilization.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
	function convertDateFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMMM D, YYYY');
		} else {
			return "";
		}
	}
	
	function convertDateFromServerToTime(date) {
		if (date) {
			return moment(date).format('h:mm A');
		} else {
			return "";
		}
	}

	TimeUtilization.closeModalPopup = function(el) {
		el.modal('hide');
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