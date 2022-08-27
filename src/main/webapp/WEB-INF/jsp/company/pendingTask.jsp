<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | TaskPlan To CarryForward</title>
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";
</script>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Pending TaskList To ReScheudle</h2>
			<div class="clearfix"></div>
			<hr />
					<div class="row col-xs-12">
				<div class="pull-right"><label style="color:teal;font-weight:bold;font-size:15px;">Select Date To assign</label>
				
								<br />
								<div class="input-group">
									<input type="date" class="form-control" id="txtFromDate"
									style="background-color: #fff;">
								</div>
							</div>
				</div>
			
			<div class="clearfix"></div>
			<hr/>
			<br />
			<div class="table-responsive">
				<table class="collaptable table table-striped table-bordered"
					id="tblInvoiceWiseReport">
					<!--table header-->
					<thead>
						<tr>
							<th>Employee</th>
							<th>User</th>
							<th>Location</th>
							<th>No of UnAttended Task</th>
							
						</tr>
					</thead>
					<!--table header-->
					<tbody id="tBodyInvoiceWiseReport">
					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<!--collapse table-->
	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

	<spring:url
		value="/resources/app/pendingTask.js"
		var="pendingTaskJS"></spring:url>
	<script type="text/javascript" src="${pendingTaskJS}"></script>


</body>
</html>