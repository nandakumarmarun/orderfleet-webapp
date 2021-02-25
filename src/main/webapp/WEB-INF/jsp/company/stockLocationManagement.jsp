<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | OpeningStock</title>
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
			<h2>Stock Location Management</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="loader hide"></div>

			<div id='loader' class="modal fade container">

				<img src='/resources/assets/images/Spinner.gif'>

			</div>
			<table id="tblStockLocationManagement"
				class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th style="text-align: center;" colspan="2">Opening Stock
							From Back End ERP</th>
						<th rowspan="2">Stock Locations</th>
						<th style="text-align: center;" colspan="3">Live Opening
							Stock</th>
					</tr>
					<tr>
						<th>Opening Stock Time Stamp</th>
						<th>Opening Stock Details</th>
						<th  rowspan="2">Last Approved By</th>
						<th>Live Opening Stock Time Stamp</th>
						<th>Live Opening Stock Details</th>
					</tr>
				</thead>
				<tbody id="tbodyStockLocationManagement">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/stock-location-management"
				var="urlOpeningStock"></spring:url>

			<form name="viewForm" role="form">
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
								<h4 class="modal-title" id="viewModalLabel"><label id='lblModalHeading'></label></h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
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
									<div id="btnUpdateField"></div>
									<table class="table  table-striped table-bordered">
										<thead>
											<tr>
												<th id="thSelectAll"><input type="checkbox" id="selectAll"/>&nbsp;&nbsp;Select All</th>
												<th>Product Profile</th>
												<th>Stock Location</th>
												<th>Quantity</th>
											</tr>
										</thead>
										<tbody id="tbodyOpeningStock">

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
				<!-- /.Model Container-->
			</form>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/stockLocationManagement.js"
		var="stockLocationManagementJs"></spring:url>
	<script type="text/javascript" src="${stockLocationManagementJs}"></script>
</body>
</html>