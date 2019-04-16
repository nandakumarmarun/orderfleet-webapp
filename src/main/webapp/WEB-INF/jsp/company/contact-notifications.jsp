<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Contact Notifications</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Contact Notifications</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			
			<div class="table-responsive">
				<table class="collaptable table table-striped table-bordered">
					<thead>
						<tr>
							<th>Message Data</th>
							<th>Created Date</th>
						</tr>
					</thead>
					<tbody id="tBodyNotificationMessage">
						
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	 <spring:url value="/resources/app/contact-notification.js"
		var="contactNotificationJs"></spring:url>
	<script type="text/javascript" src="${contactNotificationJs}"></script>  
</body>
</html>