<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Tally Configuration</title>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>

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
			<h2>Tally Configuration</h2>
			<div class="clearfix"></div>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success" id="tallysModal">Create/Update
						Tally Configuration</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Tally Company Name</th>
						<th>Tally Product Key</th>
						<th>Dynamic Date</th>
						<th>Order Number with Employee</th>
						<th>Actual Bill Status</th>
						<th>Item Remarks Enabled</th>
						<!-- <th>GST Names</th>
						<th>Static Godown Names</th> -->
						<th>RoundOff Ledger Name</th>
						<th>Sales Ledger Name</th>
						<th>Receipt Cash Ledger</th>
						<th>Bank Receipt Voucher Type</th>
						<th>Bank Name</th>
						<th>Cash Receipt Voucher Type</th>
						<th>Transaction TYpe</th>
						<th>PDC Voucher Type</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${tallyConfigurations}" var="tallyConfiguration"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${tallyConfiguration.companyName}</td>
							<td>${tallyConfiguration.tallyCompanyName}</td>
							<td>${tallyConfiguration.tallyProductKey}</td>
							<td>${tallyConfiguration.dynamicDate}</td>
							<td>${tallyConfiguration.orderNumberWithEmployee}</td>
							<td>${tallyConfiguration.actualBillStatus}</td>
							<td>${tallyConfiguration.itemRemarksEnabled}</td>
							<%-- <td>${tallyConfiguration.gstNames}</td>
							<td>${tallyConfiguration.staticGodownNames}</td> --%>
							<td>${tallyConfiguration.roundOffLedgerName}</td>
							<td>${tallyConfiguration.salesLedgerName}</td>
							<td>${tallyConfiguration.receiptVoucherType}</td>
							<td>${tallyConfiguration.bankReceiptType}</td>
							<td>${tallyConfiguration.bankName}</td>
							<td>${tallyConfiguration.cashReceiptType}</td>
							<td>${tallyConfiguration.transactionType}</td>
							<td>${tallyConfiguration.pdcVoucherType}</td>
							<td><button type="button" class="btn btn-info"
									onclick="TallyConfiguration.edit('${tallyConfiguration.pid}','${tallyConfiguration.companyPid}');">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="TallyConfiguration.deleteConfig('${tallyConfiguration.pid}');">Delete</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<hr />
			<form id="createEditForm">
			<div class="modal fade container" id="assignTallyConfigurationsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Tally
								Configuration</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">

							<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="dbCompany" name="companyPid" class="form-control"><option
											value="-1">Select Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
							</div>

							<div class="form-group">
								<div id="divSyncOperations">
									<table class='table table-striped table-bordered'>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Tally Company Name</label> <input
														id="tallyCompanyName" name='tallyCompanyName' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Tally Product Key</label> <input
														id="tallyProductKey" name='tallyProductKey' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Dynamic Date (Format : 01-01-2019)</label> <input
														id="dynamicDate" name='dynamicDate' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Order Number With
														Employee</label> <input id="orderNumberWithEmployee" name='check'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Actual Bill Status</label> <input
														id="actualBillStatus" name='check' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Item Remarks Enabled</label> <input
														id="itemRemarksEnabled" name='check' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>

										<!-- <tr>
											<td><div class="form-group">
													<label class="control-label">GST Names</label> <input
														id="gstNames" name='gstNames' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Static Godown Name</label> <input
														id="staticGodownNames" name='staticGodownNames'
														type='text' class="form-control" />
												</div></td>
										</tr> -->

										<tr>
											<td><div class="form-group">
													<label class="control-label">RoundOff Ledger Name</label> <input
														id="roundOffLedgerName" name='roundOffLedgerName' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Sales Ledger Name</label> <input
														id="salesLedgerName" name='salesLedgerName' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Receipt Cash Ledger</label> <input
														id="receiptVoucherType" name='receiptVoucherType'
														type='text' class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Bank Receipt Voucher Type</label> <input
														id="bankReceiptType" name='bankReceiptType' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Bank Name</label> <input
														id="bankName" name='bankName' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Cash Receipt Voucher Type</label> <input
														id="cashReceiptType" name='cashReceiptType' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Transaction Type</label> <input
														id="transactionType" name='transactionType' type='text'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">PDC Voucher Type</label> <input
														id="pdcVoucherType" name='pdcVoucherType' type='text'
														class="form-control" />
												</div></td>
										</tr>

									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveTallyConfigurations" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			</form>
			<div class="modal fade container" id="alertBox">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" id="alertMessage"
							style="font-size: large;"></div>
						<div class="modal-footer">
							<button id="btnDelete" class="btn btn-danger"
								data-dismiss="modal">Ok</button>
							<button class="btn btn-info" data-dismiss="modal">Close</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/tally-configuration.js"
		var="tallyConfigurationJs"></spring:url>
	<script type="text/javascript" src="${tallyConfigurationJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

</body>
</html>