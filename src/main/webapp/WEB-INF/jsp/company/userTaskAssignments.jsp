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

<title>SalesNrich | User Task Assignments</title>
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
			<h2>User Task Assignments</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="UserTaskAssignment.showModalPopup($('#myModal'));">Create
						new User Task Assignment</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Executive</th>
						<th>Task</th>
						<th>Account</th>
						<th>Priority</th>
						<th>Start Date</th>
						<th>Remarks</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${userTaskAssignments}"
						var="userTaskAssignment" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${userTaskAssignment.executiveUserName}</td>
							<td>${userTaskAssignment.taskActivityName}</td>
							<td>${userTaskAssignment.taskAccountName}</td>
							<td>${userTaskAssignment.priorityStatus}</td>
							<td>${userTaskAssignment.startDate}</td>
							<td>${userTaskAssignment.remarks}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="UserTaskAssignment.showModalPopup($('#viewModal'),'${userTaskAssignment.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="UserTaskAssignment.showModalPopup($('#myModal'),'${userTaskAssignment.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="UserTaskAssignment.showModalPopup($('#deleteModal'),'${userTaskAssignment.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/user-task-assignment"
				var="urlUserTaskAssignment"></spring:url>

			<form id="userTaskAssignmentForm" role="form" method="post"
				action="${urlUserTaskAssignment}">
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
									User Task Assignment</h4>
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
									<label class="control-label" for="field_task">Task</label> <select
										id="field_task" name="taskPid" class="form-control">
										<option value="-1">Select Task</option>
										<c:forEach items="${tasks}" var="task">
											<option value="${task.pid}">${task.activityName} - ${task.accountProfileName}</option>
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
									<span>Task</span>
								</dt>
								<dd>
									<span id="lbl_task"></span>
								</dd>
								<hr />
								<dt>
									<span>Account</span>
								</dt>
								<dd>
									<span id="lbl_account"></span>
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
				action="${urlUserTaskAssignment}">
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
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/user-task-assignment.js"
		var="userTaskAssignmentJs"></spring:url>
	<script type="text/javascript" src="${userTaskAssignmentJs}"></script>

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