<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<spring:url value="/resources/assets/css/site.css" var="siteCss"></spring:url>
<link href="${siteCss}" rel="stylesheet">

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
						onclick="$('#myModal').modal('show', {backdrop: 'static'});">Create
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
									onclick="DynamicDocumentReportConfiguration.showModalPopup($('#myModal'),'${docHead.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="DynamicDocumentReportConfiguration.showModalPopup($('#deleteModal'),'${docHead.pid}',2);">Delete</button>
								
								<%-- <button type="button" class="btn btn-blue"
									onclick="DynamicDocumentReportConfiguration.showModalPopup($('#viewModal'),'${docHead.pid}',0);">View</button>
								<button type="button" class="btn btn-info"
									onclick="DynamicDocumentReportConfiguration.showListModalPopup($('#documentSettingsColumnsModal'),'${docHead.pid}','${docHead.documentPid}',0);">Assign
									Document Settings Columns</button>
								<button type="button" class="btn btn-info"
									onclick="DynamicDocumentReportConfiguration.showListModalPopup($('#documentSettingsRowColourModal'),'${docHead.pid}','${docHead.documentPid}',1);">Assign
									Document Settings Row Colour</button> --%>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/dynamic-document-report-configuration"
				var="urlDynamicDocumentReportConfiguration"></spring:url>

			<form id="dynamicDocumentReportConfigurationsForm" role="form"
				method="post" action="${urlDynamicDocumentReportConfiguration}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal" style="width: 100%">
					<!-- model Dialog -->
					<div class="modal-dialog" style="width: 100%">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Dynamic Document Report
									Configurations</h4>
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
									<div class="col-md-5">
										<select id="dbDocuments" class="form-control">
											<option value="-1">-- Select a document --</option>
											<c:forEach items="${documents}" var="document">
												<option value="${document.pid}">${document.name}</option>
											</c:forEach>
										</select>
									</div>
								</div>

								<div class="modal-body">
									<section class="search-results-env">
										<div class="row">
											<div class="col-md-12">

												<div class="row">
													<div class="col-md-6">
														<div class="form-group col-md-3">
															<input tabindex="5" type="checkbox" class="icheck-11"
																id="chk_filledForm"> <label for="chk_filledForm">Filled
																Form</label>
														</div>
														<div class="form-group col-md-3">
															<input tabindex="5" type="checkbox" class="icheck-11"
																id="chk_executaskexe"> <label
																for="chk_executaskexe">Transaction Head</label>
														</div>
														<div class="form-group col-md-3">
															<input tabindex="5" type="checkbox" class="icheck-11"
																id="chk_dynamicDocHead"> <label
																for="chk_dynamicDocHead">Dynamic Document Head</label>
														</div>
													</div>

												</div>
												<ul class="nav nav-tabs ">
													<li class="active"><a href="#first">First</a></li>
													<li class=""><a href="#second">Second</a></li>
												</ul>

												<div class="search-results-panes">
													<div class="search-results-pane" id="first"
														style="display: block;">
														<div class="row style-select">
															<div class="col-md-6">
																<div class="row">
																	<select multiple class="form-control" id="lstBox1"
																		style="height: 55%">
																		<c:forEach items="${dynamicDocumentHeaderColumns}"
																			var="document">
																			<option value="${document.columnName}">${document.columnName}</option>
																		</c:forEach>
																	</select>
																</div>
															</div>
															<div class="col-md-6">
																<div class="row">
																	<div class="subject-info-arrows text-center">
																		<br /> <br /> <br /> <input type='button'
																			id='btnAllRight' value='>>' class="btn btn-default" /><br />
																		<input type='button' id='btnRight' value='>'
																			class="btn btn-default" /><br /> <input
																			type='button' id='btnLeft' class="btn btn-default "
																			value='<'/><br /> <input type='button'
																			id='btnAllLeft' class="btn btn-default" value='<<'/>
																	</div>

																	<div class="col-md-9 col-sm-9 col-xs-9"
																		style="width: 90%">
																		<div class="selected-left">
																			<select multiple class="form-control country"
																				style="height: 55%" id="lstBox2">
																			</select>
																		</div>
																		<div class="selected-right">
																			<br /> <br /> <br /> <br /> <br /> <br /> <br />
																			<button type="button" class="btn btn-default btn-sm"
																				id="btnAvengerUp">
																				<span class="glyphicon glyphicon-chevron-up"></span>
																			</button>
																			<button type="button" class="btn btn-default btn-sm"
																				id="btnAvengerDown">
																				<span class="glyphicon glyphicon-chevron-down"></span>
																			</button>

																		</div>
																	</div>
																</div>
															</div>
														</div>


													</div>
													<div class="search-results-pane" id="second"
														style="display: none;">
														<div class="row">
															<p>bbbbbbbbbbbbbbbbbbbb</p>
														</div>
													</div>
												</div>
											</div>
										</div>

									</section>
								</div>




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
			<form id="deleteForm" name="deleteForm"
				action="${urlDynamicDocumentReportConfiguration}">
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
										Configuration Header?</p>
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

	<spring:url value="/resources/assets/js/jquery.selectlistactions.js"
		var="selectlistactions"></spring:url>
	<script type="text/javascript" src="${selectlistactions}"></script>

	<spring:url
		value="/resources/app/dynamic-document-report-configuration.js"
		var="dynamicDocumentReportJs"></spring:url>
	<script type="text/javascript" src="${dynamicDocumentReportJs}"></script>
</body>
</html>