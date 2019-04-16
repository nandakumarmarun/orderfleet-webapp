<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Stock Availability Status</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Stock Availability Status</h2>
			<div class="row">

				<div class="col-sm-2">
					<select id="dbFilter" class="form-control">
						<option value="no">All</option>
						<option value="AVAILABLE">Available</option>
						<option value="OUT_OFF_STOCK">Out Off Stock</option>
					</select>
				</div>

				<div class="col-sm-2">
					<select id="dbProductGroup" class="form-control">
						<option value="no">All Product Group</option>
						<c:forEach items="${productGroups}" var="productGroup">
							<option value="${productGroup.pid}">${productGroup.name}</option>
						</c:forEach>
					</select>
				</div>

				<div class="col-sm-2">
					<button type="button" class="btn btn-success"
						onclick="StockAvailabilityStatus.filter()">Apply</button>
				</div>

				<div class="col-sm-2">
					<div class='btn-group' style="display: none;" id="multipleDiv">
						<span data-toggle='dropdown' aria-haspopup='true' title="change status"
							aria-expanded='false'
							class="btn btn-info dropdown-toggle entypo-dot-3"
							style="cursor: pointer;"></span>

						<div class='dropdown-menu dropdown-menu-right'
							style='background-color: #F0F0F0'>
							<div>
								<a class='btn btn-default dropdown-item entypo-check'
									style='width: 100%; text-align: left; color: green'
									onclick='StockAvailabilityStatus.statusChange(this)'>AVAILABLE</a>
							</div>
							<div>
								<a class='btn btn-default dropdown-item entypo-cancel-circled'
									style='width: 100%; text-align: left; color: red;'
									onclick='StockAvailabilityStatus.statusChange(this)'>OUT_OFF_STOCK</a>
							</div>
						</div>
					</div>
				</div>

				<div class="col-sm-4 ">
					<div class="input-group">
						<input type="text" class="form-control" placeholder="Search...."
							id="searchText" style="height: 35px" /> <span
							class="input-group-addon btn btn-default" title="Search"
							id="btnMainSearch"> <i class=" entypo-search"
							style="font-size: 20px"> </i>
						</span>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th><input type="checkbox" class="allcheckbox">All</th>
						<th>Name</th>
						<th>Alias</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody id="tBodyProductProfile">
					<c:forEach items="${productProfiles}" var="productProfile"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td><input name='chk_productProfile' type='checkbox'
								value="${productProfile.pid}" /></td>
							<td>${productProfile.name}</td>
							<td>${productProfile.alias == null ? "" : productProfile.alias}</td>
							<td>
								<div class='btn-group'>
									<span data-toggle='dropdown' aria-haspopup='true'
										id="btn_${productProfile.pid}" aria-expanded='false'
										class="btn dropdown-toggle ${productProfile.stockAvailabilityStatus=='AVAILABLE'? 'btn-success entypo-check':'btn-danger entypo-cancel-circled' }"
										style="cursor: pointer;">${productProfile.stockAvailabilityStatus}</span>

									<div class='dropdown-menu dropdown-menu-right'
										style='background-color: #F0F0F0'>
										<div>
											<a class='btn btn-default dropdown-item entypo-check'
												style='width: 100%; text-align: left; color: green'
												onclick='StockAvailabilityStatus.changeStatus("${productProfile.pid}",this)'>AVAILABLE</a>
										</div>
										<div>
											<a
												class='btn btn-default dropdown-item entypo-cancel-circled'
												style='width: 100%; text-align: left; color: red;'
												onclick='StockAvailabilityStatus.changeStatus("${productProfile.pid}",this)'>OUT_OFF_STOCK</a>
										</div>
									</div>
								</div>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/stock_availability_status.js"
		var="stockAvailabilityStatusJs"></spring:url>
	<script type="text/javascript" src="${stockAvailabilityStatusJs}"></script>
</body>
</html>