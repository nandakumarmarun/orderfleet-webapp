<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Receivable Payable Columns</title>
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
			<h2>Receivable Payable Columns</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button id="enableConfigColumn" type="button" class="btn btn-info">Enable
						Column</button>
				</div>
				<div class="clearfix"></div>
				<hr />
				<table class="table table-striped table-bordered">
					<thead>
						<tr>
							<th>Name</th>
							<th>Enabled</th>
						</tr>
					</thead>
					<tbody id="tbodyRPColumn">
						<c:forEach items="${rpColumnConfigs}" var="rpColumnConfig">
							<tr>
								<td>${rpColumnConfig.receivablePayableColumn.name}</td>
								<td>${rpColumnConfig.enabled}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<hr />
				<!-- Footer -->
				<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

				<spring:url value="/web/document-inventory-voucher-columns"
					var="urlDocumentInventoryVoucherColumn"></spring:url>

				<div class="modal fade container" id="rpColumnsModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Assign Receivable-Payable Columns</h4>
							</div>
							<div class="modal-body" style="overflow: auto; height: 500px">
								<div class="form-group">
									<div id="rpColumnsCheckboxes">
										<table class='table table-striped table-bordered'>
											<thead>
												<tr>
													<th>Name</th>
													<th><input class='allcheckbox' type='checkbox' />Enabled</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${rpColumns}" var="rpColumn">
													<tr>
														<td>${rpColumn.name}</td>
														<td><input name='enabled' value='${rpColumn.name}' type='checkbox' /></td>
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
									id="btnSaveColumnConfigs" value="Save" />
								<button class="btn" data-dismiss="modal">Cancel</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/receivable-payable-column-config.js"
		var="receivablePayableColumnConfig"></spring:url>
	<script type="text/javascript" src="${receivablePayableColumnConfig}"></script>

</body>
</html>