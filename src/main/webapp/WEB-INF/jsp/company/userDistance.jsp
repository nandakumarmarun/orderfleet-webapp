<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Distances</title>

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
			<h2>User Distances</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								User <br /> <select id="dbUser" name="userPid"
									class="form-control">
									<option value="no">All User</option>
									<c:forEach items="${users}" var="user">
										<option value="${user.pid}">${user.firstName}&nbsp;${user.lastName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="UserDistance.showDatePicker()">
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
										From Date<input type="date" class="form-control"
											id="txtFromDate" placeholder="Select From Date"
											style="background-color: #fff;" />
									</div>
								</div>
								<div class="col-sm-2">
									<div class="input-group">
										To Date<input type="date" class="form-control" id="txtToDate"
											placeholder="Select To Date" style="background-color: #fff;" />
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<br />
								<button type="button" class="btn btn-info"
									onclick="UserDistance.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>User Name</th>
						<th>Date</th>
						<th>KM</th>
						<th>Start Location</th>
						<th>End tLocation</th>
					</tr>
				</thead>
				<tbody id="tBodyUserDocument">
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/user-distances" var="urlUserDistance"></spring:url>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/user-distance.js" var="userDistance"></spring:url>
	<script type="text/javascript" src="${userDistance}"></script>

</body>
</html>