<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Task Plan</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Task Plan</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-md-12">
					<div class="form-group">
						<div class="col-sm-6">
							<label class="col-sm-3 control-label">Plan Date</label>
							<div class="input-group">
								<input type="text" id="planDatePicker"
									class="form-control datepicker" data-format="D, dd MM yyyy">
								<div class="input-group-addon">
									<a href="#"><i class="entypo-calendar"></i></a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="panel minimal">
						<!-- panel head -->
						<div class="panel-heading">
							<div class="panel-title">
								<select id="sbUsers" class="form-control">
									<option value="-1">Select User</option>
									<c:forEach items="${users}" var="user">
										<option value="${user.pid}">${user.firstName}
											(${user.login})</option>
									</c:forEach>
								</select>
							</div>
							<div class="panel-options">
								<ul class="nav nav-tabs">
									<li class="active"><a href="#tabUserTasks"
										data-toggle="tab" aria-expanded="true"><i
											class="entypo-check"></i>User Tasks</a></li>
									<li class=""><a href="#tabTaskList" data-toggle="tab"
										aria-expanded="true"><i class="entypo-newspaper"></i>Task
											List</a></li>
									<li class=""><a href="#tabTasks" data-toggle="tab"
										aria-expanded="false"><i class="entypo-doc-text-inv"></i>Tasks</a></li>
									<li class=""><a href="#tabUserTaskDownloaded"
										data-toggle="tab" aria-expanded="false"><i
											class="entypo-doc-text-inv"></i>Downloaded Tasks</a></li>
								</ul>
							</div>
						</div>
						<!-- panel body -->
						<div class="panel-body">
							<div class="tab-content">
								<div class="tab-pane active" id="tabUserTasks">
									<div class="row col-xs-12">
										<div class="pull-right">
											<button id="btnSaveUserTasks" type="button"
												class="btn btn-default">Save UserTask</button>
											<br /> <br />
										</div>
									</div>
									<div class="pull-left tab-content"><p id="errmsg" style="color: red; font-size: 14px;"></p></div>
									<table class="table table-bordered">
										<thead>
											<tr>
												<th>Activity</th>
												<th>Account</th>
												<th>Remarks</th>
												<th>Action</th>
											</tr>
										</thead>
										<tbody id="tblTabUserTasks"></tbody>
									</table>
									<div id="loadingUserTasks"></div>
								</div>
								<div class="tab-pane" id="tabTaskList">
									<button id="btnAddTabTaskList" type="button"
										class="btn btn-info">Add To UserTask</button>
									<br /> <br />
									<div id="checkTreeTabTaskList">
										<table class="table table-condensed"
											style="border-collapse: collapse;">
											<thead>
												<tr>
													<th>&nbsp;</th>
													<th>Name</th>
													<th>Action</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${taskList}" var="tasks" varStatus="loop">
													<tr id="${tasks.pid}" title="${tasks.name}" data-toggle="collapse"
														data-target="#${loop.index}" class="accordion-toggle">
														<td><button class="btn btn-default btn-xs">
																<span class="glyphicon glyphicon-eye-open"></span>
															</button></td>
														<td>${tasks.name}</td>
														<td><button type="button" class="btn btn-default"
																onclick="loadAllTasks(this)">Assign Tasks</button></td>
													</tr>
													<c:if test="${not empty tasks.tasks}">
														<tr>
															<td colspan="12" class="hiddenRow">
																<div class="accordian-body collapse" id="${loop.index}">
																	<table class="table table-striped">
																		<thead>
																			<tr>
																				<th><label><input type="checkbox"
																						class="allcheckbox" value="">All</label></th>
																				<th>Activity</th>
																				<th>Account</th>
																				<th>Remarks</th>
																			</tr>
																		</thead>
																		<tbody>
																			<c:forEach items="${tasks.tasks}" var="task">
																				<tr>
																					<td id="${task.pid}"><input class="chechbox"
																						type="checkbox"></td>
																					<td id="${task.activityPid}">${task.activityName}</td>
																					<td id="${task.accountProfilePid}">${task.accountProfileName}</td>
																					<td>${task.remarks}</td>
																				</tr>
																			</c:forEach>
																		</tbody>
																	</table>
																</div>
															</td>
														</tr>
													</c:if>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
								<div class="tab-pane" id="tabTasks">
									<form class="form-inline">
										<div class="form-group">
											<div class="input-group">
												<span class="input-group-addon btn btn-default"
													onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"><i
													class="glyphicon glyphicon-filter"></i></span> <input type="text"
													class="form-control" placeholder="Search" /> <span
													class="input-group-btn">
													<button
														onclick="Orderfleet.searchTable($(this).parent().prev('input').val());"
														class="btn btn-default" type="button">Go!</button>
												</span>
											</div>
										</div>
										<div class="pull-right">
											<div class="form-group">
												<button id="btnAddTabTasks" type="button"
													class="btn btn-info">Add To UserTask</button>
											</div>
											<div class="form-group">
												<a href="javascript:;"
													onclick="$('#newTaskModal').modal('show');"
													class="btn btn-default">Add New Task</a>
											</div>
										</div>
									</form>

									<table class="table table-bordered of-tbl-search">
										<thead>
											<tr>
												<th><input id="cbAllTasks" type="checkbox" /></th>
												<th>Activity</th>
												<th>Account</th>
												<th>Remarks</th>
											</tr>
										</thead>
										<tbody id="tblTabTasks">
											<c:forEach items="${tasks}" var="task" varStatus="loopStatus">
												<tr>
													<td><input id="${task.pid}" type="checkbox" /></td>
													<td id="${task.activityPid}">${task.activityName}</td>
													<td id="${task.accountProfilePid}">${task.accountProfileName}</td>
													<td>${task.remarks}</td>
												</tr>
											</c:forEach>
										</tbody>
										</tbody>
									</table>
								</div>
								<div class="tab-pane" id="tabUserTaskDownloaded">
									<table class="table table-bordered">
										<thead>
											<tr>
												<th>Activity</th>
												<th>Account</th>
												<th>Remarks</th>
												<th>User Remarks</th>
												<th>Status</th>
											</tr>
										</thead>
										<tbody id="tblTabUserTaskDownloaded"></tbody>
									</table>
									<div id="loadingUserTaskDownloaded"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/tasks" var="urlTask"></spring:url>
			<form id="newTaskForm" role="form" method="post" action="${urlTask}">
				<!-- Model Container-->
				<div class="modal fade container" id="newTaskModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create New Task</h4>
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
									<label class="control-label" for="field_activity">Activity</label>
									<select id="field_activity" name="activityPid"
										class="form-control">
										<option value="-1">Select Activity</option>
										<c:forEach items="${activities}" var="activity">
											<option value="${activity.pid}">${activity.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_accountType">Account
										Type</label> <select id="field_accountType" name="accountTypePid"
										class="form-control"><option value="-1">Select
											Account Type</option>
										<c:forEach items="${accountTypes}" var="accountType">
											<option value="${accountType.pid}">${accountType.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_account">Account
									</label> <select id="field_accounts" name="accountPids"
										multiple="multiple" class="form-control"
										style="height: 150px;">
									</select> <select id="field_account" class="form-control"
										style="display: none;">
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_remarks">Remarks</label>
									<textarea class="form-control" name="remarks" maxlength="250"
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

			<div class="modal fade custom-width" id="assignTasksModal">
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
							<div class="form-group">
								<input type="text" id="txtTaskListPopUpSearch"
									placeholder="Type to search task" class="form-control">
							</div>
							<div class="row">
								<div class="form-group col-xs-5">
									<label>Activities</label> <select class="form-control"
										multiple="multiple" id="sbActivities">
										<option value="-1">-- All --</option>
										<c:forEach items="${activities}" var="activity">
											<option value="${activity.pid}">${activity.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group col-xs-5">
									<label>Locations</label> <select class="form-control"
										multiple="multiple" id="sbLocations">
										<option value="-1">-- All --</option>
										<c:forEach items="${locations}" var="location">
											<option value="${location.pid}">${location.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group col-xs-1">
									<br /> <br />
									<button id="btnSearchTasks"
										onclick="filterTasksInAssignTaskPopup();" type="button"
										class="btn btn-default">Search</button>
								</div>
							</div>
							<div class="form-group">
								<div id="tasksCheckboxes">
									<table id="tblAssignTasks" data-task-list-pid=""
										class='table table-striped table-bordered'>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveAssignedTasks" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<!-- OF Modal Filter start -->
			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>Tasks</b>
							</h4>
						</div>
						<div class="modal-body">
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<ul class="nav nav-tabs right-aligned">
											<li class="tab-title pull-left">
												<div>
													<button
														onclick="$('.search-results-panes').find('input[type=checkbox]:checked').removeAttr('checked');"
														type="button" class="btn btn-secondary">Clear All</button>
													<b>&nbsp;</b>
												</div>
											</li>
											<li class="active"><a href="#tActivity">Activities</a></li>
											<li class=""><a href="#tLocation">Locations</a></li>
										</ul>
										<form class="search-bar">
											<div class="input-group">
												<input id="ofTxtSearch" type="text"
													class="form-control input-lg" name="search"
													placeholder="Type for search...">
												<div class="input-group-btn">
													<button class="btn btn-lg btn-primary btn-icon"
														style="pointer-events: none;">
														Search <i class="entypo-search"></i>
													</button>
												</div>
											</div>
										</form>
										<div class="search-results-panes">
											<div class="search-results-pane" id="tActivity"
												style="display: block;">
												<div class="row">
													<c:forEach items="${activities}" var="activity">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${activity.pid}">${activity.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>
											<div class="search-results-pane" id="tLocation"
												style="display: none;">
												<div class="row">
													<c:forEach items="${locations}" var="location">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input value="${location.pid}"
																	type="checkbox"> ${location.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>
										</div>
									</div>
								</div>
							</section>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
							<button type="button" class="btn btn-info"
								onclick="filterTaskByActivityLocations(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>
			<!-- OF Modal Filter end -->
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<spring:url value="/resources/assets/js/bootstrap-datepicker.js"
		var="bootstrapDatePicker"></spring:url>
	<script type="text/javascript" src="${bootstrapDatePicker}"></script>
	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>
	<spring:url value="/resources/app/task.js" var="taskJs"></spring:url>
	<script type="text/javascript" src="${taskJs}"></script>
	<spring:url value="/resources/app/user-task-plan.js"
		var="userTaskPlanJs"></spring:url>
	<script type="text/javascript" src="${userTaskPlanJs}"></script>
</body>
</html>