//connect stompClient web socket
var stompClient = null;
function connect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	var socket = new SockJS(contextPath + '/websocket/tracker');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		stompClient.subscribe('/heat-map/map-view/' + companyId, function(
				response) {
			console.log("success");
		});
	});
}

// show all routes
var accountProfiles = [];

function loadHeatMapDetails() {

	map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
	directionsService = new google.maps.DirectionsService();
	$("body").isLoading({
		text : "Loading Please Wait....",
		position : "overlay"
	});

	$.ajax({
		url : contextPath + "/web/getCustomerDetails",
		type : 'GET',
		success : function(response) {
			accountProfiles= response;
			var ulLi = "";
			if (accountProfiles.length != 0) {
				markPoints(accountProfiles);
			} else {
				console.log("no data.......");
				
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

var endPointNumber = 0;
function markPoints(accountProfiles) {
	endPointNumber = accountProfiles.length;
	var number = 0;
	$.each(accountProfiles, function(index, accountProfile) {
		number = number + 1;
		createMarker(accountProfile, number, accountProfile.name);
	});
}

function createMarker(accountProfile, number, accountName) {
	
	var contentString = createContentString(accountProfile, number, accountName);

	lat = accountProfile.latitude;
	lng = accountProfile.longitude;

	var exLatLng = new google.maps.LatLng("" + lat, "" + lng);
	var marker = new MarkerWithLabel({
		position : exLatLng,
		map : map,
		labelAnchor : new google.maps.Point(31, 10),
		zIndex : number,
		labelStyle : {
			opacity : 0.85
		}
	});

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

function createContentString(accountProfile, number, accountName) {
	var location = accountProfile.location;

	return '<div id="content"><table class="table table-striped table-bed ">'
			+ '<tr><th colspan="2">Geo Tagging Details</th></tr>'
			+ '<tr><td>Account</td><td>' + accountName
			+ '</td></tr><tr><td>Location</td><td>' + location
			+ '</td></tr><tr><td>Number</td><td>' + number
			+ '</td></tr></table></div>';

}

