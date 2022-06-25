<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Doctor Visit Detail Report</title>

<script type="text/javascript"
	src="http://maps.google.com/maps/api/js?sensor=false&v=3&libraries=geometry"></script>

<style type="text/css">
.error {
	color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
	.width83 {
		width: 83%
	}
}
</style>
</head>
<body class="page-body" data-url="">
	<div class="page-container">

		<jsp:include page="/WEB-INF/jsp/fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="/WEB-INF/jsp/fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Doctor Visit Report</h2>
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-6">
								<div class="form-check">
									<input type="checkbox" class="form-check-input"
										id="inclSubOrdinates" checked="checked"> <label
										class="form-check-label" for="inclSubOrdinates">Include
										Subordinates</label>
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-2">
								<br /> <select id="dbUser" name="employeePid"
									class="form-control">
									<option value="no">Select Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
									<option value="all">All Employee</option>
								</select>
							</div>

							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="DoctorVisit.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="SINGLE">Single Date</option>

								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
								<br />
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
								<br />
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
								<br />
								<button type="button" class="btn btn-info"
									onclick="DoctorVisit.filter()">Apply</button>
							</div>
							<!-- <div class="col-sm-2">
								<br />
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
 -->
						</div>
					</form>
				</div>
			</div>

			<hr />

			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblDoctorVisit">
					<thead>
						<tr>
							<th>Employee Name</th>
							<th>No of Doctors visited</th>
							<th>Attendance</th>
							<th> StartTime </th>
							<th> EndTime </th>
							<th>Route</th>
							<th>Distance in S\W</th>
							<th>Distance in DayEnd</th>
							<th>Rs\KM</th>
							<th>TA</th>
							<th>DA</th>
							<th>Total</th>
						</tr>
					</thead>
					<tbody id="tBodyDoctorVisit">
					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>
<!-- tableExport.jquery.plugin -->
	<spring:url value="/resources/assets/js/tableexport/xlsx.core.min.js"
		var="jsXlsx"></spring:url>
	<spring:url value="/resources/assets/js/tableexport/FileSaver.min.js"
		var="fileSaver"></spring:url>
<%-- 	<spring:url value="/resources/assets/js/tableexport/tableexport.min.js" --%>
<%-- 		var="tableExport"></spring:url> --%>
	<script type="text/javascript" src="${jsXlsx}"></script>
	<script type="text/javascript" src="${fileSaver}"></script>
<%-- 	<script type="text/javascript" src="${tableExport}"></script> --%>
	
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/app/doctor-visit.js"
		var="invoiceWiseRepoetJs"></spring:url>
	<script type="text/javascript" src="${invoiceWiseRepoetJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

		
</body>
</html>