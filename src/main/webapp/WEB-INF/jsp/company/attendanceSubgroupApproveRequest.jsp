<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Attendance Subgroup Approval Request</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Attendance Subgroup Approval Request</h2>

			<div class="clearfix"></div>
			<hr />
			<div class="col-md-12 col-sm-12 clearfix">
						<div class="col-sm-2">
								 <select class="form-control" id="dbDateSearch"
									onchange="AttendanceSubgroupApprovalRequest.showDatePicker()">
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
										 placeholder="Select From Date"
										style="background-color: #fff;" readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2 hide custom_date2">
								<div class="input-group">
									<input  type="text"
										class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="AttendanceSubgroupApprovalRequest.filter()">Apply</button>
							</div>
							</div>
							<div class="clearfix"></div>
			<hr />
			 <table class="table  table-striped table-bordered">
				<thead>
					<tr>
					<th>Name</th>
						<th>Request User</th>
						<th>Requested Date</th>
					<!-- 	<th>Requested User Message</th> -->
						<th>Approved/Rejected User</th>
						<th>Approved/Rejected Date</th>
						<th>Approved/Rejected User Message</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="tbodyAttendanceSubgroupApprovalRequest">
				
				</tbody>
			</table> 
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
	<div class="modal fade container" id="approveModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Approve Root Plan Subgroup Request
									</h4>
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
										<label class="control-label" for="field_message">Message</label>
										<textarea class="form-control" name="message"
											id="field_message" placeholder="Message"></textarea>
									</div>
								

								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="acceptAttendance" class="btn btn-primary">Accept</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				
				<div class="modal fade container" id="rejectModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Reject Root Plan Subgroup Request
									</h4>
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
										<label class="control-label" for="field_message">Message</label>
										<textarea class="form-control" name="message"
											id="message" placeholder="Message"></textarea>
									</div>
								

								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button id="rejectAttendance" class="btn btn-primary">Reject</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/attendance-subgroup-approve-request.js"
		var="attendanceSubgroupApprovalRequestJs"></spring:url>
	<script type="text/javascript" src="${attendanceSubgroupApprovalRequestJs}"></script>
	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>
</body>
</html>