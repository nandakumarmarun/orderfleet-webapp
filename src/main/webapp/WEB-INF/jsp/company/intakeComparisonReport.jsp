<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<title>SalesNrich | Intake Comparison Report</title>
<style type="text/css">
.ui-datepicker-calendar {
	display: none;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Intake Comparison Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<input type="text" class="form-control" id="txtFromDate"
									placeholder="Select From Month" />
							</div>
							<div class="col-sm-2">
								<input type="text" class="form-control" id="txtToDate"
									placeholder="Select To Month" />
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="IntakeComparison.filter()">Apply</button>
							</div>
							<div class="col-sm-7">
								<button type="button" class="btn btn-info" id="btnSearch"
									style="float: right;">Search</button>
								<input type="text" id="search" placeholder="Search..."
									class="form-control" style="width: 200px; float: right;">
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead id="tHeadIntakeComparison">

					</thead>
					<tbody id="tBodyIntakeComparison">
						<tr>
							<td align="center">No data available</td>
						</tr>
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/intake-comparison-report.js"
		var="intakeComparisonJs"></spring:url>
	<script type="text/javascript" src="${intakeComparisonJs}"></script>

	<script type="text/javascript">
		$(document).ready(
				function() {
					$("#txtFromDate").datepicker(
							{
								changeMonth : true,
								changeYear : true,
								dateFormat : 'MM,yy',
								onClose : function(dateText, inst) {
									$(this).datepicker(
											'setDate',
											new Date(inst.selectedYear,
													inst.selectedMonth, 1));
								}
							});
					$("#txtToDate").datepicker(
							{
								changeMonth : true,
								changeYear : true,
								dateFormat : 'MM,yy',
								onClose : function(dateText, inst) {
									$(this).datepicker(
											'setDate',
											new Date(inst.selectedYear,
													inst.selectedMonth, 1));
								}
							});
				});
	</script>
</body>
</html>