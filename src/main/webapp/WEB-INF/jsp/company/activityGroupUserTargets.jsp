<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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

<title>SalesNrich | ActivityGroupUserTarget</title>
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
			<h2>Activity Group User Targets</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="ActivityGroupUserTarget.showModalPopup($('#myModal'));">Create
						new Activity Group User Target</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Activity Group</th>
						<th>Start Date</th>
						<th>End Date</th>
						<th>Target Number</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${activityGroupUserTargets}"
						var="activityGroupUserTarget" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${activityGroupUserTarget.userName}</td>
							<td>${activityGroupUserTarget.activityGroupName}</td>
							<td>${activityGroupUserTarget.startDate}</td>
							<td>${activityGroupUserTarget.endDate}</td>
							<td>${activityGroupUserTarget.targetNumber}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="ActivityGroupUserTarget.showModalPopup($('#viewModal'),'${activityGroupUserTarget.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="ActivityGroupUserTarget.showModalPopup($('#myModal'),'${activityGroupUserTarget.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="ActivityGroupUserTarget.showModalPopup($('#deleteModal'),'${activityGroupUserTarget.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/activity-group-user-targets"
				var="urlActivityGroupUserTarget"></spring:url>

			<form id="activityGroupUserTargetForm" role="form" method="post"
				action="${urlActivityGroupUserTarget}">
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
									Activity Group User Target</h4>
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
									<label class="control-label" for="field_user">User</label> <select
										id="field_user" name="userPid" class="form-control"><option
											value="-1">Select User</option>
										<c:forEach items="${users}" var="user">
											<option value="${user.pid}">${user.firstName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_activityGroup">Activity
										Group</label> <select id="field_activityGroup" name="activityGroupPid"
										class="form-control">
										<option value="-1">Select Activity Group</option>
										<c:forEach items="${activityGroups}" var="activityGroup">
											<option value="${activityGroup.pid}">${activityGroup.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_startDate">Start
										Date</label> <input type="text" class="form-control" name="startDate"
										id="field_startDate" maxlength="55" placeholder="Start Date" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_endDate">End
										Date</label> <input type="text" class="form-control" name="endDate"
										id="field_endDate" maxlength="55" placeholder="End Date" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_targetNumber">Target
										Number</label> <input type="number" class="form-control"
										name="targetNumber" id="field_targetNumber" maxlength="8"
										placeholder="Target Number" />
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
								<h4 class="modal-title" id="viewModalLabel">Activity User
									Target</h4>
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
									<table class="table  table-striped table-bordered">
										<tr>
											<td>User</td>
											<td id="lbl_user"></td>
										</tr>
										<tr>
											<td>Activity Group</td>
											<td id="lbl_activityGroup"></td>
										</tr>
										<tr>
											<td>Target Number</td>
											<td id="lbl_targetNumber"></td>
										</tr>
										<tr>
											<td>Start Date</td>
											<td id="lbl_startDate"></td>
										</tr>
										<tr>
											<td>End Date</td>
											<td id="lbl_endDate"></td>
										</tr>
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
				action="${urlActivityGroupUserTarget}">
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
									<p>Are you sure you want to delete this Activity User
										Target ?</p>
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

	<spring:url value="/resources/app/activity-group-user-target.js"
		var="activityGroupUserTargetJs"></spring:url>
	<script type="text/javascript" src="${activityGroupUserTargetJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#field_startDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			$("#field_endDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
		});
	</script>
</body>
</html>