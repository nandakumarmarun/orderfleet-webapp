function loadDashboardData() {
  if ($("#dbDashboardType").val() == "ACTIVITY") {
    loadActivityBasedDashboard();
  } else {
    loadAttendanceBasedDashboard();
  }
}

function loadActivityBasedDashboard() {
  console.log("Load By Activity");
  clearDashboardData();
  loadHeaderSummary();
  loadAttendanceOnMiddleBar();
  loadUsersSummary();
}


function loadAttendanceBasedDashboard() {
	let loadingImg = '<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">';
	$("#divDaySummary").html(loadingImg);
	$("#divWeekSummary").html(loadingImg);
	$("#divMonthSummary").html(loadingImg);
	loadAttendanceHeaderData();
	loadUsersSummary();
}


function clearDashboardData() {
  console.log("clear dashbord Data ");
  let loadingImg =
    '<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">';
  $("#divDaySummary").html(loadingImg);
  $("#divWeekSummary").html(loadingImg);
  $("#divMonthSummary").html(loadingImg);
  $("#divUsers").html(loadingImg);
  $("#chartContainer").html(loadingImg);
}

function loadHeaderSummary() {
  let selDate = new Date($("#txtDate").val());
  console.log("loading header summary............");
  $.ajax({
    url: appContextPath + "/web/dashboard/summary-denormalised",
    type: "GET",
    data: {
      date: formatDate(selDate, "YYYY-MM-DD"),
    },
    success: function (headerSummary) {
      console.log(headerSummary);
      $("#divDaySummary").html(
        createSingleUserTile(headerSummary.daySummaryDatas, "day")
      );
      $("#divWeekSummary").html(
        createSingleUserTile(headerSummary.weekSummaryDatas, "week")
      );
      $("#divMonthSummary").html(
        createSingleUserTile(headerSummary.monthSummaryDatas, "month")
      );
      //loading here to properly set the height of the chart
      loadDashboardSummaryChart(selDate);
    },
    error: function (xhr, error) {
      console.log("<< Error in loading dashboard header summary >>");
    },
  });
}


function loadDashboardSummaryChart(date) {
  $.ajax({
    url: appContextPath + "/web/dashboard/summary/chart-denormalised",
    type: "GET",
    data: {
      date: formatDate(date, "YYYY-MM-DD"),
    },
    success: function (chartData) {
      $("#chartContainer").html("");
      let dayTileHeight = $("#divDaySummary").parent().height();
      $("#divDaySummary").parent().height();
      if (chartData.chartType == "FUNNEL") {
        loadDashboardSummaryFunnelChart(
          chartData.funnelChartDtos,
          dayTileHeight
        );
      } else {
        loadDashboardSummaryBarChart(chartData.barChartDtos, dayTileHeight);
      }
      $("#chartLabel").html(chartData.chartLabel);
    },
    error: function (xhr, error) {
      console.log("<< Error in loading chart >>");
    },
  });
}

function loadDashboardSummaryBarChart(barChartData, dayTileHeight) {
  let barChart = new britecharts.bar();
  let tooltip = new britecharts.miniTooltip();
  console.log("Enter To summary bar Chart....");
  tooltip.numberFormat(",d");
  let barContainer = d3.select("#chartContainer");
  let containerWidth = barContainer.node()
    ? barContainer.node().getBoundingClientRect().width
    : false;
  barChart
    .width(containerWidth)
    .height(dayTileHeight)
    .isAnimated(true)
    .enableLabels(true)
    .labelsNumberFormat(",d")
    .on("customMouseOver", tooltip.show)
    .on("customMouseMove", tooltip.update)
    .on("customMouseOut", tooltip.hide);

  barContainer.datum(barChartData).call(barChart);
  let tooltipContainer = d3.select(
    "#chartContainer .bar-chart .metadata-group"
  );
  tooltipContainer.datum([]).call(tooltip);
}

function loadUsersSummary() {
  let selDate = new Date($("#txtDate").val());
  console.log("loading user wise summary............");
  $.ajax({
    url: appContextPath + "/web/dashboard/user-summary-denormalised",
    type: "GET",
    data: {
      date: formatDate(selDate, "YYYY-MM-DD"),
    },
    success: function (usersSummary) {
      console.log(usersSummary);
      $("#divUsers").html("");
      userDataList = usersSummary;
      createUserTiles(usersSummary);
    },
    error: function (xhr, error) {
      let msg = "<< Error in loading dashboard user wise summary >>";
      $("#divUsers").html(msg);
      console.log(msg);
    },
  });
}

