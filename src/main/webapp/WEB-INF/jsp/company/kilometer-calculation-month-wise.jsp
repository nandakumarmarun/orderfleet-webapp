<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Kilometer Calculation</title>

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
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Kilometer Calculation Month</h2>
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<br /> <select id="dbUser" name="userPid" class="form-control">
									<option value="no">Select Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.userPid}">${employee.name}</option>
									</c:forEach>
									<option value="all">All Employee</option>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select id="dbFareType" name="fareTypePid"
									class="form-control">
									<option value="no">Select Vehicle Fare</option>
									<c:forEach items="${distanceFare}" var="distanceFare">
										<option value="${distanceFare.fare}">${distanceFare.vehicleType}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="KilometerCalculation.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="SINGLE">Single Date</option>
									<option value="CUSTOM">CUSTOM</option>
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
									onclick="KilometerCalculation.filter()">Apply</button>
							</div>
							<div class="col-sm-2">
								<br />
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>

						</div>
					</form>
				</div>
			</div>
			<div class="col-md-12 col-sm-12 clearfix"
				style="font-size: 14px; color: black;">
				<div class="col-sm-3">
					<label>Transactions : </label> <label id="lblSub">0</label>
				</div>
				<div class="col-sm-3">
					<label>Total distance : </label> <label id="lblTotalKilo">0
						Km</label>
				</div>
				<div class="col-sm-3">
					<label>Total Fare : Rs </label> <label id="lblTotalFare">0</label>
				</div>
				<div class="col-sm-3"></div>
			</div>
			<div class="clearfix"></div>
			<hr />

			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblKilometerCalculation">
					<thead>
						<tr>
							<th>Employee</th>
							<th>Account</th>
							<th>Server Date</th>
							<th>Client Date</th>
							<th>Punch Time</th>
							<th>Location</th>
							<th>Metres</th>
							<th>Kilometer</th>
						</tr>
					</thead>
					<tbody id="tBodyKilometerCalculation">
					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<%-- <spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script> --%>

	<!-- tableExport.jquery.plugin -->
	<spring:url value="/resources/assets/js/tableexport/xlsx.core.min.js"
		var="jsXlsx"></spring:url>
	<spring:url value="/resources/assets/js/tableexport/FileSaver.min.js"
		var="fileSaver"></spring:url>
	<spring:url value="/resources/assets/js/tableexport/tableexport.min.js"
		var="tableExport"></spring:url>
	<script type="text/javascript" src="${jsXlsx}"></script>
	<script type="text/javascript" src="${fileSaver}"></script>
	<script type="text/javascript" src="${tableExport}"></script>

	<spring:url value="/resources/app/kilometer-calculation-monthwise.js"
		var="kilometerCalculationJs"></spring:url>
	<script type="text/javascript" src="${kilometerCalculationJs}"></script>
</body>
</html>