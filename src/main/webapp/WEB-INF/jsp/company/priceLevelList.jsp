<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Price Level List</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Price Level List</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="PriceLevelList.showModalPopup($('#myModal'));">Create
						new Price Level List</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbPriceLevel" class="form-control">
									<option value="no">All Price Level</option>
									<c:forEach items="${priceLevels}" var="priceLevel">
										<option value="${priceLevel.pid}">${priceLevel.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<input type="radio" name="searchFilterBy" value="Category"
									checked="checked" />&nbsp;Category &nbsp;&nbsp; <input
									type="radio" name="searchFilterBy" value="Group" />&nbsp;Group
							</div>
							<div class="col-sm-2">
								<select id="dbCategory"
									onchange="PriceLevelList.searchLoadProducts()"
									class="form-control">
									<option value="no">All Category</option>
									<c:forEach items="${categories}" var="category">
										<option value="${category.pid}">${category.name}</option>
									</c:forEach>
								</select> <select id="dbGroup"
									onchange="PriceLevelList.searchLoadProducts()"
									style="display: none;" class="form-control">
									<option value="no">All Group</option>
									<c:forEach items="${groups}" var="group">
										<option value="${group.pid}">${group.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<select id="dbProduct" class="form-control">
									<option value="no">All Product</option>
								</select>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-primary"
									onclick="PriceLevelList.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<button type="button" class="btn btn-info" id="btnSearch"
						style="float: right;">Search</button>
					<input type="text" id="search" placeholder="Search..."
						class="form-control" style="width: 200px; float: right;">
				</div>
			</div>
			<br>
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Price Level</th>
						<th>Product</th>
						<th>Range From</th>
						<th>Range To</th>
						<th>Price</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyPriceLevelList">

				</tbody>
			</table>
			<%-- <div class="row-fluid">
				<util:pagination thispage="${pagePriceLevelList}"></util:pagination>
			</div> --%>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/price-level-list" var="urlPriceLevelList"></spring:url>

			<form id="priceLevelListForm" role="form" method="post"
				action="${urlPriceLevelList}">
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
									Price Level List</h4>
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
										<label class="control-label" for="field_priceLevel">Price
											Level</label> <select id="field_priceLevel" name="priceLevelPid"
											class="form-control"><option value="-1">Select
												Price Level</option>
											<c:forEach items="${priceLevels}" var="priceLevel">
												<option value="${priceLevel.pid}">${priceLevel.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group divProduct">
										<label class="control-label" for="field_priceLevel">Product
											Filter By</label> <br /> <input type="radio" name="filterBy"
											value="Category" checked="checked" />&nbsp;Category
										&nbsp;&nbsp; <input type="radio" name="filterBy" value="Group" />&nbsp;Group

										<select id="field_category"
											onchange="PriceLevelList.loadProducts()" name="categoryPid"
											class="form-control">
											<option value="-1">Select Category</option>
											<c:forEach items="${categories}" var="category">
												<option value="${category.pid}">${category.name}</option>
											</c:forEach>
										</select> <select id="field_group" name="groupPid"
											onchange="PriceLevelList.loadProducts()"
											style="display: none;" class="form-control">
											<option value="-1">Select Group</option>
											<c:forEach items="${groups}" var="group">
												<option value="${group.pid}">${group.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group divProduct">
										<label class="control-label" for="field_product">Product</label>
										<select id="field_product" name="productProfilePid"
											class="form-control"><option value="-1">Select
												Product</option>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_price">Price</label> <input
											type="text" class="form-control" name="price"
											id="field_price" maxlength="10" placeholder="Price" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_rangeFrom">Range
											From</label> <input type="text" class="form-control" name="rangeFrom"
											id="field_rangeFrom" maxlength="50" placeholder="Range From" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_rangeTo">Range
											To</label> <input type="text" class="form-control" name="rangeTo"
											id="field_rangeTo" maxlength="50" placeholder="Range To" />
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
								<h4 class="modal-title" id="viewModalLabel">Price Level
									List</h4>
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
											<span>Price Level</span>
										</dt>
										<dd>
											<span id="lbl_priceLevel"></span>
										</dd>
										<hr />
										<dt>
											<span>Product</span>
										</dt>
										<dd>
											<span id="lbl_productProfile"></span>
										</dd>
										<hr />
										<dt>
											<span>Price</span>
										</dt>
										<dd>
											<span id="lbl_price"></span>
										</dd>
										<hr />
										<dt>
											<span>Range From</span>
										</dt>
										<dd>
											<span id="lbl_rangeFrom"></span>
										</dd>
										<hr />
										<dt>
											<span>Range To</span>
										</dt>
										<dd>
											<span id="lbl_rangeTo"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlPriceLevelList}">
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
									<p>Are you sure you want to delete this Price Level List?</p>
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

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/price-level-list.js"
		var="priceLevelListJs"></spring:url>
	<script type="text/javascript" src="${priceLevelListJs}"></script>
</body>
</html>