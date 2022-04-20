<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Receipt Download Status</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
.error {
	color: red;
}

#tBodyAccountVoucher td {
	padding-bottom: 50px;
}

.zoom {
	background-color: white;
}

.zoom:hover {
	-ms-transform: scale(2.0); /* IE 9 */
	-webkit-transform: scale(2.0); /* Safari 3-8 */
	transform: scale(2.0);
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Receipt Download Status</h2>
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
							<%-- <div class="col-sm-2">
								Document Type <select id="dbDocumentType" name="documentType" class="form-control">
								<c:if test="${empty voucherTypes}">
									<option value="no">Select DocumentType</option></c:if>
									<c:forEach items="${voucherTypes}" var="voucherType">
										<option value="${voucherType}">${voucherType}</option>
									</c:forEach>
									
								</select>
							</div>
 --%>
							<div class="col-sm-2">
								Document <select id="dbDocument" name="documentPid"
									class="form-control">
									<c:if test="${empty documents}">
										<option value="no">Select Document</option>
									</c:if>
									<c:forEach items="${documents}" var="document">
										<option value="${document.pid}">${document.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Day <select class="form-control" id="dbDateSearch"
									onchange="AccountVoucher.showDatePicker()">
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
									<option value="FAILED">FAILED</option>


								</select>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info"
									onclick="AccountVoucher.filter()">Apply</button>
							</div>

							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-success" id="downloadXls">Download</button>
							</div>

							<%-- 							<c:if test="${sendTransactionsSapPravesh=='true'}"> --%>
							<!-- 								<div class="col-sm-1"> -->
							<!-- 									<br> -->
							<!-- 									<button type="button" class="btn btn-primary" -->
							<!-- 										id="sendTransactionsSapPravesh">Send Receipts To SAP</button> -->
							<!-- 																		<div class="loader hide"></div> -->

							<!-- 								</div> -->

							<%-- 							</c:if> --%>
						</div>
					</form>
				</div>
				<div id='loader' class="modal fade container">

					<img src='/resources/assets/images/Spinner.gif'>

				</div>
				<div class="col-md-12 col-sm-12 clearfix"
					style="font-size: 14px; color: black;">
					<div class="col-sm-2">
						<label>Count : </label>&nbsp;&nbsp;&nbsp;<label id="lblCounts">0.00</label>
					</div>
					<!-- <div class="col-sm-3"> <label>Total By Amount : </label>&nbsp;&nbsp;&nbsp;<label id="lblTotalByAmount">0.00</label></div>
					<div class="col-sm-3"> <label>Total To Amount : </label>&nbsp;&nbsp;&nbsp;<label id="lblTotalToAmount" >0.00</label></div> -->
					<div class="col-sm-3">
						<label>Total Amount : </label>&nbsp;&nbsp;&nbsp;<label
							id="lblTotalAmount">0.00</label>
					</div>
					<div class="col-sm-3">
						<label>Total Cheque Amount : </label>&nbsp;&nbsp;&nbsp;<label
							id="lblTotalChequeAmount">0.00</label>
					</div>
					<div class="col-sm-3">
						<label>Total Cash Amount : </label>&nbsp;&nbsp;&nbsp;<label
							id="lblTotalCashAmount">0.00</label>
					</div>
				</div>

			</div>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th><input type="checkbox" id="selectAll" />&nbsp;&nbsp;Select
								All</th>
							<th>Employee</th>
							<th>Account</th>
							<th>Document</th>
							<th>Amount
								<p id="totalDocument" style="float: right;"></p>
							</th>
							<!-- 							<th>Volume<p id="totalVolume" style="float: right;"></p></th>
							<th>Total Quantity</th> -->
							<th>Date</th>
							<th>Status</th>
							<th>Action</th>
							<th></th>
							<!-- <th>VisitRemarks</th> -->
						</tr>
					</thead>
					<tbody id="tBodyAccountVoucher">
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/account-vouchers" var="urlAccountVoucher"></spring:url>
		</div>



		<!-- Model Container-->
		<div class="modal fade container " id="imagesModal">
			<!-- model Dialog -->
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="viewModalLabel">Receipt Images</h4>
					</div>
					<div class="modal-body zoom">
						<!-- error message -->
						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
						<div id="divReceiptImages" style="overflow: auto; height: 500px;"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
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
						<h4 class="modal-title" id="viewModalLabel">Accounting
							Voucher Details</h4>
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
								<td>Account</td>
								<td id="lbl_account"></td>
							</tr>
							<tr>
								<td>Document</td>
								<td id="lbl_document"></td>
							</tr>
							<tr>
								<td>Date</td>
								<td id="lbl_createdDate"></td>
							</tr>
							<tr>
								<td>Total Amount</td>
								<td id="lbl_totalAmount"></td>
							</tr>
							<tr>
								<td>Remarks</td>
								<td id="lbl_remarks"></td>
							</tr>
						</table>
						<div class="table-responsive">
							<table class="collaptable table  table-striped table-bordered">
								<thead>
									<tr>
										<th style="width: 80px;">Mode</th>
										<th>Amount</th>
										<th>Instrument Number</th>
										<th>Instrument Date</th>
										<th>Bank Name</th>
										<th>By Account</th>
										<th>To Account</th>
										<th>Expense Type</th>
										<th>Voucher Number</th>
										<th>Voucher Date</th>
										<th>Reference Number</th>
										<th>Provisional Receipt Number</th>
										<th>Remarks</th>
									</tr>
								</thead>
								<tbody id="tblVoucherDetails">

								</tbody>
							</table>
						</div>
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

	<spring:url value="/resources/app/accounting-voucher-tally-status.js"
		var="accountingVoucherJs"></spring:url>
	<script type="text/javascript" src="${accountingVoucherJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

</body>
</html>