<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Invoice Wise Lead status</title>
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
			<h2>LeadStatus </h2>
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
                        			<option value="no">All employee</option>
                        				<c:forEach items="${employees}" var="employee">
                             	<option value="${employee.pid}">${employee.name}</option>
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
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="LeadStatusAnalytics.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
                                    <option value="MTD">MTD</option>
									<option value="SINGLE">Single Date</option>
									<option value="CUSTOM">CUSTOM(Max 3 Months)</option>

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
							<div class="input-group col-sm-6">
								<div class="col-sm-3">
									<br />
									<button type="button" class="btn btn-info entypo-search"
										style="font-size: 18px" onclick="LeadStatusAnalytics.filter()"
										title="Apply"></button>
								</div>
								<br>
								 <div class="col-sm-3">
                              	<button type="button" class="btn btn-success" id="downloadXls">Download</button>
                               	</div>

                                <div class="col-sm-3">
	<button type="button" class="btn btn-success" id="downloadRawXls" onclick="LeadStatusAnalytics.filterRawData()">Download RawData</button>
                               	</div>

							</div>
						</div>
					</form>

				</div>


			</div>
			<div class="table-responsive">
				<table class="collaptable table  table-striped table-bordered"
					id="tblLeadStatusAnalytics">
					<!--table header-->
					<thead>
						<tr>
							<th style="min-width: 80px;">Employee</th>
							<th>Account Profile</th>
							<th>CreatedDate</th>
							<th>CreatedTime</th>
							<th>Document Name</th>
							<th>Lead Status</th>
                            <th>Deal Volume</th>
                            <th>Won Volume</th>
                            <th>Lost Volume</th>
                            <th>Balance Deal Volume</th>
						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyLeadStatusAnalytics">
					</tbody>
				</table>
			</div>
		<hr />
	<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/lead-status-analytics"
				var="urlLeadStatusAnalytics"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
    <script type="text/javascript" src="${jsXlsx}"></script>
	<script type="text/javascript" src="${fileSaver}"></script>


	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url value="/resources/app/lead-status-analytics.js"
		var="leadStatusAnalyticsJS"></spring:url>
	<script type="text/javascript" src="${leadStatusAnalyticsJS}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>
	<spring:url value="/resources/assets/js/tableexport/xlsx.core.min.js"
    		var="jsXlsx"></spring:url>
    	<spring:url value="/resources/assets/js/tableexport/FileSaver.min.js"
    		var="fileSaver"></spring:url>
<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<script type="text/javascript">

		// call from dash board
		$(document)
				.ready(
						function() {
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