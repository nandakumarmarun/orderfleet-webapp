<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Lost Analysis report</title>
<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="/WEB-INF/jsp/fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="/WEB-INF/jsp/fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<div class="row">
				<div class="col-md-6 col-sm-6 clearfix">
					<h2 id="title">Lost Analysis Report</h2>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-sm-3">
					Contacts <select id="dbAccount" name="accountPid"
						class="form-control">
						<option value="-1">All Contacts</option>
						<c:forEach items="${accountProfiles}" var="accountProfile">
							<option value="${accountProfile.pid}">${accountProfile.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-3">
					Day <select class="form-control" id="dbDateSearch"
						onchange="LostAnalysisReport.showDatePicker()">
						<option value="TODAY">Today</option>
						<option value="YESTERDAY">Yesterday</option>
						<option value="WTD">WTD</option>
						<option value="MTD">MTD</option>
						<option value="TILLDATE">TILL DATE</option>
						<option value="CUSTOM">CUSTOM</option>
					</select>
				</div>
				<div id="divDatePickers" style="display: none;">
					<div class="col-sm-2">
						From Date <input type="date" class="form-control" id="txtFromDate"
							placeholder="Select From Date"
							style="background-color: #fff; width: 139px;" />
					</div>
					<div class="col-sm-2">
						To Date <input type="date" class="form-control" id="txtToDate"
							placeholder="Select To Date"
							style="background-color: #fff; width: 139px;" />
					</div>
				</div>
				<div class="col-sm-3">
					RCA reasons <br /> <select id="dbRca" name="rcaPid"
						class="form-control" multiple="multiple">
						<c:forEach items="${rcas}" var="rca">
							<option value="${rca.pid}">${rca.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-2">
					<br>
					<button type="button" class="btn btn-info"
						onclick="LostAnalysisReport.filter()">Apply</button>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row" style="font-size: 14px; color: black;">
				<form class="form-inline">
					<div class="form-group col-sm-3">
						<label>Count : </label> <label id="lblCount">0</label>
					</div>
					<!-- <div class="form-group" align="right">
						<label for="srch-term">Search:</label> <input class="form-control" placeholder="Search" name="srch-term" id="srch-term">
					</div> -->
				</form>
			</div>
			<div class="table-responsive">
				<table class="table table-striped table-bordered"
					id="tblLostAnalysisReport">
					<thead id="tHeadLostAnalysisReport">
						<tr>
							<th>Customer Name</th>
							<th>RCA</th>
							<th>Employee Name</th>
							<th>Lead Created By</th>
							<th>Closed Date</th>
						</tr>
					</thead>
					<tbody id="tBodyLostAnalysisReport">
					</tbody>
				</table>
			</div>

			<hr />

			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>

	<spring:url value="/resources/assets/js/jquery.form.js" var="ajaxForm" />
	<script src="${ajaxForm}"></script>

	<spring:url value="/resources/app/lead-to-sales/lost-analysis-report.js" var="lostAnalysisReportJs"></spring:url>
	<script type="text/javascript" src="${lostAnalysisReportJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<%-- <spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script> --%>
</body>
</html>