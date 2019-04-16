<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Company Wise Unable Find Locations</title>
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
			<h2>Company Wise Unable Find Locations</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<select class="form-control" id="dbDateSearch"
									onchange="UnableFindLocations.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
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
								<button type="button" class="btn btn-info"
									onclick="UnableFindLocations.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>

				<div class="col-md-12 col-sm-12 clearfix">
					<div class="col-sm-3">
						<label>Total Count : </label>&nbsp;&nbsp;&nbsp;<label
							id="lblTotalCount" style="font-size: large;">0</label>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12 clearfix">
					<div class="pull-right">
						<input class="btn btn-orange" type="button" id="btnLoadLocation"
							value="update" />
					</div>
				</div>
			</div>
			<br />
			<div class="table-responsive">

				<table class='table table-striped table-bordered'>
					<thead>
						<tr>
							<th>Location Type</th>
							<th>latitude</th>
							<th>longitude</th>
							<th>location</th>
						</tr>
					</thead>
					<tbody id="tb_unableLoc">
					</tbody>
				</table>

			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/accounting-vouchers"
				var="urlUnableFindLocations"></spring:url>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/company-wise-unable-find-location.js"
		var="accountingVoucherJs"></spring:url>
	<script type="text/javascript" src="${accountingVoucherJs}"></script>

</body>
</html>