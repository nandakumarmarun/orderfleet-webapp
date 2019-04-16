<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Inventory Closing Report</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Inventory Closing Report</h2>

			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-md-12 clearfix">

					<input type="hidden" id="selectedEmployeePid"
						value='${employeePid}' />
					<div class="form-group">
						<div class="col-sm-6">
							<select id="dbEmployee" name="employeePid" class="form-control">
								<option value="no">All Employee</option>
								<c:forEach items="${employees}" var="employee">
									<option value="${employee.pid}">${employee.name}</option>
								</c:forEach>
							</select>
						</div>
						<div class="col-sm-3">
							<button id="btnFilter" type="submit"
								onclick="InventoryClosingReport.onLoadReport();"
								class="btn btn-info">Load</button>
						</div>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div id="divPreLoader" style="display: none;">
				<img src="/resources/assets/images/content-ajax-loader.gif"
					style="display: block; margin: auto;">
			</div>
			<div id="divReport">
				<table class="table  table-striped table-bordered">
					<thead>
						<tr id="trInventory"></tr>
					</thead>
					<tbody id="tbodyInventory"></tbody>
				</table>
				<sec:authorize access="hasAnyRole('ADMIN','MANAGER')">
					<div class="row">
						<div class="col-md-12" align="right">
							<button id="btnClose" type="submit" class="btn btn-success">Close</button>
						</div>
					</div>
				</sec:authorize>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/inventory-closing-report.js"
		var="inventoryClosingReportJs"></spring:url>
	<script type="text/javascript" src="${inventoryClosingReportJs}"></script>
</body>
</html>