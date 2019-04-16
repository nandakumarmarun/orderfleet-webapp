<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Dashboard UI Configuration</title>
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";

	$(document).ready(function() {
		/*  $("#btnColorChange").on('click', function() {
			saveColor();
		});  */
		
	});
	
	function saveUIConfiguration(pid) {
		console.log($('#field_dashboardUIType'+pid+'').val());
		$.ajax({
			url : contextPath + "/web/user-dashboard-ui-configuration/changeUIConfiguration",
			type : "POST",
			data : {
				dashboardUIType : $('#field_dashboardUIType'+pid+'').val(),
				userPid:pid
			},
			success : function(status) {
				onSaveSuccess(status);
			},
		});
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath+"/web/user-dashboard-ui-configuration";
	}
</script>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Dashboard UI Configuration</h2>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Dashboard UI Type</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					 <c:forEach items="${users}" var="user"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${user.firstName}</td>
							<td>${user.dashboardUIType}</td>
							<td> <select id="field_dashboardUIType${user.pid}" name="dashboardUIType"
											class="form-control" onchange="saveUIConfiguration('${user.pid}');" >
											<option value="-1">Select Dashboard UI Type</option>
											<c:forEach var="dashboardUIType" items="${dashboardUITypes}">
												<option value="${dashboardUIType}">${dashboardUIType}</option>
											</c:forEach>
										</select>
  								</td>
						</tr>
					</c:forEach> 
				</tbody>
			</table>
			<hr />
			<spring:url value="/web/user-dashboard-ui-configuration" var="urlUserDashboardUIConfiguration"></spring:url>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

</body>
</html>