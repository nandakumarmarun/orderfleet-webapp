<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Visit Detail Reports</title>
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
		<jsp:include page="/WEB-INF/jsp/fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="/WEB-INF/jsp/fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Executive Day Wise Visit Detail Report </h2>
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
									<select id="dbEmployee" name="employeePid"
										class="form-control">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>
						
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="InvoiceWiseReport.showDatePicker()">
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
							<div class="input-group col-sm-2">
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
							</div>
						</div>
					</form>

				</div>

			</div>
			<div class="table-responsive">
				<table class="collaptable table  table-striped table-bordered"
					id="tblInvoiceWiseReport">
					<!--table header-->
					<thead>
						<tr>
							<th style="min-width: 80px;font-weight:bold">Employee</th>
							<th  style="font-weight:bold">Route (Attendance Remarks)</th>
							<th  style="font-weight:bold">Attendance marked Time</th>
							
						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyInvoiceWiseReport">
					</tbody>
				</table>
			</div>


			<hr />

			
			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/executive-task-executions"
				var="urlInvoiceWiseReports"></spring:url>
		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<!-- tableExport.jquery.plugin -->
	<spring:url value="/resources/assets/js/tableexport/xlsx.core.min.js"
		var="jsXlsx"></spring:url>
	<spring:url value="/resources/assets/js/tableexport/FileSaver.min.js"
		var="fileSaver"></spring:url>
<%-- 	<spring:url value="/resources/assets/js/tableexport/tableexport.min.js" --%>
<%-- 		var="tableExport"></spring:url> --%>
	<script type="text/javascript" src="${jsXlsx}"></script>
	<script type="text/javascript" src="${fileSaver}"></script>
<%-- 	<script type="text/javascript" src="${tableExport}"></script> --%>
	
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/app/visit-detail-report.js"
		var="visitDetailReportJs"></spring:url>
	<script type="text/javascript" src="${visitDetailReportJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
	<script type="text/javascript">
		var interimSave = "${interimSave}";
		var distanceTarvel = "${distanceTarvel}";
		// call from dash board
		$(document)
				.ready(
						function() {

							var employeePid = getParameterByName('user-key-pid');
							getEmployees(employeePid);
							var documentType = getParameterByName('document-name');
							if (documentType != null) {
								$(
										"#dbDocument option:contains('"
												+ documentType + "')").attr(
										'selected', 'selected');
							}
							$("#txtToDate").datepicker({
								dateFormat : "dd-mm-yy"
							});
							$("#txtFromDate").datepicker({
								dateFormat : "dd-mm-yy"
							});

							$('#myFormSubmit').on('click', function() {
								InvoiceWiseReport.reject();
							});
							$('#btnSaveNewGeoLocation').on('click', function() {
								InvoiceWiseReport.saveNewGeoLocation();
							});

							$('#btnSearch').click(
									function() {
										InvoiceWiseReport.searchTable($(
												"#search").val(),
												$('#tbodyAccountProfile'));
									});

							$('#newAccountProfile').click(function() {
								InvoiceWiseReport.newAccount();
							});

							$('#changeAccount').click(function() {
								InvoiceWiseReport.changeAccount();
							});
							$('#return').click(function() {
								InvoiceWiseReport.oldAccount();
							});

							$('#field_dynamicDocument').change(function() {
								InvoiceWiseReport.getForms();
							});

							$('#createAndChangeAccount').click(function() {
								InvoiceWiseReport.createAndChangeAccount();
							});

							$('#loadAccountProfile').click(function() {
								InvoiceWiseReport.loadAccountFromForm();
							});

							$('#btnDownload')
									.on(
											'click',
											function() {
												var tblInvoiceWiseReport = $("#tblInvoiceWiseReport tbody");
												if (tblInvoiceWiseReport
														.children().length == 0) {
													alert("no values available");
													return;
												}
												if (tblInvoiceWiseReport[0].textContent == "No data available") {
													alert("no values available");
													return;
												}
												InvoiceWiseReport.downloadXls();
											});

							InvoiceWiseReport.filter();
							//if(documentType == null){
							//execute on normal page load

							//}
						});
	</script>

</body>
</html>