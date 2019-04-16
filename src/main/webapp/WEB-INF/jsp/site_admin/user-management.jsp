<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Users</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Users</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="User.showModalPopup($('#myModal'));">Create new
						User</button>
				</div>
			</div>
			<div class="row col-xs-12">
				<div class="form-group col-sm-3">
					<select id="dbCompany" class="form-control"
						onchange="User.filterUsers('Company')"><option value="no">Filter
							By Company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>
				</div>
				<form class="form-inline">
					<div class="form-group">
						<div class="input-group">
							<input type="text" id="search" placeholder="Search..."
								class="form-control" style="width: 200px;"><span
								class="input-group-btn">
								<button type="button" class="btn btn-info" id="btnSearch"
									style="float: right;">Search</button>
							</span>
						</div>
					</div>
				</form>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="User.loadActivateOrDeactivateUser();">Activate
						or Deactivate</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company</th>
						<th>Name</th>
						<th>Login</th>
						<th>Email</th>
						<th>Status</th>
						<th>Roles</th>
						<th>Created Date</th>
						<th>Last Modified By</th>
						<th>Last Modified Date</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyUser">
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/management/users" var="urlUser"></spring:url>

			<form id="userForm" role="form" method="post" action="${urlUser}">
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
									User</h4>
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
										<label class="control-label" for="field_company">Company</label>
										<select id="field_company" name="companyPid"
											class="form-control"><option value="-1">Select
												Company</option>
											<c:forEach items="${companies}" var="company">
												<option value="${company.pid}">${company.legalName}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_login">Login
											Name</label> <input type="text" class="form-control" name="login"
											id="field_login" maxlength="55" placeholder="login" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_firstName">First
											Name</label> <input autofocus="autofocus" type="text"
											class="form-control" name="firstName" id="field_firstName"
											maxlength="255" placeholder="First Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_lastName">Last
											Name</label> <input autofocus="autofocus" type="text"
											class="form-control" name="LastName" id="field_lastName"
											maxlength="255" placeholder="Last Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_email">Email</label> <input
											type="email" class="form-control" name="email"
											id="field_email" maxlength="100" placeholder="Email" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_mobile">Mobile
										</label> <input type="text" class="form-control" name="mobile"
											id="field_mobile" maxlength="12" placeholder="mobile " />
									</div>
									<!-- <div class="form-group">
										<label class="control-label" for="field_deviceKey">Device
											Key </label> <input type="text" class="form-control" name="deviceKey"
											id="field_deviceKey" placeholder="deviceKey " />
									</div> -->
									<div class="form-group">
										<label for="activated"> <input type="checkbox"
											checked="checked" id="field_activated" /> <span>Activated</span>
										</label>
									</div>
									<div class="form-group">
										<label>Role</label> <select class="form-control" required="required"
											multiple="multiple" name="authority" id="field_authority">
											<c:forEach items="${authorities}" var="authority">
												<option value="${authority.name}">${authority.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label for="show_all_user_data"> <input
											type="checkbox" checked="checked" value="TRUE"
											id="field_show_all_user_data" /> <span>Show All User
												Data</span>
										</label>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_dashboardUIType">Dashboard
											UI Type</label> <select id="field_dashboardUIType"
											name="dashboardUIType" class="form-control">
											<option value="TW">TW</option>
											<option value="USR">USR</option>
											<option value="ACW">ACW</option>
										</select>
									</div>
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
								<h4 class="modal-title" id="viewModalLabel">User</h4>
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

									<table class="table table-striped table-bordered ">
										<tr>
											<td>Login</td>
											<td><span id="lbl_login"></span></td>
										</tr>
										<tr>
											<td>First Name</td>
											<td><span id="lbl_firstName"></span></td>
										</tr>
										<tr>
											<td>Last Name</td>
											<td><span id="lbl_lastName"></span></td>
										</tr>
										<tr>
											<td>Company</td>
											<td><span id="lbl_company"></span></td>
										</tr>
										<tr>
											<td>Email</td>
											<td><span id="lbl_email"></span></td>
										</tr>
										<tr>
											<td>Mobile</td>
											<td><span id="lbl_mobile"></span></td>
										</tr>

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

			<form id="deleteForm" name="deleteForm" action="${urlUser}">
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
									<p>Are you sure you want to delete this User?</p>
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

			<div class="modal fade container" id="enableMultipleActivationModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Activate or Deactivate</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div>
								Activate Or Deactivate <select id="field_activatedor"
									class="form-control">
									<option value="activate">Activate</option>
									<option value="deactivate">Deactivate</option>
								</select>
							</div>
							<br>
							<div class="form-group">

								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>User</th>
											</tr>
										</thead>
										<tbody id="tblEnableUser">

										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnActivate"
								value="Ok" />
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

	<spring:url value="/resources/app/user-management.js" var="momentJs"></spring:url>
	<spring:url value="/resources/assets/js/moment.js"
		var="userManagementJs"></spring:url>
	<script type="text/javascript" src="${userManagementJs}"></script>
	<script type="text/javascript" src="${momentJs}"></script>
</body>
</html>