<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Competitor Profile Chart Color</title>
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
			url : contextPath + "/web/competitor-profile-chart-color/changeColor",
			type : "POST",
			data : {
				colorCode : $('#colorValue'+pid+'').val(),
				competitorPid:pid
			},
			success : function(status) {
				onSaveSuccess(status);
			},
		});
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = contextPath+"/web/competitor-profile-chart-color";
	}
</script>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Competitor Profile Chart Color</h2>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Competitor Profile</th>
						<th>Color</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					 <c:forEach items="${competitorProfiles}" var="competitorProfile"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${competitorProfile.name}</td>
							<td style="background-color: ${competitorProfile.chartColor}"></td>
							<td> <input type="color" name="chartColor" id="colorValue${competitorProfile.pid}">
							<input class="btn btn-success" type="button" name="${competitorProfile.pid}"
								id="btnColorChange" onclick="saveColor('${competitorProfile.pid}');" value="Save" />
  								</td>
						</tr>
					</c:forEach> 
				</tbody>
			</table>
			<hr />
			<spring:url value="/web/user-chart-color" var="urlCompetitorProfileChartColor"></spring:url>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

</body>
</html>