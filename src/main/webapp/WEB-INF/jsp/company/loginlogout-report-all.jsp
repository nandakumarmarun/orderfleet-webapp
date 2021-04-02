<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Login/Logout Report</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
.error {
	color: red;
}

.zoom {
	background-color: white;
}

.zoom:hover {
	-ms-transform: scale(2.0); /* IE 9 */
	-webkit-transform: scale(2.0); /* Safari 3-8 */
	transform: scale(2.0);
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Login/LogOut Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-1">
								<div class="form-check">
									<input type="checkbox" class="form-check-input"
										id="inclSubOrdinates" checked="checked"> <label
										class="form-check-label" for="inclSubOrdinates">Include
										Subordinate</label>
								</div>
							</div>
							<div class="col-sm-3">
								Employee
								<div class=" input-group">
									<span
										class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
										data-toggle='dropdown' aria-haspopup='true'
										aria-expanded='false' title='filter employee'></span>
									<div class='dropdown-menu dropdown-menu-left'
										style='background-color: #F0F0F0'>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
												Employee</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetAllEmployees(this,"no","All Employee")'>All
												Employee</a>
										</div>
									</div>
									<select id="dbEmployee" name="employeePid"
										class="form-control">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div>
							</div>
							<div class="col-sm-2">
								<select id="dbAttStatus" name="attendanceStatus"
									class="form-control">
									<option value="ALL">All</option>
									<option value="PRESENT">PRESENT</option>
									<option value="LEAVE">LEAVE</option>
									<option value="NOT_MARKED">NOT MARKED</option>
								</select>
							</div>
							<div class="col-sm-2">
								<select class="form-control" id="dbDateSearch"
									onchange="LoginLogOut.showDatePicker()">
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
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="LoginLogOut.filter()">Apply</button>
							</div>
							<div class="col-sm-1">
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<input type="text" id="search" placeholder="Search..."
						class="form-control" style="float: right; width: 200px;">
				</div>
			</div>
			<br>

			<table class="table  table-striped table-bordered"
				id="tblLoginLogOut">
				<thead>
					<tr>
						<th>Attendance Day</th>
						<th>Employee</th>
						<th>Attendance Status</th>
						<th>Attendance Client Date</th>
						<th>Attendance Server Date</th>
						<th>Attendance Remarks</th>
						<th>Punch Out Status</th>
						<th>Punch Out Client Date</th>
						<th>Punch Out Server Date</th>
						<th>Punch Out Remarks</th>
						
					</tr>
				</thead>
				<tbody id="tBodyLoginLogOut">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
		
		<!-- Model Container-->
		<div class="modal fade container " id="imagesModal">
			<!-- model Dialog -->
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="viewModalLabel">Attendance Images</h4>
					</div>
					<div class="modal-body zoom">
						<!-- error message -->
						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
						<div id="divAttendanceImages" style="overflow: auto; height: 500px;"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
		
		
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/loginlogoutreport.js" 
		var="LoginLogOutJs"></spring:url>
	<script type="text/javascript" src="${LoginLogOutJs}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

</body>
</html>