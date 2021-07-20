<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Set Employee Hierarchy Root</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Set Employee Hierarchy Root</h2>
			 <div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="SetEmployeeHierarchy.showModalPopup($('#myModal'));">Add/Edit
						Root</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Action</th>
					</tr>
				</thead>
				 <tbody>
					<tr>
						<td>${root.employeeName}</td>
						<td><button type="button" class="btn btn-blue" onclick="SetEmployeeHierarchy.showModalPopup($('#myModal'),'${root.employeePid}',0);">Edit</button></td>
					</tr>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/set-employee-hierarchy-root"
				var="urlEmployeeHierarchy"></spring:url>
			<form id="setEmployeeHierarchyForm" role="form" method="post"
				action="${urlEmployeeHierarchy}">
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
								<h4 class="modal-title" id="myModalLabel">Create or edit
									Employee Hierarchy Root</h4>
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
									<label class="control-label" for="field_employee">Employee</label>
									<select id="field_employee" name="employeePid"
										class="form-control">
										<option value="-1">Select Employee</option>
										<c:forEach items="${employees}" var="employee">
											<option value="${employee.pid}">${employee.name}</option>
										</c:forEach>
									</select>
								</div>

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


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/set-employee-hierarchy.js"
		var="setEmployeeHierarchyJs"></spring:url>
	<script type="text/javascript" src="${setEmployeeHierarchyJs}"></script>
</body>
</html>