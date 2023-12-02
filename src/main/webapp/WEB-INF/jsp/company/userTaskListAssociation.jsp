<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<title>SalesNrich | User Task List Association</title>
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
			<h2>User Task List Assignments</h2>
			<div class="row col-xs-12">
				<form role="form" class="form-horizontal form-groups-bordered">
					<div class="form-group">
						<div class="col-sm-3">
							<select id="dbEmployee" name="employeePid" onchange="UserTaskListAssociation.loadDataTable();" class="form-control">
								<option value=-1>Select Employee</option>
								<c:forEach items="${users}" var="employee">
									<option value="${employee.pid}">${employee.firstName}</option>
								</c:forEach>
							</select>
						</div>
						<div class="col-sm-3">
							<button id="btnDownload" type="button" class="btn btn-success">Download
								Xls</button>
						</div>

						<div class="col-sm-3 pull-right">
							<button type="button" class="btn btn-success"
								onclick="UserTaskListAssociation.showModalPopup($('#myModal'));">Create
								User Task List Association</button>
						</div>
					</div>
				</form>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="collaptable table  table-striped table-bordered" id ="tbltasklistuser">
				<thead>
					<tr>
						<th>Employee</th>
						<th>TaskList</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id ="tbodytasklistuser">
					
				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/user-task-list-association"
				var="urlUserTaskListAssociation"></spring:url>

			<form id="userTaskListAssignmentForm" role="form" method="post"
				action="${urlUserTaskListAssociation}">
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
									User Task List Assignment</h4>
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
									<label class="control-label" for="field_executiveUser">User</label>
									<select id="field_executiveUser" name="executiveUserPid"
										class="form-control">
										<option value="-1">Select User</option>
										<c:forEach items="${users}" var="executiveUser">
											<option value="${executiveUser.pid}">${executiveUser.firstName}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_taskList">Task
										List</label> <select id="field_taskList" name="taskListPid" class="form-control">
                                                         <option value="-1">Select Task List</option>
                                                         <c:forEach items="${taskLists}" var="taskList">
                                                             <option value="${taskList.pid}">
                                                                 <input type="checkbox" name="selectedTaskLists" value="${taskList.pid}">
                                                                 ${taskList.name}
                                                             </option>
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



			<form id="deleteForm" name="deleteForm"
				action="${urlUserTaskListAssociation}">
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
								<p>Are you sure you want to delete this User Task
									Assignment?</p>
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

	<spring:url value="/resources/app/user-task-list-association.js"
		var="userTaskListAssociationJs"></spring:url>
	<script type="text/javascript" src="${userTaskListAssociationJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
	
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>
	
	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>
	
	

</body>
</html>