<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Product Intake Report</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Product Intake Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<select id="dbEmployee" name="employeePid" class="form-control">
									<option value="no">Select Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<!-- <div class="col-sm-2">
								<input type="date" class="form-control" id="txtFromDate"
									placeholder="Select From Date" style="background-color: #fff;" />
							</div>
							<div class="col-sm-2">
								<input type="date" class="form-control" id="txtToDate"
									placeholder="Select To Date" style="background-color: #fff;" />
							</div> -->
							<div class="col-sm-2  custom_date1">
								<div class="input-group">
									<input type="text" class="form-control" id="txtFromDate"
										placeholder="Select From Date" style="background-color: #fff;"
										readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2  custom_date2">
								<div class="input-group">
									<input type="text" class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info"
									onclick="ProductInTake.filter()">Apply</button>
							</div>
							<div class="col-sm-5">
								<button type="button" class="btn btn-info" id="btnSearch"
									style="float: right;">Search</button>
								&nbsp;&nbsp;
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
								<input type="text" id="search" placeholder="Search..."
									class="form-control" style="width: 200px; float: right;">
							</div>
						</div>
					</form>
				</div>
			</div>
			<table class="table  table-striped table-bordered" id="tblProductInTakeRep">
				<thead id="tHeadProductInTake">

				</thead>
				<tbody id="tBodyProductInTake">
					<tr>
						<td align="center">No data available</td>
					</tr>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/app/product-intake-report.js"
		var="productIntakeJs"></spring:url>
	<script type="text/javascript" src="${productIntakeJs}"></script>
</body>
</html>