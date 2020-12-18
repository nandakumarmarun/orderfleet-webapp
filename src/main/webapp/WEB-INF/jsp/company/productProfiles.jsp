<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | ProductProfile</title>

<!-- Imported styles on this page -->
<spring:url value="/resources/assets/js/select2/select2-bootstrap.css"
	var="select2bootstrapCss"></spring:url>
<link href="${select2bootstrapCss}" rel="stylesheet">

<spring:url value="/resources/assets/js/select2/select2.css"
	var="select2Css"></spring:url>
<link href="${select2Css}" rel="stylesheet">

<style type="text/css">
.error {
	color: red;
}
</style>

<style type="text/css">

/*for upload progress bar */
#progressbox {
	position: relative;
	width: 400px;
	border: 1px solid #ddd;
	padding: 1px;
	border-radius: 3px;
}

#progressbar {
	background-color: lightblue;
	width: 0%;
	height: 20px;
}

#percent {
	position: absolute;
	display: inline-block;
	top: 3px;
	left: 48%;
}
/*****End*******/
#select2-drop {
	z-index: 10001
}

/**THE SAME CSS IS USED IN ALL 3 DEMOS**/
/**gallery margins**/
.zoom {
	-webkit-transition: all 0.35s ease-in-out;
	-moz-transition: all 0.35s ease-in-out;
	transition: all 0.35s ease-in-out;
}

.zoom:hover, .zoom:active, .zoom:focus {
	/**adjust scale to desired size, add browser prefixes**/
	-ms-transform: scale(1.3);
	-moz-transform: scale(1.3);
	-webkit-transform: scale(1.3);
	-o-transform: scale(1.3);
	transform: scale(1.3);
	position: relative;
	z-index: 100;
}
</style>

