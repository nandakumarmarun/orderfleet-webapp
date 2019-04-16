<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Document Stock Location Source</title>
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
			<h2>Document Stock Location Source</h2>
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
									onclick="DocumentStockLocationSource.showModalPopup($('#stockLocationSourceModal'),'${document.pid}',0);">Assign
									Stock Location Source</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/document-stock-location-source"
				var="urlDocumentStockLocationSource"></spring:url>

			<div class="modal fade container" id="stockLocationSourceModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Stock Location Source</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="stockLocationSourcesCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>Select</th>
												<th>Stock Location</th>
												<th>Default</th>
											</tr>
										</thead>
										<c:forEach items="${allStockLocations}" var="stockLocation">
											<tr>
												<td><input name='stockLocationSource' type='checkbox'
													value="${stockLocation.pid}"
													onclick='DocumentStockLocationSource.onSelectStockLocation(this);' /></td>
												<td>${stockLocation.name}</td>
												<td><input name='defaultStockLocation' type='checkbox'
													onclick="DocumentStockLocationSource.onSelectDefaultStockLocation(this)"
													disabled="disabled" value="${stockLocation.pid}" /></td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveStockLocationSource" value="Save" />
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

	<spring:url value="/resources/app/document-stock-location-source.js"
		var="documentStockLocationSource"></spring:url>
	<script type="text/javascript" src="${documentStockLocationSource}"></script>

</body>
</html>