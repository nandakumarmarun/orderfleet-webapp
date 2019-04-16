<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<title>SalesNrich | Inventory Voucher</title>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui-1.11.4.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<!-- Imported styles on this page -->
<spring:url value="/resources/assets/js/select2/select2-bootstrap.css"
	var="select2bootstrapCss"></spring:url>
<link href="${select2bootstrapCss}" rel="stylesheet">

<spring:url value="/resources/assets/js/select2/select2.css"
	var="select2Css"></spring:url>
<link href="${select2Css}" rel="stylesheet">

<style type="text/css">
#select2-drop {
	z-index: 10001
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		//select current user employee
		$("#dbEmployee").val("${currentEmployeePid}");
	});
</script>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Inventory Voucher</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="form-group">
					<div class="col-sm-2">
						Employee <select id="dbEmployee" class="form-control">
							<option value="no">Select Employee</option>
							<c:forEach items="${employees}" var="employee">
								<option value="${employee.pid}">${employee.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						Document <select id="dbDocument" class="form-control">
							<option value="no">Select Document</option>
							<c:forEach items="${activityDocuments}" var="document">
								<option value="${document.pid}">${document.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-3">
						Account <select id="dbAccount" class="form-control"
							name="accoutProfile">
							<option value="no">All Account</option>
						</select>
					</div>
					<div class="col-sm-4">
						<br />
						<button type="button" class="btn btn-orange" style="width: 65px;"
							onclick="InventoryVoucherTransaction.showInventoryForm()">New</button>
						<button type="button" class="btn btn-info"
							onclick="InventoryVoucherTransaction.search()">Search</button>
						<button type="button" class="btn btn-primary"
							onclick="InventoryVoucherTransaction.showReferenceDocuments()">Reference
							Documents</button>
						<button type="button" class="btn btn-green"
							onclick="InventoryVoucherTransaction.returnToTransaction()">Return
							To Txn</button>
					</div>
				</div>
			</div>
			<br />

			<div id="divInventory" class="row" style="display: none;">
				<div class="col-md-12 panel-group joined" id="accordion-test-2">
					<div class="panel panel-primary">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" data-parent="#accordion-test-2"
									href="#collapseOne-2" aria-expanded="false" class="collapsed">
									Header Info </a>
							</h4>
						</div>
						<div id="collapseOne-2" class="panel-collapse collapse"
							aria-expanded="false" style="height: 0px;">
							<div class="panel-body">
								<div class="col-md-12" id="sourceDestinationLocationPrompt"
									style="display: none;">
									<div class="col-sm-6">
										Source Stock Location : <label id="source"
											style="background-color: bisque;"></label>
									</div>
									<div class="col-sm-6">
										Destination Stock Location : <label id="destination"
											style="background-color: bisque;"></label>
									</div>
								</div>
								<div class="col-md-12">
									<div class="col-sm-4">
										Previous Document Number : <label
											id="divPreviousDocumentNumber" style="font-weight: bold;">---</label>
									</div>
									<div class="col-sm-4">
										Document Name : <label id="divDocumentName"
											style="font-weight: bold;">---</label>
									</div>
									<div class="col-sm-4">
										Document Number : <label id="divDocumentNumber"
											style="font-weight: bold;">---</label>
									</div>
								</div>
								<div class="col-md-12">
									<div class="col-sm-3">
										Receiver <select id="dbReceiver" class="form-control"
											name="receiver-account">
											<option value="no">Select Receiver</option>
										</select>
									</div>
									<div class="col-sm-3">
										Supplier <select id="dbSupplier" class="form-control"
											name="supplier-account">
											<option value="no">Select Supplier</option>
										</select>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- list products -->
				<div class="col-md-12">
					Select Product <select id="dbProduct" name="product"
						class="select2"
						onchange="InventoryVoucherTransaction.showInventoryModal()"
						data-allow-clear="true" data-placeholder="Select Product">
						<option value="no">Select Product</option>
						<c:forEach items="${products}" var="product">
							<option value="${product.pid}">${product.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="clearfix"></div>
				<br />
				<div class="clearfix"></div>
				<div class="col-md-12 col-sm-12 "
					style="font-size: 14px; color: black;">

					<div class="col-sm-3">
						<label>Total Quantity : </label> <label id="lblTotalQuantity">0</label>
					</div>
					<div class="col-sm-3">
						<label>Total  : </label> <label
							id="lblTOtalSellingRate">0</label>
					</div>
					<div class="col-sm-3">
						<label>Total Free Quantity : </label> <label
							id="lblTotalFreeQuantity">0</label>
					</div>
				</div>
				<!-- Inventory voucher form -->
				<div id="divInventoryForm" class="col-md-12"
					style="min-height: 250px;">
					<br />
					<div class="table-responsive">
						<table id="tblInventoryDetails"
							class="table  table-striped table-bordered">
							<thead>
								<tr>
									<th>Product</th>
									<th>Quantity</th>
									<th>Selling Rate</th>
									<th>Free Quantity</th>
									<th>Discount Percentage</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody id="tbodyInventoryDetails">
								<tr>
									<td colspan="6">No data avilable</td>
								</tr>
							</tbody>
						</table>
					</div>

					<!-- Model Container-->
					<div class="modal fade container" id="inventoryVoucherModal">
						<!-- model Dialog -->
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<h4 class="modal-title" id="myModalLabel">
										Product : <label id="lblProductName"></label>
									</h4>
								</div>
								<div class="modal-body" style="height: 500px; overflow: auto;">
									<input type="hidden" id="hdnDocumentPid" /> <input
										type="hidden" id="hdnProductPid" />
									<div class="form-group" id="divQUANTITY">
										<label class="control-label" for="QUANTITY">Quantity</label> <input
											autofocus="autofocus" type="number" class="form-control"
											id="QUANTITY" maxlength="5" placeholder="Quantity" min="1" />
									</div>
									<div class="form-group" id="divMRP">
										<label class="control-label" for="MRP">MRP</label> <input
											type="number" class="form-control" id="MRP" maxlength="5"
											placeholder="MRP" />
									</div>
									<div class="form-group" id="divSELLING_RATE">
										<label class="control-label" for="SELLING_RATE">Selling
											Rate</label> <input type="number" class="form-control"
											id="SELLING_RATE" maxlength="5" placeholder="Selling Rate" />
									</div>
									<div class="form-group" id="divSTOCK">
										<label class="control-label" for="STOCK">Stock</label> <input
											type="number" class="form-control" id="STOCK" maxlength="10"
											placeholder="Stock" />
									</div>
									<div class="form-group" id="divSIZE">
										<label class="control-label" for="SIZE">Size</label> <input
											type="number" class="form-control" id="SIZE" maxlength="10"
											placeholder="Size" />
									</div>
									<div class="form-group" id="divUNIT_QUANTITY">
										<label class="control-label" for="UNIT_QUANTITY"
											style="float: none; width: 100%;">Unit Quantity</label> <input
											type="number" class="form-control"
											style="width: 50%; float: left;" id="UNIT_QUANTITY"
											maxlength="10" placeholder="Unit Quantity" />&nbsp;<label
											style="margin-top: 7px;" id="lblSKU"></label>
									</div>
									<div class="form-group" id="divFREE_QUANTITY">
										<label class="control-label" for="FREE_QUANTITY">Free
											Quantity</label> <input type="number" class="form-control"
											id="FREE_QUANTITY" maxlength="5" placeholder="Free Quantity" />
									</div>
									<div class="form-group" id="divDISCOUNT_PERCENTAGE">
										<label class="control-label" for="DISCOUNT_PERCENTAGE">Discount
											Percentage</label> <input type="number" class="form-control"
											id="DISCOUNT_PERCENTAGE" maxlength="5"
											placeholder="Discount Percentage" />
									</div>
									<div class="form-group" id="divDISCOUNT_AMOUNT">
										<label class="control-label" for="DISCOUNT_AMOUNT">Discount
											Amount</label> <input type="number" class="form-control"
											id="DISCOUNT_AMOUNT" maxlength="10"
											placeholder="Discount Amount" />
									</div>
									<div class="form-group" id="divTAX_AMOUNT">
										<label class="control-label" for="TAX_AMOUNT">Tax
											Amount</label> <input type="number" class="form-control"
											id="TAX_AMOUNT" maxlength="10" placeholder="Tax Amount" />
									</div>
									<div class="form-group" id="divVOLUME">
										<label class="control-label" for="VOLUME">Volume</label> <input
											type="number" class="form-control" id="VOLUME" maxlength="5"
											placeholder="Volume" />
									</div>
									<div class="form-group" id="divOFFER_STRING">
										<label class="control-label" for="OFFER_STRING">Offer
											String</label> <input type="text" class="form-control"
											id="OFFER_STRING" maxlength="5" placeholder="Offer String" />
									</div>
									<div class="form-group" id="divREMARKS">
										<label class="control-label" for="REMARKS">REMARKS</label>
										<textarea class="form-control" id="REMARKS" maxlength="200"
											placeholder="REMARKS"></textarea>
									</div>
									<div class="form-group">
										<label class="control-label" for="Total">Total : </label> <label
											id="rowTotal">0.00</label>
									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button
										onclick="InventoryVoucherTransaction.addToInventoryDetails()"
										class="btn btn-primary">Save</button>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal-dialog -->
					</div>
					<!-- /.Model Container-->
				</div>

				<!-- error alert -->
				<div class="col-md-12">
					<div class="alert alert-danger alert-dismissible" role="alert"
						style="display: none;">
						<button type="button" class="close" onclick="$('.alert').hide();"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<p></p>
					</div>
				</div>

				<!-- Submit button -->
				<div class="row">
					<div class="col-md-12">

						<div class="form-group">
							<div class="pull-left">
								<select id="dbStatus" class="form-control">
									<option value="null">Select Order Status</option>
									<c:forEach items="${statuses}" var="status">
										<option value="${status.id}">${status.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="pull-right" style="margin-bottom: 17px;">
								<button type="button" id="btnSubmitInventoryVoucher"
									class="btn btn-default btn-success">Submit</button>
							</div>
						</div>
					</div>
				</div>

			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<!-- Model Container-->
			<div class="modal fade container" id="searchModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Inventory
								Vouchers</h4>
						</div>
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
							<label>Document :</label>&nbsp; <label id="lblDocument"
								style="color: black; font-weight: bold;"></label> &nbsp;&nbsp; <label>Account
								:</label>&nbsp; <label id="lblAccount"
								style="color: black; font-weight: bold;"></label>
							<div class="table-responsive">
								<table class="collaptable table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Date</th>
											<th>Document</th>
											<th>Document Number</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody id="tblInventoryVouchers">

									</tbody>
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

			<!-- Model Container-->
			<div class="modal fade container" id="batchModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Batch</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible alert-batch"
								role="alert" style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<input type="hidden" id="hdnBatchProductPid"> <label>Product
								:</label>&nbsp; <label id="lblBatchProductName"
								style="color: black; font-weight: bold;"></label>
							<div class="table-responsive">
								<table class="collaptable table  table-striped table-bordered">
									<thead>
										<tr>
											<th>Select Batch Number</th>
											<th>Quantity</th>
											<th>Action</th>
										</tr>
										<tr>
											<th><select id="dbBatch" class="select2"
												data-allow-clear="true" data-placeholder="Select Batch">
													<option value="no">Select Batch</option>
											</select></th>
											<th style="width: 10%;"><input type="number"
												class="form-control" style="width: 110px; height: 40px"
												id="txtBatchQuantity" maxlength="5" placeholder="Quantity"
												min="1" /></th>
											<th><button type="button" class="btn btn-success"
													onclick="InventoryVoucherTransaction.addBatchToInventoryDetail()">Add</button></th>
										</tr>
									</thead>
									<tbody id="tblBatchs">
										<tr>
											<td colspan="3">No data available</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">OK</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<!-- Model Container-->
			<div class="modal fade container" id="referenceDocumentModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Reference
								Document</h4>
						</div>
						<div class="modal-body">
							<div class="form-group">
								<label class="control-label">Reference Document</label> <select
									id="dbReferenceDocument" class="form-control">
									<option value="no">Select Reference Document</option>
								</select>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-info" data-dismiss="modal"
								onclick="InventoryVoucherTransaction.searchByReferenceDocument()">Search</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="promptStockLocationModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Stock Location</h4>
						</div>


						<div class="modal-body">
							<div class="form-group">
								<label class="control-label">Source Stock Location</label> <select
									id="dbSourceStockLocation" class="form-control">
									<option value="no">Select Source Stock Location</option>
									<c:forEach items="${sources}" var="source">
										<option value="${source.pid}">${source.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="form-group">
								<label class="control-label">Destination Stock Location</label>
								<select id="dbDestinationStockLocation" class="form-control">
									<option value="no">Select Destination Stock Location</option>
									<c:forEach items="${destinations}" var="destination">
										<option value="${destination.pid}">${destination.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-info" data-dismiss="modal"
								onclick="InventoryVoucherTransaction.saveStockLocation()">Okay</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/app/inventory-voucher-transaction.js"
		var="inventoryVoucherTransactionJs"></spring:url>
	<script type="text/javascript" src="${inventoryVoucherTransactionJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<spring:url value="/resources/assets/js/select2/select2.min.js"
		var="select2Js"></spring:url>
	<script type="text/javascript" src="${select2Js}"></script>
</body>
</html>