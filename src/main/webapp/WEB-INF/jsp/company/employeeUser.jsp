<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Employee User</title>
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
			<h2>Employee User</h2>
			<div class="row col-xs-12"></div>
			<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="search" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearch" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Employee Name</th>
						<th>User Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyemployeeUsers">
					<c:forEach items="${employeeUsers}" var="employee"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${employee.name}</td>
							<td>${employee.userFirstName}&nbsp;${employee.userLastName}</td>
							<td>
								<button type="button" class="btn btn-info" name="Bttn_play"
									onclick="EmployeeUser.showModalPopup($('#usersModal'),'${employee.pid}',0,'${employee.name}');">Assign
									User</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/employee-users" var="urlEmployeeUsers"></spring:url>

			<div class="modal fade container" id="usersModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign User</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 200px">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<div class="form-group">
								<table class='table table-striped table-bordered'>
									<tr>
										<td><label class="control-label" id="EmployeeName"></label></td>
										<td><select id="userId" name="userId"
											class="form-control">
												<option value="-1">Select User</option>
												<c:forEach items="${users}" var="user">
													<option value="${user.pid}">${user.firstName}&nbsp;${user.lastName}</option>
												</c:forEach>
										</select></td>
									</tr>
								</table>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveUser"
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

	<spring:url value="/resources/app/employee-user.js" var="employeeUser"></spring:url>
	<script type="text/javascript" src="${employeeUser}"></script>

</body>
</html>