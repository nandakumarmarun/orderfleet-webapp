<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Dashboard</title>

<script type="text/javascript">
	var companyId = "${companyId}";
	var enableWebsocket = "${enableWebsocket}";
	var dbItemGroups;
	if('${dbItemGroups}'){
		dbItemGroups = JSON.parse('${dbItemGroups}');
	} else {
		dbItemGroups = [];
	}
</script>
<spring:url value="/resources/assets/css/flipclock.css"
	var="flipclockCss"></spring:url>
<link href="${flipclockCss}" rel="stylesheet">

<style type="text/css">
.text {
	display: block;
	width: 100px;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}

/**THE SAME CSS IS USED IN ALL 3 DEMOS**/
/**gallery margins**/
.zoom {
	-webkit-transition: all 0.35s ease-in-out;
	-moz-transition: all 0.35s ease-in-out;
	transition: all 0.35s ease-in-out;
}

.zoom:hover, .zoom:active, .zoom:focus {
	/**adjust scale to desired size, add browser prefixes**/
	-ms-transform: scale(10);
	-moz-transform: scale(10);
	-webkit-transform: scale(10);
	-o-transform: scale(10);
	transform: scale(10);
	position: relative;
	z-index: 100;
}

.number-circle {
	border-radius: 50%;
	behavior: url(PIE.htc); /* remove if you don't care about IE8 */
	padding: 5px;
	background: #fff;
	border: 1px solid #666;
	color: #666;
	text-align: center;
	font: 14px Arial, sans-serif;
}
</style>
</head>

<!-- styles for the dashboard -->
<spring:url value="/resources/assets/css/dashboard-custom.css"
	var="dashboardCss"></spring:url>
<link href="${dashboardCss}" rel="stylesheet">

<!-- Bar graph -->
<spring:url
	value="/resources/assets/plugin/britecharts/css/common.min.css"
	var="barCommonCss"></spring:url>
<link href="${barCommonCss}" rel="stylesheet">
<spring:url value="/resources/assets/plugin/britecharts/css/bar.min.css"
	var="barMinCss"></spring:url>
<link href="${barMinCss}" rel="stylesheet">

