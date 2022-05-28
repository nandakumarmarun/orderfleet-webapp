<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sale Order StatusVise Report</title>
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
			<h2>Sale Order Status Wise Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />

			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								Employee<select id="dbEmployee" name="employeePid"
									class="form-control">
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Account <select id="dbAccount" name="accountPid"
									class="form-control selectpicker" data-live-search="true">
									<option value="-1">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Document Type <select id="dbDocumentType" name="documentType"
									class="form-control">
									<c:if test="${empty voucherTypes}">
										<option value="no">Select DocumentType</option>
									</c:if>
									<c:forEach items="${voucherTypes}" var="voucherType">
										<option value="${voucherType}">${voucherType}</option>
									</c:forEach>

								</select>
							</div>

							<div class="col-sm-2">
								Document <select id="dbDocument" name="documentPid"
									class="form-control">
									<option value="no">All</option>
								</select>
							</div>

							<div class="col-sm-2">
								Day <select class="form-control" id="dbDateSearch"
									onchange="InventoryVoucher.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
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



							<div class="col-sm-2">
								Status <select id="dbStatus" name="tallyDownloadStatus"
									class="form-control">
									<option value="ALL">All</option>
									<option value="PENDING">PENDING</option>
									<option value="PROCESSING">PROCESSING</option>
									<option value="COMPLETED">COMPLETED</option>
								</select>
							</div>
							<div class="col-sm-2">
								Status <select id="salesStatus" name="salesOrderStatus"
									class="form-control">
									<option value="ALL">All</option>
									<option value="CREATED">CREATED</option>
									<option value="CANCELED">CANCELED</option>
									<option value="DELIVERD">DELIVERD</option>
									<option value="CONFIRM">CONFIRM</option>
								</select>
							</div>

							<div class="col-sm-2">
								Account <select id="dbTerittory" name="terittory"
									class="form-control selectpicker" data-live-search="true">
									<option value="-1">All Terittories</option>
									<c:forEach items="${territories}" var="locationAccountProfile">
										<option value="${locationAccountProfile.pid}">${locationAccountProfile.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info"
									onclick="InventoryVoucher.filter()">Apply</button>
							</div>
							<div class="col-sm-1 " id="downloadXls">
								<br>
								<button type="button" class="btn btn-success"
									id="downloadXlsbutton">Download</button>
							</div>

						</div>
					</form>
				</div>

				<div class="col-md-12 col-sm-12 clearfix"
					style="font-size: 14px; color: black;">
					<div class="col-sm-2">
						<label>Count : </label> <label id="lblCounts">0</label>
					</div>
				</div>
			</div>


			<div id='loader' class="modal fade container">

				<img src='/resources/assets/images/Spinner.gif'>

			</div>

			<div class="table-responsive">


				<table class="table  table-striped table-bordered "
					id="inventoryVoucherTable">
					<thead id="theadInventoryVoucher">
						<tr>
							<th>OrderId</th>
							<th>Server Date</th>
							<th>Sales Executive Name</th>
							<th>Customer Name</th>
							<th>District</th>
							<th>Location</th>
							<th>Territory Route</th>
							<th>Delivery Date</th>
							<th>GPS Location</th>
							<th>SalesOrderStatus</th>
							<th>Total Order value</th>
						</tr>
					</thead>
					<tbody id="tBodyInventoryVoucher">
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/inventory-vouchers" var="urlInventoryVoucher"></spring:url>
		</div>


		<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

		<spring:url value="/resources/app/sales-order-status-report.js"
			var="inventoryVoucherJs"></spring:url>
		<script type="text/javascript" src="${inventoryVoucherJs}"></script>

		<spring:url value="/resources/assets/js/table2excel.js"
			var="table2excel"></spring:url>
		<script type="text/javascript" src="${table2excel}"></script>

		<!-- tableExport.jquery.plugin -->
		<spring:url value="/resources/assets/js/tableexport/xlsx.core.min.js"
			var="jsXlsx"></spring:url>
		<spring:url value="/resources/assets/js/tableexport/FileSaver.min.js"
			var="fileSaver"></spring:url>
		<spring:url
			value="/resources/assets/js/tableexport/tableexport.min.js"
			var="tableexport"></spring:url>
		<script type="text/javascript" src="${jsXlsx}"></script>
		<script type="text/javascript" src="${fileSaver}"></script>
		<%-- 	<script type="text/javascript" src="${tableExport}"></script> --%>

		<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
		<script type="text/javascript" src="${momentJs}"></script>

		<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
			var="aCollapTable"></spring:url>
		<script type="text/javascript" src="${aCollapTable}"></script>
</body>
</html>