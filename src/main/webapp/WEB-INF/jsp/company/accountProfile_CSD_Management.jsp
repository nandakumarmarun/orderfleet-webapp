<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>


<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | AccountProfile_CSD</title>
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

			<h2>Account Profiles CSD Management</h2>
			<hr />
			<div class="col-sm-4 ">
				<div class="input-group">
					<span> <input type="text" id="search"
						,
						placeholder="Search AccountProfiles Here..." size="40"
						width="100px"></input>
					</span>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table table-striped table-bordered of-tbl-search"
				id="tblAccountProfile">
				<thead>
					<tr>
						<th>Name</th>
						<th>Address</th>
						<th>Country</th>
						<th>State</th>
						<th>District</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyAccountProfile">
					<c:forEach items="${AccountProfiles}" var="accountProfiles"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${accountProfiles.name}</td>
							<td>${accountProfiles.address}</td>
							<td width="300px"><select
								id="dbCountrycreate-${accountProfiles.pid}"
								onchange="AccountProfileCSDManagement.showModalPopup($(),'${accountProfiles.pid}',1);"
								name="countryid" class="form-control">
												<option value="-1">Select Country</option> 
									<c:forEach var="country" items="${countries}">
										 <c:choose>
											<c:when test="${country.id == accountProfiles.countryId}">
												<option value="${country.id}" selected>${country.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${country.id}">${country.name}</option>
											 </c:otherwise>
										</c:choose> 
									</c:forEach>
							</select></td>
							<td width="300px"><select
								id="dbStatecreate-${accountProfiles.pid}"
								onchange="AccountProfileCSDManagement.showModalPopup($(),'${accountProfiles.pid}',2);"
								name="stateid" class="form-control">

									<option value="-1">Select State</option>
									<c:forEach var="state" items="${states}">
										<%-- <c:choose>
											<c:when test="${state.id == accountProfiles.stateId}">
												<option value="${state.id}" selected>${state.name}</option>
											</c:when>
											 <c:otherwise>
												<option value="${state.id}">${state.name}</option>
										</c:otherwise>
										</c:choose> --%>
									</c:forEach>
							</select></td>
							<td width="300px"><select
								id="dbDistrictcreate-${accountProfiles.pid}" name="districtid"
								class="form-control">
												<option value="-1">Select District</option>
									<c:forEach var="district" items="${districts}">
										<%-- <c:choose>
											<c:when test="${district.id == accountProfiles.districtId}">
												<option value="${district.id}" selected>${district.name}</option>
											</c:when>
											 <c:otherwise>
												<option value="${district.id}">${district.name}</option>
									</c:otherwise>
										</c:choose> --%>
									</c:forEach>

							</select></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="AccountProfileCSDManagement.showModalPopup($(),'${accountProfiles.pid}',0);">Save</button>

							</td>
						</tr>
					</c:forEach>

				</tbody>
			</table>
			<hr />
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/accountProfile_CSD_Management"
				var="urlaccountProfile_CSD_Management"></spring:url>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/accountProfilesCSDmanagement.js"
		var="accountProfilesCSDmanagementJs"></spring:url>
	<script type="text/javascript" src="${accountProfilesCSDmanagementJs}"></script>
</body>
</html>










