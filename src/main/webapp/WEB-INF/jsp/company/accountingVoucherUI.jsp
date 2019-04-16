<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>

<html lang="en">
<head>
<title>SalesNrich | Accounting Voucher</title>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui-1.11.4.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<!-- Imported styles on this page -->
<spring:url value="/resources/assets/js/select2/select2-bootstrap.css"
	var="select2bootstrapCss"></spring:url>
<link href="${select2bootstrapCss}" rel="stylesheet">
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<spring:url value="/resources/assets/js/select2/select2.css"
	var="select2Css"></spring:url>
<link href="${select2Css}" rel="stylesheet">

<style type="text/css">
#select2-drop {
	z-index: 10001
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		//select current user employee
		$("#dbEmployee").val("${currentEmployeePid}");

	});
</script>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2 id="titleName">Accounting Voucher UI</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="form-group">
					<div class="col-sm-3">
						Activity<select id="field_activity" name="activity"
							class="form-control"
							onchange="AccountingVoucherUI.onChangeActivity();">
							<option value="-1">Select Activity</option>
							<c:forEach items="${activities}" var="activity">
								<option value="${activity.pid}">${activity.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						Document <select id="dbDocument" class="form-control"
							onchange="AccountingVoucherUI.loadBuyToAccount();">
							<option value="no">Select Document</option>

						</select>
					</div>
					<div class="col-sm-3">
						Payment Mode <select id="field_paymentMode" name="paymentMode"
							class="form-control">
							<option value="-1">Select Payment Mode</option>
							<c:forEach var="paymentMode" items="${paymentModes}">
								<option value="${paymentMode}">${paymentMode}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						By<select id="field_byaccount" name="buyaccount"
							class="form-control">
							<option value="-1">Select</option>
						</select>
					</div>
					<div class="col-sm-3">
						To<select id="field_toaccount" name="toaccount"
							class="form-control">
							<option value="-1">Select</option>
						</select>
					</div>
					<div class="col-sm-3">
						<br />
						<button type="button" class="btn btn-blue" style="width: 65px;"
							onclick="AccountingVoucherUI.showModal()">Add</button>
						<button type="button" class="btn btn-orange" style="width: 65px;"
							onclick="AccountingVoucherUI.loadAccountVoucher()">Search</button>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table table-bordered" id="accountingDetailView">
				<thead>
					<tr>
						<th>Date</th>
						<th>Amount</th>
						<th>Remarks</th>
					</tr>
				</thead>
				<tbody id="tblAccountingDetail"></tbody>
			</table>

			<spring:url value="/web/accounting-voucher-ui"
				var="urlAccountingVoucherUI"></spring:url>

			<form id="accountingVoucherUIForm" role="form" method="post"
				action="${urlAccountingVoucherUI}">
				<!-- Model Container-->
				<div class="modal fade container" id="accountingVoucherModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Add</h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
								<input type="hidden" id="hdnIndex" />
								<div class="form-group" id="divAMOUNT">
									<label class="control-label" for="AMOUNT">Amount</label> <input
										autofocus="autofocus" type="number" class="form-control"
										id="amount" maxlength="10" placeholder="Amount" min="1" />
								</div>
								<div class="form-group" id="divDATE">
									<label class="control-label" for="DATE"> Date</label> <input
										type="text" class="form-control" id="date" maxlength="15"
										placeholder="Date" />
								</div>

								<div class="form-group" id="divREMARKS">
									<label class="control-label" for="REMARKS">Remarks</label> <input
										class="form-control" id="remarks" maxlength="100"
										placeholder="REMARKS">
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="myFormSubmit" class="btn btn-primary">Save</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>
			
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/app/accounting-voucher-ui.js"
		var="accountingVoucherUIJs"></spring:url>
	<script type="text/javascript" src="${accountingVoucherUIJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
</body>
</html>