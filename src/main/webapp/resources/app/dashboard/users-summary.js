var userDataList = [];
function loadUsersSummaryByDashboardItemGroup(groupId, divUserPnlName) {
	let selDate = new Date($('#txtDate').val());
	$
			.ajax({
				url : appContextPath
						+ "/web/dashboard/users-summary/dashboarditem-group/"
						+ groupId,
				type : "GET",
				data : {
					date : formatDate(selDate, 'YYYY-MM-DD'),
				},
				success : function(usersSummary) {
					$("#" + divUserPnlName + "").html("");
					userDataList.push(usersSummary);
					createDigUserTiles(usersSummary, divUserPnlName);
				},
				error : function(xhr, error) {
					let msg = "<< Error in loading dashboard item group user wise summary >>";
					$("#" + divUserPnlName + "").html(msg)
					console.log(msg);
				}
			});
}

function createDigUserTiles(summaryDatas, divUserPnlName) {
	let html = '';
	$.each(summaryDatas, function(index, smryData) {
		html += createTile(smryData);
	});
	$("#" + divUserPnlName + "").append(html);
}

function loadUsersSummary() {
	let selDate = new Date($('#txtDate').val());
	console.log("loading user wise summary............");
	$.ajax({
		url : appContextPath + "/web/dashboard/users-summary",
		type : "GET",
		data : {
			date : formatDate(selDate, 'YYYY-MM-DD'),
		},
		success : function(usersSummary) {
			console.log(usersSummary);
			$("#divUsers").html("");
			userDataList = usersSummary;
			createUserTiles(usersSummary);
		},
		error : function(xhr, error) {
			let msg = "<< Error in loading dashboard user wise summary >>";
			$("#divUsers").html(msg)
			console.log(msg);
		}
	});
}

function createUserTiles(summaryDatas) {
	let html = '';
	$.each(summaryDatas, function(index, smryData) {
		html += createTile(smryData);
		startCustomerTimeSpendClock(smryData);
	});
	$("#divUsers").append(html);
}

function createTile(userData) {
	return makeUserTileHeaderHtml(userData) + makeUserTileFooterHtml(userData);
}

function makeUserTileHeaderHtml(userData) {
	let remarks = userData.remarks == "" ? "&nbsp;" : userData.remarks;
	let userProfileImg = userData.profileImage != null ? ('data:image/png;base64,' + userData.profileImage)
			: '/resources/assets/images/user.png';
	let attendanceSubGroup = userData.attendanceSubGroupCode != null ? userData.attendanceSubGroupCode
			: '';
	let empName = userData.userName != null ? userData.userName
			.substring(0, 13) : "";

	let html = "<div class='tile-menu'><div id='divUser" + userData.userPid
			+ "' class='tile-block " + getTileColor(userData.lastTime)
			+ " height_tiles'>";
	html += "<div class='tile-header'><i id='attSubgroup" + userData.userPid
			+ "'>" + attendanceSubGroup + "</i>";
	html += getAttendanceThumbsHtml(userData);
	html += getUserNotification(userData.userPid, userData.notificationCount);
	html += "<img src='"
			+ userProfileImg
			+ "' width='30' height='30' class='img-circle profile_picture zoom'>";
	html += "<a href='" + appContextPath
			+ '/web/executive-task-executions?user-key-pid='
			+ userData.employeePid + "'>" + "<span class='text'>" + empName
			+ "</span><span title='" + remarks + "' class='text' id='attRemark"
			+ userData.userPid + "'>" + remarks + "</span></a></div>";
	html += "<div id='divMonthSummary'>"
			+ createSingleTile(userData.userSummaryData, userData.userPid,
					userData.employeePid) + "</div>";
	return html;
}

function makeUserTileFooterHtml(userData) {
	let lastLocation = userData.lastLocation != null ? userData.lastLocation
			: '';
	var mockLocation = "";
	if (userData.mockLocationStatus) {
		mockLocation = "Mock Location Is Enabled"
	}
	let lastAccountLocation = userData.lastAccountLocation != null ? userData.lastAccountLocation
			: '';
	let html = "<div class='tile-footer'><div id='clock"
			+ userData.userPid
			+ "' style='zoom: 0.39; -moz-transform: scale(0.5);display:none;margin-left: 8%;'></div>"
			+ "<div id='location"
			+ userData.userPid
			+ "' style='display:block;'>"
			+ "<p id='lastLoc"
			+ userData.userPid
			+ "' class='address popover-default' data-toggle='popover' data-trigger='hover' data-placement='top' data-content=\"$('#lastLoc"
			+ userData.userPid + "').text()\" data-original-title='Address'>";
	html += lastLocation + "</p><a href='" + appContextPath
			+ '/web/live-tracking?user-key-pid=' + userData.userPid
			+ "' class='icon-container'><div class='icon' id='locIcon"
			+ userData.userPid + "' >" + getLocationIcons(userData)
			+ "</div></a>";
	html += "<p class='address'><font id='lastAccLoc" + userData.userPid + "'>"
			+ lastAccountLocation + "</font></p>";
	html += "<p class='time'><font id='lastTime" + userData.userPid + "'>"
			+ formatDate(userData.lastTime, 'HH:mm') + "</font></p>"
			+ "</div></div><br><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ mockLocation +"</p></div></div>";

	return html;
}

