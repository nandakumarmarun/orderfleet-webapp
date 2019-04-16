<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Dynamic Document Report</title>
<style type="text/css">
.error {
	color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
	.width83 {
		width: 83%
	}
}
</style>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Dynamic Document Report Settings</h2>

			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="DynamicDocumentReportSetting.showModalPopup($('#myModal'));">Create
						new Dynamic Document Settings Header</button>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Title</th>
						<th>Document</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${documentSettingsHeaders}" var="docHead"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${docHead.name}</td>
							<td>${docHead.title}</td>
							<td>${docHead.documentName}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="DynamicDocumentReportSetting.showModalPopup($('#viewModal'),'${docHead.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="DynamicDocumentReportSetting.showModalPopup($('#myModal'),'${docHead.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="DynamicDocumentReportSetting.showModalPopup($('#deleteModal'),'${docHead.pid}',2);">Delete</button>
								<button type="button" class="btn btn-info"
									onclick="DynamicDocumentReportSetting.showListModalPopup($('#documentSettingsColumnsModal'),'${docHead.pid}','${docHead.documentPid}',0);">Assign
									Document Settings Columns</button>
								<button type="button" class="btn btn-info"
									onclick="DynamicDocumentReportSetting.showListModalPopup($('#documentSettingsRowColourModal'),'${docHead.pid}','${docHead.documentPid}',1);">Assign
									Document Settings Row Colour</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/dynamic-document-report-settings"
				var="urlDynamicDocumentReportSettings"></spring:url>

			<form id="dynamicDocumentReportSettingsForm" role="form"
				method="post" action="${urlDynamicDocumentReportSettings}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog" style="width: 100%">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Enable Account Group</h4>
							</div>
							<div class="modal-body" style="overflow: auto; height: 600px">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>
								<div class=row>
									<div class="form-group col-md-5">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group col-md-5">
										<label class="control-label" for="field_title">Title</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="title" id="field_title" maxlength="255"
											placeholder="title" />
									</div>
									<div class="clearfix"></div>
									<div class="col-md-5">
										<select id="dbDocuments" class="form-control">
											<option value="-1">-- Select a document --</option>
											<c:forEach items="${documents}" var="document">
												<option value="${document.pid}">${document.name}</option>
											</c:forEach>
										</select> <br />
									</div>
									<div class="col-md-3">
										<div id="div_selectBox"></div>
										<br />
									</div>
								</div>
								<div class="clearfix"></div>
								<hr />
								<table class="table  table-striped table-bordered">
									<thead>
										<tr>
											<th></th>
											<th>Columns</th>
											<th>Type</th>
											<th>Order No</th>
										</tr>
									</thead>
									<tbody id="tb_FormElementValues">
									</tbody>
								</table>
								<hr />
								<table class="table  table-striped table-bordered">
									<tbody id="tb_ElementValues">
									</tbody>
								</table>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-success">Save</button>
								</div>

								<label class="error-msg" style="color: red;"></label>
							</div>

						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
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
								<h4 class="modal-title" id="viewModalLabel">Dynamic
									Document Settings Header</h4>
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
											<span>Name</span>
										</dt>
										<dd>
											<span id="lbl_name"></span>
										</dd>
										<hr />
										<dt>
											<span>Title</span>
										</dt>
										<dd>
											<span id="lbl_title"></span>
										</dd>
										<hr />
										<dt>
											<span>Document</span>
										</dt>
										<dd>
											<span id="lbl_document"></span>
										</dd>
										<hr />
										<dt>
											<span>Document Settings Columns</span>
										</dt>
										<dd>
											<table class='table table-striped table-bordered'>
												<thead>
													<tr>
														<th>Column</th>
														<th>Order Number</th>
													</tr>
												</thead>
												<tbody id="viewTbodyDocumentSettingsColumns">
												</tbody>
											</table>
										</dd>
										<hr />
										<hr />
										<dt>
											<span>Document Settings Row Colour</span>
										</dt>
										<dd>
											<table class='table table-striped table-bordered'>
												<tbody id="viewTbodydocumentSettingsRowColour">
												</tbody>
											</table>
										</dd>
									</dl>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>

			<form id="deleteForm" name="deleteForm"
				action="${urlDynamicDocumentReportSettings}">
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
									<p>Are you sure you want to delete this Dynamic Document
										Settings Header?</p>
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


			<div class="modal fade container" id="alertBox">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" id="alertMessage"
							style="font-size: large;"></div>
						<div class="modal-footer">
							<button class="btn btn-info" data-dismiss="modal">Close</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="documentSettingsColumnsModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Dynamic
								Document Settings Columns</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divDocumentSettingsColumns">
									<br />
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th></th>
												<th>Column</th>
												<th>Type</th>
												<th>Order Number</th>
											</tr>
										</thead>
										<tbody id="tbodyDocumentSettingsColumns">
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveDocumentSettingsColumns" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="documentSettingsRowColourModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign Dynamic
								Document Row Colour</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="divDocumentSettingsColumns">
									<br />
									<div id="field_filledForm" class="form-group col-md-5"></div>
									<table class='table table-striped table-bordered'>
										<tbody id="tbodyDocumentSettingsRowColour">
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveDocumentSettingsRowColour" value="Save" />
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

	<spring:url value="/resources/app/dynamic-document-report-settings.js"
		var="dynamicDocumentReportJs"></spring:url>
	<script type="text/javascript" src="${dynamicDocumentReportJs}"></script>
</body>
</html>