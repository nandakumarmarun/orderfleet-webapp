<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Devices</title>
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
			<h2>User Devices</h2>
			<div class="row col-xs-12"></div>
			<br>
			<form class="form-inline">
				<div class="form-group">
					<div class="input-group">
						<input type="text" id="search" placeholder="Search..."
							class="form-control" style="width: 200px;"><span
							class="input-group-btn">
							<button type="button" class="btn btn-info" id="btnSearch"
								style="float: right;">Search</button>
						</span>
					</div>
				</div>
			</form>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
					    <th>Employee Name</th>
						<th>First Name</th>
						<th>Login name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyUserDevices">
					<c:forEach items="${userDevices}" var="userDevice"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
						    <td>${userDevice.employeeName}</td>
							<td>${userDevice.userFirstName}</td>
							<td>${userDevice.userLoginName}</td>
							<td>
								<button type="button" class="btn btn-info"
									onclick="releaseDevice('${userDevice.pid}', this);">Release
									Device</button>
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
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#btnSearch').click(function() {
				searchTable($("#search").val());
			});
		});

		function searchTable(inputVal) {
			var table = $('#tbodyUserDevices');
			table.find('tr').each(function(index, row) {
				var allCells = $(row).find('td');
				if (allCells.length > 0) {
					var found = false;
					allCells.each(function(index, td) {
						if (index != 7) {
							var regExp = new RegExp(inputVal, 'i');
							if (regExp.test($(td).text())) {
								found = true;
								return false;
							}
						}
					});
					if (found == true)
						$(row).show();
					else
						$(row).hide();
				}
			});
		}

		function releaseDevice(udPid, element) {
			var contextPath = location.protocol + '//' + location.host
					+ location.pathname;
			if (confirm("Are you sure!")) {
				$.ajax({
					url : contextPath + "/" + udPid,
					type : 'PUT',
					success : function(result) {
						$(element).parents('tr').first().remove();
						window.location = contextPath;
					}
				});
			}
		}
	</script>
</body>
</html>