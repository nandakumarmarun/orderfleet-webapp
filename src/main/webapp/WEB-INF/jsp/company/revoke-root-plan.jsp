<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Revoke Route Plan</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
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
			<h2>Revoke Route Plan</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbUser" name="userPid" class="form-control">
									<option value="no">Select User</option>
									<c:forEach items="${users}" var="user">
										<option value="${user.pid}">${user.firstName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="RevokeRootPlan.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<input type="text" id="search" placeholder="Search..."
						class="form-control" style="float: right; width: 200px;">
				</div>
			</div>
			<br>

			<table class="table  table-striped table-bordered" id="tblRevokeRootPlan">
				<thead>
					<tr>
						<th>Route Plan Header</th>
						<th>TaskList</th>
						<th>Task</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody id="tBodyRevokeRootPlan">

				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<div class="modal fade container" id="taskModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Task</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>Activity</th>
												<th>Account Type</th>
												<th>Account</th>
											</tr>
										</thead>
										<tbody id="tblAssigntask">
											
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnActivateAccountTypes" value="Activate" />
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

	<spring:url value="/resources/app/revoke-root-plan.js" var="revokeRootPlanJs"></spring:url>
	<script type="text/javascript" src="${revokeRootPlanJs}"></script>
</body>
</html>