function updateTileWithActivity(activity) {
	var isCurrentDate = false;
	if ($('#txtDate').val() == "") {
		isCurrentDate = true;
	} else {
		var tDate = formatDate(new Date(), 'DD MMM YYYY');
		var sDate = formatDate($('#txtDate').val(), 'DD MMM YYYY');
		if (sDate == tDate) {
			isCurrentDate = true;
		}
	}
	if (isCurrentDate) {
		// update dashboard
		updateDashboard(activity);

		// update last time
		updateLastTime(activity);

		if (activity.decrementSkipCount) {
			console.log("decrementing skip count................");
			decrementSkipCount(activity)
		}
	}
}

function decrementSkipCount(activity) {
	// change user notification icon
	var userPid = activity.userPid;
	$("#userNotification" + userPid).show();
	var skipped = $("#skippedCount" + userPid).text();
	skipped = parseInt(skipped) - 1;
	$("#skippedCount" + userPid).text(skipped);
}

function updateDashboard(activity) {
	// change tile color to green
	$("#divUser" + activity.userPid).removeClass("tile-yellow");
	$("#divUser" + activity.userPid).removeClass("tile-red");
	$("#divUser" + activity.userPid).addClass("tile-green");

	// number circle
	if (activity.reloadNumberCircle == true) {
		// remove circle
		$("#divUser" + activity.userPid + " b").removeClass("number-circle");
	}

	// update last location
	$("#lastLoc" + activity.userPid).html(activity.lastLocation);
	var locIcons = getLocationIcons(activity);
	$("#locIcon" + activity.userPid).html(locIcons);

	// update last account location
	$("#lastAccLoc" + activity.userPid).html(activity.lastAccountLocation);

	// update last time
	$("#lastTime" + activity.userPid).html(formatDate(activity.lastTime, 'HH:mm'));
	// update summary portion
	if (activity.dashboardItems != null) {
		updateActivityAndDocument(activity);
	}
	// flip clock
	updateFlipClockTimer(activity);
}

function updateActivityAndDocument(activity) {
	$
			.each(
					activity.dashboardItems,
					function(index, dashboardItem) {
						if (dashboardItem.dashboardItemType == "ACTIVITY") {
							updateActivityAreaWithWebsocketData(dashboardItem,
									activity);
						} else if (dashboardItem.dashboardItemType == "DOCUMENT") {
							updateDocumentAreaWithWebsocketData(dashboardItem,
									activity);
						} else if (dashboardItem.dashboardItemType == "PRODUCT") {
							updateProductAreaWithWebsocketData(dashboardItem,
									activity);
						} else if (dashboardItem.dashboardItemType == "TARGET") {
							updateSalesTargetBlockAreaWithWebsocketData(
									dashboardItem, activity);
						}
					});
}

function updateLastTime(activity) {
	$.each(userDataList, function(index, userData) {
		if (userData.userPid == activity.userPid) {
			userData.lastTime = activity.lastTime;
			return;
		}
	});
}

function updateFlipClockTimer(cusTimeSpents) {
	if (cusTimeSpents == null) {
		return;
	}
	if (cusTimeSpents.customerTimeSpentType == "RESET") {
		$('#clock' + cusTimeSpents.userPid).css('display', 'block');
		$('#clock' + cusTimeSpents.userPid).FlipClock({});
		$('#location' + cusTimeSpents.userPid).css('display', 'none');
	} else if (cusTimeSpents.customerTimeSpentType == "SHOW") {
		$('#clock' + cusTimeSpents.userPid).css('display', 'block');
		$('#location' + cusTimeSpents.userPid).css('display', 'none');
	} else {
		$('#clock' + cusTimeSpents.userPid).css('display', 'none');
		$('#clock' + cusTimeSpents.userPid).html("");
		$('#location' + cusTimeSpents.userPid).css('display', 'block');
	}
}

