<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Performance Location Wise</title>
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
			<h2>Performance Location Wise</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								Employee <select id="dbEmployee" name="employeePid" class="form-control">
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Location <select id="dbLocation" name="locationPid"
									class="form-control">
									<option value="no">All Locations</option>
									<c:forEach items="${locations}" var="location">
										<option value="${location.pid}">${location.name}</option>
									</c:forEach>
								</select>
							</div>
							<%-- <div class="col-sm-2" style="display: none;">
								Territory <select id="dbLocation" name="locationPid"
									class="form-control">
									<option value="no">All Territory</option>
									<c:forEach items="${locations}" var="location">
										<option value="${location.pid}">${location.name}</option>
									</c:forEach>
								</select>
							</div> --%>
							<div class="col-sm-2">
								Day <select class="form-control" id="dbDateSearch"
									onchange="LocationWiseSales.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
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
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info"
									onclick="LocationWiseSales.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Employee</th>
							<th>Receiver</th>
							<th>Supplier</th>
							<th>Document</th>
							<th>Document Total</th>
							<th>Date</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tBodyLocationWiseSales">

					</tbody>
				</table>
			</div>
			<%-- <div class="row-fluid">
				<util:pagination thispage="${pageLocationWiseSales}"></util:pagination>
			</div> --%>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/inventory-vouchers" var="urlLocationWiseSales"></spring:url>
		</div>


		<!-- Model Container-->
		<div class="modal fade container" id="viewModal">
			<!-- model Dialog -->
			<div class="modal-dialog">
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
						</table>
						<table class="table  table-striped table-bordered">
							<thead>
								<tr>
									<th>Product Name</th>
									<th>Quantity</th>
									<th>Free Quantity</th>
									<th>Selling Rate</th>
									<th>Tax %</th>
									<th>discount %</th>
									<th>Batch Number</th>
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

	<spring:url value="/resources/app/location-wise-sales.js"
		var="locationWiseSalesJs"></spring:url>
	<script type="text/javascript" src="${locationWiseSalesJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

</body>
</html>