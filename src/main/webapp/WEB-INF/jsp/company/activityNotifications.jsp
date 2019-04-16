<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Activity Notification</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Activity Notifications</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="ActivityNotification.showModalPopup($('#myModal'));">Create
						new Activity Notification</button>
				</div>
			</div>
			<br> <br>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Activity</th>
						<th>Document</th>
						<th>Notification Type</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${activityNotifications}"
						var="activityNotification" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${activityNotification.activityName}</td>
							<td>${activityNotification.documentName}</td>
							<td>${activityNotification.notificationType}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="ActivityNotification.showModalPopup($('#viewModal'),'${activityNotification.activityPid}_${activityNotification.documentPid}_${activityNotification.notificationType}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="ActivityNotification.showModalPopup($('#myModal'),'${activityNotification.activityPid}_${activityNotification.documentPid}_${activityNotification.notificationType}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="ActivityNotification.showModalPopup($('#deleteModal'),'${activityNotification.activityPid}_${activityNotification.documentPid}_${activityNotification.notificationType}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/activity-notifications/activity"
				var="urlActivityNotification"></spring:url>

			<form id="activityNotificationForm" role="form" method="post"
				action="${urlActivityNotification}">
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
								<h4 class="modal-title" id="myModalLabel">Create or edit
									Activity Notification</h4>
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
										<label class="control-label" for="field_activity">Activity
										</label> <select id="field_activity" name="activity"
											class="form-control">
											<option value="-1">Select Activity</option>
											<c:forEach items="${activities}" var="activity">
												<option value="${activity.pid}">${activity.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_document">Document</label>
										<select id="field_document" name="document"
											class="form-control">
											<option value="-1">Select document</option>
											<c:forEach items="${activityDocuments}" var="document">
												<option value="${document.pid}">${document.name}</option>
											</c:forEach>
										</select>
									</div>

									<div class="form-group">
										<label class="control-label" for="notification_type">Notification
											Type</label>
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="SMS" id="sms"
												name="notificationType"> &nbsp;SMS&nbsp;&nbsp;
												<input type="radio" value="SMSDETAILED" id="sms_detailed"
												name="notificationType"> &nbsp;SMS DETAILED&nbsp;&nbsp; <input
												type="radio" value="PUSH" id="push" name="notificationType">
											&nbsp;PUSH&nbsp;&nbsp; <input type="radio" value="EMAIL"
												id="email" name="notificationType">
											&nbsp;EMAIL&nbsp;&nbsp;
										</div>
									</div>

									<div class="form-group">
										<label class="control-label" for="sendCustomer">Send
											Customer</label> <input type="checkbox" class="form-control"
											name="sendCustomer" id="chk_sendCustomer" checked="checked"
											style="width: 4%;" />
									</div>

									<div class="form-group">
										<label class="control-label" for="others">Others</label> <input
											type="checkbox" class="form-control" name="others"
											id="chk_others" style="width: 4%;"
											onchange="ActivityNotification.showPhoneNumber(this);" />
									</div>
									<div id="showPhoneDiv" style="display: none">
										<div class="form-group">
											<label class="control-label" id="lbl_ph_Mail" for="field_phoneNumber">Phone
												Number</label> <br> <input type="text" class="form-control"
												id="field_phoneNumber" maxlength="400"
												placeholder="phone number"
												style="width: 90%; display: inline;" />
											<button id="btnAddOption" type="button"
												class="btn btn-info entypo-plus-circled"
												style="height: 29px;"></button>
										</div>

										<table class="table  table-striped table-bordered">
											<thead>
												<tr>
													<th colspan="2"
														style="font-size: medium; b: n; font-weight: bold;">Phone
														Number Or Mail Id</th>
												</tr>
											</thead>
											<tbody id="tblPhoneNumbers">
											</tbody>
										</table>
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
				</div>
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
								<h4 class="modal-title" id="viewModalLabel">Activity
									Notification</h4>
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
											<span>Activity :</span>
										</dt>
										<dd>
											<span id="lbl_activity"></span>
										</dd>
										<hr />
										<dt>
											<span>Document :</span>
										</dt>
										<dd>
											<span id="lbl_document"></span>
										</dd>
										<hr />
										<dt>
											<span>Notification Type :</span>
										</dt>
										<dd>
											<span id="lbl_notificationType"></span>
										</dd>
										<hr />
									</dl>
									<table class="table  table-striped table-bordered"
										id="tb_Options">
									</table>
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

			<form id="deleteForm" name="deleteForm"
				action="${urlActivityNotification}">
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
									<p>Are you sure you want to delete this Activity
										Notification?</p>
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



		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/activity-notification.js"
		var="activityNotificationJs"></spring:url>
	<script type="text/javascript" src="${activityNotificationJs}"></script>
</body>
</html>