function updateActivityAreaWithWebsocketData(dashboardItem, activity) {
	// update day tile achieved - activity
	var activitiesDayAchieved = $(
			"#achievedday" + dashboardItem.dashboardItemPid).html();
	activitiesDayAchieved = parseInt(activitiesDayAchieved) + 1;
	$("#achievedday" + dashboardItem.dashboardItemPid).html(
			activitiesDayAchieved);

	// update week tile achieved - activity
	var activitiesWeekAchieved = $(
			"#achievedweek" + dashboardItem.dashboardItemPid).html();
	activitiesWeekAchieved = parseInt(activitiesWeekAchieved) + 1;
	$("#achievedweek" + dashboardItem.dashboardItemPid).html(
			activitiesWeekAchieved);

	// update month tile achieved - activity
	var activitiesMonthAchieved = $(
			"#achievedmonth" + dashboardItem.dashboardItemPid).html();
	activitiesMonthAchieved = parseInt(activitiesMonthAchieved) + 1;
	$("#achievedmonth" + dashboardItem.dashboardItemPid).html(
			activitiesMonthAchieved);

	// update user tile achieved - activity
	var activitiesUserAchieved = $(
			"#achieved" + activity.userPid + dashboardItem.dashboardItemPid)
			.html();
	activitiesUserAchieved = parseInt(activitiesUserAchieved) + 1;
	$("#achieved" + activity.userPid + dashboardItem.dashboardItemPid).html(
			activitiesUserAchieved);
}

function updateDocumentAreaWithWebsocketData(dashboardItem, activity) {
	// update day tile count - document
	var documentDayCount = $("#countday" + dashboardItem.dashboardItemPid)
			.html();
	documentDayCount = parseInt(documentDayCount) + dashboardItem.count;
	$("#countday" + dashboardItem.dashboardItemPid).html(
			Math.round(documentDayCount));

	// update day tile amount - document
	var documentDayAmount = $("#amountday" + dashboardItem.dashboardItemPid)
			.html();
	documentDayAmount = parseInt(documentDayAmount) + dashboardItem.amount;
	$("#amountday" + dashboardItem.dashboardItemPid).html(
			Math.round(documentDayAmount));

	// update week tile count - document
	var documentWeekCount = $("#countweek" + dashboardItem.dashboardItemPid)
			.html();
	documentWeekCount = parseInt(documentWeekCount) + dashboardItem.count;
	$("#countweek" + dashboardItem.dashboardItemPid).html(documentWeekCount);

	// update week tile amount - document
	var documentWeekAmount = $("#amountweek" + dashboardItem.dashboardItemPid)
			.html();
	documentWeekAmount = parseInt(documentWeekAmount) + dashboardItem.amount;
	$("#amountweek" + dashboardItem.dashboardItemPid).html(
			Math.round(documentWeekAmount));

	// update month tile count - document
	var documentMonthCount = $("#countmonth" + dashboardItem.dashboardItemPid)
			.html();
	documentMonthCount = parseInt(documentMonthCount) + dashboardItem.count;
	$("#countmonth" + dashboardItem.dashboardItemPid).html(documentMonthCount);

	// update month tile amount - document
	var documentMonthAmount = $("#amountmonth" + dashboardItem.dashboardItemPid)
			.html();
	documentMonthAmount = parseInt(documentMonthAmount) + dashboardItem.amount;
	$("#amountmonth" + dashboardItem.dashboardItemPid).html(
			Math.round(documentMonthAmount));

	// update user tile count - document
	var documentUserCount = $(
			"#count" + activity.userPid + dashboardItem.dashboardItemPid)
			.html();
	// circle on count
	$("#count" + activity.userPid + dashboardItem.dashboardItemPid).addClass(
			"number-circle");
	documentUserCount = parseInt(documentUserCount) + dashboardItem.count;
	$("#count" + activity.userPid + dashboardItem.dashboardItemPid).html(
			documentUserCount);

	// update user tile amount - document
	var documentUserAmount = $(
			"#amount" + activity.userPid + dashboardItem.dashboardItemPid)
			.html();
	documentUserAmount = parseInt(documentUserAmount) + dashboardItem.amount;
	$("#amount" + activity.userPid + dashboardItem.dashboardItemPid).html(
			Math.round(documentUserAmount));
}

