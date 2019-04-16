<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | InwardOutwardStockLocations</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Inward Outward Stock Locations</h2>
			<div class="row col-xs-12">
				<div class="pull-right">

					<button type="button" class="btn btn-success"
						id="loadStockLocation">Assign Stock Location</button>
				</div>
			</div>
			<!-- onclick="InwardOutwardStockLocation.showModalPopup($('#myModal'));" -->
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Description</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${inwardOutwardStockLocations.content}"
						var="inwardOutwardStockLocation" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${inwardOutwardStockLocation.name}</td>
							<td>${inwardOutwardStockLocation.alias == null ? "" : inwardOutwardStockLocation.alias}</td>
							<td>${inwardOutwardStockLocation.description == null ? "" : inwardOutwardStockLocation.description}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<div class="row-fluid">
				<util:pagination thispage="${inwardOutwardStockLocations}"></util:pagination>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/inwardOutwardStockLocations"
				var="urlInwardOutwardStockLocation"></spring:url>

			<div class="modal fade container" id="myModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign
								StockLocations</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divStockLocations">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br />
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>Stock Location</th>
											</tr>
										</thead>
										<tbody id="tbodyStockLocations">
											<c:forEach items="${stockLocations}" var="stockLocation">
												<tr>
													<td><input name='stockLocation' type='checkbox'
														value="${stockLocation.pid}" /></td>
													<td>${stockLocation.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveStockLocations" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/inward-outward-stock-location.js"
		var="stocklocationJs"></spring:url>
	<script type="text/javascript" src="${stocklocationJs}"></script>
</body>
</html>