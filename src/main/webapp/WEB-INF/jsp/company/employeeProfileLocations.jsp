<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | EmployeeProfile Price Levels</title>
<style type="text/css">
.error {
	color: red;
}
</style>
<spring:url
	value="/resources/assets/plugin/jstree/themes/default/style.min.css"
	var="jstreeCss"></spring:url>
<link href="${jstreeCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Employee Locations</h2>
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
						<th>Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyemployeeProfileLocations">
					<c:forEach items="${employeeProfileLocations}"
						var="employeeProfile" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${employeeProfile.name}</td>
							<td>
								<button type="button" class="btn btn-info"
									onclick="EmployeeProfileLocation.showModalPopup($('#locationsModal'),'${employeeProfile.pid}',0);">Assign
									Locations</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/employeeProfile-locations"
				var="urlEmployeeProfileLocation"></spring:url>

			<div class="modal fade container" id="locationsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Locations</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<label class="error-msg" style="color: red;"></label> <label><input
								id="cbSelectAll" type="checkbox" class="form-control" value="">Select
								All</label>
							<div class="form-group">
								<div id="tree-container"></div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveLocation"
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
	<spring:url value="/resources/assets/plugin/jstree/jstree.min.js"
		var="jstreeJS"></spring:url>
	<spring:url value="/resources/app/employee-location.js"
		var="employeeProfileLocation"></spring:url>
	<script type="text/javascript" src="${jstreeJS}"></script>
	<script type="text/javascript" src="${employeeProfileLocation}"></script>

</body>
</html>