function updateProductAreaWithWebsocketData(dashboardItem, activity) {
	// Update Summary Tiles
	var productDayVolume = $("#volumeday" + dashboardItem.dashboardItemPid)
			.html();
	productDayVolume = parseInt(productDayVolume) + dashboardItem.volume;
	$("#volumeday" + dashboardItem.dashboardItemPid).html(
			Math.round(productDayVolume));
	var productDayAmount = $(
			"#productAmountday" + dashboardItem.dashboardItemPid).html();
	productDayAmount = parseInt(productDayAmount) + dashboardItem.amount;
	$("#productAmountday" + dashboardItem.dashboardItemPid).html(
			Math.round(productDayAmount));

	var productWeekVolume = $("#volumeweek" + dashboardItem.dashboardItemPid)
			.html();
	productWeekVolume = parseInt(productWeekVolume) + dashboardItem.volume;
	$("#volumeweek" + dashboardItem.dashboardItemPid).html(productWeekVolume);
	var productWeekAmount = $(
			"#productAmountweek" + dashboardItem.dashboardItemPid).html();
	productWeekAmount = parseInt(productWeekAmount) + dashboardItem.amount;
	$("#productAmountweek" + dashboardItem.dashboardItemPid).html(
			Math.round(productWeekAmount));

	var productMonthVolume = $("#volumemonth" + dashboardItem.dashboardItemPid)
			.html();
	productMonthVolume = parseInt(productMonthVolume) + dashboardItem.volume;
	$("#volumemonth" + dashboardItem.dashboardItemPid).html(productMonthVolume);
	var productMonthAmount = $(
			"#productAmountmonth" + dashboardItem.dashboardItemPid).html();
	productMonthAmount = parseInt(productMonthAmount) + dashboardItem.amount;
	$("#productAmountmonth" + dashboardItem.dashboardItemPid).html(
			Math.round(productMonthAmount));

	// Update User Tiles
	var productUserVolume = $(
			"#volume" + activity.userPid + dashboardItem.dashboardItemPid)
			.html();
	productUserVolume = parseInt(productUserVolume) + dashboardItem.volume;
	$("#volume" + activity.userPid + dashboardItem.dashboardItemPid).html(
			productUserVolume);
	var productUserAmount = $(
			"#productAmount" + activity.userPid
					+ dashboardItem.dashboardItemPid).html();
	productUserAmount = parseInt(productUserAmount) + dashboardItem.amount;
	$("#productAmount" + activity.userPid + dashboardItem.dashboardItemPid)
			.html(Math.round(productUserAmount));
}

