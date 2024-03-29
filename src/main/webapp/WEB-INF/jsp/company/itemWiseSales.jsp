<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Item Wise Sales</title>
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
			<h2>Item Wise Transactions</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />

			<form class="form-group">
				<div class="col-md-12">
					<div class="input-group col-md-4" style="float: right;">

						<!-- <span class="input-group-addon btn btn-default"
							onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"><i
							class="glyphicon glyphicon-filter"></i></span> -->

							<input type="text" class="form-control" placeholder="Search" id="search"
							style="width: 200px;" />
						<button id="btnSearch" class="btn btn-info" type="button">Search</button>
					</div>


					<div class="col-sm-2">
						<div class="form-check">
							<input type="checkbox" class="form-check-input"
								id="inclSubOrdinates" checked="checked"> <label
								class="form-check-label" for="inclSubOrdinates">Include
								Subordinate</label>
						</div>
					</div>

					<div class="col-sm-3">
						<div class="form-check">
							<input name='includeStockLocationDetails'
								id="includeStockLocationDetails" type='checkbox' value='no' />
							<label class="form-check-label" for="inclStock"> Include
								Stock Location Details</label>
						</div>
					</div>

				</div>
			</form>

			<div class="row" style="margin-top: 6%;">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">


							<div class="col-sm-3">
								Employee
								<div class=" input-group">
									<span
										class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
										data-toggle='dropdown' aria-haspopup='true'
										aria-expanded='false' title='filter employee'></span>
									<div class='dropdown-menu dropdown-menu-left'
										style='background-color: #F0F0F0'>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
												Employee</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetAllEmployees(this,"no","All Employee")'>All
												Employee</a>
										</div>
									</div>
									<select id="dbEmployee" name="employeePid" class="form-control">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>

							<div class="col-sm-2">
                            	Territory <select id="dbTerittory" name="terittory"
                            									class="form-control selectpicker"  data-live-search="true">
                            									<option value="-1">All Terittories</option>
                            									<c:forEach items="${territories}" var="locationAccountProfile">
                            										<option value="${locationAccountProfile.pid}">${locationAccountProfile.name}</option>
                            									</c:forEach>
                            								</select>
                            							</div>

							<!--<div class="col-sm-2">
								Account <select id="dbAccount" name="accountPid"
									class="form-control">
								</select>
							</div>-->

							<div class="col-sm-2">
                                Account <select id="dbAccount" name="accountPid"
                            									class="form-control">
                            									<option value="-1">All</option>
                            								</select>
                            							</div>
							
							<div class="col-sm-2">
								ProductGroup <select id="dbproductGroup" name="productGroup"
									class="form-control selectpicker" data-live-search="true">
									<option value="-1">All Product Groups</option>
									<c:forEach items="${productGroups}" var="productGroup">
										<option value="${productGroup.pid}">${productGroup.name}</option>
									</c:forEach>
								</select>
							</div>
							

							
							<div class="col-sm-2">
								Document Type <select id="dbDocumentType" name="documentType"
									class="form-control">
									<c:if test="${empty voucherTypes}">
										<option value="no">Select DocumentType</option>
									</c:if>
									<c:forEach items="${voucherTypes}" var="voucherType">
										<option value="${voucherType}">${voucherType}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-2">
								Document <select id="dbDocument" name="documentPid"
									class="form-control">
									<option value="no">All</option>
								</select>
							</div>
							<div class="col-sm-2">
								Day <select class="form-control" id="dbDateSearch"
									onchange="ItemWiseSale.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM(Max 3 months)</option>
								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
                            								<br />
                            								<div class="input-group">
                            									<input type="text" class="form-control" id="txtFromDate"
                            										placeholder="Select From Date" style="background-color: #fff;"
                            										readonly="readonly" />

                            									<div class="input-group-addon">
                            										<a href="#"><i class="entypo-calendar"></i></a>
                            									</div>
                            								</div>
                            							</div>
                            							<div class="col-sm-2 hide custom_date2">
                            								<br />
                            								<div class="input-group">
                            									<input type="text" class="form-control" id="txtToDate"
                            										placeholder="Select To Date" style="background-color: #fff;"
                            										readonly="readonly" />
                            									<div class="input-group-addon">
                            										<a href="#"><i class="entypo-calendar"></i></a>
                            									</div>
                            								</div>
                            							</div>
							
							
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-info"
									onclick="ItemWiseSale.filter()">Apply</button>
							</div>
							<div class="col-sm-1">
								<br>
								<button type="button" class="btn btn-success" id="downloadXls">Download</button>
							</div>
						</div>
					</form>
				</div>
			</div>



			<div class="table-responsive">

				<div class="col-md-12 col-sm-12 clearfix"
					style="font-size: 14px; color: black;">
					<div class="col-sm-3"></div>
					<div class="col-sm-3"></div>
					<div class="col-sm-3"></div>
					<div class="col-sm-3">
						<label>Total : </label> <label id="lblTotal">0</label>
					</div>
				</div>
				<table class="table  table-striped table-bordered"
					id="tblItemWiseSale">
					<thead id="tHeadItemWiseSale">
					</thead>
					<tbody id="tBodyItemWiseSale">
					</tbody>
				</table>
			</div>
			<hr />
			<div class="modal fade custom-width" id="ofModalSearch"
				style="display: none;">
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								Search for: <b>Item Wise Sale</b>
							</h4>
						</div>
						<ul class="nav nav-tabs left-aligned">

							<li class="active" id="filterClass"><a
								onclick="ItemWiseSale.changeToFilter();">Filter</a></li>
							<li class="" id="sortClass"><a
								onclick="ItemWiseSale.changeToSort();">Sort</a></li>
						</ul>
						<div class="modal-body">
							<section class="search-results-env" id="filter"
								style="display: block;">
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
											<li class="active"><a href="#pProfile">Product
													Profile</a></li>
											<li class=""><a href="#pCategory">Product Category</a></li>
											<li class=""><a href="#pGroup">Product Group</a></li>
											<li class=""><a href="#stockLocation">stock Location</a></li>
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
											<div class="search-results-pane" id="pProfile"
												style="display: block;">
												<div class="row">
													<c:forEach items="${productProfiles}" var="productProfile">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${productProfile.pid}">${productProfile.name}
																</label>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>
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
											<div class="search-results-pane" id="stockLocation"
												style="display: none;">
												<div class="row">
													<c:forEach items="${stockLocations}" var="stockLocation">
														<div class="col-md-4">
															<div class="checkbox">
																<label> <input type="checkbox"
																	value="${stockLocation.pid}">${stockLocation.name}
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
							<section class="search-results-env" id="sort"
								style="display: none;">
								<div class="row">

									<div class="col-md-2">
										<label>Sorted By :</label>
									</div>
									<div class="col-md-2">
										<label> <input type="radio" name="sorting"
											value="date" checked="checked"> Date
										</label>
									</div>
									<div class="col-md-2">
										<label> <input type="radio" name="sorting"
											value="category"> Category
										</label>
									</div>
									<div class="col-md-2">
										<label> <input type="radio" name="sorting"
											value="item"> Item
										</label>
									</div>
									<div class="col-md-2">
										<label> <input type="radio" name="sorting"
											value="quantity"> Quantity
										</label>
									</div>
								</div>

								<div class="row" style="margin-top: 5%;">

									<div class="col-md-2">
										<label>Ordered By :</label>
									</div>
									<div class="col-md-2">
										<label> <input type="radio" name="order" value="desc"
											checked="checked"> Descending
										</label>
									</div>
									<div class="col-md-2">
										<label> <input type="radio" name="order" value="asc">
											Ascending
										</label>
									</div>

								</div>

							</section>

						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
							<button type="button" class="btn btn-info"
								onclick="ItemWiseSale.filterByCategoryAndGroup(); $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>


		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/item-wise-sale.js"
		var="itemWiseSaleJs"></spring:url>
	<script type="text/javascript" src="${itemWiseSaleJs}"></script>

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

</body>
</html>