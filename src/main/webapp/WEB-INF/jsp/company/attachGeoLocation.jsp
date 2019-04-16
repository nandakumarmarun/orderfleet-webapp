<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Attach Geo Location</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
.error {
	color: red;
}
</style>
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";

	// call from dash board
	$(document).ready(function() {

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

		var userPid = getParameterByName('user-key-pid');
		if (userPid != null && userPid != "") {
			$('#dbUser').val(userPid);
			AttachGeoLocation.filter();
		}
	});

	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}
</script>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Attach Geo Location</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								Report From<select id="dbReportFrom" class="form-control">
									<option value="MOBILE">MOBILE</option>
									<option value="VISIT">VISIT</option>
								</select>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-2">
								Employee<select id="dbEmployee" name="employeePid" class="form-control">
									<option value="no">All Employees</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Account <select id="dbAccount" name="accountPid"
									class="form-control">
									<option value="no">All Account</option>
									<c:forEach items="${accounts}" var="account">
										<option value="${account.pid}">${account.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="AttachGeoLocation.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="SINGLE">Single Date</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
								<br />
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
								<br />
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
								<br />
								<button type="button" class="btn btn-info"
									onclick="AttachGeoLocation.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="collaptable table  table-striped table-bordered"
					id="tblAttachGeoLocation">
					<!--table header-->
					<thead>
						<tr>
							<th>Date</th>
							<th>Employee</th>
							<th>Account Profile</th>
							<th>Latitude</th>
							<th>Longitude</th>
							<th>Location</th>
							<th>Action</th>
						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyAttachGeoLocation">
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Model Container for Visit Geo location -->
			<div class="modal fade container" id="viewModalAccountProfile">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Update Location In Account Profile</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<div class="table-responsive" id="divAttachAccountProfile">
								<table class="collaptable table  table-striped table-bordered">
									<thead>
										<tr>
											<th></th>
											<th>Old</th>
											<th>New</th>
										</tr>
									</thead>
									<tbody id="tbldivAttachAccountProfile">

									</tbody>
								</table>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
							<button id="myFormSubmit" class="btn btn-primary">Attach</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			
			<!-- Model Container for Mobile Geo location -->
			<div class="modal fade container" id="viewModalMobile">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Enable Account Profile</h4>
						</div>
						<div class="modal-body" style="overflow: auto; height: 500px">
							<table class='table table-striped table-bordered'>
								<thead>
									<tr>
										<th>Old Latitude</th>
										<th>Old Longitude</th>
										<th>Old Geo Location</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td id="oldLatitude"></td>
										<td id="oldLongitude"></td>
										<td id="oldGeoLocation"></td>
									</tr>
								</tbody>
							</table>

							<div class="form-group">
								<div id="accountsCheckboxes">
									<table class='table table-striped table-bordered'>
										<thead>
											<tr>
												<th></th>
												<th>New Latitude</th>
												<th>New Longitude</th>
												<th>New Geo Location</th>
											</tr>
										</thead>
										<tbody id="tblAccountProfileGeoLocation">
										</tbody>
									</table>
								</div>
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button"
								id="btnSaveNewGeoLocation" value="Save" />
							<button class="btn" data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery.table2excel.min.js"
		var="table2excelMin"></spring:url>
	<script type="text/javascript" src="${table2excelMin}"></script>

	<spring:url value="/resources/app/attach-geo-location.js"
		var="attachGeoLocationJs"></spring:url>
	<script type="text/javascript" src="${attachGeoLocationJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
</body>
</html>