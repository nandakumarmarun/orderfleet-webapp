<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Day Plan Summary (User Wise)</title>
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";
</script>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Day Plan Summary (User Wise)</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-sm-3 col-xs-12">
					<div class=" input-group">
						<span
							class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
							data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'
							title='filter employee'></span>
						<div class='dropdown-menu dropdown-menu-left'
							style='background-color: #F0F0F0'>
							<div>
								<a class='btn btn-default dropdown-item'
									style='width: 100%; text-align: left;'
									onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
									Employees</a>
							</div>
							<div>
								<a class='btn btn-default dropdown-item'
									style='width: 100%; text-align: left;'
									onclick='GetAllEmployees(this,"no","All Employees")'>All
									Employees</a>
							</div>
							<!-- <div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetOtherEmployees(this,"no","Other Employees")'>Other
												Employees</a>
										</div> -->
						</div>
						<select id="dbEmployee" name="employeePid"
							class="form-control">
							<option value="Dashboard Employee">All Dashboard
								Employees</option>
						</select>
					</div>
				</div>
				<div class="col-md-6 col-xs-12">
					<div id="daterangePicker" class="pull-right"
						style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc;">
						<i class="glyphicon glyphicon-calendar fa fa-calendar"></i>&nbsp;
						<span></span><b class="caret"></b>
					</div>
				</div>
				<div class="col-sm-3 col-xs-12">
					<button id="btnApply" type="button" class="btn btn-info">Apply</button>
				</div>
			</div>
			<br />
			<table class="collaptable table table-striped table-responsive"
				style="border: 2px solid #EBEBEB;">
				<thead>
					<tr style="font-weight: bold;" class="td-color">
						<th id="thUser">Employee</th>
						<th id="thSchdld">Scheduled</th>
						<th id="thAchvd">Achieved</th>
						<th id="thPercentage">Percentage(%)</th>
					</tr>
				</thead>
				<tbody id="tblBody">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<!--collapse table-->
	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url
		value="/resources/app/day-plan-execution-summary-user-wise.js"
		var="dayPlanExecutionSummeryJS"></spring:url>
	<script type="text/javascript" src="${dayPlanExecutionSummeryJS}"></script>


</body>
</html>