
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Company DeActive User Count</title>
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
			<h2>Company- DeactiveUser Count</h2>
			<div class="row col-xs-12"></div>
			<hr>
							<div class="row">
				<div class="form-group">
					<div class="col-sm-3">
						Company<select id="dbCompany" name="companyPid"
							class="form-control selectpicker" data-live-search="true">
							<option value="no">Select Company</option>
							<c:forEach items="${companies}" var="company">
								<option value="${company.id}">${company.legalName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						<br />
						<button id=btnload type="button" class="btn btn-orange" style="width: 65px;">Load</button>
					<hr/>
					<button id=btnloadAll type="button" class="btn btn-green" style="width: 65px;">Load All</button>
					</div>
					<div class="col-sm-3">
						
					</div>
				</div>
				
			</div>
							
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>DeactiveUsers Count</th>
					</tr>
				</thead>
				<tbody id="tableCompanyUserCount">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/company-deactiveuser-count"
				var="url/company-user-count"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/company-deactiveusers-count.js" var="companyDeactiveUsersCountJs"></spring:url>
	<script type="text/javascript" src="${companyDeactiveUsersCountJs}"></script>
</body>
</html>