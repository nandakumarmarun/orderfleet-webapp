<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Lead Management</title>
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

			<h2>Lead Management</h2>
			<hr />

			<div class="row">
				<div class="col-sm-4">
					<button type="button" class="btn btn-success"
						onclick="LeadManagement.showModalPopup($('#myModal'));"
						title="Create new">
						<i class="entypo-plus-circled"></i> Create
					</button>
				</div>
			</div>
			
			<div class="clearfix"></div>
			<hr/>
			<table class="table table-striped table-bordered of-tbl-search" id="tblAccountProfile">
				<thead>
					<tr>
						<th>Name</th>
						<th>Alias</th>
						<th>Address</th>
						<th>Phone</th>
						<th>Contact Person</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tBodyLeadManagement">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<spring:url value="/web/leadManagement" var="urlLeadManagement"></spring:url>

			<form id="leadManagementForm" role="form" method="post"
				action="${urlLeadManagement}">
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
								<h4 class="modal-title" id="myModalLabel">Create or edit Lead Management</h4>
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
									<label class="control-label" for="field_phone1">Phone</label>
									<input type="text" class="form-control" name="phone1"
										id="field_phone1" maxlength="20" placeholder="Phone" />
								</div>
								<div class="form-group">
									<label class="control-label" for="field_address">Address</label>
									<textarea class="form-control" name="address" maxlength="250"
										id="field_address" placeholder="Address"></textarea>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_territory">Territory</label> 
									<select id="field_territory" name="territory"
										class="form-control"><!-- <option value="-1">Select
											Account Type</option> -->
										<c:forEach items="${territories}" var="territory">
											<option value="${territory.pid}">${territory.name}</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<label class="control-label" for="field_contactPerson">Contact
										Person</label>
									<input type="text" class="form-control" name="contactPerson"
										maxlength="250" id="field_contactPerson"
										placeholder="Contact Person">
								</div>
								<div class="form-group">
									<label class="control-label" for="field_remarks">Remarks</label>
									<textarea class="form-control" name="remarks" maxlength="250"
										id="field_remarks" placeholder="Remarks"></textarea>
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
			
			<form name="location" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="locationModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Locations</h4>
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
									<table class="table  table-striped table-bordered">
										<thead>
											<tr>
												<th>Name</th>
											</tr>
										</thead>
										<tbody id="tbllocation">

										</tbody>
									</table>
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
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/lead-management.js"
		var="leadManagementJs"></spring:url>
	<script type="text/javascript" src="${leadManagementJs}"></script>
	
</body>
</html>