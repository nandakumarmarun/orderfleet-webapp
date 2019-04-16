<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Price Level Account Product Group</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Price Level Account Product Group</h2>
			<div class="row col-xs-12">
				<div class="pull-left">
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
				</div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="PriceLevelAccountProductGroup.showModalPopup($('#ofModalSearch'));">Assign
						Price Level Account Product Group</button>
				</div>
			</div>
			<br> <br>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Account</th>
						<th>Product Group</th>
						<th>Price Level</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyPriceLevelAccountProductGroups">
					<c:forEach items="${priceLevelAccountProductGroups}"
						var="priceLevelAccountProductGroup" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${priceLevelAccountProductGroup.accountName}</td>
							<td>${priceLevelAccountProductGroup.productGroupName}</td>
							<td>${priceLevelAccountProductGroup.priceLevelName}</td>

							<td>
								<button type="button" class="btn btn-blue"
									onclick="PriceLevelAccountProductGroup.showModalPopup($('#viewModal'),'${priceLevelAccountProductGroup.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="PriceLevelAccountProductGroup.showModalPopup($('#myModal'),'${priceLevelAccountProductGroup.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="PriceLevelAccountProductGroup.showModalPopup($('#deleteModal'),'${priceLevelAccountProductGroup.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/price-level-account-product-group"
				var="urlPriceLevelAccountProductGroup"></spring:url>

			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>PriceLevel Account ProductGroup</b>
							</h4>
						</div>
						<div class="modal-body">
							<div class="form-group">
								<label class="control-label" for="field_price_Level">Price
									Level</label> <select id="dbPriceLevel" name="priceLevelPid"
									class="form-control" onchange="PriceLevelAccountProductGroup.loadProductGroup();">
									<option value="-1">-- Select --</option>
									<c:forEach items="${priceLevels}" var="priceLevel">
										<option value="${priceLevel.pid}">${priceLevel.name}</option>
									</c:forEach>
								</select>
							</div>
							<section class="search-results-env">
								<div class="row">
									<div class="col-md-12">
										<ul class="nav nav-tabs right-aligned">
											<li class="tab-title pull-left">
												<div>
													<button
														onclick="$('.search-results-panes').find('input[type=checkbox]:checked:visible').removeAttr('checked');"
														type="button" class="btn btn-secondary">Clear All</button>
													<b>&nbsp;</b>
												</div>
											</li>
											<li class="active"><a href="#productGroup">Product
													Group</a></li>
											<li class=""><a href="#account">Account</a></li>
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
											<div class="search-results-pane" id="productGroup"
												style="display: block;">
												<div class="row" id="divProdcutGroup">
													
												</div>
											</div>
																					
											<div class="search-results-pane" id="account"
												style="display: none;">
												<div class="row">
													<c:forEach items="${accounts}" var="account">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${account.pid}">${account.name}
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
								onclick="PriceLevelAccountProductGroup.saveAccountAndProductGroup(); $('#ofModalSearch').modal('hide');">Save</button>
						</div>
					</div>
				</div>
			</div>
			<form id="priceLevelAccountProductGroupForm" role="form"
				method="post" action="${urlPriceLevelAccountProductGroup}">
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
								<h4 class="modal-title" id="myModalLabel">Edit a PriceLevel
									Account ProductGroup</h4>
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
										<label class="control-label" for="field_price_Level">Price
											Level</label> <select id="dbPriceLevel1" name="priceLevelPid"
											class="form-control">
											<option value="-1">-- Select --</option>
											<c:forEach items="${priceLevels}" var="priceLevel">
												<option value="${priceLevel.pid}">${priceLevel.name}</option>
											</c:forEach>
										</select>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_product_group">Product
											Group</label> <select id="dbProductGroup" name="productGroupPid"
											class="form-control">
											<option value="-1">-- Select --</option>
											<c:forEach items="${productGroups}" var="productGroup">
												<option value="${productGroup.pid}">${productGroup.name}</option>
											</c:forEach>
										</select>
									</div>

									<div class="form-group">
										<label class="control-label" for="field_account">Account</label>
										<select id="dbAccount" name="accountPid" class="form-control">
											<option value="-1">-- Select --</option>
											<c:forEach items="${accounts}" var="account">
												<option value="${account.pid}">${account.name}</option>
											</c:forEach>
										</select>
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
								<h4 class="modal-title" id="viewModalLabel">PriceLevelAccountProductGroup</h4>
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
											<span>Account</span>
										</dt>
										<dd>
											<span id="lbl_account"></span>
										</dd>
										<hr />
										<dt>
											<span>Product Group</span>
										</dt>
										<dd>
											<span id="lbl_productgroup"></span>
										</dd>
										<hr />
										<dt>
											<span>Price Level</span>
										</dt>
										<dd>
											<span id="lbl_pricelevel"></span>
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
				action="${urlPriceLevelAccountProductGroup}">
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
									<p>Are you sure you want to delete this
										PriceLevelAccountProductGroup?</p>
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



		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/price-level-account-product-group.js"
		var="priceLevelAccountProductGroupJs"></spring:url>
	<script type="text/javascript" src="${priceLevelAccountProductGroupJs}"></script>
</body>
</html>