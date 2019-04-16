<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Accounting Voucher UI Settings</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Accounting Voucher UI Settings</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="AccountingVoucherUISetting.showModalPopup($('#myModal'));">Create
						new Accounting Voucher UI Setting</button>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Title</th>
						<th>Activity</th>
						<th>Document</th>
						<th>Payment Mode</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${accountingVoucherUISettings}"
						var="accountingVoucherUISetting" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${accountingVoucherUISetting.name}</td>
							<td>${accountingVoucherUISetting.title}</td>
							<td>${accountingVoucherUISetting.activityName}</td>
							<td>${accountingVoucherUISetting.documentName}</td>
							<td>${accountingVoucherUISetting.paymentMode}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="AccountingVoucherUISetting.showModalPopup($('#viewModal'),'${accountingVoucherUISetting.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="AccountingVoucherUISetting.showModalPopup($('#myModal'),'${accountingVoucherUISetting.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="AccountingVoucherUISetting.showModalPopup($('#deleteModal'),'${accountingVoucherUISetting.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/accounting-voucher-ui-setting"
				var="urlAccountingVoucherUISetting"></spring:url>

			<form id="accountingVoucherUISettingForm" role="form" method="post"
				action="${urlAccountingVoucherUISetting}">
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
									Accounting Voucher UI Setting</h4>
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
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_title">Title</label> <input
											type="text" class="form-control" name="title"
											id="field_title" maxlength="55" placeholder="Title" />
									</div>
									
										<div class="form-group">
										<label class="control-label" for="field_activity">Activity</label><select id="field_activity" name="activity"
											class="form-control" onchange="AccountingVoucherUISetting.onChangeDocumentType();">
											<option value="-1">Select Activity</option>
										<c:forEach items="${activities}" var="activity">
											<option value="${activity.pid}">${activity.name}</option>
										</c:forEach>
										</select>
											</div>
									<div class="form-group">
										<label class="control-label" for="field_document">Document</label>
										<select id="field_document" name="documentPid"
											class="form-control">
											<option value="-1">Select Document</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_paymentMode">Payment
											Mode</label> <select id="field_paymentMode" name="paymentMode"
											class="form-control">
											<option value="-1">Select Payment Mode</option>
											<c:forEach var="paymentMode" items="${paymentModes}">
												<option value="${paymentMode}">${paymentMode}</option>
											</c:forEach>
										</select>
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
								<h4 class="modal-title" id="viewModalLabel">Accounting
									Voucher UI Setting</h4>
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
											<span>Name</span>
										</dt>
										<dd>
											<span id="lbl_name"></span>
										</dd>
										<hr />
										<dt>
											<span>Title</span>
										</dt>
										<dd>
											<span id="lbl_title"></span>
										</dd>
										<hr />
										<dt>
											<span>Activity</span>
										</dt>
										<dd>
											<span id="lbl_activity"></span>
										</dd>
										<hr />
										<dt>
											<span>Document</span>
										</dt>
										<dd>
											<span id="lbl_document"></span>
										</dd>
										<hr />
										<dt>
											<span>PaymentMode</span>
										</dt>
										<dd>
											<span id="lbl_paymentMode"></span>
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
				action="${urlAccountingVoucherUISetting}">
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
									<p>Are you sure you want to delete this Accounting Voucher
										UI Setting?</p>
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

	<spring:url value="/resources/app/accounting-voucher-ui-setting.js"
		var="accountingVoucherUISettingJs"></spring:url>
	<script type="text/javascript" src="${accountingVoucherUISettingJs}"></script>
</body>
</html>