<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Stock Consumption Report</title>
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
			<h2>Stock Consumption Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="row col-xs-12">
				<div class="pull-left">
					<form class="form-inline">
						<div class="form-group">
							<div class="col-sm-5">
								<select id="dbStockLocation" name="dbStockLocation"
									class="form-control">
									<option value="-1">All Locations</option>
									<c:forEach items="${stockLocations}" var="stockLocation">
										<option value="${stockLocation.pid}">${stockLocation.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</form>
					</div>
					<div class="pull-right">
					<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="searchStockConsumption" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearchStockConsumption" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
				</div>
				</div>
			</div>
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Product Name</th>
						<th>Location</th>
						<th>Opening Stock</th>
						<th>In</th>
						<th>Out</th>
						<th>Closing Stock</th>
					</tr>
				</thead>
				<tbody id="stockConsumption">
					<c:forEach items="${stockConsumptions}" var="stockConsumption"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${stockConsumption.productName}</td>
							<td>${stockConsumption.stockLocationName}</td>
							<td>${stockConsumption.openingStock}</td>
							<td>${stockConsumption.in}</td>
							<td>${stockConsumption.out}</td>
							<td>${stockConsumption.closingStock}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/attendance-report" var="urlAttendanceReports"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<script type="text/javascript">
		$(document).ready(function() {
			$("#dbStockLocation").val("${selectedLocationPid}");
			$('#btnSearchStockConsumption').click(function() {
				searchTableStockConsumption($("#searchStockConsumption").val());
			});

		});
		function searchTableStockConsumption(inputVal) {
			var table = $('#stockConsumption');
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
		$("#dbStockLocation").change(function() {
			var locationPid = this.value;
			var stockConcumptionContextPath = location.protocol + '//' + location.host + "/web/stock-consumption-report/";
			if(this.value === "-1"){
				location.href = stockConcumptionContextPath;
			} else {
				location.href = stockConcumptionContextPath + locationPid
			}
		});
	</script>
</body>
</html>