function createUserTiles(summaryDatas) {
  let html = "";
  $.each(summaryDatas, function (index, smryData) {
    html += createTile(smryData);
    //     startCustomerTimeSpendClock(smryData);
  });
  $("#divUsers").append(html);
}

function createTile(userData) {
  return makeUserTileHeaderHtml(userData) + makeUserTileFooterHtml(userData);
}

function makeUserTileHeaderHtml(userData) {
  let remarks = userData.remarks == "" ? "&nbsp;" : userData.remarks;
  let userProfileImg =
    userData.profileImage != null
      ? "data:image/png;base64," + userData.profileImage
      : "/resources/assets/images/user.png";
  let attendanceSubGroup =
    userData.attendanceSubGroupCode != null
      ? userData.attendanceSubGroupCode
      : "";
  let empName =
    userData.userName != null ? userData.userName.substring(0, 13) : "";

  let html =
    "<div class='tile-menu'><div id='divUser" +
    userData.userPid +
    "' class='tile-block " +
    getTileColor(userData, userData.lastTime) +
    " height_tiles'>";
  html +=
    "<div class='tile-header'><i id='attSubgroup" +
    userData.userPid +
    "'>" +
    attendanceSubGroup +
    "</i>";
  html += getAttendanceThumbsHtml(userData);
  html += getUserNotification(userData.userPid, userData.notificationCount);
  html +=
    "<img src='" +
    userProfileImg +
    "' width='30' height='30' class='img-circle profile_picture zoom'>";
  html +=
    "<a href='" +
    appContextPath +
    "/web/executive-task-executions?user-key-pid=" +
    userData.employeePid +
    "'>" +
    "<span class='text'>" +
    empName +
    "</span><span title='" +
    (remarks != null ? remarks : "") +
    "' class='text' id='attRemark" +
    userData.userPid +
    "'>" +
    remarks +
    "</span></a></div>";
  html +=
    "<div id='divMonthSummary'>" +
    createSingleUserTile(
      userData.userSummaryData,
      userData.userPid,
      userData.employeePid
    ) +
    "</div>";
  return html;
}

function createSingleUserTile(summaryDatas, tileName, empPid) {
  console.log("CreateSingleTileData.......");
  if (summaryDatas.length == 0) {
    return "<div class='tile-content'>Please configure dashboard items</div>";
  }
  let actySecHtml = "";
  let docSecHtml = "";
  let prdctSecHtml = "";
  let targetSecHtml = "";

  $.each(summaryDatas, function (index, smryData) {
    var id = tileName + smryData.dashboardItemPid;
    if ($("#dbDashboardType").val() == "ACTIVITY") {
      if (smryData.dashboardItemType == "ACTIVITY") {
        actySecHtml += makeActivitySingleSectionHtml(smryData, id);
      } else if (smryData.dashboardItemType == "DOCUMENT") {
        docSecHtml += makeDocumentsingleSectionHtml(smryData, id, empPid);
      } else if (smryData.dashboardItemType == "PRODUCT") {
        prdctSecHtml += makeProductSectionHtml(smryData, id);
      } else if (smryData.dashboardItemType == "TARGET") {
        targetSecHtml += makeTargetSectionHtml(smryData, id);
      }
    } else {
      docSecHtml += makeAttendanceSubgroupSectionHtml(smryData, id);
    }

  });

  return makesingleTileHtml(
    actySecHtml,
    docSecHtml,
    prdctSecHtml,
    targetSecHtml,
    tileName,
    empPid
  );
}

function makeActivitySingleSectionHtml(data, id) {
  console.log("makeActivitySectionHtml...");
  var activityHtml =
    '<div class="first"><p>' +
    data.label +
    '</p><p><b id="achieved' +
    id +
    '">' +
    data.achieved +
    "</b></p><p><b>" +
    data.scheduled +
    "</b></p></div>";
  console.log("ActivitySectionHtml", activityHtml);
  return activityHtml;
}

function makeDocumentsingleSectionHtml(data, id, empId) {
  console.log("doucmentloading ");
  let linkToPlanSummary =
    appContextPath +
    "/web/executive-task-executions?user-key-pid=" +
    empId +
    "&document-name=" +
    data.label;
  let circleClass = "";
  if (data.numberCircle == true) {
    circleClass = "number-circle";
  }
  var documentHtml =
    '<div class="first"><p><a href="' +
    linkToPlanSummary +
    '" style="color:inherit">' +
    data.label +
    '</a></p><p><b id="count' +
    id +
    '" class="' +
    circleClass +
    '">' +
    data.count +
    '</b></p><p><b id="amount' +
    id +
    '">' +
    Math.round(data.amount) +
    "</b></p></div>";

  return documentHtml;
}

