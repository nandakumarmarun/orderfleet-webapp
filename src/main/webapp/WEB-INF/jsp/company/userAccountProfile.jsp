<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Account Profile</title>
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
			<h2>User Account Profile</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${users}" var="user"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${user.firstName}&nbsp;${user.lastName}</td>
							<td>
								<button type="button" class="btn btn-info" name="Bttn_play"
									onclick="UserAccountProfile.showModalPopup($('#accountProfileModal'),'${user.pid}',0,'${user.firstName}');">Assign
									Account Profile</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/user-account-profile"
				var="urlUserAccountProfile"></spring:url>

			<div class="modal fade container" id="accountProfileModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Account Profiles</h4>
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
										<td><label class="control-label" id="userName"></label></td>
										<td><select id="accountProfilePid"
											name="accountProfilePid" class="form-control">
												<option value="-1">Select Account Profile</option>
												<c:forEach items="${accountProfiles}" var="account">
													<option value="${account.pid}">${account.name}</option>
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

	<spring:url value="/resources/app/user-account-profile.js"
		var="userAccountProfile"></spring:url>
	<script type="text/javascript" src="${userAccountProfile}"></script>

</body>
</html>