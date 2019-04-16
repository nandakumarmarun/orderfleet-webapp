<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Task Notification Settings</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Task Notification Settings</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="TaskNotificationSetting.showModalPopup($('#myModal'));">Create new Task Notification Setting</button>
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
						<!-- <th>Columns</th> -->
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					 <c:forEach items="${taskNotificationSettings}" var="taskNotificationSetting"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${taskNotificationSetting.activityName}</td>
							<td>${taskNotificationSetting.documentName}</td>
							<td>${taskNotificationSetting.activityEvent}</td>
							<%-- <td><table class="table">
									<c:forEach items="${taskNotificationSetting.notificationSettingColumns}" var="notificationSettingColumn">
										<tr>
											<td>${notificationSettingColumn.columnName}</td>
										</tr>
									</c:forEach>
								</table></td> --%>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="TaskNotificationSetting.showModalPopup($('#viewModal'),'${taskNotificationSetting.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="TaskNotificationSetting.showModalPopup($('#myModal'),'${taskNotificationSetting.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="TaskNotificationSetting.showModalPopup($('#deleteModal'),'${taskNotificationSetting.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach> 
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/task-notification-settings" var="urlTaskNotificationSetting"></spring:url>

			<form id="taskNotificationSettingForm" role="form" method="post"
				action="${urlTaskNotificationSetting}">
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
									Task Notification Setting</h4>
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
											onchange="TaskNotificationSetting.loadDocuments()" class="form-control"><option
												value="-1">Select Activity</option>
											<c:forEach items="${activities}" var="activity">
												<option value="${activity.pid}">${activity.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_document">Document</label>
										<select id="field_document" name="documentPid"
											onchange=""
											class="form-control"><option value="-1">Select
												Document</option>
										</select>
									</div>
									<div class="form-group" id="checkboxDocument" style="border:2px solid black;display: none;" ></div>
									<div class="form-group">
										<label class="control-label" for="field_activityEvent">Activity
											Event</label> <select id="field_activityEvent" name="activityEvent"
											class="form-control"><option value="-1">Select
												Activity Event</option>
											<option value="ONCREATE">ONCREATE</option>
											<option value="ONDELETE">ONDELETE</option>
										</select>
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
								<h4 class="modal-title" id="viewModalLabel">Task Notification Setting</h4>
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
									<!-- 	<hr />
										<dt>
											<span id="lbl_columnName">Column Name</span>
										</dt> -->
									<!-- 	<dd>
											<span id="lbl_columnLabel"></span>
										</dd> -->
										<hr />
										<dt>
											<span>Activity Event</span>
										</dt>
										<dd>
											<span id="lbl_activityEvent"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlTaskNotificationSetting}">
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
									<p>Are you sure you want to delete this Task Notification Setting?</p>
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

	<spring:url value="/resources/app/task-notification-setting.js" var="taskNotificationSettingJs"></spring:url>
	<script type="text/javascript" src="${taskNotificationSettingJs}"></script>
</body>
</html>