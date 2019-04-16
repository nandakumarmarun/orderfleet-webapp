<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Stock Summary Report</title>
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
			<h2>Stock Summary Report</h2>
			<div class="row col-xs-12"></div>
			
			<div class="row">
				
				<div class="col-md-12 col-sm-12 clearfix">
				<form class="form-group">
				<div class="input-group col-md-10 " style="float: left;">
					<span class="input-group-addon btn btn-default"
						onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"><i
						class="glyphicon glyphicon-filter"></i></span> <input type="text" id="searchReport" placeholder="Search..."
									class="form-control" style="width: 200px;">
									<button type="button" class="btn btn-info"
										id="btnSearchReport" >Search</button>
				</div>
				</form>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table collaptable  table-striped table-bordered">
				<thead>
					<tr>
						<th>Product Name</th>
						<th>Stock</th>
					</tr>
				</thead>
				<tbody id="stockSummary">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>Stock Summary Report</b>
							</h4>
						</div>
						<div class="modal-body">
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<ul class="nav nav-tabs right-aligned">
											<li class="tab-title pull-left">
												<div>
													<button
														onclick="$('.search-results-panes').find('input[type=checkbox]:checked').removeAttr('checked');"
														type="button" class="btn btn-secondary">Clear All</button>
													<b>&nbsp;</b>
												</div>
											</li>
											<li class="active"><a href="#pStockLocation">Stock Location</a></li>
										</ul>
										<form class="search-bar">
											<div class="input-group">
												<input id="ofTxtSearch" type="text"
													class="form-control input-lg" name="search"
													placeholder="Type for search...">
												<div class="input-group-btn">
													<button class="btn btn-lg btn-primary btn-icon"
														style="pointer-events: none;">
														Search <i class="entypo-search"></i>
													</button>
												</div>
											</div>
										</form>
										<hr>
										<div class="search-results-panes">
											<div class="search-results-pane" id="pStockLocation"
												style="display: block;">
												<div class="row">
													<c:forEach items="${stockLocations}" var="stockLocation">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${stockLocation.pid}">${stockLocation.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>

										</div>
									</div>
								</div>
							</section>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
							<button type="button" class="btn btn-info"
								onclick="getReport(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>
			
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<!--collapse table-->
	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<script type="text/javascript">
		var contextPath = "${pageContext.request.contextPath}";

		$(document).ready(function() {
			getReport();
			$('#btnSearchReport').click(function() {
				searchTableReport($("#searchReport").val());
			});
		});
		
		function searchTableReport(inputVal) {
			var table = $('#stockSummary');
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

		function getReport() {
			
			var stockLocationPids = [];
			$("#pStockLocation").find('input[type="checkbox"]:checked').each(function() {
				stockLocationPids.push($(this).val());
			});
			$("#stockSummary")
					.html(
							'<tr><td colspan="2" style="color:black;font-weight: bold;">Please wait...</td></tr>');

			$
					.ajax({
						url : contextPath + "/web/stock-summary-report/load",
						type : 'GET',
						contentType : "application/json",
						data : {
							stockLocationPids : stockLocationPids.join(","),
						},
						success : function(stockSummaryList) {
							$("#stockSummary").html("");
							$
									.each(
											stockSummaryList,
											function(index, stockSummary) {

												$("#stockSummary")
														.append(
																'<tr style="background: beige;" data-id="'+ stockSummary.productPid + '" data-parent="">'
																		+ '<td class="janvary">'
																		+ stockSummary.productName
																		+ '</td><td>'
																		+ stockSummary.quantity
																		+ '</td></tr>');
												$
														.each(
																stockSummary.stockLocationSummarys,
																function(
																		index1,
																		stockLocation) {

																	$(
																			"#stockSummary")
																			.append(
																					'<tr style="background: rgba(255, 228, 196, 0.43);" data-id="'
																							+ stockLocation.stockLocationPid
																							+ '" data-parent="'
																							+ stockSummary.productPid
																							+ '"><td class="janvary">'
																							+ stockLocation.stockLocationName
																							+ '</td><td>'
																							+ stockLocation.quantity
																							+ '</td></tr>');

																});
											});

							$('.collaptable')
									.aCollapTable(
											{
												startCollapsed : true,
												addColumn : false,
												plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
												minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
											});
						}
					});
		}
	</script>
</body>
</html>