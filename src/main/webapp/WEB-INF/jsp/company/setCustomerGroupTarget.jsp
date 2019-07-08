<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Set Monthly Activity Target</title>

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/MonthPicker.css"
	var="monthPickerCss"></spring:url>
<link href="${monthPickerCss}" rel="stylesheet" media="all"
	type="text/css">
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>

		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Set Monthly Customer Group Target</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbUser"
									onchange="SetActivityTarget.loadUserActivities()"
									class="form-control">
									<option value="-1">Select User</option>
									<c:forEach items="${users}" var="user">
										<option value="${user.pid}">${user.firstName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3 ">
								<div class="input-group">
									<input type="text" class="form-control" id="txtMonth"
										onchange="SetActivityTarget.loadUserActivities()"
										placeholder="Select Month" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
			<hr />
			<div class="table-responsive">
				<table class="table table-bordered datatable">
					<thead>
						<tr>
							<th>Customer Group</th>
							<th>Target</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tblSetTarget">
						<tr>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<!-- Model Container -->
			<div class="modal fade container" id="modalSetTarget">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Set Activity
								Target</h4>
						</div>
						<div class="modal-body">
							<input type="hidden" id="hdnCustomerGroupPid" />
							<input type="hidden" id="hdnUserCustomerGroupTargetPid" />
							<div class="form-group">
								<label>Target</label> <input id="txtTarget" class="form-control" />
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="saveTarget"
								value="Save" onclick="SetCustomerGroupTarget.saveTarget()" />
							<button class="btn btn-info" data-dismiss="modal">Cancel</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/assets/js/MonthPicker.js"
		var="monthPicker"></spring:url>
	<script type="text/javascript" src="${monthPicker}"></script>

	<spring:url value="/resources/app/set-customer-group-target.js"
		var="setCustomerGroupTargetJs"></spring:url>
	<script type="text/javascript" src="${setCustomerGroupTargetJs}"></script>
</body>
</html>