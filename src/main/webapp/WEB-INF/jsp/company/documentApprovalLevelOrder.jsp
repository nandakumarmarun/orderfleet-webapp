<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Document Approval Level Order</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Document Approval Level Order</h2>

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
									onclick="DocumentApprovalLevelOrder.showModalPopup($('#documentApprovalLevelOrderModal'),'${document.pid}',0);">Document
									Approval Level Order</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/document-approval-levels-order"
				var="urlDocumentApprovalLevelOrder"></spring:url>

			<div class="modal fade container"
				id="documentApprovalLevelOrderModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Document Approval Level Order</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<table class='table table-striped table-bordered' id="tbldocumentApprovalLevelOrders">
									<thead>
										<tr>
											<th>Document Approval Level</th>
											<th>Order No</th>
										</tr>
									</thead>
									<tbody id="documentApprovalLevelOrders"></tbody>
								</table>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnDocumentApprovalLevelOrder" value="Save" />
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

	<spring:url value="/resources/app/document-approval-level-order.js"
		var="documentApprovalLevelOrder"></spring:url>
	<script type="text/javascript" src="${documentApprovalLevelOrder}"></script>
</body>
</html>