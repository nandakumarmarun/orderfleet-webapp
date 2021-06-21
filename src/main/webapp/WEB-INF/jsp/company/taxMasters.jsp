<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Tax Masters</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Tax Masters</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="TaxMaster.showModalPopup($('#myModal'));">Create
						new Tax Master</button>
				</div>
			</div>
			<br> <br>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Vat Name</th>
						<th>Class</th>
						<th>Vat Percentage</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${taxMasters}" var="taxMaster"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${taxMaster.vatName}</td>
							<td>${taxMaster.vatClass == null ? "" : taxMaster.vatClass}</td>
							<td>${taxMaster.vatPercentage == null ? "" : taxMaster.vatPercentage}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="TaxMaster.showModalPopup($('#viewModal'),'${taxMaster.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="TaxMaster.showModalPopup($('#myModal'),'${taxMaster.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="TaxMaster.showModalPopup($('#deleteModal'),'${taxMaster.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/taxMasters" var="urlTaxMaster"></spring:url>

			<form id="taxMasterForm" role="form" method="post"
				action="${urlTaxMaster}">
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
									TaxMaster</h4>
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
										<label class="control-label" for="field_vatName">Vat
											Name</label> <input autofocus="autofocus" type="text"
											class="form-control" name="vatName" id="field_vatName"
											maxlength="255" placeholder="Vat Name" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_taxId">Tax Id</label>
										<input type="text" class="form-control" name="taxId"
											id="field_taxId" placeholder="Tax Id" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_taxCode">Tax
											Code</label> <input type="text" class="form-control" name="taxCode"
											id="field_taxCode" placeholder="Tax Code" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_class">Class</label> <input
											type="text" class="form-control" name="class"
											id="field_class" placeholder="Class" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_vatPercentage">Vat
											Percentage</label> <input type="number" step="any"
											class="form-control" name="vatPercentage"
											id="field_vatPercentage" placeholder="Vat Percentage" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<input type="text" class="form-control" name="description"
											id="field_description" placeholder="Description" />
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
								<h4 class="modal-title" id="viewModalLabel">Tax Master</h4>
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
											<span>Vat Name</span>
										</dt>
										<dd>
											<span id="lbl_vatName"></span>
										</dd>
										<hr />
										<dt>
											<span>Tax Id</span>
										</dt>
										<dd>
											<span id="lbl_taxId"></span>
										</dd>
										<hr />
										<dt>
											<span>Tax Code</span>
										</dt>
										<dd>
											<span id="lbl_taxCode"></span>
										</dd>
										<hr />
										<dt>
											<span>Class</span>
										</dt>
										<dd>
											<span id="lbl_class"></span>
										</dd>
										<hr />
										<dt>
											<span>Vat Percentage</span>
										</dt>
										<dd>
											<span id="lbl_vatPercentage"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlTaxMaster}">
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
									<p>Are you sure you want to delete this TaxMaster?</p>
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

	<spring:url value="/resources/app/tax-master.js" var="taxMasterJs"></spring:url>
	<script type="text/javascript" src="${taxMasterJs}"></script>
</body>
</html>