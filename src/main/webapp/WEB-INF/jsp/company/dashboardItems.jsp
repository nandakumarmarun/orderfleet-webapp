<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | DashboardItem</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Dashboard Items</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="DashboardItem.showModalPopup($('#myModal'));">Create
						new Dashboard Item</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<br>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr>
							<th>Name</th>
							<th>Item Type</th>
							<th>Task Plan Type</th>
							<th>Dashboard Item Config Type</th>
							<th>sort order</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody id="tBodyDashboardItem">
						<c:forEach items="${dashboardItems}" var="dashboardItem"
							varStatus="loopStatus">
							<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
								<td>${dashboardItem.name}</td>
								<td>${dashboardItem.dashboardItemType}</td>
								<td>${dashboardItem.taskPlanType}</td>
								<td>${dashboardItem.dashboardItemConfigType}</td>
								<td>${dashboardItem.sortOrder}</td>
								<td>
									<button type="button" class="btn btn-blue"
										onclick="DashboardItem.showModalPopup($('#viewModal'),'${dashboardItem.pid}',0);">View</button>
									<button type="button" class="btn btn-blue"
										onclick="DashboardItem.showModalPopup($('#myModal'),'${dashboardItem.pid}',1);">Edit</button>
									<button type="button" class="btn btn-danger"
										onclick="DashboardItem.showModalPopup($('#deleteModal'),'${dashboardItem.pid}',2);">Delete</button>
									<button type="button" class="btn btn-green"
										onclick="DashboardItem.showModalPopup($('#assignUsersModal'),'${dashboardItem.pid}',3);">Assign
										User</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/dashboardItems" var="urlDashboardItem"></spring:url>

			<form id="dashboardItemForm" role="form" method="post"
				action="${urlDashboardItem}">
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
									Dashboard Item</h4>
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
								<div class="form-group">
									<label class="control-label" for="field_name">Name</label> <input
										autofocus="autofocus" type="text" class="form-control"
										name="name" id="field_name" maxlength="255" placeholder="Name" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_sortOrder">Sort
										Order</label> <input type="number" min="0" class="form-control"
										name="sortOrder" id="field_sortOrder" value="0" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_taskPlanType">Task
										Plan Type</label> <select id="field_taskPlanType" name="taskPlanType"
										class="form-control"><option value="PLANNED">PLANNED</option>
										<option value="UN_PLANNED">UN_PLANNED</option>
										<option value="BOTH">BOTH</option>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_dashboardItemType">Dashboard
										Item Type</label> <select id="field_dashboardItemType"
										name="dashboardItemType" class="form-control"
										onchange="DashboardItem.onChangeItemType();"><option
											value="ACTIVITY">ACTIVITY</option>
										<option value="DOCUMENT">DOCUMENT</option>
										<option value="PRODUCT">PRODUCT</option>
										<option value="TARGET">TARGET BLOCK</option>
									</select>
								</div>
								<div id="divDocumentType" class="form-group"
									style="display: none;">
									<label class="control-label" for="field_documentType">Document
										Type</label> <select id="field_documentType" class="form-control"
										name="documentType"
										onchange="DashboardItem.onChangeDocumentType();">
										<option value="-1">Select Document Type</option>
										<option value="INVENTORY_VOUCHER">Inventory Voucher</option>
										<option value="ACCOUNTING_VOUCHER">Accounting Voucher</option>
										<option value="DYNAMIC_DOCUMENT">Dynamic Document</option>
									</select>
								</div>
								<div id="divActivities" class="form-group">
									<label class="control-label" for="field_activities">Activities</label><select
										multiple="multiple" id="field_activities" name="activities"
										class="form-control" style="height: 130px;">
										<c:forEach items="${activities}" var="activity">
											<option value="${activity.pid}">${activity.name}</option>
										</c:forEach>
									</select>
								</div>
								<div id="divPGroups" class="form-group" style="display: none;">
									<label class="control-label" for="field_pGroups">Product
										Groups</label><select multiple="multiple" id="field_pGroups"
										name="pGroups" class="form-control" style="height: 130px;">
										<c:forEach items="${productGroups}" var="pGroup">
											<option value="${pGroup.pid}">${pGroup.name}</option>
										</c:forEach>
									</select>
								</div>
								<div id="divDocuments" class="form-group" style="display: none;">
									<label class="control-label" for="field_documents">Documents</label><select
										multiple="multiple" id="field_documents" name="documents"
										class="form-control" style="height: 130px;">
										<c:forEach items="${documents}" var="document">
											<option value="${document.pid}">${document.name}</option>
										</c:forEach>
									</select>
								</div>
								<div id="divTargetBlock" class="form-group"
									style="display: none;">
									<label class="control-label" for="field_targetGroup">Target
										Groups</label> <select id="field_targetGroup" class="form-control"
										name="targetGroup"
										onchange="DashboardItem.onChangeTargetGroup();">
										<option value="-1">Select Target Group</option>
										<c:forEach items="${targetGroups}" var="targetGroup">
											<option value="${targetGroup.pid}">${targetGroup.name}</option>
										</c:forEach>
									</select> <label class="control-label" for="field_pGroups">Target
										Blocks</label><select id="field_targets" name="targetBlocks"
										class="form-control">
										<option value="-1">Select Target Block</option>
									</select>
								</div>


								<div id="divDashboardItemConfig" class="form-group">
									<label class="control-label" for="field_dashboardItemConfig">Dashboard
										Item Config Type</label> <select id="field_dashboardItemConfig"
										name="dashboardItemConfig" class="form-control">
										<c:forEach items="${dashboardItemConfigTypes}"
											var="dashboardItemConfig">
											<option value="${dashboardItemConfig}">${dashboardItemConfig}</option>
										</c:forEach>
									</select>
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
								<h4 class="modal-title" id="viewModalLabel">Activity</h4>
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

								<dl class="dl-horizontal">
									<dt>
										<span>Name</span>
									</dt>
									<dd>
										<span id="lbl_name"></span>
									</dd>
									<hr />
									<dt>
										<span>Item Type</span>
									</dt>
									<dd>
										<span id="lbl_itemType"></span>
									</dd>
									<hr />
									<dt>
										<span>Task Plan Type</span>
									</dt>
									<dd>
										<span id="lbl_taskPlanType"></span>
									</dd>
									<hr />
									<dt>
										<span>dashboard Item Config Type</span>
									</dt>
									<dd>
										<span id="lbl_dashboardItemConfigType"></span>
									</dd>
									<hr />
									<dt>
										<span>Sort Order</span>
									</dt>
									<dd>
										<span id="lbl_sortOrder"></span>
									</dd>
								</dl>
								<table class="table  table-striped table-bordered"
									id="tblActivities">
								</table>
								<table class="table  table-striped table-bordered"
									id="tblDocuments">
								</table>
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
										<thead>
											<tr>
												<th><input type='checkbox' class="allcheckbox" /></th>
												<th>Users</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${users}" var="user">
												<tr>
													<td><input name='user' type='checkbox'
														value="${user.pid}" /></td>
													<td>${user.firstName}</td>
												</tr>
											</c:forEach>
										</tbody>
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

			<form id="deleteForm" name="deleteForm" action="${urlDashboardItem}">
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
									<p>Are you sure you want to delete this Dashboard Item ?</p>
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

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/dashboard-item.js"
		var="dashboardItemJs"></spring:url>
	<script type="text/javascript" src="${dashboardItemJs}"></script>
</body>
</html>