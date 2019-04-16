<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Notification</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Notification</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-8 col-sm-8 clearfix">
					<div class="form-group row">
						<label for="msgTitle" class="col-sm-3 col-form-label">Message
							Title</label>
						<div class="col-sm-9">
							<input id="msgTitle" type="text" class="form-control" id="msgTitle" placeholder="Enter a title for the message" />
						</div>
					</div>
					<div class="form-group row">
						<label for="txtMsg" class="col-sm-3 col-form-label">Message</label>
						<div class="col-sm-9">
							<textarea id="txtMsg" placeholder="Enter the message" rows="6" cols="58"></textarea>
						</div>
					</div>
					<div class="form-group">
						<div align="center" id="divImp">
							<input type="checkbox" style="height: 20px; width: 20px"
								id="chkImportant"> <label
								style="font-size: 15px; font: bold;">Important</label>
						</div>
						<div align="center">
							<button type="button" class="btn" onclick="$('#txtMsg').val('')">Clear</button>
							<button type="button" class="btn btn-success"
								id="sendNotification">Send</button>
						</div>
					</div>
				</div>
			</div>
			<table class="table table-bordered datatable" id="dtExecutives">
				<thead>
					<tr>
						<th>Employee Name</th>
						<th>Phone</th>
						<th><input id="checkAll" type="checkbox" /></th>
				</thead>
				<tbody>
					<c:forEach items="${userDevices}" var="userDevice">
						<tr>
							<td>${userDevice.employeeName}</td>
							<td>${userDevice.userPhone}</td>
							<td>&nbsp;&nbsp;<input type="checkbox"
								value="${userDevice.pid}" name="check-one" class="check-one" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<!-- Model Container -->
	<div class="modal fade container" id="modalStatus">
		<!-- model Dialog -->
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">Notification Status</h4>
				</div>
				<div class="modal-body">
					<div id="divMsg"></div>
					<table class="table table-striped table-bordered " id="tblStatus">
						<tbody>
							<tr>
								<td>Total</td>
								<td id="tdTotal" style="font-weight: bold; color: black;"></td>
							</tr>
							<tr>
								<td>Success</td>
								<td id="tdSuccess" style="font-weight: bold; color: green;"></td>
							</tr>
							<tr>
								<td>Failed</td>
								<td id="tdFailed" style="font-weight: bold; color: red;"></td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button class="btn" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/notification.js" var="notificationJs"></spring:url>
	<script type="text/javascript" src="${notificationJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>
</body>
</html>