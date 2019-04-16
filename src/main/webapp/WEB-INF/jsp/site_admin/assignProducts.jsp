<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Assign Products</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
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
			<h2>Assign Products</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbCompany" name="companyPid" class="form-control"
									onchange=" AssignProduct.loadUser()">
									<option value="no">Select Company</option>
									<c:forEach items="${companies}" var="company">
										<option value="${company.pid}">${company.legalName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3">
								<select id="dbUser" name="userPid" class="form-control"
									multiple="multiple">
									<option value="no">Select User</option>
								</select>
							</div>
							<div class="col-sm-3">
								<button type="button" class="btn btn-info"
									onclick=" $('#ofModalSearch').modal('show', {backdrop: 'static'});">Select
									Group and Category</button>
							</div>

							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick=" AssignProduct.assignGroupAndCategory();">Save</button>
							</div>
						</div>
					</form>
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
													<input type="checkbox" class="allcheckbox" name="checkAll">All <b>&nbsp;</b>
												</div>
											</li>
											<li class="active"><a href="#pCategory">Product
													Category</a></li>
											<li class=""><a href="#pGroup">Product Group</a></li>
										</ul>

										<div class="search-results-panes">
											<div class="search-results-pane" id="pCategory"
												style="display: block;">
												<div class="row" id="productCategoryAdd"></div>
											</div>
											<div class="search-results-pane" id="pGroup"
												style="display: none;">
												<div class="row" id="productGroupAdd"></div>
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
								onclick=" $('#ofModalSearch').modal('hide');">Apply</button>
						</div>
					</div>
				</div>
			</div>

			<div class="modal fade container" id="selectedValueModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Confirm</h4>
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
								<h4>Are you sure you want to save?</h4>
								<table class="table">
									<tr>
										<td>Company :</td>
										<td id="companyName"></td>
									</tr>
									<tr>
										<td>User :</td>
										<td id="userName"></td>
									</tr>
									<tr>
										<td>Product Group :</td>
										<td id="selectGroup"></td>
									</tr>
									<tr>
										<td>Product Category :</td>
										<td id="selectCategory"></td>
									</tr>
								</table>

							</div>
						</div>
						<div class="modal-footer">
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button class="btn btn-success" data-dismiss="modal" onclick="AssignProduct.assignProduct();">Confirm</button>
							</div>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/assign-product.js"
		var="assignProductJs"></spring:url>
	<script type="text/javascript" src="${assignProductJs}"></script>

</body>
</html>