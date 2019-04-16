<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Stage File Upload</title>
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
			<h2>Stage File Upload</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="StageFileUpload.showModalPopup($('#myModal'));"
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
						<th>Stage</th>
						<th>File Upload Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:if test = "${empty stageFileUploads}">
						<tr><td colspan = 3 style = "text-align: center;">No Data Available</td></tr>
					</c:if>
					<c:forEach items="${stageFileUploads}" var="stageFileUpload">
						<tr>
							<td>${stageFileUpload.stage.name == null ? "" : stageFileUpload.stage.name}</td>
							<td>${stageFileUpload.fileUploadName == null ? "" : stageFileUpload.fileUploadName}</td>
							<td>
								<button type="button" class="btn btn-info" title="Edit"
									onclick="StageFileUpload.showModalPopup($('#myModal'),'${stageFileUpload.pid}',1);">
										<i class="entypo-pencil"></i> Edit
								</button>
								<button type="button" class="btn btn-danger"
									onclick="StageFileUpload.showModalPopup($('#deleteModal'),'${stageFileUpload.pid}',2);">
									<i class="entypo-trash"></i> Delete
								</button>
							</td>
						</tr>
					</c:forEach>
						
				</tbody>
			</table>
			
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<spring:url value="/web/stage-file-upload" var="urlStageFileUpload"></spring:url>

			<form id="stageFileUploadForm" role="form" method="post"
				action="${urlStageFileUpload}">
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
								<h4 class="modal-title" id="myModalLabel">Create
									Stage File Upload</h4>
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
								<div class="modal-body" style="overflow: auto; height: 60%;">
									
									<div class="form-group">
										<label class="control-label" for="field_stage">Stage</label>
										<select id="field_stage" name="stagePid" class="form-control">
											<option value="-1">Select Stage</option>
											<c:forEach items="${stages}" var="stage">
												<option value="${stage.pid}">${stage.name}</option>
											</c:forEach>
										</select>
									</div>
									
									<div class="form-group">
										<label class="control-label" for="field_file_upload_name">
										File Upload Name (Separate with ';')</label>
										<input type="text" class="form-control" name="fileUploadName"
											id = "field_file_upload_name" placeholder="File Upload Name" />
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
			
			<form id="deleteForm" name="deleteForm" action="${urlDocument}">
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
								<!-- error message -->
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<p>Are you sure you want to delete this Stage File Upload?</p>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button class="btn btn-danger">Delete</button>
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

	<spring:url value="/resources/app/stage-file-upload.js" var="stageFileUploadJs"></spring:url>
	<script type="text/javascript" src="${stageFileUploadJs}"></script>
	
</body>
</html>