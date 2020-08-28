<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Task User Settings</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Task User Settings</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="TaskUserSetting.showModalPopup($('#myModal'));">Create
						new Task User Setting</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Executor</th>
						<th>Activity</th>
						<th>Document</th>
						<th>Activity Event</th>
						<th>Approvers</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${taskUserSettings}"
						var="taskUserSetting" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${taskUserSetting.executorName}</td>
							<td>${taskUserSetting.activityName}</td>
							<td>${taskUserSetting.documentName}</td>
							<td>${taskUserSetting.taskSettingEvent}</td>
							<td><table class="table">
									<c:forEach items="${taskUserSetting.approvers}" var="approver">
										<tr>
											<td>${approver.firstName}</td>
										</tr>
									</c:forEach>
								</table></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="TaskUserSetting.showModalPopup($('#viewModal'),'${taskUserSetting.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="TaskUserSetting.showModalPopup($('#myModal'),'${taskUserSetting.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="TaskUserSetting.showModalPopup($('#deleteModal'),'${taskUserSetting.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/taskUserSettings" var="urlTaskUserSetting"></spring:url>

			<form id="taskUserSettingForm" role="form" method="post"
				action="${urlTaskUserSetting}">
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
									Task User Setting</h4>
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
										<label class="control-label" for="field_executor">Executor</label>
										<select id="field_executor" class="form-control"
											name="executorPid"><option value="-1">Select
												Executor</option>
											<c:forEach items="${users}" var="user">
												<option value="${user.pid}">${user.firstName}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_activity">Filter
											Task Settings</label> <select id="field_activity"
											onchange="TaskUserSetting.loadDocuments()"
											class="form-control"><option value="-1">Select
												Activity</option>
											<c:forEach items="${activities}" var="activity">
												<option value="${activity.pid}">${activity.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<select id="field_document" class="form-control"
											onchange="TaskUserSetting.loadTaskSettings()"><option
												value="-1">Select Document</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_taskSetting">Task
											Setting</label> <select id="field_taskSetting" name="taskSettingPid"
											class="form-control"><option value="-1">Select
												Task Setting</option>
											<c:forEach items="${taskSettings}" var="taskSetting">
												<option value="${taskSetting.pid}">${taskSetting.activityEvent}
													- ${taskSetting.activityName} - ${taskSetting.startDateColumn}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group" style="overflow: auto; height: 180px">
										<table class="table table-striped table-bordered">
											<thead>
												<tr>
													<th>Approvers</th>
													<th>
														<button type="button" class="btn btn-info"
															style="height: 30px; float: right;"
															onclick="$('#approversModal').modal('show');">Assign</button>
													</th>
												</tr>
											<tbody id="tblApprovers">
											</tbody>
										</table>
									</div>
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

			<div class="modal fade container" id="approversModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Select Approvers</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divApprovers">
									<table class='table table-striped table-bordered'>
										<c:forEach items="${users}" var="approver">
											<tr>
												<td><input name='approver' type='checkbox'
													value="${approver.pid}" /></td>
												<td>${approver.firstName}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<button class="btn" data-dismiss="modal">Cancel</button>
							<input class="btn btn-success" type="button" id="btnAddApprover"
								value="Add" />
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

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
								<h4 class="modal-title" id="viewModalLabel">Task User
									Setting</h4>
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
											<span>Executor</span>
										</dt>
										<dd>
											<span id="lbl_executor"></span>
										</dd>
										<hr />
										<dt>
											<span>Activity Event</span>
										</dt>
										<dd>
											<span id="lbl_activityEvent"></span>
										</dd>
										<hr />
									</dl>
									<table class="table table-striped table-bordered">
										<thead>
											<tr>
												<td>Approvers</td>
											</tr>
										</thead>
										<tbody id="tbl_Approvers"></tbody>
									</table>
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
				action="${urlTaskUserSetting}">
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
									<p>Are you sure you want to delete this Task User Setting?</p>
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

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/task-user-setting.js"
		var="taskUserSettingJs"></spring:url>
	<script type="text/javascript" src="${taskUserSettingJs}"></script>
</body>
</html>