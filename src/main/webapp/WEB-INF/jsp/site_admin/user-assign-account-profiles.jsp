<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | UsrAssignAccountProfile</title>
<style type="text/css">
.error {
	color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
	.width83 {
		width: 83%
	}
}
</style>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Account Profiles</h2>

			<div class="form-group">
				<div class="col-md-3 ">
					<select id="field_company" name="companyPid" class="form-control"><option
							value="-1">Select Company</option>
						<c:forEach items="${companies}" var="company">
							<option value="${company.pid}">${company.legalName}</option>
						</c:forEach>
					</select>
				</div>

				<div class="col-md-3">
					<select id="field_user" name="userPid" class="form-control"><option
							value="-1">Select User</option>
					</select>
				</div>
				<button id="btnfindAccountProfile" class="btn btn-info"
					type="button">Go</button>
			</div>
			
				<div class="col-md-2 pull-right">
					<table class="table table-bordered responsive table-striped">
						<thead>
							<tr>
								<th width="10%"
									class="head-bg-th text-center color-black-light f-s-15-b">TOTAL
									COUNT</th>
								<th width="3%" class="text-center btn-info" style="color: rgb(199, 4, 4);" id="totalcount">0</th>
							</tr>
						</thead>
					</table>
				</div>

			<div class="clearfix"></div>
			<hr />
			<br>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Name</th>
							<th>Type</th>
							<th>City</th>
							<th>Address</th>
							<th>Phone 1</th>
							<th>Email 1</th>
							<th>Account Status</th>
							<th>Status</th>
						</tr>
					</thead>
					<tbody id="tBodyAccountProfile">

					</tbody>
				</table>
			</div>
			<%-- <div class="row-fluid">
				<util:pagination thispage="${pageAccountProfile}"></util:pagination>
			</div> --%>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/accountProfiles" var="urlAccountProfile"></spring:url>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/user-assign-account-profile.js"
		var="accountProfileJs"></spring:url>
	<script type="text/javascript" src="${accountProfileJs}"></script>
</body>
</html>