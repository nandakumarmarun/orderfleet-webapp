<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Accounting Ledger Report</title>
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
			<h2>Accounting Ledger Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<select id="dbEmployee" name="employeePid" class="form-control">
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<select id="dbAccount" name="accountPid" class="form-control selectpicker" data-live-search="true">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<select id="dbDocument" name="documentPid" class="form-control">
									<option value="no">All Documents</option>
									<c:forEach items="${documents}" var="document">
										<option value="${document.pid}">${document.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<select class="form-control" id="dbDateSearch"
									onchange="AccountingLedgerReport.showDatePicker()">
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
										 placeholder="Select From Date"
										style="background-color: #fff;" readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2 hide custom_date2">
								<div class="input-group">
									<input  type="text"
										class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="AccountingLedgerReport.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>

				<div class="col-md-12 col-sm-12 clearfix">
					<div class="col-sm-3">
						<label>Toatal By Amount : </label>&nbsp;&nbsp;&nbsp;<label
							id="lblTotalByAmount" style="font-size: large;">0.00</label>
					</div>
					<div class="col-sm-3">
						<label>Toatal To Amount : </label>&nbsp;&nbsp;&nbsp;<label
							id="lblTotalToAmount" style="font-size: large;">0.00</label>
					</div>

				</div>
			</div>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Employee</th>
							<th>Document</th>
							<th>Date</th>
							<th>By Account</th>
							<th>To Account</th>
							<th>Income Expense Head</th>
							<th>By Amount</th>
							<th>To Amount</th>
						</tr>
					</thead>
					<tbody id="tBodyAccountingLedgerReport">

					</tbody>
				</table>
			</div>
			<%-- <div class="row-fluid">
				<util:pagination thispage="${pageAccountingLedgerReport}"></util:pagination>
			</div> --%>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/accounting-ledger-report.js"
		var="accountingLedgerReportJs"></spring:url>
	<script type="text/javascript" src="${accountingLedgerReportJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

</body>
</html>