<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Dashboard</title>
<script type="text/javascript">
	var companyId = "${companyId}";
	var territoryId = "${territory.id}";
	var dashboardType = "${dashboardType}";
</script>
</head>

<!-- styles for the dashboard -->
<spring:url value="/resources/assets/css/dashboard-custom.css"
	var="dashboardCss"></spring:url>
<link href="${dashboardCss}" rel="stylesheet">

<spring:url
	value="/resources/assets/plugin/jstree/themes/default/style.min.css"
	var="jstreeCss"></spring:url>
<link href="${jstreeCss}" rel="stylesheet">
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<div>
				<div class="row">
					<div class="col-sm-6">
						<span id="lnkRoot" data-root-territory-id="${territory.id}"
							data-root-territory-name="${territory.name}"
							class="glyphicon glyphicon-home" style="cursor: pointer;"></span>
						<span id="lnkPrevious" data-prev-territory-id="${territory.id}"
							data-prev-territory-name="${territory.name}"
							class="glyphicon glyphicon-step-backward"
							style="cursor: pointer;"></span>
						<div class="btn-group">
							<button type="button" class="btn btn-white dropdown-toggle"
								data-toggle="dropdown" aria-expanded="false">
								<b id="headerName">${territory.name}</b>&nbsp;&nbsp;&nbsp;<b
									class="caret"></b>
							</button>
							<ul id="sbTerritory" class="dropdown-menu" role="menu">
							</ul>
							<img width="30" height="30" src="/resources/assets/images/hierarchy-icon.png" data-toggle="modal" data-target="#locationsModal" style="cursor: pointer;" />
						</div>
					</div>
					<div class="col-sm-3">
						<div>
							<select id="dbDashboardType" class="form-control">
								<option value="TW">Territory Wise</option>
								<option value="USR">User Wise</option>
								<option value="ACW">Account Wise</option>
							</select>
						</div>
					</div>
					<div class="col-sm-3">
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
					<div class="col-sm-6 col-md-5 col-lg-4 col-xs-12">
						<div class="tile-block tile-aqua" style="height: auto;">
							<div class="tile-header font-size-14">
								<i class="entypo-chart-line"></i> <a href="#"
									class="text-center"><label id="lblDate"></label></a>
							</div>
							<div id="divDaySummary">
								<img src="/resources/assets/images/menu-ajax-loader.gif"
									style="display: block; margin: auto;">
							</div>
						</div>
					</div>

					<div class="col-sm-6 col-md-5 col-lg-4 col-xs-12">
						<div class="tile-block tile-green-top">
							<div class="tile-header font-size-14">
								<i class="entypo-chart-line"></i> <a href="#"
									class="text-center"> <label id="fromDateWeek"></label> To <label
									id="toDateWeek"></label></a>
							</div>
							<div id="divWeekSummary">
								<img src="/resources/assets/images/menu-ajax-loader.gif"
									style="display: block; margin: auto;">
							</div>
						</div>
					</div>

					<div class="col-sm-6 col-md-5 col-lg-4 col-xs-12">
						<div class="tile-block tile-red-top">
							<div class="tile-header font-size-14">
								<i class="entypo-chart-line"></i> <a href="#"
									class="text-center"> <label id="fromDateMonth">18
										Dec 2016</label> To <label id="toDateMonth">22 Dec 2016</label></a>
							</div>
							<div id="divMonthSummary">
								<img src="/resources/assets/images/menu-ajax-loader.gif"
									style="display: block; margin: auto;">
							</div>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
						<div class="panel panel-primary" data-collapsed="0">
							<div id="divLiveStreamPanel" class="panel-heading"
								style="background-color: rgb(231, 231, 231);"></div>
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
						<input class="btn btn-success" type="button" value="Update"
							onclick="DashboardTerritory.updateDelayTime()" />
						<button class="btn" data-dismiss="modal">Close</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>

		<!-- location hierarchy modal -->
		<div class="modal fade container" id="locationsModal">
			<!-- model Dialog -->
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title">Assign Locations</h4>
					</div>
					<div class="modal-body" style="overflow: auto; height: 500px">
						<div class="form-group">
							<div id="tree-container"></div>
						</div>
						<label class="error-msg" style="color: red;"></label>
					</div>
					<div class="modal-footer">
						<input class="btn btn-success" type="button" id="btnSaveLocation"
							value="Save" />
						<button class="btn" data-dismiss="modal">Cancel</button>
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

	<spring:url value="/resources/assets/plugin/jstree/jstree.min.js"
		var="jstreeJS"></spring:url>
	<!-- custom -->
	<spring:url value="/resources/app/dashboard-territorywise.js"
		var="dashboardTerritoryWiseJs"></spring:url>
	<script type="text/javascript" src="${jstreeJS}"></script>
	<script type="text/javascript" src="${dashboardTerritoryWiseJs}"></script>

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