</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Product Profiles</h2>
			<hr />
			<div class="row">
				<div class="col-sm-5">
					<button type="button" class="btn btn-success"
						onclick="ProductProfile.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>
					<button type="button" class="btn btn-orange" id="btnDownload"
						title="download xlsx">
						<i class="entypo-download"></i> download
					</button>

					<button type="button" class="btn btn-info"
						onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"
						title="filter">
						<i class="glyphicon glyphicon-filter"></i> filter
					</button>
					<button type="button" class="btn btn-primary"
						onclick="ProductProfile.showModalPopup($('#setSizeModal'));"
						title="Set Size">
						<i class="entypo-resize-full"></i> Set Size
					</button>
				</div>

				<div class="col-sm-3">
					<div class="form-group col-sm-7">
						<select id="slt_status" class="form-control" title="sort">
							<option value="All">All</option>
							<option value="Active">Active</option>
							<option value="Deactive">Deactive</option>
							<option value="MultipleActivate">Multiple Activate</option>
						</select>
					</div>
				</div>

				<div class="col-sm-4">
					<div class="input-group">
						<input type="text" class="form-control" placeholder="Search" /> <span
							class="input-group-btn">
							<button
								onclick="Orderfleet.searchTable($(this).parent().prev('input').val());"
								class="btn btn-default entypo-search" type="button"
								style="font-size: 18px"></button>
						</span>
					</div>
				</div>

			</div>
			<div class="clearfix"></div>
			<hr />


			<table class="table table-striped table-bordered of-tbl-search"
				id="tblProductProfile">
				<thead>
					<tr>
						<th>Name</th>
						<th>Category</th>
						<th>Division</th>
						<th>Unit Quantity</th>
						<th>SKU</th>
						<th>Price</th>
						<th>Alias</th>
						<th>Created Date</th>
						<th>Last Updated Date</th>
						<th>Status</th>
						<th>Actions</th>

					</tr>
				</thead>
				<tbody id="tBodyProductProfile">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/productProfiles" var="urlProductProfile"></spring:url>

			<form id="productProfileForm" role="form" method="post"
				action="${urlProductProfile}">
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
									Product Profile</h4>
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
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
								<div class="form-group">
									<label class="control-label" for="field_name">Name</label> <input
										autofocus="autofocus" type="text" class="form-control"
										name="name" id="field_name" maxlength="255" placeholder="Name" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_alias">Alias</label> <input
										type="text" class="form-control" name="alias" id="field_alias"
										maxlength="55" placeholder="Alias" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_productId">Product
										Id</label> <input type="text" class="form-control" name="productId"
										id="field_productId" maxlength="55" placeholder="Product Id" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_category">Category</label>
									<select id="field_category" name="productCategoryPid"
										class="form-control">
										<option value="-1">Select Product Category</option>
										<c:forEach items="${productCategories}" var="productCategory">
											<option value="${productCategory.pid}">${productCategory.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_division">Division</label>
									<select id="field_division" name="divisionPid"
										class="form-control"><option value="-1">Select
											Product Division</option>
										<c:forEach items="${divisions}" var="division">
											<option value="${division.pid}">${division.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_sku">Stock
										Keeping Unit</label> <input type="text" class="form-control"
										name="sku" id="field_sku" maxlength="20"
										placeholder="Stock Keeping Unit" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_unitQty">Unit
										Quantity</label> <input type="number" class="form-control"
										name="unitQty" id="field_unitQty" maxlength="20"
										placeholder="Unit Quantity" value="1" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_price">Price</label> <input
										type="number" class="form-control" name="price"
										id="field_price" maxlength="10" max="1000000"
										placeholder="Price" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_mrp">MRP</label> <input
										type="number" class="form-control" name="mrp" id="field_mrp"
										maxlength="10" max="1000000" placeholder="MRP" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_taxRate">Tax
										Rate</label> <input type="number" class="form-control" name="taxRate"
										id="field_taxRate" maxlength="4" max="10000"
										placeholder="Tax Rate" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_size">Size</label> <input
										type="text" class="form-control" name="size" id="field_size"
										placeholder="Size" value="0" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_hsnCode">HSN
										Code</label> <input type="text" class="form-control" name="hsnCode"
										id="field_hsnCode" placeholder="HSNCODE" />
								</div>
								<div class="form-group" id="divColorImage">
									<label>Choose Color Image</label> <input type="file"
										name="colorImage" id="field_colorImage" accept="image/*" /> <img
										id="previewColorImage" src="" style="max-height: 100px;"
										alt="Image preview...">
								</div>
								<div class="form-group">
									<label class="control-label" for="field_description">Description</label>
									<textarea class="form-control" name="description"
										maxlength="250" id="field_description"
										placeholder="Description"></textarea>
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
								<h4 class="modal-title" id="viewModalLabel">Product Profile</h4>
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
									<table class="table  table-striped table-bordered">
										<tr>
											<td>Name</td>
											<td id="lbl_name"></td>
										</tr>
										<tr>
											<td>Alias</td>
											<td id="lbl_alias"></td>
										</tr>
										<tr>
											<td>Product Id</td>
											<td id="lbl_productId"></td>
										</tr>
										<tr>
											<td>Category</td>
											<td id="lbl_category"></td>
										</tr>
										<tr>
											<td>Division</td>
											<td id="lbl_division"></td>
										</tr>
										<tr>
											<td>Unit Quantity</td>
											<td id="lbl_unitQty"></td>
										</tr>
										<tr>
											<td>Stock Keeping Unit</td>
											<td id="lbl_sku"></td>
										</tr>
										<tr>
											<td>Price</td>
											<td id="lbl_price"></td>
										</tr>
										<tr>
											<td>MRP</td>
											<td id="lbl_mrp"></td>
										</tr>
										<tr>
											<td>Tax Rate</td>
											<td id="lbl_taxRate"></td>
										</tr>
										<tr>
											<td>Size</td>
											<td id="lbl_size"></td>
										</tr>
										<tr>
											<td>HSN Code</td>
											<td id="lbl_hsnCode"></td>
										</tr>
										<tr>
											<td>Color Image</td>
											<td id="lbl_colorImage"></td>
										</tr>
										<tr>
											<td>Description</td>
											<td id="lbl_description"></td>
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

			<form id="deleteForm" name="deleteForm" action="${urlProductProfile}">
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
									<p>Are you sure you want to delete this Product Profile ?</p>
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

			<%-- <form id="uploadImageForm" role="form" method="post"
				action="${urlProductProfile}/upload-image"
				enctype="multipart/form-data">
				<!-- Model Container-->
				<div class="modal fade container" id="uploadImageModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Upload Image</h4>
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
									<div>

<div id="divUpdateProductProfile">
								<table class='table table-striped table-bordered'
									id="field_updateProducts">
								</table>
							</div>

										


									<input type="hidden" id="hdnPid" name="productPid">
									<div class="form-group" id="divUploadFile">
										<label>Choose Image</label> <input id="imgProduct" type="file"
											name="file" id="txtFile" accept="image/*" /> <img
											id="previewProductImage" src="" style="max-height: 100px;"
											alt="Image preview...">
									</div>








									<br />
									<div id="progressbox" style="width: 100%;">
										<div id="progressbar"></div>
										<div id="percent">0%</div>
									</div>
									<div id="message" style="font-weight: bold;"></div>
								</div>



							</div>







						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="btnUploadImage" class="btn btn-primary">Upload</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
		</div>
		<!-- /.Model Container-->
		</form> --%>

			<!-- Model Container-->
			<div class="modal fade container" id="imagesViewModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Product Profile
								Images</h4>
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
							<div id="divProductProfileImages"
								style="overflow: auto; height: 500px;"></div>
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
			<div class="modal fade container" id="setSizeModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Set Size</h4>
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
							<div class="form-group">
								<div class="form-group">
									<label class="control-label" for="txtSize">Size</label> <input
										type="number" class="form-control" id="txtSize" maxlength="10"
										placeholder="Size" />
								</div>
								<div class="form-group">
									<label style="float: left;" class="control-label"
										for="field_products">Products Profiles</label>
								</div>
								<br />
								<div id="divProductProfile">
									<table class='table table-striped table-bordered'
										id="field_products">
									</table>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="btnSaveSize" class="btn btn-primary">Save</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<!-- OF Modal Filter start -->
			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>Product Profile</b>
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
											<li class="active"><a href="#pCategory">Product
													Category</a></li>
											<li class=""><a href="#pGroup">Product Group</a></li>
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
										<div class="search-results-panes">
											<div class="search-results-pane" id="pCategory"
												style="display: block;">
												<div class="row">
													<c:forEach items="${productCategories}"
														var="productCategory">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${productCategory.pid}">${productCategory.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>
											<div class="search-results-pane" id="pGroup"
												style="display: none;">
												<div class="row">
													<c:forEach items="${productGroups}" var="productGroup">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input value="${productGroup.pid}"
																	type="checkbox"> ${productGroup.name}
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
								onclick="ProductProfile.filterByCategoryAndGroup(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>
			<!-- OF Modal Filter end -->

			<div class="modal fade container" id="enableProductProfilesModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Product Profiles</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Product Profile</th>
											</tr>
										</thead>
										<tbody id="tblEnableProductProfile">
											<c:forEach items="${deactivatedProductProfiles}"
												var="productprofile">
												<tr>
													<td><input name='productprofile' type='checkbox'
														value="${productprofile.pid}" /></td>
													<td>${productprofile.name}</td>
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
								id="btnActivateProductProfiles" value="Activate" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<form name="productGroup" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="productGroupModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Product Groups</h4>
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
									<table class="table  table-striped table-bordered">
										<thead>
											<tr>
												<th>Name</th>
												<th>Alias</th>
												<th>Description</th>
											</tr>
										</thead>
										<tbody id="tblProductGroups">

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
				<!-- /.Model Container-->
			</form>

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
							<div class="row">
								<div class="col-md-12">
									<label class="control-label" for="field_alias">Tax
										Masters</label>
									<div class="form-group"
										style="border: 1px solid #f9f2f2; height: 400px; overflow: auto;">
										<table class='table'>
											<c:forEach items="${taxMasters}" var="taxMaster">
												<tr id="taxcheck">
													<td><input name='taxMaster' type="checkbox"
														value="${ taxMaster.pid}"></td>
													<td><c:out value="${taxMaster.vatName }"></c:out></td>
													<td><c:out value="${taxMaster.vatPercentage }"></c:out></td>
												</tr>
											</c:forEach>
										</table>
									</div>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
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


			<div class="modal fade container" id="oploadImage">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%">
					<div class="modal-content" style="width: 100%">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Upload Image</h4>
						</div>
						<div id="divImgeUpload"></div>
						<div class="modal-body"
							style="height: 70%; overflow-x: hidden; overflow-y: scroll;">
							<div class="col-sm-10">
								<div class="fileinput fileinput-new " data-provides="fileinput"
									style="width: 80%; float: right;">
									<div class="fileinput-new thumbnail"
										style="width: 100%; height: 80%;" data-trigger="fileinput">
										<img id="previewImage" src="" alt="">
									</div>
									<div>
										<span class="btn btn-green btn-file"> <span
											id="btnSltImg" class="fileinput-new entypo-picture">
												new image</span> <input type="file" name="image" id="field_image"
											accept="image/*">
										</span>

										<div class="pull-right">
											<a class="btn btn-info" id="btnRefresh"><i
												class="entypo-cw" title="reset"></i> </a> <a
												class="btn btn-success" id="btnSaveFilledFormImage"> <i
												class="entypo-floppy" title="Save"></i>
											</a> <a class="btn btn-danger" id="btnRemoveImage"> <i
												class="entypo-Trash" title="Remove"></i>
											</a>
										</div>
									</div>
									<hr />
								</div>
							</div>

							<div>
								<span id="showImages"></span>
							</div>
							<label class="error-msg" style="color: red;"></label>
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



		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery.form.js" var="ajaxForm" />
	<script src="${ajaxForm}"></script>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/product-profile.js"
		var="productProfileJs"></spring:url>
	<script type="text/javascript" src="${productProfileJs}"></script>
</body>
</html>