<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Creation Request</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Creation Request</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			
			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblUserCreationRequest">
					<thead id="tHeadUserCreationRequest">
						<tr>
							<th>Partner</th>
							<th>Partner Phone</th>
							<th>Company</th>
							<th>Users Count</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody id="tbody_userCreationRequest">
					<c:if test="${empty snrichPartners}">
					<tr><td align="center" colspan = 5>No Data Available</td></tr>
					</c:if>
					<c:forEach items="${snrichPartners}" var="snrichPartner"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${snrichPartner.snrichPartnerName}</td>
							<td>${snrichPartner.snrichPartnerPhone}</td>
							<td>${snrichPartner.companyLegalName}</td>
							<td>${snrichPartner.usersCount}</td>
							<td>
								<c:if test="${not snrichPartner.userAdminExist}">
									<a type="button" class="btn btn-blue"
										onclick="UserCreationRequest.createAdminUsers('${snrichPartner.companyPid}')">
										Create Admin User</a>
								</c:if>
								<a type="button" class="btn btn-blue" 
									onclick="UserCreationRequest.createUsers('${snrichPartner.companyPid}',
										'${snrichPartner.usersCount}',${snrichPartner.id})">
									Create User</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
				<hr />
				<!-- Footer -->
				<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			</div>
			
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	
	<spring:url value="/resources/app/user-creation-request.js" var="userCreationRequestJs"></spring:url>
	<script type="text/javascript" src="${userCreationRequestJs}"></script>
</body>
</html>