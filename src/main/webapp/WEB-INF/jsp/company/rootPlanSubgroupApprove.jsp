<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Route Plan Subgroup Approval</title>
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
			<h2>Route Plan Subgroup Approval</h2>
				<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="RootPlanSubgroupApprove.showModalPopup($('#myModal'));">Create new
						Route Plan Subgroup Approval</button>
				</div>
			</div>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Subgroup</th>
						<th>Approval Required</th>
						<th>Route Plan Based</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${rootPlanSubgroupApproves}" var="rootPlanSubgroupApprove"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${rootPlanSubgroupApprove.userName}</td>
							<td>${rootPlanSubgroupApprove.attendanceStatusSubgroupName }</td>
							<td>${rootPlanSubgroupApprove.approvalRequired}</td>
							<td>${rootPlanSubgroupApprove.rootPlanBased}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="RootPlanSubgroupApprove.showModalPopup($('#viewModal'),'${rootPlanSubgroupApprove.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="RootPlanSubgroupApprove.showModalPopup($('#myModalEdit'),'${rootPlanSubgroupApprove.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="RootPlanSubgroupApprove.showModalPopup($('#deleteModal'),'${rootPlanSubgroupApprove.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/root-plan-subgroup-approves" var="urlRootPlanSubgroupApprove"></spring:url>

			<form id="rootPlanSubgroupApproveForm" role="form" method="post" action="${urlRootPlanSubgroupApprove}">
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
									Root Plan Subgroup Approval</h4>
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
									<label class="control-label" for="field_user">User</label>
									<select id="field_user" name="userPid"
										class="form-control">
										<option value="-1">Select User</option>
										<c:forEach items="${users}" var="user">
											<option value="${user.pid}">${user.firstName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_attendanceStatusSubgroup">Attendance Status Subgroup</label> 
									<select id="field_attendanceStatusSubgroup" name="attendanceStatusSubgroupId"
										class="form-control"><option value="-1">Select
											Attendance Status Subgroup</option>
										<c:forEach items="${attendanceStatusSubgroups}" var="attendanceStatusSubgroup">
											<option value="${attendanceStatusSubgroup.id}">${attendanceStatusSubgroup.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
										<label for="approvalRequired"> <input
											type="radio" id="field_approvalRequired" name="required"/> &nbsp;<span>Approval Required</span>
										</label>
									</div>
									<div class="form-group">
										<label for="rootPlanBased"> <input type="radio"
											id="field_rootPlanBased" name="required"/> &nbsp;<span>Root Plan Based</span>
										</label>
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
			
			<form id="rootPlanSubgroupApproveEditForm" role="form" method="post" action="${urlRootPlanSubgroupApproveEdit}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModalEdit">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Root Plan Subgroup Approval</h4>
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
									<select id="field_userEdit" name="userPid"
										class="form-control" style="display: none;">
										<option value="-1">Select User</option>
										<c:forEach items="${users}" var="user">
											<option value="${user.pid}">${user.firstName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_attendanceStatusSubgroup">Attendance Status Subgroup</label> 
									<select id="field_attendanceStatusSubgroupEdit" name="attendanceStatusSubgroupId"
										class="form-control"><option value="-1">Select
											Attendance Status Subgroup</option>
										<c:forEach items="${attendanceStatusSubgroups}" var="attendanceStatusSubgroup">
											<option value="${attendanceStatusSubgroup.id}">${attendanceStatusSubgroup.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
										<label for="approvalRequired"> <input
											type="radio" id="field_approvalRequiredEdit" name="required"/> &nbsp;<span>Approval Required</span>
										</label>
									</div>
									<div class="form-group">
										<label for="rootPlanBased"> <input type="radio"
											id="field_rootPlanBasedEdit" name="required"/> &nbsp;<span>Root Plan Based</span>
										</label>
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
								<h4 class="modal-title" id="viewModalLabel">Root Plan Subgroup Approval</h4>
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
											<span>User</span>
										</dt>
										<dd>
											<span id="lbl_user"></span>
										</dd>
										<hr />
										<dt>
											<span>Attendance Subgroup</span>
										</dt>
										<dd>
											<span id="lbl_attendanceSubgroup"></span>
										</dd>
										<hr />
										<dt>
											<span>Approval Required</span>
										</dt>
										<dd>
											<span id="lbl_approvalRequired"></span>
										</dd>
										<hr />
										<dt>
											<span>Root Plan Based</span>
										</dt>
										<dd>
											<span id="lbl_rootPlanBased"></span>
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

	<form id="deleteForm" name="deleteForm" action="${urlRootPlanSubgroupApprove}">
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
								<p>Are you sure you want to delete this Root Plan Subgroup Approve?</p>
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

	<spring:url value="/resources/app/root-plan-subgroup-approve.js" var="rootPlanSubgroupApprove"></spring:url>
	<script type="text/javascript" src="${rootPlanSubgroupApprove}"></script>

</body>
</html>