function makesingleTileHtml(
  actySecHtml,
  docSecHtml,
  prdctSecHtml,
  targetSecHtml,
  tileName,
  empPid
) {
  let tileHtml = "";
  if (actySecHtml != "") {
    tileHtml = appendSingleActivitySectionHtml(actySecHtml, tileName, empPid);
  }
  if (docSecHtml != "") {
    tileHtml += appendSingleDocumentSectionHtml(docSecHtml);
  }
  if (prdctSecHtml != "") {
    tileHtml += appendProductSectionHtml(prdctSecHtml);
  }
  if (targetSecHtml != "") {
    tileHtml += appendTargetSectionHtml(targetSecHtml);
  }
  return tileHtml;
}

function appendSingleActivitySectionHtml(actySecHtml, tileName, empId) {
  let linkToPlanSummary =
    appContextPath + "/web/day-plans-execution-summary-user-wise?user=" + empId;
  let html =
    '<div class="tile-content tile-content_pad"><div class="table_"><div class="head">' +
    '<div class="first"><p class="text-center">Activities</p></div>' +
    '<div class="second"><div><p class="text-center">Achieved</p>';
  if (tileName == "day" || tileName == "week" || tileName == "month") {
    html += '<p class="text-center">Scheduled</p>';
  } else {
    html +=
      '<p class="text-center"><a href="' +
      linkToPlanSummary +
      '" style="color:inherit">Scheduled</a></p>';
  }
  html +=
    "</div></div></div>" +
    '<div class="clearfix"></div><div class="body">' +
    actySecHtml +
    "</div></div></div>";
  return html;
}

function appendSingleDocumentSectionHtml(docSecHtml) {
  if ($("#dbDashboardType").val() == "ACTIVITY") {
    return (
      '<div class="tile-content tile-content_pad"><div class="table_"><div class="head">' +
      '<div class="first"><p class="text-center">Vouchers</p></div>' +
      '<div class="second"><div><p class="text-center">Count</p>' +
      '<p class="text-center">Amount</p></div></div></div>' +
      '<div class="clearfix"></div><div class="body">' +
      docSecHtml +
      "</div></div></div>"
    );
  } else {
    // attendance subgroup
    return (
      '<div class="tile-content padding_btm tile-content_pad"><div class="table_"><div class="head">' +
      '<div class="first"><p class="text-center">Description</p></div>' +
      '<div class="second"><div><p class="text-center"></p>' +
      '<p class="text-center">Count</p></div></div></div>' +
      '<div class="clearfix"></div><div class="body">' +
      docSecHtml +
      "</div></div></div>"
    );
  }
}

function getTileColor(userwiseData, lastOrderTime) {
console.log("hi......................")
  let tileColor = "tile-red";
  if (
    userwiseData.attendaceBaseColorTile != null &&
    userwiseData.attendaceBaseColorTile == true
  ) {
    if (
      userwiseData.attendanceMarked == true &&
      userwiseData.punchOutStatus == false
    ) {
      tileColor = "tile-yellow";
    }
    if (
      userwiseData.attendanceMarked == true &&
      userwiseData.punchOutStatus == true
    ) {
      tileColor = "tile-green";
    }
  }
  if (lastOrderTime != null && userwiseData.attendaceBaseColorTile == false) {
    let delayTime = $("#txtDelayTime").val();
    let orderDate = new Date(
      formatDate(lastOrderTime, "MMM DD YYYY, h:mm:ss a")
    );
    let currentDate = new Date();
    let seconds = (currentDate.getTime() - orderDate.getTime()) / 1000;
    let minute = seconds / 60;
    if (minute < delayTime) {
      tileColor = "tile-green";
    } else if (minute > delayTime * 2) {
      tileColor = "tile-red";
    } else if (minute > delayTime) {
      tileColor = "tile-yellow";
    }
  }
  return tileColor;
}

function getAttendanceThumbsHtml(userData) {
  let attendance, attendanceTitle;
  console.log("attendence present ");
  if (userData.attendanceStatus == "true") {
    attendance = "up";
    attendanceTitle = "present";
  } else if (userData.attendanceStatus == "False") {
    attendance = "down";
    attendanceTitle = "absent";
  }
  return (
    "<a href='/web/attendance-report/all?empPid=" +
    userData.employeePid +
    "'><i id='attThumb" +
    userData.userPid +
    "' title='" +
    attendanceTitle +
    "' class='entypo-thumbs-" +
    attendance +
    "'><span style='margin-left:20%;'>" +
    formatDate(userData.plannedDate, "HH:mm") +
    "</span></i></a>"
  );
}

