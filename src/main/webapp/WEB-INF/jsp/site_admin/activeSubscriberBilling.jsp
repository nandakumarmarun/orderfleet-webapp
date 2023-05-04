<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Active Subscribers Billing Reports</title>
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
			<h2>Active Subscribers Billing Reports</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12  clearfix">
					<form role="form" class="form-horizontal">

						<div class="form-group">
							<div class="col-sm-2">

								<label class="control-label" for="field_billingPeriod">
									Select BillingPeriod </label> <select id="field_billingPeriod"
									name="billingPeriod" class="form-control">
									<option value="no">All</option>
									<option value="MONTHLY">MONTHLY(1 MONTH)</option>
									<option value="QUARTERLY">QUARTERLY(3 MONTHS)</option>
									<option value="HALF_YEARLY">HALF_YEARLY(6 MONTHS)</option>
									<option value="YEARLY">YEARLY(12 MONTHS)</option>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onclick="ActiveSubscriberBilling.showDatePicker()">

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
							<div class="input-group col-sm-2">
								<div class="col-sm-3">
									<br />
									<button type="button" class="btn btn-info entypo-search"
										style="font-size: 18px"
										onclick="ActiveSubscriberBilling.filter()" title="Apply"></button>
								</div>
								<div class="col-sm-3 ">
									<button id="btnDownloadxls" type="button" class="btn btn-info">Download
										Xls</button>
								</div>
							</div>
						</div>
					</form>

				</div>

			</div>
			<div class="table-responsive">
				<table class="collaptable table table-striped table-bordered"
					id="tblActiveBillingReport">
					<!--table header-->
					<thead>
						<tr>
						<th><input type="checkbox" id="selectAll" />&nbsp;&nbsp;Select
								All</th>
							<th>Company Name</th>
							<th>From Date</th>
							<th>To Date</th>
							<th>No Of Months</th>
							<th></th>

						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyActiveBillingReport">
					</tbody>
				</table>
			</div>
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
								<h4 class="modal-title" id="viewModalLabel">Account Profile</h4>
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
									<table class="table  table-striped table-bordered"
										id="tbAccountProfile">
										<th style="min-width: 80px;" id="lbl_CompanyName" colspan="2">please
											wait...</th>
										<tr>
											<td>Active Users</td>
											<td id="lbl_active_user"></td>
										</tr>
									</table>
									<div class="table-responsive">
										<table class="collaptable table  table-striped table-bordered"
											id="tblInvoiceWiseReport">
											<!--table header-->
											<thead>
												<tr>
													<th style="min-width: 80px;">SlabName</th>
													<th>SlabRate</th>
												</tr>
											</thead>
											<!--table header-->
											<tbody id="tbodyDetails">
											</tbody>
										</table>
									</div>
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
			<hr />


			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>



	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>



	<spring:url value="/resources/app/active-subscribers-billing.js"
		var="activeSubscriberBillingJs"></spring:url>
	<script type="text/javascript" src="${activeSubscriberBillingJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>


	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable">
	</spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
</body>
</html>