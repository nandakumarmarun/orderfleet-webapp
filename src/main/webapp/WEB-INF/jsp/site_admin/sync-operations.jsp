<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sync Operation</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Sync Operation</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						id="btnCreateSyncOPeration">Create Sync Operation</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Sync Operation Types</th>
						<!-- <th>Actions</th> -->
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${syncOperations}" var="syncOperation"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${syncOperation.companyName}</td>
							<td>
								<div>
									<c:forEach items="${syncOperation.operationTypes}"
										var="operationType">
										<span class="label label-info">${operationType}</span>
										<br />
									</c:forEach>
								</div>
							</td>
							<%-- <td>
								<button type="button" class="btn btn-blue"
									onclick="SyncOperation.showModalPopup($('#myModal'),'${syncOperation.companyName}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="SyncOperation.showModalPopup($('#deleteModal'),'${syncOperation.companyName}',2);">Delete</button>
							</td> --%>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/syncOperations" var="urlSyncOperation"></spring:url>
			<div class="modal fade container" id="assignSyncOperationsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Sync
								Operations</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="field_company" name="companyPid"
										class="form-control selectpicker" data-live-search="true"><option value="-1">Select
											Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
							</div>

							<div class="form-group">
								<div id="divSyncOperations">
									<table class='table table-striped table-bordered'>
										<tr>
											<td><input type="checkbox" class="allcheckbox">All
											</td>
											<td>sync Operation Types</td>
											<td>reset</td>
											<td>user</td>
											<td>document</td>
										</tr>
										<c:forEach items="${syncOperationTypes}" var="syncOperation">
											<tr>
												<td><input class="chk"  name='syncOperation' type='checkbox' value="${syncOperation}" /></td>
												<td>${syncOperation}</td>
												<td><input name='syncOperation_reset' type='checkbox' id="${syncOperation}" value="reset${syncOperation}" /></td>
												<td><input name='syncOperation_user' type='checkbox' id="user${syncOperation}" value="user${syncOperation}" /></td>
												<td><input name='syncOperation_document' type='checkbox' id="document${syncOperation}" value="document${syncOperation}" /></td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveSyncOperations" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
		</div>
	</div>

	<spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
	<script type="text/javascript" src="${joinable}"></script>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/sync-operation.js" var="uploadXlsJs"></spring:url>
	<script type="text/javascript" src="${uploadXlsJs}"></script>
</body>
</html>