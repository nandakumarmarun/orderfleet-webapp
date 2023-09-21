<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
			<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util" %>
				<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>

					<html lang="en">
					<head>
						<jsp:include page="../fragments/m_head.jsp"></jsp:include>
						<title>SalesNrich | Live Stock</title>
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

								<h2>Live Stocks</h2>

								<br> <br>
								<!-- <div class="row col-xs-12">
									<div class="pull-right">
										<button type="button" class="btn btn-orange" id="btnDownload"
											title="download xlsx">
											<i class="entypo-download"></i> Download
										</button>
									</div>
								</div> -->

								<form class="form-inline">
									<div class="form-group">
										<div class="input-group">
											<input type="text" id="search" placeholder="Search..." class="form-control"
												style="width: 200px;"><span class="input-group-btn">
												<button type="button" class="btn btn-info" id="btnSearch"
													style="float: right;">Search</button>
											</span>
										</div>
									</div>
								</form>

								
								<!-- <form class="form-inline">
									<div class="form-group">
										<div class="input-group">
												<button type="button" class="btn btn-info" id="btnload"
													style="float: right;">Search</button>
											</span>
										</div>
									</div>
								</form> -->
								

								<div class="clearfix"></div>
								<hr />

								<table id="tblLiveStocks" class="table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Created Date</th>
											<th>ProductID</th>
											<th>Product Name</th>
											<th>Stock Location</th>
											<th>Batch</th>
											<th>Opening Stock</th>
											<th>Sold Stock</th>
											<th>Avilable stock</th>
											<th>Replaced stock / Damage Stock </th>
											<th>Last Modified Date</th>
										</tr>
									</thead>
									<tbody id="tbodyOpeningStocks">
									</tbody>
								</table>
								<hr />

								<!-- Footer -->
								<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

							</div>
						</div>
						<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
						<spring:url value="/resources/assets/js/table2excel.js" var="table2excel"></spring:url>
						<script type="text/javascript" src="${table2excel}"></script>
						<spring:url value="/resources/app/live-stock.js" var="StockJs"></spring:url>
						<script type="text/javascript" src="${StockJs}"></script>
					</body>

					</html>