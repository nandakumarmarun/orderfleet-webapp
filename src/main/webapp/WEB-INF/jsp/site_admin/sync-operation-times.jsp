<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>Sync-Operation-Time</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Sync Operation Time</h2>

			<div class="col-md-6 col-md-offset-3">
								<div class="form-group">
									<select id="field_company" name="companyPid"
										class="form-control selectpicker" data-live-search="true"><option value="-1">Select
											Company</option>
										<c:forEach items="${companies}" var="company">
											<option value="${company.pid}">${company.legalName}</option>
										</c:forEach>
									</select>
								</div>
							</div>

			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
					<!-- 	<th>Company Name</th> -->
						<th>Sync Operation Types</th>
						<th>Last Sync Started Date</th>
						<th>Last Sync Completed Date</th>
						<th>Last Sync Time (milli sec)</th>
						<th>Last Sync Time (min)</th>
						<th>Completed</th>
					</tr>
				</thead>
				<tbody id="maintable">
				
				</tbody>
			</table>
			
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>

	<spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
	<script type="text/javascript" src="${joinable}"></script>

	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/sync-operation-time.js" var="syncJs"></spring:url>
	<script type="text/javascript" src="${syncJs}"></script>
</body>
</html>