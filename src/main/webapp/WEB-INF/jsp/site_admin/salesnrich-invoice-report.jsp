<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Salesnrich Invoice Reports</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Salesnrich Invoice Reports</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<select id="dbCompany" name="companyPid" class="form-control">
									<option value="no">All Companies</option>
									<c:forEach items="${companies}" var="company">
										<option value="${company.pid}">${company.legalName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="SalesnrichInvoiceReport.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Invoice Number</th>
							<th>Date</th>
							<th>User Count</th>
							<th>GST %</th>
							<th>Total</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tBodySalesnrichInvoiceReport">

					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
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
						<h4 class="modal-title" id="viewModalLabel">Salesnrich
							Invoice Report</h4>
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
								<td>Invoice Number</td>
								<td id="lbl_invoiceNumber"></td>
							</tr>
							<tr>
								<td>Active Users</td>
								<td id="lbl_activeUsers"></td>
							</tr>
							<tr>
								<td>Checked Users</td>
								<td id="lbl_checkedUsers"></td>
							</tr>
							<tr>
								<td>Total USers</td>
								<td id="lbl_totalUsers"></td>
							</tr>
							<tr>
								<td>Date</td>
								<td id="lbl_createdDate"></td>
							</tr>
							<tr>
								<td>Sub Total Amount</td>
								<td id="lbl_subTotalAmount"></td>
							</tr>
							<tr>
								<td>GST %</td>
								<td id="lbl_gstPercent"></td>
							</tr>
							<tr>
								<td>GST Amount</td>
								<td id="lbl_gstAmt"></td>
							</tr>
							<tr>
								<td>Total Amount</td>
								<td id="lbl_totalAmount"></td>
							</tr>
						</table>
						<div class="table-responsive">
							<table class="table table-striped table-bordered">
								<thead>
									<tr>
										<th>Particulars</th>
										<th>Quantity</th>
										<th>Price</th>
										<th>Total</th>
									</tr>
								</thead>
								<tbody id="tblInvoiceDetails">
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

	<spring:url value="/resources/app/salesnrich-invoice-report.js"
		var="salesnrichInvoiceReportJs"></spring:url>
	<script type="text/javascript" src="${salesnrichInvoiceReportJs}"></script>

</body>
</html>