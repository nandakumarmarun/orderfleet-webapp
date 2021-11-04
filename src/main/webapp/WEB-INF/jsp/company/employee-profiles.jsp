<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | EmployeeProfile</title>
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
			<h2>Employee Profiles</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="EmployeeProfile.showModalPopup($('#myModal'));">Create
						new Employee Profile</button>
				</div>
			</div>
			<br> <br>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn"
						onclick="EmployeeProfile.showModalPopup($('#enableEmployeeProfileModal'));">Deactivated
						EmployeeProfile</button>
				</div>
				
			</div>
			<div class="input-group col-md-2 col-md-offset-10"
					style="padding-top: 5px;">
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered" id="tblEmployeeProfile">
				<thead>
					<tr>
						<th>Name</th>
						<th>Designation</th>
						<th>Department</th>
						<th>Phone</th>
						<th>Email</th>
						<th>Address</th>
						<th>OrgEmpId</th>
						
						<!-- <th>Profile Image</th> -->
						<th>Status</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${employee}" var="employeeProfile"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${employeeProfile.name}</td>
							<td>${employeeProfile.designationName}</td>
							<td>${employeeProfile.departmentName}</td>
							<td>${employeeProfile.phone == null ? "" : employeeProfile.phone}</td>
							<td>${employeeProfile.email == null ? "" : employeeProfile.email}</td>
							<td>${employeeProfile.address == null ? "" : employeeProfile.address}</td>
							<td>${employeeProfile.orgEmpId == null ? "" : employeeProfile.orgEmpId}</td>
							
							<%-- <td>${employeeProfile.encodedBase64Image}</td> --%>
							<td><span class="label ${employeeProfile.activated? 'label-success':'label-danger' }"
							onclick="EmployeeProfile.setActive('${employeeProfile.name}',
							'${ employeeProfile.address}',
							'${employeeProfile.designationPid}',
							'${employeeProfile.departmentPid}',
							'${employeeProfile.pid}',
							'${!employeeProfile.activated}')"
							style="cursor:pointer;">${employeeProfile.activated? "Activated" : "Deactivated"}</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="EmployeeProfile.showModalPopup($('#viewModal'),'${employeeProfile.pid}',0);">View</button>
								<button type="button" class="btn btn-blue"
									onclick="EmployeeProfile.showModalPopup($('#myModal'),'${employeeProfile.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="EmployeeProfile.showModalPopup($('#deleteModal'),'${employeeProfile.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/employee-profiles" var="urlEmployeeProfile"></spring:url>

			<form id="employeeProfileForm" role="form" method="post"
				action="${urlEmployeeProfile}">
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
									Employee Profile</h4>
							</div>
							<div class="modal-body" style="height: 500px; overflow: auto;">
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
									<label class="control-label" for="field_alias">Alias</label> <input
										type="text" class="form-control" name="alias" id="field_alias"
										maxlength="55" placeholder="Alias" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_address">Address</label>
									<textarea class="form-control" name="address" maxlength="500"
										id="field_address" placeholder="Address"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_org_emp_id">orgEmpId</label>
									<textarea class="form-control" name="orgEmpId" maxlength="500"
										id="field_org_emp_id" placeholder="orgEmpId"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_designation">Designation</label>
									<select id="field_designation" name="designationPid"
										class="form-control">
										<option value="-1">Select Employee Designation</option>
										<c:forEach items="${designations}" var="designation">
											<option value="${designation.pid}">${designation.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_department">Department</label>
									<select id="field_department" name="departmentPid"
										class="form-control"><option value="-1">Select
											Employee Department</option>
										<c:forEach items="${departments}" var="department">
											<option value="${department.pid}">${department.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_phone">Phone</label> <input
										type="text" class="form-control" name="phone" id="field_phone"
										maxlength="20" placeholder="Phone" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_email">Email</label> <input
										type="email" class="form-control" name="email"
										id="field_email" maxlength="100" placeholder="Email" />
								</div>

								<div class="form-group">
									<label class="control-label" for="field_profile_image">Image
									</label> <input id="profileImage" type="file"
										class="btn btn-default btn-block"> <img
										id="previewImage" src="" style="max-height: 100px;"
										alt="Image preview..."> <br>
								</div>
								<div class="modal-footer">
									<div class="modal-footer">
										<button type="button" class="btn btn-default"
											data-dismiss="modal">Cancel</button>
										<button id="myFormSubmit" class="btn btn-primary">Save</button>
									</div>
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
								<h4 class="modal-title" id="viewModalLabel">Employee
									Profile</h4>
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
									<table class="table table-striped table-bordered ">
										<tr>
											<td>Name</td>
											<td><span id="lbl_name"></span></td>
											<td rowspan="7" align="center"><img alt="Profile Image"
												id="profileImageView" src="" class="img-circle" width="80" /></td>
										</tr>
										<tr>
											<td>Alias</td>
											<td><span id="lbl_alias"></span></td>
										</tr>
										<tr>
											<td>Designation</td>
											<td><span id="lbl_designation"></span></td>
										</tr>
										<tr>
											<td>Department</td>
											<td><span id="lbl_department"></span></td>
										</tr>
										<tr>
											<td>Phone</td>
											<td><span id="lbl_phone"></span></td>
										</tr>
										<tr>
											<td>Email</td>
											<td><span id="lbl_email"></span></td>
										</tr>
										<tr>
											<td>Address</td>
											<td><span id="lbl_address"></span></td>
										</tr>
										<tr>
											<td>Address</td>
											<td><span id="lbl_org_emp_id"></span></td>
										</tr>
									</table>
									<!-- <table class="table  table-striped table-bordered">
										<tr>
											<td>Name</td>
											<td id="lbl_name"></td>
										</tr>
										<tr>
											<td>Designation</td>
											<td id="lbl_designation"></td>
										</tr>
										<tr>
											<td>Department</td>
											<td id="lbl_department"></td>
										</tr>
										<tr>
											<td>Phone</td>
											<td id="lbl_phone"></td>
										</tr>
										<tr>
											<td>Email</td>
											<td id="lbl_email"></td>
										</tr>
										<tr>
											<td>Address</td>
											<td id="lbl_address"></td>
										</tr>
									</table> -->
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
				action="${urlEmployeeProfile}">
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
									<p>Are you sure you want to delete this Employee Profile ?</p>
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
			
			<div class="modal fade container" id="enableEmployeeProfileModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Employee Profile</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th><input type="checkbox" class="allcheckbox">All</th>
												<th>Employee Profile</th>
											</tr>
										</thead>
										<tbody id="tblEnableEmployeeProfile">
											<c:forEach items="${deactivatedEmployeeProfiles}"
												var="employeeprofile">
												<tr>
													<td><input name='employeeprofile' type='checkbox'
														value="${employeeprofile.pid}" /></td>
													<td>${employeeprofile.name}</td>
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
								id="btnActivateEmployeeProfile" value="Activate" />
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

<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/app/employee-profile.js"
		var="employeeProfileJs"></spring:url>
	<script type="text/javascript" src="${employeeProfileJs}"></script>

	<script type="text/javascript">
		function openFile(type, data) {
			console.log(type);
			console.log(data);
			$window.open('data:' + type + ';base64,' + data, '_blank',
					'height=300,width=400');
		}
	</script>
</body>
</html>