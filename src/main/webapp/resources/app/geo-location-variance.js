if (!this.GeoLocationVariance) {
	this.GeoLocationVariance = {};
}

(function() {
	'use strict';

	var geoLocationVarianceContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
// GeoLocationVariance.filter();
		
		$('#btnDownload').on('click', function() {
			var tbltblGeoLocationVariance = $("#tblGeoLocationVariance tbody");
			if (tbltblGeoLocationVariance.children().length == 0) {
				alert("no values available");
				return;
			}
			var name = "geo-location variance report";
			downloadXls(name);
		});
	});
	
		function downloadXls(excelName) {
		 var table2excel = new Table2Excel();
	     table2excel.export(document.getElementById('tblGeoLocationVariance'),excelName);
	}
		
		$('#dbUser').on('change', function() {
			$('#dbAccountProfile').html("Loading...");
			
			$
			.ajax({
				url : geoLocationVarianceContextPath + "/get-account-profile",
				type : 'GET',
				data : {
					userPid : $("#dbUser").val(),
				},
				success : function(data) {
					$('#dbAccountProfile').html("");
					if (data.length == 0) {
						$('#dbAccountProfile').append($('<option>', { 
					        val: "no",
					        text : "No Account Profiles"
					    }));
						return;
					}
					$('#dbAccountProfile').append($('<option>', { 
				        val: "no",
				        text : "All Account Profile"
				    }));
					$.each(data, function (index,accountProfile ) {
					    $('#dbAccountProfile').append($('<option>', { 
					        val: accountProfile.pid,
					        text : accountProfile.name 
					    }));
					});
				},
				
		});
		});
	
	GeoLocationVariance.showDatePicker = function() {
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

	GeoLocationVariance.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyGeoLocationVariance').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : geoLocationVarianceContextPath + "/filter",
					type : 'GET',
					data : {
						userPid : $("#dbUser").val(),
						accountProfilePid : $("#dbAccountProfile").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(data) {
						$('#tBodyGeoLocationVariance').html("");
						if (data.length == 0) {
							$('#tBodyGeoLocationVariance')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										data,
										function(index, geoLocVariance) {
											var variance="";
											
											var executionPid="'" + geoLocVariance.executionPid + "'";
											
											if(geoLocVariance.variation=="Button"){
												
												variance='<button class="btn btn-success" onclick="GeoLocationVariance.calculateVariance('+executionPid+')">Calculate Variance</button>';
																									
											}else{
													variance=geoLocVariance.variation;
											}
											$('#tBodyGeoLocationVariance')
													.append(
										
															"<tr><td>"
																	+ geoLocVariance.accountProfileName
																	+ "</td><td>"
																	+ geoLocVariance.activityName
																	+ "</td><td>"
																	+ geoLocVariance.date
																	+ "</td><td>"
																	+ geoLocVariance.actualLocation
																	+ "</td><td>"
																	+ geoLocVariance.reportedLocation
																	+ "</td><td>"
																	+ variance
																	+ "</td></tr>");
										});

					}
				});
	}
	
	GeoLocationVariance.calculateVariance = function(executionPid) {
		
		console.log(executionPid);
		
		$
				.ajax({
					url : geoLocationVarianceContextPath + "/calculateVariance",
					type : 'GET',
					data : {
						executionPid : executionPid
					},
					success : function(status) {
						
						if(status){
							GeoLocationVariance.filter();
						}
						
					}
				});
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = geoLocationVarianceContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = geoLocationVarianceContextPath;
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