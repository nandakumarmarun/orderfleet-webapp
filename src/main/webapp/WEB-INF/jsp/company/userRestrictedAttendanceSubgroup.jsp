<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Restricted Attendance Subgroup</title>

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
			<h2>User Restricted Attendance Subgroup</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${users}" var="user" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${user.firstName}&nbsp;${user.lastName}</td>
							<td>
								<button type="button" class="btn btn-info"
									onclick="UserRestrictedAttendanceSubgroup.showModalPopup($('#restrictedAttendanceSubgroupModal'),'${user.pid}',0);">Assign
									Restricted Attendance Subgroup</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/user-restricted-attendance-subgroup"
				var="urlUserRestrictedAttendanceSubgroup"></spring:url>

			<div class="modal fade container"
				id="restrictedAttendanceSubgroupModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Restricted Attendance
								Subgroup</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="documentsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th></th>
												<th>Attendance Status Subgroup</th>
											</tr>
										</thead>
										<tbody id="tblDocuments">
										
											<c:forEach items="${attendanceSubgroups}"
												var="attendanceSubgroup">
												<tr>
												<td><input  class="form-control"name='attendanceSubgroup' type='checkbox' value='${attendanceSubgroup.id}' /></td>
												<td>${attendanceSubgroup.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSave"
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

	<spring:url value="/resources/app/user-restricted-attendance-subgroup.js" var="userRestrictedAttendanceSubgroupJs"></spring:url>
	<script type="text/javascript" src="${userRestrictedAttendanceSubgroupJs}"></script>

</body>
</html>