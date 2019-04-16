<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<title>SalesNrich | Activity Hour Reporting</title>

<style type="text/css">
td, th, table, tr {
	border: 1px solid black !important;
}

#divProductCategory td, #divProductCategory th {
	text-align: center !important;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>

		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Activity Hour Reporting</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="col-sm-3">
				<select id="dbProductGroup"
					onchange="ActivityHourReporting.loadData()" class="form-control">
					<option value="-1">Select Price Trend Product Group</option>
					<c:forEach items="${priceTrendProductGroups}" var="productGroup">
						<option value="${productGroup.pid}">${productGroup.name}</option>
					</c:forEach>
				</select>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div class="clearfix"></div>
			<div class="row col-xs-12" id="divProductGroupName"
				style="font-size: medium; text-align: center; font-weight: bolder; color: black;"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row col-xs-12" id="divProductCategory"
				style="min-height: 300px; overflow: auto;"></div>
			<div class="clearfix"></div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/activity-hour-reporting.js"
		var="activityHourReportingJs"></spring:url>
	<script type="text/javascript" src="${activityHourReportingJs}"></script>


</body>
</html>