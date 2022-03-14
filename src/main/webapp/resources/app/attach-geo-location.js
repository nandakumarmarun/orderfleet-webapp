if (!this.AttachGeoLocation) {
	this.AttachGeoLocation = {};
}

(function() {
	'use strict';

	var attachGeoLocationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var accountPid = null;
	var newLatitude = null;
	var newLongitude = null;
	var newLocation = null;

	$(document).ready(function() {
		var rNameParam = getParameterByName('rname');
		if(rNameParam) {
			if(rNameParam.toUpperCase() == 'VISIT'){
				$("#dbReportFrom").val('VISIT');
			} else {
				$("#dbReportFrom").val('MOBILE');
			}
		}
		
		// load today data
		AttachGeoLocation.filter();

		$("#myFormSubmit").click(function() {
			attachAccountProfile();
		});

		$("#dbReportFrom").change(function() {
			AttachGeoLocation.filter();
		});
		
		$("#btnSaveNewGeoLocation").click(function() {
			saveNewGeoLocation();
		});
		

		$('#btnDownload')
				.on(
						'click',
						function() {
							var tblAttachGeoLocation = $("#tblAttachGeoLocation tbody");
							console.log(tblAttachGeoLocation.children().length);
							console.log(tblAttachGeoLocation[0].textContent.trim());
							if (tblAttachGeoLocation.children().length == 0) {
								alert("no values available");
								return;
							}
							if (tblAttachGeoLocation[0].textContent.trim() == "No data available") {
								alert("no values available");
								return;
							}
							downloadXls();
							$("#tblAttachGeoLocation th:last-child, #tblAttachGeoLocation td:last-child").show();
						});
	});

	
	
	function downloadXls() {
		var activity = "no";
		 var employeePid = $("#dbEmployee").val();
		  var activityPid = activity;
		  var accountPid = $("#dbAccount").val();
		   var filterBy = $("#dbDateSearch").val();
		  var fromDate = $("#txtFromDate").val();
		  var toDate = $("#txtToDate").val();
		  var rName = $("#dbReportFrom").val();
		 var status = $('#slt_status').val();
		console.log(status);
		window.location.href = attachGeoLocationContextPath
				+ "/download-attach-geo-Location-xls?employeePid=" + employeePid + '&activityPid='+activityPid + '&accountPid='+accountPid + '&filterBy='+filterBy + '&fromDate='+fromDate + '&rName='+rName +'&toDate='+toDate;	
	}
	
	
	function attachAccountProfile() {
		$.ajax({
			url : attachGeoLocationContextPath + "/attachAccountProfile",
			type : 'POST',
			data : {
				accountProfilePid : accountPid,
				latitude : newLatitude,
				longitude : newLongitude,
				location : newLocation
			},
			success : function(result) {
				AttachGeoLocation
						.closeModalPopup($('#viewModalAccountProfile'))
				AttachGeoLocation.filter();
				alert("Successfully Attached");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = attachGeoLocationContextPath;
	}

	AttachGeoLocation.filter = function() {
	
		
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		var activity = "no";
		$('#tBodyAttachGeoLocation').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : attachGeoLocationContextPath + "/filter",
					type : 'GET',
					data : {
// userPid : $("#dbUser").val(),
						employeePid : $("#dbEmployee").val(),
						activityPid : activity,
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						rName : $("#dbReportFrom").val()
					},
					success : function(attachGeoLocations) {
						$('#tBodyAttachGeoLocation').html("");
						if (attachGeoLocations == null) {
							$('#tBodyAttachGeoLocation')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}
						if (attachGeoLocations.rName == 'MOBILE') {
							generateMobileGeoData(attachGeoLocations.mobileGeoLocationViews);
						} else if (attachGeoLocations.rName == 'VISIT') {
							generateVisitGeoData(attachGeoLocations.visitGeoLocationViews);
						}

						$('.collaptable')
								.aCollapTable(
										{
											startCollapsed : true,
											addColumn : false,
											plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
											minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
										});
					}
				});
	}

	function generateMobileGeoData(mobileGeoDatas) {
		
		if (mobileGeoDatas.length == 0) {
			$('#tBodyAttachGeoLocation')
					.html(
							"<tr><td colspan='7' align='center'>No data available</td></tr>");
			return;
		}
		$
		.each(
				mobileGeoDatas,
				function(index, mobileGeoData) {
					var locationIcon = "";
					if (mobileGeoData.locationType == "GpsLocation") {
						locationIcon = '<img src="/resources/assets/images/map/location.png" width="20px">';
					} else if (mobileGeoData.locationType == "TowerLocation") {
						locationIcon = '<img src="/resources/assets/images/map/tower.png" width="20px">';
					} else if (mobileGeoData.locationType == "FlightMode") {
						locationIcon = '<img src="/resources/assets/images/map/flight.png" width="20px">';
					}
					// check gsp is Off
					if (mobileGeoData.isGpsOff) {
						locationIcon += ' <img src="/resources/assets/images/map/gps-off.png" width="20px">';
					}
					// check Mobile Data is Off
					if (mobileGeoData.isMobileDataOff) {
						locationIcon += ' <img src="/resources/assets/images/map/mobile-data-off.png" width="20px">';
					}
					$('#tBodyAttachGeoLocation')
							.append(
									"<tr><td>"
											+ convertDateTimeFromServer(mobileGeoData.sendDate)
											+ "</td><td>"
											+ mobileGeoData.userName
											+ "</td><td>"
											+ mobileGeoData.accountProfileName
											+ "</td><td>"
											+ (mobileGeoData.latitude == null ? ""
													: mobileGeoData.latitude)
											+ "</td><td>"
											+ (mobileGeoData.longitude == null ? ""
													: mobileGeoData.longitude)
											+ "</td><td>"
											+ (mobileGeoData.location == null ? ""
													: mobileGeoData.location)
											+ locationIcon
											+ "</td><td><button type='button' class='btn btn-success' onclick='AttachGeoLocation.attachtoAccountProfile(\""
											+ mobileGeoData.accountPid
											+ "\",\""
											+ mobileGeoData.latitude
											+ "\",\""
											+ mobileGeoData.longitude
											+ "\",\""
											+ mobileGeoData.location
											+ "\");'>Attach to AccountProfile</button></td></tr>");
				});

	}

	function generateVisitGeoData(visitGeoData) {
		if (visitGeoData.length == 0) {
			$('#tBodyAttachGeoLocation')
					.html(
							"<tr><td colspan='7' align='center'>No data available</td></tr>");
			return;
		}
		$
				.each(
						visitGeoData,
						function(index, attachGeoLocation) {
							var locationIcon = "";
							if (attachGeoLocation.locationType == "GpsLocation") {
								locationIcon = '<img src="/resources/assets/images/map/location.png" width="20px">';
							} else if (attachGeoLocation.locationType == "TowerLocation") {
								locationIcon = '<img src="/resources/assets/images/map/tower.png" width="20px">';
							} else if (attachGeoLocation.locationType == "FlightMode") {
								locationIcon = '<img src="/resources/assets/images/map/flight.png" width="20px">';
							}
							// check gsp is Off
							if (attachGeoLocation.isGpsOff) {
								locationIcon += ' <img src="/resources/assets/images/map/gps-off.png" width="20px">';
							}
							// check Mobile Data is Off
							if (attachGeoLocation.isMobileDataOff) {
								locationIcon += ' <img src="/resources/assets/images/map/mobile-data-off.png" width="20px">';
							}
							$('#tBodyAttachGeoLocation')
									.append(
											"<tr><td>"
													+ convertDateTimeFromServer(attachGeoLocation.plannedDate)
													+ "</td><td>"
													+ attachGeoLocation.employeeName
													+ "</td><td>"
													+ attachGeoLocation.accountProfileName
													+ "</td><td>"
													+ (attachGeoLocation.latitude == null ? ""
															: attachGeoLocation.latitude)
													+ "</td><td>"
													+ (attachGeoLocation.longitude == null ? ""
															: attachGeoLocation.longitude)
													+ "</td><td>"
													+ (attachGeoLocation.location == null ? ""
															: attachGeoLocation.location)
													+ locationIcon
													+ "</td><td><button type='button' class='btn btn-success' onclick='AttachGeoLocation.attachtoAccountProfile(\""
													+ attachGeoLocation.accountProfilePid
													+ "\",\""
													+ attachGeoLocation.latitude
													+ "\",\""
													+ attachGeoLocation.longitude
													+ "\",\""
													+ attachGeoLocation.location
													+ "\");'>Attach to AccountProfile</button></td></tr>");
						});
	}
	
	AttachGeoLocation.attachtoAccountProfile = function(pid, latitude,
			longitude, location) {
		newLatitude = latitude;
		newLongitude = longitude;
		newLocation = location;
		accountPid = pid;
		var sbRName = $("#dbReportFrom").val();
		if (sbRName == 'MOBILE') {
			loadMobileGeoLocation(newLatitude,newLongitude, newLocation, accountPid);
		} else if (sbRName == 'VISIT') {
			loadVisitGeoLocation(newLatitude,newLongitude, newLocation, accountPid);
		}
	}
	
	function loadMobileGeoLocation(lat,long, loc, accountPid) {
		$("#tblAccountProfileGeoLocation").html("");
		var row = "";
		$.ajax({
			url : attachGeoLocationContextPath + "/getAccountProfileGeoLocation",
			type : 'GET',
			data : {
				accountProfilePid : accountPid,
			},
			success : function(result) {
				$("#oldLatitude").text((lat == "null" || lat == null ? "": lat));
				$("#oldLongitude").text((long == "null" || long == null ? "": long));
				$("#oldGeoLocation").text((loc == "null" || loc == null? "": loc));
				$.each(result,function(index, geoLocation){
					row += "<tr><td><input type='radio' name='geoLocation' id='geoLocation' value='"+geoLocation.pid+"' ></td>" 
					+"<td>"+geoLocation.latitude+"</td><td>"+geoLocation.longitude+"</td><td>"+geoLocation.location+"</td></tr>";
				});
				$("#tblAccountProfileGeoLocation").html(row);
				$('#viewModalMobile').modal('show');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function loadVisitGeoLocation(newLatitude,newLongitude, newLocation, accountPid) {
		var row1 = "";
		var row2 = "";
		var row3 = "";
		$.ajax({
			url : attachGeoLocationContextPath + '/getOldAccountProfile',
			type : 'GET',
			data : {
				accountProfilePid : accountPid
			},
			success : function(result) {
				$("#tbldivAttachAccountProfile").html("");
				row1 = "<tr><td><label>Latitude</label</td><td>"
						+ (result.latitude == null ? "" : result.latitude)
						+ "</td><td>"
						+ (newLatitude == null ? "" : newLatitude)
						+ "</td></tr>";
				row2 = "<tr><td><label>Longitude</label</td><td>"
						+ (result.longitude == null ? "" : result.longitude)
						+ "</td><td>"
						+ (newLongitude == null ? "" : newLongitude)
						+ "</td></tr>";
				row3 = "<tr><td><label>Location</label</td><td>"
						+ (result.location == null ? "" : result.location)
						+ "</td><td>"
						+ (newLocation == null ? "" : newLocation)
						+ "</td></tr>";
				$("#tbldivAttachAccountProfile").append(row1);
				$("#tbldivAttachAccountProfile").append(row2);
				$("#tbldivAttachAccountProfile").append(row3);
				$('#viewModalAccountProfile').modal('show');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function saveNewGeoLocation(){
		var geoLocationPid = $("input[name='geoLocation']:checked").val();
		if(geoLocationPid==null){
			alert("Please Select Geo Location");
			return;
		}
		$.ajax({
			url:attachGeoLocationContextPath + "/saveGeoLocation",
			type:'POST',
			data:{
				geoLocationPid: geoLocationPid,
				accountProfilePid: accountPid,
			},
			success: function(result){
				$("#viewModal").hide();
				onSaveSuccess(result);
			},
			error:function(xhr, error){
				onError(xhr, error);
			},
		});
	}
	
	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}

	AttachGeoLocation.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}

	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}

	AttachGeoLocation.closeModalPopup = function(el) {
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