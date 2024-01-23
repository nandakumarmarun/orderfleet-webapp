<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Invoice Wise Reports</title>
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
			<h2>Transactions with Time Difference </h2>
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
						<div class="col-sm-2">
                        		Employee<select id="dbEmployee" name="employeePid"
                        			class="form-control">
                        			<option value="no">select employee</option>
                        				<c:forEach items="${employees}" var="employee">
                             	<option value="${employee.pid}">${employee.name}</option>
                        				</c:forEach>
                        		</select>
                      	</div>

							<div class="col-sm-2">
								Document <select id="dbDocument" name="documentPid"
									class="form-control">
									<option value="no">All Document</option>
									<c:forEach items="${documents}" var="document">
										<option value="${document.pid}">${document.name}</option>
									</c:forEach>
								</select>
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
								Activity
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
												onclick='InvoiceWiseReport.getActivities("all")'>All
												Activity</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='InvoiceWiseReport.getActivities("planed")'>Planned
												Activity</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='InvoiceWiseReport.getActivities("unPlaned")'>UnPlanned
												Activity</a>
										</div>

									</div>
									<select id="dbActivity" name="employeePid"
										class="form-control">
										<option value="no">All Activity</option>
										<c:forEach items="${activities}" var="activity">
											<option value="${activity.pid}">${activity.name}</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="InvoiceTimeDiff.showDatePicker()">
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
										style="font-size: 18px" onclick="InvoiceTimeDiff.filter()"
										title="Apply"></button>
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
							<th style="min-width: 80px;">Employee</th>
							<th>Account Profile</th>
							<th>Activity</th>
							<th>Punch In</th>
							<th>Client Date</th>
							<th>Time b\w Transaction</th>
							<th>Time Spend (hh:mm:ss:mmm)</th>
							<th>Server Date</th>
							<th>Enable/ Disable</th>

							<th>Remarks</th>
						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyInvoiceWiseReport">
					</tbody>
				</table>
			</div>
		<hr />
	<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/invoice-time-diff"
				var="urlInvoiceTimeDiff"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
    <script type="text/javascript" src="${jsXlsx}"></script>
	<script type="text/javascript" src="${fileSaver}"></script>


	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/app/invoice-time-diff.js"
		var="invoiceTimeDiffJS"></spring:url>
	<script type="text/javascript" src="${invoiceTimeDiffJS}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<script type="text/javascript">

		// call from dash board
		$(document)
				.ready(
						function() {

							var documentType = getParameterByName('document-name');
							if (documentType != null) {
								$(
										"#dbDocument option:contains('"
												+ documentType + "')").attr(
										'selected', 'selected');
							}
						<!--	Set the date selection limit to 3 months-->
							$("#txtToDate").datepicker({
								 dateFormat: 'mm-dd-yy',
                                            minDate: 0,
                                onSelect: function(selectedDate) {
                                var toDate = new Date(selectedDate);
                                var fromDate = new Date(toDate);
                                fromDate.setMonth(toDate.getMonth() - 3);
                                $("#txtFromDate").datepicker("option", "minDate", fromDate);
                                $("#txtFromDate").datepicker("option", "maxDate",toDate);

                                                                                        }
							});
							$("#txtFromDate").datepicker({
								dateFormat : "mm-dd-yy",
								onSelect: function(selectedDate) {

                                                var fromDate = new Date(selectedDate);

                                                var toDate = new Date(fromDate);
                                                toDate.setMonth(toDate.getMonth() + 3);
                                            $("#txtToDate").datepicker("option", "minDate", fromDate);
                                                $("#txtToDate").datepicker("option", "maxDate",toDate);

                                            }
							});
	});
	</script>

</body>
</html>