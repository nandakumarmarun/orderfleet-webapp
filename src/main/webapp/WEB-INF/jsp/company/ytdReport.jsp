<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html lang="en">
<head>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | YTD Report</title>

<script type="text/javascript">

	function filter() {
		var territories = $('#divUl input:checkbox:checked').map(function() {
			return this.value;
		}).get();
		getReport(territories);
	}

	
</script>
</head>
<body class="page-body  skin-red">

	<div class="page-container">
		<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->

		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">

			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>

			<hr />
			<!-- the contents starts here-->
			<h2>YTD Report</h2>
			<hr />
			<div class="row">

		<!-- 		<div class=" col-md-12 col-sm-12">

					<div class="panel panel-primary panel-collapse" data-collapsed="0">

						<div class="panel-heading">
							<div class="panel-title">Filter</div>

							<div class="panel-options">
								<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
							</div>
						</div>
						<div class="panel-body" style="display: none;">
							<div id="example-7">
								<div class="nestable-table-list">
									<div id="example-55">
										<h4 class="terittory text-center" style="padding: 1% 0 1% 0;">Territories</h4>
										<div id="divUl"></div>
									</div>
								</div>
								<hr />
								<div align="center">
									<button class="btn btn-blue" onclick="filter()">Apply</button>
								</div>
							</div>
						</div>
					</div>
				</div> -->

				<div class="col-md-12 col-sm-12">
					<div>
						<div class="panel panel-primary" data-collapsed="0">
							<div class="panel-heading">
								<div class="panel-title">&nbsp;</div>

								<div class="panel-options">
									<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
								</div>
							</div>
							<div class="panel-body"
								style="max-width: 100%; overflow-x: scroll;">
								<table class="collaptable table table-striped table-responsive"
									style="border: 2px solid #EBEBEB;">
									<!--table header-->
									<thead>
										<tr
											style="font-weight: bold; "
											class="td-color">
											<th></th>
											<th>TC</th>
											<th>PC</th>
											<th>Efficiency(%)</th>
											<th>Volume</th>
											<th>Amount</th>
										</tr>
										<tr style="font-weight: bold; color: brown;">
											<td>YTD</td>
											<td id="totalTC">0.0</td>
											<td id="totalPC">0.0</td>
											<td id="totalEfficiency">0.0</td>
											<td id="totalVol">0.0</td>
											<td id="totalAmount">0.0</td>
										</tr>
										<tr style="font-weight: bold;">
											<td>Month</td>
											<td>TC</td>
											<td>PC</td>
											<td>Efficiency(%)</td>
											<td>Volume</td>
											<td>Amount</td>
										</tr>
									</thead>
									<!--table header-->
									<tbody id="tblBody">

									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Footer -->
		<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	
	<!--collapse table-->
	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<!-- bottom common scripts -->
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	
	<spring:url value="/resources/app/ytd-report.js" var="ytdReportJs"></spring:url>
	<script type="text/javascript" src="${ytdReportJs}"></script>

</body>
</html>