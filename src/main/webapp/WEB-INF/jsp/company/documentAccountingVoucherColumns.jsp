<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Document Accounting Voucher Columns</title>
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
			<h2>Document Accounting Voucher Columns</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${documents}" var="document"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${document.name}</td>
							<td>
								<button type="button" class="btn btn-info"
									onclick="DocumentAccountingVoucherColumn.showModalPopup($('#accountingVoucherColumnsModal'),'${document.pid}',0);">Assign
									Accounting Voucher Columns</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/document-accounting-voucher-columns"
				var="urlDocumentAccountingVoucherColumn"></spring:url>



			<div class="modal fade container" id="accountingVoucherColumnsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Accounting Voucher Columns</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountingVoucherColumnsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th colspan="2">Accounting Voucher Column</th>
												<th>Enabled</th>
											</tr>
										</thead>
										<c:forEach items="${accountingVoucherColumns}"
											var="accountingVoucherColumn">
											<tr>
												<td><input name='accountingVoucherColumn'
													type='checkbox' value="${accountingVoucherColumn.name}" /></td>
												<td>${accountingVoucherColumn.name}</td>
												<td><input id="${accountingVoucherColumn.name}"
													name='enabled' type='checkbox' /></td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveAccountingVoucherColumn" value="Save" />
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

	<spring:url
		value="/resources/app/document-accounting-voucher-column.js"
		var="DocumentAccountingVoucherColumn"></spring:url>
	<script type="text/javascript" src="${DocumentAccountingVoucherColumn}"></script>

</body>
</html>