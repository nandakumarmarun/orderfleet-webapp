<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Route Plan Headers</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Route Plan Headers</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								User<select id="dbUser" name="userPid" class="form-control">
									<option value="no">All User</option>
									<c:forEach items="${users}" var="user">
										<option value="${user.pid}">${user.firstName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3">
								<br /> <select id="activated" class=" form-control  "
									title="filter">
									<option value="Active">Active</option>
									<option value="All">All</option>
									<option value="Deactive">Deactive</option>
								</select>
							</div>

							<div class="col-sm-2">
								<br />
								<button type="button" class="btn btn-info"
									onclick="RootPlanHeader.filter()">Apply</button>
							</div>
							<div class="col-sm-2">
								<br />
								<div class="pull-right">
									<button type="button" class="btn btn-success"
										onclick="RootPlanHeader.showModalPopup($('#myModal'));">Create
										new Route Plan Header</button>
								</div>
							</div>
						</div>
					</form>
				</div>

			</div>
			<div class="row col-xs-12"></div>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User</th>
						<th>Name</th>
						<th>From Date</th>
						<th>To Date</th>
						<th>Actions</th>
						<th>Status</th>
						<th>Copy Route Plan</th>
					</tr>
				</thead>
				<tbody id="tBodyRootPlanHeader">

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/root-plan-headers" var="urlRootPlanHeader"></spring:url>

			<form id="rootPlanHeaderForm" role="form" method="post"
				action="${urlRootPlanHeader}">
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
									RootPlanHeader</h4>
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
										<label class="control-label" for="field_user">User</label> <select
											id="field_user" name="userPid" class="form-control">
											<option value="-1">Select User</option>
											<c:forEach var="user" items="${users}">
												<option value="${user.pid}">${user.firstName}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<div class="input-group">
											<input type="text" class="form-control" id="txtFromDate"
												placeholder="Select From Date"
												style="background-color: #fff; z-index: 99999999999999;"
												readonly="readonly" />

											<div class="input-group-addon">
												<a href="#"><i class="entypo-calendar"></i></a>
											</div>
										</div>
									</div>

									<div class="form-group">
										<div class="input-group">
											<input type="text" class="form-control" id="txtToDate"
												placeholder="Select To Date"
												style="background-color: #fff; z-index: 99999999999999;"
												readonly="readonly" />
											<div class="input-group-addon">
												<a href="#"><i class="entypo-calendar"></i></a>
											</div>
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
			</form>


			<!-- Model Container-->
			<div class="modal fade container" id="copyRoutePlanModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="background-color: silver;">
					<div class="modal-content">
						<div class="modal-header" style="background-color: beige;">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Copy Route Plan</h4>
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
								<div class="input-group">
									<input type="text" class="form-control" id="txtNewFromDate"
										placeholder="Select From Date"
										style="background-color: #fff; z-index: 99999999999999;"
										readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>

							<div class="form-group">
								<div class="input-group">
									<input type="text" class="form-control" id="txtNewToDate"
										placeholder="Select To Date"
										style="background-color: #fff; z-index: 99999999999999;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>

							<div class="modal-body" style="overflow: auto; height: 500px">
								<table class='table table-striped table-bordered'
									id="tblCopyTaskList">
									<thead>
										<tr>
											<th><input type="checkbox" class="allcheckbox">
												All</th>
											<th>TaskList</th>
											<th>Schedule Order</th>
										</tr>
									</thead>
									<tbody id="detailWithOrder1">
										<c:forEach items="${taskLists }" var="taskList">
											<tr>
												<td><input name='rootPlanTaskList1'
													value='${ taskList.pid}' type='checkbox' /></td>
												<td>${taskList.name}</td>
												<td><input type='number' name="rootPlanOrder1"
													id='rootPlanOrder1${ taskList.pid}' /></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								<label class="error-msg" style="color: red;"></label>
							</div>
						</div>
						<div class="modal-footer">
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="copyTaskList" class="btn btn-primary">Save</button>
							</div>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.Model Container-->

			<!-- Model Container-->
			<div class="modal fade container" id="assignModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign TaskList</h4>
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

							<div class="modal-body" style="overflow: auto; height: 500px">
								<table class='table table-striped table-bordered'
									id="tblAssignTaskList">
									<thead>
										<tr>
											<th><input type="checkbox" class="allcheckbox">
												All</th>
											<th>TaskList</th>
											<th>Status</th>
											<th>Schedule Order</th>
										</tr>
									</thead>
									<tbody id="detailWithOrder">
										<c:forEach items="${taskLists }" var="taskList">
											<tr>
												<td><input name='rootPlanTaskList'
													value='${ taskList.pid}' type='checkbox' /></td>
												<td>${ taskList.name}</td>
												<td id="status${ taskList.pid}"></td>
												<td><input type='number' name="rootPlanOrder"
													id='rootPlanOrder${ taskList.pid}'
													onblur="RootPlanHeader.validateRootPlanOrder();" /></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								<label class="error-msg" style="color: red;"></label>
							</div>
						</div>
						<div class="modal-footer">
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="assignTaskList" class="btn btn-primary">Save</button>
							</div>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.Model Container-->

			<div class="modal fade container" id="assignUserModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Assign User</h4>
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

							<div class="modal-body" style="overflow: auto; height: 500px">
								<table class='table table-striped table-bordered'>
									<thead>
										<tr>
											<th><input type="checkbox" class="allcheckbox">
												All</th>
											<th>User</th>
										</tr>
									</thead>
									<tbody id="tbodyUser">
										<c:forEach items="${users }" var="user">
											<tr>
												<td><input name='assignUsers' value='${user.pid }'
													type='checkbox' /></td>
												<td>${user.firstName}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								<label class="error-msg" style="color: red;"></label>
							</div>
						</div>
						<div class="modal-footer">
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="assignUser" class="btn btn-primary">Save</button>
							</div>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.Model Container-->

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
								<h4 class="modal-title" id="viewModalLabel">RootPlanHeader</h4>
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
											<span>From Date</span>
										</dt>
										<dd>
											<span id="lbl_fromDate"></span>
										</dd>
										<hr />
										<dt>
											<span>To Date</span>
										</dt>
										<dd>
											<span id="lbl_toDate"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlRootPlanHeader}">
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
									<p>Are you sure you want to delete this Root Plan Header?</p>
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

	<spring:url value="/resources/app/root-plan-header.js"
		var="rootPlanHeaderJs"></spring:url>
	<script type="text/javascript" src="${rootPlanHeaderJs}"></script>
	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>
</body>
</html>