<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Task Execution</title>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Task Execution</h2>
			<div class="row col-xs-12">
				<div class="pull-right"></div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<!-- executive task plans -->
			<div id="executiveTaskPlans">
				<div class="row col-xs-12">
					<h4 style="font-weight: bold;">Task Plans</h4>
				</div>
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Activity</th>
							<th>Customer</th>
							<th>Remarks</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${executiveTaskPlans}" var="executiveTaskPlan"
							varStatus="loopStatus">
							<tr>
								<td>${executiveTaskPlan.activityName}</td>
								<td>${executiveTaskPlan.accountProfileName}</td>
								<td>${executiveTaskPlan.remarks}</td>
								<td>
									<button type="button" class="btn btn-blue"
										onclick="TaskExecution.showDocuments('${executiveTaskPlan.activityPid}','${executiveTaskPlan.accountProfilePid}');">Select</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>

			<!-- user documents -->
			<div id="userDocuments" style="display: none;">
				<div class="row col-xs-12">
					<div class="pull-right">
						<button type="button" class="btn"
							onclick="TaskExecution.back('userDocuments');">Back</button>
					</div>
					<h4 style="font-weight: bold;">Documents</h4>
				</div>
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Document</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tblDocuments">
					</tbody>
				</table>
			</div>

			<!-- inventory voucher form -->
			<div id="inventoryVoucherForm" style="display: none;">
				<div class="row col-xs-12">
					<div class="pull-right">
						<button type="button" class="btn"
							onclick="TaskExecution.back('inventoryVoucherForm');">Back</button>
					</div>
					<h4 style="font-weight: bold;">Inventory Voucher Form</h4>
				</div>
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Product</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tblProducts">

					</tbody>
				</table>


				<!-- Model Container-->
				<div class="modal fade container" id="inventoryVoucherModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">
									Product : <label id="lblProductName"></label>
								</h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
								<input type="hidden" id="hdnDocumentPid" /> <input
									type="hidden" id="hdnProductPid" />
								<div class="form-group" id="divQUANTITY" style="display: none;">
									<label class="control-label" for="QUANTITY">Quantity</label> <input
										type="text" class="form-control" id="QUANTITY" maxlength="5"
										placeholder="Quantity" />
								</div>
								<div class="form-group" id="divMRP" style="display: none;">
									<label class="control-label" for="MRP">MRP</label> <input
										type="text" class="form-control" id="MRP" maxlength="5"
										placeholder="MRP" />
								</div>
								<div class="form-group" id="divSELLING_RATE"
									style="display: none;">
									<label class="control-label" for="SELLING_RATE">Selling
										Rate</label> <input type="text" class="form-control" id="SELLING_RATE"
										maxlength="5" placeholder="Selling Rate" />
								</div>
								<div class="form-group" id="divSTOCK" style="display: none;">
									<label class="control-label" for="STOCK">Stock</label> <input
										type="text" class="form-control" id="STOCK" maxlength="10"
										placeholder="Stock" />
								</div>
								<div class="form-group" id="divFREE_QUANTITY"
									style="display: none;">
									<label class="control-label" for="FREE_QUANTITY">Free
										Quantity</label> <input type="text" class="form-control"
										id="FREE_QUANTITY" maxlength="5" placeholder="Free Quantity" />
								</div>
								<div class="form-group" id="divDISCOUNT_PERCENTAGE"
									style="display: none;">
									<label class="control-label" for="DISCOUNT_PERCENTAGE">Discount
										Percentage</label> <input type="text" class="form-control"
										id="DISCOUNT_PERCENTAGE" maxlength="5"
										placeholder="Discount Percentage" />
								</div>
								<div class="form-group" id="divDISCOUNT_AMOUNT"
									style="display: none;">
									<label class="control-label" for="DISCOUNT_AMOUNT">Discount
										Amount</label> <input type="text" class="form-control"
										id="DISCOUNT_AMOUNT" maxlength="10"
										placeholder="Discount Amount" />
								</div>
								<div class="form-group" id="divTAX_AMOUNT"
									style="display: none;">
									<label class="control-label" for="TAX_AMOUNT">Tax
										Amount</label> <input type="text" class="form-control" id="TAX_AMOUNT"
										maxlength="10" placeholder="Tax Amount" />
								</div>
								<div class="form-group" id="divVOLUME" style="display: none;">
									<label class="control-label" for="VOLUME">Volume</label> <input
										type="text" class="form-control" id="VOLUME" maxlength="5"
										placeholder="Volume" />
								</div>
								<div class="form-group" id="divOFFER_STRING"
									style="display: none;">
									<label class="control-label" for="OFFER_STRING">Offer
										String</label> <input type="text" class="form-control"
										id="OFFER_STRING" maxlength="5" placeholder="Offer String" />
								</div>
								<div class="form-group" id="divREMARKS" style="display: none;">
									<label class="control-label" for="REMARKS">REMARKS</label>
									<textarea class="form-control" id="REMARKS" maxlength="200"
										placeholder="REMARKS"></textarea>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button onclick="TaskExecution.addToInventoryDetails()"
										class="btn btn-primary">Save</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</div>

			<!-- accounting voucher form -->
			<div id="accountingVoucherForm" style="display: none;">
				<div class="row col-xs-12">
					<div class="pull-right">
						<button type="button" class="btn"
							onclick="TaskExecution.back('accountingVoucherForm');">Back</button>
					</div>
					<h4 style="font-weight: bold;">Accounting Voucher Form</h4>
				</div>
				<br />
				<div class="col-md-6">
					<input type="hidden" id="hdnAccountingDocumentPid" />
					<div class="form-group" id="divAMOUNT" style="display: none;">
						<label class="control-label" for="AMOUNT">Amount</label> <input
							type="text" class="form-control" id="AMOUNT" maxlength="5"
							placeholder="Amount" />
					</div>
					<div class="form-group" id="divTYPE" style="display: none;">
						<label class="control-label" for="TYPE">Type</label> <select
							id="TYPE" class="form-control">
							<option value="Cash">Cash</option>
							<option value="Bank">Bank</option>
						</select>
					</div>
					<div class="form-group" id="divBANK_NAME" style="display: none;">
						<label class="control-label" for="BANK_NAME">Bank Name</label> <input
							type="text" class="form-control" id="BANK_NAME" maxlength="5"
							placeholder="Bank Name" />
					</div>
					<div class="form-group" id="divCHEQUE_DATE" style="display: none;">
						<label class="control-label" for="CHEQUE_DATE">Cheque Date</label>
						<input type="text" class="form-control" id="CHEQUE_DATE"
							maxlength="5" placeholder="Cheque Date" />
					</div>
					<div class="form-group" id="divCHEQUE_NO" style="display: none;">
						<label class="control-label" for="CHEQUE_NO">Cheque NO</label> <input
							type="text" class="form-control" id="CHEQUE_NO" maxlength="10"
							placeholder="Cheque NO" />
					</div>
				</div>
				<div class="col-md-6">
					<div class="form-group" id="divBY" style="display: none;">
						<label class="control-label" for="BY">From</label> <select id="BY"
							class="form-control">
							<option value="">Select From Account</option>
							<c:forEach items="${byAccounts}" var="account">
								<option value="${account.pid}">${account.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group" id="divTO" style="display: none;">
						<label class="control-label" for="TO">To</label> <select id="TO"
							class="form-control">
							<option value="">Select To Account</option>
							<c:forEach items="${toAccounts}" var="account">
								<option value="${account.pid}">${account.name}</option>
							</c:forEach>
						</select>
					</div>
					<div id="divAccountingBtn">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
						<button onclick="TaskExecution.addToAccountingDetails()"
							class="btn btn-primary">Save</button>
					</div>
				</div>
			</div>

			<!-- Dynamic  forms -->
			<div id="dynamicForms" style="display: none;">
				<div class="row col-xs-12">
					<div class="pull-right">
						<button type="button" class="btn"
							onclick="TaskExecution.back('dynamicForms');">Back</button>
					</div>
					<h4 style="font-weight: bold;">Dynamic Form</h4>
				</div>

			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/task-execution" var="urlTaskExecution"></spring:url>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/task-execution.js"
		var="taskExecutionJs"></spring:url>
	<script type="text/javascript" src="${taskExecutionJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$("#CHEQUE_DATE").datepicker({
				dateFormat : 'yy-mm-dd'
			});
		});
	</script>
</body>
</html>