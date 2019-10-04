<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Geo Location Variance</title>

<script type="text/javascript"
	src="http://maps.google.com/maps/api/js?sensor=false&v=3&libraries=geometry"></script>



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
			<hr />
			<h2>Geo Location Variance Report</h2>
			<br> <br>
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">

							<c:if test="${gpsVarianceQuery=='true'}">
								<div id="divGpsVarianceQuery">
									<div class="col-sm-3">
										<div class="radio">
											<label><input type="radio" name="optVariance"
												value="All" checked="checked">All</label>&nbsp;&nbsp;&nbsp;
											<label><input type="radio" name="optVariance"
												value="Less Than">Less Than</label>&nbsp;&nbsp;&nbsp; <label><input
												type="radio" name="optVariance" value="Greater Than">Greater
												Than</label>&nbsp;&nbsp;&nbsp;
										</div>
									</div>
									<div class="col-sm-2 divSetVariance hide" id="divSetVariance">
										Set Variance(in KM)<input type="number" class="form-control"
											id="txtVariance" style="background-color: #fff;" value='0' />
									</div>
									<br> <br> <br>
								</div>
							</c:if>


							<div class="col-sm-2">
								<br /> <select id="dbUser" name="userPid" class="form-control">
									<option value="no">Select Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.userPid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-4">
								<br /> <select id="dbAccountProfile" name="accountProfilePid"
									class="form-control">
									<option value="no">All Account Profiles</option>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="GeoLocationVariance.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							<div id="divDatePickers" style="display: none;">
								<div class="col-sm-2">
									<div class="input-group">
										From Date<input type="text" class="form-control"
											id="txtFromDate" placeholder="Select From Date"
											style="background-color: #fff;" />
									</div>
								</div>
								<div class="col-sm-2">
									<div class="input-group">
										To Date<input type="text" class="form-control" id="txtToDate"
											placeholder="Select To Date" style="background-color: #fff;" />
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<br />
								<button type="button" class="btn btn-info"
									onclick="GeoLocationVariance.filter()">Apply</button>
							</div>
							<div class="col-sm-2">
								<br />
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
						</div>
					</form>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<br>
			<div class="table-responsive">
				<table class="table  table-striped table-bordered"
					id="tblGeoLocationVariance">
					<thead>
						<tr>
							<th>Account</th>
							<th>Activity</th>
							<th>Date</th>
							<th>Actual Location</th>
							<th>Reported Location</th>
							<th>Variance</th>
							<!-- <th>Actions</th> -->
						</tr>
					</thead>
					<tbody id="tBodyGeoLocationVariance">
					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<div class="modal fade container" id="enableMapModal"
				style="width: 100%; z-index: 1041;">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 90%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<a href="#" data-rel="reload" id="fullscreen"
								data-toggle="tooltip" data-placement="top"
								data-original-title="Full Screen"><i class="entypo-window"></i></a>
						</div>


						<input type="text" name="from" id="field_fromLAT"
							placeholder="from lat" value="10.49975873961764" /> <input
							type="text" name="from" id="field_fromLNG" placeholder="from lng"
							value="76.20563507080078" /> <br /> <input type="text"
							name="to" id="field_toLAT" placeholder="to lat"
							value="10.02516684" /> <input type="text" name="to"
							id="field_toLNG" placeholder="to lng" value="76.4469488" />

						<button type="button" class="btn btn-info"
							onclick="GeoLocationVariance.km()">Apply</button>


						<div class="modal-body" style="overflow: auto; height: 650px;">
							<div class="panel-body no-padding" style="height: 600px;">
								<div id="map-canvas" style="height: 100%; width: 100%;"></div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>


	<spring:url value="/resources/app/geo-location-variance.js"
		var="geoLocationVarianceJs"></spring:url>
	<script type="text/javascript" src="${geoLocationVarianceJs}"></script>


</body>
</html>