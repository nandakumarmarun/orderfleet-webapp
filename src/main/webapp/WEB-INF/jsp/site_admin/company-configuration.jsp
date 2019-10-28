<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<title>SalesNrich | Company Configuration</title>

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
			<h2>Company Configuration</h2>
			<div class="clearfix"></div>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success" id="companysModal">Create/Update
						Company Configuration</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Distance Traveled</th>
						<th>Location Variance</th>
						<th>Interim Save</th>
						<th>Refresh ProductGroup Product</th>
						<th>Stage Change For Accounting Voucher</th>
						<th>New Customer Alias</th>
						<th>Chat Reply</th>
						<th>Sales Pdf Download</th>
						<th>Visit Based Transaction</th>
						<th>Sales Management</th>
						<th>Sales Edit Enabled</th>
						<th>Gps Variance Query</th>
						<th>Send Sales Order Email</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${companyConfigurations}"
						var="companyConfiguration" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${companyConfiguration.companyName}</td>
							<td>${companyConfiguration.distanceTraveled}</td>
							<td>${companyConfiguration.locationVariance}</td>
							<td>${companyConfiguration.interimSave}</td>
							<td>${companyConfiguration.refreshProductGroupProduct}</td>
							<td>${companyConfiguration.stageChangeAccountingVoucher}</td>
							<td>${companyConfiguration.newCustomerAlias}</td>
							<td>${companyConfiguration.chatReply}</td>
							<td>${companyConfiguration.salesPdfDownload}</td>
							<td>${companyConfiguration.visitBasedTransaction}</td>
							<th>${companyConfiguration.salesManagement}</th>
							<th>${companyConfiguration.salesEditEnabled}</th>
							<th>${companyConfiguration.gpsVarianceQuery}</th>
							<th>${companyConfiguration.sendSalesOrderEmail}</th>
							<td><button type="button" class="btn btn-danger"
									onclick="CompanyConfiguration.deletes('${companyConfiguration.companyPid}');">Delete</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<hr />

			<div class="modal fade container"
				id="assignCompanyConfigurationsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Company
								Configuration</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">

							<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="dbCompany" name="companyPid" class="form-control selectpicker" data-live-search="true"><option
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
													<label class="control-label">Distance Traveled</label> <input
														id="distanceTraveled" name='checkDistanceTraveled'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Location Variance</label> <input
														id="locationVariance" name='checksLocationVariance'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Interim Save</label> <input
														id="interimSave" name='checksInterimSave' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Refresh ProductGroup
														Product</label> <input id="refreshProductGroupProduct"
														name='checksRefreshProductGroupProduct' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Stage Change For
														Accounting Voucher</label> <input
														id="stageChangeAccountingVoucher"
														name='checksStageChangeAccountingVoucher' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">New Customer Alias</label> <input
														id="newCustomerAlias" name='checksNewCustomerAlias'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Chat Reply</label> <input
														id="chatReply" name='checksChatReply' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Sales Pdf Download</label> <input
														id="salesPdfDownload" name='checksSalesPdfDownload'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>
										<tr>
											<td><div class="form-group">
													<label class="control-label">Visit Based Transation</label>
													<input id="visitBasedTransaction"
														name='checksVisitBasedTransation"' type='checkbox'
														class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Sales Management</label> <input
														id="salesManagement" name='checksSalesManagement"'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Sales Edit Enabled</label> <input
														id="salesEditEnabled" name='checksSalesEditEnabled"'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Gps Variance Query</label> <input
														id="gpsVarianceQuery" name='checksGpsVarianceQuery"'
														type='checkbox' class="form-control" />
												</div></td>
										</tr>

										<tr>
											<td><div class="form-group">
													<label class="control-label">Send Sales Order Email</label>
													<input id="sendSalesOrderEmail"
														name='checksSendSalesOrderEmail"' type='checkbox'
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
								id="btnSaveCompanyConfigurations" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

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

	<spring:url value="/resources/app/company-configuration.js"
		var="companyConfigurationJs"></spring:url>
	<script type="text/javascript" src="${companyConfigurationJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>
</body>
</html>