<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Ecom Product Profile</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Ecom Product Profile</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="EcomProductProfile.showModalPopup($('#myModal'));">Create
						new Ecom Product Profile</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">
				<div class="pull-left">
					<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="searchEcomProduct"
									placeholder="Search..." class="form-control"
									style="width: 200px;"><span class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearchEcomProduct" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
				</div>
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="EcomProductProfile.showModalPopup($('#enableEcomProductProfileModal'));">Deactivated
						Ecom Product Profile</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Ecom Display Attributes</th>
						<th>Description</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyEcomProduct">
					<c:forEach items="${ecomProductProfiles}" var="ecomProductProfile"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${ecomProductProfile.name}</td>
							<td>${ecomProductProfile.alias == null ? "" : ecomProductProfile.alias}</td>
							<td>${ecomProductProfile.ecomDisplayAttributes}</td>
							<td>${ecomProductProfile.description == null ? "" : ecomProductProfile.description}</td>
							<td><span
								class="label ${ecomProductProfile.activated ? 'label-success' : 'label-danger'}"
								onClick="EcomProductProfile.setActive('${ecomProductProfile.name}','${ecomProductProfile.pid}', '${!ecomProductProfile.activated}')"
								style="cursor: pointer;">${ecomProductProfile.activated ? "Activated" : "Deactivated"}</span>
							</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="EcomProductProfile.showModalPopup($('#viewModal'),'${ecomProductProfile.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="EcomProductProfile.showModalPopup($('#myModal'),'${ecomProductProfile.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="EcomProductProfile.showModalPopup($('#deleteModal'),'${ecomProductProfile.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="EcomProductProfile.showModalPopup($('#productsModal'),'${ecomProductProfile.pid}',3);">Assign
									Products</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/ecom-product-profiles"
				var="urlEcomProductProfile"></spring:url>

			<form id="ecomProductProfileForm" role="form" method="post"
				action="${urlEcomProductProfile}">
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
									Ecom Product Profile</h4>
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
										<label class="control-label" for="field_ecomDisplayAttribute">Ecom
											Display Attributes</label>
										<div id="ecomDisplayAttributesCheckboxes">
											<table class='table table-striped table-bordered'>
												<c:forEach items="${ecomDisplayAttributes}"
													var="ecomDisplayAttribute">
													<tr>
														<td><input name='ecomDisplayAttribute'
															id='ecomDisplayAttribute' type='checkbox'
															value="${ecomDisplayAttribute}" /></td>
														<td>${ecomDisplayAttribute}</td>
													</tr>
												</c:forEach>
											</table>
										</div>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_offerString">Offer
											String</label>
										<textarea class="form-control" name="offerString"
											id="field_offerString" placeholder="offer string"></textarea>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<input type="text" class="form-control" name="description"
											id="field_description" placeholder="Description" />
									</div>
									<div class="form-group" id="divColorImage">
										<label>Choose Image</label> <input type="file" name="image"
											id="field_image" accept="image/*" /> <img id="previewImage"
											src="" style="max-height: 100px;" alt="Image preview...">
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
								<h4 class="modal-title" id="viewModalLabel">Ecom Product
									Profile</h4>
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
											<span>Ecom Display Attributes</span>
										</dt>
										<dd>
											<span id="lbl_ecomDisplayAttributes"></span>
										</dd>
										<hr />
										<dt>
											<span>Offer String</span>
										</dt>
										<dd>
											<span id="lbl_offerString"></span>
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
				action="${urlEcomProductProfile}">
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
									<p>Are you sure you want to delete this Ecom Product
										Profile?</p>
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
									<br>
									<table class='table table-striped table-bordered'>
									<tbody id="tblProducts">
										<c:forEach items="${products}" var="product">
											<tr>
												<td><input name='product' type='checkbox'
													value="${product.pid}" /></td>
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
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="enableEcomProductProfileModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Ecom Product Profile</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Ecom Product Profile</th>
											</tr>
										</thead>
										<tbody id="tblEnableEcomProductProfile">
											<c:forEach items="${deactivatedEcomProductProfiles}"
												var="ecomproductprofile">
												<tr>
													<td><input name='ecomproductprofile' type='checkbox'
														value="${ecomproductprofile.pid}" /></td>
													<td>${ecomproductprofile.name}</td>
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
								id="btnActivateEcomProductProfile" value="Activate" />
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

	<spring:url value="/resources/app/ecomProductProfile.js"
		var="ecomProductProfileJs"></spring:url>
	<script type="text/javascript" src="${ecomProductProfileJs}"></script>
</body>
</html>