<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Unable To Find Locations</title>
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
			<h2>Unable Locations</h2>
			<div class="row col-xs-12">
				<div class="row">
					<div class="form-group">
						<div class="col-sm-3">
							<br> <select id="dbCompany" name="companyPid"
								class="form-control">
								<option value="no">All Companies</option>
								<c:forEach items="${companies}" var="company">
									<option value="${company.pid}">${company.legalName}</option>
								</c:forEach>
							</select>
						</div>
						<br>
						<div class="col-sm-2">
							<select class="form-control" id="dbDateSearch"
								onchange="UnableToFindLocations.showDatePicker()">
								<option value="TODAY">Today</option>
								<option value="YESTERDAY">Yesterday</option>
								<option value="WTD">WTD</option>
								<option value="MTD">MTD</option>
								<option value="CUSTOM">CUSTOM</option>
							</select>
						</div>
						<div class="col-sm-2 hide custom_date1">
							<div class="input-group">
								<input type="text" class="form-control" id="txtFromDate"
									placeholder="Select From Date" style="background-color: #fff;"
									readonly="readonly" />
								<div class="input-group-addon">
									<a href="#"><i class="entypo-calendar"></i></a>
								</div>
							</div>
						</div>
						<div class="col-sm-2 hide custom_date2">
							<div class="input-group">
								<input type="text" class="form-control" id="txtToDate"
									placeholder="Select To Date" style="background-color: #fff;"
									readonly="readonly" />
								<div class="input-group-addon">
									<a href="#"><i class="entypo-calendar"></i></a>
								</div>
							</div>
						</div>
						<div class="col-sm-3">
							<select id="dbLocationTypes" name="locationType"
								class="form-control">
								<option value="no">All</option>
								<c:forEach items="${locationTypes}" var="locationType">
									<option value="${locationType}">${locationType}</option>
								</c:forEach>
							</select>
						</div>
						<div class="col-sm-3">
							<button type="button" class="btn btn-orange" style="width: 65px;"
								onclick="UnableToFindLocations.getUnableToFindLocations()">Load</button>
						</div>
						<div class="col-md-12 col-sm-12 clearfix ">
							<div class="col-sm-6">
								<br><label>Total Unable Location Count : </label><label
									id="lbl_totalUnableLoc" style="font-size: large;">0</label>&nbsp;&nbsp;&nbsp;
								<label>Total No Location Count : </label><label
									id="lbl_totalNoLoc" style="font-size: large;">0</label>
							</div>
						</div>

					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table id="table" class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Company Name</th>
						<th>Count Of Unable Locations</th>
						<th>Count Of No Locations</th>
					</tr>
				</thead>
				<tbody id="tbodyUnableToFindLocations">
				</tbody>
			</table>
			<hr />
			<form name="viewForm" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="viewModal">
					<!-- model Dialog -->
					<div class="modal-dialog" style="width: 90%;">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="viewModalLabel">Unable find
									locations of a company</h4>
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
									<div class="row">
										<div class="form-group">
											<div class="col-sm-3">
												<br> <select id="dbLocationType" name="locationType"
													class="form-control">
													<option value="no">All</option>
													<c:forEach items="${locationTypes}" var="locationType">
														<option value="${locationType}">${locationType}</option>
													</c:forEach>
												</select>
											</div>
											<br>
											<div class="col-sm-3">
												<button id="btnLocationTypeButton" type="button"
													class="btn btn-info" style="width: 65px;">Apply</button>
											</div>
										</div>
									</div>
									<br>
									<table id="unableListTable"
										class="table  table-striped table-bordered">
										<thead>
											<tr>
												<th>Location Type</th>
												<th>Latitude</th>
												<th>Longitude</th>
												<th>Location</th>
											</tr>
										</thead>
										<tbody id="tbodyUnableListLocations">
										</tbody>
									</table>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-info"
										onclick="UnableToFindLocations.locationUpdate()">Update</button>
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
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/unable-to-find-locations.js"
		var="unableToFindLocationsJs"></spring:url>
	<script type="text/javascript" src="${unableToFindLocationsJs}"></script>
</body>
</html>