<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Receivable Payables</title>
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
			<h2>Receivable Payables</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-4">
								<select id="dbAccount" name="accountPid" class="form-control">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="ReceivablePayable.loadData()">Apply</button>
							</div>
							<div class="col-sm-2">
								<button type="button" class="btn btn-success"
									title="Update All OverDue" onclick="ReceivablePayableEdit.dueDateUpdate()">Update OverDue</button>
							</div>
							<div class="col-sm-3 pull-right">
								<button type="button" class="btn btn-success pull-right"
								title="Create New" onclick="ReceivablePayableEdit.showModalPopup($('#myModal'));">Create
								Receivable or Payable</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="collaptable table table-striped table-bordered">
					<thead>
						<tr>
							<th>Account</th>
							<th>Type</th>
							<th>Address</th>
							<th>Blanace Amount</th>
						</tr>
					</thead>
					<tbody id="tBodyReceivablePayable">

					</tbody>
				</table>
			</div>
			<spring:url value="/web/receivable-payables-edit" var="urlReceivablePayableEdit"></spring:url>
			<form id="receivablePayablesForm" role="form" method="post"
				action="${urlReceivablePayableEdit}">
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
									<h4 class="modal-title" id="myModalLabel">Create or edit a
										Receivable Payables</h4>
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
	
									<div class="modal-body" style="overflow: auto;">
										<div class="form-group">
											<label class="control-label" for="field_account">Account</label> 
											<select id="field_account" name="accountPid"
												class="form-control"><option value="-1">Select
													Account</option>
													<c:forEach items="${accounts}" var="account">
														<option value="${account.pid}">${account.name}</option>
													</c:forEach>
											</select>
										</div>
										<div class="form-group">
											<label class="control-label" for="field_receivablePayableType">Receivable or Payable</label> 
											<select id="field_receivablePayableType" name="receivablePayableType"
												class="form-control">
													<option value="Receivable">Receivable</option>
													<option value="Payable">Payable</option>
											</select>
										</div>
										<div class="form-group">
											<label class="control-label" for="field_voucherNumber">Voucher Number</label> 
											<input type="text" class="form-control" name="referenceDocumentNumber"
												id="field_voucherNumber" maxlength="55" placeholder="Voucher Number" />
										</div>
										<div class="form-group">
											<label class="control-label" for="field_alias">Voucher Date</label>
											<div class="input-group">
												<input type="text" class="form-control" id="voucherDate"
													placeholder="Select Vocuher Date" name="referenceDocumentDate"
													style="background-color: #fff; z-index: inherit;"
													readonly="readonly" />
	
												<div class="input-group-addon">
													<a href="#"><i class="entypo-calendar"></i></a>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label" for="field_voucherAmount">Voucher Amount</label> 
											<input type="text" class="form-control" name="referenceDocumentAmount"
												id="field_voucherAmount" maxlength="55" placeholder="Voucher Amount" />
										</div>
										<div class="form-group">
											<label class="control-label" for="field_balanceAmount">Balance Amount</label> 
											<input type="text" class="form-control" name="referenceDocumentBalanceAmount"
												id="field_balanceAmount" maxlength="55" placeholder="Balance Amount" />
										</div>
										
									</div>
								</div>
								<div class="modal-footer">
									<div class="modal-footer">
										<button type="button" class="btn btn-default"
											data-dismiss="modal">Cancel</button>
										<button id="myFormSubmit" class="btn btn-primary" 
										>Save</button>
									</div>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal-dialog -->
					</div>
					<!-- /.Model Container-->
				</form>
				
				<form id="deleteForm" name="deleteForm" action="${urlReceivablePayableEdit}">
				<!-- Model Container-->
				<div class="modal fade container" id="deleteModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Confirm delete operation</h4>
							</div>
							<div class="modal-body">

								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<p>Are you sure you want to delete this Receivable Payable ?</p>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button class="btn btn-danger">Delete</button>
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
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/receivable-payables-edit.js"
		var="receivableJs"></spring:url>
	<script type="text/javascript" src="${receivableJs}"></script>
</body>
</html>