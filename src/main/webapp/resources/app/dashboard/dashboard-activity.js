function loadActivityBasedDashboard() {
	clearDashboardData();
	loadHeaderSummary();
	loadAttendanceOnMiddleBar();
	if(dbItemGroups.length > 0){
		$("#divUserWiseSummary").html("");
		for (let value of dbItemGroups) {
			let divUserPnl = "divDigUserPnl"+value.id;
			$("#divUserWiseSummary").append(getUserPanelHtml(value.id, value.name, divUserPnl));
			loadUsersSummaryByDashboardItemGroup(value.id, divUserPnl);
		}
	} else {
		loadUsersSummary();
	}
}

function clearDashboardData() {
	let loadingImg = '<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">';
	$("#divDaySummary").html(loadingImg);
	$("#divWeekSummary").html(loadingImg);
	$("#divMonthSummary").html(loadingImg);
	$("#divUsers").html(loadingImg);
	$("#chartContainer").html(loadingImg);
}

function getUserPanelHtml(grpId, groupName, divUserPnl) {
	let divColps = "collapse"+grpId;
	let html = '<div class="row"><div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"><div class="panel panel-primary"><div class="panel-heading" style="height: 30px;">';
	html += '<div class="panel-title"><strong>Live streaming - '+ groupName +'</strong></div>';
	html += '<div class="panel-options" style="padding: 10px 15px;"><a href="#'+ divColps +'" data-toggle="collapse"><i class="entypo-down-open" style="color: white;"></i></a></div></div>';
	html += '<div id="'+ divColps +'" class="panel-collapse"><div id="'+ divUserPnl +'" class="panel-body">Panel content</div></div></div></div></div>';
	return html;
}

// checking every order time for change tile color
if (enableWebsocket == "true") {
	setInterval(function() {
		$.each(userDataList, function(index, userData) {
			var tileColor = getTileColor(userData.lastTime);
			// change tile color class
			$("#divUser" + userData.userPid).removeClass("tile-yellow");
			$("#divUser" + userData.userPid).removeClass("tile-red");
			$("#divUser" + userData.userPid).removeClass("tile-green");
			$("#divUser" + userData.userPid).addClass(tileColor);
		});
	}, 20000);
}