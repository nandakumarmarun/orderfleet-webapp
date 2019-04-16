<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<title>SalesNrich | User Task List Assignments</title>
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
			<h2>User Task List Assignments</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="UserTaskListAssignment.showModalPopup($('#myModal'));">Create
						new User Task List Assignment</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Executive</th>
						<th>Task</th>
						<th>Priority</th>
						<th>Start Date</th>
						<th>Remarks</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pageUserTaskListAssignment.content}"
						var="userTaskListAssignment" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${userTaskListAssignment.executiveUserName}</td>
							<td>${userTaskListAssignment.taskListName}</td>
							<td>${userTaskListAssignment.priorityStatus}</td>
							<td>${userTaskListAssignment.startDate}</td>
							<td>${userTaskListAssignment.remarks}</td>
							<%-- <td><c:forEach items="${userTaskListAssignment.taskLists}"
									var="taskList">
									<span>${taskList.name}</span>
									<br />
								</c:forEach></td> --%>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="UserTaskListAssignment.showModalPopup($('#viewModal'),'${userTaskListAssignment.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="UserTaskListAssignment.showModalPopup($('#myModal'),'${userTaskListAssignment.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="UserTaskListAssignment.showModalPopup($('#deleteModal'),'${userTaskListAssignment.pid}',2);">Delete</button>
								<%-- <button type="button" class="btn btn-info"
									onclick="UserTaskListAssignment.showModalPopup($('#taskListsModal'),'${userTaskListAssignment.pid}',3);">Assign
									Tasks</button> --%>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="row-fluid">
				<util:pagination thispage="${pageUserTaskListAssignment}"></util:pagination>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/user-task-list-assignment"
				var="urlUserTaskListAssignment"></spring:url>

			<form id="userTaskListAssignmentForm" role="form" method="post"
				action="${urlUserTaskListAssignment}">
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
									User Task List Assignment</h4>
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
									<label class="control-label" for="field_executiveUser">User</label>
									<select id="field_executiveUser" name="executiveUserPid"
										class="form-control">
										<option value="-1">Select User</option>
										<c:forEach items="${users}" var="executiveUser">
											<option value="${executiveUser.pid}">${executiveUser.firstName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_taskList">Task
										List</label> <select id="field_taskList" name="taskListPid"
										class="form-control">
										<option value="-1">Select Task List</option>
										<c:forEach items="${taskLists}" var="taskList">
											<option value="${taskList.pid}">${taskList.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_priority">Priority</label>
									<select id="field_priority" name="priority"
										class="form-control">
										<option value="NORMAL">NORMAL</option>
										<option value="LOW">LOW</option>
										<option value="MEDIUM">MEDIUM</option>
										<option value="HIGH">HIGH</option>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_startDate">Start
										Date</label> <input type="text" class="form-control" name="startDate"
										id="field_startDate" maxlength="55" placeholder="Start Date" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_remarks">Remarks</label>
									<textarea class="form-control" name="remarks"
										id="field_remarks" placeholder="Remarks"></textarea>
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
							<h4 class="modal-title" id="viewModalLabel">User Task
								Assignment</h4>
						</div>
						<div class="modal-body">
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
									<span>User</span>
								</dt>
								<dd>
									<span id="lbl_executiveUser"></span>
								</dd>
								<hr />
								<dt>
									<span>Task List</span>
								</dt>
								<dd>
									<span id="lbl_taskList"></span>
								</dd>
								<hr />
								<dt>
									<span>Priority</span>
								</dt>
								<dd>
									<span id="lbl_priority"></span>
								</dd>
								<hr />
								<dt>
									<span>Start Date</span>
								</dt>
								<dd>
									<span id="lbl_startDate"></span>
								</dd>
								<hr />
								<dt>
									<span>Remarks</span>
								</dt>
								<dd>
									<span id="lbl_remarks"></span>
								</dd>
								<hr />
							</dl>
							<!-- <table class="table  table-striped table-bordered">
								<thead>
									<tr>
										<th>Tasks</th>
									</tr>
								</thead>
								<tbody id="tblTasks">

								</tbody>
							</table> -->
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

			<form id="deleteForm" name="deleteForm"
				action="${urlUserTaskListAssignment}">
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
								<!-- error message -->
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<p>Are you sure you want to delete this User Task
									Assignment?</p>
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

			<%-- <div class="modal fade container" id="tasksModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Tasks</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="tasksCheckboxes">
									<table class='table table-striped table-bordered'>
										<c:forEach items="${tasks}" var="task">
											<tr>
												<td><input name='task' type='checkbox'
													value="${task.pid}" /></td>
												<td>${task.name}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveTasks"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div> --%>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/user-task-list-assignment.js"
		var="userTaskListAssignmentJs"></spring:url>
	<script type="text/javascript" src="${userTaskListAssignmentJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$("#field_startDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
		});
	</script>
</body>
</html>