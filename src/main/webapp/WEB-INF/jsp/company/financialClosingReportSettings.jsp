<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Financial Closing Report Settings</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Financial Closing Report Settings</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="SalesReportSettings.showModalPopup($('#myModal'));">Create
						new Financial Closing Report Settings</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Document</th>
						<th>Payment Mode</th>
						<th>Debit Credit</th>
						<th>Sort Order</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
				 	<c:forEach items="${financialClosingReportSettings}" var="salesReportSetting"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${salesReportSetting.documentName}</td>
							<td>${salesReportSetting.paymentMode}</td>
							<td>${salesReportSetting.debitCredit}</td>
							<td>${salesReportSetting.sortOrder}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="SalesReportSettings.showModalPopup($('#viewModal'),'${salesReportSetting.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="SalesReportSettings.showModalPopup($('#myModal'),'${salesReportSetting.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="SalesReportSettings.showModalPopup($('#deleteModal'),'${salesReportSetting.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach> 
				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/financial-closing-report-settings" var="urlSalesReportSettings"></spring:url>

			<form id="salesReportSettingsForm" role="form" method="post"
				action="${urlSalesReportSettings}">
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
									Financial Closing Report Settings</h4>
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
										<label class="control-label" for="field_document">Document</label> <select id="field_document"
											name="documentPid" class="form-control">
											<option value="-1">Select Document</option>
											<c:forEach var="document" items="${documents}">
											<option value="${document.pid}">${document.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_paymentMode">Payment Mode</label> <select id="field_paymentMode"
											name="paymentMode" class="form-control">
											<option value="-1">Select Payment Mode</option>
											<c:forEach var="paymentMode" items="${paymentModes}">
											<option value="${paymentMode}">${paymentMode}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_debitCredit">Debit Credit</label> <select id="field_debitCredit"
											name="debitCredit" class="form-control">
											<option value="-1">Select Debit Credit</option>
											<c:forEach var="debitCredit" items="${debitCredits}">
											<option value="${debitCredit}">${debitCredit}</option>
											</c:forEach>
										</select>
									</div>
									
									
											<div class="form-group">
										<label class="control-label" for="field_sortorder">Sort Order</label> <input
											type="number" class="form-control" name="sortOrder"
											id="field_sortorder" placeholder="sortOrder" value="0"/>
									</div>
									
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
								<h4 class="modal-title" id="viewModalLabel">Financial Closing Report Settings</h4>
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
											<span>Document</span>
										</dt>
										<dd>
											<span id="lbl_document"></span>
										</dd>
										<hr />
										<dt>
											<span>Payment Mode</span>
										</dt>
										<dd>
											<span id="lbl_paymentMode"></span>
										</dd>
										<hr />
										<dt>
											<span>Debit Credit</span>
										</dt>
										<dd>
											<span id="lbl_debitCredit"></span>
										</dd>
										<hr />
										<dt>
											<span>Sort Order</span>
										</dt>
										<dd>
											<span id="lbl_sortorder"></span>
										</dd>
										<hr />
									</dl>
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

			<form id="deleteForm" name="deleteForm" action="${urlSalesReportSettings}">
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
								<!-- error message -->
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<p>Are you sure you want to delete this Financial Closing Report Settings?</p>
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

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/financial-closing-report-settings.js" var="salesReportSettingsJs"></spring:url>
	<script type="text/javascript" src="${salesReportSettingsJs}"></script>
</body>
</html>