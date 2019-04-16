<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Inventory Closing Report Settings</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Inventory Closing Report Settings</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="InventoryClosingReportSettings.showModalPopup($('#myModal'));">Create
						new Inventory Closing Report Settings</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
					
						<th>Document</th>
						<th>Group Name</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
				 	<c:forEach items="${inventoryClosingReports}" var="inventoryClosingReport"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${inventoryClosingReport.documentName}</td>
							<td>${inventoryClosingReport.inventoryClosingReportSettingGroupName}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="InventoryClosingReportSettings.showModalPopup($('#viewModal'),'${inventoryClosingReport.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="InventoryClosingReportSettings.showModalPopup($('#myModal'),'${inventoryClosingReport.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="InventoryClosingReportSettings.showModalPopup($('#deleteModal'),'${inventoryClosingReport.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach> 
				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/inventory-closing-report-settings" var="urlInventoryClosingReportSettings"></spring:url>

			<form id="inventoryClosingReportForm" role="form" method="post"
				action="${urlInventoryClosingReportSettings}">
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
									Inventory Closing Report Settings</h4>
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
										<label class="control-label" for="field_document">Document</label> <select id="field_document"
											name="documentPid" class="form-control">
											<option value="-1">Select Document</option>
											<c:forEach var="document" items="${documents}">
											<option value="${document.pid}">${document.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_inClgRptStgGrp">Document</label> <select id="field_inClgRptStgGrp"
											name="inClgRptStgGrpPid" class="form-control">
											<option value="-1">Select Inventory Closing Report Setting Group</option>
											<c:forEach var="inventoryClosingReportGrp" items="${inventoryClosingReportGrps}">
											<option value="${inventoryClosingReportGrp.pid}">${inventoryClosingReportGrp.name}</option>
											</c:forEach>
										</select>
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
								<h4 class="modal-title" id="viewModalLabel">Inventory Closing Report Settings</h4>
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
											<span>Document</span>
										</dt>
										<dd>
											<span id="lbl_document"></span>
										</dd>
										<hr />
										<dt>
											<span>Inventory Setting Group</span>
										</dt>
										<dd>
											<span id="lbl_settingGroup"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlInventoryClosingReportSettings}">
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
								<p>Are you sure you want to delete this Inventory Closing Report ?</p>
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

	<spring:url value="/resources/app/inventory-closing-report-settings.js" var="inventoryClosingReportSettingsJs"></spring:url>
	<script type="text/javascript" src="${inventoryClosingReportSettingsJs}"></script>
</body>
</html>