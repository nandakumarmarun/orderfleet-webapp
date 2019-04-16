<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Competitor Price Trends</title>
<!-- Include the plugin's CSS and JS for multi-select dropdown: -->
<!-- https://github.com/davidstutz/bootstrap-multiselect -->
<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<spring:url value="/resources/assets/css/MonthPicker.css"
	var="monthPickerCss"></spring:url>
<link href="${monthPickerCss}" rel="stylesheet" media="all"
	type="text/css">
<style type="text/css">
.error {
	color: red;
}

td, th, table, tr {
	border: 1px solid black !important;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Competitor Price Trends</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-sm-6">
					<select id="dbProductGroup" class="form-control">
						<option value="-1">Select Product Group</option>
						<c:forEach items="${productGroups}" var="productGroup">
							<option value="${productGroup.pid}">${productGroup.name}</option>
						</c:forEach>
					</select>

				</div>
				<div class="col-sm-6">
					<select id="dbUser" class="form-control">
						<option value="-1">Select User</option>
						<c:forEach items="${users}" var="user">
							<option value="${user.pid}">${user.firstName}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<br />
			<div class="row">
				<div class="col-sm-3"">
					<select class="form-control" id="dbDateSearch"
						onchange="CompitatorPriceTrend.showDatePicker()">
						<option value="DATE">Select Date</option>
						<option value="THISWEEK">This Week</option>
						<option value="THISMONTH" selected="selected">This Month</option>
						<option value="LASTMONTH">Last Month</option>
						<option value="CUSTOMDATE">Custom Date</option>
						<option value="CUSTOMMONTH">Custom Month</option>
					</select>
				</div>
				<div class="col-sm-3 hide custom_date1">
					<div class="input-group">
						<input type="text" class="form-control" id="txtFromDate"
							placeholder="Select From Date" style="background-color: #fff;"
							readonly="readonly" />
						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>
				<div class="col-sm-3 hide custom_date2">
					<div class="input-group">
						<input type="text" class="form-control" id="txtToDate"
							placeholder="Select To Date" style="background-color: #fff;"
							readonly="readonly" />
						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>
				<div class=" col-sm-3 hide custom_month1">
					<div class="input-group">
						<input type="text" class="form-control" id="txtFromMonth"
							placeholder="Select From Month" style="background-color: #fff;"
							readonly="readonly" />

						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>
				<div class="col-sm-3 hide custom_month2">
					<div class="input-group">
						<input type="text" class="form-control" id="txtToMonth"
							placeholder="Select To Month" style="background-color: #fff;"
							readonly="readonly" />
						<div class="input-group-addon">
							<a href="#"><i class="entypo-calendar"></i></a>
						</div>
					</div>
				</div>
				<div class="col-sm-1">
					<button id="btnApply" type="button" class="btn btn-info"
						onclick="CompitatorPriceTrend.filter()">Apply</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<br>
			<div id="cptReport" class="row col-xs-12">
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/competitor-price-trends"
				var="urlCompetitorPriceTrends"></spring:url>

		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/MonthPicker.js"
		var="monthPicker"></spring:url>
	<script type="text/javascript" src="${monthPicker}"></script>

	<spring:url value="/resources/app/competitor-price-trend.js"
		var="compitatorPriceTrendJs"></spring:url>
	<script type="text/javascript" src="${compitatorPriceTrendJs}"></script>

</body>
</html>