<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Price Trends Chart</title>

<!-- Include the plugin's CSS and JS for multi-select dropdown: -->
<!-- https://github.com/davidstutz/bootstrap-multiselect -->
<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Price Trends Chart</h2>

			<div class="row">
				<div class="col-sm-5">
					<select id="dbProductGroups" class="form-control">
						<option value="-1">Select a Group</option>
						<c:forEach items="${productGroups}" var="pg">
							<option value="${pg.pid}">${pg.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-4">
					<select id="dbCompetitorProfile" name="competitorProfilePid" class="form-control">
						<option value="-1">Select a Competitor</option>
						<c:forEach items="${activatedCompetitorProfiles}" var="competitorProfile">
							<option value="${competitorProfile.pid}">${competitorProfile.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-3">
					<select id="dbPrices" class="form-control">
						<option value="-1">All Price Level</option>
						<c:forEach items="${prices}" var="price">
							<option value="${price}">${price}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="clearfix"></div>
			<br />
			<div class="row">
				<div class="col-sm-3">
					<div>
						<select class="form-control" id="dbDate">
							<option value="THIS_MONTH">This Month</option>
							<option value="PREVIOUS_MONTH">Previous Month</option>
							<option value="CUSTOM">CUSTOM</option>
						</select>
					</div>
				</div>

				<div class="col-sm-3">
					<div class="input-group">
						<input type="text" class="form-control" id="txtFromDate"
							style="background-color: #fff;" placeholder="Select From Date"
							readonly="readonly" />
						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>

				<div class="col-sm-3">
					<div class="input-group">
						<input type="text" class="form-control" id="txtToDate"
							style="background-color: #fff;" placeholder="Select To Date"
							readonly="readonly" />
						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>
				<div class="col-sm-1">
					<button type="button" class="btn btn-info" id="btnApply">Apply</button>
				</div>
			</div>
			<div id="divCharts" class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/competitor-price-trends"
				var="urlCompetitorPriceTrends"></spring:url>

		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<!-- Graph -->
	<spring:url value="/resources/assets/js/Chart.min.js" var="chartJs"></spring:url>
	<script type="text/javascript" src="${chartJs}"></script>

	<spring:url value="/resources/app/user-price-trend-chart.js"
		var="userPriceTrendsChartJs"></spring:url>
	<script type="text/javascript" src="${userPriceTrendsChartJs}"></script>
	
		<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>
</body>
</html>