function getUserNotification(userPid, notificationCount) {
  let dateAr = $("#txtDate").val().split("-");
  let newDate = dateAr[2] + "-" + dateAr[1] + "-" + dateAr[0];
  let skipped =
    "<a href='/web/day-plan-execution-report?pid=" +
    userPid +
    "&date=" +
    newDate +
    "' id='userNotification" +
    userPid +
    "'> <i class='entypo-attention'><span id='skippedCount" +
    userPid +
    "' class='error_status_number'>" +
    notificationCount +
    "</span></i></a>";
  let notSkipped =
    "<a href='/web/day-plan-execution-report?pid=" +
    userPid +
    "&date=" +
    newDate +
    "' id='userNotification" +
    userPid +
    "' style=\"display: none;\"> <i class='entypo-attention'><span id='skippedCount" +
    userPid +
    "' class='error_status_number'>" +
    notificationCount +
    "</span></i></a>";
  return notificationCount > 0 ? skipped : notSkipped;
}

function makeUserTileFooterHtml(userData) {
  let lastLocation = userData.lastLocation != null ? userData.lastLocation : "";

  if (lastLocation == "No Location" && userData.latitude != 0) {
    lastLocation =
      "<span class='btn btn-success'  id='" +
      userData.taskExecutionPid +
      "' onClick='getLocation(this)' >get location</span>";
  }
  console.log(lastLocation);
  var mockLocation = "";
  if (userData.mockLocationStatus) {
    mockLocation = "Mock Location Is Enabled";
  }
  let lastAccountLocation =
    userData.lastAccountLocation != null ? userData.lastAccountLocation : "";
  let html =
    "<div class='tile-footer'><div id='clock" +
    userData.userPid +
    "' style='zoom: 0.39; -moz-transform: scale(0.5);display:none;margin-left: 8%;'></div>" +
    "<div id='location" +
    userData.userPid +
    "' style='display:block;'>" +
    "<p id='lastLoc" +
    userData.userPid +
    "' class='address popover-default' data-toggle='popover' data-trigger='hover' data-placement='top' data-content=\"$('#lastLoc" +
    userData.userPid +
    "').text()\" data-original-title='Address'>";
  html +=
    lastLocation +
    "</p><a href='" +
    appContextPath +
    "/web/live-tracking?user-key-pid=" +
    userData.userPid +
    "' class='icon-container'><div class='icon' id='locIcon" +
    userData.userPid +
    "' >" +
    getLocationIcons(userData) +
    "</div></a>";
  html +=
    "<p class='address'><font id='lastAccLoc" +
    userData.userPid +
    "'>" +
    lastAccountLocation +
    "</font></p>";
  html +=
    "<p class='time'><font id='lastTime" +
    userData.userPid +
    "'>" +
    formatDate(userData.lastTime, "HH:mm") +
    "</font></p>" +
    "</div></div><br><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
    mockLocation +
    "</p></div></div>";
  return html;
}

function getLocation(obj) {
  var pid = $(obj).attr("id");
  $(obj).html("loading...");
  $.ajax({
    url: appContextPath + "/web/dashboard/users-summary/updateLocation/" + pid,
    method: "GET",
    success: function (data) {
      $(obj).html(data.location);
      $(obj).removeClass("btn-success");
      $(obj).removeClass("btn");
    },
    error: function (xhr, error) {
      onError(xhr, error);
    },
  });
}

function getLocationIcons(userData) {
  let images = "";
  if (userData.locationType != null) {
    if (userData.locationType == "GpsLocation") {
      images =
        '<img src="' +
        appContextPath +
        '/resources/assets/images/map/location.png" width="20px">';
    } else if (userData.locationType == "TowerLocation") {
      images =
        '<img src="' +
        appContextPath +
        '/resources/assets/images/map/tower.png" width="20px">';
    } else if (userData.locationType == "FlightMode") {
      images =
        '<img src="' +
        appContextPath +
        '/resources/assets/images/map/flight.png" width="20px">';
    }
    // check gsp is Off
    if (userData.isGpsOff && userData.locationType != "GpsLocation") {
      images +=
        ' <img src="' +
        appContextPath +
        '/resources/assets/images/map/gps-off.png" width="20px">';
    }
    // check Mobile Data is Off
    if (userData.isMobileDataOff) {
      images +=
        ' <img src="' +
        appContextPath +
        '/resources/assets/images/map/mobile-data-off.png" width="20px">';
    }
  }
  return images;
}

