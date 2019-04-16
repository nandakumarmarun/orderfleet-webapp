<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Best Performance Configuration</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Best Performance Configuration</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success" id="btnDocId">Create
						new Best Performance Configuration</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Best Performance Type</th>
						<th>Documents</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="entry" items="${bestPerformanceConfigurations}">
						<tr>
							<td><c:out value="${entry.key}" /></td>
							<td><c:forEach items="${entry.value}"
									var="bestPerformanceConfiguration" varStatus="loopStatus">
									<a class="btn btn-info">${bestPerformanceConfiguration.documentName}</a>
									<br />
									<input type="hidden" id="${entry.key}"
										value="${bestPerformanceConfiguration.documentType}">
								</c:forEach></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="BestPerformanceConfiguration.showModalPopup($('#documentsModal'),'${entry.key}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="BestPerformanceConfiguration.showModalPopup($('#deleteModal'),'${entry.key}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/bestPerformanceConfigurations"
				var="urlBestPerformanceConfiguration"></spring:url>



			<form id="deleteForm" name="deleteForm" action="${urlBestPerformanceConfiguration}">
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
									<p>Are you sure you want to delete this Best Performance Configuration?</p>
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



			<div class="modal fade container" id="documentsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Assign Documents</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<label class="control-label" for="field_alias">Best
									Performance Type</label> <select id="field_bestPerformanceType"
									name="bestPerformanceType" class="form-control">
									<option value="-1">-- Select --</option>
									<c:forEach items="${bestPerformanceTypes}"
										var="bestPerformanceType">
										<option value="${bestPerformanceType}">${bestPerformanceType}</option>
									</c:forEach>
								</select>
							</div>

							<div class="form-group">
								<label class="control-label" for="field_alias">Document
									Type</label> <select id="field_documentType" name="documentType"
									class="form-control">
									<option value="-1">-- Select --</option>
									<c:forEach items="${documentTypes}" var="documentType">
										<option value="${documentType}">${documentType}</option>
									</c:forEach>
								</select>
							</div>

							<div class="form-group">
								<div id="documentsCheckboxes">
									<table class='table table-striped table-bordered'
										id="tbl_documentsCheckboxes">
										<c:forEach items="${documents}" var="document">
											<tr>
												<td><input name='document' type='checkbox'
													value="${document.pid}" /></td>
												<td>${document.name}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveDocument"
								value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/best-performance-configuration.js"
		var="bestPerformanceConfigurationJs"></spring:url>
	<script type="text/javascript" src="${bestPerformanceConfigurationJs}"></script>
</body>
</html>