<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | DayEnd Sales & Damage Report</title>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/MonthPicker.css"
	var="monthPickerCss"></spring:url>
<link href="${monthPickerCss}" rel="stylesheet" media="all"
	type="text/css">


<style type="text/css">
.table-bordered>thead>tr>th, .table-bordered>tbody>tr>th,
	.table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td,
	.table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
	font-size: 12px;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>

		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Day End Sales & Damage Report</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12  clearfix">
					<form role="form" class="form-horizontal">
						<div class="form-group">
							<div class="col-sm-6">
								<div class="form-check">
									<input type="checkbox" class="form-check-input"
										id="inclSubOrdinates" checked="checked"> <label
										class="form-check-label" for="inclSubOrdinates">Include
										Subordinates</label>
								</div>
							</div>
						</div>
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
												onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
												Employee</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetAllEmployees(this,"no","All Employee")'>All
												Employee</a>
										</div>
										<!-- <div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetOtherEmployees(this,"no","Other Employee")'>Other
												Employees</a>
										</div> -->
									</div>
									<select id="dbEmployee" name="employeePid" class="form-control">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>

							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="CustomerWiseItemSale.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>

									<option value="SINGLE">Single Date</option>

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
							<!-- <div class="input-group col-sm-2">
								<div class="col-sm-3">
									<br />
									<button type="button" class="btn btn-info entypo-search"
										style="font-size: 18px" onclick="InvoiceWiseReport.filter()"
										title="Apply"></button>
								</div>
								<div class="col-sm-3">
									<br />
									<button id="btnDownload" type="button" style="font-size: 18px"
										class="btn btn-orange entypo-download" title="Download Xls"></button>
								</div>
							</div> -->
							<div class="col-sm-1">
							<br />
								<button id="applyBtn" type="button" class="btn btn-info">Apply</button>

							</div>
							<div class="col-sm-1">
									<br />
									<button id="btnDownload" type="button" style="font-size: 18px"
										class="btn btn-orange entypo-download" title="Download Xls"></button>
								</div>
						</div>
					</form>
				</div>
			</div>
			<hr />

			<div class="table-responsive">
				<table class="collaptable table  table-striped table-bordered"
					id="tblInvoiceWiseReport">
					<!--table header-->
					<thead>
						<tr id="rowhead">
							<th rowspan="2" style="text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: normal;font-size:18px; vertical-align: middle;">Customer Name</th>

							<c:forEach items="${products}" var="product">

								<th colspan="2" id="sales" style="text-align: center; color: white; background-color: rgb(48, 54, 65); font-weight: normal;font-size:18px; vertical-align: middle;">${product.name}
							</c:forEach>
						<tr>
							<c:forEach items="${products}" var="product">
								<th style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;">Sales</th>
								<th style="color: white; background-color: #35aa47; text-align: center; vertical-align: middle;">Damage</th>
							</c:forEach>
					</thead>
					<!--table header-->
					<tbody id="tBodyInvoiceWiseReport">
					</tbody>
				</table>
			</div>

			<div id="loadingData" style="display: none;">
				<h3 id="hLoadId"></h3>
			</div>
			<hr/>
		<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>

		<!-- Footer -->
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>



	<spring:url value="/resources/assets/js/MonthPicker.js"
		var="monthPicker"></spring:url>
	<script type="text/javascript" src="${monthPicker}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>
	<spring:url value="/resources/app/customerWise-itemSale.js"
		var="customerWiseItemSaleJs"></spring:url>

	<script type="text/javascript" src="${customerWiseItemSaleJs}"></script>

</body>
</html>