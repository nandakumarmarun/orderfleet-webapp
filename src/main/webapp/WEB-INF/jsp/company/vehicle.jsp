<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Vehicle</title>
</head>
<style type="text/css">
.error {
	color: red;
}
</style>

<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Vehicle</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="Vehicle.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Registration Number</th>
						<th>Description</th>
						<th>Stock Location</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:if test = "${empty vehicles}">
						<tr><td colspan = 5 style = "text-align: center;">No Data Available</td></tr>
					</c:if>
					<c:forEach items="${vehicles}" var="vehicle" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${vehicle.name== null ? "" : vehicle.name}</td>
							<td>${vehicle.registrationNumber== null ? "" : vehicle.registrationNumber}</td>
							<td>${vehicle.description== null ? "" : vehicle.description}</td>
							<td>${vehicle.stockLocation.name== null ? "" : vehicle.stockLocation.name}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="Vehicle.showModalPopup($('#viewModal'),'${vehicle.pid}',0);" title="View">
										View
								</button>
								<button type="button" class="btn btn-blue"
									onclick="Vehicle.showModalPopup($('#myModal'),'${vehicle.pid}',1);" title="Edit">
										Edit
								</button>
								<button type="button" class="btn btn-blue"
									onclick="Vehicle.showModalPopup($('#deleteModal'),'${vehicle.pid}',2);" title="Delete">
										Delete
								</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/vehicle" var="urlVehicle"></spring:url>
			
			<form id="createEditForm" role="form" method="post"
				action="${urlVehicle}">
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
									Vehicle</h4>
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
								<div class="modal-body" style="overflow: auto; height: 60%;">
									<div class="form-group">
										<label class="control-label" for="field_name">Name
										</label> <input autofocus="autofocus" type="text"
											class="form-control" name="name" id="field_name"
											maxlength="255" placeholder="Name" />
									</div>

									<div class="form-group">
										<label class="control-label" for="field_registrationNumber">Registration Number
										</label>
										<input class="form-control" name="registrationNumber" maxlength="500"
											id="field_registrationNumber" placeholder="Registration Number"/>
									</div>
									<div class="form-group">
										<label class="control-label" for="field_description">Description</label>
										<input type="text" class="form-control" name="description"
											id="field_description" placeholder="Description" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_stockLocation">Stock Location</label>
										<select id="field_stockLocation" name="stockLocation" class="form-control">
											<option value="-1">Select Stock Location</option>
											<c:forEach items="${stockLocations}" var="stockLocation">
												<option value="${stockLocation.pid}">${stockLocation.name}</option>
											</c:forEach>
										</select>
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
								<h4 class="modal-title" id="viewModalLabel">Partner</h4>
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
											<td><strong>Name</strong></td>
											<td><span id="lbl_name"></span></td>
										</tr>
										<tr>
											<td><strong>Registration Number</strong></td>
											<td><span id="lbl_registrationNumber"></span></td>
										</tr>
										<tr>
											<td><strong>Description</strong></td>
											<td><span id="lbl_description"></span></td>
										</tr>
										<tr>
											<td><strong>Stock Location</strong></td>
											<td><span id="lbl_stockLocation"></span></td>
										</tr>
									</table>
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

			<form id="deleteForm" name="deleteForm" action="${urlVehicle}">
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
									<p>Are you sure you want to delete this Vehicle?</p>
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

	<spring:url value="/resources/app/vehicle.js" var="vehicleJs"></spring:url>
	<script type="text/javascript" src="${vehicleJs}"></script>
	
</body>
</html>