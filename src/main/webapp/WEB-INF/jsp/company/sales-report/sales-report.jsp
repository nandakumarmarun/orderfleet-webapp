<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sales report</title>
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
					<h2 id="title">Sales Report</h2>
				</div>
				<div class="col-md-6 col-sm-6 clearfix">
					<div class="form-group">
						Report Name<select id="dbReportName" class="form-control" disabled="disabled">
							<option value="Sales Report">Sales Report</option>
							<!-- <option value="Not Ordered Products">Not Ordered
								Products</option>
							<option value="User Wise Product Group Summary">Userwise Product Group Summary</option> -->
						</select>
					</div>
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
							<div class="col-sm-3">
								Product Group<select id="dbProductGroup" class="form-control">
									<option value="no">All Groups</option>
									<c:forEach items="${productGroups}" var="productGroup">
										<option value="${productGroup.pid}">${productGroup.name}</option>
									</c:forEach>
								</select>
							</div>
							<%-- <div class="col-sm-3">
								Product <select id="dbProduct" class="form-control">
									<option value="no">Select Product</option>
									<c:forEach items="${products}" var="product">
										<option value="${product.pid}">${product.name}</option>
									</c:forEach>
								</select>
							</div> --%>
						</div>

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
								Account <select id="dbAccount" name="accountPid"
									class="form-control selectpicker" data-live-search="true">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Day <select class="form-control" id="dbDateSearch"
									onchange="SalesReport.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM(Max 3 months)</option>
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
							<div class="col-sm-2">
								Document Type <select id="dbDocumentType" name="documentType"
									class="form-control">
									<c:if test="${empty voucherTypes}">
									</c:if>
									<c:forEach items="${voucherTypes}" var="voucherType">
										<option value="${voucherType}">${voucherType}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info"
									onclick="SalesReport.filter()">Apply</button>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-success" id="downloadXls">Download</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<jsp:include page="sales-report-common.jsp"></jsp:include>
			<hr />

			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/sales-report.js" var="saleReportJs"></spring:url>
	<script type="text/javascript" src="${saleReportJs}"></script>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

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