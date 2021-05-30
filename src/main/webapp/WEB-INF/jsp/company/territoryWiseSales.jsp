<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Territory Wise Sales</title>
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
			<h2>Territory Wise Sales</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">

							<div id='loader' class="modal fade container">

								<img src='/resources/assets/images/Spinner.gif'>

							</div>

							<c:forEach items="${levels}" var="level">
								<div class="col-sm-2">
									Level ${level} <select id="dbLevel${level}" name="zonePid"
										class="form-control">
										<option value="-1">All</option>
									</select>
								</div>
							</c:forEach>

							<!-- 							<div class="col-sm-2"> -->
							<!-- 								District <select id="dbTerrtory" name="terrtoryPid" -->
							<!-- 									class="form-control"> -->
							<!-- 									<option value="no">All</option> -->
							<%-- 									<c:forEach items="${locations}" var="location"> --%>
							<%-- 										<option value="${location.pid}">${location.name}</option> --%>
							<%-- 									</c:forEach> --%>
							<!-- 								</select> -->
							<!-- 							</div> -->

							<!-- 							<div class="col-sm-2"> -->
							<!-- 								Distributor <select id="dbTerrtory" name="terrtoryPid" -->
							<!-- 									class="form-control"> -->
							<!-- 									<option value="no">All</option> -->
							<%-- 									<c:forEach items="${locations}" var="location"> --%>
							<%-- 										<option value="${location.pid}">${location.name}</option> --%>
							<%-- 									</c:forEach> --%>
							<!-- 								</select> -->
							<!-- 							</div> -->

							<!-- 							<div class="col-sm-2"> -->
							<!-- 								Route <select id="dbTerrtory" name="terrtoryPid" -->
							<!-- 									class="form-control"> -->
							<!-- 									<option value="no">All</option> -->
							<%-- 									<c:forEach items="${locations}" var="location"> --%>
							<%-- 										<option value="${location.pid}">${location.name}</option> --%>
							<%-- 									</c:forEach> --%>
							<!-- 								</select> -->
							<!-- 							</div> -->

							<div class="col-sm-2">
								Account <select id="dbAccount" name="accountPid"
									class="form-control">
									<option value="-1">All Account</option>
								</select>
							</div>

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
								Document<select id="dbDocument" name="document"
									class="form-control">
									<option value="no">All</option>
									<c:forEach items="${documents}" var="document">
										<option value="${document.pid}">${document.name}</option>
									</c:forEach>

								</select>
							</div>

							<div class="col-sm-2">
								Day <select class="form-control" id="dbDateSearch"
									onchange="TerritoryWiseSales.showDatePicker()">
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
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info" id="btnApply">Apply</button>
							</div>
							<div class="col-sm-1">
								<br>
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
						</div>
					</form>
				</div>
				<hr />
				<div class="col-md-12" style="font-size: 14px; color: black;">
					<div class="col-md-2">

						<label>Total Count : </label> <label id="lblCounts">0</label>

					</div>
					<div class="col-md-2">

						<label>Total Volume : </label> <label id="lblVolume">0</label>

					</div>
					<div class="col-md-2">

						<label>Total Amount : </label> <label id="lblAmount">0</label>

					</div>
				</div>
			</div>


			<div class="table-responsive">
				<table class="table table-striped table-bordered"
					id="tblSalesReport">
					<thead id="tHeadSalesReport">
					</thead>
					<tbody id="tBodySalesReport">
					</tbody>
				</table>
			</div>
			<!-- 			<div class="table-responsive"> -->


			<!-- 				<table class="table  table-striped table-bordered"> -->
			<!-- 					<thead> -->
			<!-- 						<tr> -->
			<!-- 							<th>Employee</th> -->
			<!-- 							<th>Receiver</th> -->
			<!-- 							<th>Document</th> -->
			<!-- 							<th>Amount -->
			<!-- 								<p id="totalDocument" style="float: right;"></p> -->
			<!-- 							</th> -->
			<!-- 							<th>Volume -->
			<!-- 								<p id="totalVolume" style="float: right;"></p> -->
			<!-- 							</th> -->
			<!-- 							<th>Total Quantity</th> -->
			<!-- 							<th>Date</th> -->
			<!-- 							<th>Management Status</th> -->
			<!-- 							<th>Status</th> -->
			<!-- 							<th>Action</th> -->
			<!-- 							<th>VisitRemarks</th> -->
			<!-- 						</tr> -->
			<!-- 					</thead> -->
			<!-- 					<tbody id="tBodyInventoryVoucher"> -->
			<!-- 					</tbody> -->
			<!-- 				</table> -->
			<!-- 			</div> -->
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/territory-wise-sales.js"
		var="territoryWiseSalesJs"></spring:url>
	<script type="text/javascript" src="${territoryWiseSalesJs}"></script>


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