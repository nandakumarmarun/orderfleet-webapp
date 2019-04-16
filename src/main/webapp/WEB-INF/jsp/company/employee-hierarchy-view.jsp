<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<spring:url
	value="/resources/assets/plugin/orgchart/jquery.orgchart.css"
	var="orgchartCss"></spring:url>
<link href="${orgchartCss}" rel="stylesheet">

<style type="text/css">
#chart-container {
	width: 100%;
	height: 100%;
}

.spinner {
	position: fixed;
	top: 50%;
	left: 50%;
	margin-left: -50px; /* half width of the spinner gif */
	margin-top: -50px; /* half height of the spinner gif */
	text-align: center;
	z-index: 1234;
	overflow: auto;
	width: 100px; /* width of the spinner gif */
	height: 102px; /*hight of the spinner gif +2px to fix IE8 issue */
}
</style>

<title>SalesNrich | Employee Hierarchy</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Employee Hierarchy</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="EmployeeHierarchy.showModalPopup($('#myModal'));">Add
					</button>
				</div>
			</div>
			<div id="spinner" class="spinner" style="display: none;">
				<img id="img-spinner"
					src="../../../resources/assets/images/ajax-loader.gif"
					alt="Loading..." />
			</div>
			<div id="chart-container"></div>
		</div>


		<spring:url value="/web/employee-hierarchy" var="urlEmployeeHierarchy"></spring:url>
		<form id="employeeHierarchyForm" role="form" method="post"
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
								Employee Hierarchy</h4>
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
							<div class="form-group">
								<label class="control-label" for="field_parent">Parent</label> <select
									id="field_parent" name="parentPid" class="form-control"><option
										value="-1">Select Parent</option>
									<c:forEach items="${employees}" var="parent">
										<option value="${parent.pid}">${parent.name}</option>
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
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url
		value="/resources/assets/plugin/orgchart/jquery.orgchart.js"
		var="orgchartJS"></spring:url>
	<spring:url
		value="/resources/assets/plugin/orgchart/flat-to-nested-js.js"
		var="flatToNestedJS"></spring:url>

	<script type="text/javascript" src="${orgchartJS}"></script>
	<script type="text/javascript" src="${flatToNestedJS}"></script>

	<spring:url value="/resources/app/employee-hierarchy-view.js"
		var="employeeHierarchyJS"></spring:url>
	<script type="text/javascript" src="${employeeHierarchyJS}"></script>

	<spring:url value="/resources/app/employee-hierarchy-new.js"
		var="employeeHierarchyNewJs"></spring:url>
	<script type="text/javascript" src="${employeeHierarchyNewJs}"></script>
</body>
</html>