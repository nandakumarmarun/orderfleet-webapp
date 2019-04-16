<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Receipt History Report</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<h2>Receipt History Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr/>
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbEmployee" name="employeePid" class="form-control">
									<option value="-1">All Employees</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.userPid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3">
								<select id="dbAccounts" name="accountPid" class="form-control">
									<option value="-1">All Accounts</option>
								</select>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="ReceiptHistoryReport.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblReceiptHistoryReport">
					<thead id="tHeadReceiptHistoryReport">
					</thead>
					<tbody id="tBodyReceiptHistoryReport">
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/receipt-history-report.js"
		var="receiptHistoryReportJs"></spring:url>
	<script type="text/javascript" src="${receiptHistoryReportJs}"></script>
</body>
</html>