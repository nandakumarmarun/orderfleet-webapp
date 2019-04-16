<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | OpeningStock</title>
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
			<h2>Opening Stocks</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="OpeningStock.showModalPopup($('#myModal'));">Create
						new Opening Stock</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="OpeningStock.showModalPopup($('#enableOpeningStockModal'));">Deactivated
						OpeningStock</button>
				</div>
			</div>
			<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="search" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearch" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Product Profile</th>
						<th>Batch Number</th>
						<th>Stock Location</th>
						<th>Quantity</th>
						<th>Opening Stock Date</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyOpeningStocks">
					<c:forEach items="${openingStocks}" var="openingStock"
						varStatus="loopStatus">

						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${openingStock.productProfileName}</td>
							<td>${openingStock.batchNumber}</td>
							<td>${openingStock.stockLocationName}</td>
							<td>${openingStock.quantity}</td>
							<td><javatime:format value="${openingStock.openingStockDate}" style="MS" /></td>
							<td><span class="label ${openingStock.activated?'label-success':'label-danger' }" 
							onclick="OpeningStock.setActive('${openingStock.pid}','${!openingStock.activated }')"
							style="cursor: pointer;"
							>${openingStock.activated ?"Activated":"Deactivated"}</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="OpeningStock.showModalPopup($('#viewModal'),'${openingStock.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="OpeningStock.showModalPopup($('#myModal'),'${openingStock.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="OpeningStock.showModalPopup($('#deleteModal'),'${openingStock.pid}',2);">Delete</button>
							</td>
						</tr>


					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/openingStocks" var="urlOpeningStock"></spring:url>

			<form id="openingStockForm" role="form" method="post"
				action="${urlOpeningStock}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Opening Stock</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>

								<div class="form-group">
									<label class="control-label" for="field_productProfile">Product
										Profile</label> <select id="field_productProfile"
										name="productProfilePid" class="form-control">
										<option value="-1">Select Product Profile</option>
										<c:forEach items="${productProfiles}" var="productProfile">
											<option value="${productProfile.pid}">${productProfile.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_batchNumber">Batch
										Number</label> <input autofocus="autofocus" type="text"
										class="form-control" name="batchNumber" id="field_batchNumber"
										maxlength="255" placeholder="Batch Number" />
								</div>

								<div class="form-group">
									<label class="control-label" for="field_stockLocation">Stock
										Location</label> <select id="field_stockLocation"
										name="stockLocationPid" class="form-control"><option
											value="-1">Select Stock Location</option>
										<c:forEach items="${stockLocations}" var="stockLocation">
											<option value="${stockLocation.pid}">${stockLocation.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_quantity">Quantity</label>
									<input type="text" class="form-control" name="quantity"
										id="field_quantity" maxlength="100" placeholder="Quantity" />
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="myFormSubmit" class="btn btn-primary">Save</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form name="viewForm" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="viewModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Opening Stock</h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
								<div class="modal-body">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<table class="table  table-striped table-bordered">
										<tr>
											<td>Batch Number</td>
											<td id="lbl_batchNumber"></td>
										</tr>
										<tr>
											<td>Stock Location</td>
											<td id="lbl_stockLocation"></td>
										</tr>
										<tr>
											<td>Product Profile</td>
											<td id="lbl_productProfile"></td>
										</tr>
										<tr>
											<td>Quantity</td>
											<td id="lbl_quantity"></td>
										</tr>
										<tr>
											<td>Opening Stock Date</td>
											<td id="lbl_openingStockDate"></td>
										</tr>
									</table>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm" action="${urlOpeningStock}">
				<!-- Model Container-->
				<div class="modal fade container" id="deleteModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Confirm delete operation</h4>
							</div>
							<div class="modal-body">

								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<p>Are you sure you want to delete this Opening Stock ?</p>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button class="btn btn-danger">Delete</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

		<div class="modal fade container" id="enableOpeningStockModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable OpeningStock</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>OpeningStock</th>
											</tr>
										</thead>
										<tbody id="tblEnableOpeningStock">
											<c:forEach items="${deactivatedOpeningStocks}"
												var="openingstock">
												<tr>
													<td><input name='openingstock' type='checkbox'
														value="${openingstock.pid}" /></td>
													<td>${openingstock.productProfileName}</td>
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
								id="btnActivateOpeningStock" value="Activate" />
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

	<spring:url value="/resources/app/openingStock.js" var="openingStockJs"></spring:url>
	<script type="text/javascript" src="${openingStockJs}"></script>
</body>
</html>