<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<div>
				<div class="row">
					<div class="col-sm-5"></div>
					<div class="col-sm-3">
						<div>
							<select id="dbDashboardType" class="form-control">
								<option value="ACTIVITY">Activity</option>
								<option value="ATTENDANCE">Attendance</option>
							</select>
						</div>
					</div>
					<div class="col-sm-4">
						<div class="form-group pull-right date_">
							<div class="input-group">
								<input type="date" class="form-control" id="txtDate">
								<div class="input-group-addon">
									<a href="#"><i class="entypo-calendar"></i></a>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row" style="color: white;">
					<div class="col-sm-6 col-md-3 col-lg-3 col-xs-12">
						<strong id="chartLabel" style="color: black;"></strong>
						<div id="chartContainer">
							<img src="/resources/assets/images/menu-ajax-loader.gif"
								style="display: block; margin: auto;">
						</div>
					</div>
					<div class="col-sm-6 col-md-3 col-lg-3 col-xs-12">
						<div class="tile-block tile-aqua" style="height: auto;">
							<div class="tile-header font-size-14">
								<i class="entypo-chart-line"></i> <a
									href="<%=request.getContextPath()%>/web/executive-task-executions?filterBy=TODAY"
									class="text-center"><label id="lblDate"
									style="cursor: pointer;"></label></a>
							</div>
							<div id="divDaySummary">
								<img src="/resources/assets/images/menu-ajax-loader.gif"
									style="display: block; margin: auto;">
							</div>
						</div>
					</div>

					<div class="col-sm-6 col-md-3 col-lg-3 col-xs-12">
						<div class="tile-block tile-green-top">
							<div class="tile-header font-size-14">
								<i class="entypo-chart-line"></i> <a
									href="<%=request.getContextPath()%>/web/executive-task-executions?filterBy=WTD"
									class="text-center"> <label id="fromDateWeek"
									style="cursor: pointer;"></label> To <label id="toDateWeek"
									style="cursor: pointer;"></label></a>
							</div>
							<div id="divWeekSummary">
								<img src="/resources/assets/images/menu-ajax-loader.gif"
									style="display: block; margin: auto;">
							</div>
						</div>
					</div>

					<div class="col-sm-6 col-md-3 col-lg-3 col-xs-12">
						<div class="tile-block tile-red-top">
							<div class="tile-header font-size-14">
								<i class="entypo-chart-line"></i> <a
									href="<%=request.getContextPath()%>/web/executive-task-executions?filterBy=MTD"
									class="text-center"> <label id="fromDateMonth"
									style="cursor: pointer;"></label> To <label id="toDateMonth"
									style="cursor: pointer;"></label></a>
							</div>
							<div id="divMonthSummary">
								<img src="/resources/assets/images/menu-ajax-loader.gif"
									style="display: block; margin: auto;">
							</div>
						</div>
					</div>
				</div>

				<div id="divAttendancePanel">
					<div class="row">
						<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
							<div class="panel panel-primary" data-collapsed="0">
								<div class="panel-heading"
									style="background-color: rgb(231, 231, 231);">
									<div class="panel-title">
										<strong>Live streaming - Salesman wise performance</strong>
										&nbsp;&nbsp;|&nbsp;&nbsp; <strong><b>Attendance -
												<label id="lblAttendedUsers">0</label>/<label
												id="lblTotalUsers">0</label>
										</b></strong>
									</div>
									<div class="panel-options" style="padding: 10px 15px;">
										<button type="button"
											onclick="$('#modalDelayTime').modal('show');"
											class="btn btn-blue btn-xs tooltip-info">Set Delay
											Time</button>
										<a href="#" data-rel="collapse"><i
											class="entypo-down-open" style="color: white;"></i></a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div id="divUserWiseSummary">
					<div class="row">
						<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
							<div class="panel panel-primary" data-collapsed="0">
								<div id="divUsersSummary" class="panel-body">
									<!-- users tile container -->
									<div class="row" id="divUsers">
										<img src="/resources/assets/images/menu-ajax-loader.gif"
											style="display: block; margin: auto;">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!--the inner contents Ends here-->
				<div class="clearfix"></div>

				<!-- footer -->
				<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


			</div>
		</div>
		<!-- Model Container -->
		<div class="modal fade container" id="modalDelayTime">
			<!-- model Dialog -->
			<div class="modal-dialog" style="width: 50%">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">
						<div class="form-group">
							<label>Set delay time in minute</label> <input type="text"
								id="txtDelayTime" class="form-control" placeholder="Time" />
						</div>
					</div>
					<div class="modal-footer">
						<input id="btnDelayTime" class="btn btn-success" type="button"
							value="Update" />
						<button class="btn" data-dismiss="modal">Close</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<!-- web socket -->
	<spring:url value="/resources/assets/js/q.js" var="QJs"></spring:url>
	<script type="text/javascript" src="${QJs}"></script>

	<spring:url value="/resources/assets/js/websocket/sockjs-0.3.4.js"
		var="StockJs"></spring:url>
	<script type="text/javascript" src="${StockJs}"></script>

	<spring:url value="/resources/assets/js/websocket/stomp.js"
		var="StompJs"></spring:url>
	<script type="text/javascript" src="${StompJs}"></script>

	<spring:url value="/resources/assets/plugin/d3/d3.min.js" var="d3Js"></spring:url>
	<script type="text/javascript" src="${d3Js}"></script>
	<spring:url value="/resources/assets/plugin/britecharts/js/bar.min.js"
		var="barMinJs"></spring:url>
	<script type="text/javascript" src="${barMinJs}"></script>
	<spring:url
		value="/resources/assets/plugin/britecharts/js/miniTooltip.min.js"
		var="miniTooltipJs"></spring:url>
	<script type="text/javascript" src="${miniTooltipJs}"></script>

	<spring:url value="/resources/assets/plugin/d3/d3-funnel.min.js"
		var="d3FunnelJs"></spring:url>
	<script type="text/javascript" src="${d3FunnelJs}"></script>

	<!-- custom -->
	<spring:url value="/resources/app/dashboard.js" var="dashboardJs"></spring:url>
	<script type="text/javascript" src="${dashboardJs}"></script>

	<!-- flipclock js countdown timer-->
	<spring:url value="/resources/assets/js/flipclock.min.js"
		var="flipclockJs"></spring:url>
	<script type="text/javascript" src="${flipclockJs}"></script>

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							if (window
									.matchMedia('(max-width: 990px) and (min-width: 768px)').matches) {
								$(".sidebar-collapse-icon").click(
										function() {
											$(".tile-content .table_ .head")
													.css("width", "100%");
											$(".tile-content .table_ .body")
													.css("width", "100%");

										});
							}
						});
	</script>
</body>
</html>