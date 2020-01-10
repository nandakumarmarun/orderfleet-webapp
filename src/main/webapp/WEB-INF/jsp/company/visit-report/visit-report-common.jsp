<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Visit report</title>
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
					<h2 id="title">Visit Report</h2>
				</div>
			</div>

			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row" style="margin-top: 6%;">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-1">
								<div class="form-check">
									<input type="checkbox" class="form-check-input"
										id="inclSubOrdinates" checked="checked"> <label
										class="form-check-label" for="inclSubOrdinates">Include
										Subordinate</label>
								</div>
							</div>
							<div class="col-sm-3">
								Employee
								<div class=" input-group">
									<span
										class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
										data-toggle='dropdown' aria-haspopup='true'
										aria-expanded='false' title='filter employee'></span>
									<div class='dropdown-menu dropdown-menu-left'
										style='background-color: #F0F0F0'>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
												Employee</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetAllEmployees(this,"no","All Employee")'>All
												Employee</a>
										</div>
									</div>
									<select id="dbEmployee" name="employeePid"
										class="form-control">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>

							<div class="col-sm-2">
								Day <select class="form-control" id="dbDateSearch"
									onchange="VisitsReport.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							<div id="divDatePickers" style="display: none;">
								<div class="col-sm-2">
									From Date <input type="date" class="form-control"
										id="txtFromDate" placeholder="Select From Date"
										style="background-color: #fff; width: 139px;" />
								</div>
								<div class="col-sm-2">
									To Date <input type="date" class="form-control" id="txtToDate"
										placeholder="Select To Date"
										style="background-color: #fff; width: 139px;" />
								</div>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info"
									onclick="VisitsReport.filter()">Apply</button>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-success" id="downloadXls">Download</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<div class="col-md-12 col-sm-12 clearfix"
					style="font-size: 14px; color: black; display: none;">
					<div class="col-sm-3"></div>
					<div class="col-sm-3"></div>
					<div class="col-sm-3"></div>
					<div class="col-sm-3">
						<label>Total Visit: </label> <label id="lblTotal">0</label>
					</div>
				</div>
				<table class="table table-striped table-bordered"
					id="tblVisitReport">
					<thead id="tHeadVisitReport">
					</thead>
					<tbody id="tBodyVisitReport">
					</tbody>
				</table>
			</div>

			<hr />

			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/visit-report.js" var="visitReportJs"></spring:url>
	<script type="text/javascript" src="${visitReportJs}"></script>

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

</body>
</html>