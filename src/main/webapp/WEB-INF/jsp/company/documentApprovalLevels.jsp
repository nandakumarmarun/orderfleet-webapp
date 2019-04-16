<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Document Approval Levels</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Document Approval Levels</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="DocumentApprovalLevel.showModalPopup($('#myModal'));">Create
						new Document Approval Level</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Document</th>
						<th>Approvar Count</th>
						<th>Approval Order</th>
						<th>Required</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${documentApprovalLevel}"
						var="documentApprovalLevel" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${documentApprovalLevel.name}</td>
							<td>${documentApprovalLevel.documentName}</td>
							<td>${documentApprovalLevel.approverCount}</td>
							<td>${documentApprovalLevel.approvalOrder}</td>
							<td>${documentApprovalLevel.required}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="DocumentApprovalLevel.showModalPopup($('#viewModal'),'${documentApprovalLevel.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="DocumentApprovalLevel.showModalPopup($('#myModal'),'${documentApprovalLevel.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="DocumentApprovalLevel.showModalPopup($('#deleteModal'),'${documentApprovalLevel.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="DocumentApprovalLevel.showModalPopup($('#assignUsersModal'),'${documentApprovalLevel.pid}',3);">Assign
									Users</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/documentApprovalLevels"
				var="urlDocumentApprovalLevel"></spring:url>

			<form id="documentApprovalLevelForm" role="form" method="post"
				action="${urlDocumentApprovalLevel}">
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
									Document Approval Level</h4>
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
									<label class="control-label" for="field_name">Name</label> <input
										autofocus="autofocus" type="text" class="form-control"
										name="name" id="field_name" maxlength="255" placeholder="Name" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_document">Document</label>
									<select id="field_document" name="documentPid"
										class="form-control"><option value="-1">Select
											Document</option>
										<c:forEach items="${documents}" var="document">
											<option value="${document.pid}">${document.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_name">Approver
										Count</label> <input type="text" class="form-control"
										name="approverCount" id="field_approverCount" maxlength="3"
										placeholder="Approver Count" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_script">Script</label>
									<textarea class="form-control" name="script" id="field_script"
										placeholder="Script" maxlength="14000"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_required">Required</label>
									<input autofocus="autofocus" type="checkbox"
										class="form-control" name="required" id="field_required" />
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
								<h4 class="modal-title" id="viewModalLabel">Document
									Approval Level</h4>
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
											<span>Document</span>
										</dt>
										<dd>
											<span id="lbl_document"></span>
										</dd>
										<hr />
										<dt>
											<span>Approvar Count</span>
										</dt>
										<dd>
											<span id="lbl_approverCount"></span>
										</dd>
										<hr />
										<dt>
											<span>Approval Order</span>
										</dt>
										<dd>
											<span id="lbl_approvalOrder"></span>
										</dd>
										<hr />
										<dt>
											<span>Required</span>
										</dt>
										<dd>
											<span id="lbl_required"></span>
										</dd>
										<hr />
										<dt>
											<span>Script</span>
										</dt>
										<dd>
											<span id="lbl_script"></span>
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

			<form id="deleteForm" name="deleteForm"
				action="${urlDocumentApprovalLevel}">
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
									<p>Are you sure you want to delete this Document Approval
										Level?</p>
								</div>
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

			<div class="modal fade container" id="assignUsersModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Users</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divUsers">
									<table class='table table-striped table-bordered'>
										<c:forEach items="${users}" var="user">
											<tr>
												<td><input name='user' type='checkbox'
													value="${user.pid}" /></td>
												<td>${user.firstName}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveUsers"
								value="Save" />
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

	<spring:url value="/resources/app/document-approval-level.js"
		var="documentApprovalLevelJs"></spring:url>
	<script type="text/javascript" src="${documentApprovalLevelJs}"></script>
</body>
</html>