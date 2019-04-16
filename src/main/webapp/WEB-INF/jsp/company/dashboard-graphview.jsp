<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Dashboard</title>
<script type="text/javascript">
	var companyId = "${companyId}";
	var territoryId = "${territory.id}";
</script>
</head>

<!-- styles for the dashboard -->

<!-- chart -->
<spring:url value="/resources/assets/css/morris.css"
	var="morrisCss"></spring:url>
<link href="${morrisCss}" rel="stylesheet">

<!-- custom -->
<spring:url value="/resources/assets/css/dashboard-custom.css"
	var="dashboardCss"></spring:url>
<link href="${dashboardCss}" rel="stylesheet">

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
						</div>
					</div>
					<div class="col-sm-3">
						<div style="display: none;">
							<select id="dbDashboardType" class="form-control">
								<option value="TW">Territory Wise</option>
								<option value="USR">User Wise</option>
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
				<div id="divHeader" class="row">
					<img src="/resources/assets/images/menu-ajax-loader.gif"
										style="display: block; margin: auto;">
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
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<!-- Graph -->
	<spring:url value="/resources/assets/js/raphael-min.js" var="raphaelJs"></spring:url>
	<script type="text/javascript" src="${raphaelJs}"></script>
	<spring:url value="/resources/assets/js/morris.min.js" var="morissJs"></spring:url>
	<script type="text/javascript" src="${morissJs}"></script>
	
	<!-- custom -->
	<spring:url value="/resources/app/dashboard-graphview.js"
		var="dashboardGraphviewJs"></spring:url>
	<script type="text/javascript" src="${dashboardGraphviewJs}"></script>

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