<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Product Profile Groups</title>
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

</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Product Profile Groups</h2>

			<div class="clearfix"></div>
			<hr />

			<form class="form-inline">
				<div class="form-group">
					<div class="input-group">
						<span class="input-group-addon btn btn-default"
							onclick="$(this).next('input').val('');$('#ofModalSearch').modal('show', {backdrop: 'static'});"><i
							class="glyphicon glyphicon-filter"></i></span> <input type="text"
							id="search" placeholder="Search..." class="form-control"
							style="width: 200px;"><span class="input-group-btn">
							<button type="button" class="btn btn-info" id="btnSearch"
								style="float: right;">Search</button>
						</span>
					</div>
				</div>
			</form>
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Category</th>
						<th>Price</th>
						<th>Assigned Product Groups</th>
					</tr>
				</thead>
				<tbody id="tBodyProductProfileGroup">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/productProfileGroups"
				var="urlProductProfileGroup"></spring:url>
		</div>
	</div>

	<div class="modal fade custom-width" id="ofModalSearch"
		style="display: none;">
		<div class="modal-dialog" style="width: 96%">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title">
						Search for: <b>Product Profile Group</b>
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
											<c:forEach items="${productCategories}" var="productCategory">
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
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-info"
						onclick="ProductProfileGroup.filterByCategoryAndGroup(); $('#ofModalSearch').modal('hide');">Apply</button>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery.form.js" var="ajaxForm" />
	<script src="${ajaxForm}"></script>

	<spring:url value="/resources/app/product-profile-group.js"
		var="productProfileGroupJs"></spring:url>
	<script type="text/javascript" src="${productProfileGroupJs}"></script>

</body>
</html>