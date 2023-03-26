<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Company</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Company Billing Setting</h2>
			<br>
			<div class="row col-xs-12">


				<div class="pull-left">
					<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="searchCompany" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearchCompany" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
				</div>
				<!-- 	<div class="col-sm-4">
					<br />
					<button id="btnDownload" type="button" class="btn btn-success">
						Download Xls</button>
				</div> -->
				<div class="col-sm-4 pull-right">
					<button type="button" class="btn btn-success"
						onclick="BillingSetting.showModalPopup($('#myModal'));">Create
						Company Biiling</button>
				</div>

			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered" id="tblCompany">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Billing Period</th>
						<th>No Of Months</th>
						<th>Last Billed Date(From)</th>
						<th>Next Billed Date(To)</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyCompany">
					<c:forEach items="${billList}" var="bill" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${bill.company.legalName}</td>
							<td>${bill.billingPeriod }</td>
							<td>${bill.noOfMonths}</td>
							<td>${bill.lastBilledDate}</td>
							<td>${bill.next_bill_date}</td>
							<td>
								 <button type="button" class="btn btn-blue"
									onclick="BillingSetting.showModalPopup($('#editModal'),'${bill.pid}',1);">Edit</button>   
								<button type="button" class="btn btn-blue"
									onclick="BillingSetting.changeBillDate('${bill.company.pid}');">Change
									bill Date</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/billing-setting" var="urlBillingSetting"></spring:url>
			<form id="companyForm" role="form" method="post"
				action="${urlBillingSetting}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create
									Company Billing Setting</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<div class="modal-body" style="overflow: auto; height: 60%;">
									<div class="form-group">
										<select id="dbCompany" name="companyPid"
											class="form-control selectpicker" data-live-search="true"><option
												value="-1">Select Company</option>
											<c:forEach items="${companies}" var="company">
												<option value="${company.pid}">${company.legalName}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_billingPeriod">BillingPeriod
										</label> <select id="field_billingPeriod" name="billingPeriod"
											class="form-control">
											<option value="-1">Select Billing Duration</option>
											<option value="MONTHLY">MONTHLY(1 MONTH)</option>
											<option value="QUARTERLY">QUARTERLY(3 MONTHS)</option>
											<option value="HALF_YEARLY">HALF_YEARLY(6 MONTHS)</option>
											<option value="YEARLY">YEARLY(12 MONTHS)</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_noOfMonth">No
											Of Months</label> <input type="text" class="form-control"
											name="noOfMonth" id="field_noOfMonth" maxlength="55" />
									</div>
									<div class="form-group">
										<label class="control-label">Last Billed Date(From)</label> <input
											id="txtFromDate" type="text" class="form-control"
											value="${billingSetting.lastBillDate}" />
									</div>
									<div class="form-group">
										<label class="control-label">Next Billed Date(To)</label> <input
											id="txtToDate" type="text" class="form-control"
											value="${billingSetting.dueBillDate}" />
									</div>


								</div>

							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="editForm" role="form" method="post"
				action="${urlBillingSetting}">
				<!-- Model Container-->
				<div class="modal fade container" id="editModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="editModalLabel">Edit
									Company Billing Setting</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<div class="modal-body" style="overflow: auto; height: 60%;">
									<div class="form-group">
										
										<div class="form-group">
										<label class="control-label" for="dbCompanys">CompanyName
											</label> <input type="text" class="form-control"
											name="companyName" id="dbCompanys" maxlength="55" />
									</div>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_billingPeriod">BillingPeriod
										</label> <select id="field_billingPeriods" name="billingPeriod"
											class="form-control">
											<option value="-1">Select Billing Duration</option>
											<option value="MONTHLY">MONTHLY(1 MONTH)</option>
											<option value="QUARTERLY">QUARTERLY(3 MONTHS)</option>
											<option value="HALF_YEARLY">HALF_YEARLY(6 MONTHS)</option>
											<option value="YEARLY">YEARLY(12 MONTHS)</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_noOfMonth">No
											Of Months</label> <input type="text" class="form-control"
											name="noOfMonth" id="field_noOfMonths" maxlength="55" />
									</div>
									<div class="form-group">
										<label class="control-label">Last Billed Date(From)</label> <input
											id="txtFromDates" type="text" class="form-control"
											value="${billingSetting.lastBillDate}" />
									</div>
									<div class="form-group">
										<label class="control-label">Next Billed Date(To)</label> <input
											id="txtToDates" type="text" class="form-control"
											value="${billingSetting.dueBillDate}" />
									</div>


								</div>

							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
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

	<spring:url value="/resources/app/billing-setting.js"
		var="billingSettingJs"></spring:url>
	<script type="text/javascript" src="${billingSettingJs}"></script>

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

</body>
</html>