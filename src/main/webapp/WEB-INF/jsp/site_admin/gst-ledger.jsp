<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | GST Ledger</title>
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
			<h2>GST Ledger</h2>
			<div class="row col-xs-12">
				<div class="col-sm-6">
					<select id="dbCompany" name="companyPid" class="form-control"><option
							value="-1">Select Company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="pull-right">
					<div class="col-sm-3">
						<button type="button" class="btn btn-success"
							id="buttonActivated"
							onclick="GstLedger.changeStatus();">Apply</button>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th><input type="checkbox" class="allcheckbox" id="selectAll" />&nbsp;&nbsp;All</th>
						<th>Name</th>
						<th>Tax Type</th>
						<th>Status</th>
						<th>Account Type</th>
						<th>Tax Rate</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyGstLedgers">
					
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<spring:url value="/web/gst-ledger-configure" var="urlGstLedger"></spring:url>
			
			
			<form id="deleteForm" name="deleteForm" action="${urlGstLedger}">
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
									<p>Are you sure you want to delete this GST Ledger?</p>
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

	<spring:url value="/resources/app/gst-ledger.js" var="gstLedgerJs"></spring:url>
	<script type="text/javascript" src="${gstLedgerJs}"></script>
</body>
</html>