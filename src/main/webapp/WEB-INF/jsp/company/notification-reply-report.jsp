<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Notification Reply</title>
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
			<h2>Notification Reply</h2>
			
			
			
			
						<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							
							<div class="col-sm-2">
								<select class="form-control" id="dbDateSearch"
									onchange="NotificationReply.showDatePicker()">
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
										<input type="date" class="form-control" id="txtFromDate"
											placeholder="Select From Date"
											style="background-color: #fff;" />
										<div class="input-group-addon">
											<a href="#"><i class="entypo-calendar"></i></a>
										</div>
									</div>
								</div>
								<div class="col-sm-2">
									<div class="input-group">
										<input type="date" class="form-control" id="txtToDate"
											placeholder="Select To Date" style="background-color: #fff;" />
										<div class="input-group-addon">
											<a href="#"><i class="entypo-calendar"></i></a>
										</div>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="NotificationReply.loadData()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			
			
			
			
			
			
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="table-responsive">
				<table class="collaptable table table-striped table-bordered">
					<thead>
						<tr>
							<th>Title</th>
							<th>Message Data</th>
							<th>Send Date</th>
						</tr>
					</thead>
					<tbody id="tBodyNotificationMessage">
						
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	 <spring:url value="/resources/app/notification-reply-report.js"
		var="notificationReplyJs"></spring:url>
	<script type="text/javascript" src="${notificationReplyJs}"></script>  
</body>
</html>