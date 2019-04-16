<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Executive Wise Leads</title>
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
					<h2 id="title">Executive Wise Leads</h2>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row" style="margin-top: 6%;">
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
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
										class="form-control selectpicker">
										<option value="no">Select Employees</option>
									</select>
								</div> 
							</div>

							<div class="col-sm-2">
								Stages <br /> <select id="dbStage" name="stagePid"
									class="form-control" multiple="multiple">
									<c:forEach items="${leadToCashReportConfigStages}" var="stage">
										<option value="${stage.stagetPid}">${stage.stageName}</option>
									</c:forEach>
								</select>
							</div>
							
							<div class="col-sm-3">
								Day <select class="form-control" id="dbDateSearch"
								onchange="StageReport.showDatePicker()">
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
							<div class="col-sm-2">
							<br>
								<button type="button" class="btn btn-info"
									onclick="StageReport.filter()">Apply</button>	
							</div>

						</div>
					</form>
				</div>
			</div>

			<div class="row">
				
				
				
				
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row" style="font-size: 14px; color: black;">
				<form class="form-inline">
					<div class="form-group col-sm-3">
						<label>Count : </label> <label id="lblCount">0</label>
					</div>
				</form>
			</div>
			<div class="table-responsive">
				<table class="table table-striped table-bordered"
					id="tblStageReport">
					<thead id="tHeadStageReport">
						<tr>
							<th>User</th>
							<th>Customer</th>
							<th>Stage</th>
							<th>Remarks</th>
						</tr>
					</thead>
					<tbody id="tBodyStageReport">
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

	<spring:url value="/resources/app/lead-to-sales/executive-wise-leads.js" var="executiveWiseLeadsJs"></spring:url>
	<script type="text/javascript" src="${executiveWiseLeadsJs}"></script>
	 
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>
</body>
</html>