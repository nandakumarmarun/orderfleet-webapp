<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | User Growth Report</title>
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
			<h2>User Growth Report</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<select id="dbCompany" name="companyPid" class="form-control selectpicker" data-live-search="true">
									<option value="no">All Company</option>
									<c:forEach items="${companies}" var="company">
										<option value="${company.pid}">${company.legalName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2 ">
								<div class="input-group">
									<input type="text" class="form-control" id="txtFromMonth"
										placeholder="From Month" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2">
								<div class="input-group">
									<input type="text" class="form-control" id="txtToMonth"
										placeholder="To Month" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button id="applyBtn" type="button" class="btn btn-info">Apply</button>
							</div>
						</div>
					</form>
				</div>
				<!-- <div class="col-md-12 col-sm-12 clearfix ">
					<div class="col-sm-6"></div>
					<div class="col-sm-6" align="right">
						<br> <label>Total User Count: </label>&nbsp;<label
							id="lbl_totalUSR" style="font-size: large;">0</label>
					</div>
				</div> -->
			</div>
			<hr />

			<div style="overflow: auto;">
				<table style="display: none;"
					class="table table-striped table-bordered table-responsive"
					id="tblUserGrowthReport">
					<thead>
					</thead>
					<tbody>
					</tbody>
				</table>
				<div id="loadingData" style="display: none;">
					<h3 id="hLoadId"></h3>
				</div>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/MonthPicker.js"
		var="monthPicker"></spring:url>
	<script type="text/javascript" src="${monthPicker}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/user-growth-report.js"
		var="userGrowthReportJs"></spring:url>
	<script type="text/javascript" src="${userGrowthReportJs}"></script>

</body>
</html>