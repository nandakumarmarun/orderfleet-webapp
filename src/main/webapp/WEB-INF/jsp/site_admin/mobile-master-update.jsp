
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Mobile Master Update</title>
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
			<h2>Mobile Master Update</h2>
			<div class="row col-xs-12"></div>
			<hr>
							<div class="row">
				<div class="form-group">
					<div class="col-sm-3">
						Company<select id="dbCompany" name="companyPid"
							class="form-control selectpicker" data-live-search="true">
							<option value="no">Select Company</option>
							<c:forEach items="${companies}" var="company">
								<option value="${company.pid}">${company.legalName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3"> 
						User <select id="dbUser" name="userPid"
							class="form-control">
							<option value="no">All User</option>
						</select>
					</div>
					<div class="col-sm-3">
						<br />
						<button type="button" class="btn btn-orange" style="width: 65px;"
							onclick="MobileMasterUpdate.getMobileMasterUpdate()">Load</button>
					</div>
				</div>
				<div class="col-sm-3">
				<div class="input-group">
						 <input type="text"
							class="form-control" placeholder="Search" id="search"/> <span
							class="input-group-btn">
							<button
								class="btn btn-default" type="button" id="btnSearch">Search</button>
						</span>
						
					</div>
					</div>
			</div>
							
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User name</th>
						<th>Activated</th>
						<th>Created Date</th>
						<th>Last Modified Date</th>
						<th>Device Key</th>
						<th>Last Accessed Device Key</th>
						<th>Last Accessed Login</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tableMobileMasterUpdate">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/mobile-master-update.js" var="mobileMasterUpdateJs"></spring:url>
	<script type="text/javascript" src="${mobileMasterUpdateJs}"></script>
</body>
</html>