function getTileColor(lastOrderTime) {
	let tileColor = "tile-red";
	if (lastOrderTime != null) {
		let delayTime = $('#txtDelayTime').val();
		let orderDate = new Date(formatDate(lastOrderTime,
				'MMM DD YYYY, h:mm:ss a'));
		let currentDate = new Date();
		let seconds = (currentDate.getTime() - orderDate.getTime()) / 1000;
		let minute = seconds / 60;
		if (minute < delayTime) {
			tileColor = "tile-green";
		} else if (minute > (delayTime * 2)) {
			tileColor = "tile-red";
		} else if (minute > delayTime) {
			tileColor = "tile-yellow";
		}
	}
	return tileColor;
}

function getAttendanceThumbsHtml(userData) {
	let attendance, attendanceTitle;
	if (userData.attendanceStatus == "True") {
		attendance = 'up';
		attendanceTitle = 'present';
	} else if (userData.attendanceStatus == "False") {
		attendance = 'down';
		attendanceTitle = 'absent';
	}
	return "<a href='/web/attendance-report/all?empPid=" + userData.employeePid
			+ "'><i id='attThumb" + userData.userPid + "' title='"
			+ attendanceTitle + "' class='entypo-thumbs-" + attendance
			+ "'><span style='margin-left:20%;'>"
			+ formatDate(userData.plannedDate, 'HH:mm') + "</span></i></a>";
}

function getUserNotification(userPid, notificationCount) {
	let dateAr = ($('#txtDate').val()).split('-');
	let newDate = dateAr[2] + '-' + dateAr[1] + '-' + dateAr[0];
	let skipped = "<a href='/web/day-plan-execution-report?pid=" + userPid
			+ "&date=" + newDate + "' id='userNotification" + userPid
			+ "'> <i class='entypo-attention'><span id='\skippedCount"
			+ userPid + "' class='error_status_number'>" + notificationCount
			+ "</span></i></a>";
	let notSkipped = "<a href='/web/day-plan-execution-report?pid="
			+ userPid
			+ "&date="
			+ newDate
			+ "' id='userNotification"
			+ userPid
			+ "' style=\"display: none;\"> <i class='entypo-attention'><span id='skippedCount"
			+ userPid + "' class='error_status_number'>" + notificationCount
			+ "</span></i></a>";
	return notificationCount > 0 ? skipped : notSkipped;
}

function getLocationIcons(userData) {
	let images = "";
	if (userData.locationType != null) {
		if (userData.locationType == "GpsLocation") {
			images = '<img src="'
					+ appContextPath
					+ '/resources/assets/images/map/location.png" width="20px">';
		} else if (userData.locationType == "TowerLocation") {
			images = '<img src="' + appContextPath
					+ '/resources/assets/images/map/tower.png" width="20px">';
		} else if (userData.locationType == "FlightMode") {
			images = '<img src="' + appContextPath
					+ '/resources/assets/images/map/flight.png" width="20px">';
		}
		// check gsp is Off
		if (userData.isGpsOff && userData.locationType != "GpsLocation") {
			images += ' <img src="' + appContextPath
					+ '/resources/assets/images/map/gps-off.png" width="20px">';
		}
		// check Mobile Data is Off
		if (userData.isMobileDataOff) {
			images += ' <img src="'
					+ appContextPath
					+ '/resources/assets/images/map/mobile-data-off.png" width="20px">';
		}
	}
	return images;
}

function startCustomerTimeSpendClock(userData) {
	if (userData.customerTimeSpentBoolean) {
		let clock = $('#clock' + userData.userPid).FlipClock({});
		clock.setTime(userData.customerTimeSpentTime);
		clock.start();
		$('#clock' + userData.userPid).css('display', 'block');
		$('#location' + userData.userPid).css('display', 'none');
	} else {
		$('#location' + userData.userPid).css('display', 'none');
	}
}