function updateTileWithActivity(activity) {
  console.log("updateTileWithActivity......");
  var isCurrentDate = false;
  if ($("#txtDate").val() == "") {
    isCurrentDate = true;
  } else {
    var tDate = formatDate(new Date(), "DD MMM YYYY");
    var sDate = formatDate($("#txtDate").val(), "DD MMM YYYY");
    if (sDate == tDate) {
      isCurrentDate = true;
    }
  }
  console.log("isCurrentDate : ", isCurrentDate);
  if (isCurrentDate) {
    console.log("istrue : ", isCurrentDate);
    // update dashboard
    updateDashboard(activity);

    // update last time
    updateLastTime(activity);

    if (activity.decrementSkipCount) {
      console.log("decrementing skip count................");
      decrementSkipCount(activity);
    }
  }
}

function updateDashboard(activity) {
  console.log("Live Loading............" + activity);
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
  $("#lastTime" + activity.userPid).html(
    formatDate(activity.lastTime, "HH:mm")
  );
  // update summary portion
  if (activity.dashboardItems != null) {
    updateActivityAndDocument(activity);
  }
  // flip clock
  // updateFlipClockTimer(activity);
}

function updateActivityAndDocument(activity) {
  $.each(activity.dashboardItems, function (index, dashboardItem) {
    if (dashboardItem.dashboardItemType == "ACTIVITY") {
      updateActivityAreaWithWebsocketData(dashboardItem, activity);
    } else if (dashboardItem.dashboardItemType == "DOCUMENT") {
      updateDocumentAreaWithWebsocketData(dashboardItem, activity);
    } else if (dashboardItem.dashboardItemType == "PRODUCT") {
      updateProductAreaWithWebsocketData(dashboardItem, activity);
    } else if (dashboardItem.dashboardItemType == "TARGET") {
      updateSalesTargetBlockAreaWithWebsocketData(dashboardItem, activity);
    }
  });
}

function updateLastTime(activity) {
  $.each(userDataList, function (index, userData) {
    if (userData.userPid == activity.userPid) {
      userData.lastTime = activity.lastTime;
      return;
    }
  });
}

