<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Set Monthly Product Groups Target</title>

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
			<h2>Set Monthly Product Groups Target</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbLocation" name="locationPid" class="form-control">
									<option value="-1">Select Locations</option>
									<c:forEach items="${locations}" var="location">
										<option value="${location.pid}">${location.name}</option>
									</c:forEach>
								</select>
							</div>

							
							<div class="col-sm-3 ">
								<div class="input-group">
									<input type="text" class="form-control" id="txtMonth"
										
										placeholder="Select Month" style="background-color: #fff;"
										readonly="readonly" />
								</div>
							</div>
							
							<div class="col-sm-2">
								<button id="btnApplyMonthlyProductGroups" type="button" class="btn btn-info">Apply</button>
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
							<th>Product Group</th>
							<th>Amount (Monthly)</th>
							<th>Volume (Monthly)</th>
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
							<h4 class="modal-title" id="myModalLabel">Set Product Group Target</h4>
						</div>
						<div class="modal-body">
							<input type="hidden" id="hdnproductGroupPid" /> <input type="hidden"
								id="hdnLocationProductGroupTargetPid" />
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

	<spring:url value="/resources/app/set-monthly-location-product-group-target.js"
		var="setMonthlyProductGroupTargetJs"></spring:url>
	<script type="text/javascript" src="${setMonthlyProductGroupTargetJs}"></script>
	
</body>
</html>