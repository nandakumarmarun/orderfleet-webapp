<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | ProductGroups</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Product Groups</h2>

			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="ProductGroup.showModalPopup($('#assignTaxModal'));">Assign
						Tax</button>
					<button type="button" class="btn btn-success"
						onclick="ProductGroup.showModalPopup($('#assignUnitQtyModal'));">Assign
						Unit Qty</button>
					<button type="button" class="btn btn-success"
						onclick="ProductGroup.showModalPopup($('#myModal'));">Create
						new Product Group</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">

				<div class="pull-left">
					<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="searchProduct" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearchProduct" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
				</div>
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="ProductGroup.showModalPopup($('#enableProductGroupModal'));">Deactivated
						ProductGroup</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />

			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Description</th>
						<th>Status</th>
						<th>Updatable</th>
						<th>Actions</th>

					</tr>
				</thead>
				<tbody id="tBodyProductGroup">
					<c:forEach items="${pageProductGroup}" var="productGroup"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${productGroup.name}</td>
							<td>${productGroup.alias}</td>
							<td>${productGroup.description}</td>
							<td><span
								class="label ${productGroup.activated ? 'label-success' : 'label-danger'}"
								onClick="ProductGroup.setActive('${productGroup.name}','${productGroup.pid}', '${!productGroup.activated}')"
								style="cursor: pointer;">${productGroup.activated ? "Activated" : "Deactivated"}</span>
							</td>
							<td><span
								class="label ${productGroup.thirdpartyUpdate ? 'label-success' : 'label-danger'}"
								onClick="ProductGroup.setThirdpartyUpdate('${productGroup.name}','${productGroup.pid}', '${!productGroup.thirdpartyUpdate}')"
								style="cursor: pointer;">${productGroup.thirdpartyUpdate ? "TRUE" : "FALSE"}</span>
							</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="ProductGroup.showModalPopup($('#viewModal'),'${productGroup.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="ProductGroup.showModalPopup($('#myModal'),'${productGroup.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="ProductGroup.showModalPopup($('#deleteModal'),'${productGroup.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="ProductGroup.showModalPopup($('#productsModal'),'${productGroup.pid}',3);">Assign
									Products</button>
								<button type="button" class="btn btn-info"
									onclick="ProductGroup.showModalPopup($('#ecomProductsModal'),'${productGroup.pid}',4);">Assign
									Ecom Products</button>
							</td>
					</c:forEach>
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/productGroups" var="urlProductGroup"></spring:url>

			<form id="productGroupForm" role="form" method="post"
				action="${urlProductGroup}">
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
									Product Group</h4>
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

								<div class="modal-body" style="overflow: auto;">
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Alias</label> <input
											type="text" class="form-control" name="alias"
											id="field_alias" maxlength="55" placeholder="Alias" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<textarea class="form-control" name="description"
											id="field_description" placeholder="Description"></textarea>
									</div>
									<div class="form-group" id="divColorImage">
										<label>Choose Image</label> <input type="file" name="image"
											id="field_image" accept="image/*" /> <img id="previewImage"
											src="" style="max-height: 100px;" alt="Image preview...">
									</div>

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
								<h4 class="modal-title" id="viewModalLabel">Product Group</h4>
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

									<dl class="dl-horizontal">
										<dt>
											<span>Name</span>
										</dt>
										<dd>
											<span id="lbl_name"></span>
										</dd>
										<hr />
										<dt>
											<span>Alias</span>
										</dt>
										<dd>
											<span id="lbl_alias"></span>
										</dd>
										<hr />
										<dt>
											<span>Description</span>
										</dt>
										<dd>
											<span id="lbl_description"></span>
										</dd>
										<hr />
										<dt>
											<span>Image</span>
										</dt>
										<dd>
											<div id="div_image" style="height: 200px; overflow: auto;"></div>
										</dd>
										<hr />
									</dl>
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

			<form id="deleteForm" name="deleteForm" action="${urlProductGroup}">
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
									<p>Are you sure you want to delete this Product Group?</p>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button class="btn btn-danger">Delete</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<div class="modal fade container" id="productsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Products</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="productsCheckboxes">
									<div class="row">
										<div class="col-md-12 clearfix">
											<input type="radio" value="all" name="filter">
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filter"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="unselected" name="filter">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<div class="row">
										<div class="col-md-12 clearfix">
											<button id="btnNotAssigned" type="button"
												class="btn btn-success">Load Not Assigned Products</button>
										</div>
									</div>

									<br>
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">
													All</th>
												<th>Product</th>
											</tr>
										</thead>
										<tbody id="tblProducts">
											<c:forEach items="${products}" var="product">
												<tr>
													<td><input name='product' type='checkbox'
														value="${product.pid}" style="display: block;" /></td>
													<td>${product.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveProducts"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
			</div>

			<div class="modal fade container" id="ecomProductsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Ecom Products</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="ecomProductsCheckboxes">
									<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<input type="radio" value="all" name="filterEcom">
											&nbsp;All&nbsp;&nbsp; <input type="radio" value="selected"
												name="filterEcom"> &nbsp;Selected&nbsp;&nbsp; <input
												type="radio" value="unselected" name="filterEcom">
											&nbsp;Unselected&nbsp;&nbsp;

											<button type="button" class="btn btn-info" id="btnEcomSearch"
												style="float: right;">Search</button>
											<input type="text" id="searchEcom" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br>
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckboxEcom">
													All</th>
												<th>Product</th>
											</tr>
										</thead>
										<tbody id="tblEcomProducts">
											<c:forEach items="${ecomProducts}" var="ecomProduct">
												<tr>
													<td><input name='ecomProduct' type='checkbox'
														value="${ecomProduct.pid}" style="display: block;" /></td>
													<td>${ecomProduct.name}</td>
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
								id="btnSaveEcomProducts" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="assignTaxModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Tax</h4>
						</div>
						<div class="modal-body">
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<ul class="nav nav-tabs right-aligned">

											<li class="active"><a href="#commonTax">Common Tax</a></li>
											<li class=""><a href="#gstTax">GST Tax</a></li>
										</ul>
										<br>
										<div class="search-results-panes">
											<div class="search-results-pane" id="commonTax"
												style="display: block;">
												<div class="form-group">
													<label class="control-label" for="field_alias">Product
														Groups</label> <select multiple="multiple" id="dbProductGroups"
														class="form-control" style="height: 200px;">
														<c:forEach items="${productGroups}" var="productGroup">
															<option value="${productGroup.pid}">${productGroup.name}</option>
														</c:forEach>
													</select>
												</div>
												<div class="form-group">
													<label class="control-label" for="field_alias">Tax
														Rate</label> <input type="number" id="txtTax" min="0"
														class="form-control" />
												</div>
												<label class="error-msg" style="color: red;"></label>
											</div>
											<div class="search-results-pane" id="gstTax"
												style="display: none;">
												<div class="form-group">
													<label class="control-label" for="field_alias">Product
														Groups</label> <select multiple="multiple" id="dbProductGroup1s"
														class="form-control" style="height: 200px;">
														<c:forEach items="${productGroups}" var="productGroup">
															<option value="${productGroup.pid}">${productGroup.name}</option>
														</c:forEach>
													</select>
												</div>
												<label class="control-label" for="field_alias">Tax
													Masters</label>
												<div class="form-group"
													style="border: 1px solid #f9f2f2; height: 120px; overflow: auto;">
													<table class='table'>
														<c:forEach items="${taxMasters}" var="taxMaster">
															<tr id="taxcheck">
																<td><input name='taxMaster'
																	onchange='ProductGroup.loadProductGroups();'
																	type="checkbox" value="${ taxMaster.pid}"></td>
																<td><c:out value="${taxMaster.vatName }"></c:out></td>
																<td><c:out value="${taxMaster.vatPercentage }"></c:out></td>
															</tr>
														</c:forEach>
													</table>
												</div>
												<label class="error-msg" style="color: red;"></label>
											</div>
										</div>
									</div>
								</div>
							</section>

						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveTax"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="assignUnitQtyModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Unit Quantity</h4>
						</div>
						<div class="modal-body">
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<div class="search-results-panes">
											<div class="search-results-pane" style="display: block;">
												<div class="form-group">
													<label class="control-label" for="field_alias">Product
														Groups</label> <select multiple="multiple" id="dbUQProductGroups"
														class="form-control" style="height: 200px;">
														<c:forEach items="${productGroups}" var="productGroup">
															<option value="${productGroup.pid}">${productGroup.name}</option>
														</c:forEach>
													</select>
												</div>
												<div class="form-group">
													<label class="control-label" for="txtUnitQty">Unit Quantity</label> 
													<input type="number" id="txtUnitQty" min="1" value="1" class="form-control" />
												</div>
												<label class="error-msg" style="color: red;"></label>
											</div>
										</div>
									</div>
								</div>
							</section>

						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveUnitQty"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="enableProductGroupModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable ProductGroup</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>ProductGroup</th>
											</tr>
										</thead>
										<tbody id="tblEnableBank">
											<c:forEach items="${deactivatedProductGroups}"
												var="productgroup">
												<tr>
													<td><input name='productgroup' type='checkbox'
														value="${productgroup.pid}" /></td>
													<td>${productgroup.name}</td>
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
								id="btnActivateProductGroup" value="Activate" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>


			<!-- /.modal-dialog -->
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/product-group.js"
		var="productGroupJs"></spring:url>
	<script type="text/javascript" src="${productGroupJs}"></script>
</body>
</html>