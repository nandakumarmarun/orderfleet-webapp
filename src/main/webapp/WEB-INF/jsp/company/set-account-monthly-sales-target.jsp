<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Set Account Monthly Sales Target</title>

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
			<h2>Set Account Wise Monthly Sales Target</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbSalesTargetGroup" class="form-control">
									<option value="-1">Select Sales Target Group</option>
									<c:forEach items="${salesTargetGroups}" var="salesTargetGroup">
										<option value="${salesTargetGroup.pid}">${salesTargetGroup.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3 ">
								<div class="input-group">
									<input type="text" class="form-control"
										id="txtMonthAccountWise" onchange="return false;"
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
			<div class="row">
				<div class="container">
					<div class="btn-toolbar">
						<div class="btn-group btn-group-sm">
							<button name="A-B" class="btn btn-default alphabets">AB</button>
							<button name="C-D" class="btn btn-default alphabets">CD</button>
							<button name="E-F" class="btn btn-default alphabets">EF</button>
							<button name="G-H" class="btn btn-default alphabets">GH</button>
							<button name="I-J" class="btn btn-default alphabets">IJ</button>
							<button name="K-L" class="btn btn-default alphabets">KL</button>
							<button name="M-N" class="btn btn-default alphabets">MN</button>
							<button name="O-P" class="btn btn-default alphabets">OP</button>
							<button name="Q-R" class="btn btn-default alphabets">QR</button>
							<button name="S-T" class="btn btn-default alphabets">ST</button>
							<button name="U-V" class="btn btn-default alphabets">UV</button>
							<button name="W-X" class="btn btn-default alphabets">WX</button>
							<button name="Y-Z"class="btn btn-default alphabets">YZ</button>
							<button name="ALL" type="button" class="btn btn-success alphabets">All</button>
						</div>
					</div>
				</div>
			</div>
			<hr />
			<div class="row">
				<div class="col-sm-4">
					Filter by territory: <select id="sbLocation" class="form-control">
						<option value="-1">Select Territory</option>
						<c:forEach items="${locations}" var="location">
							<option value="${location.pid}">${location.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-md-8 col-sm-8 clearfix">
					<br />
					<button type="button" class="btn btn-info" id="btnSearch"
						style="float: right;">Search</button>
					<input type="text" id="search" placeholder="Search..."
						class="form-control" style="width: 200px; float: right;">
				</div>
			</div>
			<br>
			<div class="table-responsive">
				<table class="table table-bordered datatable">
					<thead>
						<tr>
							<th>Account</th>
							<th>Amount</th>
							<th>Volume</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody id="tblSetTarget">
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
							<h4 class="modal-title" id="myModalLabel">Set Sales Target</h4>
						</div>
						<div class="modal-body">
							<input type="hidden" id="hdnSalesPid" /> <input type="hidden"
								id="hdnUserSalesTargetPid" />
							<div class="form-group">
								<label>Amount</label> <input id="txtAmount" class="form-control" />
								<label>Volume</label> <input id="txtVolume" class="form-control" />
							</div>
							<label class="error-msg" style="color: red;"></label>
						</div>
						<div class="modal-footer">
							<input class="btn btn-success" type="button" id="saveTarget"
								value="Save" onclick="SetMonthlySalesTarget.saveTarget()" />
							<button class="btn btn-info" data-dismiss="modal">Cancel</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/MonthPicker.js"
		var="monthPicker"></spring:url>
	<script type="text/javascript" src="${monthPicker}"></script>

	<spring:url value="/resources/app/set-account-monthly-sales-target.js"
		var="setMonthlySalesTargetJs"></spring:url>
	<script type="text/javascript" src="${setMonthlySalesTargetJs}"></script>
</body>
</html>