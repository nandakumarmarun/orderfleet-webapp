<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Day Plan Execution Summary</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Day Plan Execution Summary</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-sm-3 col-xs-12">
					<select id="dbEmployee" name="employeePid" class="form-control" >
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
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
					<tr
						style="font-weight: bold;"
						class="td-color">
						<th>Created Date</th>
						<th>Date</th>
						<th>Scheduled</th>
						<th>Achieved</th>
						<th>Percentage(%)</th>
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

	<spring:url value="/resources/app/day-plan-execution-summery.js"
		var="dayPlanExecutionSummeryJS"></spring:url>
	<script type="text/javascript" src="${dayPlanExecutionSummeryJS}"></script>

</body>
</html>