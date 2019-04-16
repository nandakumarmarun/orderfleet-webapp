<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | RealTimeAPIs</title>
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
			<h2>Real Time APIs</h2>
			<div class="row col-xs-12">
			<div class="row">
				<div class="form-group">
					<div class="col-sm-3">
						Company<select id="dbCompany" name="companyPid"
							class="form-control">
							<option value="no">All Companies</option>
							<c:forEach items="${companies}" var="company">
								<option value="${company.pid}">${company.legalName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						<br />
						<button type="button" class="btn btn-orange" style="width: 65px;"
							onclick="RealTimeAPI.filter()">Load</button>
					</div>
				</div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="RealTimeAPI.showModalPopup($('#myModal'));">Create
						new Real Time API</button>
				</div>
			</div>
				


			</div>
			<br> <br>
			<div class="clearfix"></div>
			<hr />
			<table id="tableRealTimeAPI" class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Name</th>
						<th>Api</th>
						<th>Version</th>
						<th>Activated</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyTableRealTimeAPI">
					<c:forEach items="${realtimeAPIs}" var="realTimeAPI"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${realTimeAPI.companyName}</td>
							<td>${realTimeAPI.name}</td>
							<td>${realTimeAPI.api == null ? "" : realTimeAPI.api}</td>
							<td>${realTimeAPI.version == null ? "" : realTimeAPI.version}</td>
							<td><span
								class="label ${realTimeAPI.activated? 'label-success':'label-danger' }"
								onclick="RealTimeAPI.setActive('${realTimeAPI.name}','${realTimeAPI.id}','${ !realTimeAPI.activated}')"
								style="cursor: pointer;">${realTimeAPI.activated? "Activated" : "Deactivated"}</span>
							</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="RealTimeAPI.showModalPopup($('#viewModal'),'${realTimeAPI.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="RealTimeAPI.showModalPopup($('#myModal'),'${realTimeAPI.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="RealTimeAPI.showModalPopup($('#deleteModal'),'${realTimeAPI.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/real-time-api" var="urlRealTimeAPI"></spring:url>

			<form id="realTimeAPIForm" role="form" method="post"
				action="${urlRealTimeAPI}">
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
									Real Time API</h4>
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
										<label class="control-label" for="field_api">Api</label> <input
											type="text" class="form-control" name="api" id="field_api"
											maxlength="55" placeholder="Api" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_version">Version</label>
										<input type="text" class="form-control" name="version"
											id="field_version" placeholder="Version" />
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
								<h4 class="modal-title" id="viewModalLabel">RealTimeAPI</h4>
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
											<span>Api</span>
										</dt>
										<dd>
											<span id="lbl_api"></span>
										</dd>
										<hr />
										<dt>
											<span>Version</span>
										</dt>
										<dd>
											<span id="lbl_version"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlRealTimeAPI}">
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
									<p>Are you sure you want to delete this RealTimeAPI?</p>
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

			<div class="modal fade container" id="enableRealTimeAPIModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable RealTimeAPI</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>RealTimeAPI</th>
											</tr>
										</thead>
										<tbody id="tblEnableRealTimeAPI">
											<c:forEach items="${deactivatedRealTimeAPIs}"
												var="realTimeAPI">
												<tr>
													<td><input name='realTimeAPI' type='checkbox'
														value="${realTimeAPI.id}" /></td>
													<td>${realTimeAPI.name}</td>
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
								id="btnActivateRealTimeAPI" value="Activate" />
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

	<spring:url value="/resources/app/realTimeAPI.js" var="realTimeAPIJs"></spring:url>
	<script type="text/javascript" src="${realTimeAPIJs}"></script>
</body>
</html>