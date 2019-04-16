<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Product Group Info Sections</title>

<spring:url value="/resources/assets/plugin/summernote/summernote.css"
	var="summernoteCss"></spring:url>
<link href="${summernoteCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Product Group Info Sections</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="ProductGroupInfoSection.showModalPopup($('#myModal'));">Create
						New</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Product Group</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pageProductGroupInfoSection.content}"
						var="productInfoSection" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${productInfoSection.name}</td>
							<td>${productInfoSection.productGroupName}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="ProductGroupInfoSection.showModalPopup($('#viewModal'),'${productInfoSection.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="ProductGroupInfoSection.showModalPopup($('#myModal'),'${productInfoSection.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="ProductGroupInfoSection.showModalPopup($('#deleteModal'),'${productInfoSection.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="row-fluid">
				<util:pagination thispage="${pageProductGroupInfoSection}"></util:pagination>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/product-group-info-sections"
				var="urlProductGroupInfoSection"></spring:url>

			<form id="productGroupInfoSectionForm" role="form" method="post"
				action="${urlProductGroupInfoSection}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog" style="width: 100%;">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Product Group Info Section</h4>
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
								<div class="modal-body" style="height: 450px; overflow: auto;">
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_productGroup">Product
											Group</label> <select id="field_productGroup" name="productPid"
											class="form-control"><option value="-1">Select
												Product Group</option>
											<c:forEach items="${productGroups}" var="productGroup">
												<option value="${productGroup.pid}">${productGroup.name}</option>
											</c:forEach>
										</select>
									</div>
									<div id="field_richText"></div>
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
					<div class="modal-dialog" style="width: 100%;">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Product Group
									Info Section</h4>
							</div>
							<div class="modal-body">
								<!-- error message -->
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<div class="modal-body" style="height: 450px; overflow: auto;">
									<table class="table  table-striped table-bordered">
										<tr>
											<td>Name</td>
											<td id="lblName"></td>
										</tr>
										<tr>
											<td>Product Group</td>
											<td id="lblProductGroup"></td>
										</tr>
									</table>
									<div id="divRichText"></div>
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

			<form id="deleteForm" name="deleteForm"
				action="${urlProductGroupInfoSection}">
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
									<p>Are you sure you want to delete this Product Group Info
										Section?</p>
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

	<spring:url value="/resources/app/product-group-info-section.js"
		var="productInfoSectionJs"></spring:url>
	<script type="text/javascript" src="${productInfoSectionJs}"></script>

	<spring:url value="/resources/assets/plugin/summernote/summernote.js"
		var="summernoteJs"></spring:url>
	<script type="text/javascript" src="${summernoteJs}"></script>

	<script>
		$(document).ready(function() {
			$('#field_richText').summernote({
				height : 250
			});
		});
	</script>
</body>
</html>