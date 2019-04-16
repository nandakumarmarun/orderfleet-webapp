<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Knowledgebase Files</title>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<style type="text/css">

/*for upload progress bar */
#progressbox {
	position: relative;
	width: 400px;
	border: 1px solid #ddd;
	padding: 1px;
	border-radius: 3px;
}

#progressbar {
	background-color: lightblue;
	width: 0%;
	height: 20px;
}

#percent {
	position: absolute;
	display: inline-block;
	top: 3px;
	left: 48%;
}
/*****End*******/

/*for search tags */
.ui-autocomplete {
	max-height: 130px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
	z-index: 10001;
}
/* IE 6 doesn't support max-height
   * we use height instead, but this forces the menu to always be this tall
   */
* html .ui-autocomplete {
	height: 130px;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Knowledgebase Files</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="KnowledgebaseFile.showModalPopup($('#myModal'));">Create
						new Knowledgebase File</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>File Name</th>
						<th>Knowledgebase</th>
						<th>Serach Tags</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${knowledgebaseFiles}" var="knowledgebaseFile"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${knowledgebaseFile.fileName}</td>
							<td>${knowledgebaseFile.knowledgebaseName}</td>
							<td>${knowledgebaseFile.searchTags}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="KnowledgebaseFile.showModalPopup($('#viewModal'),'${knowledgebaseFile.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="KnowledgebaseFile.showModalPopup($('#myModal'),'${knowledgebaseFile.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="KnowledgebaseFile.showModalPopup($('#deleteModal'),'${knowledgebaseFile.pid}',2);">Delete</button>
								<a class="btn btn-fb" style="width: 130px;"
								href="${pageContext.request.contextPath}/web/knowledgebase-files/view-file/${knowledgebaseFile.filePid}"
								target="_blank">View File</a>
								<button type="button" class="btn btn-white"
									onclick="KnowledgebaseFile.showModalPopup($('#assignUsersModal'),'${knowledgebaseFile.pid}',3);">Assign
									Users</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/knowledgebase-files"
				var="urlKnowledgebaseFile"></spring:url>

			<form id="knowledgebaseFileForm" role="form" method="post"
				action="${urlKnowledgebaseFile}" enctype="multipart/form-data">
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
								<h4 class="modal-title" id="myModalLabel">Upload File</h4>
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
									<div>
										<input type="hidden" id="hdnPid" name="pid">
										<div class="form-group">
											<label class="control-label" for="field_fileName">File
												Name</label> <input autofocus="autofocus" type="text"
												class="form-control" name="fileName" id="field_fileName"
												maxlength="200" placeholder="File Name" />
										</div>
										<div class="form-group">
											<label class="control-label" for="field_knowledgebase">Knowledgebase</label>
											<select id="field_knowledgebase" name="knowledgebasePid"
												class="form-control">
												<option value="-1">Select Knowledgebase</option>
												<c:forEach items="${knowledgebases}" var="knowledgebase">
													<option value="${knowledgebase.pid}">${knowledgebase.name}</option>
												</c:forEach>
											</select>
										</div>
										<div class="form-group">
											<label class="control-label" for="field_searchTags">Search
												Tags</label> <input autofocus="autofocus" class="form-control"
												name="searchTags" id="field_searchTags" maxlength="2500"
												placeholder="Serach Tags" />
										</div>
										<div class="form-group">
											<label>Choose File</label> <input type="file" name="file"
												id="txtFile" />
										</div>
										<br />
										<div id="progressbox" style="width: 100%;">
											<div id="progressbar"></div>
											<div id="percent">0%</div>
										</div>
										<div id="message" style="font-weight: bold;"></div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="myFormSubmit" class="btn btn-primary">Upload</button>
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
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Knowledgebase
									File</h4>
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

									<dl class="dl-horizontal">
										<dt>
											<span>File Name</span>
										</dt>
										<dd>
											<span id="lbl_fileName"></span>
										</dd>
										<hr />
										<dt>
											<span>Knowledgebase</span>
										</dt>
										<dd>
											<span id="lbl_knowledgebase"></span>
										</dd>
										<hr />
										<dt>
											<span>Saerch Tags</span>
										</dt>
										<dd>
											<span id="lbl_searchTags"></span>
										</dd>
										<hr />
									</dl>
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
				action="${urlKnowledgebaseFile}">
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
									<p>Are you sure you want to delete this Knowledgebase File
										?</p>
								</div>
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


			<div class="modal fade container" id="assignUsersModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Users</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divUsers">
									<table class='table table-striped table-bordered'>
										<tr>
											<th><input type="checkbox" class="allcheckbox">All</th>
											<th>Users First Name</th>
											<th>Users Last Name</th>
										</tr>
										<c:forEach items="${users}" var="user">
											<tr>
												<td><input name='user' type='checkbox'
													value="${user.pid}" /></td>
												<td>${user.firstName}</td>
												<td>${user.lastName}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="btnSaveUsers"
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

	<spring:url value="/resources/assets/js/jquery.form.js" var="ajaxForm" />
	<script src="${ajaxForm}"></script>

	<spring:url value="/resources/app/knowledgebase-file.js"
		var="knowledgebaseJs"></spring:url>
	<script type="text/javascript" src="${knowledgebaseJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>


</body>
</html>