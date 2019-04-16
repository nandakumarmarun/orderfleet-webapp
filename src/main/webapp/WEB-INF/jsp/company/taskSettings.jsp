<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Task Settings</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>TaskSettings</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="TaskSetting.showModalPopup($('#myModal'));">Create
						new Task Setting</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Activity</th>
						<th>Document</th>
						<th>Activity Event</th>
						<th>Task Activity</th>
						<th>Required</th>
						<th>Create Plan</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${taskSettings}" var="taskSetting"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${taskSetting.activityName}</td>
							<td>${taskSetting.documentName}</td>
							<td>${taskSetting.activityEvent}</td>
							<td>${taskSetting.taskActivityName}</td>
							<td>${taskSetting.required}</td>
							<td>${taskSetting.createPlan}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="TaskSetting.showModalPopup($('#viewModal'),'${taskSetting.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="TaskSetting.showModalPopup($('#myModal'),'${taskSetting.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="TaskSetting.showModalPopup($('#deleteModal'),'${taskSetting.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/taskSettings" var="urlTaskSetting"></spring:url>

			<form id="taskSettingForm" role="form" method="post"
				action="${urlTaskSetting}">
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
									Task Setting</h4>
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
										<label class="control-label" for="field_activity">Activity</label>
										<select id="field_activity" name="activityPid"
											onchange="TaskSetting.loadDocuments()" class="form-control"><option
												value="-1">Select Activity</option>
											<c:forEach items="${activities}" var="activity">
												<option value="${activity.pid}">${activity.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_document">Document</label>
										<select id="field_document" name="documentPid"
											onchange="TaskSetting.onChangeDocument()"
											class="form-control"><option value="-1">Select
												Document</option>
										</select>
									</div>
									<div class="form-group" style="display: none;"
										id="divStartDateColumn">
										<label class="control-label" for="field_startDateColumn">Start
											Date Column</label> <select id="field_startDateColumn"
											name="startDateColumn" class="form-control">
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_activityEvent">Activity
											Event</label> <select id="field_activityEvent" name="activityEvent"
											class="form-control"><option value="-1">Select
												Activity Event</option>
											<option value="ONCREATE">ONCREATE</option>
											<option value="ONDELETE">ONDELETE</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_taskActivity">Task
											Activity</label> <select id="field_taskActivity"
											name="taskActivityPid" class="form-control"><option
												value="-1">Select Task Activity</option>
											<c:forEach items="${activities}" var="taskActivity">
												<option value="${taskActivity.pid}">${taskActivity.name}</option>
											</c:forEach>
										</select>
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
									<div class="form-group">
										<label class="control-label" for="field_createPlan">Create Plan</label>
										<input autofocus="autofocus" type="checkbox"
											class="form-control" name="createPlan" id="field_createPlan"  />
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
								<h4 class="modal-title" id="viewModalLabel">Task Setting</h4>
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
											<span>Activity</span>
										</dt>
										<dd>
											<span id="lbl_activity"></span>
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
											<span>Activity Event</span>
										</dt>
										<dd>
											<span id="lbl_activityEvent"></span>
										</dd>
										<hr />
										<dt>
											<span>Task Activity</span>
										</dt>
										<dd>
											<span id="lbl_taskActivity"></span>
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
											<span>Create Plan</span>
										</dt>
										<dd>
											<span id="lbl_createPlan"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlTaskSetting}">
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
									<p>Are you sure you want to delete this Task Setting?</p>
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

	<spring:url value="/resources/app/task-setting.js" var="taskSettingJs"></spring:url>
	<script type="text/javascript" src="${taskSettingJs}"></script>
</body>
</html>