function updateSalesTargetBlockAreaWithWebsocketData(dashboardItem, activity) {
	if (dashboardItem.targetType == "VOLUME") {
		// Update Summary Tiles
		var targetDayVolume = $(
				"#tbAchievedday" + dashboardItem.dashboardItemPid).html();
		targetDayVolume = parseInt(targetDayVolume)
				+ dashboardItem.targetAchievedVolume;
		$("#tbAchievedday" + dashboardItem.dashboardItemPid).html(
				Math.round(targetDayVolume));
		var targetWeekVolume = $(
				"#tbAchievedweek" + dashboardItem.dashboardItemPid).html();
		targetWeekVolume = parseInt(targetWeekVolume)
				+ dashboardItem.targetAchievedVolume;
		$("#tbAchievedweek" + dashboardItem.dashboardItemPid).html(
				targetWeekVolume);
		var targetMonthVolume = $(
				"#tbAchievedmonth" + dashboardItem.dashboardItemPid).html();
		targetMonthVolume = parseInt(targetMonthVolume)
				+ dashboardItem.targetAchievedVolume;
		$("#tbAchievedmonth" + dashboardItem.dashboardItemPid).html(
				targetMonthVolume);

		// Update User Tiles
		var targetUserVolume = $(
				"#tbAchieved" + activity.userPid
						+ dashboardItem.dashboardItemPid).html();
		targetUserVolume = parseInt(targetUserVolume)
				+ dashboardItem.targetAchievedVolume;
		$("#tbAchieved" + activity.userPid + dashboardItem.dashboardItemPid)
				.html(targetUserVolume);

		// Update Tiles target Label
		var load = 0;
		if (dashboardItem.targetAverageVolume > 0) {
			load = $(
					"#tbLabel" + activity.userPid
							+ dashboardItem.dashboardItemPid).html().replace(
					/[^0-9$.,]/g, '');
			load = (Number(load.trim()) + dashboardItem.targetAverageVolume);
			load = Math.round(load * 100) / 100;
			$("#tbLabel" + activity.userPid + dashboardItem.dashboardItemPid)
					.html(dashboardItem.label + ' (' + load + ')');

			load = $("#tbLabelday" + dashboardItem.dashboardItemPid).html()
					.replace(/[^0-9$.,]/g, '');
			load = (Number(load.trim()) + dashboardItem.targetAverageVolume);
			load = Math.round(load * 100) / 100;
			$("#tbLabelday" + dashboardItem.dashboardItemPid).html(
					dashboardItem.label + ' (' + load + ')');

			load = $("#tbLabelweek" + dashboardItem.dashboardItemPid).html()
					.replace(/[^0-9$.,]/g, '');
			load = (Number(load.trim()) + dashboardItem.targetAverageVolume);
			load = Math.round(load * 100) / 100;
			$("#tbLabelweek" + dashboardItem.dashboardItemPid).html(
					dashboardItem.label + ' (' + load + ')');

			load = $("#tbLabelmonth" + dashboardItem.dashboardItemPid).html()
					.replace(/[^0-9$.,]/g, '');
			load = (Number(load.trim()) + dashboardItem.targetAverageVolume);
			load = Math.round(load * 100) / 100;
			$("#tbLabelmonth" + dashboardItem.dashboardItemPid).html(
					dashboardItem.label + ' (' + load + ')');
		}
	} else {
		// Update Summary Tiles
		var targetDayAmount = $(
				"#tbAchievedday" + dashboardItem.dashboardItemPid).html();
		targetDayAmount = parseInt(targetDayAmount)
				+ dashboardItem.targetAchievedAmount;
		$("#tbAchievedday" + dashboardItem.dashboardItemPid).html(
				Math.round(targetDayAmount));
		var targetWeekAmount = $(
				"#tbAchievedweek" + dashboardItem.dashboardItemPid).html();
		targetWeekAmount = parseInt(targetWeekAmount)
				+ dashboardItem.targetAchievedAmount;
		$("#tbAchievedweek" + dashboardItem.dashboardItemPid).html(
				Math.round(targetWeekAmount));
		var targetMonthAmount = $(
				"#tbAchievedmonth" + dashboardItem.dashboardItemPid).html();
		targetMonthAmount = parseInt(targetMonthAmount)
				+ dashboardItem.targetAchievedAmount;
		$("#tbAchievedmonth" + dashboardItem.dashboardItemPid).html(
				Math.round(targetMonthAmount));

		// Update User Tiles
		var targetUserAmount = $(
				"#tbAchieved" + activity.userPid
						+ dashboardItem.dashboardItemPid).html();
		targetUserAmount = parseInt(targetUserAmount)
				+ dashboardItem.targetAchievedAmount;
		$("#tbAchieved" + activity.userPid + dashboardItem.dashboardItemPid)
				.html(Math.round(targetUserAmount));
		if (targetUserAmount > 0) {
			var load = 0;
			load = targetUserAmount
					/ (Number($(
							"#tbPlanned" + activity.userPid
									+ dashboardItem.dashboardItemPid).html()));
			load = Math.round(load * 100) / 100;
			$("#tbLabel" + activity.userPid + dashboardItem.dashboardItemPid)
					.html(dashboardItem.label + ' (' + load + ')');
		}
	}
}