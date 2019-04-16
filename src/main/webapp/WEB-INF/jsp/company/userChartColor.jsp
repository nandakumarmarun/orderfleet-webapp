<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Chart Color</title>
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";

	$(document).ready(function() {
		/*  $("#btnColorChange").on('click', function() {
			saveColor();
		});  */
		
	});
	
	function saveColor(pid) {
		console.log($('#colorValue'+pid+'').val());
		$.ajax({
			url : contextPath + "/web/user-chart-color/changeColor",
			type : "POST",
			data : {
				colorCode : $('#colorValue'+pid+'').val(),
				userPid:pid
			},
			success : function(status) {
				onSaveSuccess(status);
			},
		});
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath+"/web/user-chart-color";
	}
</script>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>User Chart Color</h2>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Color</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					 <c:forEach items="${users}" var="user"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${user.firstName}</td>
							<td style="background-color: ${user.chartColor}"></td>
							<td> <input type="color" name="chartColor" id="colorValue${user.pid}">
							<input class="btn btn-success" type="button" name="${user.pid}"
								id="btnColorChange" onclick="saveColor('${user.pid}');" value="Save" />
  								</td>
						</tr>
					</c:forEach> 
				</tbody>
			</table>
			<hr />
			<spring:url value="/web/user-chart-color" var="urlUserChartColor"></spring:url>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<%-- <spring:url value="/resources/app/user-chart-color.js" var="userChartColorJs"></spring:url>
	<script type="text/javascript" src="${userChartColorJs}"></script> --%>
</body>
</html>