<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Task</title>
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
			<h2>Tasks</h2>
			<div class="clearfix"></div>
			<hr />
			<form class="form-inline">
				<div class="form-group">
					<div class="input-group">
						<span class="input-group-addon btn btn-default"
							onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"><i
							class="glyphicon glyphicon-filter"></i></span> <input type="text"
							class="form-control" placeholder="Search" id="value" /> <span
							class="input-group-btn">
							<button id="btnSearch" class="btn btn-default" type="button">Go!</button>
						</span>
					</div>
				</div>

				<div class="pull-right">
					<div class="form-group">
						<button type="button" class="btn btn-success"
							onclick="Task.showModalPopup($('#myModal'));">Create new
							Task</button>
					</div>
				</div>

				<br> <br>

				<div class="pull-right">
					<div class="form-group">
						<button type="button" class="btn"
							onclick="Task.showModalPopup($('#enableTaskModal'));">Deactivated
							Task</button>
					</div>
				</div>
			</form>
			<br> <br>
			<table class="table  table-striped table-bordered of-tbl-search">
				<thead>
					<tr>
						<th>Activity</th>
						<th>Account Type</th>
						<th>Account</th>
						<th>Remarks</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyTask">
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/tasks" var="urlTask"></spring:url>

			<form id="taskForm" role="form" method="post" action="${urlTask}">
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
									Task</h4>
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
								<h4 class="modal-title" id="viewModalLabel">Task</h4>
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
								<table class="table  table-striped table-bordered">
									<tr>
										<td>Activity</td>
										<td id="lbl_activity"></td>
									</tr>
									<tr>
										<td>Account Type</td>
										<td id="lbl_accountType"></td>
									</tr>
									<tr>
										<td>Account</td>
										<td id="lbl_account"></td>
									</tr>
									<tr>
										<td>Remarks</td>
										<td id="lbl_remarks"></td>
									</tr>
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
			</form>

			<form id="deleteForm" name="deleteForm" action="${urlTask}">
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
								<p>Are you sure you want to delete this Task ?</p>
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

			<div class="modal fade container" id="enableTaskModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Task</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Activity</th>
												<th>Account Type</th>
												<th>Account Profile</th>
											</tr>
										</thead>
										<tbody id="tblEnableTask">
											<c:forEach items="${deactivatedTasks}" var="task">
												<tr>
													<td><input name='task' type='checkbox'
														value="${task.pid}" /></td>
													<td>${task.activityName}</td>
													<td>${task.accountTypeName}</td>
													<td>${task.accountProfileName}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>

								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnActivateTask"
								value="Activate" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>Task</b>
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
											<li class="active"><a href="#accountType">Account
													Type</a></li>
											<li class=""><a href="#activity">Activity</a></li>
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
											<div class="search-results-pane" id="accountType"
												style="display: block;">
												<div class="row">
													<c:forEach items="${accountTypes}" var="accountType">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${accountType.pid}">${accountType.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>
											<div class="search-results-pane" id="activity"
												style="display: none;">
												<div class="row">
													<c:forEach items="${activities}" var="activity">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input value="${activity.pid}"
																	type="checkbox"> ${activity.name}
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
								onclick="Task.filterByActivityAndAccountType(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/task.js" var="taskJs"></spring:url>
	<script type="text/javascript" src="${taskJs}"></script>
</body>
</html>