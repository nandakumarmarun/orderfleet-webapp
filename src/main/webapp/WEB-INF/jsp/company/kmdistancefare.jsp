<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

		<html lang="en">

		<head>
			<jsp:include page="../fragments/m_head.jsp"></jsp:include>
			<title>SalesNrich | Kilometer Distance Fare</title>

			<!-- jQuery UI-->
			<spring:url value="/resources/assets/css/jquery-ui.css" var="jqueryUiCss"></spring:url>
			<link href="${jqueryUiCss}" rel="stylesheet">

			<spring:url value="/resources/assets/css/MonthPicker.css" var="monthPickerCss"></spring:url>
			<link href="${monthPickerCss}" rel="stylesheet" media="all" type="text/css">
		</head>

		<body class="page-body" data-url="">

			<div class="page-container">
				<!-- add class "sidebar-collapsed" to close sidebar by default, "chat-visible" to make chat appear always -->
				<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>

				<div class="main-content">
					<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
					<hr />
					<h2>Kilometer Distance Fare </h2>
					<div class="clearfix"></div>
					<hr />
					<div class="row">
						<!-- Profile Info and Notifications -->
						<div class="col-md-12 col-sm-12 clearfix">
							<form role="form" class="form-horizontal form-groups-bordered">
								<div class="form-group">
								</div>
								<div class="form-group">
									<div class="col-sm-3">
										<div class=" input-group">
											<div class='dropdown-menu dropdown-menu-left'
												style='background-color: #F0F0F0'>
												<div>
													<a class='btn btn-default dropdown-item'
														style='width: 100%; text-align: left;'
														onclick='GetAllEmployees(this,"all"," Select Employee")'>All
														Employee</a>
												</div>
											</div>
											<select id="dbEmployee" name="employeePid" class="form-control">
												<option value="no">Select Dashboard Employee</option>
											</select>
										</div>
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
									<div class="col-sm-1">
										<button id="applyBtn" type="button" class="btn btn-info">Apply</button>
									</div>

									<div class="col-sm-1">
                                   <button id="btnDownload" type="button" class="btn btn-success">Download Xls</button>
                                    							</div>
								</div>
							</form>
						</div>
					</div>
					<hr />
					<div>
						<table class="table table-striped table-bordered of-tbl-search" id="tblAccountProfile">
							<thead>
								<tr>
									<th>Employee Name</td>
									<th>Client Date</th>
									<th>StartingLocation</th>
									<th>EndLocation</td>
									<th>Kilometer</th>
									<th>Slab Name</th>
									<th>Slab Rate</th>
								</tr>
							</thead>
							<tbody id="tbldistanceBody">
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
            <spring:url value="/resources/assets/js/jquery.table2excel.min.js"
	     	var="table2excelMin"></spring:url>
         	<script type="text/javascript" src="${table2excelMin}"></script>
			<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
			<spring:url value="/resources/assets/js/MonthPicker.js" var="monthPicker"></spring:url>
			<script type="text/javascript" src="${monthPicker}"></script>
			<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
			<script type="text/javascript" src="${momentJs}"></script>
			<spring:url value="/resources/app/report-common-js-file.js" var="reportcommonjsfileJS"></spring:url>
			<script type="text/javascript" src="${reportcommonjsfileJS}"></script>
			<spring:url value="/resources/app/distance-fare-amount.js" var="salesTargetAchievedReportJs"></spring:url>
			<script type="text/javascript" src="${salesTargetAchievedReportJs}"></script>

		</body>

		</html>