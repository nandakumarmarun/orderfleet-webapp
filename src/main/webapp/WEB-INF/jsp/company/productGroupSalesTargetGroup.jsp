<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Product Group Sales Target Groups</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Product Group Sales Target Groups</h2>
			<div class="row col-xs-12">
				<div class="pull-left">
					<div class="col-xs-6">
						<select id="productGroup" name="productGroupPid"
							class="form-control"><option value="no">All ProductGroup</option>
							<c:forEach items="${productGroups}" var="productGroup">
								<option value="${productGroup.pid}">${productGroup.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-xs-3">
						<button type="button" class="btn btn-success"
							onclick="ProductGroupSalesTargetGroup.load();">Load</button>
					</div>

				</div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="ProductGroupSalesTargetGroup.showModalPopup($('#myModal'));">Create
						new Product Group Sales Target Groups</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Product Groups</th>
						<th>Sales Target Groups</th>
					</tr>
				</thead>
				<tbody id="tbodyProductGroupSalesTargetGroup">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/product-group-sales-target-groups"
				var="urlProductGroupSalesTargetGroup"></spring:url>


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
								Product Group Sales Target Groups</h4>
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
								<label class="control-label" for="field_productGroup">Product
									group</label> <select id="field_productGroup" name="productGroupPid"
									class="form-control"><option value="no">Select ProductGroup</option>
									<c:forEach items="${productGroups}" var="productGroup">
										<option value="${productGroup.pid}">${productGroup.name}</option>
									</c:forEach>
								</select>
								<div class="form-group">
									<label class="control-label" for="field_salesTargetGroup">Sales
										Target Group</label>
									<div id="salesTargetGroupsCheckboxes">
										<table class='table table-striped table-bordered'>
											<c:forEach items="${salesTargetGroups}"
												var="salesTargetGroup">
												<tr>
													<td><input name='salesTargetGroup' type='checkbox'
														value="${salesTargetGroup.pid}" /></td>
													<td>${salesTargetGroup.name}</td>
												</tr>
											</c:forEach>
										</table>
									</div>
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



		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/product-group-sales-target-group.js"
		var="productGroupSalesTargetGroupJs"></spring:url>
	<script type="text/javascript" src="${productGroupSalesTargetGroupJs}"></script>
</body>
</html>