if (!this.AddGeoLocation) {
	this.AddGeoLocation = {};
}

(function() {
	'use strict';

	var addGeoLocationContextPath = location.protocol + '//' + location.host
	+ location.pathname;

	var markers = [];
	var map;
	var geocoder;
	var kerala = new google.maps.LatLng(10.7700, 76.6500);
	var mapOptions = {
		zoom : 8,
		center : kerala
	};

	$(document).ready(
		function() {
			
			// table search
			$('#btnSearch').click(function() {
				searchTable($("#search").val());
			});

			$('#btnSaveLocationName').click(function() {
				saveLocationToAccountProfile();
			});
			var importstatusparameter = "";
			var accounttypeparameter = "";
			if (location.search == "") {
				AddGeoLocation.loadAccountProfiles(accounttypeparameter,
					importstatusparameter);
			} else {
				var importstatusparameter = location.search.split('=');
				var accounttypeparameter = importstatusparameter[1]
					.split('&')[0];
				AddGeoLocation.loadAccountProfiles(accounttypeparameter,
					importstatusparameter[2]);
			}
			$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
						'tbody tr td input[type="checkbox"]').prop(
						'checked', $(this).prop('checked'));
				});

			$("#fullscreen").click(function() {
				fullscreen();
			});
			$("#btnSaveNewGeoLocation").click(function() {
				saveNewGeoLocation();
			});
			
			var pacContainerInitialized = false; 
            $('#pac-input').keypress(function() { 
                    if (!pacContainerInitialized) { 
                            $('.pac-container').css('z-index','1042'); 
                            pacContainerInitialized = true; 
                    } 
            }); 
		});

	var accountProfilePid = "";

	AddGeoLocation.oldLatLngValues = function(el, id, obj) {
		map = new google.maps.Map(document.getElementById('map-canvas'),mapOptions);
		// used to find location name
		geocoder = new google.maps.Geocoder();
		getLatLngAndLocation();
		
		$("#pac-input").val("");
		$("#oldLocation").text("");
		$("#newLocation").text("");		
		
		 // Create the search box and link it to the UI element.
        var input = document.getElementById('pac-input');
        var searchBox = new google.maps.places.SearchBox(input);
       /* map.controls[google.maps.ControlPosition.TOP_LEFT].push(input); */

        // Bias the SearchBox results towards current map's viewport.
        map.addListener('bounds_changed', function() {
          searchBox.setBounds(map.getBounds());
        });
       
        // Listen for the event fired when the user selects a prediction and
		// retrieve
        // more details for that place.
        searchBox.addListener('places_changed', function() {
          var places = searchBox.getPlaces();
          if (places.length == 0) {
            return;
          }

          // For each place, get the icon, name and location.
          var bounds = new google.maps.LatLngBounds();
          places.forEach(function(place) {
            if (!place.geometry) {
              console.log("Returned place contains no geometry");
              return;
            }
            if (place.geometry.viewport) {
              // Only geocodes have viewport.
              bounds.union(place.geometry.viewport);
            } else {
              bounds.extend(place.geometry.location);
            }
          });
          map.fitBounds(bounds);
        });

		var location = $(obj).closest('tr').find('td:eq(3)').text();
		var lat = $(obj).closest('tr').find('td:eq(1)').text();
		var lng = $(obj).closest('tr').find('td:eq(2)').text();

		accountProfilePid = id;

		if (lat != null) {
			var marker = new google.maps.Marker({
				position : new google.maps.LatLng(lat, lng),
				map : map
			});
			marker.setIcon('http://chart.apis.google.com/chart?chst=d_map_spin&chld=.7|0|065404|12|_|');

		}
if(lat!=""){
		$("#oldLocation").text(location + "( " + lat + "," + lng + " )");
}
		el.modal('show');
	}

	function getLatLngAndLocation() {
		// used to first load show map
		google.maps.event.addListenerOnce(map, 'idle', function() {
			var currCenter = map.getCenter();
			google.maps.event.trigger(map, 'resize');
			map.setCenter(currCenter);
		});
		google.maps.event.addListener(map, 'click', function(event) {
			removeOldMarkers();
			var marker = new google.maps.Marker({
				position : event.latLng,
				map : map
			});
			markers.push(marker);
			getLocationNameByLatLng(event.latLng.lat(), event.latLng.lng());

			$("#newLat").text(event.latLng.lat());
			$("#newLng").text(event.latLng.lng());
			$("#newLocation").text(
				"Please wait..." + "( " + event.latLng.lat() + ","
				+ event.latLng.lng() + " )");
		});
		
		
	}

	// removeing old dropped markers
	function removeOldMarkers() {
		for (var i = 0; i < markers.length; i++) {
			markers[i].setMap(null);
		}
	}
	// get the name of the location on click event
	function getLocationNameByLatLng(lat, lng) {
		var latlng = new google.maps.LatLng(lat, lng);
		geocoder.geocode({
			latLng : latlng
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var locationName = results[0].formatted_address;
				$("#newLocation").text(
					locationName + "( " + lat + "," + lng + " )");
			}
		});
	}

	function saveLocationToAccountProfile() {
		var apLocSaveUrl = location.protocol + '//' + location.host
			+ '/web/attach-geo-location/attachAccountProfile';

		var latitude = $("#newLat").text();
		var longitude = $("#newLng").text();
		var locationName = $("#newLocation").text().split("(")[0];

		if ((latitude == "") || (locationName == "")) {
			alert("select a location");
			return false;
		}
		if (locationName == "Please wait...") {
			alert("location name loading...");
			return false;
		}


		$.ajax({
			method : "POST",
			url : apLocSaveUrl,
			data : {
				accountProfilePid : accountProfilePid,
				latitude : latitude,
				longitude : longitude,
				location : locationName,
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	// Value of Imported is "Imported" get imported status is true
	AddGeoLocation.loadAccountProfiles = function(accountType, importStatus) {
		var accounttypepid = "";
		var imports = "";
		if (accountType != "" && importStatus != "") {
			if (accountType != "No" && importStatus != "No") {
				accounttypepid = accountType;
				if (importStatus == "Imported") {
					imports = "true";
				} else {
					imports = "false";
				}
			}
			if (accountType == "No" && importStatus != "No") {
				if (importStatus == "Imported") {
					imports = "true";
				} else {
					imports = "false";
				}
			}
			if (accountType != "No" && importStatus == "No") {
				accounttypepid = accountType;
			}
		}
		$('#tBodyAddGeoLocation').html(
			"<tr><td colspan='5' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : addGeoLocationContextPath + "/filterByAccountType",
				method : 'GET',
				data : {
					accountTypePids : accounttypepid,
					importedStatus : imports
				},
				success : function(accountProfiles) {
					$('#tBodyAddGeoLocation').html("");
					if (accountProfiles.length == 0) {
						$('#tBodyAddGeoLocation')
							.html(
								"<tr><td colspan='5' align='center'>No data available</td></tr>");
						return;
					}
					$
						.each(
							accountProfiles,
							function(index, accountProfile) {
								$('#tBodyAddGeoLocation')
									.append(
										"<tr><td>"
										+ accountProfile.name
										+ "</td><td>"
										+ (accountProfile.latitude == null ? ""
											: accountProfile.latitude)
										+ "</td><td>"
										+ (accountProfile.longitude == null ? ""
											: accountProfile.longitude)
										+ "</td><td>"
										+ (accountProfile.location == null ? ""
											: accountProfile.location)
										+ "</td><td><button type='button' class='btn btn-blue' onclick='AddGeoLocation.showModalPopup($(\"#viewModal\"),\""
										+ accountProfile.pid + "\",\"" + accountProfile.latitude + "\",\"" + accountProfile.longitude + "\",\""
										+ accountProfile.location
										+ "\",0);'>Location From Geo Tag Mobile</button><button type='button'data-target='#enableMapModal' class='btn btn-success' onclick='AddGeoLocation.oldLatLngValues($(\"#enableMapModal\"),\""
										+ accountProfile.pid
										+ "\",this);'>Location From Map</button></td></tr>");
							});
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
	}
	
	

	AddGeoLocation.filterByAccountType = function() {
		var accountTypePids = [];
		var importedStatus = "";
		$("#paccountType").find('input[type="checkbox"]:checked').each(
			function() {
				accountTypePids.push($(this).val());
			});
		var imports = $("input[name='import']:checked").val();

		$('#tBodyAddGeoLocation').html(
			"<tr><td colspan='5' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : addGeoLocationContextPath + "/filterByAccountType",
				method : 'GET',
				data : {
					accountTypePids : accountTypePids.join(","),
					importedStatus : imports,
				},

				success : function(accountProfiles) {
					$('#tBodyAddGeoLocation').html("");
					if (accountProfiles.length == 0) {
						$('#tBodyAddGeoLocation')
							.html(
								"<tr><td colspan='5' align='center'>No data available</td></tr>");
						return;
					}
					$
						.each(
							accountProfiles,
							function(index, accountProfile) {
								$('#tBodyAddGeoLocation')
									.append(
										"<tr><tr><td>"
										+ accountProfile.name
										+ "</td><td>"
										+ (accountProfile.latitude == null ? ""
											: accountProfile.latitude)
										+ "</td><td>"
										+ (accountProfile.longitude == null ? ""
											: accountProfile.longitude)
										+ "</td><td>"
										+ (accountProfile.location == null ? ""
											: accountProfile.location)
										+ "</td><td><button type='button' data-target='#enableMapModal' class='btn btn-success' onclick='AddGeoLocation.oldLatLngValues($(\"#enableMapModal\"),\""
										+ accountProfile.pid
										+ "\",this);'>Add Geo Location From Map</button></td></tr>");
							});
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
	}
	
// <button type='button' class='btn btn-blue'
// onclick='AddGeoLocation.showModalPopup($(\"#viewModal\"),\""
// + accountProfile.pid + "\",\"" + accountProfile.latitude + "\",\"" +
// accountProfile.longitude + "\",\""
// + accountProfile.location
// + "\",0);'>Add Geo Location From Order</button>

	AddGeoLocation.showModalPopup = function(el, id, lat, long, loc, action) {
		
		// resetForm();
		if (id) {
			switch (action) {
			case 0:
				addGeoLocationFromOrder(id, lat, long, loc);
				break;
			}
			el.modal('show');
		}
	}

	function addGeoLocationFromOrder(id, lat, long, loc) {
		
		accountProfilePid=id;
		$("#tblAccountProfileGeoLocation").html("");
		var row = "";
		$.ajax({
			url : addGeoLocationContextPath + "/getAccountProfileGeoLocation",
			type : 'GET',
			data : {
				accountProfilePid : id,
			},
			success : function(result) {
				console.log(lat);
				$("#oldLatitude").text((lat == "null" || lat == null ? "": lat));
				$("#oldLongitude").text((long == "null" || long == null ? "": long));
				$("#oldGeoLocation").text((loc == "null" || loc == null? "": loc));
				$.each(result,function(index, geoLocation){
					row += "<tr><td><input type='radio' name='geoLocation' id='geoLocation' value='"+geoLocation.pid+"' ></td>" 
					+"<td>"+geoLocation.latitude+"</td><td>"+geoLocation.longitude+"</td><td>"+geoLocation.location+"</td></tr>";
				});
				$("#tblAccountProfileGeoLocation").html(row);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function saveNewGeoLocation(){
		var geoLocationPid = $("input[name='geoLocation']:checked").val();
		if(geoLocationPid==null){
			alert("Please Select Geo Location");
			return;
		}
		$.ajax({
			url:addGeoLocationContextPath+"/saveGeoLocation",
			type:'POST',
			data:{
				geoLocationPid:geoLocationPid,
				accountProfilePid:accountProfilePid,
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
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = addGeoLocationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = addGeoLocationContextPath;
	}

	function searchTable(inputVal) {
		var table = $('#tBodyAddGeoLocation');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function fullscreen() {
		var elem = document.getElementById("map-canvas");

		console.log(elem);
		if (elem.requestFullscreen) {
			elem.requestFullscreen();
		} else if (elem.msRequestFullscreen) {
			elem.msRequestFullscreen();
		} else if (elem.mozRequestFullScreen) {
			elem.mozRequestFullScreen();
		} else if (elem.webkitRequestFullscreen) {
			elem.webkitRequestFullscreen();
		}
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