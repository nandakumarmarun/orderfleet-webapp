<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Location Wise Target Vs Achieved Report</title>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/MonthPicker.css"
	var="monthPickerCss"></spring:url>
<link href="${monthPickerCss}" rel="stylesheet" media="all"
	type="text/css">


<style type="text/css">
.table-bordered>thead>tr>th, .table-bordered>tbody>tr>th,
	.table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td,
	.table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
	font-size: 12px;
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
			<h2>Location Wise Target Vs Achieved Report</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbProduct" name="productPid" class="form-control">
									<option value="no">Select Product Group</option>
									<c:forEach items="${products}" var="product">
										<option value="${product.pid}">${product.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2 ">
								<div class="input-group">
									<input type="text" class="form-control" id="txtFromMonth"
										placeholder="From Month" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2">
								<div class="input-group">
									<input type="text" class="form-control" id="txtToMonth"
										placeholder="To Month" style="background-color: #fff;"
										readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button id="applyBtn" type="button" class="btn btn-info">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<hr />
			<div style="overflow: auto; min-height: 300px;">
				<table style="display: none;"
					class="collaptable table table-striped table-bordered table-responsive"
					id="tblLocationWiseTargetAchievedReport">
					<thead>
						<tr>
							<th rowspan="2"
								style="text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: bold; vertical-align: middle;">LOCATION</th>
							<th colspan="3" id="th_target"
								style="text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: bold; vertical-align: middle;">TARGET</th>
							<th rowspan="2"
								style="text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: bold; vertical-align: middle;">TOTAL</th>
							<th colspan="3" id="th_achived"
								style="text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: bold; vertical-align: middle;">ACHIEVED</th>
							<th rowspan="2"
								style="text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: bold; vertical-align: middle;">TOTAL</th>
							<th rowspan="2"
								style="text-align: center; color: white; font-weight: bold; background-color: rgb(48, 54, 65); vertical-align: middle;">ACHIEVED%
							</th>
						</tr>
						<tr id="txtRowHeader">
							<td
								style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;"></td>
							<td
								style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;"></td>
							<td
								style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;"></td>
							<td
								style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;"></td>
							<td
								style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;"></td>
							<td
								style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;"></td>
						</tr>
					</thead>
					<tbody id="tblBody">
					</tbody>
				</table>
				<div id="loadingData" style="display: none;">
					<h3 id="hLoadId"></h3>
				</div>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<%-- 	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script> --%>

	<spring:url value="/resources/assets/js/MonthPicker.js"
		var="monthPicker"></spring:url>
	<script type="text/javascript" src="${monthPicker}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/app/location-wise-target-achieved-report.js"
		var="locationWiseTargetAchievedReportJs"></spring:url>
	<script type="text/javascript" src="${locationWiseTargetAchievedReportJs}"></script>


</body>
</html>