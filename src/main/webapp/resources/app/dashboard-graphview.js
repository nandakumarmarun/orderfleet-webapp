// Create a Dash board object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DashboardGraphView) {
	this.DashboardGraphView = {};
}

(function() {
	'use strict';
	var contextPath = location.protocol + '//' + location.host;
	var dashboardContextPath =  contextPath + location.pathname;
	var locationDataList = [];
	var userDataList = [];
	var colors = ['red','blue','green','brown', 'coral', 'gray', 'maroon', 'orchid', 'pale', 'pink'];
	//dummy key names for morris chart yKeys 
	var keyNames = ["a", "b", "c", "d", "e" , "f", "g", "h", "i", "j", "k", "l"];
	
	$(document).ready(function() {
		$('#txtDate').val(new Date().toISOString().substring(0, 10));
		
		//root and previous links
		$("#lnkRoot").click(function () {
			DashboardGraphView.locationTileClicked($(this).data("root-territory-id"),$(this).data("root-territory-name"));
		});
		$("#lnkPrevious").click(function () {
			//get parent territory id.
			$.ajax({
				url : contextPath + "/web/dashboard/territorywise/parent-territory",
				type : "GET",
				data : {
					territoryId : territoryId
				},
				success : function(location) {
					if(location) {
						DashboardGraphView.locationTileClicked(location.id,location.name);
					}
				},
				error : function(xhr, error) {
					console.log("Error loading territory drop down..........");
				}
			});
			
		});
		
		$("#txtDate").change(function () {
			if($("#dbDashboardType").val() == "USR") {
				loadUserBasedDashboard();
			} else {
				loadTerritoryBasedDashboard();
			}
		});
		
		//drop down change
		$("#dbDashboardType").change(function () {
			if($("#dbDashboardType").val() == "USR") {
				loadUserBasedDashboard();
			} else {
				loadTerritoryBasedDashboard();
			}
		});
		//load dash board
		if($("#dbDashboardType").val() == "USR") {
			loadUserBasedDashboard();
		} else {
			loadTerritoryBasedDashboard();
		}
		loadCurrentTerritoryChildrenInDropdown();
	});
	
	function loadUserBasedDashboard() {
		dashboardContextPath = location.protocol + '//' + location.host + "/web/dashboard"; 
		var activeDate = $('#txtDate').val();
		clearDashboardData();
		loadDashboardHeaderSummaryData(activeDate);
		//createUserLiveSteamPanel();
		loadAttendanceSummary(activeDate);
		loadUserTilesData(activeDate);
		setTimerForTileColorChange();
	}
	
	function loadTerritoryBasedDashboard() {
		//dashboardContextPath = location.protocol + '//' + location.host + "/web/dashboard/territorywise";; 
		var activeDate = $('#txtDate').val();
		clearDashboardData();
		loadDashboardHeaderSummaryData(activeDate);
		createTerritoryLiveSteamPanel();
		loadTerritoryTilesData(activeDate);
	}
	
	function loadDashboardHeaderSummaryData(date) {
		console.log("loading dashboard header summary..................");
		$
				.ajax({
					url : dashboardContextPath + "/summary",
					type : "GET",
					data : {
						dashboardType : $("#dbDashboardType").val(),
						date : convertToServerFormat(date),
						territoryId : territoryId
					},
					success : function(dashboardHeaderGraphData) {
						if(dashboardHeaderGraphData == "") {
							$('#divHeader').html("<h4>Please configure dashboard items</h4>")
							return;
						}
						$('#divHeader').html('<div id="lastThirtyDaysChart"></div>');
						setLineChart(dashboardHeaderGraphData,'lastThirtyDaysChart');
						console.log("dashboard summary loaded successfully..................");
					},
					error : function(xhr, error) {
						$('#divHeader').html('<div id="lastThirtyDaysChart"></div>');
						console
								.log("Error in load Day Summary..................");
					}
				});
	}

	function setLineChart(datas, graphElementId) {
		var labelNames = datas.labels;
		var yKeyNames = keyNames.slice(0, datas.labels.length);
		var customLineColors = colors.slice(0, datas.labels.length);
		var config = {
			    xkey: 'date',
			    ykeys: yKeyNames,
			    labels: labelNames,
			    fillOpacity: 0.6,
			    hideHover: 'auto',
			    behaveLikeLine: true,
			    resize: true,
			    pointFillColors:['#ffffff'],
			    pointStrokeColors: ['black'],
			    lineColors: customLineColors
		 };
		config.element = graphElementId;
		var lineChartData = [];
		for (var i = 0; i < datas.lineGraphDatas.length; i++){
			var data = datas.lineGraphDatas[i];
			var elem = new Object();
			elem["date"] = data[0];
			for(var j = 0 ; j < yKeyNames.length; j++) {
				elem[keyNames[j]] = data[j + 1].toFixed(2);
			}
			lineChartData.push(elem);
		}
		config.data = lineChartData;
		Morris.Line(config);
	}
	
	function setLineChartTiles(datas, graphElementId) {
		var labelNames = datas.labels;
		var yKeyNames = keyNames.slice(0, datas.labels.length);
		var customLineColors = colors.slice(0, datas.labels.length);
		var config = {
			    xkey: 'date',
			    ykeys: yKeyNames,
			    labels: labelNames,
			    fillOpacity: 0.6,
			    hideHover: 'auto',
			    behaveLikeLine: true,
			    resize: true,
			    pointFillColors:['#ffffff'],
			    pointStrokeColors: ['black'],
			    lineColors:customLineColors
		 };
		config.element = graphElementId;
		var lineChartData = [];
		for (var i = 0; i < datas.lineGraphDatas.length; i++){
			var data = datas.lineGraphDatas[i];
			var elem = new Object();
			elem["date"] = data[0];
			for(var j = 0 ; j < yKeyNames.length; j++) {
				elem[keyNames[j]] = data[j + 1].toFixed(2);
			}
			lineChartData.push(elem);
		}
		config.data = lineChartData;
		Morris.Line(config);
	}
	
	
	function createTerritoryLiveSteamPanel(){
		var html = '<div class="panel-title"><strong>Location wise performance</strong></div>'
			 + '<div class="panel-options" style="padding: 10px 15px;">'
			 //+ '<button type="button" onclick="$(\'#modalDelayTime\').modal(\'show\');" class="btn btn-blue btn-xs tooltip-info">Set Delay Time</button>'
			 + '<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a></div>';
		$('#divLiveStreamPanel').html(html);
	}
	
	function createUserLiveSteamPanel() {
		var html = '<div class="panel-title"><strong>Live streaming - Location wise performance</strong>'
			 + '&nbsp;&nbsp;|&nbsp;&nbsp; <strong><b>Attendance - <label id="lblAttendedUsers">0</label>/<label id="lblTotalUsers">0</label>'
			 + '</b></strong></div>'
			 + '<div class="panel-options" style="padding: 10px 15px;">'
			 + '<button type="button" onclick="$(\'#modalDelayTime\').modal(\'show\');" class="btn btn-blue btn-xs tooltip-info">Set Delay Time</button>'
			 + '<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a></div>';
		$('#divLiveStreamPanel').html(html);
	}
	
	function loadCurrentTerritoryChildrenInDropdown() {
		$.ajax({
			url : contextPath + "/web/dashboard/territorywise/child-territories",
			type : "GET",
			data : {
				territoryId : territoryId
			},
			success : function(locations) {
				$('#sbTerritory').html('');
				var li = ""; 
				$.each(locations, function(index, loc) {
					li += "<li><a href='javascript:void(0);' onclick='DashboardGraphView.locationTileClicked(\"" +loc.id + "\", \""+ loc.name +"\");'>" + loc.name + " </a></li>"
				});
				$('#sbTerritory').html(li);
			},
			error : function(xhr, error) {
				console.log("Error loading territory drop down..........");
			}
		});
	}
	//###################### Territory tiles start #########################
	function loadTerritoryTilesData(date) {
		console.log("loading territory tiles data..................");
		$.ajax({
			url : dashboardContextPath + "/territorytiles-summary",
			type : "GET",
			data : {
				date : convertToServerFormat(date),
				territoryId : territoryId
			},
			success : function(dashboardLocations) {
				if(dashboardLocations.length == 0) {
					$("#divUsers").html("");
					return;
				}
				$("#divUsers").html("");
				locationDataList = dashboardLocations;
				$.each(dashboardLocations, function(index, dashboardLoc) {
					createTerritotyTile(dashboardLoc);
				});
				console.log("Territory tiles loaded successfully............");
			},
			error : function(xhr, error) {
				$("#divUsers").html("Error in loading territory tiles..........")
				console.log("Error in loading territory tiles..........");
			}
		});
	}
	
	function createTerritotyTile(locationData) {
		console.log(locationData);
		var tileColor = 'tile-gray';//checkTimeDiffrence(locationData.lastTime);
		var tileElement = "<div class='col-md-6'>"
			+ "<div id='divUser" + locationData.locationPid + "' class='tile-block " + tileColor + " height_tiles'>"
			+ "<div class='tile-header'>"
			+ "<a href='javascript:void(0);' onclick='DashboardGraphView.locationTileClicked(\"" +locationData.locationId + "\", \""+ locationData.locationName +"\");'>" + locationData.locationName + " </a>"
			//+ "<strong><b><label id='attPresentUsers" + locationData.locationPid + "'>"+ locationData.attendencePresentUsers +"</label>/<label id='attTotalUsers" + locationData.locationPid + "'>"+ locationData.attendenceTotalUsers +"</label></b></strong>"
			+ "</div>"
			+ "<div id='divChart"+ locationData.locationPid +"'></div>"
			+ "</div></div>";
		
		$("#divUsers").append(tileElement);
		if(locationData.dashboardGraphDTO != null) {
			setLineChartTiles(locationData.dashboardGraphDTO,"divChart"+ locationData.locationPid +"")
		}
	}
	
	DashboardGraphView.locationTileClicked = function(locationId, locationName) {
		territoryId = locationId; //assign to global territory Id
		$('#headerName').text(locationName);
		loadCurrentTerritoryChildrenInDropdown();
		if($("#dbDashboardType").val() == "USR") {
			loadUserBasedDashboard();
		} else {
			loadTerritoryBasedDashboard();
		}
	}
	
	//###################### Territory tiles end #########################
	
	//###################### User tiles start ############################
	
	function loadAttendanceSummary(date) {
		console.log("loading attendance summary..................");
		$
				.ajax({
					url : dashboardContextPath + "/attendance",
					type : "GET",
					data : {
						date : convertToServerFormat(date),
						territoryId : territoryId
					},
					success : function(attendance) {
						$("#lblTotalUsers").html(attendance.totalUsers);
						$("#lblAttendedUsers").html(attendance.attendedUsers);
						console
								.log("attendance summary loaded successfully..................");
					},
					error : function(xhr, error) {
						console
								.log("Error in load Attendance Summary..................");
					}
				});
	}
	
	function loadUserTilesData(date) {
		console.log("loading user tiles data..................");
		$.ajax({
			url : dashboardContextPath + "/users-summary",
			type : "GET",
			data : {
				date : convertToServerFormat(date),
				territoryId : territoryId
			},
			success : function(dashboardUsers) {
				$("#divUsers").html("");
				userDataList = dashboardUsers;
				$.each(dashboardUsers, function(index, dashboardUser) {
					$("#divUsers").append(createUserTile(dashboardUser));
				});
				console.log("User tiles data loaded successfully............");
			},
			error : function(xhr, error) {
				$("#divUsers").html("Error in load user tiles data..........")
				console.log("Error in load user tiles data..........");
			}
		});
	}
	
	function createUserTile(userData) {
		var attendance;
		if (userData.attendanceStatus == "True") {
			attendance = 'up';
		} else if (userData.attendanceStatus == "False") {
			attendance = 'down';
		} else {
			attendance
		}
		var lastAccountLocation = userData.lastAccountLocation != null ? userData.lastAccountLocation
				: '';
		var lastLocation = userData.lastLocation != null ? userData.lastLocation
				: '';

		var summary = getSummaryHtml(userData.userSummaryData, userData.userPid);

		var profileImage = "/resources/assets/images/user.png";
		if (userData.profileImage != null) {
			profileImage = "data:image/png;base64," + userData.profileImage;
		}
		var tileColor = checkTimeDiffrence(userData.lastTime);
		var linkToDetails = location.protocol + '//' + location.host
				+ "/web/executive-task-executions?user-key-pid="
				+ userData.userPid;
		var linkToLiveTracking = location.protocol + '//' + location.host
				+ "/web/live-tracking?user-key-pid=" + userData.userPid;

		var selDate = $('#txtDate').val();
		var skipped = "<a href='/web/day-plan-execution-report?pid="
				+ userData.userPid + "&date=" + selDate
				+ "' id='userNotification" + userData.userPid
				+ "'> <i class='entypo-attention'><span id='\skippedCount"
				+ userData.userPid + "' class='error_status_number'>"
				+ userData.notificationCount + "</span></i></a>";
		var notSkipped = "<a href='/web/day-plan-execution-report?pid="
				+ userData.userPid
				+ "&date="
				+ selDate
				+ "' id='userNotification"
				+ userData.userPid
				+ "' style=\"display: none;\"> <i class='entypo-attention'><span id='skippedCount"
				+ userData.userPid + "' class='error_status_number'>"
				+ userData.notificationCount + "</span></i></a>";
		var userNotification = userData.notificationCount > 0 ? skipped
				: notSkipped;

		var remarks = userData.remarks == "" ? "&nbsp;" : userData.remarks;

		var locIcons = locationIcons(userData);

		return "<div\n" + "class=\"tile-menu\">\n" + "<div id=\"divUser"
				+ userData.userPid
				+ "\" class=\"tile-block "
				+ tileColor
				+ " height_tiles\">\n"
				+ "<div class=\"tile-header\">\n"
				+ "<i id=\"attThumb"
				+ userData.userPid
				+ "\" class=\"entypo-thumbs-"
				+ attendance
				+ "\">"
				+ "<span style='margin-left:20%;'>"
				+ convertTime(userData.plannedDate)
				+ "</span>"
				+ "</i>"
				+ userNotification
				+ "<img\n"
				+ "src=\""
				+ profileImage
				+ "\" width=\"30\"\n"
				+ " height=\"30\"  class=\"img-circle profile_picture\"><a\n"
				+ "href=\""
				+ linkToDetails
				+ "\">"
				+ userData.userName
				+ " <span id=\"attRemark"
				+ userData.userPid
				+ "\">"
				+ remarks
				+ "</span>\n"
				+ "</a>\n"
				+ "</div>\n"
				+ "<div id=\"divMonthSummary\">"
				+ summary
				+ "</div>"
				+ "<div class=\"tile-footer\">\n"
				+ "<p id='lastLoc"
				+ userData.userPid
				+ "' class=\"address popover-default\" data-toggle=\"popover\"\n"
				+ "data-trigger=\"hover\" data-placement=\"top\"\n"
				+ "data-content=\"$('#lastLoc" + userData.userPid
				+ "').text()\"\n" + "data-original-title=\"Address\">"
				+ lastLocation + "</p>\n" + "<a href=\"" + linkToLiveTracking
				+ "\" class='icon-container' ><div class='icon' id='locIcon"
				+ userData.userPid + "' >" + locIcons + "</div></a>\n"
				+ "<p class=\"address\"><font id='lastAccLoc"
				+ userData.userPid + "'>" + lastAccountLocation
				+ "</font></p>\n" + "<p class=\"time\"><font id='lastTime"
				+ userData.userPid + "'>" + convertTime(userData.lastTime)
				+ "</font></p>\n" + "</div>\n" + "\n" + "</div>\n" + "</div";
	}

	function locationIcons(userData) {
		var images = "";
		if (userData.locationType != null) {

			if (userData.locationType == "GpsLocation") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/location.png" width="20px">';
			} else if (userData.locationType == "TowerLocation") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/tower.png" width="20px">';
			} else if (userData.locationType == "FlightMode") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/flight.png" width="20px">';
			}
			// check gsp is Off
			if (userData.isGpsOff) {
				images += ' <img src="'
						+ contextPath
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
	
	function checkTimeDiffrence(lastOrderTime) {
		console.log("lastOrderTime : " + lastOrderTime);
		if (lastOrderTime == null) {
			return "tile-red";
		}
		var delayTime = $('#txtDelayTime').val();
		var tileColor = "";
		var orderDate = new Date(convertDateTimeFromServer(lastOrderTime));
		var currentDate = new Date();
		var seconds = (currentDate.getTime() - orderDate.getTime()) / 1000;
		var minute = seconds / 60;
		if (minute < delayTime) {
			tileColor = "tile-green";
		} else if (minute > (delayTime * 2)) {
			tileColor = "tile-red";
		} else if (minute > delayTime) {
			tileColor = "tile-yellow";
		}
		return tileColor;
	}
	
	// checking every order time for change tile color
	function setTimerForTileColorChange() {
		setInterval(function() {
			console.log("checking last order time.............");
			$.each(userDataList, function(index, userData) {
				var tileColor = checkTimeDiffrence(userData.lastTime);
				// change tile color class
				$("#divUser" + userData.userPid).removeClass("tile-yellow");
				$("#divUser" + userData.userPid).removeClass("tile-red");
				$("#divUser" + userData.userPid).removeClass("tile-green");
				$("#divUser" + userData.userPid).addClass(tileColor);
			});
		}, 20000);
	}
	//###################### User tiles end #########################
	
	function getSummaryHtml(summaryDatas, key) {
		if (summaryDatas.length == 0) {
			return "<div class='tile-content'>Please configure dashboard items</div>";
		}
		var activityItems = "";
		var documentItems = "";
		$.each(summaryDatas, function(index, summary) {
			var id = key + "" + summary.dashboardItemPid;
			if (summary.dashboardItemType == "ACTIVITY") {
				activityItems += '<div class="first"><p>' + summary.label
						+ '</p><p><b id="achieved' + id + '">'
						+ summary.achieved + '</b></p><p><b>'
						+ summary.scheduled + '</b></p></div>';
			} else {
				documentItems += '<div class="first"><p>' + summary.label
						+ '</p><p><b id="count' + id + '">' + summary.count
						+ '</b></p><p><b id="amount' + id + '">'
						+ Math.round(summary.amount) + '</b></p></div>';
			}
		});
		var summary = "";
		// append activity items
		if (activityItems != "") {
			summary = '<div class="tile-content tile-content_pad"><div class="table_"><div class="head">'
					+ '<div class="first"><p class="text-center">Activities</p></div>'
					+ '<div class="second"><div><p class="text-center">Achieved</p>'
					+ '<p class="text-center">Scheduled</p></div></div></div>'
					+ '<div class="clearfix"></div><div class="body">'
					+ activityItems + '</div></div></div>';
		}
		// append document items
		if (documentItems != "") {
			summary += '<div class="tile-content padding_btm tile-content_pad"><div class="table_"><div class="head">'
					+ '<div class="first"><p class="text-center">Vouchers</p></div>'
					+ '<div class="second"><div><p class="text-center">Count</p>'
					+ '<p class="text-center">Amount</p></div></div></div>'
					+ '<div class="clearfix"></div><div class="body">'
					+ documentItems + '</div></div></div>';
		}
		return summary;
	}
	
	function clearDashboardData() {
		$("#divHeader").html('<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">');
		$("#divUsers").html('<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">');
	}

	function updateDashboardView(activity) {
		console.log("updateDashboardView..................");
		var isCurrentDate = false;
		if ($('#txtDate').val() == "") {
			isCurrentDate = true;
		} else {
			var tDate = convertDate(new Date());
			var sDate = convertDate($('#txtDate').val());
			if (sDate == tDate) {
				isCurrentDate = true;
			}
		}
		if (isCurrentDate) {
			if ($("#dbDashboardType").val() == "USR") {
				updateUserBasedDashboard(activity);
				updateLastTime(activity);
				if (activity.decrementSkipCount) {
					console.log("decrementing skip count................");
					decrementSkipCount(activity)
				}
			} else {
				updateTerritoryBasedDashboard(activity);
			}
		}
	}

	function updateTerritoryBasedDashboard(activity) {
		var toUpdateTile;
		$.each(locationDataList, function(index, locationData) {
			$.each(locationData.accountProfilePids, function(index, accPid) {
				if(accPid == activity.accountProfilePid) {
					console.log("breaking for each loop;");
					toUpdateTile = locationData.locationPid
					return false;
				}
			});
		});
		
		// change tile color to green
		$("#divUser" + toUpdateTile).removeClass("tile-yellow");
		$("#divUser" + toUpdateTile).removeClass("tile-red");
		$("#divUser" + toUpdateTile).addClass("tile-green");

		// update last location
//		$("#lastLoc" + activity.userPid).html(activity.lastLocation);
//		var locIcons = locationIcons(activity);
//		$("#locIcon" + activity.userPid).html(locIcons);

		// update last account location
		//$("#lastAccLoc" + activity.userPid).html(activity.lastAccountLocation);

		// update summary portion
		$.each(
						activity.dashboardItems,
						function(index, dashboardItem) {
							if (dashboardItem.dashboardItemType == "ACTIVITY") {

								// update day tile achieved - activity
								var activitiesDayAchieved = $(
										"#achievedday"
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesDayAchieved = parseInt(activitiesDayAchieved) + 1;
								$(
										"#achievedday"
												+ dashboardItem.dashboardItemPid)
										.html(activitiesDayAchieved);

								// update week tile achieved - activity
								var activitiesWeekAchieved = $(
										"#achievedweek"
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesWeekAchieved = parseInt(activitiesWeekAchieved) + 1;
								$(
										"#achievedweek"
												+ dashboardItem.dashboardItemPid)
										.html(activitiesWeekAchieved);

								// update month tile achieved - activity
								var activitiesMonthAchieved = $(
										"#achievedmonth"
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesMonthAchieved = parseInt(activitiesMonthAchieved) + 1;
								$(
										"#achievedmonth"
												+ dashboardItem.dashboardItemPid)
										.html(activitiesMonthAchieved);

								// update territory tile achieved - activity
								var activitiesUserAchieved = $(
										"#achieved"
												+ toUpdateTile
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesUserAchieved = parseInt(activitiesUserAchieved) + 1;
								$(
										"#achieved"
												+ toUpdateTile
												+ dashboardItem.dashboardItemPid)
										.html(activitiesUserAchieved);

							} else {
								// update day tile count - document
								var documentDayCount = $(
										"#countday"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentDayCount = parseInt(documentDayCount)
										+ dashboardItem.count;
								$("#countday" + dashboardItem.dashboardItemPid)
										.html(Math.round(documentDayCount));

								// update day tile amount - document
								var documentDayAmount = $(
										"#amountday"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentDayAmount = parseInt(documentDayAmount)
										+ dashboardItem.amount;
								$("#amountday" + dashboardItem.dashboardItemPid)
										.html(Math.round(documentDayAmount));

								// update week tile count - document
								var documentWeekCount = $(
										"#countweek"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentWeekCount = parseInt(documentWeekCount)
										+ dashboardItem.count;
								$("#countweek" + dashboardItem.dashboardItemPid)
										.html(documentWeekCount);

								// update week tile amount - document
								var documentWeekAmount = $(
										"#amountweek"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentWeekAmount = parseInt(documentWeekAmount)
										+ dashboardItem.amount;
								$(
										"#amountweek"
												+ dashboardItem.dashboardItemPid)
										.html(Math.round(documentWeekAmount));

								// update month tile count - document
								var documentMonthCount = $(
										"#countmonth"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentMonthCount = parseInt(documentMonthCount)
										+ dashboardItem.count;
								$(
										"#countmonth"
												+ dashboardItem.dashboardItemPid)
										.html(documentMonthCount);

								// update month tile amount - document
								var documentMonthAmount = $(
										"#amountmonth"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentMonthAmount = parseInt(documentMonthAmount)
										+ dashboardItem.amount;
								$(
										"#amountmonth"
												+ dashboardItem.dashboardItemPid)
										.html(Math.round(documentMonthAmount));

								// update territory tile count - document
								var documentUserCount = $(
										"#count"
												+ toUpdateTile
												+ dashboardItem.dashboardItemPid)
										.html();
								documentUserCount = parseInt(documentUserCount)
										+ dashboardItem.count;
								$(
										"#count"
												+ toUpdateTile
												+ dashboardItem.dashboardItemPid)
										.html(documentUserCount);

								// update user tile amount - document
								var documentUserAmount = $(
										"#amount"
												+ toUpdateTile
												+ dashboardItem.dashboardItemPid)
										.html();
								documentUserAmount = parseInt(documentUserAmount)
										+ dashboardItem.amount;
								$(
										"#amount"
												+ toUpdateTile
												+ dashboardItem.dashboardItemPid)
										.html(Math.round(documentUserAmount));
							}
						});
	}


	function updateUserBasedDashboard(activity) {
		// change tile color to green
		$("#divUser" + activity.userPid).removeClass("tile-yellow");
		$("#divUser" + activity.userPid).removeClass("tile-red");
		$("#divUser" + activity.userPid).addClass("tile-green");

		// update last location
		$("#lastLoc" + activity.userPid).html(activity.lastLocation);
		var locIcons = locationIcons(activity);
		$("#locIcon" + activity.userPid).html(locIcons);

		// update last account location
		$("#lastAccLoc" + activity.userPid).html(activity.lastAccountLocation);

		// update last time
		$("#lastTime" + activity.userPid).html(convertTime(activity.lastTime));

		// update summary portion
		$
				.each(
						activity.dashboardItems,
						function(index, dashboardItem) {
							if (dashboardItem.dashboardItemType == "ACTIVITY") {

								// update day tile achieved - activity
								var activitiesDayAchieved = $(
										"#achievedday"
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesDayAchieved = parseInt(activitiesDayAchieved) + 1;
								$(
										"#achievedday"
												+ dashboardItem.dashboardItemPid)
										.html(activitiesDayAchieved);

								// update week tile achieved - activity
								var activitiesWeekAchieved = $(
										"#achievedweek"
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesWeekAchieved = parseInt(activitiesWeekAchieved) + 1;
								$(
										"#achievedweek"
												+ dashboardItem.dashboardItemPid)
										.html(activitiesWeekAchieved);

								// update month tile achieved - activity
								var activitiesMonthAchieved = $(
										"#achievedmonth"
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesMonthAchieved = parseInt(activitiesMonthAchieved) + 1;
								$(
										"#achievedmonth"
												+ dashboardItem.dashboardItemPid)
										.html(activitiesMonthAchieved);

								// update user tile achieved - activity
								var activitiesUserAchieved = $(
										"#achieved"
												+ activity.userPid
												+ dashboardItem.dashboardItemPid)
										.html();
								activitiesUserAchieved = parseInt(activitiesUserAchieved) + 1;
								$(
										"#achieved"
												+ activity.userPid
												+ dashboardItem.dashboardItemPid)
										.html(activitiesUserAchieved);

							} else {
								// update day tile count - document
								var documentDayCount = $(
										"#countday"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentDayCount = parseInt(documentDayCount)
										+ dashboardItem.count;
								$("#countday" + dashboardItem.dashboardItemPid)
										.html(Math.round(documentDayCount));

								// update day tile amount - document
								var documentDayAmount = $(
										"#amountday"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentDayAmount = parseInt(documentDayAmount)
										+ dashboardItem.amount;
								$("#amountday" + dashboardItem.dashboardItemPid)
										.html(Math.round(documentDayAmount));

								// update week tile count - document
								var documentWeekCount = $(
										"#countweek"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentWeekCount = parseInt(documentWeekCount)
										+ dashboardItem.count;
								$("#countweek" + dashboardItem.dashboardItemPid)
										.html(documentWeekCount);

								// update week tile amount - document
								var documentWeekAmount = $(
										"#amountweek"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentWeekAmount = parseInt(documentWeekAmount)
										+ dashboardItem.amount;
								$(
										"#amountweek"
												+ dashboardItem.dashboardItemPid)
										.html(Math.round(documentWeekAmount));

								// update month tile count - document
								var documentMonthCount = $(
										"#countmonth"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentMonthCount = parseInt(documentMonthCount)
										+ dashboardItem.count;
								$(
										"#countmonth"
												+ dashboardItem.dashboardItemPid)
										.html(documentMonthCount);

								// update month tile amount - document
								var documentMonthAmount = $(
										"#amountmonth"
												+ dashboardItem.dashboardItemPid)
										.html();
								documentMonthAmount = parseInt(documentMonthAmount)
										+ dashboardItem.amount;
								$(
										"#amountmonth"
												+ dashboardItem.dashboardItemPid)
										.html(Math.round(documentMonthAmount));

								// update user tile count - document
								var documentUserCount = $(
										"#count"
												+ activity.userPid
												+ dashboardItem.dashboardItemPid)
										.html();
								documentUserCount = parseInt(documentUserCount)
										+ dashboardItem.count;
								$(
										"#count"
												+ activity.userPid
												+ dashboardItem.dashboardItemPid)
										.html(documentUserCount);

								// update user tile amount - document
								var documentUserAmount = $(
										"#amount"
												+ activity.userPid
												+ dashboardItem.dashboardItemPid)
										.html();
								documentUserAmount = parseInt(documentUserAmount)
										+ dashboardItem.amount;
								$(
										"#amount"
												+ activity.userPid
												+ dashboardItem.dashboardItemPid)
										.html(Math.round(documentUserAmount));
							}
						});
	}

	function updateLastTime(activity) {
		$.each(userDataList, function(index, userData) {
			if (userData.userPid == activity.userPid) {
				console.log("lastTime.......before" + userData.lastTime);
				userData.lastTime = activity.lastTime;
				console.log("lastTime.......after" + userData.lastTime);
				return;
			}
		});
	}
	

	function decrementSkipCount(activity) {
		// change user notification icon
		var userPid = activity.userPid;
		$("#userNotification" + userPid).show();
		var skipped = $("#skippedCount" + userPid).text();
		skipped = parseInt(skipped) - 1;
		$("#skippedCount" + userPid).text(skipped);
	}
	
	function convertDate(date) {
		if (date) {
			return moment(date).format('DD/MM/YY');
		} else {
			return "";
		}
	}

	function convertTime(date) {
		if (date) {
			return moment(date).format('HH:mm');
		} else {
			return "";
		}
	}

	function convertToServerFormat(date) {
		if (date) {
			return moment(date).format('YYYY-MM-DD');
		} else {
			return "";
		}
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

})();