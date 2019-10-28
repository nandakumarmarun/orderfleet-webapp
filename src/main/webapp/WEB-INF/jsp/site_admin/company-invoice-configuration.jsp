<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Company Invoice Configuration</title>
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
			<h2>Company Invoice Configuration</h2>
			<div class="row col-xs-12">
				<div class="row">
					<div class="form-group">
						<div class="pull-right">
							<button type="button" class="btn btn-success"
								onclick="CompanyInvoiceConfiguration.showModalPopup($('#myModal'));">Create
								new Company Invoice Configuration</button>
						</div>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table id="tableCompanyInvoiceConfiguration"
				class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Address</th>
						<th>Amount Per User</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyCompanyInvoiceConfiguration">
					<c:forEach items="${companyInvoiceConfigurations}"
						var="companyInvoiceConfiguration" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${companyInvoiceConfiguration.companyName}</td>
							<td>${companyInvoiceConfiguration.address}</td>
							<td>${companyInvoiceConfiguration.amountPerUser}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="CompanyInvoiceConfiguration.showModalPopup($('#viewModal'),'${companyInvoiceConfiguration.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="CompanyInvoiceConfiguration.showModalPopup($('#myModal'),'${companyInvoiceConfiguration.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="CompanyInvoiceConfiguration.showModalPopup($('#deleteModal'),'${companyInvoiceConfiguration.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/company-invoice-configuration"
				var="urlCompanyInvoiceConfiguration"></spring:url>

			<form id="companyInvoiceConfigurationForm" role="form" method="post"
				action="${urlCompanyInvoiceConfiguration}">
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
									Company Invoice Configuration</h4>
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

								<div class="form-group">
									<select id="field_company" name="companyPid"
										class="form-control selectpicker" data-live-search="true"><option value="-1">Select
											Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="clearfix"></div>
								<div class="modal-body" style="overflow: auto;">
									<div class="form-group">
										<label class="control-label" for="field_address">Address</label>
										<textarea autofocus="autofocus" class="form-control"
											name="address" id="field_address" placeholder="Address"></textarea>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_amtPerUser">Amount
											Per User</label> <input type="number" step="0.01"
											class="form-control" name="amtPerUser" id="field_amtPerUser"
											value="0.0" maxlength="8" placeholder="Amount Per User"
											required />
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
								<h4 class="modal-title" id="viewModalLabel">Company Invoice
									Configuration</h4>
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
									<dl class="dl-horizontal">
										<dt>
											<span>Address</span>
										</dt>
										<dd>
											<span id="lbl_address"></span>
										</dd>
										<hr />
										<dt>
											<span>Amount Per User</span>
										</dt>
										<dd>
											<span id="lbl_amtPerUser"></span>
										</dd>
										<hr />
									</dl>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm"
				action="${urlCompanyInvoiceConfiguration}">
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
									<p>Are you sure you want to delete this Company Invoice
										Configuration?</p>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button class="btn btn-danger">Delete</button>
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

	<spring:url value="/resources/app/company-invoice-configuration.js"
		var="companyInvoiceConfigurationJs"></spring:url>
	<script type="text/javascript" src="${companyInvoiceConfigurationJs}"></script>
</body>
</html>