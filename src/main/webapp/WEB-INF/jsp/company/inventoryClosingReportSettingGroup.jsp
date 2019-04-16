<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Inventory Closing Report Setting Group</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Inventory Closing Report Setting Group</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="InventoryClosingReportSettingGroup.showModalPopup($('#myModal'));">Create new
						Inventory Closing Report Setting Group</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="InventoryClosingReportSettingGroup.showModalPopup($('#enableInventoryClosingReportSettingGroupModal'));">Deactivated
						Inventory Closing Report Setting Group</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<!-- <th>Description</th> -->
						<th>Flow</th>
						<th>Sort Order</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${inventoryClosingReportSettingGroups}" var="inventoryClosingReportSettingGroup"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${inventoryClosingReportSettingGroup.name}</td>
							<td>${inventoryClosingReportSettingGroup.alias == null ? "" : inventoryClosingReportSettingGroup.alias}</td>
							<%-- <td>${inventoryClosingReportSettingGroup.description == null ? "" : inventoryClosingReportSettingGroup.description}</td> --%>
							<td>${inventoryClosingReportSettingGroup.flow}</td>
							<td>${inventoryClosingReportSettingGroup.sortOrder}</td>
							<td><span class="label ${inventoryClosingReportSettingGroup.activated? 'label-success':'label-danger' }"
							onclick="InventoryClosingReportSettingGroup.setActive('${inventoryClosingReportSettingGroup.name}','${inventoryClosingReportSettingGroup.pid}','${ !inventoryClosingReportSettingGroup.activated}')"
							style="cursor:pointer;">${inventoryClosingReportSettingGroup.activated? "Activated" : "Deactivated"}</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="InventoryClosingReportSettingGroup.showModalPopup($('#viewModal'),'${inventoryClosingReportSettingGroup.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="InventoryClosingReportSettingGroup.showModalPopup($('#myModal'),'${inventoryClosingReportSettingGroup.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="InventoryClosingReportSettingGroup.showModalPopup($('#deleteModal'),'${inventoryClosingReportSettingGroup.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/inventory-closing-report-setting-group" var="urlInventoryClosingReportSettingGroup"></spring:url>

			<form id="inventoryClosingReportSettingGroupForm" role="form" method="post" action="${urlInventoryClosingReportSettingGroup}">
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
									Inventory Closing Report Setting Group</h4>
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
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Alias</label> <input
											type="text" class="form-control" name="alias"
											id="field_alias" maxlength="55" placeholder="Alias" />
									</div>

									<!-- <div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<input type="text" class="form-control" name="description"
											id="field_description" placeholder="Description" />
									</div> -->
									<div class="form-group">
										<label class="control-label" for="field_flow">Flow</label> <select id="field_flow"
											name="flow" class="form-control">
											<option value="-1">Select Flow</option>
											<c:forEach var="flow" items="${flows}">
											<option value="${flow}">${flow}</option>
											</c:forEach>
										</select>
									</div>
									
											<div class="form-group">
										<label class="control-label" for="field_sortorder">Sort Order</label> <input
											type="number" class="form-control" name="sortOrder"
											id="field_sortorder" placeholder="sortOrder" value="0"/>
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
								<h4 class="modal-title" id="viewModalLabel">Inventory Closing Report Setting Group</h4>
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
											<span>Alias</span>
										</dt>
										<dd>
											<span id="lbl_alias"></span>
										</dd>
										<hr />
									<!-- 	<dt>
											<span>Description</span>
										</dt>
										<dd>
											<span id="lbl_description"></span>
										</dd>
										<hr /> -->
										<dt>
											<span>Flow</span>
										</dt>
										<dd>
											<span id="lbl_flow"></span>
										</dd>
										<hr />
										<dt>
											<span>Sort Order</span>
										</dt>
										<dd>
											<span id="lbl_sortorder"></span>
										</dd>
										<hr />
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

			<form id="deleteForm" name="deleteForm" action="${urlInventoryClosingReportSettingGroup}">
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
									<p>Are you sure you want to delete this Inventory Closing Report Setting Group?</p>
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

		<div class="modal fade container" id="enableInventoryClosingReportSettingGroupModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Inventory Closing Report SettingG roup</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>InventoryClosingReportSettingGroup</th>
											</tr>
										</thead>
										<tbody id="tblEnableInventoryClosingReportSettingGroup">
											<c:forEach items="${deactivatedInventoryClosingReportSettingGroups}"
												var="inventoryClosingReportSettingGroup">
												<tr>
													<td><input name='inventoryClosingReportSettingGroup' type='checkbox'
														value="${inventoryClosingReportSettingGroup.pid}" /></td>
													<td>${inventoryClosingReportSettingGroup.name}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnActivateInventoryClosingReportSettingGroup" value="Activate" />
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

	<spring:url value="/resources/app/inventory-closing-report-setting-group.js" var="inventoryClosingReportSettingGroupJs"></spring:url>
	<script type="text/javascript" src="${inventoryClosingReportSettingGroupJs}"></script>
</body>
</html>