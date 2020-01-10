<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Ecom Sales Report</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
.error {
	color: red;
}
</style>
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";
</script>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Ecom Sales Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
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
												onclick='GetDashboardEmployees(this,"Dashboard Employees","All Dashboard Employee")'>Dashboard
												Employee</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetAllEmployees(this,"no","All Employee")'>All
												Employee</a>
										</div>
									</div>
									<select id="dbEmployee" name="employeePid" class="form-control"
										onchange="EcomSalesReport.loadAccountProfileByEmployee();">
										<option value="Dashboard Employees">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>
							<div class="col-sm-2">
								Account <select id="dbAccount" name="accountPid"
									class="form-control">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="EcomSalesReport.showDatePicker()">
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
									onclick="EcomSalesReport.filter()">Apply</button>
							</div>
							<div class="col-sm-2">
								<br />
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
						</div>
					</form>

				</div>

				<div class="col-md-12 col-sm-12 clearfix"
					style="font-size: 14px; color: black;">
					<div class="col-sm-3">
						<label>Transaction Amount : </label> <label
							id="lblTransactionAmount">0</label>
					</div>
					<div class="col-sm-3">
						<label>Transaction Volume : </label> <label
							id="lblTransactionVolume">0</label>
					</div>
				</div>
			</div>
			<div class="table-responsive">
				<table class="collaptable table  table-striped table-bordered"
					id="tblEcomSalesReport">
					<!--table header-->
					<thead>
						<tr>
							<th>Account Profile</th>
							<th>Server Date</th>
							<th>Client Date</th>
							<th>Location</th>
							<th>Verification</th>
							<th>Action</th>
							<th>Remarks</th>
						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyEcomSalesReport">
					</tbody>
				</table>
			</div>


			<hr />

			<div class="modal fade container" id="myModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Add Reason for
								Rejecting</h4>
						</div>
						<div class="modal-body">
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>

							<div class="modal-body" style="overflow: auto;">
								<div class="form-group">
									<label class="control-label" for="field_name">Reason</label>
									<textarea rows="4" cols="50" class="form-control" name="reason"
										id="field_reason" placeholder="Reason"></textarea>
								</div>
								<div>
									<label id="reasonAlert" style="display: none; color: red;">Please
										Enter Reason</label>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="myFormSubmit" class="btn btn-primary">Save</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>



			<!-- Model Container Inventory Voucher-->
			<div class="modal fade container" id="viewModalInventoryVoucher">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Inventory
								Voucher Details</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<table class="table  table-striped table-bordered">
								<tr>
									<td>Document Number</td>
									<td id="lbl_documentNumberIc"></td>
								</tr>
								<tr>
									<td>User</td>
									<td id="lbl_userIc"></td>
								</tr>
								<tr>
									<td>Document</td>
									<td id="lbl_documentIc"></td>
								</tr>
								<tr>
									<td>Date</td>
									<td id="lbl_documentDateIc"></td>
								</tr>
								<tr>
									<td>Receiver</td>
									<td id="lbl_receiverIc"></td>
								</tr>
								<tr>
									<td>Supplier</td>
									<td id="lbl_supplierIc"></td>
								</tr>
								<tr>
									<td>Document Total</td>
									<td id="lbl_documentTotalIc"></td>
								</tr>
								<tr>
									<td>Document Discount Amount</td>
									<td id="lbl_documentDiscountAmountIc"></td>
								</tr>
								<tr>
									<td>Document Discount Percentage</td>
									<td id="lbl_documentDiscountPercentageIc"></td>
								</tr>
							</table>
							<div class="table-responsive" id="divVoucherDetailsInventory">
								<table class="collaptable table table-striped table-bordered">
									<thead>
										<tr>
											<th>Product</th>
											<th>Quantity</th>
											<th>Free Quantity</th>
											<th>Selling Rate</th>
											<th>Tax %</th>
											<th>Discount Amount</th>
											<th>Discount %</th>
											<th>Total</th>
										</tr>
									</thead>
									<tbody id="tblVoucherDetailsIc">

									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/ecom-sales-reports" var="urlEcomSalesReports"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/app/ecom-sales-report.js"
		var="ecomSalesReportJs"></spring:url>
	<script type="text/javascript" src="${ecomSalesReportJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
	<script type="text/javascript">
		// call from dash board
		$(document).ready(function() {

			var employeePid = getParameterByName('user-key-pid');
			getEmployees(employeePid);

			$("#txtToDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});

			$('#myFormSubmit').on('click', function() {
				EcomSalesReport.reject();
			});

			$('#btnDownload').on('click', function() {
				var tblEcomSalesReport = $("#tblEcomSalesReport tbody");
				if (tblEcomSalesReport.children().length == 0) {
					alert("no values available");
					return;
				}
				if (tblEcomSalesReport[0].textContent == "No data available") {
					alert("no values available");
					return;
				}
				EcomSalesReport.downloadXls();
			});

			/* EcomSalesReport.filter(); */

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