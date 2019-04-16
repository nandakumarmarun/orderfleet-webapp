<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Task Lists</title>
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
			<h2>Task Lists</h2>
			<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="searchTasklist" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearchTaskList" style="float: right;">Search</button>
								</span>
							</div>
						</div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="TaskList.showModalPopup($('#myModal'));">Create
						new Task List</button>
				</div>
			</form>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Description</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody  id="tBodyTaskList">
					<c:forEach items="${taskLists}" var="taskList"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${taskList.name}</td>
							<td>${taskList.alias}</td>
							<td>${taskList.description}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="TaskList.showModalPopup($('#viewModal'),'${taskList.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="TaskList.showModalPopup($('#myModal'),'${taskList.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="TaskList.showModalPopup($('#deleteModal'),'${taskList.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="TaskList.showModalPopup($('#tasksModal'),'${taskList.pid}',3);">Assign
									Tasks</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/task-lists" var="urlTaskList"></spring:url>

			<form id="taskListForm" role="form" method="post"
				action="${urlTaskList}">
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
									Task List</h4>
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
									<label class="control-label" for="field_alias">Alias</label> <input
										type="text" class="form-control" name="alias" id="field_alias"
										maxlength="55" placeholder="Alias" />
								</div>

								<div class="form-group">
									<label class="control-label" for="field_description">Description</label>
									<textarea class="form-control" name="description"
										id="field_description" placeholder="Description"></textarea>
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
							<h4 class="modal-title" id="viewModalLabel">Task List</h4>
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
									<span>Name</span>
								</dt>
								<dd>
									<span id="lbl_name"></span>
								</dd>
								<hr />
								<dt>
									<span>Alias</span>
								</dt>
								<dd>
									<span id="lbl_alias"></span>
								</dd>
								<hr />
								<dt>
									<span>Description</span>
								</dt>
								<dd>
									<span id="lbl_description"></span>
								</dd>
								<hr />
							</dl>
							<table class="table  table-striped table-bordered" id="tblTasks">

							</table>
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

			<form id="deleteForm" name="deleteForm" action="${urlTaskList}">
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
								<p>Are you sure you want to delete this Task List?</p>
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

			<div class="modal fade custom-width" id="tasksModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Tasks</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="row">
								<div class="form-group col-xs-5">
									<label>Activities</label> <select class="form-control"
										multiple="multiple" id="sbActivities" style=" width: 101%; height: 23%;">
										<option value="-1">-- All --</option>
										<c:forEach items="${activities}" var="activity">
											<option value="${activity.pid}">${activity.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group col-xs-5">
									<label>Locations</label> <select class="form-control"
										multiple="multiple" id="sbLocations" style=" width: 101%; height: 23%;">
										<option value="-1">-- All --</option>
										<c:forEach items="${locations}" var="location">
											<option value="${location.pid}">${location.name}</option>
										</c:forEach>
									</select>
								</div></div>
								<div >
								<input type="text" id="txtTasksPopUpSearch"
									placeholder="Type to search task" class="form-control" style=" width: 60%; float: left;"><div style=" margin-left: 64%;"><input type="radio" value="all" name="filter" checked>
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="nonselected" name="filter">
											&nbsp;Nonselected&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id="btnSearchTasks" type="button"
										class="btn btn-default">Search</button></div>
							</div>
							<br>
							<div class="form-group">
								<div id="tasksCheckboxes">
									<table id="tblAssignTasks" class='table table-striped table-bordered'>
										<c:forEach items="${tasks}" var="task">
											<tr>
												<td><input name='task' type='checkbox'
													value="${task.pid}" /></td>
												<td>${task.activityName}</td>
												<td>${task.accountProfileName}</td>
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
			</div>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/task-list.js" var="taskListJs"></spring:url>
	<script type="text/javascript" src="${taskListJs}"></script>

</body>
</html>