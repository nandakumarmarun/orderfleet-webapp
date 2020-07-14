<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Receivable Payables</title>
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
			<h2>Receivable Payables</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-4">
								<br /> <select id="dbAccount" name="accountPid"
									class="form-control selectpicker" data-live-search="true">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="ReceivablePayable.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
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
									onclick="ReceivablePayable.loadData()">Apply</button>
							</div>
							<div class="col-sm-1">
								<br />
								<button type="button" class="btn btn-orange" id="btnDownload"
									title="download xlsx">
									<i class="entypo-download"></i> Download
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="collaptable table table-striped table-bordered">
					<thead>
						<tr>
							<th>Account</th>
							<th>Balance Amount</th>
							<th>Type</th>
							<th>Address</th>
						</tr>
					</thead>
					<tbody id="tBodyReceivablePayable">

					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/receivable-payables.js"
		var="receivableJs"></spring:url>
	<script type="text/javascript" src="${receivableJs}"></script>
</body>
</html>