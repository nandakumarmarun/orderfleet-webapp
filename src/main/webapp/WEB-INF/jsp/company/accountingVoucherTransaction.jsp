<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
			<h2>Accounting Voucher</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="form-group">
					<div class="col-sm-2">
						Employee <select id="dbEmployee" class="form-control">
							<option value="no">Select Employee</option>
							<c:forEach items="${employees}" var="employee">
								<option value="${employee.pid}">${employee.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						Document <select id="dbDocument" class="form-control">
							<option value="no">Select Document</option>
							<c:forEach items="${activityDocuments}" var="document">
								<option value="${document.pid}">${document.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						Account <select id="dbAccount" class="form-control">
							<option value="no">All Account</option>
						</select>
					</div>
					<div class="col-sm-4">
						<br />
						<button type="button" class="btn btn-orange" style="width: 65px;"
							onclick="AccountingVoucherTransaction.showAccountingForm()">New</button>
						<button type="button" class="btn btn-info"
							onclick="AccountingVoucherTransaction.search()">Search</button>
							<button type="button" class="btn btn-green"
							onclick="AccountingVoucherTransaction.returnToTransaction()">Return To Txn</button>
						<!-- <button type="button" class="btn btn-primary"
							onclick="AccountingVoucherTransaction.showReferenceDocuments()">Reference
							Documents</button> -->
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div id="divAccounting" class="row" style="display: none;">

				<div class="col-md-12">
					<div class="col-sm-4">
						Previous Document Number : <label id="divPreviousDocumentNumber"
							style="font-weight: bold;">---</label>
					</div>
					<div class="col-sm-4">
						Document Name : <label id="divDocumentName"
							style="font-weight: bold;">---</label>
					</div>
					<div class="col-sm-4">
						Document Number : <label id="divDocumentNumber"
							style="font-weight: bold;">---</label>
					</div>
				</div>
				<div class="clearfix"></div>
				<br>
				<div class="col-md-12"></div>
				<div class="clearfix"></div>
				<hr />

				<div class="col-md-12">
					<button type="button" class="btn btn-info pull-right"
						data-dismiss="modal"
						onclick="AccountingVoucherTransaction.showAccountingModal()">Add</button>
				</div>

				<!-- Accounting voucher form -->
				<div id="divAccountingForm" class="col-md-12"
					style="min-height: 250px;">
					<br />
					<div class="table-responsive">
						<table id="tblAccountingDetails"
							class="table  table-striped table-bordered">
							<thead>
								<tr>
									<th>Amount</th>
									<th>Mode</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody id="tbodyAccountingDetails">
								<tr>
									<td colspan="6">No data avilable</td>
								</tr>
							</tbody>
						</table>
					</div>

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
									<h4 class="modal-title" id="myModalLabel">Details</h4>
								</div>
								<div class="modal-body" style="height: 500px; overflow: auto;">
									<input type="hidden" id="hdnIndex" />
									<div class="form-group" id="divBY">
										<label class="control-label" for="BY">By</label> <select
											id="BY" class="form-control">
											<option value="no">Select BY</option>
										</select>
									</div>
									<div class="form-group" id="divTO">
										<label class="control-label" for="BY">To</label> <select
											id="TO" class="form-control">
											<option value="no">Select TO</option>
										</select>
									</div>
									<div class="form-group" id="divOUTSTANDING_AMOUNT">
										<label class="control-label" for="OUTSTANDING_AMOUNT">Outstanding
											Amount</label> <input autofocus="autofocus" type="number"
											class="form-control" id="OUTSTANDING_AMOUNT" maxlength="10"
											placeholder="Outstanding Amount" min="1" />
									</div>
									<div class="form-group" id="divTYPE">
										<label class="control-label" for="TYPE">Mode</label> <select
											id="TYPE" class="form-control">
											<option value="Cash">Cash</option>
											<option value="Bank">Bank</option>
										</select>
									</div>
									<div class="form-group" id="divAMOUNT">
										<label class="control-label" for="AMOUNT">Amount</label> <input
											autofocus="autofocus" type="number" class="form-control"
											id="AMOUNT" maxlength="10" placeholder="Amount" min="1" />
									</div>
									<div class="form-group" id="divBANK_NAME">
										<label class="control-label" for="BANK_NAME">BANK NAME</label>
										<input type="text" class="form-control" id="BANK_NAME"
											maxlength="50" placeholder="BANK NAME" />
									</div>
									<div class="form-group" id="divCHEQUE_DATE">
										<label class="control-label" for="CHEQUE_DATE">Cheque
											Date</label> <input type="date" class="form-control" id="CHEQUE_DATE"
											maxlength="15" placeholder="Cheque Date" />
									</div>
									<div class="form-group" id="divCHEQUE_NO">
										<label class="control-label" for="CHEQUE_NO">Cheque No</label>
										<input type="text" class="form-control" id="CHEQUE_NO"
											maxlength="50" placeholder="Cheque No" />
									</div>
									<div class="form-group" id="divREFERENCE_NUMBER">
										<label class="control-label" for="REFERENCE_NUMBER">Reference
											Number</label> <input type="number" class="form-control"
											id="REFERENCE_NUMBER" maxlength="50"
											placeholder="Reference Number" />
									</div>

									<div class="form-group" id="divFROM">
										<label class="control-label" for="FROM">From</label> <input
											type="text" class="form-control" id="FROM" maxlength="5"
											placeholder="From" />
									</div>
									<div class="form-group" id="divINCOME_EXPENSE_HEAD">
										<label class="control-label" for="INCOME_EXPENSE_HEAD">Incomx
											Expense Head</label> <select id="INCOME_EXPENSE_HEAD"
											class="form-control">
											<option value="no">Select Income Expense Head</option>
											<c:forEach items="${incomeExpenseHeads}"
												var="incomeExpenseHead">
												<option value="${incomeExpenseHead.pid}">${incomeExpenseHead.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group" id="divREMARKS">
										<label class="control-label" for="REMARKS">REMARKS</label>
										<textarea class="form-control" id="REMARKS" maxlength="200"
											placeholder="REMARKS"></textarea>
									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button
										onclick="AccountingVoucherTransaction.addToAccountingDetails()"
										class="btn btn-primary">Save</button>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal-dialog -->
					</div>
					<!-- /.Model Container-->
				</div>

				<!-- error alert -->
				<div class="col-md-12">
					<div class="alert alert-danger alert-dismissible" role="alert"
						style="display: none;">
						<button type="button" class="close" onclick="$('.alert').hide();"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<p></p>
					</div>
				</div>

				<!-- Submit button -->
				<div class="col-md-12">
					<div class="form-group">
						<div class="col-sm-offset-11 col-sm-1"
							style="margin-bottom: 17px;">
							<button type="button" id="btnSubmitAccountingVoucher"
								class="btn btn-default btn-success">Submit</button>
						</div>
					</div>
				</div>

			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<!-- Model Container-->
			<div class="modal fade container" id="searchModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Accounting
								Vouchers</h4>
						</div>
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
							<label>Document :</label>&nbsp; <label id="lblDocument"
								style="color: black; font-weight: bold;"></label> &nbsp;&nbsp; <label>Account
								:</label>&nbsp; <label id="lblAccount"
								style="color: black; font-weight: bold;"></label>
							<div class="table-responsive">
								<table class="collaptable table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Date</th>
											<th>Document</th>
											<th>Document Number</th>
											<!-- <th>Action</th> -->
										</tr>
									</thead>
									<tbody id="tblAccountingVouchers">

									</tbody>
								</table>
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

			<!-- Model Container-->
			<div class="modal fade container" id="allocationModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 800px;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Allocation</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible alert-batch"
								role="alert" style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<input type="hidden" id="hdnAlloIndex"> <label>Amount
								:</label>&nbsp; <label id="lblAlloAmount"
								style="color: black; font-weight: bold;"></label> &nbsp; <label>Balance
								Amount :</label>&nbsp; <label id="lblAlloBalAmount"
								style="color: black; font-weight: bold;"></label>
							<div class="table-responsive">
								<table class="table  table-striped table-bordered">
									<thead>
										<tr>
											<th>V Number</th>
											<th>V Date</th>
											<th>Bill Over Due</th>
											<th>Balance Amount</th>
											<th>Allocated</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody id="tblAllocations">
										<tr>
											<td colspan="6">No data available</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">OK</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<!-- Model Container-->
			<div class="modal fade container" id="referenceDocumentModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Reference
								Document</h4>
						</div>
						<div class="modal-body">
							<div class="form-group">
								<label class="control-label">Reference Document</label> <select
									id="dbReferenceDocument" class="form-control">
									<option value="no">Select Reference Document</option>
								</select>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-info" data-dismiss="modal"
								onclick="AccountingVoucherTransaction.searchByReferenceDocument()">Search</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/app/accounting-voucher-transaction.js"
		var="accountingVoucherTransactionJs"></spring:url>
	<script type="text/javascript" src="${accountingVoucherTransactionJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<spring:url value="/resources/assets/js/select2/select2.min.js"
		var="select2Js"></spring:url>
	<script type="text/javascript" src="${select2Js}"></script>
</body>
</html>