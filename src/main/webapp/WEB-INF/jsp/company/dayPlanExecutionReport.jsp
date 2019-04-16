<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Day Plan Execution Report</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Day Plan Execution Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbEmployee" name="employeePid" class="form-control" >
									<option value="no">Select Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3 custom_date1">
							<div class="input-group">
									<input type="text" class="form-control" id="txtDate"
										 placeholder="Select Date"
										style="background-color: #fff;" readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="DayPlanExecutionReport.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Day Plan</th>
						<th>Execution Order</th>
						<th>Execution Status</th>
						<th>Remarks</th>
					</tr>
				</thead>
				<tbody id="tBodyDayPlans">
					<tr>
						<td colspan='4' align='center'>No data available</td>
					</tr>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/accounting-vouchers"
				var="urlAccountingVoucher"></spring:url>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/day-plan-execution-report.js"
		var="dayPlanExecutionReportJs"></spring:url>
	<script type="text/javascript" src="${dayPlanExecutionReportJs}"></script>
	<script type="text/javascript">
		// call from dash board
		$(document).ready(function() {
			var userPid = getParameterByName('pid');
			var date = getParameterByName('date');
			if (userPid != null && userPid != "") {
				$('#dbUser').val(userPid);
				$('#txtDate').val(date);
				DayPlanExecutionReport.filter();
			}
		});

		function getParameterByName(name, url) {
			if (!url)
				url = window.location.href;
			name = name.replace(/[\[\]]/g, "\\$&");
			var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
					.exec(url);
			if (!results)
				return null;
			if (!results[2])
				return '';
			return decodeURIComponent(results[2].replace(/\+/g, " "));
		}
	</script>
</body>
</html>