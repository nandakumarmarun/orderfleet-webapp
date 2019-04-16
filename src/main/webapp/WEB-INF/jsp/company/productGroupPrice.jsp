<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Product Group Price</title>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>

		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Set Product Group Price</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success" data-toggle="modal"
						data-target="#myModal">Product Group Price</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div class="table-responsive">
				<table class="table table-bordered datatable">
					<thead>
						<tr>
							<th>Product Group</th>
							<th>Price</th>
							<th></th>
						</tr>
					</thead>
					<tbody id="tblProductGroupPrice">
						<c:forEach items="${productGroupPrices}" var="productGroupPrice">
							<tr>
								<td>${productGroupPrice.productGroupDTO.name}</td>
								<td><input type="number" id="price" name="price"
									value="${productGroupPrice.price}" maxlength="55" /></td>
								<td><spring:url
										value="/web/productGroupPrice/${productGroupPrice.id}/update"
										var="updateUrl" />
									<button class="btn btn-primary"
										onclick="location.href='${updateUrl}?price='+$(this).closest('td').prev().find('input').val()">Update</button></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/productGroupPrice" var="urlProductGroupPrice"></spring:url>
			<form id="productGroupPriceForm" role="form" method="post"
				action="${urlProductGroupPrice}">
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
								<h4 class="modal-title" id="myModalLabel">Create new
									Product Group Price</h4>
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
										<label class="control-label" for="field_product_group">Product
											Group</label> <select id="dbProductGroup" name="productGroupPid"
											class="form-control">
											<option value="no">-- Select --</option>
											<c:forEach items="${productGroups}" var="productGroup">
												<option value="${productGroup.pid}">${productGroup.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_price">Price</label> <input
											type="number" class="form-control" name="price" step="0.01"
											id="field_price" maxlength="55" placeholder="Price" />
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
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
</body>

<script type="text/javascript">
	function updatePrice() {

	}
</script>

</html>