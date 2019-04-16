<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | WhiteListedDevices</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>White Listed Devices</h2>
			<div class="row col-xs-12">
					<button type="button" class="btn btn-success" style="margin-left: 81%;"
						onclick="WhiteListedDevices.showModalPopup($('#myModal'));">Add
						New </button>
					<button type="button" class="btn"
						onclick="WhiteListedDevices.showModalPopup($('#searchModal'));">Search User Device</button>
			</div>
	
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered" >
				<thead>
					<tr>
						<th>Device Name</th>
						<th>Device Key</th>
						<th>Device Verification Not Required</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${whiteListedDevices}" var="whiteListedDevice"
						varStatus="loopStatus">
						<tr  >
							<td>${whiteListedDevice.deviceName}</td>
							<td>${whiteListedDevice.deviceKey}</td>
							<td><span class="label ${whiteListedDevice.deviceVerificationNotRequired? 'label-success':'label-danger' }"
							onclick="WhiteListedDevices.setActive('${whiteListedDevice.id}','${ !whiteListedDevice.deviceVerificationNotRequired}')"
							style="cursor:pointer;">${whiteListedDevice.deviceVerificationNotRequired? "True" : "False"}</span></td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="WhiteListedDevices.showModalPopup($('#myModal'),'${whiteListedDevice.id}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="WhiteListedDevices.showModalPopup($('#deleteModal'),'${whiteListedDevice.id}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/white-listed-devices" var="urlWhiteListedDevices"></spring:url>

			<form id="whiteListedDevicesForm" role="form" method="post"
				action="${urlWhiteListedDevices}">
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
									White Listed Devices</h4>
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
										<label class="control-label" for="field_deviceName">Device Name</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="deviceName" id="field_deviceName" 
											placeholder="Device Name" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_deviceKey">Device Key</label> <input
											type="text" class="form-control" name="deviceKey"
											id="field_deviceKey"  placeholder="Device Key" />
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

			<form id="deleteForm" name="deleteForm" action="${urlWhiteListedDevices}">
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
									<p>Are you sure you want to delete this White Listed Devices?</p>
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

			<div class="modal fade container" id="searchModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">User Device</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<div class="form-group">
								<div id="accountsCheckboxes">
								<div class="row">
										<div class="col-md-12 col-sm-12 clearfix">
											<button type="button" class="btn btn-info" id="btnSearch"
												style="float: right;">Search</button>
											<input type="text" id="search" placeholder="Search..."
												class="form-control" style="width: 200px; float: right;">
										</div>
									</div>
									<br>
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th>User</th>
												<th>Login</th>
												<th>Device Key</th>
											</tr>
										</thead>
										<tbody id="tblUserDevice">
											<c:forEach items="${userDevices}"
												var="userDevice">
												<tr>
													<td>${userDevice.user.firstName }</td>
													<td>${userDevice.user.login }</td>
													<td>${userDevice.deviceKey}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
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

	<spring:url value="/resources/app/white-listed-devices.js" var="whiteListedDevicesJs"></spring:url>
	<script type="text/javascript" src="${whiteListedDevicesJs}"></script>
</body>
</html>