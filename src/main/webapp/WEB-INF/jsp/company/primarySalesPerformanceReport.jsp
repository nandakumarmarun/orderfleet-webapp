<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Performance Report (Primary)</title>
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
			<h2>Transaction Summary</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								Employee<select id="dbEmployee" name="employeePid" class="form-control">
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Account <select id="dbAccount" name="accountPid"
									class="form-control">
									<option value="-1">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Document Type <select id="dbDocumentType" name="documentType" class="form-control">
								<c:if test="${empty voucherTypes}">
									<option value="no">Select DocumentType</option></c:if>
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
							
							<input id="companyConfiguration" type="hidden" value='${companyConfiguration}'/>
							<div class="col-sm-2 hide custom_date1">
							<br/>
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
							<br/>
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
							<div class="col-sm-2">
								Status <select id="dbStatus" name="status"
									class="form-control">
									<option value="no">All</option>
										<option value="processed">Processed</option>
									<option value="pending">Pending</option>
								</select>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info" id="btnApply">Apply</button>
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
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th><input type="checkbox" id="selectAll"/>&nbsp;&nbsp;Select All</th>
							<th>Employee</th>
							<th>Receiver</th>
							<th>Document</th>
							<th>Amount<p id="totalDocument" style="float: right;"></p></th>
							<th>Volume<p id="totalVolume" style="float: right;"></p></th>
							<th>Total Quantity</th>
							<th>Date</th>
							<th>Status</th>
							<th>Action</th>
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


		<!-- Model Container-->
		<div class="modal fade container" id="viewModal">
			<!-- model Dialog -->
			<div class="modal-dialog" style="width: 100%;">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="viewModalLabel">Inventory Voucher
							Details</h4>
					</div>
					<div class="modal-body">
						<!-- error message -->
						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
						<table class="table  table-striped table-bordered">
							<tr>
								<td>Document Number</td>
								<td id="lbl_documentNumber"></td>
							</tr>
							<tr>
								<td>User</td>
								<td id="lbl_user"></td>
							</tr>
							<tr>
								<td>Document</td>
								<td id="lbl_document"></td>
							</tr>
							<tr>
								<td>Date</td>
								<td id="lbl_documentDate"></td>
							</tr>
							<tr>
								<td>Receiver</td>
								<td id="lbl_receiver"></td>
							</tr>
							<tr>
								<td>Supplier</td>
								<td id="lbl_supplier"></td>
							</tr>
							<tr>
								<td>Document Total</td>
								<td id="lbl_documentTotal"></td>
							</tr>
							<tr>
								<td>Document Volume</td>
								<td id="lbl_documentVolume"></td>
							</tr>
							<tr>
								<td>Document Discount Amount</td>
								<td id="lbl_documentDiscountAmount"></td>
							</tr>
							<tr>
								<td>Document Discount Percentage</td>
								<td id="lbl_documentDiscountPercentage"></td>
							</tr>
						</table>
						<table class="collaptable table table-striped table-bordered">
							<thead>
								<tr>
									<th>Product</th>
									<th>Quantity</th>
									<th>Unit Qty</th>
									<th>Total Qty</th>
									<th>Free Qty</th>
									<th>Selling Rate</th>
									<th>Tax%</th>
									<th>Discount%</th>
									<th>Total</th>
								</tr>
							</thead>
							<tbody id="tblVoucherDetails">

							</tbody>
						</table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/inventory-voucher.js"
		var="inventoryVoucherJs"></spring:url>
	<script type="text/javascript" src="${inventoryVoucherJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

</body>
</html>