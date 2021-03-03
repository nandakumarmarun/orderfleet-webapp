<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Documents</title>
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
			<h2>Documents</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="Document.showModalPopup($('#myModal'));">Create
						new Document</button>
				</div>
				<div class="col-sm-4">

					<select id="field_mainCompany" name="mainCompanyPid"
						class="form-control selectpicker" data-live-search="true"><option
							value="-1">Select Company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>

				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Description</th>
						<th>Document Type</th>
						<th>Activity Account</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="documentTbody">
					<%-- <c:forEach items="${documents}" var="document"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${document.name}</td>
							<td>${document.alias == null ? "" : document.alias}</td>
							<td>${document.description == null ? "" : document.description}</td>
							<td>${document.documentType}</td>
							<td>${document.activityAccount}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="Document.showModalPopup($('#viewModal'),'${document.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="Document.showModalPopup($('#myModal'),'${document.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="Document.showModalPopup($('#deleteModal'),'${document.pid}',2);">Delete</button>

							</td>
						</tr>
					</c:forEach> --%>
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/documents" var="urlDocument"></spring:url>

			<form id="documentForm" role="form" method="post"
				action="${urlDocument}">
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
									Document</h4>
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
										class="form-control"><option value="-1">Select
											Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="clearfix"></div>

								<div class="modal-body" style="overflow: auto;">
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_documentPrefix">Prefix</label>
										<input type="text" class="form-control" name="documentPrefix"
											id="field_documentPrefix" maxlength="10" placeholder="Prefix" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Alias</label> <input
											type="text" class="form-control" name="alias"
											id="field_alias" maxlength="55" placeholder="Alias" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_accountType">Document
											Type</label> <select id="field_documentType" name="documentType"
											class="form-control"
											onchange="Document.onChangeDocumentType();">
											<option value="-1">Select Document Type</option>
											<option value="DYNAMIC_DOCUMENT">Dynamic Document</option>
											<option value="INVENTORY_VOUCHER">Inventory Voucher</option>
											<option value="ACCOUNTING_VOUCHER">Accounting
												Voucher</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_activityAccount">Activity
											Account</label> <select id="field_activityAccount"
											name="activityAccount" class="form-control">
											<option value="-1">Select Activity Account</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_paymentMode">Payment
											Mode</label> <select id="field_paymentMode" name="paymentMode"
											class="form-control" onchange="">
											<c:forEach items="${paymentModes}" var="paymentMode">
												<option value="${paymentMode}"
													${paymentMode == 'ALL' ? 'selected' : '' }>${paymentMode}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_stockFlow">Stock
											Flow</label> <select id="field_stockFlow" name="stockFlow"
											class="form-control" onchange="">
											<c:forEach items="${stockFlows}" var="stockFlow">
												<option value="${stockFlow}"
													${stockFlow == 'NONE' ? 'selected' : '' }>${stockFlow}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<textarea class="form-control" name="description"
											maxlength="250" id="field_description"
											placeholder="Description"></textarea>
									</div>
									<div class="form-group">
										<label for="save"> <input type="checkbox"
											id="field_save" /> &nbsp;<span>Save</span>
										</label>
									</div>
									<div class="form-group">
										<label for="editable"> <input type="checkbox"
											id="field_editable" /> &nbsp;<span>Editable</span>
										</label>
									</div>
									<div class="form-group">
										<label for="batchEnabled"> <input type="checkbox"
											id="field_batchEnabled" /> &nbsp;<span>Batch Enabled</span>
										</label>
									</div>
									<div class="form-group">
										<label for="promptStockLocation"> <input
											type="checkbox" id="field_promptStockLocation" /> &nbsp;<span>Prompt
												Stock Location</span>
										</label>
									</div>
									<div class="form-group" style="display: none;" id="single">
										<label for="singleVoucherMode"> <input type="checkbox"
											id="field_singleVoucherMode" /> &nbsp;<span>Single
												Voucher Mode</span>
										</label>
									</div>
									<div class="form-group" id="photoMandatory">
										<label for="photoMandatory"> <input type="checkbox"
											id="field_photoMandatory" /> &nbsp;<span>Photo
												Mandatory</span>
										</label>
									</div>
									<div class="form-group" id="isTakeImageFromGallery">
										<label for="isTakeImageFromGallery"> <input
											type="checkbox" id="field_isTakeImageFromGallery" /> &nbsp;<span>Take
												Image From Gallery</span>
										</label>
									</div>
									<div class="form-group" id="qrCodeEnabled">
										<label for="qrCodeEnabled"> <input type="checkbox"
											id="field_qrCodeEnabled" /> &nbsp;<span>QR-code
												Enable</span>
										</label>
									</div>
									<div class="form-group" id="addNewCustomer">
										<label for="addNewCustomer"> <input type="checkbox"
											id="field_addNewCustomer" /> &nbsp;<span>Add New
												Customer</span>
										</label>
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
								<h4 class="modal-title" id="viewModalLabel">Document</h4>
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
											<span>Document Prefix</span>
										</dt>
										<dd>
											<span id="lbl_documentPrefix"></span>
										</dd>
										<hr />
										<dt>
											<span>Alias</span>
										</dt>
										<dd>
											<span id="lbl_alias"></span>
										</dd>
										<hr />
										<dt>
											<span>Document Type</span>
										</dt>
										<dd>
											<span id="lbl_documentType"></span>
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
											<span>Activity Account</span>
										</dt>
										<dd>
											<span id="lbl_activityAccount"></span>
										</dd>
										<hr />
										<dt>
											<span>Description</span>
										</dt>
										<dd>
											<span id="lbl_description"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlDocument}">
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
								<p>Are you sure you want to delete this Document?</p>
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

			<div class="modal fade container" id="accountTypesModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">
								Assign <font id="accountTypeColumn"></font> Account Types
							</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<input type="hidden" id="hdnAccountTypeColumn">
								<div id="divAccountTypes">
									<table class='table table-striped table-bordered'>
										<c:forEach items="${accountTypes}" var="accountType">
											<tr>
												<td><input name='accountType' type='checkbox'
													value="${accountType.pid}" /></td>
												<td>${accountType.name}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveAccountTypes" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<div class="modal fade container" id="priceLevelsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Price
								Levels</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divPriceLevels">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="all" name="filter">
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="unselected" name="filter">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br />
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>Price Level</th>
											</tr>
										</thead>
										<tbody id="tblPriceLevels">
											<c:forEach items="${priceLevels}" var="priceLevel">
												<tr>
													<td><input name='priceLevel' type='checkbox'
														value="${priceLevel.pid}" /></td>
													<td>${priceLevel.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSavePriceLevels" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="stockCalculationModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Stock Calculation</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" id="divRadio">
								<input type="radio" value="Opening" name="rdStockCalculation" />
								Opening <input type="radio" value="Closing"
									name="rdStockCalculation" /> Closing Actual
							</div>
							<div class="form-group" id="divLogical" style="display: none;">
								<input id="txtClosingLogical" type="checkbox" value="Opening" />
								Closing Logical
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnStockCalculation" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/site-document.js" var="documentJs"></spring:url>
	<script type="text/javascript" src="${documentJs}"></script>
</body>
</html>