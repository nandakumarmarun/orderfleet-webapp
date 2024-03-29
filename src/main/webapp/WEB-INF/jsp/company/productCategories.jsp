<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | ProductCategories</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Product Categories</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="ProductCategory.showModalPopup($('#myModal'));">Create
						new Product Category</button>
						<button type="button" class="btn btn-success"
						onclick="ProductCategory.showModalPopup($('#assignDiscountPercentage'));">Assign
						Discount Percentage</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="ProductCategory.showModalPopup($('#enableProductCategoryModal'));">Deactivated
						Product Category</button>
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
						<th>Updateable</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${productCategorys}"
						var="productCategory" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${productCategory.name}</td>
							<td>${productCategory.alias}</td>
							<td>${productCategory.description}</td>
							<td><span
								class="label ${productCategory.activated?'label-success':'label-danger' }"
								onclick="ProductCategory.setActive('${productCategory.name }' ,'${productCategory.pid }' , '${ !productCategory.activated}' )"
								style="cursor: pointer;">${productCategory.activated? "Activated":"Deactivated"}</span></td>

							<td><span
								class="label ${productCategory.thirdpartyUpdate?'label-success':'label-danger' }"
								onclick="ProductCategory.setThirdpartyUpdate('${productCategory.name }' ,'${productCategory.pid }' , '${ !productCategory.thirdpartyUpdate}' )"
								style="cursor: pointer;">${productCategory.thirdpartyUpdate? "TRUE":"FALSE"}</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="ProductCategory.showModalPopup($('#viewModal'),'${productCategory.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="ProductCategory.showModalPopup($('#myModal'),'${productCategory.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="ProductCategory.showModalPopup($('#deleteModal'),'${productCategory.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/productCategories" var="urlProductCategory"></spring:url>

			<form id="productCategoryForm" role="form" method="post"
				action="${urlProductCategory}">
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
									ProductCategory</h4>
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

								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
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
								<h4 class="modal-title" id="viewModalLabel">Product
									Category</h4>
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
									</dl>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm"
				action="${urlProductCategory}">
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
									<p>Are you sure you want to delete this Product Category?</p>
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
			
			<div class="modal fade container" id="assignDiscountPercentage">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Discount Percentage</h4>
						</div>
						<div class="modal-body">
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<div class="search-results-panes">
											<div class="search-results-pane" style="display: block;">
												<div class="form-group">
													<label class="control-label" for="field_alias">Product
														Category</label> <select multiple="multiple" id="dbDPProductCategory"
														class="form-control" style="height: 200px;">
														<c:forEach items="${productCategorys}" var="productCategory">
															<option value="${productCategory.pid}">${productCategory.name}</option>
														</c:forEach>
													</select>
												</div>
												<div class="form-group">
													<label class="control-label" for="txtdiscountPercentage">Discount Percentage</label> 
													<input type="number" id="txtdiscountPercentage" class="form-control" />
												</div>
												<label class="error-msg" style="color: red;"></label>
											</div>
										</div>
									</div>
								</div>
							</section>

						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnsavediscountPercentage"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			
			<div class="modal fade container" id="enableProductCategoryModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Product Category</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="productCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Product Category</th>
											</tr>
										</thead>
										<tbody id="tblEnableProductCategory">
											<c:forEach items="${productCatagoryDeactive}"
												var="deactivatedProductCategory">
												<tr>
													<td><input name='productcategory' type='checkbox'
														value="${deactivatedProductCategory.pid}" /></td>
													<td>${deactivatedProductCategory.name}</td>
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
								id="btnActivateProductCategory" value="Activate" />
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

	<spring:url value="/resources/app/product-category.js"
		var="productCategoryJs"></spring:url>
	<script type="text/javascript" src="${productCategoryJs}"></script>
</body>
</html>