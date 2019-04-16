<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Attendance Status SubGroups Report</title>
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
			<h2>Attendance Status SubGroup Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
							Sub-Groups
								<select id="dbAttStatus" name="attendanceStatus"
									class="form-control">
									<option value="all">ALL</option>
									<c:forEach items="${subgroups}" var="subgroup">
										<option value="${subgroup.id}">${subgroup.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
							Date
								<select class="form-control" id="dbDateSearch"
									onchange="AttendanceStatusSubgroupReport.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
								<div class="input-group">
									<input type="text" class="form-control" id="txtFromDate"
										placeholder="Select From Date" style="background-color: #fff;"
										readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2 hide custom_date2">
								<div class="input-group">
									<input type="text" class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="AttendanceStatusSubgroupReport.filter()">Apply</button>
							</div>
							<div class="col-sm-1">
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<input type="text" id="search" placeholder="Search..."
						class="form-control" style="float: right; width: 200px;">
				</div>
			</div>
			<br>
			
			<table class="table  table-striped table-bordered"
				id="tblAttendanceReport">
				<thead>
					<tr>
						<th>Attendance Day</th>
						<th>Employee</th>
						<th>Attendance Status</th>
						<th>Client Date</th>
						<th>Server Date</th>
						<th>Work Location</th>
						<th>Remarks</th>
					</tr>
				</thead>
				<tbody id="tBodyAttendanceReport">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
		</div>
	</div>
	
		<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/attendance-status-subgroup-report.js"
		var="attendanceStatusSubgroupReportJs"></spring:url>
	<script type="text/javascript" src="${attendanceStatusSubgroupReportJs}"></script>
	
</body>
</html>