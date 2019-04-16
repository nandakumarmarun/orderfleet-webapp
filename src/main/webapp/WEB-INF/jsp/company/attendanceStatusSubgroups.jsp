<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Attendance Status Subgroups</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Attendance Status Subgroups</h2>
			<div class="row col-xs-12">
				<div class="pull-left">
				<form class="form-inline">
						<div class="form-group">
							<div class="input-group">
								<input type="text" id="searchAttendance" placeholder="Search..."
									class="form-control" style="width: 200px;"><span
									class="input-group-btn">
									<button type="button" class="btn btn-info"
										id="btnSearch" style="float: right;">Search</button>
								</span>
							</div>
						</div>
					</form>
									</div>
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="AttendanceStatusSubgroup.showModalPopup($('#myModal'));">Create new Subgroup</button>
				</div>
			</div>
			<br> <br>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>code</th>
						<th>Description</th>
						<!-- <th>Icon Url</th> -->
						<th>Sort Order</th>
						<th>Attendance Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyattendanceStatusSubgroupTable">
					<c:forEach items="${attendanceStatusSubgroups}" var="attendanceStatusSubgroup"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${attendanceStatusSubgroup.name}</td>
							<td>${attendanceStatusSubgroup.code == null ? "" : attendanceStatusSubgroup.code}</td>
							<td>${attendanceStatusSubgroup.description == null ? "" : attendanceStatusSubgroup.description}</td>
							<%-- <td>${attendanceStatusSubgroup.iconUrl == null ? "" : attendanceStatusSubgroup.iconUrl}</td> --%>
							<td>${attendanceStatusSubgroup.sortOrder}</td>
							<td>${attendanceStatusSubgroup.attendanceStatus}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="AttendanceStatusSubgroup.showModalPopup($('#viewModal'),'${attendanceStatusSubgroup.id}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="AttendanceStatusSubgroup.showModalPopup($('#myModal'),'${attendanceStatusSubgroup.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="AttendanceStatusSubgroup.showModalPopup($('#deleteModal'),'${attendanceStatusSubgroup.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/attendance-status-subgroups" var="urlAttendanceStatusSubgroup"></spring:url>

			<form id="attendanceStatusSubgroupForm" role="form" method="post" action="${urlAttendanceStatusSubgroup}">
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
									Attendance Status Subgroup</h4>
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
										<label class="control-label" for="field_attendanceStatus">Attendance Status</label> <select id="field_attendanceStatus" name="attendanceStatus"
										class="form-control"><option value="-1">Select
											Attendance Status</option>
											<option value="PRESENT">PRESENT</option>
												<option value="LEAVE">LEAVE</option>
									</select>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_name">Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="name" id="field_name" maxlength="255"
											placeholder="Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_code">Code</label> <input
											type="text" class="form-control" name="code"
											id="field_code" maxlength="3" placeholder="Code" />
									</div>
									
									<div class="form-group">
									<label class="control-label" for="field_sortOrder">Sort Order</label> <input
										type="number" min="0" class="form-control" name="sortOrder" id="field_sortOrder" value="0" />
									</div>
									
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<input type="text" class="form-control" name="description"
											id="field_description" maxlength="250" placeholder="Description" />
									</div>
								<!-- 	<div class="form-group">
										<label class="control-label" for="field_iconUrl">Icon Url</label>
										<input type="text" class="form-control" name="iconUrl"
											id="field_iconUrl" placeholder="Icon Url" />
									</div> -->
									
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
								<h4 class="modal-title" id="viewModalLabel">Attendance Status Subgroup</h4>
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
											<span>Code</span>
										</dt>
										<dd>
											<span id="lbl_code"></span>
										</dd>
										<hr />
										<dt>
											<span>Sort Order</span>
										</dt>
										<dd>
											<span id="lbl_sortOrder"></span>
										</dd>
										<hr />
										<dt>
											<span>Description</span>
										</dt>
										<dd>
											<span id="lbl_description"></span>
										</dd>
										<hr />
									<!-- 	<dt>
											<span>Icon Url</span>
										</dt>
										<dd>
											<span id="lbl_iconUrl"></span>
										</dd>
										<hr /> -->
										<dt>
											<span>Attendance Status</span>
										</dt>
										<dd>
											<span id="lbl_attendanceStatus"></span>
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

			<form id="deleteForm" name="deleteForm" action="${urlAttendanceStatusSubgroup}">
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
									<p>Are you sure you want to delete this Attendance Status Subgroup?</p>
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

	<spring:url value="/resources/app/attendance-status-subgroup.js" var="attendanceStatusSubgroupJs"></spring:url>
	<script type="text/javascript" src="${attendanceStatusSubgroupJs}"></script>
</body>
</html>