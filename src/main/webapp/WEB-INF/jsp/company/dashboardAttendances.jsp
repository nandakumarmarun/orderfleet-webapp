<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | DashboardAttendance</title>
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
			<h2>Dashboard Attendances</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="DashboardAttendance.showModalPopup($('#myModal'));">Create
						new Dashboard Attendance</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<br>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Name</th>
							<th>Attendance Status</th>
							<th>Attendance Status Subgroup</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody id="tBodyDashboardAttendance">
						<c:forEach items="${dashboardAttendances}" var="dashboardAttendance"
							varStatus="loopStatus">
							<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
								<td>${dashboardAttendance.name}</td>
								<td>${dashboardAttendance.attendanceStatus}</td>
								<td>${dashboardAttendance.attSSubgroupName}</td>
								<td>
									<button type="button" class="btn btn-blue"
										onclick="DashboardAttendance.showModalPopup($('#viewModal'),'${dashboardAttendance.id}',0);">View</button>
									<button type="button" class="btn btn-blue"
										onclick="DashboardAttendance.showModalPopup($('#myModal'),'${dashboardAttendance.id}',1);">Edit</button>
									<button type="button" class="btn btn-danger"
										onclick="DashboardAttendance.showModalPopup($('#deleteModal'),'${dashboardAttendance.id}',2);">Delete</button>
									<button type="button" class="btn btn-green"
										onclick="DashboardAttendance.showModalPopup($('#assignUsersModal'),'${dashboardAttendance.id}',3);">Assign
										User</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/dashboard-attendance" var="urlDashboardAttendance"></spring:url>

			<form id="dashboardAttendanceForm" role="form" method="post"
				action="${urlDashboardAttendance}">
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
									Dashboard Attendance</h4>
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
									<label class="control-label" for="field_groupOrSubgroup">Group / Subgroup</label> <select id="field_groupOrSubgroup" name="groupOrSubgroup"
										class="form-control" onchange="DashboardAttendance.onChangeAttendance();">
										<option value="-1">Select</option>
										<option value="group">Attendance Status</option>
										<option value="subgroup">Attendance Status Subgroup</option>
									</select>
								</div>
								<div id="divAttendanceStatus" class="form-group"
									style="display: none;">
									<label class="control-label" for="field_attendanceStatus">Attendance Status</label> <select id="field_attendanceStatus"
										name="attendanceStatus" class="form-control"><option
											value="PRESENT">PRESENT</option>
										<option value="LEAVE">LEAVE</option>
									</select>
								</div>
								<div id="divAttendanceStatusSubgroup" class="form-group" style="display: none;">
									<label class="control-label" for="field_attendanceStatusSubgroup">Attendance Status Subgroup</label><select
										 id="field_attendanceStatusSubgroup" name="attendanceStatusSubgroup"
										class="form-control" >
										<c:forEach items="${attendanceStatusSubgroups}" var="attendanceStatusSubgroup">
											<option value="${attendanceStatusSubgroup.id}">${attendanceStatusSubgroup.name}</option>
										</c:forEach>
									</select>
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
								<h4 class="modal-title" id="viewModalLabel">Activity</h4>
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
										<span>Attendance Status</span>
									</dt>
									<dd>
										<span id="lbl_attendanceStatus"></span>
									</dd>
									<hr />
									<dt>
										<span>Attendance Status Subgroup</span>
									</dt>
									<dd>
										<span id="lbl_attendanceStatusSubgroup"></span>
									</dd>
									<hr />
								</dl>
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

			<div class="modal fade container" id="assignUsersModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Users</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divUsers">
									<table class='table table-striped table-bordered'>
									<thead>
									<tr><th><input  type='checkbox' class="allcheckbox" /></th><th>Users</th></tr></thead>
									<tbody>
										<c:forEach items="${users}" var="user">
											<tr>
												<td><input name='user' type='checkbox'
													value="${user.pid}" /></td>
												<td>${user.firstName}</td>
											</tr>
										</c:forEach></tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveUsers" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<form id="deleteForm" name="deleteForm" action="${urlDashboardAttendance}">
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
									<p>Are you sure you want to delete this Dashboard Attendance ?</p>
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

	<spring:url value="/resources/app/dashboard-attendance.js"
		var="dashboardAttendanceJs"></spring:url>
	<script type="text/javascript" src="${dashboardAttendanceJs}"></script>
</body>
</html>