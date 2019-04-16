<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Set Monthly Sales Target</title>

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
								<select id="dbEmployee" class="form-control">
									<option value="-1">Select User</option>
									<c:forEach items="${users}" var="user">
										<option value="${user.pid}">${user.firstName}</option>
									</c:forEach>
								</select>
							</div>
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
										id="txtMonthAccountWise" placeholder="Select Month"
										style="background-color: #fff;" readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2">
								<button id="btnApply" type="button" class="btn btn-info">Apply</button>
							</div>

							<!-- <div class="input-group col-sm-2">
								<div class="col-sm-3">
									<button type="button" class="btn btn-info entypo-search"
										style="font-size: 18px"
										onclick="SetMonthlySalesTarget.loadAccountWiseMonthlySales()"
										title="Apply"></button>
								</div>
							</div> -->

						</div>
					</form>
				</div>
			</div>
			<hr />
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<button type="button" class="btn btn-info" id="btnSearch"
						style="float: right;">Search</button>
					<input type="text" id="search" placeholder="Search..."
						class="form-control" style="width: 200px; float: right;">
				</div>
			</div>
			<br>
			<div class="table-responsive">
				<ul class="pagination pagination-md">
					<li class="active"><a href="#">A</a></li>
					<li><a href="#">B</a></li>
					<li><a href="#">C</a></li>
					<li><a href="#">D</a></li>
					<li><a href="#">E</a></li>
					<li><a href="#">F</a></li>
					<li><a href="#">G</a></li>
					<li><a href="#">H</a></li>
					<li><a href="#">I</a></li>
					<li><a href="#">J</a></li>
					<li><a href="#">K</a></li>
					<li><a href="#">L</a></li>
					<li><a href="#">M</a></li>
					<li><a href="#">N</a></li>
					<li><a href="#">O</a></li>
					<li><a href="#">P</a></li>
					<li><a href="#">Q</a></li>
					<li><a href="#">R</a></li>
					<li><a href="#">S</a></li>
					<li><a href="#">T</a></li>
					<li><a href="#">U</a></li>
					<li><a href="#">V</a></li>
					<li><a href="#">W</a></li>
					<li><a href="#">X</a></li>
					<li><a href="#">Y</a></li>
					<li><a href="#">Z</a></li>
					<li><a href="#">NON-ALPHABETICAL</a></li>
				</ul>
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

	<spring:url value="/resources/app/set-monthly-sales-target.js"
		var="setMonthlySalesTargetJs"></spring:url>
	<script type="text/javascript" src="${setMonthlySalesTargetJs}"></script>
</body>
</html>