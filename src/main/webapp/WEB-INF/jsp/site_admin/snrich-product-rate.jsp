<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Partners</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>

<body class="page-body" data-url="">
	
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>SalesnRich Partner Rate</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="SnrichProductRate.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>SalesnRich Product Name</th>
						<th>Payment Mode</th>
						<th>Rate / User</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:if test = "${empty snrichProductRates}">
						<tr><td colspan = 4 style = "text-align: center;">No Data Available</td></tr>
					</c:if>
					<c:forEach items="${snrichProductRates}" var="snrichProductRate" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${snrichProductRate.snrichProduct.name == null ? "" : snrichProductRate.snrichProduct.name}</td>
							<td>${snrichProductRate.orderProPaymentMode == null ? "" : snrichProductRate.orderProPaymentMode}</td>
							<td>${snrichProductRate.rate == null ? "" : snrichProductRate.rate}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="SnrichProductRate.showModalPopup($('#myModal'),'${snrichProductRate.pid}',1);" 
									title="Edit"><i class="entypo-pencil"></i></button>
								<button type="button" class="btn btn-blue"
									onclick="SnrichProductRate.showModalPopup($('#deleteModal'),'${snrichProductRate.pid}',2);" 
									title="Delete"><i class="entypo-trash"></i></button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<spring:url value="/web/snrich-product-rate" var="urlSnrichProductRate"></spring:url>

			<form id="createEditForm" role="form" method="post"
				action="${urlSnrichProductRate}">
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
									SalenRich Product Rate </h4>
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
										<label class="control-label" for="field_snrich_product">SalesnRich Product</label>
										<select id="field_snrich_product" name="snrichProductPid" class="form-control">
										<option value="-1">Select Product</option>
											<c:forEach items="${snrichProducts}" var="snrichProduct">
												<option value="${snrichProduct.pid}">${snrichProduct.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_paymentMode">Payment Mode</label> <select
											id="field_paymentMode" name="orderProPaymentMode" class="form-control">
											<option value="-1">Select Payment Mode</option>
											<c:forEach items="${orderProPaymentModes}" var="orderProPaymentMode">
												<option value="${orderProPaymentMode}">${orderProPaymentMode}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_rate">Rate / User
										</label> <input autofocus="autofocus" type="number"
											class="form-control" name="rate" id="field_rate"
											placeholder="Rate / User" />
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
			
			<form id="deleteForm" name="deleteForm" action="${urlSnrichProductRate}">
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
									<p>Are you sure you want to delete this Product Rate?</p>
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

	<spring:url value="/resources/app/snrich-product-rate.js" var="snrichProductRateJs"></spring:url>
	<script type="text/javascript" src="${snrichProductRateJs}"></script>

</body>
</html>