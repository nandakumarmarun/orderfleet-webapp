<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | UnitOfMeasures</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Unit Of Measures</h2>

			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="UnitOfMeasure.showModalPopup($('#myModal'));">Create
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
			</div>
			<div class="clearfix"></div>
			<hr />

			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Uom Id</th>
						<th>Description</th>
						<th>Actions</th>

					</tr>
				</thead>
				<tbody id="tBodyUnitOfMeasure">
					<c:forEach items="${unitOfMeasures}" var="unitOfMeasure"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${unitOfMeasure.name}</td>
							<td>${unitOfMeasure.alias}</td>
							<td>${unitOfMeasure.uomId}</td>
							<td>${unitOfMeasure.description}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="UnitOfMeasure.showModalPopup($('#viewModal'),'${unitOfMeasure.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="UnitOfMeasure.showModalPopup($('#myModal'),'${unitOfMeasure.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="UnitOfMeasure.showModalPopup($('#deleteModal'),'${unitOfMeasure.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="UnitOfMeasure.showModalPopup($('#productsModal'),'${unitOfMeasure.pid}',3);">Assign
									Products</button>
							</td>
					</c:forEach>
				</tbody>
			</table>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/unitOfMeasures" var="urlUnitOfMeasure"></spring:url>

			<form id="unitOfMeasureForm" role="form" method="post"
				action="${urlUnitOfMeasure}">
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
										<label class="control-label" for="field_alias">UoM Id</label> <input
											type="text" class="form-control" name="uomId"
											id="field_uomId" maxlength="55" placeholder="UoM Id" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<textarea class="form-control" name="description"
											id="field_description" placeholder="Description"></textarea>
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
											<span>UoM Id</span>
										</dt>
										<dd>
											<span id="lbl_uomId"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlUnitOfMeasure}">
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
									<p>Are you sure you want to delete this Unit Of Measure?</p>
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
			<!-- /.modal-dialog -->
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/unit-of-measure.js"
		var="unitofMeasureJs"></spring:url>
	<script type="text/javascript" src="${unitofMeasureJs}"></script>
</body>
</html>