<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Release Info</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr/>
			<h1>Release Info</h1>
		<div id="divInfo">
		<p id="bodyInfo">
		
		</p>
		
		</div>
		<br>
	<jsp:include page="../fragments/m_footer.jsp"></jsp:include> 

	<%--  <spring:url value="/web/Release-info" var="urlReleaseInfo"></spring:url> --%>  


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/release-info.js"
		var="releaseInfoJs"></spring:url>
	<script type="text/javascript" src="${releaseInfoJs}"></script> 
</body>
</html>