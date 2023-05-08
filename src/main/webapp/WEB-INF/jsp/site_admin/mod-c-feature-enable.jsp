<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Users</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Enable Mod-c Features</h2>

			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick=" User.ActivatedOnclick(true);">Activate</button>
				</div>

				<div class="pull-right">
                					<button type="button" class="btn btn-danger"
                						onclick=" User.DeActivatedOnclick(false);">DeActivate</button>
                				</div>
			</div>

			<div class="row col-xs-12">
				<div class="form-group col-sm-3">
					<select id="dbCompany" class="form-control selectpicker" data-live-search="true"
						onchange="User.loadUser('Company')" ><option value="no">Filter
							By Company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>
				</div>

			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Status</th>
						<th>Enable</th>
					</tr>
				</thead>
				<tbody id="tBodyUser">
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/enable-mod-c-features.js" var="momentJs"></spring:url>
	<spring:url value="/resources/assets/js/moment.js"
		var="userManagementJs"></spring:url>
	<script type="text/javascript" src="${userManagementJs}"></script>
	<script type="text/javascript" src="${momentJs}"></script>
</body>
</html>