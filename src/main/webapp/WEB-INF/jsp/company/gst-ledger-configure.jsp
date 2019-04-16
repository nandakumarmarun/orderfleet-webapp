<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | GST Ledger Configuration</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>

<body class="page-body" data-url="">
	<div class="page-container">
		<%-- <jsp:include page="../fragments/m_navbar.jsp"></jsp:include> --%>
		<div class="main-content">
			<%-- <jsp:include page="../fragments/m_header_main.jsp"></jsp:include> --%>
			<hr />
			<div class="col-sm-3">
				<h2 style="margin: 0px">GST Ledger Configuration</h2>
			</div>
			<!-- <div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="GstLedgerConfig.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>
				</div>
			</div> -->
			
			<div class="col-sm-9">
				<button type="button" class="btn btn-success pull-right"
					id="buttonActivated"
					onclick="GstLedgerConfig.changeStatus();">Activate</button>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th><input type="checkbox" class="allcheckbox"
								id="selectAll" />&nbsp;&nbsp;All</th>
						<th>Name</th>
						<th>Tax Type</th>
						<th>Status</th>
						<th>Account Type</th>
						<th>Tax Rate</th>
						<!-- <th>Actions</th> -->
					</tr>
				</thead>
				<tbody>
					<c:if test = "${empty gstLedgers}">
						<tr><td colspan = 7 style = "text-align: center;">No Data Available</td></tr>
					</c:if>
					<c:forEach items="${gstLedgers}" var="gstLedger" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<c:choose>
								<c:when test = "${gstLedger.activated == true}">
									<td><input type="checkbox" name="ledger" value="${gstLedger.id}" checked/></td>
								</c:when>
								<c:otherwise>
									<td><input type="checkbox" name="ledger" value="${gstLedger.id}"/></td>
								</c:otherwise>
							</c:choose>
							<td>${gstLedger.name== null ? "" : gstLedger.name}</td>
							<td>${gstLedger.taxType== null ? "" : gstLedger.taxType}</td>
							<c:choose>
								<c:when test = "${gstLedger.activated == true}">
									<td><span class="label label-success">Activated</span></td>
								</c:when>
								<c:otherwise>
									<td><span class="label label-danger">Deactivated</span></td>
								</c:otherwise>
							</c:choose>
							<td>${gstLedger.accountType== null ? "" : gstLedger.accountType}</td>
							<td>${gstLedger.taxRate== null ? "" : gstLedger.taxRate}</td>
							<%-- <td>
								<button type="button" class="btn btn-blue"
									onclick="GstLedgerConfig.showModalPopup($('#myModal'),'${gstLedger.id}',1);" title="Edit">
										Edit
								</button>
								<button type="button" class="btn btn-danger"
									onclick="GstLedgerConfig.showModalPopup($('#deleteModal'),'${gstLedger.id}',2);" title="Delete">
										Delete
								</button>
							</td> --%>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/gst-ledger-config" var="urlGstLedgerConfig"></spring:url>
			
			<form id="createEditForm" role="form" method="post"
				action="${urlGstLedgerConfig}">
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
									GST Ledger</h4>
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
								<div class="modal-body" style="overflow: auto; height: 60%;">
									<div class="form-group">
										<label class="control-label" for="field_name">Name
										</label> <input autofocus="autofocus" type="text"
											class="form-control" name="name" id="field_name"
											maxlength="255" placeholder="Name" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_taxType">Tax Type
										</label>
										<input class="form-control" name="taxType" maxlength="500"
											id="field_taxType" placeholder="Tax Type"/>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_taxRate">Tax Rate</label>
										<input type="number" class="form-control" name="taxRate"
											id="field_taxRate" placeholder="Tax Rate" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_accountType">Account Type</label>
										<input type="text" id="field_accountType" name="accountType" 
										placeholder="Account Type" class="form-control" >
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
			
			<form id="deleteForm" name="deleteForm" action="${urlGstLedgerConfig}">
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

	<spring:url value="/resources/app/gst-ledger-config.js" var="gstLedgerConfigJs"></spring:url>
	<script type="text/javascript" src="${gstLedgerConfigJs}"></script>
</body>
</html>