function updateActivityAreaWithWebsocketData(dashboardItem, activity) {
  console.log("Achived count........................" + dashboardItem);
  // update day tile achieved - activity
  var activitiesDayAchieved = $(
    "#achievedday" + dashboardItem.dashboardItemPid
  ).html();
  activitiesDayAchieved = parseInt(activitiesDayAchieved) + 1;
  $("#achievedday" + dashboardItem.dashboardItemPid).html(
    activitiesDayAchieved
  );

  // update week tile achieved - activity
  var activitiesWeekAchieved = $(
    "#achievedweek" + dashboardItem.dashboardItemPid
  ).html();
  activitiesWeekAchieved = parseInt(activitiesWeekAchieved) + 1;
  $("#achievedweek" + dashboardItem.dashboardItemPid).html(
    activitiesWeekAchieved
  );

  // update month tile achieved - activity
  var activitiesMonthAchieved = $(
    "#achievedmonth" + dashboardItem.dashboardItemPid
  ).html();
  activitiesMonthAchieved = parseInt(activitiesMonthAchieved) + 1;
  $("#achievedmonth" + dashboardItem.dashboardItemPid).html(
    activitiesMonthAchieved
  );

  // update user tile achieved - activity
  var activitiesUserAchieved = $(
    "#achieved" + activity.userPid + dashboardItem.dashboardItemPid
  ).html();
  activitiesUserAchieved = parseInt(activitiesUserAchieved) + 1;
  $("#achieved" + activity.userPid + dashboardItem.dashboardItemPid).html(
    activitiesUserAchieved
  );
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
  var productDayVolume = $(
    "#volumeday" + dashboardItem.dashboardItemPid
  ).html();
  productDayVolume = parseInt(productDayVolume) + dashboardItem.volume;
  $("#volumeday" + dashboardItem.dashboardItemPid).html(
    Math.round(productDayVolume)
  );
  var productDayAmount = $(
    "#productAmountday" + dashboardItem.dashboardItemPid
  ).html();
  productDayAmount = parseInt(productDayAmount) + dashboardItem.amount;
  $("#productAmountday" + dashboardItem.dashboardItemPid).html(
    Math.round(productDayAmount)
  );

  var productWeekVolume = $(
    "#volumeweek" + dashboardItem.dashboardItemPid
  ).html();
  productWeekVolume = parseInt(productWeekVolume) + dashboardItem.volume;
  $("#volumeweek" + dashboardItem.dashboardItemPid).html(productWeekVolume);
  var productWeekAmount = $(
    "#productAmountweek" + dashboardItem.dashboardItemPid
  ).html();
  productWeekAmount = parseInt(productWeekAmount) + dashboardItem.amount;
  $("#productAmountweek" + dashboardItem.dashboardItemPid).html(
    Math.round(productWeekAmount)
  );

  var productMonthVolume = $(
    "#volumemonth" + dashboardItem.dashboardItemPid
  ).html();
  productMonthVolume = parseInt(productMonthVolume) + dashboardItem.volume;
  $("#volumemonth" + dashboardItem.dashboardItemPid).html(productMonthVolume);
  var productMonthAmount = $(
    "#productAmountmonth" + dashboardItem.dashboardItemPid
  ).html();
  productMonthAmount = parseInt(productMonthAmount) + dashboardItem.amount;
  $("#productAmountmonth" + dashboardItem.dashboardItemPid).html(
    Math.round(productMonthAmount)
  );

  // Update User Tiles
  var productUserVolume = $(
    "#volume" + activity.userPid + dashboardItem.dashboardItemPid
  ).html();
  productUserVolume = parseInt(productUserVolume) + dashboardItem.volume;
  $("#volume" + activity.userPid + dashboardItem.dashboardItemPid).html(
    productUserVolume
  );
  var productUserAmount = $(
    "#productAmount" + activity.userPid + dashboardItem.dashboardItemPid
  ).html();
  productUserAmount = parseInt(productUserAmount) + dashboardItem.amount;
  $("#productAmount" + activity.userPid + dashboardItem.dashboardItemPid).html(
    Math.round(productUserAmount)
  );

  if (dashboardItem.targetType == "VOLUME") {
    // Update Summary Tiles
    var targetDayVolume = $(
      "#tbAchievedday" + dashboardItem.dashboardItemPid
    ).html();
    targetDayVolume =
      parseInt(targetDayVolume) + dashboardItem.targetAchievedVolume;
    $("#tbAchievedday" + dashboardItem.dashboardItemPid).html(
      Math.round(targetDayVolume)
    );
    var targetWeekVolume = $(
      "#tbAchievedweek" + dashboardItem.dashboardItemPid
    ).html();
    targetWeekVolume =
      parseInt(targetWeekVolume) + dashboardItem.targetAchievedVolume;
    $("#tbAchievedweek" + dashboardItem.dashboardItemPid).html(
      targetWeekVolume
    );
    var targetMonthVolume = $(
      "#tbAchievedmonth" + dashboardItem.dashboardItemPid
    ).html();
    targetMonthVolume =
      parseInt(targetMonthVolume) + dashboardItem.targetAchievedVolume;
    $("#tbAchievedmonth" + dashboardItem.dashboardItemPid).html(
      targetMonthVolume
    );

    // Update User Tiles
    var targetUserVolume = $(
      "#tbAchieved" + activity.userPid + dashboardItem.dashboardItemPid
    ).html();
    targetUserVolume =
      parseInt(targetUserVolume) + dashboardItem.targetAchievedVolume;
    $("#tbAchieved" + activity.userPid + dashboardItem.dashboardItemPid).html(
      targetUserVolume
    );

    // Update Tiles target Label
    var load = 0;
    if (dashboardItem.targetAverageVolume > 0) {
      load = $("#tbLabel" + activity.userPid + dashboardItem.dashboardItemPid)
        .html()
        .replace(/[^0-9$.,]/g, "");
      load = Number(load.trim()) + dashboardItem.targetAverageVolume;
      load = Math.round(load * 100) / 100;
      $("#tbLabel" + activity.userPid + dashboardItem.dashboardItemPid).html(
        dashboardItem.label + " (" + load + ")"
      );

      load = $("#tbLabelday" + dashboardItem.dashboardItemPid)
        .html()
        .replace(/[^0-9$.,]/g, "");
      load = Number(load.trim()) + dashboardItem.targetAverageVolume;
      load = Math.round(load * 100) / 100;
      $("#tbLabelday" + dashboardItem.dashboardItemPid).html(
        dashboardItem.label + " (" + load + ")"
      );

      load = $("#tbLabelweek" + dashboardItem.dashboardItemPid)
        .html()
        .replace(/[^0-9$.,]/g, "");
      load = Number(load.trim()) + dashboardItem.targetAverageVolume;
      load = Math.round(load * 100) / 100;
      $("#tbLabelweek" + dashboardItem.dashboardItemPid).html(
        dashboardItem.label + " (" + load + ")"
      );

      load = $("#tbLabelmonth" + dashboardItem.dashboardItemPid)
        .html()
        .replace(/[^0-9$.,]/g, "");
      load = Number(load.trim()) + dashboardItem.targetAverageVolume;
      load = Math.round(load * 100) / 100;
      $("#tbLabelmonth" + dashboardItem.dashboardItemPid).html(
        dashboardItem.label + " (" + load + ")"
      );
    }
  } else {
    // Update Summary Tiles
    var targetDayAmount = $(
      "#tbAchievedday" + dashboardItem.dashboardItemPid
    ).html();
    targetDayAmount =
      parseInt(targetDayAmount) + dashboardItem.targetAchievedAmount;
    $("#tbAchievedday" + dashboardItem.dashboardItemPid).html(
      Math.round(targetDayAmount)
    );
    var targetWeekAmount = $(
      "#tbAchievedweek" + dashboardItem.dashboardItemPid
    ).html();
    targetWeekAmount =
      parseInt(targetWeekAmount) + dashboardItem.targetAchievedAmount;
    $("#tbAchievedweek" + dashboardItem.dashboardItemPid).html(
      Math.round(targetWeekAmount)
    );
    var targetMonthAmount = $(
      "#tbAchievedmonth" + dashboardItem.dashboardItemPid
    ).html();
    targetMonthAmount =
      parseInt(targetMonthAmount) + dashboardItem.targetAchievedAmount;
    $("#tbAchievedmonth" + dashboardItem.dashboardItemPid).html(
      Math.round(targetMonthAmount)
    );

    // Update User Tiles
    var targetUserAmount = $(
      "#tbAchieved" + activity.userPid + dashboardItem.dashboardItemPid
    ).html();
    targetUserAmount =
      parseInt(targetUserAmount) + dashboardItem.targetAchievedAmount;
    $("#tbAchieved" + activity.userPid + dashboardItem.dashboardItemPid).html(
      Math.round(targetUserAmount)
    );
    if (targetUserAmount > 0) {
      var load = 0;
      load =
        targetUserAmount /
        Number(
          $(
            "#tbPlanned" + activity.userPid + dashboardItem.dashboardItemPid
          ).html()
        );
      load = Math.round(load * 100) / 100;
      $("#tbLabel" + activity.userPid + dashboardItem.dashboardItemPid).html(
        dashboardItem.label + " (" + load + ")"
      );
    }
  }
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

function loadAttendanceOnMiddleBar() {
	console.log("loading attendance summary............");
	$.ajax({
		url : appContextPath + "/web/dashboard/attendance",
		type : "GET",
		data : {
			date : formatDate($('#txtDate').val(), 'YYYY-MM-DD'),
		},
		success : function(attendance) {
			$("#lblTotalUsers").html(attendance.totalUsers);
			$("#lblAttendedUsers").html(attendance.attendedUsers);
		},
		error : function(xhr, error) {
			console.log("<< Error in loading attendance summary >>");
		}
	});
}



function loadAttendanceHeaderData() {

	var activeDate = $('#txtDate').val();
	$
			.ajax({
				url : appContextPath + "/web/dashboard/summary/attendance",
				type : "GET",
				data : {
					date : formatDate($('#txtDate').val(), 'YYYY-MM-DD'),
				},
				success : function(dashboardData) {
					$("#divDaySummary").html(
							getSummaryHtml(dashboardData.daySummaryDatas,
									"day", ""));
					$("#divWeekSummary").html(
							getSummaryHtml(dashboardData.weekSummaryDatas,
									"week", ""));
					$("#divMonthSummary").html(
							getSummaryHtml(dashboardData.monthSummaryDatas,
									"month", ""));
				},
				error : function(xhr, error) {
					console
							.log("Error in load attendance header Summary..................");
				}
			});
}


function makeAttendanceSubgroupSectionHtml(data, id) {
	return '<div class="first"><p>' + data.label + '</p><p><b></b></p><p><b id="attsubgrpCount' + id + '">' + data.count
			+ '</b></p></div>';
}




// not using modifed for later use 
function createSingleTile(summaryDatas, tileName, empPid) {
  console.log("CreateSingleTileData.......");
  if (summaryDatas.length == 0) {
    return "<div class='tile-content'>Please configure dashboard items</div>";
  }
  let actySecHtml = "";
  let docSecHtml = "";
  let prdctSecHtml = "";
  let targetSecHtml = "";

  $.each(summaryDatas, function (index, smryData) {
    var id = tileName + smryData.dashboardItemPid;
    if ($("#dbDashboardType").val() == "ACTIVITY") {
      if (smryData.dashboardItemType == "ACTIVITY") {
        actySecHtml += makeActivitySectionHtml(smryData, id);
      } else if (smryData.dashboardItemType == "DOCUMENT") {
        docSecHtml += makeDocumentSectionHtml(smryData, id, empPid);
      }
      else if (smryData.dashboardItemType == "PRODUCT") {
         prdctSecHtml += makeProductSectionHtml(smryData, id);
      } else if (smryData.dashboardItemType == "TARGET") {
         targetSecHtml += makeTargetSectionHtml(smryData, id);
      }
    } else {
      // attendance subgroup
      docSecHtml += makeAttendanceSubgroupSectionHtml(smryData, id);
    }
  });
  return makeTileHtml(
    actySecHtml,
    docSecHtml,
    prdctSecHtml,
    targetSecHtml,
    tileName,
    empPid
  );
}

function makeActivitySectionHtml(data, id) {
  var activityHtml =
    '<div class="body-item"><p>' +
    data.label +
    '</p><p><b id="achieved' +
    id +
    '">' +
    data.achieved +
    "</b></p><p><b>" +
    data.scheduled +
    "</b></p></div>";
  console.log(
    data.label,
    " : ",
    data.achieved,
    " : ",
    data.achieved,
    " : ",
    data.scheduled
  );
  return activityHtml;
}

function makeDocumentSectionHtml(data, id, empId) {
  let linkToPlanSummary =
    appContextPath +
    "/web/executive-task-executions?user-key-pid=" +
    empId +
    "&document-name=" +
    data.label;

  let circleClass = "";

  if (data.numberCircle == true) {
    circleClass = "number-circle";
  }

  var documentHtml =
    '<div class="body-item"><p><a href="' +
    linkToPlanSummary +
    '" style="color:inherit">' +
    data.label +
    '</a></p><p><b id="count' +
    id +
    '" class="' +
    circleClass +
    '">' +
    data.count +
    '</b></p><p><b id="amount' +
    id +
    '">' +
    Math.round(data.amount) +
    "</b></p></div>";

  console.log(data.label, " : ", data.count, " : ", data.amount);
  return documentHtml;
}

function makeTileHtml(
  actySecHtml,
  docSecHtml,
  prdctSecHtml,
  targetSecHtml,
  tileName,
  empPid
) {
  let tileHtml = "";
  if (actySecHtml != "") {
    tileHtml = appendActivitySectionHtml(actySecHtml, tileName, empPid);
  }
  if (docSecHtml != "") {
    tileHtml += appendDocumentSectionHtml(docSecHtml);
  }
  if (prdctSecHtml != "") {
    tileHtml += appendProductSectionHtml(prdctSecHtml);
  }
  if (targetSecHtml != "") {
    tileHtml += appendTargetSectionHtml(targetSecHtml);
  }
  return tileHtml;
}

function appendActivitySectionHtml(actySecHtml, tileName, empId) {
  let linkToPlanSummary =
    appContextPath + "/web/day-plans-execution-summary-user-wise?user=" + empId;
  let html =
    '<div class="tile-card-data"><div class="tile-card-item-header"><div class="card-header-items">' +
    '<div class="header-item"><b><p class="text-center">Activities</p></b<b><p class="text-center">Achieved</p></b>';
  if (tileName == "day" || tileName == "week" || tileName == "month") {
    html += '<b><p class="text-center">Scheduled</p></b>';
  } else {
    html +=
      '<b><p class="text-center"><a href="' +
      linkToPlanSummary +
      '" style="color:inherit">Scheduled</a></p></b>';
  }
  html +=
    "</div></div></div>" +
    '<div class="tile-card-item-body">' +
    actySecHtml +
    "</div></div></div>";
  return html;
}

function appendDocumentSectionHtml(docSecHtml) {
  if ($("#dbDashboardType").val() == "ACTIVITY") {
    return (
      '<div class="tile-card-data"><div class="tile-card-item-header"><div class="card-header-items">' +
      '<div class="header-item"><b><p class="text-center">Vouchers</p></b><b><p class="text-center">Count</p></b><b><p class="text-center">Amount</p></b></div>' +
      '</div></div><div class="tile-card-item-body">' +
      docSecHtml +
      "</div></div></div>"
    );
  } else {
    return (
      '<div class="tile-content padding_btm tile-content_pad"><div class="table_"><div class="head">' +
      '<div class="first"><p class="text-center">Description</p></div>' +
      '<div class="second"><div><p class="text-center"></p>' +
      '<p class="text-center">Count</p></div></div></div>' +
      '<div class="clearfix"></div><div class="body">' +
      docSecHtml +
      "</div></div></div>"
    );
  }
}