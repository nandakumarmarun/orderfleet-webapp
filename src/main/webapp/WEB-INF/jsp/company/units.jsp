<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Units</title>
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
			<h2>Units</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="Units.showModalPopup($('#myModal'));"
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
						<th>Short Name</th>
						<th>Alias</th>
						<th>Unit Id</th>
						<th>Unit Code</th>
					</tr>
				</thead>
				<tbody>
					<c:if test = "${empty units}">
						<tr><td colspan = 5 style = "text-align: center;">No Data Available</td></tr>
					</c:if>
					<c:forEach items="${units}" var="unit" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${unit.name== null ? "" : unit.name}</td>
							<td>${unit.shortName== null ? "" : unit.shortName}</td>
							<td>${unit.alias== null ? "" : unit.alias}</td>
							<td>${unit.unitId== null ? "" : unit.unitId}</td>
							<td>${unit.unitCode== null ? "" : unit.unitCode}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="Units.showModalPopup($('#viewModal'),'${unit.pid}',0);" title="View">
										View
								</button>
								<button type="button" class="btn btn-blue"
									onclick="Units.showModalPopup($('#myModal'),'${unit.pid}',1);" title="Edit">
										Edit
								</button>
								<button type="button" class="btn btn-blue"
									onclick="Units.showModalPopup($('#deleteModal'),'${unit.pid}',2);" title="Delete">
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

			<spring:url value="/web/units" var="urlUnits"></spring:url>
			
			<form id="createEditForm" role="form" method="post"
				action="${urlUnits}">
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
									Units</h4>
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
										<label class="control-label" for="field_shortName">Short Name</label>
										<input type="text" class="form-control" name="shortName"
											id="field_shortName" placeholder="shortName" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_alias">Alias</label>
										<input type="text" class="form-control" name="alias"
											id="field_alias" placeholder="Alias" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_unitId">Unit id</label>
										<input type="text" class="form-control" name="unitId"
											id="field_unitId" placeholder="unitId" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_unitCode">Unit code</label>
										<input type="text" class="form-control" name="unitCode"
											id="field_unitCode" placeholder="unitCode" />
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
											<td><strong>Short Name</strong></td>
											<td><span id="lbl_registrationNumber"></span></td>
										</tr>
										<tr>
											<td><strong>Alias</strong></td>
											<td><span id="lbl_description"></span></td>
										</tr>
										<tr>
											<td><strong>Unit Id</strong></td>
											<td><span id="lbl_stockLocation"></span></td>
										</tr>
										<tr>
											<td><strong>Unit code</strong></td>
											<td><span id="lbl_unitCode"></span></td>
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

			<form id="deleteForm" name="deleteForm" action="${urlUnits}">
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
									<p>Are you sure you want to delete this Unit?</p>
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

	<spring:url value="/resources/app/units.js" var="unitsJs"></spring:url>
	<script type="text/javascript" src="${unitsJs}"></script>
	
</body>
</html>