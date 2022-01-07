<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Location_AccountProfiles</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Location AccountProfile Management</h2>
			<hr/>
			<div class="col-sm-4 ">
				<div class="input-group">
					<span>
					<input type="text" id="search" placeholder="   Search Here..." size="40" width="100px"></input>
					</span>
				</div>
			</div>


			<div class="clearfix"></div>
			<hr />
			<table id="tBodyAccountProfileDemo" class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Location</th>
						<th>Assign New Location</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${AccountProfiles}" var="accountProfiles"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${accountProfiles.name}</td>
							<td>${accountProfiles.locationName}</td>
							<td><label class="control-label" for="field_location"></label>
								<select id="field_location-${accountProfiles.pid}"
								name="locations" class="form-control">
									<option value="-1">Select Location</option>
									<c:forEach items="${locations}" var="location">
										<option value="${location.pid}">${location.name}</option>
									</c:forEach>
							</select></td>

							<td>
								<button type="button" class="btn btn-blue"
									onclick="LocationAccountProfilesManagement.showModalPopup($(),'${accountProfiles.pid}',0);">Save</button>

							</td>
						</tr>
					</c:forEach>
				</tbody>

			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/locationAccountProfileManagement"
				var="urllocationAccountProfileManagement"></spring:url></div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/locationAccountProfilesManagement.js"
		var="locationAccountProfilesManagementJs"></spring:url>
	<script type="text/javascript" src="${locationAccountProfilesManagementJs}"></script>
</body>
</html>