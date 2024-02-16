var scripts = [ '/resources/app/dashboard-load.js',
        '/resources/app/dashboard/dashboard-attendance.js'
//		'/resources/app/dashboard/chart.js',
//		'/resources/app/dashboard/header-summary.js',
//		'/resources/app/dashboard/users-summary.js',
//		'/resources/app/dashboard/dashboard-websocket.js',
//		'/resources/app/dashboard/dashboard-activity.js',

];

$.getMultiScripts(scripts).done(function() {
	'use strict';
	let dashboardContextPath = location.protocol + '//' + location.host + location.pathname;
	
	// onExit unsubscribe websocket
	window.onbeforeunload = unsubscribe;

	var stompClient = null;
	var subscriber = null;
	var listener = Q.defer();
	var connected = Q.defer();
	var alreadyConnectedOnce = false;
	$(document).ready(function() {
		$('#txtDate').val(new Date().toISOString().substring(0, 10));
		init();
		console.log("loading dashbord data")
		loadDashboardData();
	});
	
	function init() {
		if (enableWebsocket == "true") {
			initWebSocket();
		}

		$("#dbDashboardType").change(function() {
		console.log("loading init...")
			loadDashboardData();
		});
		$("#txtDate").change(function() {
		 console.log("loading txtDate...")
			loadDashboardData();
			setHeaderTilesDate();
		});
		$("#btnDelayTime").click(function() {
		  console.log("loading txtDate...")
			updateDelayTime();
		});
		
		setHeaderTilesDate();
	}
	
	function initWebSocket() {
		// web socket
		connect();
		subscribe();
		receive().then(null, null, function(activity) {
			console.log("new activity received...........");
			updateTileWithActivity(activity);
		});
//		 get and set delay
		 getDelayTime();
	}
	

	
	function setHeaderTilesDate() {
		let selDate = $('#txtDate').val();
		// set date
		let selDateFormatted = formatDate(selDate, 'DD MMM YYYY');
		$("#lblDate").html(selDateFormatted);
		$("#fromDateWeek").html(moment(selDate).startOf('week').format('DD MMM YYYY'));
		$("#toDateWeek").html(selDateFormatted);
		$("#fromDateMonth").html(moment(selDate).startOf('month').format('DD MMM YYYY'));
		$("#toDateMonth").html(selDateFormatted);
	}
	
	

	/** *************** web socket********** */
	function connect() {
		// building absolute path so that websocket doesnt fail when deploying
		// with a context path
		var loc = window.location;
		var url = loc.protocol + '//' + loc.host + '/websocket/tracker';
		var socket = new SockJS(url);
		stompClient = Stomp.over(socket);
		var headers = {};
		
		stompClient.connect(headers, function() {
			console.log("connected....")
			connected.resolve('success');

			if (!alreadyConnectedOnce) {
				alreadyConnectedOnce = true;
			}
		});
	}

	function disconnect() {
		if (stompClient !== null) {
			stompClient.disconnect();
			stompClient = null;
		}
	}

	function receive() {
		return listener.promise;
	}

	function subscribe() {
		connected.promise.then(function() {

			subscriber = stompClient.subscribe('/live-tracking/dashboard-view/'+ companyId, function(data) {
				listener.notify(JSON.parse(data.body));
			});

			subscriber = stompClient.subscribe('/live-tracking/attendance/'
					+ companyId, function(data) {
				let attendance = JSON.parse(data.body);
				console.log("attendance received..................");
				// change total attendance
				let attendedUsers = $("#lblAttendedUsers").text();
				attendedUsers = 1 + parseInt(attendedUsers);
				$("#lblAttendedUsers").text(attendedUsers);
				
				// change thumb to up
				$("#attThumb" + attendance.userPid).attr("class",
						"entypo-thumbs-up");
				// change time under thumb
				$("#attThumb" + attendance.userPid + " span").html(formatDate(attendance.time, 'HH:mm'));
				
				// change remarks
				let remarks = attendance.remarks == "" ? " "
						: attendance.remarks;
				$("#attRemark" + attendance.userPid).text(remarks);

				// change subgroup
				let subGroupCode = attendance.attendanceSubGroupCode == ""
						|| attendance.attendanceSubGroupCode == null ? " "
						: attendance.attendanceSubGroupCode;

				// update main header tile
				// Day tile
				let subgroupCountDay = $(
						"#attsubgrpCountday" + attendance.dashboardItemId)
						.html();
				subgroupCountDay = parseInt(subgroupCountDay) + 1;
				$("#attsubgrpCountday" + attendance.dashboardItemId).html(
						subgroupCountDay);
				// Week tile
				let subgroupCountWeek = $(
						"#attsubgrpCountweek" + attendance.dashboardItemId)
						.html();
				subgroupCountWeek = parseInt(subgroupCountWeek) + 1;
				$("#attsubgrpCountweek" + attendance.dashboardItemId).html(
						subgroupCountWeek);
				// Day tile
				let subgroupCountMonth = $("#attsubgrpCountmonth" + attendance.dashboardItemId).html();
				subgroupCountMonth = parseInt(subgroupCountMonth) + 1;
				$("#attsubgrpCountmonth" + attendance.dashboardItemId).html(
						subgroupCountMonth);

				// user tile
				$("#attSubgroup" + attendance.userPid).text(subGroupCode);
			});

			subscriber = stompClient.subscribe('/live-tracking/user-notification/' + companyId, function(data) {
				         var userPid = data.body;
						// change user notification icon
						$("#userNotification" + userPid).show();
						var skipped = $("#skippedCount" + userPid).text();
						skipped = 1 + parseInt(skipped);
						$("#skippedCount" + userPid).text(skipped);
					});

		}, null, null);
	}


	function unsubscribe() {
		if (subscriber !== null) {
			subscriber.unsubscribe();
		}
		listener = Q.defer();
	}
	/** ********** web socket end******** */
	

	 function updateDelayTime() {
	 	$.ajax({
	 		url : dashboardContextPath + "/update-delay-time-denormalised",
	 		type : "POST",
	 		data : {
	 			delayTime : $('#txtDelayTime').val()
	 		},
	 		success : function(status) {
	 			$('#modalDelayTime').modal('hide');
	 		}
	 	});
	 }

	 function getDelayTime() {
	 	$.ajax({
	 		url : dashboardContextPath + "/delay-time-denormalised",
	 		type : "GET",
	 		success : function(delayTime) {
	 			if (delayTime == 0) {
	 				delayTime = 30;
	 			}
	 			$('#txtDelayTime').val(delayTime);
	 		}
	 	});
	 }


});