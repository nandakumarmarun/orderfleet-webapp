<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Sales Summary Achievment</title>

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
			<h2>Sales Summary Achievment</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbEmployee" class="form-control"
									onchange="SalesSummaryAchievment.onLoadLocation();">
									<option value="-1">Select Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3">
								<select id="productGroup" name="productGroupPid"
									class="form-control"><option value="no">All
										ProductGroup</option>
									<c:forEach items="${productGroups}" var="productGroup">
										<option value="${productGroup.pid}">${productGroup.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-3">
								<select id="dbLocation" class="form-control">
									<option value="-1">Select Location</option>
									<%-- <c:forEach items="${locations}" var="location">
										<option value="${location.pid}">${location.firstName}</option>
									</c:forEach> --%>
								</select>
							</div>
							<div class="col-sm-3 ">
								<div class="input-group">
									<input type="text" class="form-control" id="txtMonth"
										placeholder="Select Month" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button id="applyBtn" type="button" class="btn btn-info">Apply</button>
							</div>
							<!-- <div class="col-sm-1">
								<button id="btnProductNoTarget" type="button" class="btn btn-info">Show Products With Out Target</button>
							</div> -->

						</div>
					</form>
				</div>
			</div>
			<hr />
			<div class="table-responsive">
				<table class="table table-bordered datatable">
					<thead>
						<tr>
							<th>Sales Target Group</th>
							<th>Amount / Volume</th>
						</tr>
					</thead>
					<tbody id="tblSalesSummaryAchievment">
						<tr>
							<td></td>
							<td></td>
						</tr>
					</tbody>
				</table>
				<div align="right">
					<input class="btn btn-success" type="button" id="save" value="Save"
						onclick="SalesSummaryAchievment.save()" />
				</div>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/assets/js/MonthPicker.js"
		var="monthPicker"></spring:url>
	<script type="text/javascript" src="${monthPicker}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>


	<spring:url value="/resources/app/sales-summary-achievment.js"
		var="salesSummaryAchievmentJS"></spring:url>
	<script type="text/javascript" src="${salesSummaryAchievmentJS}"></script>
</body>
</html>