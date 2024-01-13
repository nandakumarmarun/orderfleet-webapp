 //connect stompClient web socket
var stompClient = null;
function connect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	var socket = new SockJS(contextPath + '/websocket/tracker');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		stompClient.subscribe('/live-routing/map-view/' + companyId, function(
				response) {
			var taskExecution = JSON.parse(response.body);
			updateUserRouteDetails(taskExecution);
			console.log("success");
		});
	});
}

// show all routes
var userDataList = [];
var selectedUserPid = "no";

function loadUsersDetails(dashboardUserPid) {
	console.log("selectedUserPid.......:- " + selectedUserPid);
	map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
	directionsService = new google.maps.DirectionsService();
	$("body").isLoading({
		text : "Loading Please Wait....",
		position : "overlay"
	});

	$
			.ajax({
				url : contextPath + "/web/uploadUserLocations",
				type : 'GET',
				data : {
					date : $("#txtDate").val()
				},
				success : function(response) {
					userDataList = response;
					var ulLi = "";
					if (userDataList.length != 0) {
						$
								.each(
										userDataList,
										function(index, userData) {
											ulLi += '<div class="panel panel-default"><div class="panel-heading">'
													+ '<h4 class="panel-title"><button id="btn'
													+ userData.userPid
													+ '" onclick="onSelectUser(\''
													+ userData.userPid
													+ '\',this);" class="btn btn-green btn-block u-name">'
													+ userData.employeeName
													+ '</button>'
													+ '<a onclick="showUserRouteHistory(\''
													+ userData.userPid
													+ '\')" data-toggle="collapse" data-parent="#divUsers" href="#collapseTwo-'
													+ userData.userPid
													+ '" class="collapsed"></a>'
													+ '</h4></div>	<div id="collapseTwo-'
													+ userData.userPid
													+ '" class="panel-collapse collapse">	'
													+ '<div class="panel-body route-details-li"><ul id="ul'
													+ userData.userPid
													+ '"></ul></div></div></div>';
											markPoints(userData.trackingPoints,
													userData.employeeName);
										});
					} else {
						console.log("no data.......");
						ulLi += "<div style='color: red;text-align: center;'>No data available</div>";
					}
					$("#divUsers").html(ulLi);

					if (dashboardUserPid != null && dashboardUserPid != "") {
						onSelectUser(dashboardUserPid, null);
						$("#btn" + dashboardUserPid).addClass("active");
					}

					setTimeout(function() {
						$("body").isLoading("hide");
					}, 2000);
				},
				beforeSend : function(xhr) {
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				error : function(jqXhr, textStatus, errorThrown) {
					console.log("Error in load routes first time.......");
				}
			});
}

function updateUserRouteDetails(taskExecution) {
	console.log("updateUserRouteDetails");
	$.each(userDataList, function(index, userData) {
		if (userData.userPid == taskExecution.userPid) {
			userData.trackingPoints.push(taskExecution);
			return;
		}
	});
	// mark point
	if (selectedUserPid == "no" || selectedUserPid == taskExecution.userPid) {
		createMarker(taskExecution, 0, taskExecution.employeeName);
	}
}

function onSelectUser(userPid, obj) {
	// add active class
	$('.btn-green').removeClass("active");
	if (obj != null)
		$(obj).addClass("active");

	selectedUserPid = userPid;

	$.each(userDataList, function(index, userData) {
		if (userData.userPid == userPid) {
			var trackingPoints = userData.trackingPoints.slice();
//			createUserMapOptionsAndRefreshMap(trackingPoints);
			markPoints(userData.trackingPoints, userData.employeeName);
			drawRoute(trackingPoints);
			return;
		}
	});
}

function createUserMapOptionsAndRefreshMap(trackingPoints) {
	var userMapOptions = mapOptions;
	// find user starting point
	$(trackingPoints.reverse())
			.each(
					function(index, trackingPoint) {
						console.log(trackingPoint.locationType);
						if ((trackingPoint.locationType == "GpsLocation" || trackingPoint.locationType == "TowerLocation")
								&& trackingPoint.location != "Exception") {
							var point = new google.maps.LatLng(
									trackingPoint.latitude,
									trackingPoint.longitude);
							var options = {
								zoom : 14,
								center : point
							};
							userMapOptions = options;
							return false;
						}
					});
	map = new google.maps.Map(document.getElementById('map-canvas'),
			userMapOptions);
}
var taskUserPid = "";
function showUserRouteHistory(userPid) {
	$('#ul' + userPid).html("<li>Please wait...</li>");
	$
			.each(
					userDataList,
					function(index, userData) {
						if (userData.userPid == userPid) {
							taskUserPid = userPid;
							var trackingPoints = userData.trackingPoints;
							if (trackingPoints.length > 0) {
								var list = "";
								var number=0;
								$
										.each(
												trackingPoints,
												function(index, trackingPoint) {
													number = number + 1;
													var location;
													if (trackingPoint.locationType == "TowerLocation") {
														location = trackingPoint.towerLocation;
													} else {
														location = trackingPoint.location;
													}

													if (location == "No Location"
															&& trackingPoint.latitude != 0) {
														location = "<span class='btn btn-success'  id='"
																+ trackingPoint.pid
																+ "' onClick='getLocation(this)' >get location</span>";

													}

													var li = "<li style='cursor: pointer;' onclick='zoomToLocation("
															+ trackingPoint.latitude
															+ ", "
															+ trackingPoint.longitude
															+ ")'><i class='fa fa-caret-left'></i><span>"
															+ trackingPoint.accountProfileName+" :"+number
															+ "</span><br><span style='color: #542C2C;'>"
															+ location
															+ "</span><br><span>"
															+ convertTime(trackingPoint.date)
															+ "</span></li>";
													list += li;
												});
								$('#ul' + userPid).html(list);
							} else {
								$('#ul' + userPid).html(
										"<li>Route history not found</li>");
							}
							return;
						}
					});
}

function getLocation(obj) {
	var pid = $(obj).attr("id");
	$(obj).html("loading...");
	$.ajax({
		url : contextPath + "/web/updateLocation/" + pid,
		method : 'GET',
		success : function(data) {
			$(obj).html(data.location);
			$(obj).removeClass("btn-success");
			$(obj).removeClass("btn");

			window.location = contextPath + "/web/live-tracking?user-key-pid="
					+ taskUserPid;
		},
		error : function(xhr, error) {
			onError(xhr, error);
		}
	});
}

//function zoomToLocation(lat, lng) {
//	console.log("latitude : " + lat);
//	console.log("longitude : " + lng);
//	if (lat === 0 && lng === 0) {
//		return;
//	}
//	var point = new google.maps.LatLng(lat, lng);
//	map.setZoom(18);
//	map.setCenter(point);
//}

var endPointNumber = 0;
function markPoints(trackingPoints, employeeName) {
	endPointNumber = trackingPoints.length;
	console.log("Size of points :"+endPointNumber)
	var number = 0;
	$.each(trackingPoints, function(index, trackingPoint) {
		number = number + 1;
		createMarker(trackingPoint, number, employeeName);
	});
}

function createMarker(trackingPoint, number, employeeName) {
	if (trackingPoint.locationType == "GpsLocation"
			|| trackingPoint.locationType == "TowerLocation") {

		var contentString = createContentString(trackingPoint, number,
				employeeName);
		var lat = 0;
		var lng = 0;
		if (trackingPoint.locationType == "TowerLocation") {
			if (trackingPoint.towerLatitude != null) {
				lat = trackingPoint.towerLatitude;
				lng = trackingPoint.towerLongitude;
			} else {
				lat = trackingPoint.latitude;
				lng = trackingPoint.longitude;
			}
		} else {
			lat = trackingPoint.latitude;
			lng = trackingPoint.longitude;
		}
		var time = convertTime(trackingPoint.date);

		var exLatLng = new google.maps.LatLng("" + lat, "" + lng);
		var marker = new MarkerWithLabel({
			position : exLatLng,
			map : map,
			labelContent : '' + time,
			title : '' + time,
			labelAnchor : new google.maps.Point(31, 10),
			zIndex : number,
			labelClass : "labels",
			labelStyle : {
				opacity : 0
				
				
			}
		});
 
		if (trackingPoint.locationType == "TowerLocation") {
			marker.setIcon(contextPath
					+ '/resources/assets/images/map/tower.png');
		} else {
			if (trackingPoint.accountProfileName == "Attendance") {
				marker
						.setIcon(contextPath
								+ '/resources/assets/images/map/location.png');
			} else if(trackingPoint.accountProfileName == "CurrentLocation") {
				marker
						.setIcon(contextPath
								+ '/resources/assets/images/map/currentLocation.png');
			}
			else{
				marker
				.setIcon(contextPath
						+ '/resources/assets/images/map/location.png');
			}
		}


		var infowindow = new google.maps.InfoWindow({
			content : contentString
		});
		marker.addListener('mouseover', function() {
			infowindow.open(map, marker);
		});
		marker.addListener('mouseout', function() {
			infowindow.close();
		});
		marker.addListener('click', function() {
			map.setZoom(30);
			map.setCenter(marker.getPosition());
		});
	}
}

function createContentString(trackingPoint, number, employeeName) {
	var locationIcon = locationIcons(trackingPoint);
	/*
	 * if (trackingPoint.locationType == "GpsLocation") { locationIcon = '<img
	 * src="' + contextPath + '/resources/assets/images/map/location.png"
	 * width="20px" />' } else if (trackingPoint.locationType ==
	 * "TowerLocation") { locationIcon = '<img src="' + contextPath +
	 * '/resources/assets/images/map/tower.png" width="20px" />' }
	 */
	var location;
	if (trackingPoint.locationType == "TowerLocation") {
		location = trackingPoint.towerLocation;
	} else {
		location = trackingPoint.location;
	}
	return '<div id="content"><table class="table table-striped table-bed ">'
			+ '<tr><th colspan="2">Activity Details</th></tr>'
			+ '<tr><td>Employee</td><td>' + employeeName
			+ '</td></tr><tr><td>Account</td><td>'
			+ trackingPoint.accountProfileName + '<tr><td>Location</td><td>'
			+ location + " " + locationIcon + '</td></tr><tr><td>Date</td><td>'
			+ convertDateTime(trackingPoint.date)
			+ '</td></tr><tr><td>Battery Percentage</td><td>'
			+ trackingPoint.batteryPercentage
			+ '</td></tr><tr><td>Number</td><td>' + number
			+ '</td></tr></table></div>';

}
function locationIcons(userData) {

	var images = "";
	if (userData.locationType != null) {

		if (userData.locationType == "GpsLocation") {
			images = '<img src="'
					+ contextPath
					+ '/resources/assets/images/map/location.png" width="20px">';
		} else if (userData.locationType == "TowerLocation") {
			images = '<img src="' + contextPath
					+ '/resources/assets/images/map/tower.png" width="20px">';
		} else if (userData.locationType == "FlightMode") {
			images = '<img src="' + contextPath
					+ '/resources/assets/images/map/flight.png" width="20px">';
		}
		// check gsp is Off
		if (userData.isGpsOff && userData.locationType != "GpsLocation") {
			images += ' <img src="' + contextPath
					+ '/resources/assets/images/map/gps-off.png" width="20px">';
		}
		// check Mobile Data is Off
		if (userData.isMobileDataOff) {
			images += ' <img src="'
					+ contextPath
					+ '/resources/assets/images/map/mobile-data-off.png" width="20px">';
		}

	}
	return images;
}
function convertDateTime(createdDate) {
	if (createdDate) {
		return moment(createdDate).format('MMM DD YYYY, h:mm:ss');
	} else {
		return "";
	}
}

function convertTime(createdDate) {
	if (createdDate) {
		return moment(createdDate).format('h:mm:ss');
	} else {
		return "";
	}
}

function drawRoute(points) {

	var array = removeDuplicatesFromPoints(points);
	console.log("totalPoints............" + array.length +" Points :"+points)
	if (array.length > 0) {
		var totalPoints = array.length;
		var lastIndex = totalPoints - 1;

		// deviated Points array split by ten
		if (totalPoints > 10) {
			var tenPoints = new Array();
			var count = 0;
			for (var i = 0; i < totalPoints; i++) {
				count++;
				if (count < 10) {
					tenPoints.push(array[i]);
				}
				if (count == 10 || lastIndex == i) {
					console.log("tenPoints............" + tenPoints.length)
					executeDirectionService(tenPoints);
					count = 0;
					tenPoints = new Array();
					tenPoints.push(array[i]);
				}
			}
		} else {
			executeDirectionService(array);
		}
	}
}

function executeDirectionService(points) {

	var directionsDisplay = new google.maps.DirectionsRenderer(
			dvRendererOptions);
	directionsDisplay.setMap(map);

	// create start and end LatLng object
	var startLatLng = new google.maps.LatLng(points[0].latitude,
			points[0].longitude);
	var endLatLng = new google.maps.LatLng(points[points.length - 1].latitude,
			points[points.length - 1].longitude);

	// remove first and last array from list
	points.shift();
	points.pop();

	// create wayponts array using deviated points
	var pointsArray = new Array()
	for (var i = 0; i < points.length; i++) {
		pointsArray.push({
			location : new google.maps.LatLng(points[i].latitude,
					points[i].longitude),
			stopover : true

		});
	}
	console.log("size:"+pointsArray.length);
	var request = {
		origin : startLatLng,
		destination : endLatLng,
		waypoints : pointsArray,
		// optimizeWaypoints : true,
		travelMode : google.maps.TravelMode.DRIVING
	};
	directionsService.route(request, function(response, status) {
		if (status == google.maps.DirectionsStatus.OK) {
			directionsDisplay.setDirections(response);
		}

	});
}

var dvRendererOptions = {
	suppressMarkers : true,
	polylineOptions : {
		suppressPolylines : true,
		geodesic : true,
		strokeColor : "#2c7ea1",
	
		strokeWeight : 4,
		strokeOpacity : 1
	}
};

function removeDuplicatesFromPoints(points) {
	var arr = {};
	for (var i = 0; i < points.length; i++) {
		if (points[i]['longitude'] == 0) {
			points[i]['latitude'] = points[i]['towerLatitude'];
			points[i]['longitude'] = points[i]['towerLongitude'];
			arr[points[i]['towerLongitude']] = points[i];
		} else {
			arr[points[i]['longitude']] = points[i];
		}
	}
	points = new Array();
	for ( var key in arr) {
		points.push(arr[key]);
	}
	return points;
}
