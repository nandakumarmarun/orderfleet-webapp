<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Task Lists</title>
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
			<h2>User Task Lists</h2>
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
					<c:forEach items="${pageUser.content}" var="user"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${user.firstName}&nbsp;${user.lastName}</td>
							<td>
								<button type="button" class="btn btn-info"
									onclick="UserTaskList.showModalPopup($('#taskListsModal'),'${user.pid}',0);">Assign
									Task Lists</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="row-fluid">
				<util:pagination thispage="${pageUser}"></util:pagination>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/user-taskLists" var="urlUserTaskList"></spring:url>

			<div class="modal fade container" id="taskListsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Task Lists</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="taskListsCheckboxes">
									<table class='table table-striped table-bordered'>
										<c:forEach items="${taskLists}" var="taskList">
											<tr>
												<td><input name='taskList' type='checkbox'
													value="${taskList.pid}" /></td>
												<td>${taskList.name}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveTaskList"
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

	<spring:url value="/resources/app/user-task-list.js" var="userTaskList"></spring:url>
	<script type="text/javascript" src="${userTaskList}"></script>

</body>
</html>