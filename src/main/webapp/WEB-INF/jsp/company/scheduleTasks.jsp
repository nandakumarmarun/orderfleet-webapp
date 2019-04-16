<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<title>SalesNrich | Schedule Tasks</title>
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
			<h2>Schedule Tasks</h2>
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
					<c:forEach items="${users}" var="user"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${user.firstName}&nbsp;${user.lastName}</td>
							<td>
								<button type="button" class="btn btn-info"
									onclick="ScheduleTask.showModalPopup($('#taskModal'),'${user.pid}','Task');">Tasks</button>

								<button type="button" class="btn btn-info"
									onclick="ScheduleTask.showModalPopup($('#taskGroupModal'),'${user.pid}','TaskGroup');">Task
									Groups</button>

								<button type="button" class="btn btn-info"
									onclick="ScheduleTask.showModalPopup($('#taskListModal'),'${user.pid}','TaskList');">Task
									List</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/schedule-tasks" var="urlScheduleTask"></spring:url>

			<div class="modal fade container" id="taskModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">User Task Assignment</h4>
						</div>

						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divTaskAssignment">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="all" name="filter">
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="unselected" name="filter">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br />
									<table id="tblTask" class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>Task</th>
												<th>Priority</th>
												<th>Start Date</th>
												<th>Remarks</th>
											</tr>
										</thead>
										<tbody id="tbodyTask">
											<c:forEach items="${tasks}" var="task">
												<tr>
													<td><input name="task" type="checkbox"
														value="${task.pid}" />&nbsp; ${task.activityName} -
														${task.accountProfileName}</td>
													<td><select id="priority${task.pid}"
														class="form-control priority" style="width: 100px;">
															<option value="NORMAL">NORMAL</option>
															<option value="LOW">LOW</option>
															<option value="MEDIUM">MEDIUM</option>
															<option value="HIGH">HIGH</option>
													</select></td>
													<td><input id="startDate${task.pid}" type="text"
														class="form-control startDate" maxlength="55"
														placeholder="Start Date" /></td>
													<td><textarea id="remarks${task.pid}"
															class="form-control remarks" placeholder="Remarks"></textarea></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						<label class="error-msg" style="color: red; padding: 5px;"></label>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveTask"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>



			<div class="modal fade container" id="taskGroupModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">User Task Group Assignment</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divTaskGroupAssignment">
									<table id="tblTaskGroup"
										class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>Task Group</th>
												<th>Priority</th>
												<th>Start Date</th>
												<th>Remarks</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${taskGroups}" var="taskGroup">
												<tr>
													<td><input name="taskGroup" type="checkbox"
														value="${taskGroup.pid}" />&nbsp; ${taskGroup.name}</td>
													<td><select id="priority${taskGroup.pid}"
														class="form-control priority" style="width: 100px;">
															<option value="NORMAL">NORMAL</option>
															<option value="LOW">LOW</option>
															<option value="MEDIUM">MEDIUM</option>
															<option value="HIGH">HIGH</option>
													</select></td>
													<td><input id="startDate${taskGroup.pid}" type="text"
														class="form-control startDate" maxlength="55"
														placeholder="Start Date" /></td>
													<td><textarea id="remarks${taskGroup.pid}"
															class="form-control remarks" placeholder="Remarks"></textarea></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						<label class="error-msg" style="color: red; padding: 5px;"></label>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveTaskGroup" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="taskListModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">User Task List Assignment</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divTaskListAssignment">
									<table id="tblTaskList"
										class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>Task List</th>
												<th>Priority</th>
												<th>Start Date</th>
												<th>Remarks</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${taskLists}" var="taskList">
												<tr>
													<td><input name="taskList" type="checkbox"
														value="${taskList.pid}" />&nbsp; ${taskList.name}</td>
													<td><select id="priority${taskList.pid}"
														class="form-control priority" style="width: 100px;">
															<option value="NORMAL">NORMAL</option>
															<option value="LOW">LOW</option>
															<option value="MEDIUM">MEDIUM</option>
															<option value="HIGH">HIGH</option>
													</select></td>
													<td><input id="startDate${taskList.pid}" type="text"
														class="form-control startDate" maxlength="55"
														placeholder="Start Date" /></td>
													<td><textarea id="remarks${taskList.pid}"
															class="form-control remarks" placeholder="Remarks"></textarea></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						<label class="error-msg" style="color: red; padding: 5px;"></label>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveTaskList"
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

	<spring:url value="/resources/app/schedule-task.js"
		var="scheduleTaskJs"></spring:url>
	<script type="text/javascript" src="${scheduleTaskJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$(".startDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
		});
	</script>

</body>
</html>